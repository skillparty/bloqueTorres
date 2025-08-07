package com.skillparty.towerblox.game;

import com.skillparty.towerblox.game.physics.Block;
import com.skillparty.towerblox.game.physics.Crane;
import com.skillparty.towerblox.game.physics.Tower;
import java.awt.Color;

/**
 * Professional gameplay enhancement system
 * Focuses on creating an engaging, balanced, and satisfying game experience
 */
public class GameplayEnhancer {
    
    // Gameplay balance constants
    private static final double PERFECT_ALIGNMENT_THRESHOLD = 0.85; // 85% overlap for perfect
    private static final double GOOD_ALIGNMENT_THRESHOLD = 0.65;    // 65% overlap for good
    private static final double ACCEPTABLE_THRESHOLD = 0.45;        // 45% overlap minimum
    
    // Scoring multipliers for engaging progression
    private static final int BASE_SCORE = 100;
    private static final double PERFECT_MULTIPLIER = 3.0;
    private static final double GOOD_MULTIPLIER = 2.0;
    private static final double HEIGHT_BONUS_MULTIPLIER = 0.1;
    private static final double COMBO_MULTIPLIER = 1.5;
    
    // Timing and flow constants
    private static final long PERFECT_TIMING_WINDOW = 150; // ms for perfect timing
    private static final long GOOD_TIMING_WINDOW = 300;    // ms for good timing
    
    // Game feel enhancements
    private static final double SCREEN_SHAKE_INTENSITY = 2.0;
    private static final long SCREEN_SHAKE_DURATION = 200;
    
    /**
     * Placement quality assessment for professional feedback
     */
    public enum PlacementQuality {
        PERFECT("PERFECT!", new Color(255, 215, 0), 3.0),
        EXCELLENT("EXCELLENT", new Color(50, 205, 50), 2.5),
        GOOD("GOOD", new Color(30, 144, 255), 2.0),
        OKAY("OKAY", new Color(255, 165, 0), 1.5),
        POOR("POOR", new Color(255, 69, 0), 1.0);
        
        public final String displayText;
        public final Color color;
        public final double scoreMultiplier;
        
        PlacementQuality(String displayText, Color color, double scoreMultiplier) {
            this.displayText = displayText;
            this.color = color;
            this.scoreMultiplier = scoreMultiplier;
        }
    }
    
    /**
     * Game state for enhanced feedback
     */
    public static class GameplayFeedback {
        public PlacementQuality quality;
        public int score;
        public boolean isCombo;
        public int comboCount;
        public boolean triggerScreenShake;
        public boolean perfectTiming;
        public double alignmentPercentage;
        public String feedbackMessage;
        
        public GameplayFeedback() {
            this.quality = PlacementQuality.POOR;
            this.score = 0;
            this.isCombo = false;
            this.comboCount = 0;
            this.triggerScreenShake = false;
            this.perfectTiming = false;
            this.alignmentPercentage = 0.0;
            this.feedbackMessage = "";
        }
    }
    
    private int consecutivePerfectPlacements = 0;
    private int consecutiveGoodPlacements = 0;
    private long lastPlacementTime = 0;
    private double totalAlignmentScore = 0;
    private int totalPlacements = 0;
    
    /**
     * Analyzes block placement and provides comprehensive feedback
     */
    public GameplayFeedback analyzeBlockPlacement(Block placedBlock, Block previousBlock, 
                                                 Tower tower, Crane crane, long placementTime) {
        GameplayFeedback feedback = new GameplayFeedback();
        
        // Calculate alignment quality
        double alignmentPercentage = calculateAlignment(placedBlock, previousBlock);
        feedback.alignmentPercentage = alignmentPercentage;
        
        // Determine placement quality
        feedback.quality = determinePlacementQuality(alignmentPercentage);
        
        // Calculate timing bonus
        boolean perfectTiming = calculateTimingBonus(placementTime);
        feedback.perfectTiming = perfectTiming;
        
        // Calculate base score
        int baseScore = calculateBaseScore(feedback.quality, tower.getHeight(), perfectTiming);
        
        // Apply combo system
        updateComboSystem(feedback.quality);
        feedback.isCombo = consecutivePerfectPlacements > 1 || consecutiveGoodPlacements > 2;
        feedback.comboCount = Math.max(consecutivePerfectPlacements, consecutiveGoodPlacements);
        
        // Apply combo multiplier
        if (feedback.isCombo) {
            baseScore = (int)(baseScore * (1.0 + (feedback.comboCount - 1) * 0.2));
        }
        
        feedback.score = baseScore;
        
        // Determine special effects
        feedback.triggerScreenShake = feedback.quality == PlacementQuality.PERFECT || 
                                     (feedback.isCombo && feedback.comboCount >= 5);
        
        // Generate feedback message
        feedback.feedbackMessage = generateFeedbackMessage(feedback);
        
        // Update statistics
        updateGameplayStats(alignmentPercentage);
        lastPlacementTime = placementTime;
        
        return feedback;
    }
    
