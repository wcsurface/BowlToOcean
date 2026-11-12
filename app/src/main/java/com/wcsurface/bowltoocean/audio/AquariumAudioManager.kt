package com.wcsurface.bowltoocean.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import java.util.concurrent.Executors
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

/**
 * AquariumAudioManager provides zero-asset procedural audio synthesis in memory via AudioTrack PCM.
 * Requires zero external MP3/WAV files.
 */
class AquariumAudioManager {

    var soundEnabled: Boolean = true
    var hapticsEnabled: Boolean = true

    private val sampleRate = 44100
    private val executor = Executors.newSingleThreadExecutor()

    private val glassTapBuffer: ShortArray
    private val bubblePopBuffer: ShortArray
    private val swooshBuffer: ShortArray
    private val rewardChimeBuffer: ShortArray

    init {
        glassTapBuffer = generateGlassTapPcm()
        bubblePopBuffer = generateBubblePopPcm()
        swooshBuffer = generateSwooshPcm()
        rewardChimeBuffer = generateRewardChimePcm()
    }

    private fun generateGlassTapPcm(): ShortArray {
        val durationMs = 140
        val numSamples = (sampleRate * durationMs) / 1000
        val pcm = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val decay = exp(-28.0 * t)
            val fundamental = sin(2.0 * PI * 140.0 * t) * 0.7
            val harmonic = sin(2.0 * PI * 420.0 * t) * 0.3 * exp(-60.0 * t)
            val sample = (fundamental + harmonic) * decay * 30000.0
            pcm[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return pcm
    }

    private fun generateBubblePopPcm(): ShortArray {
        val durationMs = 60
        val numSamples = (sampleRate * durationMs) / 1000
        val pcm = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val progress = i.toDouble() / numSamples
            val freq = 450.0 + progress * 450.0 // Chirp 450Hz to 900Hz
            val decay = exp(-35.0 * t)
            val sample = sin(2.0 * PI * freq * t) * decay * 26000.0
            pcm[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return pcm
    }

    private fun generateSwooshPcm(): ShortArray {
        val durationMs = 260
        val numSamples = (sampleRate * durationMs) / 1000
        val pcm = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val bellEnvelope = sin(PI * progress) // Soft bell curve envelope
            val noise = (Random.nextDouble() * 2.0 - 1.0) * 0.4
            val lowpassSine = sin(2.0 * PI * 180.0 * (i.toDouble() / sampleRate)) * 0.6
            val sample = (noise + lowpassSine) * bellEnvelope * 22000.0
            pcm[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return pcm
    }

    private fun generateRewardChimePcm(): ShortArray {
        val durationMs = 420
        val numSamples = (sampleRate * durationMs) / 1000
        val pcm = ShortArray(numSamples)
        val freqs = doubleArrayOf(523.25, 659.25, 783.99) // C5, E5, G5 triad
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val progress = i.toDouble() / numSamples
            val decay = exp(-7.0 * progress)
            var sampleSum = 0.0
            for ((idx, freq) in freqs.withIndex()) {
                val offsetSec = idx * 0.08
                if (t >= offsetSec) {
                    val subT = t - offsetSec
                    sampleSum += sin(2.0 * PI * freq * subT) * exp(-10.0 * subT)
                }
            }
            val sample = sampleSum * decay * 14000.0
            pcm[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return pcm
    }

    fun playGlassTap() {
        if (!soundEnabled) return
        playBuffer(glassTapBuffer)
    }

    fun playBubblePop() {
        if (!soundEnabled) return
        playBuffer(bubblePopBuffer)
    }

    fun playMaintenanceSwoosh() {
        if (!soundEnabled) return
        playBuffer(swooshBuffer)
    }

    fun playRewardChime() {
        if (!soundEnabled) return
        playBuffer(rewardChimeBuffer)
    }

    private fun playBuffer(pcm: ShortArray) {
        executor.execute {
            try {
                val minBufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
                val bufferSize = pcm.size * 2
                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize.coerceAtLeast(minBufferSize))
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(pcm, 0, pcm.size)
                track.play()
                Thread.sleep((pcm.size * 1000L / sampleRate) + 50)
                track.stop()
                track.release()
            } catch (_: Exception) {
            }
        }
    }

    fun release() {
        executor.shutdown()
    }
}
