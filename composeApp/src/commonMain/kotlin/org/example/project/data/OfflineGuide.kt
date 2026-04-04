package org.example.project.data

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
    val earthquakeGuide = EmergencyGuide(
        id = "earthquake",
        title = "Earthquake",
        icon = "🏚️",
        steps = listOf(
            GuideStep(1, "Take Cover", "Drop under a sturdy table or desk and hold on to its leg."),
            GuideStep(2, "Protect Yourself", "Cover your head and neck with your arms."),
            GuideStep(3, "Stay Away", "Keep away from windows, mirrors, bookcases, and tall furniture that could fall."),
            GuideStep(4, "Stay Inside", "Do not try to exit the building during the earthquake. Wait until the shaking stops.")
        )
    )

    val fireGuide = EmergencyGuide(
        id = "fire",
        title = "Fire",
        icon = "🔥",
        steps = listOf(
            GuideStep(1, "Raise Alarm", "Shout \"Fire!\" to alert people nearby and trigger the fire alarm."),
            GuideStep(2, "Evacuate Immediately", "Leave the building using the nearest stairs. Do not use the elevator!"),
            GuideStep(3, "Heavy Smoke", "If there is a lot of smoke, crawl low to the floor where the air is cleaner."),
            GuideStep(4, "Check Doors", "If a door is hot, do not open it. Find an alternative route.")
        )
    )

    val evacuationGuide = EmergencyGuide(
        id = "evacuation",
        title = "Evacuation",
        icon = "🚪",
        steps = listOf(
            GuideStep(1, "Stay Calm", "Do not panic. Follow the instructions of the designated personnel."),
            GuideStep(2, "Follow Signs", "Follow the green emergency exit signs out of the building."),
            GuideStep(3, "Leave Items", "Do not go back for personal belongings."),
            GuideStep(4, "Assembly Point", "Go to the designated Meeting Point and report your presence.")
        )
    )

    val firstAidGuide = EmergencyGuide(
        id = "first_aid",
        title = "First Aid",
        icon = "🩺",
        steps = listOf(
            GuideStep(1, "Assess Safety", "Ensure the area is safe before taking action."),
            GuideStep(2, "Check Condition", "Check if the victim is conscious and breathing."),
            GuideStep(3, "Call Emergency", "Call the 112 emergency number immediately if the situation is serious."),
            GuideStep(4, "Stop Bleeding", "Apply firm pressure with a clean cloth to any heavily bleeding wound.")
        )
    )

    val allGuides = listOf(earthquakeGuide, fireGuide, evacuationGuide, firstAidGuide)
}
