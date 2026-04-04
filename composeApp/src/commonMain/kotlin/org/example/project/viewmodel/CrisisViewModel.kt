package org.example.project.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * Un item din checklist-ul de criză.
 *
 * @param id       Identificator unic.
 * @param text     Acțiunea de efectuat.
 * @param category Categoria din care face parte (Comunică / Evaluează / Acționează).
 * @param isChecked Stare bifată sau nu.
 */
data class ChecklistItem(
    val id: String,
    val text: String,
    val category: String,
    val isChecked: Boolean = false
)

/**
 * ViewModel pentru Crisis Mode și Crisis Checklist.
 *
 * Starea checklist-ului este locală (nu se sincronizează pe Firebase),
 * deoarece fiecare utilizator urmărește propriul progres.
 */
class CrisisViewModel : ViewModel() {

    // ─── Crisis Mode ─────────────────────────────────────────────────────────

    private val _isCrisisMode = MutableStateFlow(false)
    /**
     * True dacă Crisis Mode este activ.
     * Când e activ, UI-ul afișează CrisisScreen cu interfața simplificată.
     */
    val isCrisisMode: StateFlow<Boolean> = _isCrisisMode.asStateFlow()

    /** Activează sau dezactivează Crisis Mode. */
    fun toggleCrisisMode() {
        _isCrisisMode.value = !_isCrisisMode.value
    }

    fun activateCrisisMode() {
        _isCrisisMode.value = true
    }

    fun deactivateCrisisMode() {
        _isCrisisMode.value = false
    }

    // ─── Checklist ────────────────────────────────────────────────────────────

    private val _checklistItems = MutableStateFlow(defaultChecklistItems())
    /** Lista completă a itemilor din checklist, cu starea lor curentă. */
    val checklistItems: StateFlow<List<ChecklistItem>> = _checklistItems.asStateFlow()

    /** Numărul de itemi bifați. */
    val completedCount: Int
        get() = _checklistItems.value.count { it.isChecked }

    /** Numărul total de itemi din checklist. */
    val totalCount: Int
        get() = _checklistItems.value.size

    /**
     * Bifează sau debifează un item din checklist.
     *
     * @param itemId ID-ul itemului de modificat.
     */
    fun toggleItem(itemId: String) {
        _checklistItems.update { items ->
            items.map { if (it.id == itemId) it.copy(isChecked = !it.isChecked) else it }
        }
    }

    /** Resetează toate itemii la starea nebifată. */
    fun resetChecklist() {
        _checklistItems.value = defaultChecklistItems()
    }

    // ─── Date implicite ───────────────────────────────────────────────────────

    private fun defaultChecklistItems(): List<ChecklistItem> = listOf(
        // Comunică
        ChecklistItem("comm_1", "Anunță că ești în siguranță (buton I'm Safe)", "Comunică"),
        ChecklistItem("comm_2", "Verifică statusul tuturor membrilor grupului", "Comunică"),
        ChecklistItem("comm_3", "Confirmă meeting point-ul cu grupul", "Comunică"),

        // Evaluează
        ChecklistItem("eval_1", "Evaluează pericolele imediate din jur", "Evaluează"),
        ChecklistItem("eval_2", "Verifică dacă există persoane rănite", "Evaluează"),
        ChecklistItem("eval_3", "Identifică ieșirile de urgență", "Evaluează"),

        // Acționează
        ChecklistItem("act_1", "Activează partajarea locației", "Acționează"),
        ChecklistItem("act_2", "Mergi spre punctul de întâlnire al grupului", "Acționează"),
        ChecklistItem("act_3", "Sună 112 dacă situația este critică", "Acționează"),
        ChecklistItem("act_4", "Urmează instrucțiunile autorităților", "Acționează")
    )
}
