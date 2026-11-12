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

## Current Phase: Engine & Cloud Pipeline Operational

### Implemented Components
- [x] **GitHub Initialization & Commit Pipeline**: Repository created, committed, and synced at `wcsurface/BowlToOcean`.
- [x] **Railway Backend REST API (`/backend`)**:
  - Express.js server deployed with `/api/sync`, `/api/config`, and `/api/leaderboard` endpoints.
- [x] **Vercel Web Landing & Compliance (`/web`)**:
  - Hosted showcase site with Google Play required Privacy Policy and Terms of Service.
- [x] **60 FPS Jetpack Compose 2D Mesh Simulation Engine**:
  - `Canvas.drawVertices()` 2D mesh skinning in `VertexMode.TriangleStrip` (20 columns / 42 vertices).
  - Travelling sine wave spine deformation, vertical steering curve, and 250ms direction flip.
  - Boids flocking algorithm for schooling fish.
  - Sinking food pellets, feeding consumption, and floating text popups.
  - Concentric glass tap ripples, clean-view HUD toggle, and startled/curious fish reaction.
  - Atmospheric water layer with caustic light shafts and rising bubbles.
- [x] **Domain Models & Asset Resolver**:
  - 7 `TankTier` definitions, 14 `FishSpecies` definitions, `FishInstance` state machine.
  - Procedural high-contrast asset fallback generator.

---

## Next Steps
- [ ] Implement `DataStoreRepository` for persistent game save/load on Android client.
- [ ] Connect Android ViewModel to Railway REST API (`/api/sync` and `/api/config`).
- [ ] Build interactive UI overlay (Shop drawer, Tank Switcher, Maintenance Action Bar, Ecosystem Stats).
