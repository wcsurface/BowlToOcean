# PROJECT_MEMORY.md - System Context & Architectural Memory

## Project Core Identity
- **App Name**: Bowl to Ocean Aquarium Sim
- **Package Name**: `com.wcsurface.bowltoocean`
- **GitHub Repository**: https://github.com/wcsurface/BowlToOcean
- **Railway Backend REST API**: https://backend-production-9e7da31c.up.railway.app
- **Vercel Showcase Landing Page**: https://bowltoocean-web-surface7.vercel.app
- **Privacy Policy**: https://bowltoocean-web-surface7.vercel.app/privacy
- **Terms of Service**: https://bowltoocean-web-surface7.vercel.app/terms
- **Target Platform**: Android (Min SDK 26, Target/Compile SDK 37)
- **Deployment Target**: Google Play Closed Testing

---

## Cloud Architecture & Cloud-Native Pipeline

### 1. GitHub Repository (`wcsurface/BowlToOcean`)
- Source of truth for Android Jetpack Compose engine, Node.js Railway backend, and Vercel web landing pages.
- Auto-deploys Railway and Vercel services on git push to `main`.

### 2. Railway Backend REST API (`/backend`)
- Deployed on Railway: `https://backend-production-9e7da31c.up.railway.app`
- **`POST /api/sync`**: Upserts player state (`playerId`, `carePoints`, `diamonds`, `xp`, `activeTankTier`, `unlockedTanks`, `ownedFishJson`, `bioLoadJson`).
- **`GET /api/config`**: Serves live remote-tunable game balance constants without needing new Play Store build updates.
- **`POST /api/leaderboard`**: Receives player bio-load submissions and returns top exhibit collection rankings.

### 3. Vercel Web & Google Play Compliance (`/web`)
- Deployed on Vercel: `https://bowltoocean-web-surface7.vercel.app`
- Hosted Legal Endpoints:
  - Privacy Policy: `https://bowltoocean-web-surface7.vercel.app/privacy`
  - Terms of Service: `https://bowltoocean-web-surface7.vercel.app/terms`
- Fulfills mandatory Google Play Console Closed Testing URL submission requirements.

---

## Android Simulation Architecture

### 1. Rendering Engine: `Canvas.drawVertices()`
- Hardware-accelerated 2D mesh skinning via `Canvas.drawVertices()` in `VertexMode.TriangleStrip` (20 horizontal columns / 42 vertices).
- Travelling sine wave along normalized spine $u \in [0, 1]$: $offsetY = \sin(phase - u \cdot waveFreq) \cdot (waveAmp \cdot u^{1.6f})$.
- Steering curve offset proportional to vertical velocity $vy$.
- Smooth 250ms horizontal turn flip interpolation (`flipProgress` from -1f to 1f).

### 2. State & Persistence Strategy
- **DataStore Preferences**: Stores currency (points), unlocked tank tier, purchased fish instances, clean level, and maintenance timestamps.
- **State Flow**: `AquariumViewModel` exposes read-only `StateFlow<AquariumUiState>` to the Compose layer.

### 3. Tank Tier Progression Architecture
The game features 7 distinct progression tiers:
1. `BOWL`: "Desktop Bowl" (Cost: 0, Max Fish: 1, Capacity: 10)
2. `TANK_5G`: "5-Gal Planted" (Cost: 250, Max Fish: 4, Capacity: 30)
3. `TANK_10G`: "10-Gal Community" (Cost: 1,000, Max Fish: 8, Capacity: 60)
4. `TANK_40G`: "40-Gal Breeder" (Cost: 4,500, Max Fish: 15, Capacity: 120)
5. `TANK_100G`: "100-Gal Reef" (Cost: 18,000, Max Fish: 25, Capacity: 250)
6. `LARGE_DISPLAY`: "Gallery Exhibit" (Cost: 75,000, Max Fish: 45, Capacity: 500)
7. `MEGA_TANK`: "Oceanic Mega-Tank" (Cost: 250,000, Max Fish: 80, Capacity: 1,000)

### 4. Resilient Fallback Asset Management
- `AssetResolver.kt` bridges missing bitmap assets by procedurally generating high-contrast fallbacks using Compose `Canvas` and `ImageBitmap`.
- Ensures zero missing-resource runtime crashes during iterative development.
