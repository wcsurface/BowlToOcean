package com.wcsurface.bowltoocean.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wcsurface.bowltoocean.engine.AquariumEngine
import com.wcsurface.bowltoocean.model.MaintenanceAction

/**
 * Reusable translucent Aqua-Glass panel with luminous cyan border.
 */
@Composable
fun AquaGlassPanel(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(18.dp),
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xAA13384C),
                        Color(0x880B2230)
                    )
                )
            )
            .border(
                border = BorderStroke(1.dp, Color(0x667CE8FF)),
                shape = shape
            )
    ) {
        content()
    }
}

// --- Custom Canvas Drawn Aquarium Icons ---

@Composable
fun FeedShakerIcon(tint: Color = Color(0xFF7CE8FF), modifier: Modifier = Modifier.size(24.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Shaker body tilted 25 degrees
        val bodyPath = Path().apply {
            moveTo(w * 0.35f, h * 0.25f)
            lineTo(w * 0.75f, h * 0.25f)
            lineTo(w * 0.70f, h * 0.75f)
            lineTo(w * 0.40f, h * 0.75f)
            close()
        }
        drawPath(path = bodyPath, color = tint.copy(alpha = 0.85f))

        // Cap
        drawRect(
            color = tint,
            topLeft = Offset(w * 0.32f, h * 0.18f),
            size = Size(w * 0.46f, h * 0.08f)
        )

        // Dropping flakes
        drawCircle(color = Color(0xFFFFD700), radius = 2f, center = Offset(w * 0.25f, h * 0.85f))
        drawCircle(color = Color(0xFFFFD700), radius = 2.5f, center = Offset(w * 0.40f, h * 0.92f))
        drawCircle(color = Color(0xFFFFD700), radius = 2f, center = Offset(w * 0.55f, h * 0.82f))
    }
}

@Composable
fun MaintainSpongeIcon(tint: Color = Color(0xFF38B0A0), modifier: Modifier = Modifier.size(24.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Sponge rectangle
        drawRoundRect(
            color = tint.copy(alpha = 0.9f),
            topLeft = Offset(w * 0.18f, h * 0.30f),
            size = Size(w * 0.64f, h * 0.45f),
            cornerRadius = CornerRadius(4f, 4f)
        )

        // Sponge pores/dots
        drawCircle(color = Color.Black.copy(alpha = 0.3f), radius = 1.8f, center = Offset(w * 0.32f, h * 0.45f))
        drawCircle(color = Color.Black.copy(alpha = 0.3f), radius = 2.2f, center = Offset(w * 0.52f, h * 0.52f))
        drawCircle(color = Color.Black.copy(alpha = 0.3f), radius = 1.8f, center = Offset(w * 0.68f, h * 0.42f))

        // Bubbles around sponge
        drawCircle(color = Color.White.copy(alpha = 0.8f), radius = 2.5f, center = Offset(w * 0.15f, h * 0.22f), style = Stroke(1.5f))
        drawCircle(color = Color.White.copy(alpha = 0.8f), radius = 3.5f, center = Offset(w * 0.82f, h * 0.25f), style = Stroke(1.5f))
    }
}

@Composable
fun CatalogBookIcon(tint: Color = Color(0xFFFFD700), modifier: Modifier = Modifier.size(24.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Left page
        val leftPage = Path().apply {
            moveTo(w * 0.10f, h * 0.30f)
            lineTo(w * 0.48f, h * 0.22f)
            lineTo(w * 0.48f, h * 0.78f)
            lineTo(w * 0.10f, h * 0.85f)
            close()
        }
        drawPath(path = leftPage, color = tint.copy(alpha = 0.8f))

        // Right page
        val rightPage = Path().apply {
            moveTo(w * 0.52f, h * 0.22f)
            lineTo(w * 0.90f, h * 0.30f)
            lineTo(w * 0.90f, h * 0.85f)
            lineTo(w * 0.52f, h * 0.78f)
            close()
        }
        drawPath(path = rightPage, color = tint)

        // Center spine line
        drawLine(color = Color.Black.copy(alpha = 0.5f), start = Offset(w * 0.5f, h * 0.20f), end = Offset(w * 0.5f, h * 0.80f), strokeWidth = 2f)
    }
}

