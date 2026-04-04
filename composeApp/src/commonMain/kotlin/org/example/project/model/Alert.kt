package org.example.project.model

import kotlinx.serialization.Serializable

/**
 * Un eveniment din Group Activity Feed.
 *
 * Fiecare acțiune importantă (schimbare status, alertă, locație etc.)
 * generează un [Alert] care apare în feed-ul grupului.
 *
 * @param id Identificator unic al alertei.
 * @param memberId ID-ul membrului care a generat evenimentul.
 * @param memberName Numele membrului, stocat direct pentru afișare rapidă.
 * @param type Tipul evenimentului. Vezi [AlertType].
 * @param message Mesajul descriptiv afișat în feed (ex: "Ion și-a actualizat statusul: Safe").
 * @param timestamp Momentul evenimentului (milisecunde Unix epoch).
 */
@Serializable
data class Alert(
    val id: String,
    val memberId: String,
    val memberName: String,
    val type: AlertType,
    val message: String,
    val timestamp: Long
)
