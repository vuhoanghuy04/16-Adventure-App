# AGENTS.md

## Project Snapshot
- Android app module only (`:app`) built with AGP `9.1.0`, Java `11` source/target, compile/target SDK `35`.
- Main package is `com.example.a16adventure` (`app/src/main/java/com/example/a16adventure`).
- Existing CI is in `.github/workflows/android-ci.yml` and runs `testDebugUnitTest` + `lintDebug`.
- No existing repo AI-instruction files were found via the requested convention glob search.

## Architecture and Data Flow (high-impact)
- Navigation is Activity-first, not Fragment-first: `MainActivity` is launcher, and `BaseActivity.setupBottomNavigation(...)` routes among Home/Explore/Map/AI/Profile.
- Cross-screen monument state is shared through singleton memory (`MonumentDataManager`), seeded in `MainActivity.setupData()` from `MonumentRepository.loadFromAssets(...)`.
- Important dependency: `MapActivity` and `SavedMonumentsActivity` expect `MonumentDataManager` to already contain monuments; if opened before Home seeds data, lists can be empty.
- Monument source of truth for core browsing is asset JSON (`app/src/main/assets/monuments.json`) with schema keys `id,name,district,description,imageUrl,lat,lng`.
- Quiz content is local asset JSON (`app/src/main/assets/quizzes.json`) while scores persist in Firebase Realtime DB (`users/{uid}/quiz`).
- Knowledge articles are fetched from Firestore collection `articles` in `KnowledgeActivity.fetchArticlesFromFirebase()`.

## Firebase + AI Integration Boundaries
- Auth gates several features (save monument, upload chat image, journal, quiz profile stats): check `FirebaseAuth.getInstance().getCurrentUser()` before write paths.
- Realtime DB paths are not fully uniform:
  - Saved IDs are written/read at `users/{uid}/saved_ids` (`MonumentAdapter`, `SavedMonumentsActivity`).
  - Profile "visited" count reads `users/{uid}/saved` (`ProfileActivity.loadUserStats`) - treat as legacy mismatch risk when changing schema.
  - Journals at `users/{uid}/journals` (`AddJournalActivity`, `JournalListActivity`).
- Firebase Storage usage:
  - Chat uploads: `users/{uid}/chat_images/{uuid}.jpg`.
  - Profile avatars: `profile_images/{uid}.jpg`.
  - Journals: `journals/{uid}/{uuid}.jpg`.
- Gemini setup is local-key based (`BuildConfig.GEMINI_API_KEY`) in `ChatActivity` and `RecognitionActivity`; both fail gracefully with a Toast when key is missing.

## Build/Test/Debug Workflows
- Local secrets come from `local.properties` (template in `local.properties.example`): set `GEMINI_API_KEY` and `MAPS_API_KEY`.
- Gradle module applies Google services plugin; keep `app/google-services.json` present for Firebase-enabled flows.
- Standard commands aligned with CI:
  - `./gradlew testDebugUnitTest`
  - `./gradlew lintDebug`
  - `./gradlew assembleDebug`
- Current unit tests are logic-focused (`KnowledgeActivityTest`, `MonumentDataManagerTest`), not end-to-end UI tests.

## Codebase-Specific Conventions
- Code is Java-only in `app/src/main/java` (no Kotlin sources currently).
- UI text/comments are predominantly Vietnamese; preserve Vietnamese labels and category strings used by filters (e.g., `"Phong tuc"`, `"Di tich"`, `"Tat ca"`).
- Category and district filters are string-equality based across screens (`MainActivity`, `MapActivity`, `KnowledgeActivity`, `MediaLibraryActivity`), so renaming labels can silently break filtering.
- Many screens extend `BaseActivity` specifically to apply theme from `SharedPreferences("Settings")` and keep bottom-nav behavior consistent.
- `MonumentAdapter` mutates in-memory ordering for like/dislike recommendation behavior; avoid replacing list references without updating adapter assumptions.

