package com.wcsurface.bowltoocean.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.graphics.Typeface
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.util.concurrent.ConcurrentHashMap

/**
 * AssetResolver provides a resilient bridge for loading game drawables.
 * If a target PNG asset is missing from res/drawable-nodpi/ or res/drawable/,
 * it dynamically synthesizes a high-contrast procedural ImageBitmap fallback.
 */
object AssetResolver {

    private val bitmapCache = ConcurrentHashMap<String, ImageBitmap>()

    /**
     * Resolves an ImageBitmap by resource name.
     * Checks local cache first, then Android res/drawable, and falls back to dynamic procedural generation.
     */
    fun getBitmap(context: Context, resName: String, width: Int = 256, height: Int = 256): ImageBitmap {
        val cacheKey = "${resName}_${width}x${height}"
        bitmapCache[cacheKey]?.let { return it }

        // Attempt loading real resource
        val resId = context.resources.getIdentifier(resName, "drawable", context.packageName)
        if (resId != 0) {
            try {
                val opts = BitmapFactory.Options().apply {
                    inScaled = false
                }
                val rawBitmap = BitmapFactory.decodeResource(context.resources, resId, opts)
                if (rawBitmap != null) {
                    val imageBitmap = rawBitmap.asImageBitmap()
                    bitmapCache[cacheKey] = imageBitmap
                    return imageBitmap
                }
            } catch (_: Exception) {
                // Fall back to procedural generation if decode fails
            }
        }

        // Generate dynamic procedural fallback
        val fallbackBitmap = if (resName.startsWith("bg_tank_")) {
            generateProceduralTankBg(resName, width, height)
        } else {
            generateProceduralFishBitmap(resName, width, height)
        }

        val imageBitmap = fallbackBitmap.asImageBitmap()
        bitmapCache[cacheKey] = imageBitmap
        return imageBitmap
    }

    /**
     * Clears cached bitmaps if needed.
     */
    fun clearCache() {
        bitmapCache.clear()
    }

