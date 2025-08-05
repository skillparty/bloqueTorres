package com.skillparty.towerblox.audio;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages sound effects and audio playback for the game
 */
public class SoundManager {
    private Map<SoundType, AudioClip> soundEffects;
    private float masterVolume = 0.7f;
    private boolean soundEnabled = true;
    
    public enum SoundType {
        BLOCK_DROP,
        BLOCK_LAND,
        PERFECT_PLACEMENT,
        MILESTONE_REACHED,
        GAME_OVER,
        MENU_SELECT
    }
    
    /**
     * Audio clip wrapper for sound effects
     */
    private static class AudioClip {
        private Clip clip;
        private FloatControl volumeControl;
        
        public AudioClip(Clip clip) {
            this.clip = clip;
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                this.volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            }
        }
        
        public void play(float volume) {
            if (clip != null) {
                clip.setFramePosition(0);
                if (volumeControl != null) {
                    float gain = (float) (Math.log(Math.max(0.01f, volume)) / Math.log(10.0) * 20.0);
                    gain = Math.max(volumeControl.getMinimum(), Math.min(gain, volumeControl.getMaximum()));
                    volumeControl.setValue(gain);
                }
                clip.start();
            }
        }
        
        public void stop() {
            if (clip != null && clip.isRunning()) {
                clip.stop();
            }
        }
    }
    
    public SoundManager() {
        this.soundEffects = new HashMap<>();
        loadSoundEffects();
    }
    
    /**
     * Loads all sound effects
     */
    private void loadSoundEffects() {
        try {
            // Create simple synthetic sounds since we don't have audio files
            loadSyntheticSound(SoundType.BLOCK_DROP, 200, 100); // Low tone for drop
            loadSyntheticSound(SoundType.BLOCK_LAND, 400, 150); // Higher tone for land
            loadSyntheticSound(SoundType.PERFECT_PLACEMENT, 800, 200); // High tone for perfect
            loadSyntheticSound(SoundType.MILESTONE_REACHED, 600, 300); // Celebration tone
            loadSyntheticSound(SoundType.GAME_OVER, 150, 500); // Low tone for game over
            loadSyntheticSound(SoundType.MENU_SELECT, 500, 100); // Menu selection
            
            System.out.println("Sound effects loaded successfully");
        } catch (Exception e) {
            System.err.println("Failed to load sound effects: " + e.getMessage());
            soundEnabled = false;
        }
    }
    
    /**
     * Creates a synthetic sound effect
     */
    private void loadSyntheticSound(SoundType type, int frequency, int duration) {
        try {
            int sampleRate = 44100;
            int samples = (sampleRate * duration) / 1000;
            byte[] buffer = new byte[samples * 2];
            
            for (int i = 0; i < samples; i++) {
                double angle = 2.0 * Math.PI * i * frequency / sampleRate;
                short sample = (short) (Math.sin(angle) * 32767 * 0.3); // 30% volume
                
                // Apply envelope to avoid clicks
                float envelope = 1.0f;
                if (i < samples * 0.1) {
                    envelope = (float) i / (samples * 0.1f);
                } else if (i > samples * 0.9) {
                    envelope = (float) (samples - i) / (samples * 0.1f);
                }
                sample = (short) (sample * envelope);
                
                buffer[i * 2] = (byte) (sample & 0xFF);
                buffer[i * 2 + 1] = (byte) ((sample >> 8) & 0xFF);
            }
            
            AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(format, buffer, 0, buffer.length);
            
            soundEffects.put(type, new AudioClip(clip));
        } catch (LineUnavailableException e) {
            System.err.println("Failed to create synthetic sound for " + type + ": " + e.getMessage());
        }
    }
    
    /**
     * Plays a sound effect
     */
    public void playSound(SoundType type) {
        playSound(type, 1.0f);
    }
    
    /**
     * Plays a sound effect with specified volume
     */
    public void playSound(SoundType type, float volume) {
        if (!soundEnabled || !soundEffects.containsKey(type)) {
            return;
        }
        
        try {
            AudioClip audioClip = soundEffects.get(type);
            if (audioClip != null) {
                audioClip.play(volume * masterVolume);
            }
        } catch (Exception e) {
            System.err.println("Error playing sound " + type + ": " + e.getMessage());
        }
    }
    
    /**
     * Sets the master volume (0.0 to 1.0)
     */
    public void setMasterVolume(float volume) {
        this.masterVolume = Math.max(0.0f, Math.min(1.0f, volume));
    }
    
    /**
     * Gets the master volume
     */
    public float getMasterVolume() {
        return masterVolume;
    }
    
    /**
     * Enables or disables sound
     */
    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
        if (!enabled) {
            stopAllSounds();
        }
    }
    
    /**
     * Checks if sound is enabled
     */
    public boolean isSoundEnabled() {
        return soundEnabled;
    }
    
    /**
     * Stops all currently playing sounds
     */
    public void stopAllSounds() {
        for (AudioClip clip : soundEffects.values()) {
            if (clip != null) {
                clip.stop();
            }
        }
    }
    
    /**
     * Cleanup resources
     */
    public void dispose() {
        stopAllSounds();
        for (AudioClip clip : soundEffects.values()) {
            if (clip != null && clip.clip != null) {
                clip.clip.close();
            }
        }
        soundEffects.clear();
    }
}