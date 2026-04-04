package org.example.project.model

import kotlinx.serialization.Serializable

/**
 * Reprezintă identitatea unui utilizator în ecosistemul SyncSafe.
 * Un utilizator există independent de grup.
 * 
 * @param username Numele ales (va acționa și ca ID unic temporar/de login)
 * @param passwordHash Parola (plain text sau simplu hash pentru MVP)
 * @param memberId ID-ul permanent generat odată cu contul, cu care se regăsește printre membrii grupului
 * @param joinedGroups Mapare a grupurilor salvate de utilizator (InviteCode -> Nume Cerc)
 * @param lastActiveGroup Ultimul grup vizualizat/activ (dacă e cazul)
 */
@Serializable
data class UserAccount(
    val username: String,
    val passwordHash: String,
    val memberId: String,
    val joinedGroups: Map<String, String> = emptyMap(),
    val lastActiveGroup: String? = null
)
