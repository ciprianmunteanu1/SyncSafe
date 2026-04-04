package org.example.project.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable
import com.multiplatform.webview.jsbridge.IJsMessageHandler
import com.multiplatform.webview.jsbridge.JsMessage
import com.multiplatform.webview.jsbridge.rememberWebViewJsBridge
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.WebViewNavigator
import com.multiplatform.webview.web.rememberWebViewStateWithHTMLData
import com.multiplatform.webview.web.rememberWebViewNavigator
import org.example.project.model.MeetingPoint
import org.example.project.model.Member
import org.example.project.model.MemberStatus
import org.example.project.ui.theme.SafeGreen
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.PI
import org.example.project.data.GroupRepository

@Serializable
data class MapMarkerModel(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val status: String,
    val hasLocation: Boolean
)

fun calculateDistanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371e3
    val p1 = lat1 * PI / 180.0
    val p2 = lat2 * PI / 180.0
    val dp = (lat2 - lat1) * PI / 180.0
    val dl = (lon2 - lon1) * PI / 180.0

    val a = sin(dp / 2) * sin(dp / 2) + cos(p1) * cos(p2) * sin(dl / 2) * sin(dl / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1.0 - a))
    return r * c
}

val leafletHtmlTemplate = """
<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
    <style>
        body, html, #map { width: 100%; height: 100%; margin: 0; padding: 0; }
        .marker-icon { font-size: 24px; text-align: center; line-height: 24px; }
    </style>
</head>
<body>
    <div id="map"></div>
    <script>
        var map = L.map('map', { doubleClickZoom: false }).setView([44.4268, 26.1025], 13);
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            maxZoom: 19,
            attribution: '© OpenStreetMap'
        }).addTo(map);

        var markers = {};
        var meetMarkers = {};

        function getEmoji(status) {
            if(status === 'SAFE') return '🟢';
            if(status === 'ON_THE_WAY') return '🏃';
            if(status === 'NEEDS_HELP') return '🆘';
            return '⚪';
        }

        window.updateMap = function(membersJson, pointsJson) {
            try {
                var members = JSON.parse(membersJson);
                var bounds = [];
                
                for(var i=0; i<members.length; i++) {
                    var m = members[i];
                    if(!m.hasLocation) continue;
                    
                    var lat = m.latitude;
                    var lng = m.longitude;
                    var emoji = getEmoji(m.status);
                    
                    var icon = L.divIcon({
                        className: 'custom-div-icon',
                        html: "<div class='marker-icon'>" + emoji + "</div>",
                        iconSize: [30, 42],
                        iconAnchor: [15, 42]
                    });

                    if(markers[m.id]) {
                        markers[m.id].setLatLng([lat, lng]);
                        markers[m.id].setIcon(icon);
                    } else {
                        markers[m.id] = L.marker([lat, lng], {icon: icon}).addTo(map)
                            .bindPopup("<b>" + m.name + "</b>");
                    }
                    bounds.push([lat, lng]);
                }

                var points = JSON.parse(pointsJson);
                var newMeetMarkers = {};
                for(var j=0; j<points.length; j++) {
                    var p = points[j];
                    if(meetMarkers[p.id]) {
                        meetMarkers[p.id].setLatLng([p.latitude, p.longitude]);
                        meetMarkers[p.id].setPopupContent("<b>" + p.name + "</b>");
                        newMeetMarkers[p.id] = meetMarkers[p.id];
                        delete meetMarkers[p.id];
                    } else {
                        newMeetMarkers[p.id] = L.circleMarker([p.latitude, p.longitude], {
                            color: 'red', fillColor: '#f03', fillOpacity: 0.5, radius: 15
                        }).addTo(map).bindPopup("<b>" + p.name + "</b>");
                    }
                    bounds.push([p.latitude, p.longitude]);
                }
                
                // Remove old markers
                for(var oldId in meetMarkers) {
                    map.removeLayer(meetMarkers[oldId]);
                }
                meetMarkers = newMeetMarkers;

                // Fit bounds intelligently
                if(bounds.length > 0) {
                    if(!window.hasFittedBounds || window.lastBoundsLength !== bounds.length) {
                        map.fitBounds(bounds, {padding: [50, 50]});
                        window.hasFittedBounds = true;
                        window.lastBoundsLength = bounds.length;
                    }
                }
            } catch(e) {
                console.error(e);
            }
        };

        window.panTo = function(lat, lng) {
            map.flyTo([lat, lng], 16, { animate: true, duration: 1.5 });
        };

        var routeLayer = null;

        window.drawRoute = function(startLat, startLng, endLat, endLng) {
            if (routeLayer) {
                map.removeLayer(routeLayer);
                routeLayer = null;
            }
            
            var url = 'https://router.project-osrm.org/route/v1/driving/' + 
                      startLng + ',' + startLat + ';' + 
                      endLng + ',' + endLat + '?overview=full&geometries=geojson';
                      
            fetch(url)
                .then(function(response) { return response.json(); })
                .then(function(data) {
                    if (data.routes && data.routes.length > 0) {
                        var route = data.routes[0].geometry;
                        routeLayer = L.geoJSON(route, {
                            style: function (feature) {
                                return {color: '#4285F4', weight: 6, opacity: 0.8};
                            }
                        }).addTo(map);
                        
                        map.fitBounds(routeLayer.getBounds(), {padding: [50, 50]});
                    }
                })
                .catch(function(error) { console.error('Route error:', error); });
        };
        
        window.clearRoute = function() {
            if (routeLayer) {
                map.removeLayer(routeLayer);
                routeLayer = null;
            }
        };

        map.on('dblclick', function(e) {
            if(window.kmpJsBridge) {
                window.kmpJsBridge.callNative(
                    "setMeetingPoint",
                    e.latlng.lat + "," + e.latlng.lng,
                    function(data) {}
                );
            }
        });
    </script>
</body>
</html>
""".trimIndent()

