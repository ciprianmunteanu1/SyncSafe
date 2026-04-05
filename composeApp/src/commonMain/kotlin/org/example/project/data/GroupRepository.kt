package org.example.project.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.example.project.model.Alert
import org.example.project.model.AlertType
import org.example.project.model.Group
import org.example.project.model.Member
import org.example.project.model.MeetingPoint
import org.example.project.model.MemberStatus
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Sursa unică de adevăr (Single Source of Truth) pentru datele aplicației.
 *
 * Sincronizează datele cu Firebase Realtime Database prin:
 * - **Scriere imediată**: orice schimbare (status, locație, meeting point)
 *   este trimisă instant la Firebase prin PUT/POST.
 * - **Polling la [POLL_INTERVAL_MS]ms**: citește periodic Firebase
 *   și actualizează [group] și [alerts] — astfel toate dispozitivele
 *   primesc actualizările celorlalți.
 *
 * Structura datelor în Firebase:
 * ```
 * /groups/{inviteCode}         → obiectul Group complet (JSON)
 * /alerts/{inviteCode}/{id}    → fiecare Alert individual
 * ```
 */
object GroupRepository {

    // ─── Constante ────────────────────────────────────────────────────────────

    private const val BASE_URL = FirebaseConfig.DATABASE_URL

    /** Intervalul de polling în milisecunde. 3 secunde = latență acceptabilă. */
    private const val POLL_INTERVAL_MS = 3_000L

    // ─── JSON & HTTP ──────────────────────────────────────────────────────────

    private val json = Json {
        ignoreUnknownKeys = true   // ignoră câmpuri necunoscute din Firebase
        isLenient = true           // acceptă JSON mai permisiv
        encodeDefaults = true      // scrie și valorile default (ex: lista goală)
        coerceInputValues = true   // coercion pentru enum-uri necunoscute → default
    }

