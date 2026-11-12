package com.wcsurface.bowltoocean

import com.wcsurface.bowltoocean.audio.AquariumAudioManager
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AquariumAudioTest {

    @Test
    fun testAudioMutingToggles() {
        val audioManager = AquariumAudioManager()

        assertTrue(audioManager.soundEnabled)
        assertTrue(audioManager.hapticsEnabled)

        audioManager.soundEnabled = false
        assertFalse(audioManager.soundEnabled)

        audioManager.hapticsEnabled = false
        assertFalse(audioManager.hapticsEnabled)

        audioManager.release()
    }
}
