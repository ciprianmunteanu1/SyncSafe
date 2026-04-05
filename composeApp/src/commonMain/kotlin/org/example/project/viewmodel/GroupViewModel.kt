package org.example.project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.data.GroupRepository
import org.example.project.model.Group

/**
 * ViewModel pentru ecranele de creare/aderare la grup și lista de membri.
 *
 * Expune [group] și [alerts] direct din [GroupRepository].
 * Gestionează starea de loading și erorile pentru UI.
 */
class GroupViewModel : ViewModel() {

    /** Grupul curent — null dacă utilizatorul nu e în niciun grup. */
    val group: StateFlow<Group?> = GroupRepository.group

    /** Lista alertelor din feed. */
    val alerts = GroupRepository.alerts

    private val _isLoading = MutableStateFlow(false)
    /** True în timp ce se execută o operație de rețea. */
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    /** Mesajul de eroare curent, sau null dacă nu există. */
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _navigateToHome = MutableStateFlow(false)
    /** True când grupul a fost creat/accesat — UI navighează la Home. */
    val navigateToHome: StateFlow<Boolean> = _navigateToHome.asStateFlow()

    // ─── Acțiuni ─────────────────────────────────────────────────────────────

    /**
     * Creează un grup nou pe Firebase.
     *
     * @param groupName Numele grupului.
     * @param myName    Numele utilizatorului curent.
     */
    fun createGroup(groupName: String) {
        if (groupName.isBlank()) {
            _error.value = "Completeaza toate câmpurile."
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            GroupRepository.createGroup(groupName.trim())
                .onSuccess { _navigateToHome.value = true }
                .onFailure { _error.value = "Eroare: ${it.message}" }
            _isLoading.value = false
        }
    }

    /**
     * Alătură utilizatorul la un grup existent folosind codul de invitare.
     *
     * @param code   Codul de 6 caractere (case-insensitive).
     * @param myName Numele utilizatorului curent.
     */
    fun joinGroup(code: String) {
        if (code.isBlank()) {
            _error.value = "Completează toate câmpurile."
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            GroupRepository.joinGroup(code.trim())
                .onSuccess { _navigateToHome.value = true }
                .onFailure { _error.value = "Cod invalid sau fără internet." }
            _isLoading.value = false
        }
    }

    /** Resetează flag-ul de navigare după ce UI-ul a navigat. */
    fun onNavigatedToHome() {
        _navigateToHome.value = false
    }

    /** Șterge eroarea curentă (ex: după ce utilizatorul a văzut-o). */
    fun clearError() {
        _error.value = null
    }
}
