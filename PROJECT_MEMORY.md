# PROJECT_MEMORY.md - System Context & Architectural Memory

## Project Core Identity
- **App Name**: Bowl to Ocean Aquarium Sim
- **Package Name**: `com.wcsurface.bowltoocean`
- **Target Platform**: Android (Min SDK 26, Target/Compile SDK 37)
- **Deployment Target**: Google Play Closed Testing

---

## Architectural Choices & Key Decisions

### 1. Rendering Engine: `Canvas.drawVertices()`
Instead of sprite sheets or static image rotations, fish movement uses 2D mesh skinning via `Canvas.drawVertices()` in Jetpack Compose:
- Mesh Grid: A $M \times N$ vertex grid generated over the fish texture's normalized bounds.
- Deformation Formula: $x' = x + \sin(\text{phase} + x \cdot \text{freq}) \cdot \text{amp} \cdot (x / \text{width})^2$
- This creates realistic wave-like fish body flexing (head anchored, tail swinging dynamically).

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
