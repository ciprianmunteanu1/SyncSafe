package org.example.project.data

import org.example.project.i18n.AppLanguage
import org.example.project.i18n.stringsFor

data class GuideStep(
    val id: Int,
    val title: String,
    val description: String
)

data class EmergencyGuide(
    val id: String,
    val title: String,
    val icon: String,
    val steps: List<GuideStep>
)

object OfflineGuide {

    fun allGuides(language: AppLanguage): List<EmergencyGuide> {
        val s = stringsFor(language)
        return listOf(
            EmergencyGuide(
                id = "earthquake",
                title = s.guideEarthquake,
                icon = "🏚️",
                steps = listOf(
                    GuideStep(1, s.eqStep1Title, s.eqStep1Desc),
                    GuideStep(2, s.eqStep2Title, s.eqStep2Desc),
                    GuideStep(3, s.eqStep3Title, s.eqStep3Desc),
                    GuideStep(4, s.eqStep4Title, s.eqStep4Desc)
                )
            ),
            EmergencyGuide(
                id = "fire",
                title = s.guideFire,
                icon = "🔥",
                steps = listOf(
                    GuideStep(1, s.fireStep1Title, s.fireStep1Desc),
                    GuideStep(2, s.fireStep2Title, s.fireStep2Desc),
                    GuideStep(3, s.fireStep3Title, s.fireStep3Desc),
                    GuideStep(4, s.fireStep4Title, s.fireStep4Desc)
                )
            ),
            EmergencyGuide(
                id = "evacuation",
                title = s.guideEvacuation,
                icon = "🚪",
                steps = listOf(
                    GuideStep(1, s.evacStep1Title, s.evacStep1Desc),
                    GuideStep(2, s.evacStep2Title, s.evacStep2Desc),
                    GuideStep(3, s.evacStep3Title, s.evacStep3Desc),
                    GuideStep(4, s.evacStep4Title, s.evacStep4Desc)
                )
            ),
            EmergencyGuide(
                id = "first_aid",
                title = s.guideFirstAid,
                icon = "🩺",
                steps = listOf(
                    GuideStep(1, s.faStep1Title, s.faStep1Desc),
                    GuideStep(2, s.faStep2Title, s.faStep2Desc),
                    GuideStep(3, s.faStep3Title, s.faStep3Desc),
                    GuideStep(4, s.faStep4Title, s.faStep4Desc)
                )
            )
        )
    }

    // Keep backward-compatible property for any code that still uses it
    val allGuides: List<EmergencyGuide> get() = allGuides(AppLanguage.EN)
}
