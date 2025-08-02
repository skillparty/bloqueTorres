package com.skillparty.towerblox.score;

import com.skillparty.towerblox.game.DifficultyLevel;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a high score entry with nickname, score, difficulty and date
 */
public class HighScore implements Comparable<HighScore>, Serializable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    private String nickname;
    private int score;
    private DifficultyLevel difficulty;
    private LocalDateTime date;

    /**
     * Creates a new high score entry
     * @param nickname Player nickname (max 3 characters)
     * @param score Player score
     * @param difficulty Game difficulty level
     */
    public HighScore(String nickname, int score, DifficultyLevel difficulty) {
        setNickname(nickname);
        this.score = score;
        this.difficulty = difficulty;
        this.date = LocalDateTime.now();
    }

    /**
     * Creates a high score entry with specific date (for loading from file)
     */
    public HighScore(String nickname, int score, DifficultyLevel difficulty, LocalDateTime date) {
        setNickname(nickname);
        this.score = score;
        this.difficulty = difficulty;
        this.date = date;
    }

    /**
     * Sets the nickname with validation
     * @param nickname Player nickname (will be truncated to 3 characters)
     */
    public void setNickname(String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            this.nickname = "???";
        } else {
            // Truncate to 3 characters and convert to uppercase
            String trimmed = nickname.trim().toUpperCase();
            this.nickname = trimmed.length() > 3 ? trimmed.substring(0, 3) : trimmed;
            
            // Pad with spaces if less than 3 characters
            while (this.nickname.length() < 3) {
                this.nickname += " ";
            }
        }
    }

    /**
     * Validates if a nickname is acceptable
     * @param nickname Nickname to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidNickname(String nickname) {
        if (nickname == null) return false;
        String trimmed = nickname.trim();
        return trimmed.length() >= 1 && trimmed.length() <= 3 && 
               trimmed.matches("[A-Za-z0-9]+"); // Only alphanumeric characters
    }

    /**
     * Formats nickname for display (removes trailing spaces)
     */
    public String getDisplayNickname() {
        return nickname.trim();
    }

    // Getters
    public String getNickname() {
        return nickname;
    }

    public int getScore() {
        return score;
    }

    public DifficultyLevel getDifficulty() {
        return difficulty;
    }

    public LocalDateTime getDate() {
        return date;
    }

    /**
     * Gets formatted date string
     */
    public String getFormattedDate() {
        return date.format(DATE_FORMATTER);
    }

    /**
     * Gets difficulty display name
     */
    public String getDifficultyName() {
        return difficulty.getDisplayName();
    }

    /**
     * Compares high scores for sorting (highest score first)
     */
    @Override
    public int compareTo(HighScore other) {
        // Primary sort: by score (descending)
        int scoreComparison = Integer.compare(other.score, this.score);
        if (scoreComparison != 0) {
            return scoreComparison;
        }
        
        // Secondary sort: by difficulty (harder difficulties first)
        int difficultyComparison = other.difficulty.compareTo(this.difficulty);
        if (difficultyComparison != 0) {
            return difficultyComparison;
        }
        
        // Tertiary sort: by date (newer first)
        return other.date.compareTo(this.date);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        HighScore highScore = (HighScore) obj;
        return score == highScore.score &&
               nickname.equals(highScore.nickname) &&
               difficulty == highScore.difficulty &&
               date.equals(highScore.date);
    }

    @Override
    public int hashCode() {
        int result = nickname.hashCode();
        result = 31 * result + score;
        result = 31 * result + difficulty.hashCode();
        result = 31 * result + date.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s - %d pts (%s) - %s", 
                           getDisplayNickname(), score, getDifficultyName(), getFormattedDate());
    }

    /**
     * Creates a formatted string for display in score table
     */
    public String toTableString(int rank) {
        return String.format("%2d. %-3s %8d %-8s %s", 
                           rank, getDisplayNickname(), score, getDifficultyName(), getFormattedDate());
    }
}