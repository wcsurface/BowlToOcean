package com.wcsurface.bowltoocean.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.wcsurface.bowltoocean.R

/**
 * Maintenance actions specific to each tank tier.
 */
data class MaintenanceAction(
    val id: String,
    val name: String,
    val cost: Long,
    val cleanBonus: Float = 25f,
    val rewardPoints: Long = 10L,
    val cooldownSeconds: Int = 30
)

/**
 * The 7 progression tank tiers from Desktop Bowl to Oceanic Mega-Tank.
 */
enum class TankTier(
    val tierName: String,
    val cost: Long,
    val maxFish: Int,
    val capacity: Int,
    val bgDrawableResName: String,
    @DrawableRes val backgroundResId: Int,
    val ambientTint: Color,
    val maintenanceActions: List<MaintenanceAction>
) {
    BOWL(
        tierName = "Desktop Bowl",
        cost = 0L,
        maxFish = 1,
        capacity = 10,
        bgDrawableResName = "bg_tank_1_bowl",
        backgroundResId = R.drawable.bg_tank_1_bowl,
        ambientTint = Color(0xFF40A0E0),
        maintenanceActions = listOf(
            MaintenanceAction("bowl_wc", "Water Change", cost = 0L, cleanBonus = 35f, rewardPoints = 15L, cooldownSeconds = 15),
            MaintenanceAction("bowl_cg", "Clean Glass", cost = 10L, cleanBonus = 25f, rewardPoints = 20L, cooldownSeconds = 20),
            MaintenanceAction("bowl_cw", "Check Water", cost = 15L, cleanBonus = 15f, rewardPoints = 25L, cooldownSeconds = 30)
        )
    ),
    TANK_5G(
        tierName = "5-Gal Planted",
        cost = 250L,
        maxFish = 4,
        capacity = 30,
        bgDrawableResName = "bg_tank_2_5g",
        backgroundResId = R.drawable.bg_tank_1_bowl,
        ambientTint = Color(0xFF38B0A0),
        maintenanceActions = listOf(
            MaintenanceAction("5g_tp", "Trim Plants", cost = 25L, cleanBonus = 30f, rewardPoints = 50L, cooldownSeconds = 25),
            MaintenanceAction("5g_ss", "Syphon Substrate", cost = 40L, cleanBonus = 40f, rewardPoints = 75L, cooldownSeconds = 35),
            MaintenanceAction("5g_tn", "Test Nitrates", cost = 50L, cleanBonus = 20f, rewardPoints = 100L, cooldownSeconds = 45)
        )
    ),
    TANK_10G(
        tierName = "10-Gal Community",
        cost = 1000L,
        maxFish = 8,
        capacity = 60,
        bgDrawableResName = "bg_tank_3_10g",
        backgroundResId = R.drawable.bg_tank_1_bowl,
        ambientTint = Color(0xFF30B0C0),
        maintenanceActions = listOf(
            MaintenanceAction("10g_fs", "Filter Sponge Rinse", cost = 100L, cleanBonus = 45f, rewardPoints = 200L, cooldownSeconds = 40),
            MaintenanceAction("10g_as", "Algae Scrape", cost = 120L, cleanBonus = 35f, rewardPoints = 250L, cooldownSeconds = 45),
            MaintenanceAction("10g_bb", "Dosing Bio-Boost", cost = 150L, cleanBonus = 25f, rewardPoints = 350L, cooldownSeconds = 60)
        )
    ),
    TANK_40G(
        tierName = "40-Gal Breeder",
        cost = 4500L,
        maxFish = 15,
        capacity = 120,
        bgDrawableResName = "bg_tank_4_40g",
        backgroundResId = R.drawable.bg_tank_1_bowl,
        ambientTint = Color(0xFF2888B0),
        maintenanceActions = listOf(
            MaintenanceAction("40g_ws", "Water Scrub", cost = 300L, cleanBonus = 50f, rewardPoints = 600L, cooldownSeconds = 60),
            MaintenanceAction("40g_cf", "Canister Flush", cost = 450L, cleanBonus = 60f, rewardPoints = 900L, cooldownSeconds = 75),
            MaintenanceAction("40g_to", "Water Top-Off", cost = 500L, cleanBonus = 30f, rewardPoints = 1200L, cooldownSeconds = 90)
        )
    ),
    TANK_100G(
        tierName = "100-Gal Reef",
        cost = 18000L,
        maxFish = 25,
        capacity = 250,
        bgDrawableResName = "bg_tank_5_100g",
        backgroundResId = R.drawable.bg_tank_1_bowl,
        ambientTint = Color(0xFF2060A0),
        maintenanceActions = listOf(
            MaintenanceAction("100g_ps", "Protein Skimmer Clean", cost = 1200L, cleanBonus = 55f, rewardPoints = 2500L, cooldownSeconds = 90),
            MaintenanceAction("100g_dc", "Dosing Calcium", cost = 1800L, cleanBonus = 35f, rewardPoints = 3500L, cooldownSeconds = 120),
            MaintenanceAction("100g_cd", "Coral Dip", cost = 2500L, cleanBonus = 45f, rewardPoints = 5000L, cooldownSeconds = 150)
        )
    ),
    LARGE_DISPLAY(
        tierName = "Gallery Exhibit",
        cost = 75000L,
        maxFish = 45,
        capacity = 500,
        bgDrawableResName = "bg_tank_6_large",
        backgroundResId = R.drawable.bg_tank_1_bowl,
        ambientTint = Color(0xFF184888),
        maintenanceActions = listOf(
            MaintenanceAction("large_ab", "Automated Backwash", cost = 5000L, cleanBonus = 65f, rewardPoints = 12000L, cooldownSeconds = 150),
            MaintenanceAction("large_ap", "Acrylic Polish", cost = 7500L, cleanBonus = 50f, rewardPoints = 18000L, cooldownSeconds = 180),
            MaintenanceAction("large_lt", "Water Lab Test", cost = 10000L, cleanBonus = 40f, rewardPoints = 25000L, cooldownSeconds = 240)
        )
    ),
    MEGA_TANK(
        tierName = "Oceanic Mega-Tank",
        cost = 250000L,
        maxFish = 80,
        capacity = 1000,
        bgDrawableResName = "bg_tank_7_mega",
        backgroundResId = R.drawable.bg_tank_1_bowl,
        ambientTint = Color(0xFF102868),
        maintenanceActions = listOf(
            MaintenanceAction("mega_dc", "Dive Crew Scrub", cost = 15000L, cleanBonus = 75f, rewardPoints = 40000L, cooldownSeconds = 200),
            MaintenanceAction("mega_is", "Industrial Skimmer", cost = 25000L, cleanBonus = 85f, rewardPoints = 65000L, cooldownSeconds = 300),
            MaintenanceAction("mega_sb", "Sonar Bio-Scan", cost = 40000L, cleanBonus = 60f, rewardPoints = 100000L, cooldownSeconds = 360)
        )
    )
}

