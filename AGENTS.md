# AGENTS.md - Developer & AI Agent Guidelines

## Project Overview
**Bowl to Ocean Aquarium Sim** is an Android aquarium simulation and progression game built natively using **100% Jetpack Compose** and hardware-accelerated **`Canvas.drawVertices()`** 2D mesh skinning for fluid fish animations. The game is targeted for release on Google Play Closed Testing.

---

## Architectural Principles & Strict Constraints

1. **Pure Native Tech Stack**:
   - **UI & Simulation Rendering**: 100% Jetpack Compose (`Canvas`, `drawVertices()`, `Modifier.drawWithCache()`).
   - **No External Game Engines**: Absolute prohibition on heavy game engines (e.g., Unity, Godot, LibGDX). All physics, swimming sine-wave mesh deformers, and rendering run on native Compose Canvas pipelines.
   - **Persistence**: `androidx.datastore:datastore-preferences` for lightweight, non-blocking asynchronous state serialization.
   - **Architecture Pattern**: Unidirectional Data Flow (UDF) with `ViewModel`, `StateFlow`, and Compose state.

2. **2D Mesh Skinning & Mesh Deformation**:
   - Swimming animation is achieved by deforming a 2D quad mesh (triangulated grid) across `Canvas.drawVertices()` using sine wave mathematical functions (`waveFrequency`, `waveAmplitude`, `swimPhase`).
   - Flip transitions smoothly interpolate vertex scale/x-coordinates when fish turn left or right (`flipProgress` from -1.0f to 1.0f).

3. **Asset Fallback Resilience**:
   - All graphical assets (tank backgrounds, fish species textures) are resolved through `AssetResolver.kt`.
   - If a PNG drawable is missing from `res/drawable-nodpi/` or `res/drawable/`, `AssetResolver` renders a high-contrast procedural `ImageBitmap` dynamically so the app always compiles and runs cleanly without crashing.

4. **Progression System**:
   - 7 Progression Tank Tiers (`BOWL` to `MEGA_TANK`).
   - 14 distinct fish species unlocked across the tiers.
   - Unique tier-specific Maintenance Actions that generate income/points and maintain ecosystem health.

---

## Code Quality & Conventions

- **Language**: 100% Kotlin with idiomatic coroutine and state management.
- **Null Safety**: Avoid force non-null assertions (`!!`). Use safe calls (`?.`) or default fallbacks.
- **UI Responsiveness**: Never run heavy physics, mesh computations, or DataStore reads on the Main Thread. Ensure 60fps / 120fps smooth animation frame loops using Compose frame clock (`withFrameNanos` / `LaunchedEffect`).
