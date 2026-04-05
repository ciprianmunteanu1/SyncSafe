package org.example.project.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.example.project.i18n.LocalAppLanguage
import org.example.project.i18n.stringsFor
import org.example.project.model.MemberStatus
import org.example.project.ui.theme.EmergencyRed
import org.example.project.ui.theme.OnTheWayBlue
import org.example.project.ui.theme.SafeGreen
import org.example.project.ui.theme.UnknownOrange

@Composable
fun StatusBadge(status: MemberStatus, modifier: Modifier = Modifier) {
    val s = stringsFor(LocalAppLanguage.current)
    val backgroundColor = when (status) {
        MemberStatus.SAFE -> SafeGreen
        MemberStatus.NEEDS_HELP -> EmergencyRed
        MemberStatus.UNKNOWN -> UnknownOrange
        MemberStatus.ON_THE_WAY -> OnTheWayBlue
    }
    val localizedLabel = when (status) {
        MemberStatus.SAFE -> s.statusSafe
        MemberStatus.UNKNOWN -> s.statusUnknown
        MemberStatus.NEEDS_HELP -> s.statusNeedsHelp
        MemberStatus.ON_THE_WAY -> s.statusOnMyWay
    }

    Box(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "${status.emoji} $localizedLabel",
            color = Color.White,
            style = MaterialTheme.typography.labelMedium
        )
    }
}