@Composable
fun BucketIcon(tint: Color = Color(0xFF00D2FF), modifier: Modifier = Modifier.size(22.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val bucketPath = Path().apply {
            moveTo(w * 0.20f, h * 0.30f)
            lineTo(w * 0.80f, h * 0.30f)
            lineTo(w * 0.70f, h * 0.85f)
            lineTo(w * 0.30f, h * 0.85f)
            close()
        }
        drawPath(path = bucketPath, color = tint.copy(alpha = 0.85f))

        // Bucket handle arch
        drawArc(
            color = tint,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(w * 0.20f, h * 0.12f),
            size = Size(w * 0.60f, h * 0.35f),
            style = Stroke(2f, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun SqueegeeIcon(tint: Color = Color(0xFF38B0A0), modifier: Modifier = Modifier.size(22.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Wiper blade top bar
        drawRoundRect(color = tint, topLeft = Offset(w * 0.12f, h * 0.22f), size = Size(w * 0.76f, h * 0.12f), cornerRadius = CornerRadius(2f, 2f))

        // Squeegee handle
        drawLine(color = tint, start = Offset(w * 0.5f, h * 0.34f), end = Offset(w * 0.5f, h * 0.85f), strokeWidth = 3.5f, cap = StrokeCap.Round)
    }
}

@Composable
fun TestTubeIcon(tint: Color = Color(0xFFFFD700), modifier: Modifier = Modifier.size(22.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Test tube outline
        val tubePath = Path().apply {
            moveTo(w * 0.38f, h * 0.15f)
            lineTo(w * 0.62f, h * 0.15f)
            lineTo(w * 0.62f, h * 0.75f)
            arcTo(rect = androidx.compose.ui.geometry.Rect(w * 0.38f, h * 0.65f, w * 0.62f, h * 0.88f), startAngleDegrees = 0f, sweepAngleDegrees = 180f, forceMoveTo = false)
            lineTo(w * 0.38f, h * 0.15f)
            close()
        }
        drawPath(path = tubePath, color = tint.copy(alpha = 0.3f), style = Stroke(1.8f))

        // Liquid inside tube
        val liquidPath = Path().apply {
            moveTo(w * 0.40f, h * 0.45f)
            lineTo(w * 0.60f, h * 0.45f)
            lineTo(w * 0.60f, h * 0.72f)
            arcTo(rect = androidx.compose.ui.geometry.Rect(w * 0.40f, h * 0.62f, w * 0.60f, h * 0.85f), startAngleDegrees = 0f, sweepAngleDegrees = 180f, forceMoveTo = false)
            lineTo(w * 0.40f, h * 0.45f)
            close()
        }
        drawPath(path = liquidPath, color = tint)
    }
}

// --- Top StatusBar Composable ---

@Composable
fun TopStatusBar(
    engine: AquariumEngine,
    modifier: Modifier = Modifier
) {
    AquaGlassPanel(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 4.dp, end = 56.dp, top = 4.dp, bottom = 4.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
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
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.MonetizationOn,
                        contentDescription = "Care Points",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${engine.carePoints}",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(Color(0x33FFD700))
                            .clickable { engine.isShopSheetOpen = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add Points",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Diamonds Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x66000000))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Diamond,
                        contentDescription = "Diamonds",
                        tint = Color(0xFF7CE8FF),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${engine.diamonds}",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(Color(0x3300D2FF))
                            .clickable { engine.claimDailyBonus() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add Diamonds",
                            tint = Color(0xFF7CE8FF),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            // --- Center Badge: Level & XP Bar ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x66000000))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Level",
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "Lv. ${engine.playerLevel}",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))

                val xpProgress = (engine.playerXp.toFloat() / engine.xpToNextLevel.toFloat()).coerceIn(0f, 1f)
                Column(
                    modifier = Modifier.width(70.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator(
                        progress = { xpProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Color(0xFF7CE8FF),
                        trackColor = Color(0x33FFFFFF)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${engine.playerXp}/${engine.xpToNextLevel}",
                        color = Color(0xFF8B949E),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // --- Right Gauge: Bio-Load / Tank Status Balance Meter ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x66000000))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.WaterDrop,
                        contentDescription = "Tank Status",
                        tint = Color(0xFF7CE8FF),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${engine.bioLoadBalance.toInt()}%",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    val barColor = when {
                        engine.bioLoadBalance >= 75f -> Color(0xFF4CAF50) // Green
                        engine.bioLoadBalance >= 40f -> Color(0xFFFFC107) // Amber
                        else -> Color(0xFFF44336) // Red
                    }

                    LinearProgressIndicator(
                        progress = { (engine.bioLoadBalance / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .width(60.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = barColor,
                        trackColor = Color(0x33FFFFFF)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { engine.isSettingsOpen = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Settings",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * Slide-Out Maintain Drawer containing Water Change, Clean Glass, and Check Water action tiles.
 */
@Composable
fun MaintainDrawer(
    engine: AquariumEngine,
    modifier: Modifier = Modifier
) {
    val tier = engine.currentTankTier

    AquaGlassPanel(
        modifier = modifier
            .width(230.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "${tier.tierName} Maintenance",
                color = Color(0xFF7CE8FF),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            for (action in tier.maintenanceActions) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0x4413384C)),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0x337CE8FF)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            when {
                                action.name.contains("Water") -> BucketIcon(tint = Color(0xFF00D2FF))
                                action.name.contains("Glass") || action.name.contains("Clean") -> SqueegeeIcon(tint = Color(0xFF38B0A0))
                                else -> TestTubeIcon(tint = Color(0xFFFFD700))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = action.name,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Clean: +${action.cleanBonus.toInt()}% | Reward: +${action.rewardPoints} pts",
                            color = Color(0xFF38B0A0),
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Button(
                            onClick = { engine.performMaintenanceAction(action) },
                            enabled = engine.carePoints >= action.cost,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF7CE8FF),
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp)
                        ) {
                            Text(
                                text = "Perform (${action.cost} pts)",
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
 * RightDock floating controls with custom Canvas-drawn aquarium icons.
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

        // Right-hand Aqua-Glass Action Dock
        AquaGlassPanel(shape = RoundedCornerShape(26.dp)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.padding(vertical = 14.dp, horizontal = 10.dp)
            ) {
                // Feed Action Button
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = { engine.triggerFeedAction() },
                        enabled = engine.feedCooldownTimer <= 0f,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(if (engine.feedCooldownTimer <= 0f) Color(0x3300D2FF) else Color(0x11FFFFFF))
                    ) {
                        FeedShakerIcon(tint = if (engine.feedCooldownTimer <= 0f) Color(0xFF7CE8FF) else Color.Gray)
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
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0x3338B0A0))
                    ) {
                        MaintainSpongeIcon(tint = Color(0xFF38B0A0))
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
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0x33FFD700))
                    ) {
                        CatalogBookIcon(tint = Color(0xFFFFD700))
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
        CatalogSheet(
            engine = engine,
            onDismiss = { engine.isShopSheetOpen = false }
        )
    }

    // 4. Settings Dialog
    if (engine.isSettingsOpen) {
        SettingsDialog(
            engine = engine,
            onResetData = {
                engine.resetDataToDefaults()
            },
            onDismiss = { engine.isSettingsOpen = false }
        )
    }
}
