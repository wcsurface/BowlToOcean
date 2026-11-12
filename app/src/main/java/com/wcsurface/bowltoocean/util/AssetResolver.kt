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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.util.concurrent.ConcurrentHashMap

/**
 * AssetResolver handles direct loading of game drawables and strips white background pixels from fish textures.
 * Uses zero placeholder debug text overlays.
 */
object AssetResolver {

    private val bitmapCache = ConcurrentHashMap<String, ImageBitmap>()

    fun getBitmap(context: Context, resName: String, width: Int = 256, height: Int = 256): ImageBitmap {
        val cacheKey = "${resName}_${width}x${height}"
        bitmapCache[cacheKey]?.let { return it }

        // 1. Direct Resource Identifier Resolution
        val resId = context.resources.getIdentifier(resName, "drawable", context.packageName)
        if (resId != 0) {
            try {
                val opts = BitmapFactory.Options().apply {
                    inScaled = false
                }
                val rawBitmap = BitmapFactory.decodeResource(context.resources, resId, opts)
                if (rawBitmap != null) {
                    val processedBitmap = if (resName.contains("fish")) {
                        stripWhiteBackground(rawBitmap)
                    } else {
                        rawBitmap
                    }
                    val imageBitmap = processedBitmap.asImageBitmap()
                    bitmapCache[cacheKey] = imageBitmap
                    return imageBitmap
                }
            } catch (_: Exception) {
            }
        }

        // 2. High-Quality Procedural Artwork Fallback (without debug text)
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
     * Converts pure-white background pixels (R > 240, G > 240, B > 240) to transparent alpha = 0.
     */
    private fun stripWhiteBackground(source: Bitmap): Bitmap {
        val w = source.width
        val h = source.height
        val pixels = IntArray(w * h)
        source.getPixels(pixels, 0, w, 0, 0, w, h)

        var modified = false
        for (i in pixels.indices) {
            val color = pixels[i]
            val alpha = (color shr 24) and 0xFF
            val red = (color shr 16) and 0xFF
            val green = (color shr 8) and 0xFF
            val blue = color and 0xFF

            if (alpha > 0 && red > 240 && green > 240 && blue > 240) {
                pixels[i] = 0
                modified = true
            }
        }

        return if (modified) {
            val result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            result.setPixels(pixels, 0, w, 0, 0, w, h)
            result
        } else {
            source
        }
    }

    fun clearCache() {
        bitmapCache.clear()
    }

    private fun generateProceduralTankBg(resName: String, width: Int, height: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val topColor: Int
        val bottomColor: Int

        when {
            resName.contains("bowl") -> {
                topColor = AndroidColor.rgb(80, 200, 255)
                bottomColor = AndroidColor.rgb(12, 50, 110)
            }
            resName.contains("5g") -> {
                topColor = AndroidColor.rgb(60, 210, 180)
                bottomColor = AndroidColor.rgb(10, 60, 80)
            }
            resName.contains("10g") -> {
                topColor = AndroidColor.rgb(50, 180, 220)
                bottomColor = AndroidColor.rgb(8, 50, 100)
            }
            resName.contains("40g") -> {
                topColor = AndroidColor.rgb(40, 150, 210)
                bottomColor = AndroidColor.rgb(8, 40, 85)
            }
            resName.contains("100g") -> {
                topColor = AndroidColor.rgb(30, 120, 200)
                bottomColor = AndroidColor.rgb(5, 25, 70)
            }
            resName.contains("large") -> {
                topColor = AndroidColor.rgb(20, 90, 180)
                bottomColor = AndroidColor.rgb(5, 18, 55)
            }
            resName.contains("mega") -> {
                topColor = AndroidColor.rgb(15, 60, 150)
                bottomColor = AndroidColor.rgb(2, 10, 40)
            }
            else -> {
                topColor = AndroidColor.rgb(40, 140, 220)
                bottomColor = AndroidColor.rgb(10, 35, 90)
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

        // Substrate bed
        val substratePaint = Paint().apply {
            color = AndroidColor.argb(220, 185, 150, 100)
            style = Paint.Style.FILL
        }
        val path = Path().apply {
            moveTo(0f, height * 0.86f)
            lineTo(width * 0.35f, height * 0.82f)
            lineTo(width * 0.68f, height * 0.87f)
            lineTo(width.toFloat(), height * 0.83f)
            lineTo(width.toFloat(), height.toFloat())
            lineTo(0f, height.toFloat())
            close()
        }
        canvas.drawPath(path, substratePaint)

        // Water surface light rays
        val rayPaint = Paint().apply {
            color = AndroidColor.argb(35, 255, 255, 255)
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height * 0.10f, rayPaint)

        return bitmap
    }

    private fun generateProceduralFishBitmap(resName: String, width: Int, height: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val bodyColor = AndroidColor.rgb(25, 100, 240)
        val finColor = AndroidColor.rgb(230, 240, 255)

        val w = width.toFloat()
        val h = height.toFloat()
        val centerY = h * 0.5f

        // Flowing Tail fin
        val finPaint = Paint().apply {
            color = finColor
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        val tailPath = Path().apply {
            moveTo(w * 0.18f, centerY)
            lineTo(w * 0.02f, centerY - h * 0.35f)
            quadTo(w * 0.10f, centerY, w * 0.02f, centerY + h * 0.35f)
            close()
        }
        canvas.drawPath(tailPath, finPaint)

        // Main body
        val bodyPaint = Paint().apply {
            color = bodyColor
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        val bodyPath = Path().apply {
            moveTo(w * 0.18f, centerY)
            quadTo(w * 0.50f, centerY - h * 0.30f, w * 0.92f, centerY)
            quadTo(w * 0.50f, centerY + h * 0.30f, w * 0.18f, centerY)
            close()
        }
        canvas.drawPath(bodyPath, bodyPaint)

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
        canvas.drawCircle(w * 0.75f, centerY - h * 0.08f, h * 0.065f, eyeBgPaint)
        canvas.drawCircle(w * 0.77f, centerY - h * 0.08f, h * 0.032f, pupilPaint)

        return bitmap
    }
}
