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

## Current Phase: Direct Resource Binding, White Pixel Stripping & Bowl Clipping Complete

### Root Cause Fixes Implemented
- [x] **Direct Resource ID Binding ([`AquariumModels.kt`](file:///G:/My%20Drive/APPS/BowltoOcean/app/src/main/java/com/wcsurface/bowltoocean/model/AquariumModels.kt))**:
  - Bound `@DrawableRes val backgroundResId = R.drawable.bg_tank_1_bowl` directly to `TankTier.BOWL`.
  - Bound `@DrawableRes val drawableResId = R.drawable.fish_betta_blue` directly to `FishSpecies.betta_blue`.
- [x] **Direct BitmapFactory Decoding & White Pixel Removal ([`AssetResolver.kt`](file:///G:/My%20Drive/APPS/BowltoOcean/app/src/main/java/com/wcsurface/bowltoocean/util/AssetResolver.kt))**:
  - Deleted the geometric circle bowl & blue oval fallback drawing code.
  - Loads real bitmaps directly via `BitmapFactory.decodeResource(context.resources, resId, ...)` with `ARGB_8888`.
  - Automatically loops through pixels once and strips near-white background pixels (`red > 235 && green > 235 && blue > 235`) to `Color.TRANSPARENT`.
- [x] **Orientation & Bowl Circle Clipping ([`ui/AquariumCanvas.kt`](file:///G:/My%20Drive/APPS/BowltoOcean/app/src/main/java/com/wcsurface/bowltoocean/ui/AquariumCanvas.kt))**:
  - Corrected raw Betta orientation ($u = 0$ is Head on left, $u = 1$ is Tail on right; head is kept steady while $u^{1.6}$ undulates the tail).
  - Clipped caustic light shafts and rising air bubbles strictly inside the glass bowl circle (`center = (width * 0.5f, height * 0.48f)`, `radius = height * 0.45f`), preventing light spilling onto the room background outside the bowl.
- [x] **Unit Testing Suite**:
  - All 12 unit tests passing across test suite.
