package com.wcsurface.bowltoocean.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color as AndroidColor
import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.util.concurrent.ConcurrentHashMap

/**
 * AssetResolver loads real Android drawables directly via BitmapFactory.decodeResource
 * and strips near-white background pixels from fish textures.
 */
object AssetResolver {

    private val bitmapCache = ConcurrentHashMap<Int, ImageBitmap>()
    private val nameCache = ConcurrentHashMap<String, ImageBitmap>()

    /**
     * Resolves an ImageBitmap directly by integer DrawableRes ID.
     */
    fun getBitmapByResId(context: Context, @DrawableRes resId: Int, isFish: Boolean = false): ImageBitmap {
        bitmapCache[resId]?.let { return it }

        val opts = BitmapFactory.Options().apply {
            inMutable = true
            inPreferredConfig = Bitmap.Config.ARGB_8888
            inScaled = false
        }

        val decoded = BitmapFactory.decodeResource(context.resources, resId, opts)
            ?: Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888)

        val processed = if (isFish) {
            stripWhiteBackground(decoded)
        } else {
            decoded
        }

        val imageBitmap = processed.asImageBitmap()
        bitmapCache[resId] = imageBitmap
        return imageBitmap
    }

    /**
     * Resolves an ImageBitmap by string resource name.
     */
    fun getBitmap(context: Context, resName: String, width: Int = 256, height: Int = 256): ImageBitmap {
        nameCache[resName]?.let { return it }

        val resId = context.resources.getIdentifier(resName, "drawable", context.packageName)
        if (resId != 0) {
            val isFish = resName.contains("fish")
            val bitmap = getBitmapByResId(context, resId, isFish)
            nameCache[resName] = bitmap
            return bitmap
        }

        val emptyBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).asImageBitmap()
        nameCache[resName] = emptyBitmap
        return emptyBitmap
    }

    /**
     * Loops through bitmap pixels and sets any near-white pixel
     * (red > 235 && green > 235 && blue > 235) to Color.TRANSPARENT.
     */
    private fun stripWhiteBackground(source: Bitmap): Bitmap {
        val w = source.width
        val h = source.height
        val pixels = IntArray(w * h)
        source.getPixels(pixels, 0, w, 0, 0, w, h)

        var modified = false
        for (i in pixels.indices) {
            val px = pixels[i]
            val alpha = AndroidColor.alpha(px)
            val red = AndroidColor.red(px)
            val green = AndroidColor.green(px)
            val blue = AndroidColor.blue(px)

            if (alpha > 0 && red > 235 && green > 235 && blue > 235) {
                pixels[i] = AndroidColor.TRANSPARENT
                modified = true
            }
        }

        return if (modified) {
            val result = if (source.isMutable) source else source.copy(Bitmap.Config.ARGB_8888, true)
            result.setPixels(pixels, 0, w, 0, 0, w, h)
            result
        } else {
            source
        }
    }

    fun clearCache() {
        bitmapCache.clear()
        nameCache.clear()
    }
}
