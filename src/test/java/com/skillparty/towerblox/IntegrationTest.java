package com.skillparty.towerblox;

import com.skillparty.towerblox.game.DifficultyLevel;
import com.skillparty.towerblox.game.GameEngine;
import com.skillparty.towerblox.game.GameState;
import com.skillparty.towerblox.game.physics.Block;
import com.skillparty.towerblox.score.HighScore;
import com.skillparty.towerblox.score.ScoreStorage;
import com.skillparty.towerblox.ui.components.FontManager;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;
import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Integration tests for the complete Tower Bloxx game system
 */
public class IntegrationTest {
    private GameEngine gameEngine;
    private ScoreStorage scoreStorage;
    private FontManager fontManager;

    @Before
    public void setUp() {
        gameEngine = new GameEngine();
        scoreStorage = new ScoreStorage();
        fontManager = FontManager.getInstance();
    }

    @After
    public void tearDown() {
        if (gameEngine != null) {
            gameEngine.stopEngine();
        }
    }

    @Test
    public void testCompleteGameFlow() {
        // Start a new game
        gameEngine.startNewGame(DifficultyLevel.NORMAL);
        assertEquals(GameState.PLAYING, gameEngine.getCurrentState());
        assertEquals(DifficultyLevel.NORMAL, gameEngine.getCurrentDifficulty());
        
        // Verify game components are initialized
        assertNotNull(gameEngine.getTower());
        assertNotNull(gameEngine.getCrane());
        assertNotNull(gameEngine.getScoreManager());
        
        // Verify initial state
        assertTrue(gameEngine.getTower().isEmpty());
        assertEquals(0, gameEngine.getScoreManager().getCurrentScore());
        
        // Simulate game update
        gameEngine.update(16);
        
        // Game should still be running
        assertEquals(GameState.PLAYING, gameEngine.getCurrentState());
    }

    @Test
    public void testScoreSystemIntegration() {
        // Clear existing scores
        scoreStorage.clearScores();
        
        // Add test scores
        HighScore score1 = new HighScore("ABC", 10000, DifficultyLevel.HARD);
        HighScore score2 = new HighScore("XYZ", 5000, DifficultyLevel.NORMAL);
        HighScore score3 = new HighScore("DEF", 15000, DifficultyLevel.EASY);
        
        assertTrue(scoreStorage.addScore(score1));
        assertTrue(scoreStorage.addScore(score2));
        assertTrue(scoreStorage.addScore(score3));
        
        // Verify scores are sorted correctly
        var scores = scoreStorage.getHighScores();
        assertEquals(3, scores.size());
        assertEquals(15000, scores.get(0).getScore()); // Highest first
        assertEquals(10000, scores.get(1).getScore());
        assertEquals(5000, scores.get(2).getScore());
        
        // Test qualification
        assertTrue(scoreStorage.qualifiesForHighScore(20000));
        assertFalse(scoreStorage.qualifiesForHighScore(1000));
    }

    @Test
    public void testGameEngineScoreIntegration() {
        gameEngine.startNewGame(DifficultyLevel.HARD);
        
        // Verify score manager is configured correctly
        assertEquals(DifficultyLevel.HARD, gameEngine.getScoreManager().getDifficulty());
        assertEquals(0, gameEngine.getScoreManager().getCurrentScore());
        
        // Simulate adding score
        gameEngine.getScoreManager().addBonusPoints(1000);
        assertTrue(gameEngine.getScoreManager().getCurrentScore() > 0);
        
        // Test final score calculation
        int finalScore = gameEngine.getScoreManager().calculateFinalScore();
        assertTrue(finalScore >= gameEngine.getScoreManager().getCurrentScore());
    }

