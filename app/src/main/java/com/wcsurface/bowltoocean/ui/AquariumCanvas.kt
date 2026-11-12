package com.wcsurface.bowltoocean.ui

import android.graphics.Paint as NativePaint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.VertexMode
import androidx.compose.ui.graphics.Vertices
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wcsurface.bowltoocean.engine.AquariumEngine
import com.wcsurface.bowltoocean.model.FishInstance
import com.wcsurface.bowltoocean.model.FishSpecies
import com.wcsurface.bowltoocean.util.AssetResolver
import kotlin.math.pow
import kotlin.math.sin

/**
 * FishMeshRenderer renders a fish using hardware-accelerated Canvas.drawVertices()
 * with 20 horizontal columns (42 vertices in TriangleStrip mode).
 */
fun drawFishMesh(
    drawScope: androidx.compose.ui.graphics.drawscope.DrawScope,
    fish: FishInstance,
    species: FishSpecies,
    imageBitmap: ImageBitmap,
    viewportWidth: Float,
    viewportHeight: Float
) {
    val columns = 20
    val numVertices = (columns + 1) * 2 // 42 vertices

    val bmpWidth = imageBitmap.width.toFloat()
    val bmpHeight = imageBitmap.height.toFloat()
    val aspectRatio = if (bmpWidth > 0f) bmpHeight / bmpWidth else 0.6f

    // Hero scale 240dp base width preserving image aspect ratio
    val density = drawScope.density
    val heroWidth = 240f * density * (species.scale / 1.8f)
    val baseWidth = heroWidth
    val baseHeight = heroWidth * aspectRatio

    val positions = ArrayList<Offset>(numVertices)
    val uvs = ArrayList<Offset>(numVertices)
    val indices = ArrayList<Int>(numVertices)

    val fishCenterX = fish.x * viewportWidth
    val fishCenterY = fish.y * viewportHeight

    val waveFreq = species.waveFrequency
    val waveAmp = species.waveAmplitude
    val phase = fish.swimPhase
    val flip = fish.flipProgress // Smoothly interpolates -1f..1f over 250ms

    // Steering offset proportional to vertical velocity vy
    val steeringScale = 180f

    for (i in 0..columns) {
        val u = i.toFloat() / columns.toFloat() // 0.0 (nose/front) to 1.0 (tail/back)

        // 1. Travelling sine wave along normalized spine from u=0 to u=1
        val offsetY = sin(phase - u * waveFreq) * (waveAmp * u.pow(1.6f))

        // 2. Steering curve offset proportional to vertical velocity vy
        val steeringOffset = fish.vy * steeringScale * u * (1f - u)

        val totalOffsetY = offsetY + steeringOffset

        // Relative X position from center scaled by flipProgress for smooth left/right turn
        val relX = (0.5f - u) * baseWidth * flip

        val topRelY = -0.5f * baseHeight + totalOffsetY
        val botRelY = 0.5f * baseHeight + totalOffsetY

        val posX = fishCenterX + relX
        val topPosY = fishCenterY + topRelY
        val botPosY = fishCenterY + botRelY

        // Texture coordinates UV in PIXEL SPACE (Nose at uTex = bmpWidth, Tail at uTex = 0f)
        val uTexPixel = (1.0f - u) * bmpWidth
        val topUV = Offset(uTexPixel, 0.0f)
        val botUV = Offset(uTexPixel, bmpHeight)

        positions.add(Offset(posX, topPosY))
        uvs.add(topUV)

        positions.add(Offset(posX, botPosY))
        uvs.add(botUV)
    }

    for (idx in 0 until numVertices) {
        indices.add(idx)
    }

    val colors = List(numVertices) { Color.White }

    val vertices = Vertices(
        vertexMode = VertexMode.TriangleStrip,
        positions = positions,
        textureCoordinates = uvs,
        colors = colors,
        indices = indices
    )

    val paint = Paint().apply {
        shader = ImageShader(imageBitmap)
    }

    drawScope.drawIntoCanvas { canvas ->
        canvas.drawVertices(
            vertices = vertices,
            blendMode = BlendMode.SrcOver,
            paint = paint
        )
    }
}

/**
 * AquariumCanvas is the main 60 FPS full-screen interactive simulation view.
 */
