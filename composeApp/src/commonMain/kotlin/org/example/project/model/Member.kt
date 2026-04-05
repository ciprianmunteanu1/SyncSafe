package org.example.project.model

import kotlinx.serialization.Serializable

/**
 * Reprezintă un membru al grupului de siguranță.
 *
 * @param id Identificator unic generat local (UUID sau timestamp).
 * @param name Numele afișat al membrului.
 * @param status Statusul curent al membrului. Default: [MemberStatus.UNKNOWN].
 * @param latitude Latitudinea GPS curentă. Null dacă locația nu a fost partajată.
 * @param longitude Longitudinea GPS curentă. Null dacă locația nu a fost partajată.
 * @param lastUpdatedAt Timestamp-ul ultimei actualizări (milisecunde Unix epoch).
 */
@Serializable
data class Member(
    val id: String,
    val name: String,
    val status: MemberStatus = MemberStatus.UNKNOWN,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val lastUpdatedAt: Long = 0L
) {
    /**
     * Returnează true dacă membrul a partajat locația sa.
     */
    val hasLocation: Boolean
        get() = latitude != null && longitude != null
}
