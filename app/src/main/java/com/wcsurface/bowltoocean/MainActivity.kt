package com.wcsurface.bowltoocean

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.wcsurface.bowltoocean.engine.AquariumEngine
import com.wcsurface.bowltoocean.model.TankTier
import com.wcsurface.bowltoocean.ui.AquariumCanvas
import com.wcsurface.bowltoocean.ui.AquariumHud
import com.wcsurface.bowltoocean.ui.theme.BowlToOceanAquariumSimTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BowlToOceanAquariumSimTheme {
                AquariumSimScreen()
            }
        }
    }
}

@Composable
fun AquariumSimScreen() {
    val engine = remember { AquariumEngine(initialTankTier = TankTier.BOWL) }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Full-screen 60 FPS Canvas with 2D Mesh Deforming Fish and Atmospheric Water
        AquariumCanvas(
            engine = engine,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Sleek Dark-Ocean Frosted Glass HUD Overlay
        AquariumHud(
            engine = engine,
            modifier = Modifier.fillMaxSize()
        )
    }
}
