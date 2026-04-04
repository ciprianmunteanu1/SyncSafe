package org.example.project.model

/**
 * Reprezintă un grup de siguranță.
 *
 * @param id Identificator unic al grupului.
 * @param name Numele ales de creatorul grupului.
 * @param inviteCode Codul scurt de 6 caractere pentru aderare (ex: "A3K9PZ").
 * @param members Lista membrilor activi ai grupului.
 * @param meetingPoint Punctul de întâlnire stabilit, sau null dacă nu a fost setat.
 */
data class Group(
    val id: String,
    val name: String,
    val inviteCode: String,
    val members: List<Member> = emptyList(),
    val meetingPoint: MeetingPoint? = null
) {
    /**
     * Numărul membrilor cu statusul [MemberStatus.SAFE].
     */
    val safeCount: Int
        get() = members.count { it.status == MemberStatus.SAFE }

    /**
     * Numărul membrilor cu statusul [MemberStatus.NEEDS_HELP].
     */
    val needsHelpCount: Int
        get() = members.count { it.status == MemberStatus.NEEDS_HELP }

    /**
     * True dacă cel puțin un membru solicită ajutor.
     */
    val hasMemberInDanger: Boolean
        get() = needsHelpCount > 0
}
