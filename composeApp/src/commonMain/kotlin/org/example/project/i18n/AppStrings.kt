package org.example.project.i18n

import androidx.compose.runtime.compositionLocalOf

enum class AppLanguage(val code: String, val displayName: String) {
    EN("en", "English"),
    RO("ro", "Română")
}

val LocalAppLanguage = compositionLocalOf { AppLanguage.EN }

data class AppStrings(
    // ─── Bottom Nav ─────────────────────────────────────────────────────────
    val navHome: String,
    val navMap: String,
    val navFeed: String,
    val navGuide: String,
    val navSettings: String,

    // ─── Welcome Screen ─────────────────────────────────────────────────────
    val welcomeTagline: String,
    val welcomeRestoringSession: String,
    val welcomeLogin: String,
    val welcomeCreateAccount: String,

    // ─── Login Screen ───────────────────────────────────────────────────────
    val loginTitle: String,
    val loginBack: String,
    val loginUsername: String,
    val loginPassword: String,
    val loginButton: String,
    val loginFailed: String,

    // ─── Create Account Screen ──────────────────────────────────────────────
    val signUpTitle: String,
    val createProfile: String,
    val username: String,
    val password: String,
    val createCircleTab: String,
    val joinCircleTab: String,
    val newCircleName: String,
    val inviteCodeLabel: String,
    val createAndEnter: String,
    val registrationFailed: String,

    // ─── Select Group Screen ────────────────────────────────────────────────
    val youreLoggedIn: String,
    val notPartOfCircle: String,
    val createACircle: String,
    val joinACircle: String,
    val circleName: String,
    val createButton: String,
    val joinButton: String,

    // ─── Home Screen ────────────────────────────────────────────────────────
    val enterCrisisMode: String,
    val addCircle: String,
    val inviteCodeTap: String, // "Invite code: %s (Tap to copy)"
    val imSafe: String,
    val onMyWay: String,
    val needHelp: String,
    val noActiveCircle: String,
    val createOrJoinCircle: String,

    // ─── Crisis Screen ──────────────────────────────────────────────────────
    val crisisMode: String,
    val areYouSafe: String,
    val emergency: String,
    val checklist: String,
    val bunkers: String,
    val hospitals: String,
    val call112: String,
    val backToHome: String,
    val confirmEmergency: String,
    val confirmEmergencyBody: String,
    val sendEmergency: String,
    val cancel: String,
    val understood: String,

    // ─── Crisis types ───────────────────────────────────────────────────────
    val fire: String,
    val earthquake: String,
    val militaryRisk: String,
    val flood: String,

    // ─── Crisis checklist (general rules) ────────────────────────────────────
    val ruleStayCalm: String,
    val ruleAssessSituation: String,
    val ruleCall112: String,
    val ruleAlertGroup: String,

    // ─── Crisis checklist (fire) ─────────────────────────────────────────────
    val fireRule1: String,
    val fireRule2: String,
    val fireRule3: String,

    // ─── Crisis checklist (earthquake) ───────────────────────────────────────
    val quakeRule1: String,
    val quakeRule2: String,
    val quakeRule3: String,
    val quakeRule4: String,

    // ─── Crisis checklist (military) ─────────────────────────────────────────
    val milRule1: String,
    val milRule2: String,
    val milRule3: String,

    // ─── Crisis checklist (flood) ────────────────────────────────────────────
    val floodRule1: String,
    val floodRule2: String,
    val floodRule3: String,

    // ─── CrisisViewModel checklist ──────────────────────────────────────────
    val categoryComm: String,
    val categoryAssess: String,
    val categoryAct: String,
    val commAnnounce: String,
    val commCheckStatus: String,
    val commConfirmMeetingPoint: String,
    val assessDangers: String,
    val assessInjured: String,
    val assessExits: String,
    val actLocation: String,
    val actHeadMeeting: String,
    val actCall112: String,
    val actFollowAuthorities: String,

    // ─── Map Screen ─────────────────────────────────────────────────────────
    val rendezvousPoints: String,
    val deleteButton: String,
    val distanceFromYou: String, // "Distance from you: %s"
    val enableLocationDistance: String,
    val membersRadar: String,
    val onMap: String,
    val noGps: String,
    val setMeetingPointTitle: String,
    val locationName: String,
    val latitude: String,
    val longitude: String,
    val setButton: String,
    val point: String,
    val meetingPoint: String,

    // ─── Activity Feed ──────────────────────────────────────────────────────
    val liveActivityFeed: String,
    val noRecentActivity: String,
    val majorEmergency: String,
    val justNow: String,
    val oneMinAgo: String,
    val minutesAgo: String, // "%d minutes ago"
    val oneHourAgo: String,
    val hoursAgo: String, // "%d hours ago"
    val oneDayAgo: String,
    val daysAgo: String, // "%d days ago"

    // ─── Offline Guide ──────────────────────────────────────────────────────
    val offlineGuideTitle: String,
    // Earthquake guide
    val guideEarthquake: String,
    val eqStep1Title: String, val eqStep1Desc: String,
    val eqStep2Title: String, val eqStep2Desc: String,
    val eqStep3Title: String, val eqStep3Desc: String,
    val eqStep4Title: String, val eqStep4Desc: String,
    // Fire guide
    val guideFire: String,
    val fireStep1Title: String, val fireStep1Desc: String,
    val fireStep2Title: String, val fireStep2Desc: String,
    val fireStep3Title: String, val fireStep3Desc: String,
    val fireStep4Title: String, val fireStep4Desc: String,
    // Evacuation guide
    val guideEvacuation: String,
    val evacStep1Title: String, val evacStep1Desc: String,
    val evacStep2Title: String, val evacStep2Desc: String,
    val evacStep3Title: String, val evacStep3Desc: String,
    val evacStep4Title: String, val evacStep4Desc: String,
    // First Aid guide
    val guideFirstAid: String,
    val faStep1Title: String, val faStep1Desc: String,
    val faStep2Title: String, val faStep2Desc: String,
    val faStep3Title: String, val faStep3Desc: String,
    val faStep4Title: String, val faStep4Desc: String,

    // ─── Checklist Screen ───────────────────────────────────────────────────
    val emergencyChecklist: String,
    val completedOf: String, // "%d of %d completed"

    // ─── Status Screen ──────────────────────────────────────────────────────
    val updateYourStatus: String,
    val tapStatusNotify: String,
    val leaveGroupLogout: String,

    // ─── Settings Screen ────────────────────────────────────────────────────
    val settingsTitle: String,
    val yourProfile: String,
    val guest: String,
    val memberId: String, // "Member ID: %s"
    val circlesJoined: String, // "%d Circle(s) joined"
    val appearance: String,
    val darkMode: String,
    val on: String,
    val off: String,
    val languageLabel: String,
    val logOut: String,

    // ─── SOS Alert Popup (Navigation) ───────────────────────────────────────
    val sosEmergencyTitle: String,
    val sosHasEmergency: String, // "%s has an emergency!"
    val sosEmergencyType: String, // "Emergency type: %s"
    val sosViewOnMap: String,
    val sosUnderstood: String,

    // ─── GroupRepository messages ────────────────────────────────────────────
    val msgReportsEmergency: String, // "%s reports EMERGENCY: %s!"
    val msgIsSafe: String, // "%s is safe! ✅"
    val msgUpdatedLocation: String, // "%s updated her location."

    // ─── Member statuses ────────────────────────────────────────────────────
    val statusSafe: String,
    val statusUnknown: String,
    val statusNeedsHelp: String,
    val statusOnMyWay: String
)

