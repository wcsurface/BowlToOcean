package com.wcsurface.bowltoocean.engine

import com.wcsurface.bowltoocean.audio.AquariumAudioManager
import com.wcsurface.bowltoocean.model.AiState
import com.wcsurface.bowltoocean.model.FishInstance
import com.wcsurface.bowltoocean.model.FishSpecies
import com.wcsurface.bowltoocean.model.MaintenanceAction
import com.wcsurface.bowltoocean.model.TankTier
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

data class FoodPellet(
    val id: String,
    var x: Float, // Normalized 0..1
    var y: Float, // Normalized 0..1
    var vy: Float = 0.12f,
    var wobblePhase: Float = Random.nextFloat() * 6.28f
)

data class Bubble(
    val id: String,
    var x: Float, // Normalized 0..1
    var y: Float, // Normalized 0..1
    var radius: Float = Random.nextFloat() * 6f + 3f, // dp / px
    var speed: Float = Random.nextFloat() * 0.15f + 0.08f,
    var wobblePhase: Float = Random.nextFloat() * 6.28f,
    var wobbleSpeed: Float = Random.nextFloat() * 2f + 1f
)

data class GlassRipple(
    val x: Float, // Pixels
    val y: Float, // Pixels
    var radius: Float = 10f,
    val maxRadius: Float = 240f,
    var alpha: Float = 1.0f
)

data class FloatingText(
    val id: String,
    val text: String,
    var x: Float, // Pixels
    var y: Float, // Pixels
    var alpha: Float = 1.0f,
    var lifetime: Float = 1.2f
)