@Composable
fun AquariumCanvas(
    engine: AquariumEngine,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    var liveFps by remember { mutableIntStateOf(60) }

    // Frame loop driver updating simulation at 60 FPS
    LaunchedEffect(engine) {
        var lastTimeNanos = System.nanoTime()
        var frameCounter = 0
        var fpsTimerNanos = System.nanoTime()

        while (true) {
            withFrameNanos { currentTimeNanos ->
                val dt = (currentTimeNanos - lastTimeNanos) / 1_000_000_000f
                lastTimeNanos = currentTimeNanos

                frameCounter++
                if (currentTimeNanos - fpsTimerNanos >= 1_000_000_000L) {
                    liveFps = frameCounter
                    frameCounter = 0
                    fpsTimerNanos = currentTimeNanos
                }

                // Engine update with dt clamped to 0.05f
                engine.update(dt, 1080f, 1920f)
            }
        }
    }

    val textNativePaint = remember {
        NativePaint().apply {
            color = android.graphics.Color.WHITE
            textSize = 38f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            setShadowLayer(4f, 2f, 2f, android.graphics.Color.BLACK)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(engine) {
                    detectTapGestures(
                        onTap = { offset ->
                            if (engine.hapticsEnabled) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                            engine.onGlassTap(
                                tapXPx = offset.x,
                                tapYPx = offset.y,
                                viewportWidth = size.width.toFloat(),
                                viewportHeight = size.height.toFloat(),
                                tapRadiusPx = 250f
                            )
                        }
                    )
                }
        ) {
            val width = size.width
            val height = size.height

            // 1. Tank Background
            val currentTier = engine.currentTankTier
            val bgBitmap = AssetResolver.getBitmap(
                context = context,
                resName = currentTier.bgDrawableResName,
                width = width.toInt().coerceAtLeast(100),
                height = height.toInt().coerceAtLeast(100)
            )
            drawImage(image = bgBitmap, dstSize = androidx.compose.ui.unit.IntSize(width.toInt(), height.toInt()))

            // 2. Ambient Tank Tint & Bio-Load Water Clarity Overlays
            drawRect(color = currentTier.ambientTint.copy(alpha = 0.12f))

            if (engine.bioLoadBalance < 30f) {
                // Low bio-load balance (<30%): Greenish-murky tint overlay
                drawRect(color = Color(0x382E5A1E))
            } else if (engine.bioLoadBalance > 80f) {
                // High bio-load balance (>80%): Sparkling water clarity particles
                val sparklePhase = engine.causticsPhase
                for (s in 0..7) {
                    val sx = width * (0.1f + s * 0.12f + sin(sparklePhase + s) * 0.05f)
                    val sy = height * (0.2f + sin(sparklePhase * 1.5f + s) * 0.15f)
                    val sparkleAlpha = (sin(sparklePhase * 3f + s) * 0.4f + 0.5f).coerceIn(0f, 1f)
                    drawCircle(
                        color = Color.White.copy(alpha = sparkleAlpha),
                        radius = 4f + sin(sparklePhase + s) * 2f,
                        center = Offset(sx, sy)
                    )
                }
            }

            // 3. Atmospheric Water Layer - Caustic Light Shafts
            val phase = engine.causticsPhase
            for (k in 0..4) {
                val shaftX = (width * (0.15f + k * 0.22f) + sin(phase + k) * 35f).coerceIn(0f, width)
                val shaftPath = Path().apply {
                    moveTo(shaftX - 30f, 0f)
                    lineTo(shaftX + 60f, 0f)
                    lineTo(shaftX + 110f + sin(phase * 0.8f + k) * 25f, height * 0.75f)
                    lineTo(shaftX - 80f + sin(phase * 0.8f + k) * 25f, height * 0.75f)
                    close()
                }
                drawPath(
                    path = shaftPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.18f),
                            Color.White.copy(alpha = 0.02f),
                            Color.Transparent
                        )
                    )
                )
            }

            // 4. Atmospheric Water Layer - Rising Bubbles
            for (bubble in engine.bubbles) {
                val bx = bubble.x * width
                val by = bubble.y * height
                drawCircle(
                    color = Color.White.copy(alpha = 0.35f),
                    radius = bubble.radius,
                    center = Offset(bx, by),
                    style = Stroke(width = 2f)
                )
                // Highlight dot
                drawCircle(
                    color = Color.White.copy(alpha = 0.6f),
                    radius = (bubble.radius * 0.35f).coerceAtLeast(1f),
                    center = Offset(bx - bubble.radius * 0.3f, by - bubble.radius * 0.3f)
                )
            }

            // 5. Atmospheric Water Layer - Sinking Food Pellets
            for (pellet in engine.foodPellets) {
                val px = pellet.x * width
                val py = pellet.y * height
                drawCircle(
                    color = Color(0xFFE5A020),
                    radius = 7f,
                    center = Offset(px, py)
                )
                drawCircle(
                    color = Color(0xFFFFF0A0),
                    radius = 3f,
                    center = Offset(px - 2f, py - 2f)
                )
            }

            // 6. Fish Layer (using Canvas.drawVertices mesh skinning)
            for (fish in engine.fishList) {
                val species = FishSpecies.getSpeciesById(fish.speciesId) ?: continue
                val fishBitmap = AssetResolver.getBitmap(
                    context = context,
                    resName = species.drawableResName,
                    width = 256,
                    height = 256
                )
                drawFishMesh(
                    drawScope = this,
                    fish = fish,
                    species = species,
                    imageBitmap = fishBitmap,
                    viewportWidth = width,
                    viewportHeight = height
                )
            }

            // 7. Glass Tap Ripples
            for (ripple in engine.glassRipples) {
                drawCircle(
                    color = Color.White.copy(alpha = ripple.alpha.coerceIn(0f, 1f)),
                    radius = ripple.radius,
                    center = Offset(ripple.x, ripple.y),
                    style = Stroke(width = 4f)
                )
            }

            // 8. Floating Text Popups
            for (ft in engine.floatingTexts) {
                drawIntoCanvas { canvas ->
                    textNativePaint.alpha = (ft.alpha.coerceIn(0f, 1f) * 255).toInt()
                    canvas.nativeCanvas.drawText(ft.text, ft.x, ft.y, textNativePaint)
                }
            }
        }

        // Live FPS & Bio-Load Debug Overlay
        if (engine.showDebugFps) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xCC0A1428))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "$liveFps FPS | Bio-Load: ${engine.bioLoadBalance.toInt()}% | Fish: ${engine.fishList.size}/${engine.currentTankTier.maxFish}",
                    color = Color(0xFF00D2FF),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
