package com.wcsurface.bowltoocean.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Water
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
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
 * Catalog Sheet Modal Component for Tank Progression & Fish Species Shop.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogSheet(
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

            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0x66122240),
                contentColor = Color(0xFF00D2FF)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Pets, contentDescription = "Fish", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Fish & Creatures (${engine.fishList.size}/${engine.currentTankTier.maxFish})")
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Water, contentDescription = "Tanks", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Tanks & Exhibits (${engine.unlockedTanks.size}/7)")
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            when (selectedTab) {
                0 -> FishCreaturesTab(engine = engine)
                1 -> TanksExhibitsTab(engine = engine)
            }
        }
    }
}

@Composable
fun FishCreaturesTab(engine: AquariumEngine) {
    val currentTank = engine.currentTankTier
    val allSpecies = FishSpecies.ALL_SPECIES

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.height(380.dp)
    ) {
        items(allSpecies) { species ->
            val isCompatible = species.tierRequired <= currentTank
            val canAfford = engine.carePoints >= species.cost
            val hasCapacity = engine.fishList.size < currentTank.maxFish

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isCompatible) Color(0x66122240) else Color(0x3309101C)
                ),
                border = BorderStroke(
                    1.dp,
                    if (isCompatible) Color(0x3300D2FF) else Color(0x11FFFFFF)
                ),
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = species.name,
                                color = if (isCompatible) Color.White else Color(0xFF8B949E),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            if (!isCompatible) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Filled.Lock,
                                    contentDescription = "Locked",
                                    tint = Color(0xFFFF3D00),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Tier: ${species.tierRequired.tierName} | Bio-Load: ${species.bioLoadWeight}",
                            color = Color(0xFF38B0A0),
                            fontSize = 11.sp
                        )
                        Text(
                            text = "Base Rate: +${species.basePointsPerMin} pts/min",
                            color = Color(0xFFFFD700),
                            fontSize = 11.sp
                        )
                    }

                    if (isCompatible) {
                        Button(
                            onClick = { engine.addFish(species.id) },
                            enabled = canAfford && hasCapacity,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFD700),
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (!hasCapacity) "Tank Full" else "Buy (${species.cost} pts)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x33FF3D00))
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Requires ${species.tierRequired.tierName}",
                                color = Color(0xFFFF6D00),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TanksExhibitsTab(engine: AquariumEngine) {
    val tiers = TankTier.entries

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.height(380.dp)
    ) {
        items(tiers) { tier ->
            val isUnlocked = engine.unlockedTanks.contains(tier)
            val isActive = engine.currentTankTier == tier

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        isActive -> Color(0xAA004080)
                        isUnlocked -> Color(0x66122240)
                        else -> Color(0x3309101C)
                    }
                ),
                border = BorderStroke(
                    1.dp,
                    when {
                        isActive -> Color(0xFF00D2FF)
                        isUnlocked -> Color(0x4400D2FF)
                        else -> Color(0x11FFFFFF)
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Preview Color Badge
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(tier.ambientTint)
                            .border(1.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isActive) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Active",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = tier.tierName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
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
                            containerColor = when {
                                isActive -> Color(0xFF4CAF50)
                                isUnlocked -> Color(0xFF00D2FF)
                                else -> Color(0xFFFFD700)
                            },
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp)
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
