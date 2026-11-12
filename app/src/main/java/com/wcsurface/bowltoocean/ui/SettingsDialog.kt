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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.window.Dialog
import com.wcsurface.bowltoocean.engine.AquariumEngine

/**
 * SettingsDialog provides frosted glass audio, haptic, debug FPS, and save reset controls.
 */
@Composable
fun SettingsDialog(
    engine: AquariumEngine,
    onResetData: () -> Unit,
    onDismiss: () -> Unit
) {
    var showResetConfirm by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFA0A1428))
                .border(BorderStroke(1.dp, Color(0x3300D2FF)), RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings",
                            tint = Color(0xFF00D2FF),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.padding(start = 8.dp))
                        Text(
                            text = "Settings & Debug",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Toggle: Sound Effects
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Sound Effects", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Procedural audio PCM synthesis", color = Color(0xFF8B949E), fontSize = 11.sp)
                    }
                    Switch(
                        checked = engine.soundEnabled,
                        onCheckedChange = { engine.soundEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = Color(0xFF00D2FF)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Toggle: Haptic Feedback
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Haptic Feedback", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Glass taps & startled vibration", color = Color(0xFF8B949E), fontSize = 11.sp)
                    }
                    Switch(
                        checked = engine.hapticsEnabled,
                        onCheckedChange = { engine.hapticsEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = Color(0xFF00D2FF)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Toggle: Show FPS / Debug Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Show FPS / Debug Info", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Display live frame rate & bio-load", color = Color(0xFF8B949E), fontSize = 11.sp)
                    }
                    Switch(
                        checked = engine.showDebugFps,
                        onCheckedChange = { engine.showDebugFps = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = Color(0xFF00D2FF)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Reset Data Action Button
                Button(
                    onClick = { showResetConfirm = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0x33FF3D00),
                        contentColor = Color(0xFFFF5252)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFFF3D00)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.RestartAlt,
                        contentDescription = "Reset",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.padding(start = 6.dp))
                    Text(
                        text = "Reset Save Data (Closed Testing)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text("Reset Save Data?") },
            text = { Text("Are you sure you want to reset all unlocked tanks, care points, and fish back to starting Desktop Bowl state?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResetConfirm = false
                        onResetData()
                        onDismiss()
                    }
                ) {
                    Text("Reset Data", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text("Cancel")
                }
            },
            containerColor = Color(0xF20A1428),
            titleContentColor = Color.White,
            textContentColor = Color(0xFFC9D1D9)
        )
    }
}