    /**
     * Client Ktor. Engineul (Android/Darwin) este ales automat
     * din dependența platformei (ktor-client-android / ktor-client-darwin).
     */
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(json)
        }
    }

    // ─── Stare internă ────────────────────────────────────────────────────────

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var pollingJob: Job? = null

    /** ID-ul membrului curent pe dispozitivul acesta. */
    private var myMemberId: String? = null

    /** Codul de invitare al grupului curent, necesar pentru URL-urile Firebase. */
    private var currentInviteCode: String? = null

    // ─── Stare observabilă (publică) ─────────────────────────────────────────

    private val _group = MutableStateFlow<Group?>(null)

    /**
     * Grupul curent. Emite `null` dacă utilizatorul nu face parte din niciun grup.
     * Actualizat la fiecare ciclu de polling — toate dispozitivele primesc
     * schimbările celorlalți membri în max [POLL_INTERVAL_MS]ms.
     */
    val group: StateFlow<Group?> = _group.asStateFlow()

    private val _alerts = MutableStateFlow<List<Alert>>(emptyList())

    /**
     * Istoricul evenimentelor grupului, cel mai recent primul.
     * Actualizat la fiecare ciclu de polling.
     */
    val alerts: StateFlow<List<Alert>> = _alerts.asStateFlow()

    // ─── Stare derivată ────────────────────────────────────────────────────────

    /** Membrul curent (eu), sau `null` dacă nu suntem în grup. */
    val me: Member?
        get() = _group.value?.members?.find { it.id == myMemberId }

    val isInGroup: Boolean
        get() = _group.value != null

    // ─── Operații pe grup ────────────────────────────────────────────────────

    /**
     * Creează un grup nou pe Firebase și pornește polling-ul.
     */
    suspend fun createGroup(groupName: String): Result<Group> {
        return try {
            val user = AuthRepository.currentUser ?: return Result.failure(Exception("Not logged in"))
            val myName = user.username
            val memberId = user.memberId
            myMemberId = memberId

            val me = Member(
                id = memberId,
                name = myName,
                status = MemberStatus.UNKNOWN,
                lastUpdatedAt = now()
            )
            val group = Group(
                id = generateId(),
                name = groupName,
                inviteCode = generateInviteCode(),
                members = listOf(me)
            )

            // Salvare grup în Firebase
            putGroup(group)

            currentInviteCode = group.inviteCode
            _group.value = group
            _alerts.value = emptyList()

            // Eveniment în feed
            pushAlert(
                inviteCode = group.inviteCode,
                memberId = memberId,
                memberName = myName,
                type = AlertType.JOINED_GROUP,
                message = "$myName a creat grupul \"$groupName\"."
            )

            // Link-ăm utilizatorul de acest grup în contul centralizat
            AuthRepository.joinCircle(group.inviteCode, group.name)

            startPolling(group.inviteCode)
            Result.success(group)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Alătură utilizatorul la un grup existent folosind codul de invitare.
     */
    suspend fun joinGroup(code: String): Result<Group> {
        return try {
            val user = AuthRepository.currentUser ?: return Result.failure(Exception("Not logged in"))
            val myName = user.username
            val memberId = user.memberId
            myMemberId = memberId
            
            val upperCode = code.uppercase().trim()

            // 1. Caută grupul pe Firebase
            val response = client.get("$BASE_URL/groups/$upperCode.json")
            val body = response.bodyAsText()

            if (!response.status.isSuccess() || body == "null" || body.isBlank()) {
                return Result.failure(Exception("Cod de invitare invalid sau grupul nu există."))
            }

            val fetchedGroup = json.decodeFromString<Group>(body)
            currentInviteCode = fetchedGroup.inviteCode

            val alreadyme = fetchedGroup.members.find { it.id == memberId }
            val updatedGroup = if (alreadyme == null) {
                // Nu e în grup, îl adăugăm
                val me = Member(
                    id = memberId,
                    name = myName,
                    status = MemberStatus.UNKNOWN,
                    lastUpdatedAt = now()
                )
                val newGroup = fetchedGroup.copy(members = fetchedGroup.members + me)
                putGroup(newGroup)
                
                pushAlert(
                    inviteCode = fetchedGroup.inviteCode,
                    memberId = memberId,
                    memberName = myName,
                    type = AlertType.JOINED_GROUP,
                    message = "$myName s-a alăturat grupului."
                )
                newGroup
            } else {
                fetchedGroup // E deja în grup, tragem direct datele
            }

            _group.value = updatedGroup
            
            // Link-ăm utilizatorul de acest grup în contul centralizat
            AuthRepository.joinCircle(fetchedGroup.inviteCode, fetchedGroup.name)

            startPolling(fetchedGroup.inviteCode)
            Result.success(updatedGroup)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Stabilește conexiunea la un grup existent pentru care utilizatorul are deja o identitate creată
     */
    suspend fun connectToGroup(inviteCode: String): Result<Group> {
        return try {
            val user = AuthRepository.currentUser ?: return Result.failure(Exception("Not logged in"))
            currentInviteCode = inviteCode
            myMemberId = user.memberId
            
            // Tragem datele proaspete din Firebase
            refreshGroup(inviteCode)
            refreshAlerts(inviteCode)
            
            val group = _group.value
            if (group != null && group.members.any { it.id == user.memberId }) {
                // Dacă grupul încă există, continuăm cu polling
                startPolling(inviteCode)
                Result.success(group)
            } else {
                // Grupul probabil a fost șters, sau userul a fost kickat
                Result.failure(Exception("Grupul nu a putut fi coroborat cu Firebase."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun switchGroup(inviteCode: String): Result<Group> {
        stopPolling()
        AuthRepository.setActiveCircle(inviteCode)
        return connectToGroup(inviteCode)
    }

    // ─── Operații pe status ───────────────────────────────────────────────────

    /**
     * Actualizează statusul utilizatorului curent — local și pe Firebase.
     *
     * **Scenariul multi-device**:
     * - Device A apasă "Need Help" → status trimis la Firebase
     * - Device B primește noul status la următorul poll (max 3 sec)
     * - Device B apasă "On The Way" → Device A primește la următorul poll
     *
     * @param newStatus Noul status ales de utilizator.
     */
    suspend fun updateMyStatus(newStatus: MemberStatus) {
        val id = myMemberId ?: return
        val currentGroup = _group.value ?: return
        val name = me?.name ?: return

        val myIndex = currentGroup.members.indexOfFirst { it.id == id }
        if (myIndex == -1) return

        // 1. Actualizare optimistă locală (UI răspunde instant)
        val updatedMe = currentGroup.members[myIndex].copy(status = newStatus, lastUpdatedAt = now())
        val updatedMembers = currentGroup.members.toMutableList()
        updatedMembers[myIndex] = updatedMe
        val updatedGroup = currentGroup.copy(members = updatedMembers)
        _group.value = updatedGroup

        // 2. Persistare pe Firebase (ceilalți vor vedea la poll)
        try {
            putMember(currentGroup.inviteCode, myIndex, updatedMe)

            val alertType = when (newStatus) {
                MemberStatus.SAFE       -> AlertType.WENT_SAFE
                MemberStatus.NEEDS_HELP -> AlertType.NEEDS_HELP
                else                    -> AlertType.STATUS_CHANGED
            }

            pushAlert(
                inviteCode = currentGroup.inviteCode,
                memberId = id,
                memberName = name,
                type = alertType,
                message = "$name: ${newStatus.emoji} ${newStatus.label}"
            )
        } catch (e: Exception) {
            // Eroare de rețea — starea locală rămâne actualizată,
            // va fi sincronizată la următoarea scriere reușită.
        }
    }

    // ─── Operații pe locație ──────────────────────────────────────────────────

    /**
     * Actualizează coordonatele GPS ale utilizatorului curent.
     * Apelat de [LocationService] la fiecare schimbare semnificativă de locație.
     */
    suspend fun updateMyLocation(latitude: Double, longitude: Double) {
        val id = myMemberId ?: return
        val currentGroup = _group.value ?: return
        val name = me?.name ?: return

        val myIndex = currentGroup.members.indexOfFirst { it.id == id }
        if (myIndex == -1) return

        val updatedMe = currentGroup.members[myIndex].copy(latitude = latitude, longitude = longitude, lastUpdatedAt = now())
        val updatedMembers = currentGroup.members.toMutableList()
        updatedMembers[myIndex] = updatedMe
        val updatedGroup = currentGroup.copy(members = updatedMembers)
        _group.value = updatedGroup

        try {
            putMember(currentGroup.inviteCode, myIndex, updatedMe)
            pushAlert(
                inviteCode = currentGroup.inviteCode,
                memberId = id,
                memberName = name,
                type = AlertType.LOCATION_UPDATED,
                message = "$name și-a actualizat locația."
            )
        } catch (e: Exception) { /* continuăm */ }
    }

    // ─── Operații pe meeting point ────────────────────────────────────────────

    /**
     * Adaugă un punct de întâlnire nou.
     */
    suspend fun addMeetingPoint(meetingPoint: MeetingPoint) {
        val id = myMemberId ?: return
        val currentGroup = _group.value ?: return
        val name = me?.name ?: return

        val updatedPoints = currentGroup.meetingPoints + meetingPoint
        val updatedGroup = currentGroup.copy(meetingPoints = updatedPoints)
        _group.value = updatedGroup

        try {
            putGroup(updatedGroup)
            pushAlert(
                inviteCode = currentGroup.inviteCode,
                memberId = id,
                memberName = name,
                type = AlertType.MEETING_POINT_SET,
                message = "$name a adăugat punctul: \"${meetingPoint.name}\""
            )
        } catch (e: Exception) { /* continuăm */ }
    }

    /**
     * Șterge un punct de întâlnire salvat anterior.
     */
    suspend fun removeMeetingPoint(pointId: String) {
        val id = myMemberId ?: return
        val currentGroup = _group.value ?: return
        val name = me?.name ?: return

        val pointToRemove = currentGroup.meetingPoints.find { it.id == pointId } ?: return
        val updatedPoints = currentGroup.meetingPoints.filterNot { it.id == pointId }
        val updatedGroup = currentGroup.copy(meetingPoints = updatedPoints)
        _group.value = updatedGroup

        try {
            putGroup(updatedGroup)
            pushAlert(
                inviteCode = currentGroup.inviteCode,
                memberId = id,
                memberName = name,
                type = AlertType.STATUS_CHANGED, // Sau un AlertType separat daca il doresti
                message = "$name a șters reperul: \"${pointToRemove.name}\""
            )
        } catch (e: Exception) { /* continuăm */ }
    }

    // ─── Polling ─────────────────────────────────────────────────────────────

    /**
     * Pornește polling-ul periodic pentru sincronizare multi-device.
     * Anulează orice job de polling anterior înainte de a porni unul nou.
     */
    private fun startPolling(inviteCode: String) {
        pollingJob?.cancel()
        pollingJob = scope.launch {
            while (isActive) {
                try {
                    refreshGroup(inviteCode)
                    refreshAlerts(inviteCode)
                } catch (e: Exception) {
                    // Eroare de rețea tranzitorie — continuăm polling-ul
                }
                delay(POLL_INTERVAL_MS)
            }
        }
    }

    private fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }

    /** Citește grupul din Firebase și actualizează [_group]. */
    private suspend fun refreshGroup(inviteCode: String) {
        val response = client.get("$BASE_URL/groups/$inviteCode.json")
        val body = response.bodyAsText()
        if (response.status.isSuccess() && body != "null" && body.isNotBlank()) {
            val fetchedGroup = json.decodeFromString<Group>(body)
            // Actualizăm doar dacă datele s-au schimbat față de ce știm
            if (fetchedGroup != _group.value) {
                _group.value = fetchedGroup
            }
        }
    }

    /** Citește alertele din Firebase și actualizează [_alerts]. */
    private suspend fun refreshAlerts(inviteCode: String) {
        val response = client.get("$BASE_URL/alerts/$inviteCode.json")
        val body = response.bodyAsText()
        if (response.status.isSuccess() && body != "null" && body.isNotBlank()) {
            // Firebase returnează un Map<String, Alert> pentru colecția de alerte
            val alertMap = json.decodeFromString<Map<String, Alert>>(body)
            val sortedAlerts = alertMap.values.sortedByDescending { it.timestamp }
            if (sortedAlerts != _alerts.value) {
                _alerts.value = sortedAlerts
            }
        }
    }

    // ─── HTTP helpers ─────────────────────────────────────────────────────────

    /**
     * Scrie doar un anumit membru la `/groups/{inviteCode}/members/{index}.json`.
     * Previne suprascrierea datelor generate de un alt membru în același timp.
     */
    private suspend fun putMember(inviteCode: String, memberIndex: Int, member: Member) {
        client.put("$BASE_URL/groups/$inviteCode/members/$memberIndex.json") {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(Member.serializer(), member))
        }
    }

    /** Scrie descrierea completă a grupului (folosit la join, puncte întâlnire etc). */
    private suspend fun putGroup(group: Group) {
        client.put("$BASE_URL/groups/${group.inviteCode}.json") {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(Group.serializer(), group))
        }.bodyAsText()
    }

    /**
     * Adaugă o alertă la `/alerts/{inviteCode}/{id}.json` (PUT cu ID generat).
     * Firebase Realtime Database stochează fiecare alertă cu cheia sa proprie.
     */
    private suspend fun pushAlert(
        inviteCode: String,
        memberId: String,
        memberName: String,
        type: AlertType,
        message: String
    ) {
        val alertId = generateId()
        val alert = Alert(
            id = alertId,
            memberId = memberId,
            memberName = memberName,
            type = type,
            message = message,
            timestamp = now()
        )
        client.put("$BASE_URL/alerts/$inviteCode/$alertId.json") {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(Alert.serializer(), alert))
        }.bodyAsText()

        // Actualizare optimistă locală pentru UI instant
        _alerts.value = listOf(alert) + _alerts.value
    }

    // ─── Utilitare ────────────────────────────────────────────────────────────

    private fun generateId(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..12).map { chars[Random.nextInt(chars.length)] }.joinToString("")
    }

    private fun generateInviteCode(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..6).map { chars[Random.nextInt(chars.length)] }.joinToString("")
    }

    @OptIn(ExperimentalTime::class)
    private fun now(): Long = Clock.System.now().toEpochMilliseconds()
}