/**
 * Data model defining a species of fish in the game.
 */
data class FishSpecies(
    val id: String,
    val name: String,
    val tierRequired: TankTier,
    val cost: Long,
    val basePointsPerMin: Int,
    val bioLoadWeight: Int,
    val scale: Float,
    val swimSpeed: Float,
    val waveFrequency: Float,
    val waveAmplitude: Float,
    val isSchooling: Boolean,
    val drawableResName: String,
    @DrawableRes val drawableResId: Int = R.drawable.fish_betta_blue
) {
    companion object {
        val ALL_SPECIES: List<FishSpecies> = listOf(
            // Bowl Tier
            FishSpecies(
                id = "betta_blue",
                name = "Royal Blue Halfmoon Betta",
                tierRequired = TankTier.BOWL,
                cost = 50L,
                basePointsPerMin = 12,
                bioLoadWeight = 1,
                scale = 1.8f,
                swimSpeed = 1.2f,
                waveFrequency = 2.5f,
                waveAmplitude = 12.0f,
                isSchooling = false,
                drawableResName = "fish_betta_blue",
                drawableResId = R.drawable.fish_betta_blue
            ),
            FishSpecies(
                id = "guppy_fancy",
                name = "Fancy Neon Guppy",
                tierRequired = TankTier.BOWL,
                cost = 75L,
                basePointsPerMin = 18,
                bioLoadWeight = 1,
                scale = 0.7f,
                swimSpeed = 1.8f,
                waveFrequency = 4.0f,
                waveAmplitude = 8.0f,
                isSchooling = true,
                drawableResName = "fish_guppy_fancy",
                drawableResId = R.drawable.fish_betta_blue
            ),
            // 5G Planted Tier
            FishSpecies(
                id = "tetra_neon",
                name = "Neon Tetra",
                tierRequired = TankTier.TANK_5G,
                cost = 120L,
                basePointsPerMin = 25,
                bioLoadWeight = 2,
                scale = 0.6f,
                swimSpeed = 2.2f,
                waveFrequency = 4.5f,
                waveAmplitude = 7.0f,
                isSchooling = true,
                drawableResName = "fish_tetra_neon",
                drawableResId = R.drawable.fish_betta_blue
            ),
            FishSpecies(
                id = "cory_panda",
                name = "Panda Corydoras",
                tierRequired = TankTier.TANK_5G,
                cost = 180L,
                basePointsPerMin = 35,
                bioLoadWeight = 2,
                scale = 0.8f,
                swimSpeed = 1.4f,
                waveFrequency = 3.0f,
                waveAmplitude = 9.0f,
                isSchooling = false,
                drawableResName = "fish_cory_panda",
                drawableResId = R.drawable.fish_betta_blue
            ),
            // 10G Community Tier
            FishSpecies(
                id = "angelfish_marble",
                name = "Marble Angelfish",
                tierRequired = TankTier.TANK_10G,
                cost = 400L,
                basePointsPerMin = 75,
                bioLoadWeight = 4,
                scale = 1.2f,
                swimSpeed = 1.1f,
                waveFrequency = 2.0f,
                waveAmplitude = 10.0f,
                isSchooling = false,
                drawableResName = "fish_angelfish_marble",
                drawableResId = R.drawable.fish_betta_blue
            ),
            FishSpecies(
                id = "ram_german_blue",
                name = "German Blue Ram",
                tierRequired = TankTier.TANK_10G,
                cost = 650L,
                basePointsPerMin = 110,
                bioLoadWeight = 3,
                scale = 0.9f,
                swimSpeed = 1.6f,
                waveFrequency = 3.2f,
                waveAmplitude = 8.5f,
                isSchooling = false,
                drawableResName = "fish_ram_german_blue",
                drawableResId = R.drawable.fish_betta_blue
            ),
            // 40G Breeder Tier
            FishSpecies(
                id = "discus_red",
                name = "Red Turquoise Discus",
                tierRequired = TankTier.TANK_40G,
                cost = 1800L,
                basePointsPerMin = 280,
                bioLoadWeight = 6,
                scale = 1.4f,
                swimSpeed = 1.0f,
                waveFrequency = 1.8f,
                waveAmplitude = 11.0f,
                isSchooling = true,
                drawableResName = "fish_discus_red",
                drawableResId = R.drawable.fish_betta_blue
            ),
            FishSpecies(
                id = "pleco_bristlenose",
                name = "Bristlenose Pleco",
                tierRequired = TankTier.TANK_40G,
                cost = 2200L,
                basePointsPerMin = 340,
                bioLoadWeight = 5,
                scale = 1.3f,
                swimSpeed = 0.9f,
                waveFrequency = 2.2f,
                waveAmplitude = 6.0f,
                isSchooling = false,
                drawableResName = "fish_pleco_bristlenose",
                drawableResId = R.drawable.fish_betta_blue
            ),
            // 100G Reef Tier
            FishSpecies(
                id = "tang_blue",
                name = "Blue Tang",
                tierRequired = TankTier.TANK_100G,
                cost = 6000L,
                basePointsPerMin = 900,
                bioLoadWeight = 8,
                scale = 1.3f,
                swimSpeed = 2.5f,
                waveFrequency = 3.8f,
                waveAmplitude = 10.0f,
                isSchooling = true,
                drawableResName = "fish_tang_blue",
                drawableResId = R.drawable.fish_betta_blue
            ),
            FishSpecies(
                id = "clownfish_percula",
                name = "Percula Clownfish",
                tierRequired = TankTier.TANK_100G,
                cost = 8500L,
                basePointsPerMin = 1250,
                bioLoadWeight = 7,
                scale = 1.0f,
                swimSpeed = 1.7f,
                waveFrequency = 3.5f,
                waveAmplitude = 9.0f,
                isSchooling = true,
                drawableResName = "fish_clownfish_percula",
                drawableResId = R.drawable.fish_betta_blue
            ),
            // Gallery Exhibit Tier
            FishSpecies(
                id = "catfish_redtail",
                name = "Redtail Catfish",
                tierRequired = TankTier.LARGE_DISPLAY,
                cost = 25000L,
                basePointsPerMin = 3800,
                bioLoadWeight = 18,
                scale = 2.2f,
                swimSpeed = 1.3f,
                waveFrequency = 1.5f,
                waveAmplitude = 14.0f,
                isSchooling = false,
                drawableResName = "fish_catfish_redtail",
                drawableResId = R.drawable.fish_betta_blue
            ),
            FishSpecies(
                id = "arowana_silver",
                name = "Silver Arowana",
                tierRequired = TankTier.LARGE_DISPLAY,
                cost = 38000L,
                basePointsPerMin = 5500,
                bioLoadWeight = 15,
                scale = 2.5f,
                swimSpeed = 2.0f,
                waveFrequency = 1.8f,
                waveAmplitude = 16.0f,
                isSchooling = false,
                drawableResName = "fish_arowana_silver",
                drawableResId = R.drawable.fish_betta_blue
            ),
            // Oceanic Mega-Tank Tier
            FishSpecies(
                id = "shark_hammerhead",
                name = "Scalloped Hammerhead",
                tierRequired = TankTier.MEGA_TANK,
                cost = 100000L,
                basePointsPerMin = 15000,
                bioLoadWeight = 35,
                scale = 3.2f,
                swimSpeed = 2.8f,
                waveFrequency = 1.6f,
                waveAmplitude = 20.0f,
                isSchooling = true,
                drawableResName = "fish_shark_hammerhead",
                drawableResId = R.drawable.fish_betta_blue
            ),
            FishSpecies(
                id = "manta_ray",
                name = "Giant Manta Ray",
                tierRequired = TankTier.MEGA_TANK,
                cost = 180000L,
                basePointsPerMin = 28000,
                bioLoadWeight = 45,
                scale = 4.0f,
                swimSpeed = 1.5f,
                waveFrequency = 1.0f,
                waveAmplitude = 25.0f,
                isSchooling = false,
                drawableResName = "fish_manta_ray",
                drawableResId = R.drawable.fish_betta_blue
            )
        )

        fun getSpeciesById(id: String): FishSpecies? {
            return ALL_SPECIES.find { it.id == id }
        }
    }
}

/**
 * AI state enum for real-time behavior.
 */
enum class AiState {
    CRUISING,
    STARTLED,
    CURIOUS,
    SEEKING_FOOD
}

/**
 * Active runtime instance of a fish inside a tank.
 */
data class FishInstance(
    val instanceId: String,
    val speciesId: String,
    var x: Float,
    var y: Float,
    var vx: Float = 0f,
    var vy: Float = 0f,
    var facingRight: Boolean = true,
    var flipProgress: Float = 1f, // Interpolates between -1f (facing left) and 1f (facing right)
    var swimPhase: Float = 0f,
    var aiState: AiState = AiState.CRUISING,
    var stateTimer: Float = 0f,
    var targetX: Float = 0.5f,
    var targetY: Float = 0.5f
)
