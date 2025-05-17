package com.gluonapplication;  // Package for utility classes

import javafx.scene.media.AudioClip;

public class SoundManager {
    // Static instance - loaded once when class is initialized
    private static final AudioClip CLICK_SOUND = new AudioClip(
            SoundManager.class.getResource("/sounds/toy-button-105724.mp3").toString());

    static {
        // Optional: Pre-configure sound settings
        CLICK_SOUND.setVolume(0.7);  // 70% volume
    }

    public static void playClickSound() {
        if (!CLICK_SOUND.isPlaying()) {  // Prevent sound overlapping
            CLICK_SOUND.play();
        }
    }
}