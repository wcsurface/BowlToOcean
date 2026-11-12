# REQUIREMENTS.md - Specification & Game Design Document

## 1. Tank Tiers Specification

| Tier Enum | Display Name | Cost | Max Fish | Water Capacity (Gal) | Background Drawable Name | Ambient Tint Color | Maintenance Actions |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `BOWL` | Desktop Bowl | 0 | 1 | 10 | `bg_tank_1_bowl` | `#40A0E0` | Water Change, Clean Glass, Check Water |
| `TANK_5G` | 5-Gal Planted | 250 | 4 | 30 | `bg_tank_2_5g` | `#38B0A0` | Trim Plants, Syphon Substrate, Test Nitrates |
| `TANK_10G` | 10-Gal Community | 1,000 | 8 | 60 | `bg_tank_3_10g` | `#30B0C0` | Filter Sponge Rinse, Algae Scrape, Dosing Bio-Boost |
| `TANK_40G` | 40-Gal Breeder | 4,500 | 15 | 120 | `bg_tank_4_40g` | `#2888B0` | Water Scrub, Canister Flush, Water Top-Off |
| `TANK_100G` | 100-Gal Reef | 18,000 | 25 | 250 | `bg_tank_5_100g` | `#2060A0` | Protein Skimmer Clean, Dosing Calcium, Coral Dip |
| `LARGE_DISPLAY` | Gallery Exhibit | 75,000 | 45 | 500 | `bg_tank_6_large` | `#184888` | Automated Backwash, Acrylic Polish, Water Lab Test |
| `MEGA_TANK` | Oceanic Mega-Tank | 250,000 | 80 | 1,000 | `bg_tank_7_mega` | `#102868` | Dive Crew Scrub, Industrial Skimmer, Sonar Bio-Scan |

---

## 2. Fish Species Specification (14 Species)

| ID | Name | Tier Required | Purchase Cost | Base Points/Min | BioLoad Weight | Scale | Swim Speed | Wave Freq | Wave Amp | Schooling | Drawable Name |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `betta_blue` | Royal Blue Halfmoon Betta | `BOWL` | 50 | 12 | 1 | 1.0f | 1.2f | 2.5f | 12.0f | No | `fish_betta_blue` |
| `guppy_fancy` | Fancy Neon Guppy | `BOWL` | 75 | 18 | 1 | 0.7f | 1.8f | 4.0f | 8.0f | Yes | `fish_guppy_fancy` |
| `tetra_neon` | Neon Tetra | `TANK_5G` | 120 | 25 | 2 | 0.6f | 2.2f | 4.5f | 7.0f | Yes | `fish_tetra_neon` |
| `cory_panda` | Panda Corydoras | `TANK_5G` | 180 | 35 | 2 | 0.8f | 1.4f | 3.0f | 9.0f | No | `fish_cory_panda` |
| `angelfish_marble` | Marble Angelfish | `TANK_10G` | 400 | 75 | 4 | 1.2f | 1.1f | 2.0f | 10.0f | No | `fish_angelfish_marble` |
| `ram_german_blue` | German Blue Ram | `TANK_10G` | 650 | 110 | 3 | 0.9f | 1.6f | 3.2f | 8.5f | No | `fish_ram_german_blue` |
| `discus_red` | Red Turquoise Discus | `TANK_40G` | 1,800 | 280 | 6 | 1.4f | 1.0f | 1.8f | 11.0f | Yes | `fish_discus_red` |
| `pleco_bristlenose` | Bristlenose Pleco | `TANK_40G` | 2,200 | 340 | 5 | 1.3f | 0.9f | 2.2f | 6.0f | No | `fish_pleco_bristlenose` |
| `tang_blue` | Blue Tang | `TANK_100G` | 6,000 | 900 | 8 | 1.3f | 2.5f | 3.8f | 10.0f | Yes | `fish_tang_blue` |
| `clownfish_percula` | Percula Clownfish | `TANK_100G` | 8,500 | 1,250 | 7 | 1.0f | 1.7f | 3.5f | 9.0f | Yes | `fish_clownfish_percula` |
| `catfish_redtail` | Redtail Catfish | `LARGE_DISPLAY` | 25,000 | 3,800 | 18 | 2.2f | 1.3f | 1.5f | 14.0f | No | `fish_catfish_redtail` |
| `arowana_silver` | Silver Arowana | `LARGE_DISPLAY` | 38,000 | 5,500 | 15 | 2.5f | 2.0f | 1.8f | 16.0f | No | `fish_arowana_silver` |
| `shark_hammerhead` | Scalloped Hammerhead | `MEGA_TANK` | 100,000 | 15,000 | 35 | 3.2f | 2.8f | 1.6f | 20.0f | Yes | `fish_shark_hammerhead` |
| `manta_ray` | Giant Manta Ray | `MEGA_TANK` | 180,000 | 28,000 | 45 | 4.0f | 1.5f | 1.0f | 25.0f | No | `fish_manta_ray` |

---

## 3. Fish Instance State Machine
- `x`, `y`: Position coordinates normalized relative to tank viewport $(0.0 \dots 1.0)$.
- `vx`, `vy`: Velocity components.
- `facingRight`: Boolean direction flag.
- `flipProgress`: Float interpolated between $-1.0f$ (facing left) and $1.0f$ (facing right) for smooth turnarounds.
- `swimPhase`: Radians accumulator for tail sine wave flex.
- `aiState`:
  - `CRUISING`: Gentle random wandering towards targets.
  - `STARTLED`: High-speed burst away from user tap/touch.
  - `CURIOUS`: Slow approach towards user touch or new placed decorations.
  - `SEEKING_FOOD`: Direct swim path towards dropped fish food flakes.
- `stateTimer`: Time remaining in current AI state.
- `targetX`, `targetY`: Current target destination inside the tank boundaries.

---

## 4. Asset Resolver Spec (`AssetResolver.kt`)
- Checks `context.resources.getIdentifier(resName, "drawable", context.packageName)`.
- If ID $> 0$, loads bitmap via `ImageBitmap.imageResource(id)`.
- If ID $== 0$ (file missing in `res/drawable-nodpi/` or `res/drawable/`), generates a high-contrast dynamic procedural `ImageBitmap`:
  - Backgrounds: Rich ocean/water gradients with bubble highlights and tank tier title overlay.
  - Fish: Distinct vibrant anatomical fish silhouettes with directional eye, caudal fin wave indicators, and species name label.
