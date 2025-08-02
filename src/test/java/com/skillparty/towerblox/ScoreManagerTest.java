package com.skillparty.towerblox;

import com.skillparty.towerblox.game.DifficultyLevel;
import com.skillparty.towerblox.game.physics.Block;
import com.skillparty.towerblox.score.ScoreManager;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.awt.Color;

/**
 * Unit tests for ScoreManager class
 */
public class ScoreManagerTest {
    private ScoreManager scoreManager;
    private Block block1, block2;

    @Before
    public void setUp() {
        scoreManager = new ScoreManager(DifficultyLevel.NORMAL);
        block1 = new Block(100, 100, 50, 30, Color.BLUE);
        block2 = new Block(100, 70, 50, 30, Color.RED); // Perfectly aligned
    }

    @Test
    public void testInitialState() {
        assertEquals(0, scoreManager.getCurrentScore());
        assertEquals(0, scoreManager.getBlocksPlaced());
        assertEquals(0, scoreManager.getPerfectAlignments());
        assertEquals(0, scoreManager.getTowerHeight());
        assertEquals(DifficultyLevel.NORMAL, scoreManager.getDifficulty());
    }

    @Test
    public void testBasicBlockScoring() {
        int points = scoreManager.addBlockScore(block1, null); // First block
        
        assertTrue(points > 0);
        assertEquals(1, scoreManager.getBlocksPlaced());
        assertEquals(1, scoreManager.getTowerHeight());
        assertEquals(points, scoreManager.getCurrentScore());
    }

    @Test
    public void testPerfectAlignment() {
        scoreManager.addBlockScore(block1, null); // First block
        int points = scoreManager.addBlockScore(block2, block1); // Perfect alignment
        
        assertTrue(points > 100); // Should get bonus for perfect alignment
        assertEquals(1, scoreManager.getPerfectAlignments());
        assertEquals(1, scoreManager.getCurrentCombo());
    }

    @Test
    public void testDifficultyMultiplier() {
        ScoreManager easyManager = new ScoreManager(DifficultyLevel.EASY);
        ScoreManager hardManager = new ScoreManager(DifficultyLevel.HARD);
        
        int easyPoints = easyManager.addBlockScore(block1, null);
        int hardPoints = hardManager.addBlockScore(block1, null);
        
        assertTrue(hardPoints > easyPoints); // Hard should give more points
    }

    @Test
    public void testComboSystem() {
        scoreManager.addBlockScore(block1, null); // First block
        
        // Create perfectly aligned blocks for combo
        Block perfectBlock1 = new Block(100, 70, 50, 30, Color.RED);
        Block perfectBlock2 = new Block(100, 40, 50, 30, Color.GREEN);
        
        int firstPerfect = scoreManager.addBlockScore(perfectBlock1, block1);
        int secondPerfect = scoreManager.addBlockScore(perfectBlock2, perfectBlock1);
        
        assertTrue(secondPerfect > firstPerfect); // Combo should increase score
        assertEquals(2, scoreManager.getCurrentCombo());
    }

    @Test
    public void testAccuracyCalculation() {
        scoreManager.addBlockScore(block1, null); // First block (always perfect)
        scoreManager.addBlockScore(block2, block1); // Perfect alignment
        
        // Add a misaligned block
        Block misalignedBlock = new Block(150, 40, 50, 30, Color.YELLOW);
        scoreManager.addBlockScore(misalignedBlock, block2);
        
        double accuracy = scoreManager.getAccuracyPercentage();
        assertTrue(accuracy > 0 && accuracy < 100); // Should be between 0 and 100
    }

    @Test
    public void testPerformanceGrade() {
        // Test with perfect accuracy
        scoreManager.addBlockScore(block1, null);
        scoreManager.addBlockScore(block2, block1);
        
        String grade = scoreManager.getPerformanceGrade();
        assertEquals("S", grade); // Should get S grade for perfect accuracy
    }

    @Test
    public void testReset() {
        scoreManager.addBlockScore(block1, null);
        scoreManager.addBonusPoints(1000);
        
        assertTrue(scoreManager.getCurrentScore() > 0);
        
        scoreManager.reset();
        
        assertEquals(0, scoreManager.getCurrentScore());
        assertEquals(0, scoreManager.getBlocksPlaced());
        assertEquals(0, scoreManager.getTowerHeight());
    }

    @Test
    public void testFinalScoreCalculation() {
        // Add some blocks
        scoreManager.addBlockScore(block1, null);
        scoreManager.addBlockScore(block2, block1);
        
        int currentScore = scoreManager.getCurrentScore();
        int finalScore = scoreManager.calculateFinalScore();
        
        assertTrue(finalScore >= currentScore); // Final score should be at least current score
    }

    @Test
    public void testBonusPoints() {
        int initialScore = scoreManager.getCurrentScore();
        scoreManager.addBonusPoints(500);
        
        assertTrue(scoreManager.getCurrentScore() > initialScore);
    }

    @Test
    public void testGameSummary() {
        scoreManager.addBlockScore(block1, null);
        scoreManager.addBlockScore(block2, block1);
        
        String summary = scoreManager.getGameSummary();
        assertNotNull(summary);
        assertTrue(summary.contains("Blocks:"));
        assertTrue(summary.contains("Height:"));
        assertTrue(summary.contains("Perfect:"));
    }

    @Test
    public void testMilestones() {
        int milestone = scoreManager.getNextMilestone();
        assertEquals(1000, milestone); // First milestone should be 1000
        
        scoreManager.addBonusPoints(1500);
        milestone = scoreManager.getNextMilestone();
        assertEquals(5000, milestone); // Next milestone should be 5000
    }
}