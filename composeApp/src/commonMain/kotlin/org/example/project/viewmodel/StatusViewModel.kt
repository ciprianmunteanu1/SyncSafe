package org.example.project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.example.project.data.GroupRepository
import org.example.project.model.Member
import org.example.project.model.MemberStatus

/**
 * ViewModel pentru ecranele de actualizare a statusului și trimitere alerte.
 *
 * Metodele [sendSafe] și [sendNeedHelp] sunt shortcuts rapide pentru cele
 * mai comune acțiuni — apelate direct din butoanele mari de pe HomeScreen.
 */
class StatusViewModel : ViewModel() {

    /** Grupul curent — pentru afișarea membrilor și statusurilor. */
    val group = GroupRepository.group

    /** Membrul curent (eu), derivat din grupul curent. */
    val me: StateFlow<Member?> = GroupRepository.group
        .map { it?.members?.find { m -> m.id == GroupRepository.me?.id } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    // ─── Acțiuni ─────────────────────────────────────────────────────────────

    /**
     * Actualizează statusul utilizatorului curent.
     * Scrierea pe Firebase este asincronă — UI-ul se actualizează instant
     * (optimistic update), iar ceilalți membri primesc în max 3 secunde.
     */
    fun updateStatus(newStatus: MemberStatus) {
        viewModelScope.launch {
            GroupRepository.updateMyStatus(newStatus)
        }
    }

    /**
     * Shortcut: Setează statusul pe SAFE și notifică grupul.
     * Apelat din butonul "I'm Safe" de pe Home/Crisis screen.
     */
    fun sendSafe() = updateStatus(MemberStatus.SAFE)

    /**
     * Shortcut: Setează statusul pe NEEDS_HELP și notifică grupul.
     * Apelat din butonul "Need Help" de pe Home/Crisis screen.
     * Ceilalți membri văd alerta în max 3 secunde.
     */
    fun sendNeedHelp() = updateStatus(MemberStatus.NEEDS_HELP)

    /**
     * Shortcut: Setează statusul pe ON_THE_WAY.
     * Apelat când membrul confirmă că se îndreaptă spre meeting point.
     */
    fun sendOnTheWay() = updateStatus(MemberStatus.ON_THE_WAY)
}
