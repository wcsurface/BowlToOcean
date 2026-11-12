package com.wcsurface.bowltoocean

import com.wcsurface.bowltoocean.model.AiState
import com.wcsurface.bowltoocean.model.FishInstance
import com.wcsurface.bowltoocean.model.FishSpecies
import com.wcsurface.bowltoocean.model.TankTier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AquariumModelsTest {

    @Test
    fun testTankTiersCompleteness() {
        val tiers = TankTier.entries
        assertEquals(7, tiers.size)

        for (tier in tiers) {
            assertTrue("Tier name should not be blank", tier.tierName.isNotBlank())
            assertTrue("Capacity should be positive", tier.capacity > 0)
            assertTrue("Max fish should be positive", tier.maxFish > 0)
            assertEquals("Each tier must have exactly 3 maintenance actions", 3, tier.maintenanceActions.size)
        }

        assertEquals(TankTier.BOWL, tiers[0])
        assertEquals("Desktop Bowl", TankTier.BOWL.tierName)
        assertEquals(0L, TankTier.BOWL.cost)
        assertEquals(1, TankTier.BOWL.maxFish)
        assertEquals(10, TankTier.BOWL.capacity)

        assertEquals(TankTier.MEGA_TANK, tiers[6])
        assertEquals("Oceanic Mega-Tank", TankTier.MEGA_TANK.tierName)
        assertEquals(250000L, TankTier.MEGA_TANK.cost)
        assertEquals(80, TankTier.MEGA_TANK.maxFish)
        assertEquals(1000, TankTier.MEGA_TANK.capacity)
    }

    @Test
    fun testFishSpeciesCompleteness() {
        val speciesList = FishSpecies.ALL_SPECIES
        assertEquals(14, speciesList.size)

        val speciesPerTier = speciesList.groupBy { it.tierRequired }
        assertEquals(7, speciesPerTier.keys.size)

        for (tier in TankTier.entries) {
            val count = speciesPerTier[tier]?.size ?: 0
            assertEquals("Tier ${tier.name} should have exactly 2 species", 2, count)
        }

        val betta = FishSpecies.getSpeciesById("betta_blue")
        assertNotNull(betta)
        assertEquals("Royal Blue Halfmoon Betta", betta?.name)
        assertEquals(TankTier.BOWL, betta?.tierRequired)

        val hammerhead = FishSpecies.getSpeciesById("shark_hammerhead")
        assertNotNull(hammerhead)
        assertEquals("Scalloped Hammerhead", hammerhead?.name)
        assertEquals(TankTier.MEGA_TANK, hammerhead?.tierRequired)

        val manta = FishSpecies.getSpeciesById("manta_ray")
        assertNotNull(manta)
        assertEquals("Giant Manta Ray", manta?.name)
        assertEquals(TankTier.MEGA_TANK, manta?.tierRequired)
    }

    @Test
    fun testFishInstanceCreation() {
        val fish = FishInstance(
            instanceId = "fish_1",
            speciesId = "betta_blue",
            x = 0.5f,
            y = 0.5f,
            aiState = AiState.CRUISING
        )

        assertEquals("fish_1", fish.instanceId)
        assertEquals("betta_blue", fish.speciesId)
        assertEquals(0.5f, fish.x)
        assertEquals(0.5f, fish.y)
        assertEquals(AiState.CRUISING, fish.aiState)
        assertTrue(fish.facingRight)
    }
}