    @Test
    public void testTowerCraneIntegration() {
        gameEngine.startNewGame(DifficultyLevel.NORMAL);
        
        var tower = gameEngine.getTower();
        var crane = gameEngine.getCrane();
        
        // Create and add a block to the tower
        Block block = new Block(100, 500, 50, 30, Color.BLUE);
        tower.addBlock(block);
        
        assertEquals(1, tower.getHeight());
        assertFalse(tower.isEmpty());
        
        // Test collision detection
        Block collidingBlock = new Block(110, 510, 50, 30, Color.RED);
        assertTrue(tower.hasCollision(collidingBlock));
        
        Block nonCollidingBlock = new Block(200, 200, 50, 30, Color.GREEN);
        assertFalse(tower.hasCollision(nonCollidingBlock));
    }

    @Test
    public void testFontSystemIntegration() {
        // Test font manager singleton
        FontManager fm1 = FontManager.getInstance();
        FontManager fm2 = FontManager.getInstance();
        assertSame(fm1, fm2);
        
        // Test different font sizes
        assertNotNull(fm1.getLogoFont());
        assertNotNull(fm1.getMenuFont());
        assertNotNull(fm1.getGameFont());
        assertNotNull(fm1.getScoreFont());
        assertNotNull(fm1.getSmallFont());
        
        // Verify fonts are different sizes
        assertTrue(fm1.getLogoFont().getSize() > fm1.getGameFont().getSize());
        assertTrue(fm1.getMenuFont().getSize() > fm1.getSmallFont().getSize());
    }

    @Test
    public void testGameStateTransitions() {
        // Initial state
        assertEquals(GameState.MENU, gameEngine.getCurrentState());
        
        // Start game
        gameEngine.startNewGame(DifficultyLevel.EASY);
        assertEquals(GameState.PLAYING, gameEngine.getCurrentState());
        
        // Pause game
        gameEngine.pauseGame();
        assertTrue(gameEngine.isPaused());
        assertEquals(GameState.PLAYING, gameEngine.getCurrentState()); // Still playing, just paused
        
        // Resume game
        gameEngine.resumeGame();
        assertFalse(gameEngine.isPaused());
        
        // Return to menu
        gameEngine.returnToMenu();
        assertEquals(GameState.MENU, gameEngine.getCurrentState());
        
        // Show high scores
        gameEngine.showHighScores();
        assertEquals(GameState.HIGH_SCORES, gameEngine.getCurrentState());
    }

    @Test
    public void testDifficultySystemIntegration() {
        // Test easy difficulty
        gameEngine.startNewGame(DifficultyLevel.EASY);
        assertEquals(DifficultyLevel.EASY, gameEngine.getCurrentDifficulty());
        assertEquals(0.7, DifficultyLevel.EASY.getSpeedMultiplier(), 0.01);
        assertEquals(1.0, DifficultyLevel.EASY.getScoreMultiplier(), 0.01);
        
        // Test normal difficulty
        gameEngine.startNewGame(DifficultyLevel.NORMAL);
        assertEquals(DifficultyLevel.NORMAL, gameEngine.getCurrentDifficulty());
        assertEquals(1.0, DifficultyLevel.NORMAL.getSpeedMultiplier(), 0.01);
        assertEquals(1.5, DifficultyLevel.NORMAL.getScoreMultiplier(), 0.01);
        
        // Test hard difficulty
        gameEngine.startNewGame(DifficultyLevel.HARD);
        assertEquals(DifficultyLevel.HARD, gameEngine.getCurrentDifficulty());
        assertEquals(1.3, DifficultyLevel.HARD.getSpeedMultiplier(), 0.01);
        assertEquals(2.0, DifficultyLevel.HARD.getScoreMultiplier(), 0.01);
    }

    @Test
    public void testScorePersistenceIntegration() throws IOException {
        // Clear existing scores
        scoreStorage.clearScores();
        
        // Add a score
        HighScore testScore = new HighScore("TST", 12345, DifficultyLevel.NORMAL);
        assertTrue(scoreStorage.addScore(testScore));
        
        // Create new storage instance (simulates restart)
        ScoreStorage newStorage = new ScoreStorage();
        
        // Verify score was persisted
        var loadedScores = newStorage.getHighScores();
        assertFalse(loadedScores.isEmpty());
        
        boolean found = loadedScores.stream()
            .anyMatch(score -> score.getNickname().trim().equals("TST") && 
                              score.getScore() == 12345);
        assertTrue("Score should be persisted", found);
    }

