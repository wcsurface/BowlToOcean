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

## Current Phase: Widescreen Translucent Aqua-Glass Design & Immersive Fullscreen Complete

### Implemented Overhaul Highlights
- [x] **Forced Sensor-Landscape & Fullscreen Immersive Mode**:
  - `android:screenOrientation="sensorLandscape"` configured in [`AndroidManifest.xml`](file:///G:/My%20Drive/APPS/BowltoOcean/app/src/main/AndroidManifest.xml).
  - True edge-to-edge immersive mode in [`MainActivity.kt`](file:///G:/My%20Drive/APPS/BowltoOcean/app/src/main/java/com/wcsurface/bowltoocean/MainActivity.kt) using `WindowInsetsControllerCompat.hide(Type.systemBars())` with `BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE`.
- [x] **Real Drawables & White-Pixel Background Stripper ([`AssetResolver.kt`](file:///G:/My%20Drive/APPS/BowltoOcean/app/src/main/java/com/wcsurface/bowltoocean/util/AssetResolver.kt))**:
  - Removed all debug placeholder text completely.
  - Automatically converts pure white background pixels ($R > 240 \land G > 240 \land B > 240 \implies \text{alpha} = 0$) to transparent alpha at load time.
  - Hero Betta spawned visibly at exact center ($0.5f, 0.48f$) at prominent scale ($\sim 220\text{dp}$) with swim bounds clamped strictly inside central bowl glass ($25\%\dots75\%$ X, $25\%\dots72\%$ Y).
- [x] **Translucent Aqua-Glass HUD Overlay ([`ui/AquariumHud.kt`](file:///G:/My%20Drive/APPS/BowltoOcean/app/src/main/java/com/wcsurface/bowltoocean/ui/AquariumHud.kt))**:
  - Custom `AquaGlassPanel` container featuring translucent deep-teal gradient (`Color(0xAA13384C)` to `Color(0x880B2230)`) and $1\text{dp}$ luminous cyan border (`Color(0x667CE8FF)`).
  - **Top Bar**: Care Points pill (+ button), Diamonds pill (+ button), Gold Star Level badge (2) + glowing cyan XP progress bar, circular water drop gauge + Bio-Load balance bar + Settings gear button.
  - **Right Dock & Custom Canvas Icons**:
    - **Feed**: Tilted fish-food shaker sprinkling flakes.
    - **Maintain**: Bubbly sponge + sparkle star.
    - **Catalog**: Open field-guide book.
  - **Slide-Out Maintain Drawer**: Aqua-Glass panel containing *Water Change* (bucket icon), *Clean Glass* (squeegee icon), and *Check Water* (test-tube icon).
- [x] **Zero-Asset Procedural Audio & Haptics**:
  - Memory-synthesized PCM `AudioTrack` sounds (`playGlassTap`, `playBubblePop`, `playMaintenanceSwoosh`, `playRewardChime`).
  - Haptic feedback on glass taps, startled fish, and tank unlocks.
- [x] **Catalog & Settings Modals**:
  - `CatalogSheet.kt` tabbed bottom sheet for Tank Progression and Fish Store.
  - `SettingsDialog.kt` with Sound, Haptic, Debug FPS toggles, and Save Reset confirmation.
- [x] **Unit Testing Suite**:
  - All 12 unit tests passing across test suite.
