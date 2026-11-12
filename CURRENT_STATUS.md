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

## Current Phase: 60 FPS Engine & Dark-Ocean Frosted-Glass HUD Complete

### Implemented Components
- [x] **Dark-Ocean Frosted-Glass UI Overlay ([`ui/AquariumHud.kt`](file:///G:/My%20Drive/APPS/BowltoOcean/app/src/main/java/com/wcsurface/bowltoocean/ui/AquariumHud.kt))**:
  - `AnimatedVisibility` wrapper (`fadeIn`/`fadeOut` in 220ms) tied to `isHudVisible`.
  - 100% clean bottom edge with zero obstructive controls.
  - **TopStatusBar**: Care Points pill (gold coin icon), Diamonds pill (cyan gem icon), Level & XP progress bar, Bio-Load filtration meter with color-shifting bar (Green $>75\%$, Amber $40\%\dots74\%$, Red $<40\%$), and compact Settings button.
  - **Right-Hand Vertical Dock**:
    - Daily Bonus gift icon with red notification dot.
    - **Feed Button**: Drops 3 floating food pellets into top of tank with 2.0s cooldown indicator.
    - **Maintain Button**: Toggles slide-out maintenance drawer.
    - **Catalog Button**: Opens progression & fish shop modal bottom sheet.
  - **Slide-Out Maintain Drawer**: Displays 3 active `MaintenanceAction` tiles with cleaning bonuses, rewards, and clean sweep particle animation triggers.
  - **Catalog Modal Bottom Sheet**: Tabbed progression sheet displaying Tank Tiers (`BOWL` to `MEGA_TANK`) and Fish Species store.
- [x] **60 FPS Jetpack Compose 2D Mesh Simulation Engine ([`engine/AquariumEngine.kt`](file:///G:/My%20Drive/APPS/BowltoOcean/app/src/main/java/com/wcsurface/bowltoocean/engine/AquariumEngine.kt))**:
  - `Canvas.drawVertices()` 2D mesh skinning in `VertexMode.TriangleStrip` (20 columns / 42 vertices).
  - Travelling sine wave spine deformation, vertical steering curve, and 250ms direction flip.
  - Boids flocking algorithm for schooling fish.
  - Sinking food pellets, feeding consumption, and floating text popups.
  - Concentric glass tap ripples, clean-view HUD toggle, and startled/curious fish reaction.
  - Atmospheric water layer with caustic light shafts and rising bubbles.
- [x] **Unit Testing Suite**:
  - [`AquariumModelsTest.kt`](file:///G:/My%20Drive/APPS/BowltoOcean/app/src/test/java/com/wcsurface/bowltoocean/AquariumModelsTest.kt) and [`AquariumEngineTest.kt`](file:///G:/My%20Drive/APPS/BowltoOcean/app/src/test/java/com/wcsurface/bowltoocean/AquariumEngineTest.kt) (8 unit tests passing).

---

## Next Steps
- [ ] Implement `DataStoreRepository` for persistent game save/load on Android client.
- [ ] Connect Android client to Railway REST API (`/api/sync` and `/api/config`).
