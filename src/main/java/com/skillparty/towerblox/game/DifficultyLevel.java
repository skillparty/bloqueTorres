package com.skillparty.towerblox.game;

/**
 * Enum representing different difficulty levels in Tower Bloxx
 * Each difficulty has a different speed multiplier affecting game speed
 */
public enum DifficultyLevel {
    EASY("Fácil", 0.7),
    NORMAL("Normal", 1.0),
    HARD("Difícil", 1.3),
    PROFESSIONAL("Profesional", 1.5);

    private final String displayName;
    private final double speedMultiplier;

    DifficultyLevel(String displayName, double speedMultiplier) {
        this.displayName = displayName;
        this.speedMultiplier = speedMultiplier;
    }

    /**
     * Gets the display name for this difficulty level
     * @return The localized display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the speed multiplier for this difficulty level
     * @return Speed multiplier (0.7 for easy, 1.0 for normal, 1.3 for hard, 1.5 for professional)
     */
    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    /**
     * Gets the score multiplier for this difficulty level
     * @return Score multiplier based on difficulty
     */
    public double getScoreMultiplier() {
        switch (this) {
            case EASY:
                return 1.0;
            case NORMAL:
                return 1.5;
            case HARD:
                return 2.0;
            case PROFESSIONAL:
                return 3.0; // El más alto multiplicador para el modo profesional
            default:
                return 1.0;
        }
    }

    @Override
    public String toString() {
        return displayName;
    }
}