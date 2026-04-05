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
        // Communicate
        ChecklistItem("comm_1", "Announce you are safe (I'm Safe button)", "Communicate"),
        ChecklistItem("comm_2", "Check status of all group members", "Communicate"),
        ChecklistItem("comm_3", "Confirm meeting point with group", "Communicate"),

        // Assess
        ChecklistItem("eval_1", "Assess immediate dangers around you", "Assess"),
        ChecklistItem("eval_2", "Check if anyone is injured", "Assess"),
        ChecklistItem("eval_3", "Identify emergency exits", "Assess"),

        // Act
        ChecklistItem("act_1", "Enable location sharing", "Act"),
        ChecklistItem("act_2", "Head to group meeting point", "Act"),
        ChecklistItem("act_3", "Call 112 if situation is critical", "Act"),
        ChecklistItem("act_4", "Follow authorities instructions", "Act")
    )
}
