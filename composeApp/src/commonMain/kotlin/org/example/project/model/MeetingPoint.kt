package org.example.project.model

/**
 * Punctul de întâlnire stabilit pentru un grup.
 *
 * @param latitude Latitudinea GPS a punctului.
 * @param longitude Longitudinea GPS a punctului.
 * @param name Denumire descriptivă (ex: "Parcul Central", "Intrarea B").
 */
data class MeetingPoint(
    val latitude: Double,
    val longitude: Double,
    val name: String
)