// ═══════════════════════════════════════════════════════════════════════════════
// ENGLISH STRINGS
// ═══════════════════════════════════════════════════════════════════════════════

val EnStrings = AppStrings(
    // Bottom Nav
    navHome = "Home", navMap = "Map", navFeed = "Feed", navGuide = "Guide", navSettings = "Settings",

    // Welcome
    welcomeTagline = "Smart connection. Real-time safety.",
    welcomeRestoringSession = "Restoring session...",
    welcomeLogin = "Log In",
    welcomeCreateAccount = "Create Account",

    // Login
    loginTitle = "Log In", loginBack = "Back", loginUsername = "Username", loginPassword = "Password",
    loginButton = "Enter Circle", loginFailed = "Login failed.",

    // Create Account
    signUpTitle = "Sign Up", createProfile = "Create Profile",
    username = "Username", password = "Password",
    createCircleTab = "Create Circle", joinCircleTab = "Join Circle",
    newCircleName = "New Circle Name", inviteCodeLabel = "Invite Code",
    createAndEnter = "Create & Enter", registrationFailed = "Registration failed.",

    // Select Group
    youreLoggedIn = "You're Logged In!", notPartOfCircle = "You are not part of any Circle.",
    createACircle = "Create a Circle", joinACircle = "Join a Circle",
    circleName = "Circle Name", createButton = "Create", joinButton = "Join",

    // Home
    enterCrisisMode = "🚨 ENTER CRISIS MODE 🚨", addCircle = "Add Circle",
    inviteCodeTap = "Invite code: %s (Tap to copy)",
    imSafe = "I'M SAFE", onMyWay = "ON MY WAY", needHelp = "NEED HELP",
    noActiveCircle = "You do not have an active Circle.",
    createOrJoinCircle = "Create or Join a Circle",

    // Crisis
    crisisMode = "CRISIS MODE", areYouSafe = "Are you safe?",
    emergency = "EMERGENCY", checklist = "📋 Checklist",
    bunkers = "🛡️ Bunkers", hospitals = "🏥 Hospitals",
    call112 = "📞 Call 112", backToHome = "← Back to Home",
    confirmEmergency = "🚨 Confirm Emergency",
    confirmEmergencyBody = "Are you sure this is a real emergency? Select type:",
    sendEmergency = "🆘 Send Emergency", cancel = "Cancel", understood = "Understood",

    // Crisis types
    fire = "Fire", earthquake = "Earthquake", militaryRisk = "Military Risk", flood = "Flood",

    // General rules
    ruleStayCalm = "Stay calm: Keep your mind clear and analyze the danger.",
    ruleAssessSituation = "Assess situation: Are you in a safe place? If not, move urgently.",
    ruleCall112 = "Call 112: Notify authorities if there are victims.",
    ruleAlertGroup = "Alert group: Wait for location confirmations from others.",

    // Fire rules
    fireRule1 = "Use stairs, avoid elevators completely",
    fireRule2 = "Stay as close to the floor as possible",
    fireRule3 = "Cover nose/mouth with a damp cloth",

    // Earthquake rules
    quakeRule1 = "Take cover under a sturdy desk/table",
    quakeRule2 = "Stay away from windows or tall furniture",
    quakeRule3 = "Wait for the shaking to stop before exiting",
    quakeRule4 = "Do not use stairs during the earthquake",

    // Military rules
    milRule1 = "Evacuate the area in an organized manner if exit is safe",
    milRule2 = "Do not trigger large electronic equipment nearby",
    milRule3 = "Seek the nearest civil shelter / bunker",

    // Flood rules
    floodRule1 = "Turn off gas and electricity supply",
    floodRule2 = "Move documents and supplies to upper floors",
    floodRule3 = "Avoid contact with stagnant or muddy water outside",

    // CrisisViewModel checklist
    categoryComm = "Communicate", categoryAssess = "Assess", categoryAct = "Act",
    commAnnounce = "Announce you are safe (I'm Safe button)",
    commCheckStatus = "Check status of all group members",
    commConfirmMeetingPoint = "Confirm meeting point with group",
    assessDangers = "Assess immediate dangers around you",
    assessInjured = "Check if anyone is injured",
    assessExits = "Identify emergency exits",
    actLocation = "Enable location sharing",
    actHeadMeeting = "Head to group meeting point",
    actCall112 = "Call 112 if situation is critical",
    actFollowAuthorities = "Follow authorities instructions",

    // Map
    rendezvousPoints = "Rendez-vous Points", deleteButton = "Delete",
    distanceFromYou = "🗺️ Distance from you: %s",
    enableLocationDistance = "Enable location to calculate distance.",
    membersRadar = "Members Radar", onMap = "On map", noGps = "No GPS",
    setMeetingPointTitle = "Set Meeting Point", locationName = "Location Name",
    latitude = "Latitude", longitude = "Longitude",
    setButton = "Set", point = "📍 Point", meetingPoint = "Meeting Point",

    // Activity Feed
    liveActivityFeed = "Live Activity Feed", noRecentActivity = "No recent activity.",
    majorEmergency = "MAJOR EMERGENCY!",
    justNow = "Just now", oneMinAgo = "1 minute ago", minutesAgo = "%d minutes ago",
    oneHourAgo = "1 hour ago", hoursAgo = "%d hours ago",
    oneDayAgo = "1 day ago", daysAgo = "%d days ago",

    // Offline Guide
    offlineGuideTitle = "Offline Emergency Guide",
    guideEarthquake = "Earthquake",
    eqStep1Title = "Take Cover", eqStep1Desc = "Drop under a sturdy table or desk and hold on to its leg.",
    eqStep2Title = "Protect Yourself", eqStep2Desc = "Cover your head and neck with your arms.",
    eqStep3Title = "Stay Away", eqStep3Desc = "Keep away from windows, mirrors, bookcases, and tall furniture that could fall.",
    eqStep4Title = "Stay Inside", eqStep4Desc = "Do not try to exit the building during the earthquake. Wait until the shaking stops.",
    guideFire = "Fire",
    fireStep1Title = "Raise Alarm", fireStep1Desc = "Shout \"Fire!\" to alert people nearby and trigger the fire alarm.",
    fireStep2Title = "Evacuate Immediately", fireStep2Desc = "Leave the building using the nearest stairs. Do not use the elevator!",
    fireStep3Title = "Heavy Smoke", fireStep3Desc = "If there is a lot of smoke, crawl low to the floor where the air is cleaner.",
    fireStep4Title = "Check Doors", fireStep4Desc = "If a door is hot, do not open it. Find an alternative route.",
    guideEvacuation = "Evacuation",
    evacStep1Title = "Stay Calm", evacStep1Desc = "Do not panic. Follow the instructions of the designated personnel.",
    evacStep2Title = "Follow Signs", evacStep2Desc = "Follow the green emergency exit signs out of the building.",
    evacStep3Title = "Leave Items", evacStep3Desc = "Do not go back for personal belongings.",
    evacStep4Title = "Assembly Point", evacStep4Desc = "Go to the designated Meeting Point and report your presence.",
    guideFirstAid = "First Aid",
    faStep1Title = "Assess Safety", faStep1Desc = "Ensure the area is safe before taking action.",
    faStep2Title = "Check Condition", faStep2Desc = "Check if the victim is conscious and breathing.",
    faStep3Title = "Call Emergency", faStep3Desc = "Call the 112 emergency number immediately if the situation is serious.",
    faStep4Title = "Stop Bleeding", faStep4Desc = "Apply firm pressure with a clean cloth to any heavily bleeding wound.",

    // Checklist Screen
    emergencyChecklist = "Emergency Checklist", completedOf = "%d of %d completed",

    // Status
    updateYourStatus = "Update Your Status",
    tapStatusNotify = "Tap a status to notify your group instantly",
    leaveGroupLogout = "Leave Group / Logout",

    // Settings
    settingsTitle = "Settings", yourProfile = "Your Profile", guest = "Guest",
    memberId = "Member ID: %s", circlesJoined = "%d Circle%s joined",
    appearance = "Appearance", darkMode = "Dark Mode",
    on = "On", off = "Off", languageLabel = "Language", logOut = "Log Out",

    // SOS
    sosEmergencyTitle = "🚨 EMERGENCY!",
    sosHasEmergency = "%s has an emergency!",
    sosEmergencyType = "Emergency type: %s",
    sosViewOnMap = "📍 VIEW ON MAP", sosUnderstood = "Understood",

    // GroupRepository
    msgReportsEmergency = "%s reports EMERGENCY: %s!",
    msgIsSafe = "%s is safe! ✅",
    msgUpdatedLocation = "%s updated her location.",

    // Statuses
    statusSafe = "Safe", statusUnknown = "Unknown", statusNeedsHelp = "Needs Help", statusOnMyWay = "On My Way"
)

