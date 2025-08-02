package com.skillparty.towerblox.score;

import com.skillparty.towerblox.game.DifficultyLevel;
import com.skillparty.towerblox.game.physics.Block;

/**
 * Manages score calculation and tracking during gameplay
 */
public class ScoreManager {
    private int currentScore;
    private int blocksPlaced;
    private int perfectAlignments;
    private int towerHeight;
    private DifficultyLevel difficulty;
    
    // Scoring constants
    private static final int BASE_BLOCK_POINTS = 100;
    private static final int PERFECT_ALIGNMENT_BONUS = 500;
    private static final int HEIGHT_BONUS_MULTIPLIER = 50;
    private static final int PERFECT_ALIGNMENT_THRESHOLD = 95; // 95% alignment or better
    private static final int COMBO_MULTIPLIER = 2;
    private static final int MAX_COMBO = 5;
    
    private int currentCombo = 0;

    /**
     * Creates a new score manager for the specified difficulty
     */
    public ScoreManager(DifficultyLevel difficulty) {
        this.difficulty = difficulty;
        reset();
    }

    /**
     * Resets the score manager for a new game
     */
    public void reset() {
        currentScore = 0;
        blocksPlaced = 0;
        perfectAlignments = 0;
        towerHeight = 0;
        currentCombo = 0;
    }

    /**
     * Calculates and adds points for placing a block
     * @param block The block that was placed
     * @param blockBelow The block below (null if first block)
     * @return Points earned for this block
     */
    public int addBlockScore(Block block, Block blockBelow) {
        blocksPlaced++;
        towerHeight++;
        
        int points = 0;
        
        // Base points for placing a block
        points += BASE_BLOCK_POINTS;
        
        // Alignment bonus
        int alignmentScore = block.getAlignmentScore(blockBelow);
        boolean isPerfectAlignment = alignmentScore >= PERFECT_ALIGNMENT_THRESHOLD;
        
        if (isPerfectAlignment) {
            points += PERFECT_ALIGNMENT_BONUS;
            perfectAlignments++;
            currentCombo = Math.min(currentCombo + 1, MAX_COMBO);
        } else {
            // Partial alignment bonus
            points += (alignmentScore * 2); // Up to 200 bonus points for good alignment
            currentCombo = 0; // Reset combo
        }
        
        // Combo multiplier
        if (currentCombo > 1) {
            points += (points * currentCombo * COMBO_MULTIPLIER / 10); // 20%, 40%, 60%, 80%, 100% bonus
        }
        
        // Height bonus (increases with tower height)
        points += towerHeight * HEIGHT_BONUS_MULTIPLIER;
        
        // Apply difficulty multiplier
        points = (int)(points * difficulty.getScoreMultiplier());
        
        currentScore += points;
        return points;
    }

    /**
     * Adds bonus points for special achievements
     */
    public void addBonusPoints(int points) {
        currentScore += (int)(points * difficulty.getScoreMultiplier());
    }

    /**
     * Calculates final score with end-game bonuses
     */
    public int calculateFinalScore() {
        int finalScore = currentScore;
        
        // Perfect game bonus (all blocks perfectly aligned)
        if (blocksPlaced > 0 && perfectAlignments == blocksPlaced) {
            finalScore += 5000 * (int)difficulty.getScoreMultiplier();
        }
        
        // Height achievement bonuses
        if (towerHeight >= 50) {
            finalScore += 10000;
        } else if (towerHeight >= 30) {
            finalScore += 5000;
        } else if (towerHeight >= 20) {
            finalScore += 2000;
        } else if (towerHeight >= 10) {
            finalScore += 1000;
        }
        
        return finalScore;
    }

    /**
     * Gets the accuracy percentage (perfect alignments / total blocks)
     */
    public double getAccuracyPercentage() {
        if (blocksPlaced == 0) return 0.0;
        return (double) perfectAlignments / blocksPlaced * 100.0;
    }

    /**
     * Gets a grade based on performance
     */
    public String getPerformanceGrade() {
        double accuracy = getAccuracyPercentage();
        
        if (accuracy >= 90) return "S";
        if (accuracy >= 80) return "A";
        if (accuracy >= 70) return "B";
        if (accuracy >= 60) return "C";
        if (accuracy >= 50) return "D";
        return "F";
    }

    /**
     * Creates a game statistics summary
     */
    public String getGameSummary() {
        return String.format(
            "Blocks: %d | Height: %d | Perfect: %d | Accuracy: %.1f%% | Grade: %s",
            blocksPlaced, towerHeight, perfectAlignments, getAccuracyPercentage(), getPerformanceGrade()
        );
    }

    // Getters
    public int getCurrentScore() {
        return currentScore;
    }

    public int getBlocksPlaced() {
        return blocksPlaced;
    }

    public int getPerfectAlignments() {
        return perfectAlignments;
    }

    public int getTowerHeight() {
        return towerHeight;
    }

    public DifficultyLevel getDifficulty() {
        return difficulty;
    }

    public int getCurrentCombo() {
        return currentCombo;
    }

    /**
     * Sets the difficulty (useful for changing mid-game if needed)
     */
    public void setDifficulty(DifficultyLevel difficulty) {
        this.difficulty = difficulty;
    }

    /**
     * Gets the next milestone score for achievements
     */
    public int getNextMilestone() {
        int[] milestones = {1000, 5000, 10000, 25000, 50000, 100000};
        
        for (int milestone : milestones) {
            if (currentScore < milestone) {
                return milestone;
            }
        }
        
        // If past all milestones, return next 100k increment
        return ((currentScore / 100000) + 1) * 100000;
    }
}