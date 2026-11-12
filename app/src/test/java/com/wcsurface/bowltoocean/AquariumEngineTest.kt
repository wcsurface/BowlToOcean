package com.wcsurface.bowltoocean

import com.wcsurface.bowltoocean.engine.AquariumEngine
import com.wcsurface.bowltoocean.model.AiState
import com.wcsurface.bowltoocean.model.TankTier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AquariumEngineTest {

    @Test
    fun testEngineInitialization() {
        val engine = AquariumEngine(TankTier.BOWL)
        assertEquals(TankTier.BOWL, engine.currentTankTier)
        assertEquals(1, engine.fishList.size)
        assertEquals(18, engine.bubbles.size)
        assertTrue(engine.isHudVisible)
        assertEquals(1250L, engine.carePoints)
        assertEquals(25L, engine.diamonds)
    }

    @Test
    fun testGlassTapInteraction() {
        val engine = AquariumEngine(TankTier.TANK_5G)

        // Tap at center (500, 500)
        engine.onGlassTap(
            tapXPx = 500f,
            tapYPx = 500f,
            viewportWidth = 1000f,
            viewportHeight = 1000f,
            tapRadiusPx = 250f
        )

        // 1. Concentric ripple spawned
        assertEquals(1, engine.glassRipples.size)
        assertEquals(500f, engine.glassRipples[0].x)
        assertEquals(500f, engine.glassRipples[0].y)

        // 2. HUD view toggled
        assertFalse(engine.isHudVisible)

        // 3. Fish startled if close enough
        val fish = engine.fishList[0]
        fish.x = 0.52f
        fish.y = 0.52f

        engine.onGlassTap(
            tapXPx = 500f,
            tapYPx = 500f,
            viewportWidth = 1000f,
            viewportHeight = 1000f,
            tapRadiusPx = 250f
        )

        assertEquals(AiState.STARTLED, fish.aiState)
        assertEquals(0.65f, fish.stateTimer)
    }

    @Test
    fun testFoodPelletDroppingAndSeeking() {
        val engine = AquariumEngine(TankTier.TANK_10G)
        val fish = engine.fishList[0]
        fish.x = 0.5f
        fish.y = 0.5f
        fish.aiState = AiState.CRUISING

        // Drop food pellet near fish
        engine.dropFoodPellet(0.52f, 0.52f)

        assertEquals(1, engine.foodPellets.size)
        assertEquals(AiState.SEEKING_FOOD, fish.aiState)

        // Update simulation frame
        engine.update(0.016f, 1000f, 1000f)

        // Fish targets pellet coordinates
        assertEquals(0.52f, fish.targetX, 0.001f)
        assertEquals(0.52f, fish.targetY, 0.01f)
    }

    @Test
    fun testDeltaTimeClamping() {
        val engine = AquariumEngine(TankTier.BOWL)
        val fish = engine.fishList[0]
        val initialPhase = fish.swimPhase

        // Pass huge deltaTime (e.g., 2.0s during lag spike)
        engine.update(2.0f, 1000f, 1000f)

        // Verify phase updated according to 0.05f max clamped dt
        assertTrue(fish.swimPhase > initialPhase)
        // Position should remain safely bounded inside margins
        assertTrue(fish.x in 0.08f..0.92f)
        assertTrue(fish.y in 0.12f..0.82f)
    }
}