class AquariumEngine(
    initialTankTier: TankTier = TankTier.BOWL
) {
    val audioManager = AquariumAudioManager()

    var soundEnabled: Boolean
        get() = audioManager.soundEnabled
        set(value) { audioManager.soundEnabled = value }

    var hapticsEnabled: Boolean
        get() = audioManager.hapticsEnabled
        set(value) { audioManager.hapticsEnabled = value }

    var showDebugFps: Boolean = false

    var currentTankTier: TankTier = initialTankTier
    val fishList: MutableList<FishInstance> = mutableListOf()
    val foodPellets: MutableList<FoodPellet> = mutableListOf()
    val bubbles: MutableList<Bubble> = mutableListOf()
    val glassRipples: MutableList<GlassRipple> = mutableListOf()
    val floatingTexts: MutableList<FloatingText> = mutableListOf()
    val unlockedTanks: MutableList<TankTier> = mutableListOf(TankTier.BOWL)

    var isHudVisible: Boolean = true
    var carePoints: Long = 1250L
    var diamonds: Long = 25L
    var playerLevel: Int = 3
    var playerXp: Int = 180
    var xpToNextLevel: Int = 300
    var bioLoadBalance: Float = 85f // 0% to 100%
    var dailyBonusClaimable: Boolean = true
    var feedCooldownTimer: Float = 0f

    var isMaintainDrawerOpen: Boolean = false
    var isShopSheetOpen: Boolean = false
    var isSettingsOpen: Boolean = false
    var causticsPhase: Float = 0f

    init {
        // Initialize ambient rising bubbles
        repeat(18) { index ->
            bubbles.add(
                Bubble(
                    id = "bubble_$index",
                    x = Random.nextFloat(),
                    y = Random.nextFloat()
                )
            )
        }
        // Spawn default fish for starting tank
        spawnDefaultFishForTier(initialTankTier)
    }

    fun resetDataToDefaults() {
        carePoints = 1250L
        diamonds = 25L
        playerLevel = 1
        playerXp = 0
        xpToNextLevel = 300
        bioLoadBalance = 85f
        unlockedTanks.clear()
        unlockedTanks.add(TankTier.BOWL)
        setTankTier(TankTier.BOWL)
    }

    fun ensureStarterFishActive() {
        if (fishList.isEmpty()) {
            val minX = if (currentTankTier == TankTier.BOWL) 0.25f else 0.08f
            val maxX = if (currentTankTier == TankTier.BOWL) 0.75f else 0.92f
            val minY = if (currentTankTier == TankTier.BOWL) 0.25f else 0.12f
            val maxY = if (currentTankTier == TankTier.BOWL) 0.72f else 0.82f

            fishList.add(
                FishInstance(
                    instanceId = "fish_betta_blue_starter",
                    speciesId = "betta_blue",
                    x = 0.50f,
                    y = 0.48f,
                    vx = 0.02f,
                    vy = 0.01f,
                    facingRight = true,
                    aiState = AiState.CRUISING,
                    targetX = 0.52f,
                    targetY = 0.48f
                )
            )
        }
    }

    fun spawnDefaultFishForTier(tier: TankTier) {
        fishList.clear()
        val availableSpecies = FishSpecies.ALL_SPECIES.filter { it.tierRequired <= tier }
        val count = tier.maxFish.coerceAtMost(availableSpecies.size)

        val minX = if (tier == TankTier.BOWL) 0.25f else 0.08f
        val maxX = if (tier == TankTier.BOWL) 0.75f else 0.92f
        val minY = if (tier == TankTier.BOWL) 0.25f else 0.12f
        val maxY = if (tier == TankTier.BOWL) 0.72f else 0.82f

        for (i in 0 until count) {
            val species = availableSpecies[i % availableSpecies.size]
            val initX = if (tier == TankTier.BOWL && i == 0) 0.5f else minX + Random.nextFloat() * (maxX - minX)
            val initY = if (tier == TankTier.BOWL && i == 0) 0.48f else minY + Random.nextFloat() * (maxY - minY)

            fishList.add(
                FishInstance(
                    instanceId = "fish_${species.id}_$i",
                    speciesId = species.id,
                    x = initX,
                    y = initY,
                    vx = (Random.nextFloat() - 0.5f) * 0.08f,
                    vy = (Random.nextFloat() - 0.5f) * 0.08f,
                    facingRight = Random.nextBoolean(),
                    aiState = AiState.CRUISING,
                    targetX = initX,
                    targetY = initY
                )
            )
        }
    }

    fun setTankTier(tier: TankTier) {
        if (currentTankTier != tier) {
            currentTankTier = tier
            if (!unlockedTanks.contains(tier)) {
                unlockedTanks.add(tier)
            }
            spawnDefaultFishForTier(tier)
            audioManager.playRewardChime()
        }
    }

    fun unlockAndSwitchTank(tier: TankTier): Boolean {
        if (unlockedTanks.contains(tier)) {
            setTankTier(tier)
            return true
        }
        if (carePoints >= tier.cost) {
            carePoints -= tier.cost
            unlockedTanks.add(tier)
            setTankTier(tier)
            audioManager.playRewardChime()
            return true
        }
        return false
    }

    fun addFish(speciesId: String): Boolean {
        if (fishList.size >= currentTankTier.maxFish) return false
        val species = FishSpecies.getSpeciesById(speciesId) ?: return false
        if (carePoints < species.cost) return false

        carePoints -= species.cost
        fishList.add(
            FishInstance(
                instanceId = "fish_${species.id}_${System.currentTimeMillis()}",
                speciesId = species.id,
                x = 0.5f,
                y = 0.5f,
                facingRight = true,
                aiState = AiState.CRUISING
            )
        )
        audioManager.playBubblePop()
        return true
    }

    fun claimDailyBonus(viewportWidth: Float = 1080f, viewportHeight: Float = 1920f) {
        if (!dailyBonusClaimable) return
        dailyBonusClaimable = false
        carePoints += 250L
        diamonds += 5L

        audioManager.playRewardChime()

        floatingTexts.add(
            FloatingText(
                id = "txt_daily_${System.currentTimeMillis()}",
                text = "+250 Care Points & +5 Diamonds!",
                x = viewportWidth * 0.5f,
                y = viewportHeight * 0.35f
            )
        )
    }

    fun triggerFeedAction(viewportWidth: Float = 1080f, viewportHeight: Float = 1920f) {
        if (feedCooldownTimer > 0f) return
        feedCooldownTimer = 2.0f // 2 second cooldown

        audioManager.playBubblePop()

        // Drop 3 food pellets along top water surface
        dropFoodPellet(0.35f, 0.12f)
        dropFoodPellet(0.50f, 0.10f)
        dropFoodPellet(0.65f, 0.14f)

        bioLoadBalance = (bioLoadBalance - 1.5f).coerceAtLeast(0f)

        floatingTexts.add(
            FloatingText(
                id = "txt_feed_${System.currentTimeMillis()}",
                text = "Food Dropped!",
                x = viewportWidth * 0.5f,
                y = viewportHeight * 0.25f
            )
        )
    }

    fun performMaintenanceAction(
        action: MaintenanceAction,
        viewportWidth: Float = 1080f,
        viewportHeight: Float = 1920f
    ): Boolean {
        if (carePoints < action.cost) return false

        carePoints -= action.cost
        carePoints += action.rewardPoints
        playerXp += 35

        audioManager.playMaintenanceSwoosh()

        if (playerXp >= xpToNextLevel) {
            playerLevel += 1
            playerXp -= xpToNextLevel
            xpToNextLevel += 150
            audioManager.playRewardChime()
        }

        bioLoadBalance = (bioLoadBalance + action.cleanBonus).coerceAtMost(100f)

        // Spawn clean sweep particle animation lines
        glassRipples.add(GlassRipple(x = viewportWidth * 0.3f, y = viewportHeight * 0.4f))
        glassRipples.add(GlassRipple(x = viewportWidth * 0.5f, y = viewportHeight * 0.5f))
        glassRipples.add(GlassRipple(x = viewportWidth * 0.7f, y = viewportHeight * 0.6f))

        floatingTexts.add(
            FloatingText(
                id = "txt_maint_${System.currentTimeMillis()}",
                text = "${action.name}: +${action.rewardPoints} Care Points!",
                x = viewportWidth * 0.5f,
                y = viewportHeight * 0.45f
            )
        )
        return true
    }

    fun dropFoodPellet(normX: Float, normY: Float) {
        foodPellets.add(
            FoodPellet(
                id = "food_${System.currentTimeMillis()}_${Random.nextInt(1000)}",
                x = normX,
                y = normY
            )
        )
        // Alert nearby cruising fish to seek food
        for (fish in fishList) {
            if (fish.aiState == AiState.CRUISING) {
                val dx = normX - fish.x
                val dy = normY - fish.y
                val dist = sqrt(dx * dx + dy * dy)
                if (dist < 0.45f) {
                    fish.aiState = AiState.SEEKING_FOOD
                }
            }
        }
    }

    fun onGlassTap(
        tapXPx: Float,
        tapYPx: Float,
        viewportWidth: Float,
        viewportHeight: Float,
        tapRadiusPx: Float = 250f
    ) {
        // Play low-mid underwater glass tap thud
        audioManager.playGlassTap()

        // 1. Spawn concentric glass ripple
        glassRipples.add(GlassRipple(x = tapXPx, y = tapYPx))

        // 2. Toggle clean-view HUD overlay
        isHudVisible = !isHudVisible

        // Close side drawers when entering clean view
        if (!isHudVisible) {
            isMaintainDrawerOpen = false
        }

        // 3. Startle nearby fish within tap radius
        val normTapX = if (viewportWidth > 0f) tapXPx / viewportWidth else 0.5f
        val normTapY = if (viewportHeight > 0f) tapYPx / viewportHeight else 0.5f
        val normRadiusX = if (viewportWidth > 0f) tapRadiusPx / viewportWidth else 0.2f

        for (fish in fishList) {
            val dx = fish.x - normTapX
            val dy = fish.y - normTapY
            val dist = sqrt(dx * dx + dy * dy)

            if (dist < normRadiusX || dist < 0.3f) {
                // Dart away from tap point
                fish.aiState = AiState.STARTLED
                fish.stateTimer = 0.65f // 0.65s startled burst

                val angle = if (dist > 0.001f) atan2(dy, dx) else (Random.nextFloat() * 6.28f)
                val fleeSpeed = 0.45f
                fish.vx = cos(angle) * fleeSpeed
                fish.vy = sin(angle) * fleeSpeed
                fish.facingRight = fish.vx > 0f
                fish.targetX = (fish.x + cos(angle) * 0.4f).coerceIn(0.08f, 0.92f)
                fish.targetY = (fish.y + sin(angle) * 0.4f).coerceIn(0.12f, 0.82f)
            }
        }
    }

    fun update(rawDeltaTime: Float, viewportWidth: Float, viewportHeight: Float) {
        ensureStarterFishActive()

        // Clamp deltaTime to 0.05f max to prevent physics explosions
        val dt = rawDeltaTime.coerceAtMost(0.05f)
        causticsPhase += dt * 1.2f

        if (feedCooldownTimer > 0f) {
            feedCooldownTimer = (feedCooldownTimer - dt).coerceAtLeast(0f)
        }

        // Slowly degrade bio-load balance over time
        bioLoadBalance = (bioLoadBalance - dt * 0.15f).coerceIn(10f, 100f)

        // --- Update Glass Ripples ---
        val rippleIter = glassRipples.iterator()
        while (rippleIter.hasNext()) {
            val ripple = rippleIter.next()
            ripple.radius += dt * 320f
            ripple.alpha -= dt * 1.8f
            if (ripple.alpha <= 0f || ripple.radius >= ripple.maxRadius) {
                rippleIter.remove()
            }
        }

        // --- Update Floating Texts ---
        val textIter = floatingTexts.iterator()
        while (textIter.hasNext()) {
            val ft = textIter.next()
            ft.y -= dt * 45f
            ft.alpha -= dt * 1.2f
            ft.lifetime -= dt
            if (ft.alpha <= 0f || ft.lifetime <= 0f) {
                textIter.remove()
            }
        }

        // --- Update Ambient Bubbles ---
        for (bubble in bubbles) {
            bubble.y -= bubble.speed * dt
            bubble.wobblePhase += bubble.wobbleSpeed * dt
            bubble.x += sin(bubble.wobblePhase) * 0.0015f
            if (bubble.y < 0.02f) {
                bubble.y = 1.05f
                bubble.x = Random.nextFloat()
            }
        }

        // --- Update Food Pellets ---
        val foodIter = foodPellets.iterator()
        while (foodIter.hasNext()) {
            val pellet = foodIter.next()
            pellet.y += pellet.vy * dt
            pellet.wobblePhase += dt * 3f
            pellet.x += sin(pellet.wobblePhase) * 0.001f
            if (pellet.y > 0.88f) {
                foodIter.remove()
            }
        }

        // --- Update Fish Physics & AI ---
        for (fish in fishList) {
            val species = FishSpecies.getSpeciesById(fish.speciesId) ?: continue

            // 1. Smooth turn flip interpolation (flipProgress from -1f to 1f over 250ms)
            val targetFlip = if (fish.facingRight) 1f else -1f
            if (fish.flipProgress != targetFlip) {
                val step = (dt / 0.25f)
                if (fish.flipProgress < targetFlip) {
                    fish.flipProgress = (fish.flipProgress + step).coerceAtMost(targetFlip)
                } else {
                    fish.flipProgress = (fish.flipProgress - step).coerceAtLeast(targetFlip)
                }
            }

            // 2. Swim phase accumulation (3.2x tail-beat speed when STARTLED)
            val tailBeatMultiplier = if (fish.aiState == AiState.STARTLED) 3.2f else 1.0f
            fish.swimPhase += dt * species.waveFrequency * 3.5f * tailBeatMultiplier

            // 3. AI State Machine
            fish.stateTimer -= dt

            when (fish.aiState) {
                AiState.STARTLED -> {
                    if (fish.stateTimer <= 0f) {
                        // Transition to CURIOUS (slowly swim back toward tap point)
                        fish.aiState = AiState.CURIOUS
                        fish.stateTimer = 2.0f
                        // Target center-ish area for curiosity
                        fish.targetX = 0.5f + (Random.nextFloat() - 0.5f) * 0.2f
                        fish.targetY = 0.5f + (Random.nextFloat() - 0.5f) * 0.2f
                    }
                }

                AiState.CURIOUS -> {
                    if (fish.stateTimer <= 0f) {
                        fish.aiState = AiState.CRUISING
                    }
                }

                AiState.SEEKING_FOOD -> {
                    if (foodPellets.isEmpty()) {
                        fish.aiState = AiState.CRUISING
                    } else {
                        // Target nearest food pellet
                        var nearestPellet: FoodPellet? = null
                        var minDist = Float.MAX_VALUE
                        for (pellet in foodPellets) {
                            val pdx = pellet.x - fish.x
                            val pdy = pellet.y - fish.y
                            val pdist = sqrt(pdx * pdx + pdy * pdy)
                            if (pdist < minDist) {
                                minDist = pdist
                                nearestPellet = pellet
                            }
                        }

                        if (nearestPellet != null) {
                            fish.targetX = nearestPellet.x
                            fish.targetY = nearestPellet.y

                            // Consume food pellet on contact
                            if (minDist < 0.04f) {
                                foodPellets.remove(nearestPellet)
                                audioManager.playBubblePop()
                                val pointsEarned = (species.basePointsPerMin * 2L).coerceAtLeast(10L)
                                carePoints += pointsEarned

                                floatingTexts.add(
                                    FloatingText(
                                        id = "txt_${System.currentTimeMillis()}",
                                        text = "+$pointsEarned Care Points",
                                        x = fish.x * viewportWidth,
                                        y = fish.y * viewportHeight
                                    )
                                )
                                fish.aiState = AiState.CRUISING
                            }
                        }
                    }
                }

                AiState.CRUISING -> {
                    val minX = if (currentTankTier == TankTier.BOWL) 0.25f else 0.08f
                    val maxX = if (currentTankTier == TankTier.BOWL) 0.75f else 0.92f
                    val minY = if (currentTankTier == TankTier.BOWL) 0.25f else 0.12f
                    val maxY = if (currentTankTier == TankTier.BOWL) 0.72f else 0.82f

                    val distToTarget = sqrt((fish.targetX - fish.x) * (fish.targetX - fish.x) + (fish.targetY - fish.y) * (fish.targetY - fish.y))
                    if (distToTarget < 0.06f || fish.stateTimer <= 0f) {
                        // Waypoint selection within safe margins
                        fish.targetX = minX + Random.nextFloat() * (maxX - minX)
                        fish.targetY = minY + Random.nextFloat() * (maxY - minY)
                        fish.stateTimer = 3f + Random.nextFloat() * 4f
                    }

                    // Schooling boids behavior
                    if (species.isSchooling) {
                        var alignX = 0f
                        var alignY = 0f
                        var cohereX = 0f
                        var cohereY = 0f
                        var separateX = 0f
                        var separateY = 0f
                        var neighborCount = 0

                        for (other in fishList) {
                            if (other.instanceId != fish.instanceId && other.speciesId == fish.speciesId) {
                                val dx = other.x - fish.x
                                val dy = other.y - fish.y
                                val d = sqrt(dx * dx + dy * dy)
                                if (d < 0.25f && d > 0.001f) {
                                    alignX += other.vx
                                    alignY += other.vy
                                    cohereX += other.x
                                    cohereY += other.y

                                    if (d < 0.08f) {
                                        separateX -= dx / d
                                        separateY -= dy / d
                                    }
                                    neighborCount++
                                }
                            }
                        }

                        if (neighborCount > 0) {
                            cohereX = (cohereX / neighborCount) - fish.x
                            cohereY = (cohereY / neighborCount) - fish.y

                            fish.targetX = (fish.targetX + cohereX * 0.2f + separateX * 0.3f).coerceIn(minX, maxX)
                            fish.targetY = (fish.targetY + cohereY * 0.2f + separateY * 0.3f).coerceIn(minY, maxY)
                        }
                    }
                }
            }

            val minX = if (currentTankTier == TankTier.BOWL) 0.25f else 0.08f
            val maxX = if (currentTankTier == TankTier.BOWL) 0.75f else 0.92f
            val minY = if (currentTankTier == TankTier.BOWL) 0.25f else 0.12f
            val maxY = if (currentTankTier == TankTier.BOWL) 0.72f else 0.82f

            // 4. Movement Kinematics
            val dx = fish.targetX - fish.x
            val dy = fish.targetY - fish.y
            val dist = sqrt(dx * dx + dy * dy)

            val speedStat = species.swimSpeed * 0.05f
            val currentSpeed = when (fish.aiState) {
                AiState.STARTLED -> speedStat * 3.2f
                AiState.SEEKING_FOOD -> speedStat * 1.5f
                AiState.CURIOUS -> speedStat * 0.8f
                AiState.CRUISING -> speedStat
            }

            if (dist > 0.005f) {
                val moveRatio = (currentSpeed * dt / dist).coerceAtMost(1.0f)
                val targetVx = dx * moveRatio
                val targetVy = dy * moveRatio

                // Smooth velocity steering blending
                fish.vx += (targetVx - fish.vx) * 0.1f
                fish.vy += (targetVy - fish.vy) * 0.1f
            }

            fish.x = (fish.x + fish.vx).coerceIn(minX, maxX)
            fish.y = (fish.y + fish.vy).coerceIn(minY, maxY)

            if (kotlin.math.abs(fish.vx) > 0.002f) {
                fish.facingRight = fish.vx > 0f
            }
        }
    }
}
