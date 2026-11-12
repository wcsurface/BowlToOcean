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

## Current Phase: Direct Drawables, Visible Hero Fish & Fixed Top HUD Layout

### Implemented Fixes & Overhauls
- [x] **Direct Resource Loading & Background Artwork ([`res/drawable-nodpi/bg_tank_1_bowl.png`](file:///G:/My%20Drive/APPS/BowltoOcean/app/src/main/res/drawable-nodpi/bg_tank_1_bowl.png))**:
  - Saved high-resolution PNG drawables directly in `res/drawable-nodpi/`.
  - Draw background `bg_tank_1_bowl` to fill 100% of the screen with `ContentScale.Crop`.
- [x] **Betta Visibility & Pixel-Space UV Texture Mesh Fix ([`ui/AquariumCanvas.kt`](file:///G:/My%20Drive/APPS/BowltoOcean/app/src/main/java/com/wcsurface/bowltoocean/ui/AquariumCanvas.kt))**:
  - Resolved texture invisibility in `Canvas.drawVertices` by scaling UV texture coordinates to **pixel space** ($[0 \dots \text{bitmapWidth}] \times [0 \dots \text{bitmapHeight}]$).
  - Prominent hero size ($\sim 240\text{dp}$ wide) Betta spawned at center ($0.50f, 0.48f$).
  - Added white background pixel stripper in [`AssetResolver.kt`](file:///G:/My%20Drive/APPS/BowltoOcean/app/src/main/java/com/wcsurface/bowltoocean/util/AssetResolver.kt) ($R > 240 \land G > 240 \land B > 240 \implies \text{alpha} = 0$).
  - [`AquariumEngine.kt`](file:///G:/My%20Drive/APPS/BowltoOcean/app/src/main/java/com/wcsurface/bowltoocean/engine/AquariumEngine.kt): Added `ensureStarterFishActive()` to guarantee 1 active Royal Blue Halfmoon Betta strictly clamped inside the central glass bowl.
- [x] **Top HUD Bar Layout Overlap Fix ([`ui/AquariumHud.kt`](file:///G:/My%20Drive/APPS/BowltoOcean/app/src/main/java/com/wcsurface/bowltoocean/ui/AquariumHud.kt))**:
  - Fixed center `Lv. 3` XP progress bar text expanding layout by constraining column width (`width(70.dp)`), removing `fillMaxWidth()`, and setting `padding(end = 56.dp)`.
  - Restored clean visibility for Tank Status / Bio-Load gauge and Settings gear button on the right edge without overlapping the Daily Bonus gift icon.
- [x] **Unit Testing Suite**:
  - All 12 unit tests passing across test suite.
