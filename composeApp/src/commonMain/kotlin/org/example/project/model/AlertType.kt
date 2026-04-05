package org.example.project.model

import kotlinx.serialization.Serializable

/**
 * Tipul evenimentului înregistrat în Group Activity Feed.
 *
 * Folosit pentru a diferenția vizual evenimentele în feed
 * (culori, icoane, mesaje diferite per tip).
 */
@Serializable
enum class AlertType {
    /** Membrul și-a setat statusul pe SAFE. */
    WENT_SAFE,

    /** Membrul a apăsat "Need Help" și solicită ajutor. */
    NEEDS_HELP,

    /** Membrul și-a modificat statusul (orice altă schimbare). */
    STATUS_CHANGED,

    /** Membrul și-a actualizat locația GPS. */
    LOCATION_UPDATED,

    /** Un meeting point a fost setat sau modificat. */
    MEETING_POINT_SET,

    /** Un nou membru s-a alăturat grupului. */
    JOINED_GROUP
}