// ═══════════════════════════════════════════════════════════════════════════════
// ROMANIAN STRINGS
// ═══════════════════════════════════════════════════════════════════════════════

val RoStrings = AppStrings(
    // Bottom Nav
    navHome = "Acasă", navMap = "Hartă", navFeed = "Feed", navGuide = "Ghid", navSettings = "Setări",

    // Welcome
    welcomeTagline = "Conectare inteligentă. Siguranță în timp real.",
    welcomeRestoringSession = "Se restabilește sesiunea...",
    welcomeLogin = "Autentificare",
    welcomeCreateAccount = "Creează cont",

    // Login
    loginTitle = "Autentificare", loginBack = "Înapoi",
    loginUsername = "Nume utilizator", loginPassword = "Parolă",
    loginButton = "Intră în cerc", loginFailed = "Autentificare eșuată.",

    // Create Account
    signUpTitle = "Înregistrare", createProfile = "Creează profil",
    username = "Nume utilizator", password = "Parolă",
    createCircleTab = "Creează cerc", joinCircleTab = "Alătură-te",
    newCircleName = "Numele noului cerc", inviteCodeLabel = "Cod invitație",
    createAndEnter = "Creează și intră", registrationFailed = "Înregistrarea a eșuat.",

    // Select Group
    youreLoggedIn = "Ești autentificat!",
    notPartOfCircle = "Nu faci parte din niciun cerc.",
    createACircle = "Creează un cerc", joinACircle = "Alătură-te unui cerc",
    circleName = "Numele cercului", createButton = "Creează", joinButton = "Alătură-te",

    // Home
    enterCrisisMode = "🚨 ACTIVEAZĂ MODUL CRIZĂ 🚨", addCircle = "Adaugă cerc",
    inviteCodeTap = "Cod invitație: %s (Apasă pentru copiere)",
    imSafe = "SUNT ÎN SIGURANȚĂ", onMyWay = "SUNT PE DRUM", needHelp = "AM NEVOIE DE AJUTOR",
    noActiveCircle = "Nu ai un cerc activ.",
    createOrJoinCircle = "Creează sau alătură-te unui cerc",

    // Crisis
    crisisMode = "MOD CRIZĂ", areYouSafe = "Ești în siguranță?",
    emergency = "URGENȚĂ", checklist = "📋 Checklist",
    bunkers = "🛡️ Buncăre", hospitals = "🏥 Spitale",
    call112 = "📞 Sună 112", backToHome = "← Înapoi acasă",
    confirmEmergency = "🚨 Confirmă urgența",
    confirmEmergencyBody = "Ești sigur că este o urgență reală? Selectează tipul:",
    sendEmergency = "🆘 Trimite urgența", cancel = "Anulează", understood = "Am înțeles",

    // Crisis types
    fire = "Incendiu", earthquake = "Cutremur", militaryRisk = "Risc Militar", flood = "Inundație",

    // General rules
    ruleStayCalm = "Păstrează calmul: Menține-ți mintea limpede și analizează pericolul.",
    ruleAssessSituation = "Evaluează situația: Ești într-un loc sigur? Dacă nu, mută-te urgent.",
    ruleCall112 = "Sună la 112: Semnalează autoritățile dacă există victime.",
    ruleAlertGroup = "Alertează grupul: Așteaptă confirmarea locațiilor de la ceilalți.",

    // Fire rules
    fireRule1 = "Folosește scările, evită total lifturile",
    fireRule2 = "Stai cât mai aproape de podea",
    fireRule3 = "Acoperă nasul/gura cu o cârpă umedă",

    // Earthquake rules
    quakeRule1 = "Adăpostește-te sub un birou/masă solidă",
    quakeRule2 = "Îndepărtează-te de ferestre sau mobilier înalt",
    quakeRule3 = "Așteaptă oprirea seismului înainte să ieși",
    quakeRule4 = "Nu folosi scările în timpul cutremurului",

    // Military rules
    milRule1 = "Evacuează zona organizat dacă ieșirea e sigură",
    milRule2 = "Nu declanșa echipamente electronice în proximitate",
    milRule3 = "Caută cel mai apropiat adăpost civil / buncăr",

    // Flood rules
    floodRule1 = "Oprește alimentarea cu gaz și electricitate",
    floodRule2 = "Mută actele și proviziile la etajele superioare",
    floodRule3 = "Evită contactul cu apa stagnantă sau tulbure",

    // CrisisViewModel checklist
    categoryComm = "Comunică", categoryAssess = "Evaluează", categoryAct = "Acționează",
    commAnnounce = "Anunță că ești în siguranță (buton Sunt în siguranță)",
    commCheckStatus = "Verifică statusul tuturor membrilor grupului",
    commConfirmMeetingPoint = "Confirmă punctul de întâlnire cu grupul",
    assessDangers = "Evaluează pericolele imediate din jur",
    assessInjured = "Verifică dacă există persoane rănite",
    assessExits = "Identifică ieșirile de urgență",
    actLocation = "Activează partajarea locației",
    actHeadMeeting = "Mergi spre punctul de întâlnire al grupului",
    actCall112 = "Sună 112 dacă situația este critică",
    actFollowAuthorities = "Urmează instrucțiunile autorităților",

    // Map
    rendezvousPoints = "Puncte de întâlnire", deleteButton = "Șterge",
    distanceFromYou = "🗺️ Distanță față de tine: %s",
    enableLocationDistance = "Activează locația pentru a calcula distanța.",
    membersRadar = "Radar membri", onMap = "Pe hartă", noGps = "Fără GPS",
    setMeetingPointTitle = "Setează punct de întâlnire",
    locationName = "Nume locație", latitude = "Latitudine", longitude = "Longitudine",
    setButton = "Setează", point = "📍 Punct", meetingPoint = "Punct de întâlnire",

    // Activity Feed
    liveActivityFeed = "Feed activitate live", noRecentActivity = "Nicio activitate recentă.",
    majorEmergency = "URGENȚĂ MAJORĂ!",
    justNow = "Chiar acum", oneMinAgo = "acum 1 minut", minutesAgo = "acum %d minute",
    oneHourAgo = "acum 1 oră", hoursAgo = "acum %d ore",
    oneDayAgo = "acum 1 zi", daysAgo = "acum %d zile",

    // Offline Guide
    offlineGuideTitle = "Ghid urgențe offline",
    guideEarthquake = "Cutremur",
    eqStep1Title = "Adăpostește-te", eqStep1Desc = "Aruncă-te sub o masă sau birou solid și ține-te de piciorul acestuia.",
    eqStep2Title = "Protejează-te", eqStep2Desc = "Acoperă-ți capul și gâtul cu brațele.",
    eqStep3Title = "Stai departe", eqStep3Desc = "Stai departe de ferestre, oglinzi, rafturi și mobilier înalt care ar putea cădea.",
    eqStep4Title = "Rămâi înăuntru", eqStep4Desc = "Nu încerca să ieși din clădire în timpul cutremurului. Așteaptă până se oprește.",
    guideFire = "Incendiu",
    fireStep1Title = "Dă alarma", fireStep1Desc = "Strigă \"Foc!\" pentru a alerta persoanele din apropiere și declanșează alarma de incendiu.",
    fireStep2Title = "Evacuează imediat", fireStep2Desc = "Părăsește clădirea pe scări. Nu folosi liftul!",
    fireStep3Title = "Fum dens", fireStep3Desc = "Dacă este mult fum, târăște-te pe podea unde aerul este mai curat.",
    fireStep4Title = "Verifică ușile", fireStep4Desc = "Dacă o ușă este fierbinte, nu o deschide. Caută un traseu alternativ.",
    guideEvacuation = "Evacuare",
    evacStep1Title = "Stai calm", evacStep1Desc = "Nu intra în panică. Urmează instrucțiunile personalului desemnat.",
    evacStep2Title = "Urmează indicatoarele", evacStep2Desc = "Urmează indicatoarele verzi de ieșire de urgență.",
    evacStep3Title = "Lasă lucrurile", evacStep3Desc = "Nu te întoarce după bunuri personale.",
    evacStep4Title = "Punct de adunare", evacStep4Desc = "Mergi la punctul de întâlnire desemnat și anunță-ți prezența.",
    guideFirstAid = "Prim Ajutor",
    faStep1Title = "Evaluează siguranța", faStep1Desc = "Asigură-te că zona este sigură înainte de a acționa.",
    faStep2Title = "Verifică starea", faStep2Desc = "Verifică dacă victima este conștientă și respiră.",
    faStep3Title = "Sună la urgență", faStep3Desc = "Apelează imediat 112 dacă situația este gravă.",
    faStep4Title = "Oprește sângerarea", faStep4Desc = "Aplică presiune fermă cu o cârpă curată pe orice rană care sângerează abundent.",

    // Checklist Screen
    emergencyChecklist = "Checklist de urgență", completedOf = "%d din %d completat(e)",

    // Status
    updateYourStatus = "Actualizează-ți statusul",
    tapStatusNotify = "Apasă un status pentru a notifica grupul instant",
    leaveGroupLogout = "Părăsește grupul / Deconectare",

    // Settings
    settingsTitle = "Setări", yourProfile = "Profilul tău", guest = "Invitat",
    memberId = "ID Membru: %s", circlesJoined = "%d cerc(uri) alăturat(e)",
    appearance = "Aspect", darkMode = "Mod întunecat",
    on = "Activat", off = "Dezactivat", languageLabel = "Limbă", logOut = "Deconectare",

    // SOS
    sosEmergencyTitle = "🚨 URGENȚĂ!",
    sosHasEmergency = "%s are o urgență!",
    sosEmergencyType = "Tip urgență: %s",
    sosViewOnMap = "📍 VEZI PE HARTĂ", sosUnderstood = "Am înțeles",

    // GroupRepository
    msgReportsEmergency = "%s raportează URGENȚĂ: %s!",
    msgIsSafe = "%s este în siguranță! ✅",
    msgUpdatedLocation = "%s și-a actualizat locația.",

    // Statuses
    statusSafe = "În siguranță", statusUnknown = "Necunoscut",
    statusNeedsHelp = "Are nevoie de ajutor", statusOnMyWay = "Pe drum"
)

fun stringsFor(lang: AppLanguage): AppStrings = when (lang) {
    AppLanguage.EN -> EnStrings
    AppLanguage.RO -> RoStrings
}
