package org.example.project.model

import kotlinx.serialization.Serializable

/**
 * Punctul de întâlnire stabilit pentru un grup.
 *
 * @param latitude Latitudinea GPS a punctului.
 * @param longitude Longitudinea GPS a punctului.
 * @param name Denumire descriptivă (ex: "Parcul Central", "Intrarea B").
 */
@Serializable
data class MeetingPoint(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val name: String
)
