# CURRENT_STATUS.md - Project Implementation Status

## Cloud Infrastructure & Live Services
- **GitHub Repository**: [wcsurface/BowlToOcean](https://github.com/wcsurface/BowlToOcean)
- **Railway Backend REST API**: [backend-production-9e7da31c.up.railway.app](https://backend-production-9e7da31c.up.railway.app)
  - `POST /api/sync` - Player save state synchronization
  - `GET /api/config` - Remote game balance constants
  - `POST /api/leaderboard` - Closed testing bio-load ranking
- **Vercel Showcase & Legal Web Pages**: [bowltoocean-web-surface7.vercel.app](https://bowltoocean-web-surface7.vercel.app)
  - Privacy Policy: [/privacy](https://bowltoocean-web-surface7.vercel.app/privacy)
  - Terms of Service: [/terms](https://bowltoocean-web-surface7.vercel.app/terms)

---

## Current Phase: 60 FPS Mesh Engine, Dark-Ocean HUD & Cloud Economy Operational

### Implemented Components & Bug Fixes
- [x] **Crash Fix**: Resolved `java.lang.IllegalArgumentException: positions and colors lengths must match` in `drawFishMesh` ([`ui/AquariumCanvas.kt`](file:///G:/My%20Drive/APPS/BowltoOcean/app/src/main/java/com/wcsurface/bowltoocean/ui/AquariumCanvas.kt)) by passing matching color array size (`colors = List(numVertices) { Color.White }`).
- [x] **Catalog Modal Bottom Sheet ([`ui/CatalogSheet.kt`](file:///G:/My%20Drive/APPS/BowltoOcean/app/src/main/java/com/wcsurface/bowltoocean/ui/CatalogSheet.kt))**:
  - **Tanks & Exhibits Tab**: Vertical listing of all 7 `TankTier` stages (`BOWL` $\rightarrow$ `MEGA_TANK`), displaying preview badges, water capacity, max fish count, unlock cost, and "Switch / Unlock" buttons.
  - **Fish & Creatures Tab**: Compatible species store for the active tank tier plus locked preview badges for higher-tier species (e.g. Scalloped Hammerhead and Giant Manta Ray). Buying fish checks tank capacity and deducts Care Points.
- [x] **Non-Punishing Care & Bio-Load Economy Loop**:
  - Fish never die from neglect.
  - High bio-load balance ($>80\%$) grants a $2.0\times$ Care Points multiplier and triggers sparkling water particle effects.
  - Low bio-load balance ($<30\%$) applies a $0.5\times$ rate and renders a subtle greenish-murky water tint overlay.
- [x] **Hybrid DataStore + Railway Repository ([`data/AquariumRepository.kt`](file:///G:/My%20Drive/APPS/BowltoOcean/app/src/main/java/com/wcsurface/bowltoocean/data/AquariumRepository.kt))**:
  - Jetpack DataStore Preferences for local 0ms offline-first persistence.
  - `fetchRemoteConfig()`: Pulls live game balance parameters from Railway `/api/config` on startup with fallback defaults.
  - `syncPlayerStateToRailway()`: Debounced background `POST /api/sync` calls when buying fish, unlocking tanks, or app pause (`ON_STOP`).
- [x] **ViewModel Architecture ([`viewmodel/AquariumViewModel.kt`](file:///G:/My%20Drive/APPS/BowltoOcean/app/src/main/java/com/wcsurface/bowltoocean/viewmodel/AquariumViewModel.kt))**:
  - `StateFlow<AquariumUiState>` exposing unidirectional data flow to Jetpack Compose UI.
- [x] **60 FPS Jetpack Compose 2D Mesh Simulation Engine ([`engine/AquariumEngine.kt`](file:///G:/My%20Drive/APPS/BowltoOcean/app/src/main/java/com/wcsurface/bowltoocean/engine/AquariumEngine.kt))**:
  - `Canvas.drawVertices()` 2D mesh skinning in `VertexMode.TriangleStrip` (20 columns / 42 vertices).
  - Travelling sine wave spine deformation, vertical steering curve, and 250ms direction flip.
  - Boids flocking algorithm for schooling fish.
  - Sinking food pellets, feeding consumption, and floating text popups.
  - Concentric glass tap ripples, clean-view HUD toggle, and startled/curious fish reaction.
  - Atmospheric water layer with caustic light shafts, rising bubbles, murky water tint, and sparkling water dots.
- [x] **Unit Testing Suite**:
  - 11 unit tests passing across [`AquariumModelsTest.kt`](file:///G:/My%20Drive/APPS/BowltoOcean/app/src/test/java/com/wcsurface/bowltoocean/AquariumModelsTest.kt), [`AquariumEngineTest.kt`](file:///G:/My%20Drive/APPS/BowltoOcean/app/src/test/java/com/wcsurface/bowltoocean/AquariumEngineTest.kt), and [`AquariumRepositoryTest.kt`](file:///G:/My%20Drive/APPS/BowltoOcean/app/src/test/java/com/wcsurface/bowltoocean/AquariumRepositoryTest.kt).