    /**
     * Generates a dynamic high-contrast procedural background for tank tiers.
     */
    private fun generateProceduralTankBg(resName: String, width: Int, height: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Determine gradient colors based on resName
        val topColor: Int
        val bottomColor: Int
        val titleText: String

        when {
            resName.contains("bowl") -> {
                topColor = AndroidColor.rgb(100, 200, 255)
                bottomColor = AndroidColor.rgb(20, 80, 160)
                titleText = "Desktop Bowl"
            }
            resName.contains("5g") -> {
                topColor = AndroidColor.rgb(80, 210, 180)
                bottomColor = AndroidColor.rgb(15, 70, 90)
                titleText = "5-Gal Planted"
            }
            resName.contains("10g") -> {
                topColor = AndroidColor.rgb(60, 190, 220)
                bottomColor = AndroidColor.rgb(10, 60, 110)
                titleText = "10-Gal Community"
            }
            resName.contains("40g") -> {
                topColor = AndroidColor.rgb(50, 160, 210)
                bottomColor = AndroidColor.rgb(10, 45, 95)
                titleText = "40-Gal Breeder"
            }
            resName.contains("100g") -> {
                topColor = AndroidColor.rgb(40, 130, 200)
                bottomColor = AndroidColor.rgb(5, 30, 80)
                titleText = "100-Gal Reef"
            }
            resName.contains("large") -> {
                topColor = AndroidColor.rgb(30, 100, 180)
                bottomColor = AndroidColor.rgb(5, 20, 65)
                titleText = "Gallery Exhibit"
            }
            resName.contains("mega") -> {
                topColor = AndroidColor.rgb(20, 70, 150)
                bottomColor = AndroidColor.rgb(2, 10, 45)
                titleText = "Oceanic Mega-Tank"
            }
            else -> {
                topColor = AndroidColor.rgb(50, 150, 220)
                bottomColor = AndroidColor.rgb(10, 40, 100)
                titleText = "Aquarium Tank"
            }
        }

        // Background water gradient
        val bgPaint = Paint().apply {
            shader = LinearGradient(
                0f, 0f, 0f, height.toFloat(),
                topColor, bottomColor,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // Substrate bottom bed
        val substratePaint = Paint().apply {
            color = AndroidColor.argb(200, 180, 140, 90)
            style = Paint.Style.FILL
        }
        val path = Path().apply {
            moveTo(0f, height * 0.88f)
            lineTo(width * 0.3f, height * 0.85f)
            lineTo(width * 0.7f, height * 0.89f)
            lineTo(width.toFloat(), height * 0.86f)
            lineTo(width.toFloat(), height.toFloat())
            lineTo(0f, height.toFloat())
            close()
        }
        canvas.drawPath(path, substratePaint)

        // Decorative water surface rays
        val rayPaint = Paint().apply {
            color = AndroidColor.argb(40, 255, 255, 255)
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height * 0.08f, rayPaint)

        // High-contrast label
        val textPaint = Paint().apply {
            color = AndroidColor.argb(180, 255, 255, 255)
            textSize = (height * 0.045f).coerceAtLeast(16f)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("$titleText (Procedural Asset)", width / 2f, height * 0.12f, textPaint)

        return bitmap
    }

    /**
     * Generates a high-contrast procedural 2D fish bitmap with distinct anatomical features.
     */
    private fun generateProceduralFishBitmap(resName: String, width: Int, height: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val bodyColor: Int
        val finColor: Int
        val stripeColor: Int
        val fishName: String

        when {
            resName.contains("betta") -> {
                bodyColor = AndroidColor.rgb(20, 80, 220)
                finColor = AndroidColor.rgb(220, 40, 90)
                stripeColor = AndroidColor.rgb(100, 200, 255)
                fishName = "Betta"
            }
            resName.contains("guppy") -> {
                bodyColor = AndroidColor.rgb(255, 140, 0)
                finColor = AndroidColor.rgb(0, 230, 180)
                stripeColor = AndroidColor.rgb(255, 230, 0)
                fishName = "Guppy"
            }
            resName.contains("tetra") -> {
                bodyColor = AndroidColor.rgb(0, 210, 255)
                finColor = AndroidColor.rgb(240, 30, 30)
                stripeColor = AndroidColor.rgb(255, 255, 255)
                fishName = "Tetra"
            }
            resName.contains("cory") -> {
                bodyColor = AndroidColor.rgb(230, 220, 190)
                finColor = AndroidColor.rgb(30, 30, 30)
                stripeColor = AndroidColor.rgb(60, 60, 60)
                fishName = "Cory"
            }
            resName.contains("angelfish") -> {
                bodyColor = AndroidColor.rgb(240, 240, 245)
                finColor = AndroidColor.rgb(200, 160, 20)
                stripeColor = AndroidColor.rgb(40, 40, 40)
                fishName = "Angelfish"
            }
            resName.contains("ram") -> {
                bodyColor = AndroidColor.rgb(230, 190, 40)
                finColor = AndroidColor.rgb(30, 180, 220)
                stripeColor = AndroidColor.rgb(220, 50, 120)
                fishName = "Ram"
            }
            resName.contains("discus") -> {
                bodyColor = AndroidColor.rgb(220, 50, 40)
                finColor = AndroidColor.rgb(40, 180, 200)
                stripeColor = AndroidColor.rgb(255, 220, 80)
                fishName = "Discus"
            }
            resName.contains("pleco") -> {
                bodyColor = AndroidColor.rgb(80, 60, 45)
                finColor = AndroidColor.rgb(140, 110, 70)
                stripeColor = AndroidColor.rgb(200, 170, 110)
                fishName = "Pleco"
            }
            resName.contains("tang") -> {
                bodyColor = AndroidColor.rgb(30, 100, 240)
                finColor = AndroidColor.rgb(255, 210, 0)
                stripeColor = AndroidColor.rgb(20, 20, 30)
                fishName = "Blue Tang"
            }
            resName.contains("clownfish") -> {
                bodyColor = AndroidColor.rgb(255, 110, 20)
                finColor = AndroidColor.rgb(20, 20, 20)
                stripeColor = AndroidColor.rgb(255, 255, 255)
                fishName = "Clownfish"
            }
            resName.contains("catfish") -> {
                bodyColor = AndroidColor.rgb(50, 50, 60)
                finColor = AndroidColor.rgb(220, 40, 30)
                stripeColor = AndroidColor.rgb(240, 230, 210)
                fishName = "Redtail"
            }
            resName.contains("arowana") -> {
                bodyColor = AndroidColor.rgb(210, 220, 230)
                finColor = AndroidColor.rgb(180, 200, 220)
                stripeColor = AndroidColor.rgb(255, 180, 100)
                fishName = "Arowana"
            }
            resName.contains("hammerhead") -> {
                bodyColor = AndroidColor.rgb(120, 135, 150)
                finColor = AndroidColor.rgb(80, 95, 110)
                stripeColor = AndroidColor.rgb(200, 210, 220)
                fishName = "Hammerhead"
            }
            resName.contains("manta") -> {
                bodyColor = AndroidColor.rgb(35, 45, 60)
                finColor = AndroidColor.rgb(230, 235, 245)
                stripeColor = AndroidColor.rgb(100, 120, 140)
                fishName = "Manta Ray"
            }
            else -> {
                bodyColor = AndroidColor.rgb(40, 160, 220)
                finColor = AndroidColor.rgb(255, 120, 40)
                stripeColor = AndroidColor.rgb(255, 255, 255)
                fishName = "Fish"
            }
        }

        val w = width.toFloat()
        val h = height.toFloat()
        val centerX = w * 0.45f
        val centerY = h * 0.5f

        // Tail fin
        val finPaint = Paint().apply {
            color = finColor
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        val tailPath = Path().apply {
            moveTo(w * 0.15f, centerY)
            lineTo(w * 0.02f, centerY - h * 0.28f)
            quadTo(w * 0.08f, centerY, w * 0.02f, centerY + h * 0.28f)
            close()
        }
        canvas.drawPath(tailPath, finPaint)

        // Main body oval / ellipse
        val bodyPaint = Paint().apply {
            color = bodyColor
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        val bodyPath = Path().apply {
            moveTo(w * 0.15f, centerY)
            quadTo(w * 0.45f, centerY - h * 0.32f, w * 0.85f, centerY)
            quadTo(w * 0.45f, centerY + h * 0.32f, w * 0.15f, centerY)
            close()
        }
        canvas.drawPath(bodyPath, bodyPaint)

        // Stripes / Decorative accent pattern
        val stripePaint = Paint().apply {
            color = stripeColor
            style = Paint.Style.STROKE
            strokeWidth = (h * 0.04f).coerceAtLeast(3f)
            isAntiAlias = true
        }
        canvas.drawLine(w * 0.40f, centerY - h * 0.22f, w * 0.40f, centerY + h * 0.22f, stripePaint)
        canvas.drawLine(w * 0.55f, centerY - h * 0.18f, w * 0.55f, centerY + h * 0.18f, stripePaint)

        // Eye
        val eyeBgPaint = Paint().apply {
            color = AndroidColor.WHITE
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        val pupilPaint = Paint().apply {
            color = AndroidColor.BLACK
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawCircle(w * 0.72f, centerY - h * 0.08f, h * 0.07f, eyeBgPaint)
        canvas.drawCircle(w * 0.74f, centerY - h * 0.08f, h * 0.035f, pupilPaint)

        // Text label on fish
        val textPaint = Paint().apply {
            color = AndroidColor.WHITE
            textSize = (h * 0.14f).coerceAtLeast(14f)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            setShadowLayer(3f, 1f, 1f, AndroidColor.BLACK)
        }
        canvas.drawText(fishName, centerX, centerY + h * 0.05f, textPaint)

        return bitmap
    }
}