    @Test
    public void testBlockPhysicsIntegration() {
        Block block = new Block(100, 100, 50, 30, Color.BLUE);
        
        // Initial state
        assertFalse(block.isDropped());
        assertFalse(block.isStable());
        assertEquals(0.0, block.getVelocityY(), 0.01);
        
        // Drop block
        block.drop();
        assertTrue(block.isDropped());
        
        // Update physics
        double initialY = block.getY();
        for (int i = 0; i < 10; i++) {
            block.update();
        }
        
        // Block should have fallen due to gravity
        assertTrue(block.getY() > initialY);
        assertTrue(block.getVelocityY() > 0);
        
        // Make stable
        block.makeStable();
        assertTrue(block.isStable());
        assertEquals(0.0, block.getVelocityX(), 0.01);
        assertEquals(0.0, block.getVelocityY(), 0.01);
    }

    @Test
    public void testCraneAnimationIntegration() {
        gameEngine.startNewGame(DifficultyLevel.NORMAL);
        var crane = gameEngine.getCrane();
        
        // Create a block for the crane
        Block block = new Block(100, 100, 50, 30, Color.BLUE);
        crane.setCurrentBlock(block);
        
        // Initially not animating
        assertFalse(crane.isAnimating());
        
        // Drop block should start animation
        crane.dropBlock();
        assertTrue(crane.isAnimating());
        assertTrue(block.isDropped());
        
        // Update animation
        for (int i = 0; i < 10; i++) {
            crane.update(25);
        }
        
        // Animation should progress or complete
        // (exact state depends on timing, but should not crash)
        assertNotNull(crane.getStatus());
    }

    @Test
    public void testGameEngineEventSystem() {
        TestGameStateListener listener = new TestGameStateListener();
        gameEngine.setStateListener(listener);
        
        // Test state change events
        gameEngine.startNewGame(DifficultyLevel.NORMAL);
        assertEquals(GameState.PLAYING, listener.lastState);
        
        gameEngine.returnToMenu();
        assertEquals(GameState.MENU, listener.lastState);
        
        gameEngine.showHighScores();
        assertEquals(GameState.HIGH_SCORES, listener.lastState);
    }

    @Test
    public void testHighScoreQualificationFlow() {
        // Clear scores and add some test data
        scoreStorage.clearScores();
        for (int i = 0; i < 10; i++) {
            scoreStorage.addScore(new HighScore("T" + i, 1000 + i * 100, DifficultyLevel.NORMAL));
        }
        
        // Test qualification
        assertTrue(scoreStorage.qualifiesForHighScore(2000)); // Higher than all
        assertTrue(scoreStorage.qualifiesForHighScore(1500)); // Higher than some
        assertFalse(scoreStorage.qualifiesForHighScore(500)); // Lower than all
        
        // Test ranking
        assertEquals(1, scoreStorage.getScoreRank(2000));
        assertEquals(6, scoreStorage.getScoreRank(1400));
        assertEquals(-1, scoreStorage.getScoreRank(500));
    }

    // Helper class for testing events
    private static class TestGameStateListener implements GameEngine.GameStateListener {
        public GameState lastState;
        public int lastScore;
        public String lastGameOverReason;
        public int lastFinalScore;
        
        @Override
        public void onStateChanged(GameState newState) {
            lastState = newState;
        }
        
        @Override
        public void onScoreChanged(int newScore) {
            lastScore = newScore;
        }
        
        @Override
        public void onGameOver(String reason, int finalScore) {
            lastGameOverReason = reason;
            lastFinalScore = finalScore;
        }
        
        @Override
        public void onBlockPlaced(int score, int combo) {
            // Not used in these tests
        }
    }
}