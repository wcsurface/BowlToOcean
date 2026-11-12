package com.wcsurface.bowltoocean.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wcsurface.bowltoocean.engine.AquariumEngine
import com.wcsurface.bowltoocean.model.FishSpecies
import com.wcsurface.bowltoocean.model.TankTier

/**
 * TopStatusBar displaying Care Points, Diamonds, Player Level/XP, and Bio-Load filtration meter.
 */
@Composable
fun TopStatusBar(
    engine: AquariumEngine,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xCC0A1428))
            .border(BorderStroke(1.dp, Color(0x3300D2FF)), RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // --- Left Pills: Care Points & Diamonds ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Care Points Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x66000000))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.MonetizationOn,
                        contentDescription = "Care Points",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${engine.carePoints}",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Diamonds Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x66000000))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Diamond,
                        contentDescription = "Diamonds",
                        tint = Color(0xFF00D2FF),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${engine.diamonds}",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // --- Center Badge: Level & XP Bar ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x66000000))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Level",
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Lv. ${engine.playerLevel}",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                val xpProgress = (engine.playerXp.toFloat() / engine.xpToNextLevel.toFloat()).coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { xpProgress },
                    modifier = Modifier
                        .width(50.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFF00D2FF),
                    trackColor = Color(0x33FFFFFF)
                )
            }

            // --- Right Gauge: Bio-Load / Filtration Balance Meter ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x66000000))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.WaterDrop,
                        contentDescription = "Bio-Load Filtration",
                        tint = Color(0xFF00D2FF),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${engine.bioLoadBalance.toInt()}%",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))

                    val barColor = when {
                        engine.bioLoadBalance >= 75f -> Color(0xFF4CAF50) // Green
                        engine.bioLoadBalance >= 40f -> Color(0xFFFFC107) // Amber
                        else -> Color(0xFFF44336) // Red
                    }

                    LinearProgressIndicator(
                        progress = { (engine.bioLoadBalance / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .width(45.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = barColor,
                        trackColor = Color(0x33FFFFFF)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(
                    onClick = { engine.isSettingsOpen = true },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Settings",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

/**
 * Slide-Out Maintenance Drawer displaying the 3 MaintenanceAction tiles for active TankTier.
 */
@Composable
fun MaintainDrawer(
    engine: AquariumEngine,
    modifier: Modifier = Modifier
) {
    val tier = engine.currentTankTier

    Box(
        modifier = modifier
            .width(220.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xEE0A1428))
            .border(BorderStroke(1.dp, Color(0x3300D2FF)), RoundedCornerShape(20.dp))
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "${tier.tierName} Maintenance",
                color = Color(0xFF00D2FF),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            for (action in tier.maintenanceActions) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0x66122240)),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0x22FFFFFF)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = action.name,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Clean: +${action.cleanBonus.toInt()}% | XP: +35",
                            color = Color(0xFF38B0A0),
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Button(
                            onClick = { engine.performMaintenanceAction(action) },
                            enabled = engine.carePoints >= action.cost,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00D2FF),
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp)
                        ) {
                            Text(
                                text = "Clean (${action.cost} pts)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * RightDock & Daily Bonus floating controls stacked on right edge.
 */
@Composable
fun RightDock(
    engine: AquariumEngine,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
    ) {
        // Daily Bonus Gift Button
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(Color(0xCC0A1428))
                .border(BorderStroke(1.dp, Color(0xFFFFC107)), CircleShape)
                .clickable { engine.claimDailyBonus() }
                .padding(10.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.CardGiftcard,
                contentDescription = "Daily Bonus",
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(24.dp)
            )
            if (engine.dailyBonusClaimable) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF3D00))
                        .align(Alignment.TopEnd)
                )
            }
        }

        // Right-hand Frosted Glass Action Dock
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xCC0A1428))
                .border(BorderStroke(1.dp, Color(0x3300D2FF)), RoundedCornerShape(24.dp))
                .padding(vertical = 12.dp, horizontal = 8.dp)
        ) {
            // Feed Action Button
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = { engine.triggerFeedAction() },
                    enabled = engine.feedCooldownTimer <= 0f,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (engine.feedCooldownTimer <= 0f) Color(0x3300D2FF) else Color(0x11FFFFFF))
                ) {
                    Icon(
                        imageVector = Icons.Filled.Restaurant,
                        contentDescription = "Feed",
                        tint = if (engine.feedCooldownTimer <= 0f) Color(0xFF00D2FF) else Color.Gray,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Text(
                    text = if (engine.feedCooldownTimer > 0f) "${engine.feedCooldownTimer.toInt()}s" else "Feed",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Maintain Action Button
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = { engine.isMaintainDrawerOpen = !engine.isMaintainDrawerOpen },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0x3338B0A0))
                ) {
                    Icon(
                        imageVector = Icons.Filled.CleaningServices,
                        contentDescription = "Maintain",
                        tint = Color(0xFF38B0A0),
                        modifier = Modifier.size(22.dp)
                    )
                }
                Text(
                    text = "Maintain",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Catalog / Shop Action Button
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = { engine.isShopSheetOpen = true },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0x33FFD700))
                ) {
                    Icon(
                        imageVector = Icons.Filled.Store,
                        contentDescription = "Catalog",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(22.dp)
                    )
                }
                Text(
                    text = "Catalog",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Catalog Modal Bottom Sheet for Tank Progression & Fish Shop.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogModalBottomSheet(
    engine: AquariumEngine,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xF20A1428)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Aquarium Catalog & Progression",
                color = Color(0xFF00D2FF),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0x66122240),
                contentColor = Color(0xFF00D2FF)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Tank Tiers (${engine.unlockedTanks.size}/7)") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Fish Species (${engine.fishList.size}/${engine.currentTankTier.maxFish})") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (selectedTab) {
                0 -> {
                    // Tank Tiers Tab
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.height(350.dp)
                    ) {
                        items(TankTier.entries) { tier ->
                            val isUnlocked = engine.unlockedTanks.contains(tier)
                            val isActive = engine.currentTankTier == tier

                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isActive) Color(0xAA004080) else Color(0x66122240)
                                ),
                                border = BorderStroke(1.dp, if (isActive) Color(0xFF00D2FF) else Color(0x22FFFFFF)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = tier.tierName,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                        Text(
                                            text = "Capacity: ${tier.capacity} Gal | Max Fish: ${tier.maxFish}",
                                            color = Color(0xFF8B949E),
                                            fontSize = 12.sp
                                        )
                                    }

                                    Button(
                                        onClick = { engine.unlockAndSwitchTank(tier) },
                                        enabled = isActive || isUnlocked || engine.carePoints >= tier.cost,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isActive) Color(0xFF4CAF50) else Color(0xFF00D2FF),
                                            contentColor = Color.Black
                                        )
                                    ) {
                                        Text(
                                            text = when {
                                                isActive -> "Active"
                                                isUnlocked -> "Switch"
                                                else -> "Unlock (${tier.cost} pts)"
                                            },
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // Fish Species Shop Tab
                    val currentTierAvailableSpecies = FishSpecies.ALL_SPECIES.filter { it.tierRequired <= engine.currentTankTier }

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.height(350.dp)
                    ) {
                        items(currentTierAvailableSpecies) { species ->
                            val canAfford = engine.carePoints >= species.cost
                            val hasCapacity = engine.fishList.size < engine.currentTankTier.maxFish

                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0x66122240)),
                                border = BorderStroke(1.dp, Color(0x22FFFFFF)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = species.name,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "Rate: +${species.basePointsPerMin} pts/min | Bio-Load: ${species.bioLoadWeight}",
                                            color = Color(0xFF38B0A0),
                                            fontSize = 12.sp
                                        )
                                    }

                                    Button(
                                        onClick = { engine.addFish(species.id) },
                                        enabled = canAfford && hasCapacity,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFFFD700),
                                            contentColor = Color.Black
                                        )
                                    ) {
                                        Text(
                                            text = if (!hasCapacity) "Tank Full" else "Buy (${species.cost} pts)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Full Aquarium HUD Overlay Composable.
 * Entire bottom edge is kept 100% clear of buttons, bars, or labels.
 */
@Composable
fun AquariumHud(
    engine: AquariumEngine,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = engine.isHudVisible,
        enter = fadeIn(tween(220)),
        exit = fadeOut(tween(220)),
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // 1. Top Bar
            TopStatusBar(
                engine = engine,
                modifier = Modifier.align(Alignment.TopCenter)
            )

            // 2. Right-Hand Dock & Slide-Out Maintain Drawer
            Row(
                modifier = Modifier.align(Alignment.CenterEnd),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Slide-Out Maintain Drawer
                AnimatedVisibility(
                    visible = engine.isMaintainDrawerOpen,
                    enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(tween(220)),
                    exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(tween(220))
                ) {
                    MaintainDrawer(engine = engine)
                }

                // Right Dock Controls
                RightDock(engine = engine)
            }
        }
    }

    // 3. Catalog Modal Bottom Sheet
    if (engine.isShopSheetOpen) {
        CatalogModalBottomSheet(
            engine = engine,
            onDismiss = { engine.isShopSheetOpen = false }
        )
    }
}
