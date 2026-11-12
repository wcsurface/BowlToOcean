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

## Current Phase: Direct ImageBitmap.imageResource Loading & Software ARGB_8888 White Pixel Removal Complete

### Implemented Hard-Fail Pipeline
- [x] **Direct Compose Resource Loader ([`ui/AquariumCanvas.kt`](file:///G:/My%20Drive/APPS/BowltoOcean/app/src/main/java/com/wcsurface/bowltoocean/ui/AquariumCanvas.kt))**:
  - `val bowlBitmap = ImageBitmap.imageResource(id = R.drawable.bg_tank_1_bowl)`
  - `val rawBettaBitmap = ImageBitmap.imageResource(id = R.drawable.fish_betta_blue)`
  - Direct 100% canvas rendering: `drawImage(bowlBitmap, dstSize = IntSize(width, height))`.
- [x] **Software ARGB_8888 Pixel Stripper**:
  - `rawBettaBitmap.asAndroidBitmap().copy(Bitmap.Config.ARGB_8888, true)` converts hardware bitmap to mutable software ARGB_8888 array.
  - Loops over pixels to set white/near-white pixels ($r > 235 \land g > 235 \land b > 235 \implies 0$) to transparent.
  - Converts back to `ImageBitmap` via `.asImageBitmap()` inside `remember(rawBettaBitmap)`.
- [x] **Hero Betta Canvas.drawVertices Rendering**:
  - Processed `bettaBitmap` passed directly to `drawFishMesh` for 2D mesh skinning animation.
- [x] **Unit Testing Suite**:
  - All 12 unit tests passing across test suite.
