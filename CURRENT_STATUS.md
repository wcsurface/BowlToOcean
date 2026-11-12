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

## Current Phase: Procedural Audio, Haptics, Settings & Mesh Simulation Complete

### Implemented Components & Features
- [x] **Zero-Asset Procedural Audio Synthesizer ([`audio/AquariumAudioManager.kt`](file:///G:/My%20Drive/APPS/BowltoOcean/app/src/main/java/com/wcsurface/bowltoocean/audio/AquariumAudioManager.kt))**:
  - `playGlassTap()`: 140Hz decaying low-mid underwater glass thud with 420Hz harmonic decay.
  - `playBubblePop()`: Upward chirping water droplet frequency sweep (450Hz to 900Hz over 60ms).
  - `playMaintenanceSwoosh()`: Soft filtered white-noise water rush envelope (260ms).
  - `playRewardChime()`: Warm harmonic triad chime (C5, E5, G5) with smooth decay for tank unlocks and daily bonuses.
- [x] **Tactile Haptic Feedback**:
  - `HapticFeedbackType.TextHandleMove` triggered on glass taps.
  - `HapticFeedbackType.LongPress` triggered on startled fish bursts and new tank unlocks.
- [x] **Settings & Debug Dialog ([`ui/SettingsDialog.kt`](file:///G:/My%20Drive/APPS/BowltoOcean/app/src/main/java/com/wcsurface/bowltoocean/ui/SettingsDialog.kt))**:
  - Frosted glass dialog with switches for Sound Effects, Haptics, and Live FPS / Debug Overlay.
  - "Reset Save Data (Closed Testing)" button with confirmation dialog.
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
  - 12 unit tests passing across [`AquariumModelsTest.kt`](file:///G:/My%20Drive/APPS/BowltoOcean/app/src/test/java/com/wcsurface/bowltoocean/AquariumModelsTest.kt), [`AquariumEngineTest.kt`](file:///G:/My%20Drive/APPS/BowltoOcean/app/src/test/java/com/wcsurface/bowltoocean/AquariumEngineTest.kt), [`AquariumRepositoryTest.kt`](file:///G:/My%20Drive/APPS/BowltoOcean/app/src/test/java/com/wcsurface/bowltoocean/AquariumRepositoryTest.kt), and [`AquariumAudioTest.kt`](file:///G:/My%20Drive/APPS/BowltoOcean/app/src/test/java/com/wcsurface/bowltoocean/AquariumAudioTest.kt).
