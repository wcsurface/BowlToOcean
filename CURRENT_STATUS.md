# CURRENT_STATUS.md - Project Implementation Status

## Repository & Infrastructure
- **GitHub Repository**: [wcsurface/BowlToOcean](https://github.com/wcsurface/BowlToOcean)
- **Railway Backend**: `/backend` Node.js REST API
- **Vercel Web App**: `/web` Landing & Legal Pages (/privacy, /terms)

## Current Phase: Core Engine, Cloud Pipeline & Backend Active

### Implemented Components
- [x] **Gradle Build Setup & Dependencies**:
  - `androidx.datastore:datastore-preferences:1.1.3`
  - `androidx.lifecycle:lifecycle-viewmodel-compose:2.11.0`
  - `androidx.compose.material:material-icons-extended`
- [x] **Governance Documentation**:
  - `AGENTS.md`
  - `PROJECT_MEMORY.md`
  - `CURRENT_STATUS.md`
  - `REQUIREMENTS.md`
- [x] **Domain Models (`model/AquariumModels.kt`)**:
  - 7 `TankTier` definitions with costs, capacities, background drawables, ambient tints, and 3 specific maintenance actions each.
  - 14 `FishSpecies` definitions with swim speeds, wave frequencies, wave amplitudes, bio-load, and schooling parameters.
  - `FishInstance` runtime state class with position, velocity, direction, flip progress, and AI state machine.
- [x] **Safe Fallback Asset Resolver (`util/AssetResolver.kt`)**:
  - Loads PNG drawables from resources if present.
  - Synthesizes dynamic high-contrast procedural `ImageBitmap` fallbacks with species/tank signatures if PNG resources are missing.
- [x] **60 FPS Simulation Engine (`engine/AquariumEngine.kt`)**:
  - `withFrameNanos` update loop driver with `deltaTime` clamped to `0.05f`.
  - Autonomous AI state machine (`CRUISING`, `STARTLED`, `CURIOUS`, `SEEKING_FOOD`).
  - Schooling boids flocking algorithm (Separation, Alignment, Cohesion).
  - Food pellet sinking dynamics and fish feeding consumption.
  - Ambient rising air bubbles with sine-wave horizontal wobble.
- [x] **Hardware-Accelerated 2D Mesh Skinning Renderer ([`ui/AquariumCanvas.kt`](file:///G:/My%20Drive/APPS/BowltoOcean/app/src/main/java/com/wcsurface/bowltoocean/ui/AquariumCanvas.kt))**:
  - `FishMeshRenderer` using `Canvas.drawVertices()` in `VertexMode.TriangleStrip` (20 horizontal columns / 42 vertices).
  - Travelling sine wave along normalized spine $u \in [0, 1]$: $offsetY = \sin(phase - u \cdot waveFreq) \cdot (waveAmp \cdot u^{1.6f})$.
  - Steering curve offset proportional to vertical velocity $vy$.
  - Smooth 250ms horizontal flip interpolation (`flipProgress` from -1f to 1f) for seamless direction turns.
  - Atmospheric water layer with caustic light shafts, rising bubbles, sinking food, glass tap ripples, and floating text popups.
- [x] **Glass-Tap Reaction & Clean-View Toggle**:
  - `detectTapGestures` handler spawning expanding concentric `GlassRipple` rings.
  - Toggles `isHudVisible` clean-view mode.
  - Startles nearby fish within 250dp ($0.65s$ darting burst at $3.2 \times$ tail-beat speed, followed by $2.0s$ `CURIOUS` return before resuming `CRUISING`).
- [x] **Unit Testing Suite**:
  - [`AquariumModelsTest.kt`](file:///G:/My%20Drive/APPS/BowltoOcean/app/src/test/java/com/wcsurface/bowltoocean/AquariumModelsTest.kt) and [`AquariumEngineTest.kt`](file:///G:/My%20Drive/APPS/BowltoOcean/app/src/test/java/com/wcsurface/bowltoocean/AquariumEngineTest.kt) (8 unit tests passing).

---

## Next Steps
- [ ] Implement `DataStoreRepository` for persistent game save/load.
- [ ] Implement `AquariumViewModel` to expose StateFlow to UI layers.
- [ ] Build interactive UI overlay (Shop drawer, Tank Tier Switcher, Maintenance Action Bar, Ecosystem Stats).
