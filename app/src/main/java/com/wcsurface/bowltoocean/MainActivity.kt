package com.wcsurface.bowltoocean

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wcsurface.bowltoocean.ui.AquariumCanvas
import com.wcsurface.bowltoocean.ui.AquariumHud
import com.wcsurface.bowltoocean.ui.theme.BowlToOceanAquariumSimTheme
import com.wcsurface.bowltoocean.viewmodel.AquariumViewModel

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
fun AquariumSimScreen(
    viewModel: AquariumViewModel = viewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    // Trigger Railway background sync on app pause/stop
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                viewModel.onAppStop()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val engine = viewModel.engine

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
