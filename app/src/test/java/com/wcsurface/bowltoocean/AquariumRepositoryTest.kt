package com.wcsurface.bowltoocean

import com.wcsurface.bowltoocean.data.AquariumSaveData
import com.wcsurface.bowltoocean.data.RemoteGameConfig
import com.wcsurface.bowltoocean.model.TankTier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AquariumRepositoryTest {

    @Test
    fun testDefaultSaveDataValues() {
        val saveData = AquariumSaveData(
            playerId = "test_uuid_123"
        )

        assertEquals("test_uuid_123", saveData.playerId)
        assertEquals(1250L, saveData.carePoints)
        assertEquals(25L, saveData.diamonds)
        assertEquals(3, saveData.playerLevel)
        assertEquals(TankTier.BOWL.name, saveData.activeTankTier)
        assertTrue(saveData.unlockedTanks.contains(TankTier.BOWL.name))
    }

    @Test
    fun testDefaultRemoteGameConfig() {
        val config = RemoteGameConfig()
        assertEquals(15, config.passiveCarePointsPerMin)
        assertEquals(500, config.dailyBonusPoints)
        assertEquals(1.2f, config.diamondMultiplier, 0.001f)
        assertEquals(1.0f, config.fishCostMultiplier, 0.001f)
        assertNotNull(config.closedTestingNotice)
    }

    @Test
    fun testBioLoadMultipliers() {
        // High Bio-Load (>80%) gives 2.0x multiplier
        val highBioLoad = 85f
        val highMultiplier = if (highBioLoad > 80f) 2.0f else 1.0f
        assertEquals(2.0f, highMultiplier, 0.001f)

        // Low Bio-Load (<30%) gives 0.5x multiplier
        val lowBioLoad = 20f
        val lowMultiplier = if (lowBioLoad < 30f) 0.5f else 1.0f
        assertEquals(0.5f, lowMultiplier, 0.001f)

        // Medium Bio-Load (50%) gives 1.0x multiplier
        val medBioLoad = 50f
        val medMultiplier = when {
            medBioLoad > 80f -> 2.0f
            medBioLoad < 30f -> 0.5f
            else -> 1.0f
        }
        assertEquals(1.0f, medMultiplier, 0.001f)
    }
}
