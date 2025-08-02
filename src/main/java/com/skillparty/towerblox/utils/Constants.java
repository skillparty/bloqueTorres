package com.skillparty.towerblox.utils;

/**
 * Game constants and configuration values
 */
public final class Constants {
    
    // Game dimensions
    public static final int GAME_WIDTH = 800;
    public static final int GAME_HEIGHT = 600;
    public static final int GROUND_LEVEL = GAME_HEIGHT - 50;
    
    // Performance settings
    public static final int TARGET_FPS = 60;
    public static final long FRAME_TIME = 1000 / TARGET_FPS;
    
    // Block settings
    public static final int BLOCK_MIN_WIDTH = 60;
    public static final int BLOCK_MAX_WIDTH = 100;
    public static final int BLOCK_MIN_HEIGHT = 25;
    public static final int BLOCK_MAX_HEIGHT = 40;
    
    // Crane settings
    public static final double CRANE_BASE_SPEED = 2.0;
    public static final int CRANE_START_Y = 50;
    public static final int CRANE_HEIGHT = 40;
    public static final int CRANE_HOOK_LENGTH = 20;
    
    // Physics constants
    public static final double GRAVITY = 0.5;
    public static final double FRICTION = 0.98;
    public static final double MIN_VELOCITY = 0.1;
    public static final double STABILITY_THRESHOLD = 0.3; // 30% overlap required for stability
    
    // Scoring constants
    public static final int BASE_BLOCK_POINTS = 100;
    public static final int PERFECT_ALIGNMENT_BONUS = 500;
    public static final int HEIGHT_BONUS_MULTIPLIER = 50;
    public static final int PERFECT_ALIGNMENT_THRESHOLD = 95;
    public static final int COMBO_MULTIPLIER = 2;
    public static final int MAX_COMBO = 5;
    
    // Achievement thresholds
    public static final int[] SCORE_MILESTONES = {1000, 5000, 10000, 25000, 50000, 100000};
    public static final int[] HEIGHT_ACHIEVEMENTS = {10, 20, 30, 50};
    public static final int[] HEIGHT_BONUSES = {1000, 2000, 5000, 10000};
    public static final int PERFECT_GAME_BONUS = 5000;
    
    // Difficulty multipliers (already defined in DifficultyLevel enum, but here for reference)
    public static final double EASY_SPEED_MULTIPLIER = 0.7;
    public static final double NORMAL_SPEED_MULTIPLIER = 1.0;
    public static final double HARD_SPEED_MULTIPLIER = 1.3;
    
    public static final double EASY_SCORE_MULTIPLIER = 1.0;
    public static final double NORMAL_SCORE_MULTIPLIER = 1.5;
    public static final double HARD_SCORE_MULTIPLIER = 2.0;
    
    // UI Constants
    public static final int MAX_HIGH_SCORES = 10;
    public static final int NICKNAME_MAX_LENGTH = 3;
    
    // File paths
    public static final String DATA_DIR = "data";
    public static final String SCORES_FILE = "highscores.dat";
    public static final String BACKUP_FILE = "highscores.backup";
    public static final String FONT_PATH = "/fonts/UbuntuMono-Regular.ttf";
    
    // Colors (RGB values)
    public static final int SKY_BLUE_R = 135;
    public static final int SKY_BLUE_G = 206;
    public static final int SKY_BLUE_B = 235;
    
    public static final int GROUND_GREEN_R = 34;
    public static final int GROUND_GREEN_G = 139;
    public static final int GROUND_GREEN_B = 34;
    
    public static final int TERMINAL_GREEN_R = 0;
    public static final int TERMINAL_GREEN_G = 255;
    public static final int TERMINAL_GREEN_B = 0;
    
    // Animation settings
    public static final int CRANE_ANIMATION_FRAMES = 5;
    public static final long CRANE_ANIMATION_FRAME_TIME = 20; // milliseconds per frame
    
    // Game balance settings
    public static final double TOWER_INSTABILITY_THRESHOLD = 0.5; // 50% misalignment causes instability
    public static final int MIN_BLOCKS_FOR_INSTABILITY = 3; // Need at least 3 blocks before instability kicks in
    
    private Constants() {
        // Prevent instantiation
    }
    
    /**
     * Gets the appropriate speed multiplier for a difficulty level
     */
    public static double getSpeedMultiplier(String difficulty) {
        switch (difficulty.toUpperCase()) {
            case "EASY":
                return EASY_SPEED_MULTIPLIER;
            case "NORMAL":
                return NORMAL_SPEED_MULTIPLIER;
            case "HARD":
                return HARD_SPEED_MULTIPLIER;
            default:
                return NORMAL_SPEED_MULTIPLIER;
        }
    }
    
    /**
     * Gets the appropriate score multiplier for a difficulty level
     */
    public static double getScoreMultiplier(String difficulty) {
        switch (difficulty.toUpperCase()) {
            case "EASY":
                return EASY_SCORE_MULTIPLIER;
            case "NORMAL":
                return NORMAL_SCORE_MULTIPLIER;
            case "HARD":
                return HARD_SCORE_MULTIPLIER;
            default:
                return NORMAL_SCORE_MULTIPLIER;
        }
    }
    
    /**
     * Gets the next score milestone after the given score
     */
    public static int getNextMilestone(int currentScore) {
        for (int milestone : SCORE_MILESTONES) {
            if (currentScore < milestone) {
                return milestone;
            }
        }
        // If past all milestones, return next 100k increment
        return ((currentScore / 100000) + 1) * 100000;
    }
    
    /**
     * Gets the height bonus for a given tower height
     */
    public static int getHeightBonus(int height) {
        for (int i = HEIGHT_ACHIEVEMENTS.length - 1; i >= 0; i--) {
            if (height >= HEIGHT_ACHIEVEMENTS[i]) {
                return HEIGHT_BONUSES[i];
            }
        }
        return 0;
    }
}