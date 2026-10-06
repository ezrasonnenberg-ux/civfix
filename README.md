
**                                   CivFix - Community Infrastructure Issue Reporting & Tracking App
**

Institution: IU International University of Applied Sciences 
Student Name: Ezra Sonnenberg
Matriculation No: 9219811
Course Code: DLBSEPPSD01_E - Software Development  
Degree Programme: B.Sc. Software Development
Professor: Christian Remfert

Task: Task 1 - Development of a Mobile Application  

1. Project Overview & Problem Statement

The Addressed Problem
Local municipalities and public works departments often suffer from delayed response times regarding civic infrastructure damage (e.g., deep potholes, broken streetlights, sanitation overflows, and damaged public amenities). Traditional reporting channels (telephone hotlines, web forms) are fragmented, lack precise real-time geolocation data, and offer citizens zero visibility into ticket resolution progress.

Proposed Solution
CivFix is an Android mobile application engineered with Kotlin and Jetpack Compose. It allows citizens to capture photographic visual proof, extract real-time device GPS coordinates, categorize municipal hazards, and monitor the ticket resolution lifecycle (`Pending` → `In Progress` → `Resolved`).

Target Audience
 Primary Users: Everyday residents, commuters, and neighborhood watch associations.
 Secondary Users: Municipal contractors and public works dispatch teams tracking resolution status.

Justification for a Mobile App
Infrastructure hazards are encountered while citizens are on the move. A mobile application natively leverages on-device hardware sensors-specifically the device camera and the Fused Location Provider GPS sensor-which desktop solutions cannot access on-site.

2. Technical Stack & Architecture

Mobile Client (Android)
Language: Kotlin 1.9+
UI Toolkit: Jetpack Compose (Material Design 3 Guidelines)
Architecture Pattern: Clean Component Architecture (UI Components, State Hoisting, Repositories, Domain Models)
Location Engine: Google Play Services Location (`ACCESS_FINE_LOCATION`)
Serialization & Asynchronous Processing: KotlinX Serialization, Kotlin Coroutines (`Dispatchers.IO` / `Dispatchers.Main`)
Testing: JUnit 4 for core domain logic and time-formatting utilities

Backend & Cloud Architecture (C4 Model Context)
Cloud Database: PostgreSQL managed via Supabase (PostgREST API)
Local Containerization: Docker & Docker Compose (`docker-compose.yml`) providing local database isolation and deployment parity
Persistence Layer: Two-tier persistence strategy utilizing encrypted local `SharedPreferences` caching with cloud database synchronization

3. System Architecture (C4 Container Diagram)

+-----------------------------------------------------------------------+
| CivFix Client |
| (Android / Jetpack Compose) |
| +--------------------+ +---------------------+ +---------------+ |
| | Presentation Layer|-->| Repository Layer |-->| Sensors / GPS | |
| | (Feed, Map, Report)| | (IssueRepository) | | (LocationAPI) | |
| +--------------------+ +---------------------+ +---------------+ |
+--------------------------------------|--------------------------------+
|
HTTPS / REST API |
+-----------------------------------------------------------------------+
| Backend Layer |
| |
| +-------------------------------+ +-----------------------------+ |
| | Supabase Cloud DB | | Docker Compose Database | |
| | (PostgreSQL Instance) | | (Local Testing Instance) | |
| +-------------------------------+ +-----------------------------+ |
+-----------------------------------------------------------------------+

4. Key Functional Features

1. Community Issue Feed:
    Dynamic status indicators (`Pending`, `In Progress`, `Resolved`).
    Relative dynamic timestamps ("Just now", "15m ago", "2h ago").
    Interactive category filter pills (Potholes, Streetlights, Sanitation, Graffiti).
    Real-time search query matching on titles, descriptions, and addresses.
    Empty state handling when no reports match search parameters.

2. Incident Creation & Visual Proof:
    Category grid and severity selector (Low, Medium, High, Critical).
    Native device gallery picker (`ActivityResultContracts.GetContent`) for photo attachment.
    Live GPS capture using device location hardware with automatic fallback handling.
    Debounce protection on submit button to prevent double-tap race conditions.

3. Interactive Map View:
    Vector-based map canvas plotting live GPS coordinates.
    Marker selection displaying bottom card summaries with resolution priority tiers.
    Full detail modal dialog showing complete incident logs.

4. Lifecycle Progression & Archiving:
    Sequential state machine transitioning issues: `Pending` → `In Progress` → `Resolved`.
    Action buttons dynamically update based on current state and auto-hide once resolved.

5. Project Directory Structure

civfix/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/civfix/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── data/
│   │   │   │   │   ├── DummyDataRepository.kt
│   │   │   │   │   ├── IssueRepository.kt
│   │   │   │   │   ├── Issues.kt
│   │   │   │   │   ├── LocationHelper.kt
│   │   │   │   │   └── SupabaseClient.kt
│   │   │   │   └── ui/
│   │   │   │       ├── components/
│   │   │   │       │   ├── common/
│   │   │   │       │   ├── feed/
│   │   │   │       │   └── report/
│   │   │   │       └── screens/
│   │   │   │           ├── MapScreen.kt
│   │   │   │           ├── ProfileScreen.kt
│   │   │   │           └── ReportFormScreen.kt
│   │   │   └── AndroidManifest.xml
│   │   └── test/java/com/example/civfix/
│   │       └── CivFixUnitTests.kt
│   └── build.gradle.kts
├── backend/
│   └── supabase/
│       ├── docker-compose.yml
│       └── schema.sql
├── build.gradle.kts
├── settings.gradle.kts
└── README.md

6. How to Build & Run
Android Client
    Clone the repository:
  
    git clone https://github.com/ezrasonnenberg-ux/civfix.git

    Open the project in Android Studio Hedgehog / Jellyfish / Ladybug.
    Allow Gradle to sync dependencies automatically.
    Select a virtual device emulator (API 34+) or connect a physical Android device.

    Click Run 'app' (Shift + F10).
Running the Backend Container (Docker)

Per course examination criteria, the application backend is containerized:

    Open a terminal and navigate to the backend directory:

    cd backend/supabase

    Start the container in detached mode:
    docker compose up -d
    
    To inspect container status:
    docker ps

    To terminate the container environment:
    docker compose down

7. Running Unit Tests

To run the JUnit test suite covering core domain classes and time calculations:

    Open Android Studio.
    In the Project pane, navigate to: app/src/test/java/com/example/civfix/CivFixUnitTests.kt.
    Right-click the file and select Run 'CivicFixUnitTests'.
