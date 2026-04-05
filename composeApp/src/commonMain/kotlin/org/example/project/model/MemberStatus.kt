package org.example.project.model

import kotlinx.serialization.Serializable

/**
 * Statusul curent al unui membru din grup.
 *
 * @param label Textul afișat în interfață.
 * @param emoji Emoji reprezentativ, util în notificări și feed.
 */
@Serializable
enum class MemberStatus(
    val label: String,
    val emoji: String
) {
    SAFE(label = "Safe", emoji = "✅"),
    UNKNOWN(label = "Unknown", emoji = "❓"),
    NEEDS_HELP(label = "Needs Help", emoji = "🆘"),
    ON_THE_WAY(label = "On My Way", emoji = "🚶")
}