@Composable
fun MapScreen(
    members: List<Member>,
    meetingPoints: List<MeetingPoint>,
    onSetMeetingPoint: (Double, Double, String) -> Unit,
    onDeleteMeetingPoint: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var draftLat by remember { mutableStateOf<Double?>(null) }
    var draftLng by remember { mutableStateOf<Double?>(null) }
    var selectedPointId by remember { mutableStateOf<String?>(null) }

    val state = rememberWebViewStateWithHTMLData(leafletHtmlTemplate)
    val navigator = rememberWebViewNavigator()
    val jsBridge = rememberWebViewJsBridge()
    
    // Înregistrăm Handler-ul pentru mesajele venite din JS
    LaunchedEffect(jsBridge) {
        jsBridge.register(object : IJsMessageHandler {
            override fun methodName(): String = "setMeetingPoint"
            override fun handle(
                message: JsMessage,
                navigator: WebViewNavigator?,
                callback: (String) -> Unit
            ) {
                val dataStr = message.params.replace("\"", "")
                val parts = dataStr.split(",")
                if (parts.size == 2) {
                    draftLat = parts[0].toDoubleOrNull()
                    draftLng = parts[1].toDoubleOrNull()
                    showDialog = true
                }
                callback("OK")
            }
        })
    }
    
    // Configurare WebView pentru a rula JS
    LaunchedEffect(state) {
        state.webSettings.apply {
            isJavaScriptEnabled = true
        }
    }

    // Injectăm actualizările constant (la fiecare recomposition declanșat de `members` sau `meetingPoints`)
    LaunchedEffect(members, meetingPoints) {
        val markersList = members.map {
            MapMarkerModel(
                id = it.id, name = it.name,
                latitude = it.latitude ?: 0.0, longitude = it.longitude ?: 0.0,
                status = it.status.name, hasLocation = it.hasLocation
            )
        }
        val jsonStr = Json.encodeToString(markersList).replace("'", "\\'")
        val pointsStr = Json.encodeToString(meetingPoints).replace("'", "\\'")
        
        navigator.evaluateJavaScript("window.updateMap('$jsonStr', '$pointsStr');")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Graficul hărții
        Box(modifier = Modifier.fillMaxWidth().weight(1.5f)) {
            WebView(
                state = state,
                navigator = navigator,
                webViewJsBridge = jsBridge,
                modifier = Modifier.fillMaxSize()
            )
            
            // Buton Suprapus peste hartă
            SmallFloatingActionButton(
                onClick = {
                    draftLat = null
                    draftLng = null
                    showDialog = true
                },
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text("📍 Point", modifier = Modifier.padding(horizontal = 12.dp))
            }
        }

        // Details Panel
        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth().padding(16.dp)) {
            if (meetingPoints.isNotEmpty()) {
                item {
                    Text(text = "Rendez-vous Points", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(meetingPoints) { pt ->
                    val isSelected = selectedPointId == pt.id
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clickable {
                                if (selectedPointId == pt.id) {
                                    selectedPointId = null
                                    navigator.evaluateJavaScript("window.clearRoute();")
                                } else {
                                    selectedPointId = pt.id
                                    val me = GroupRepository.me
                                    if (me != null && me.hasLocation) {
                                        val lat1 = me.latitude!!
                                        val lon1 = me.longitude!!
                                        navigator.evaluateJavaScript("window.drawRoute($lat1, $lon1, ${pt.latitude}, ${pt.longitude});")
                                    } else {
                                        navigator.evaluateJavaScript("window.panTo(${pt.latitude}, ${pt.longitude});")
                                    }
                                }
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("📍 ${pt.name}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                                TextButton(onClick = { onDeleteMeetingPoint(pt.id) }) {
                                    Text("Șterge", color = MaterialTheme.colorScheme.error)
                                }
                            }
                            
                            if (isSelected) {
                                val me = GroupRepository.me
                                if (me != null && me.hasLocation) {
                                    val lat1 = me.latitude!!
                                    val lon1 = me.longitude!!
                                    val distance = calculateDistanceMeters(lat1, lon1, pt.latitude, pt.longitude)
                                    val distStr = if (distance > 1000) {
                                        "${(distance / 100.0).toInt() / 10.0} km"
                                    } else {
                                        "${distance.toInt()} m"
                                    }
                                    Text(
                                        text = "🗺️ Distanță față de tine: $distStr",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                } else {
                                    Text(
                                        text = "Activează locația pentru a calcula distanța.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }

            item {
                Text(
                    text = "Members Radar",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(members, key = { it.id }) { member ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clickable(enabled = member.hasLocation) {
                            val lat = member.latitude ?: return@clickable
                            val lng = member.longitude ?: return@clickable
                            navigator.evaluateJavaScript("window.panTo($lat, $lng);")
                        },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = member.status.emoji)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = member.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = if (member.hasLocation) "Pe hartă" else "Fără GPS",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (member.hasLocation) SafeGreen else Color.Gray
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        SetMeetingPointDialog(
            initialLat = draftLat,
            initialLng = draftLng,
            onDismiss = { 
                showDialog = false 
                draftLat = null
                draftLng = null
            },
            onConfirm = { lat, lng, name ->
                onSetMeetingPoint(lat, lng, name)
                showDialog = false
                draftLat = null
                draftLng = null
            }
        )
    }
}

@Composable
fun SetMeetingPointDialog(
    initialLat: Double?,
    initialLng: Double?,
    onDismiss: () -> Unit,
    onConfirm: (Double, Double, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var latStr by remember { mutableStateOf(initialLat?.toString() ?: "") }
    var lngStr by remember { mutableStateOf(initialLng?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Set Meeting Point",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Location Name") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = latStr,
                    onValueChange = { latStr = it },
                    label = { Text("Latitude") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = lngStr,
                    onValueChange = { lngStr = it },
                    label = { Text("Longitude") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val lat = latStr.toDoubleOrNull() ?: 0.0
                    val lng = lngStr.toDoubleOrNull() ?: 0.0
                    onConfirm(lat, lng, name.ifBlank { "Meeting Point" })
                }
            ) {
                Text("Set")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