    /**
     * Calculates precise alignment between two blocks
     */
    private double calculateAlignment(Block currentBlock, Block previousBlock) {
        if (previousBlock == null) {
            return 1.0; // First block is always perfect
        }
        
        double currentLeft = currentBlock.getX();
        double currentRight = currentBlock.getX() + currentBlock.getWidth();
        double previousLeft = previousBlock.getX();
        double previousRight = previousBlock.getX() + previousBlock.getWidth();
        
        // Calculate overlap
        double overlapLeft = Math.max(currentLeft, previousLeft);
        double overlapRight = Math.min(currentRight, previousRight);
        double overlapWidth = Math.max(0, overlapRight - overlapLeft);
        
        // Calculate alignment percentage based on smaller block
        double minWidth = Math.min(currentBlock.getWidth(), previousBlock.getWidth());
        return overlapWidth / minWidth;
    }
    
    /**
     * Determines placement quality based on alignment
     */
    private PlacementQuality determinePlacementQuality(double alignmentPercentage) {
        if (alignmentPercentage >= PERFECT_ALIGNMENT_THRESHOLD) {
            return PlacementQuality.PERFECT;
        } else if (alignmentPercentage >= 0.75) {
            return PlacementQuality.EXCELLENT;
        } else if (alignmentPercentage >= GOOD_ALIGNMENT_THRESHOLD) {
            return PlacementQuality.GOOD;
        } else if (alignmentPercentage >= ACCEPTABLE_THRESHOLD) {
            return PlacementQuality.OKAY;
        } else {
            return PlacementQuality.POOR;
        }
    }
    
    /**
     * Calculates timing bonus for responsive gameplay
     */
    private boolean calculateTimingBonus(long placementTime) {
        if (lastPlacementTime == 0) return false;
        
        long timeDifference = Math.abs(placementTime - lastPlacementTime);
        return timeDifference <= PERFECT_TIMING_WINDOW;
    }
    
    /**
     * Calculates base score with professional balancing
     */
    private int calculateBaseScore(PlacementQuality quality, int towerHeight, boolean perfectTiming) {
        int score = BASE_SCORE;
        
        // Apply quality multiplier
        score = (int)(score * quality.scoreMultiplier);
        
        // Height bonus for progression motivation
        score += (int)(towerHeight * HEIGHT_BONUS_MULTIPLIER * BASE_SCORE);
        
        // Perfect timing bonus
        if (perfectTiming) {
            score = (int)(score * 1.25);
        }
        
        return score;
    }
    
    /**
     * Updates combo system for engaging gameplay flow
     */
    private void updateComboSystem(PlacementQuality quality) {
        switch (quality) {
            case PERFECT:
                consecutivePerfectPlacements++;
                consecutiveGoodPlacements = 0;
                break;
            case EXCELLENT:
            case GOOD:
                consecutiveGoodPlacements++;
                if (consecutivePerfectPlacements > 0) {
                    consecutivePerfectPlacements = 0;
                }
                break;
            default:
                consecutivePerfectPlacements = 0;
                consecutiveGoodPlacements = 0;
                break;
        }
    }
    
    /**
     * Generates contextual feedback messages
     */
    private String generateFeedbackMessage(GameplayFeedback feedback) {
        StringBuilder message = new StringBuilder();
        
        message.append(feedback.quality.displayText);
        
        if (feedback.perfectTiming) {
            message.append(" + PERFECT TIMING!");
        }
        
        if (feedback.isCombo) {
            message.append(" COMBO x").append(feedback.comboCount).append("!");
        }
        
        return message.toString();
    }
    
    /**
     * Updates gameplay statistics for adaptive difficulty
     */
    private void updateGameplayStats(double alignmentPercentage) {
        totalAlignmentScore += alignmentPercentage;
        totalPlacements++;
    }
    
    /**
     * Gets player skill level for adaptive features
     */
    public double getPlayerSkillLevel() {
        if (totalPlacements == 0) return 0.5; // Default skill level
        
        double averageAlignment = totalAlignmentScore / totalPlacements;
        double comboFactor = Math.min(1.0, (consecutivePerfectPlacements + consecutiveGoodPlacements) / 10.0);
        
        return Math.min(1.0, (averageAlignment * 0.7) + (comboFactor * 0.3));
    }
    
    /**
     * Suggests dynamic difficulty adjustment
     */
    public double suggestSpeedMultiplier(double currentSpeed, int towerHeight) {
        double skillLevel = getPlayerSkillLevel();
        double baseMultiplier = 1.0 + (towerHeight * 0.02); // 2% increase per level
        
        // Adjust based on player skill
        if (skillLevel > 0.8) {
            baseMultiplier *= 1.2; // Increase challenge for skilled players
        } else if (skillLevel < 0.4) {
            baseMultiplier *= 0.8; // Reduce challenge for struggling players
        }
        
        return baseMultiplier;
    }
    
    /**
     * Resets combo system for new game
     */
    public void reset() {
        consecutivePerfectPlacements = 0;
        consecutiveGoodPlacements = 0;
        lastPlacementTime = 0;
        totalAlignmentScore = 0;
        totalPlacements = 0;
    }
    
    // Getters for UI feedback
    public int getConsecutivePerfectPlacements() { return consecutivePerfectPlacements; }
    public int getConsecutiveGoodPlacements() { return consecutiveGoodPlacements; }
    public double getAverageAlignment() { 
        return totalPlacements > 0 ? totalAlignmentScore / totalPlacements : 0.0; 
    }
}