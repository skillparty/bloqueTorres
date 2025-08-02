package com.skillparty.towerblox;

import com.skillparty.towerblox.game.DifficultyLevel;
import com.skillparty.towerblox.game.GameEngine;
import com.skillparty.towerblox.game.GameState;
import com.skillparty.towerblox.game.physics.Block;
import com.skillparty.towerblox.score.HighScore;
import com.skillparty.towerblox.ui.components.ASCIILogo;
import com.skillparty.towerblox.ui.components.FontManager;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Full system integration test to verify all components work together
 */
public class FullSystemTest {
    private GameEngine gameEngine;
    private FontManager fontManager;
    private ASCIILogo asciiLogo;

    @Before
    public void setUp() {
        // Set headless mode for testing
        System.setProperty("java.awt.headless", "true");
        
        gameEngine = new GameEngine();
        fontManager = FontManager.getInstance();
        asciiLogo = new ASCIILogo();
    }

    @After
    public void tearDown() {
        if (gameEngine != null) {
            gameEngine.stopEngine();
        }
    }

    @Test
    public void testCompleteGameLifecycle() {
        // Test initial state
        assertEquals(GameState.MENU, gameEngine.getCurrentState());
        
        // Start a new game
        gameEngine.startNewGame(DifficultyLevel.NORMAL);
        assertEquals(GameState.PLAYING, gameEngine.getCurrentState());
        assertEquals(DifficultyLevel.NORMAL, gameEngine.getCurrentDifficulty());
        
        // Verify game components are initialized
        assertNotNull(gameEngine.getTower());
        assertNotNull(gameEngine.getCrane());
        assertNotNull(gameEngine.getScoreManager());
        assertNotNull(gameEngine.getScoreStorage());
        
        // Test game update
        gameEngine.update(16);
        assertEquals(GameState.PLAYING, gameEngine.getCurrentState());
        
        // Test pause/resume
        gameEngine.pauseGame();
        assertTrue(gameEngine.isPaused());
        
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
    public void testGamePhysicsIntegration() {
        gameEngine.startNewGame(DifficultyLevel.NORMAL);
        
        var tower = gameEngine.getTower();
        var crane = gameEngine.getCrane();
        
        // Test initial state
        assertTrue(tower.isEmpty());
        assertEquals(0, tower.getHeight());
        assertNotNull(crane);
        
        // Create and add blocks to tower
        Block block1 = new Block(100, 500, 50, 30, Color.BLUE);
        Block block2 = new Block(105, 470, 50, 30, Color.RED);
        Block block3 = new Block(110, 440, 50, 30, Color.GREEN);
        
        tower.addBlock(block1);
        tower.addBlock(block2);
        tower.addBlock(block3);
        
        assertEquals(3, tower.getHeight());
        assertFalse(tower.isEmpty());
        
        // Test tower stability
        assertTrue(tower.isStable()); // Should be stable initially
        
        // Test collision detection
        Block collidingBlock = new Block(102, 502, 50, 30, Color.YELLOW);
        assertTrue(tower.hasCollision(collidingBlock));
        
        Block nonCollidingBlock = new Block(200, 200, 50, 30, Color.CYAN);
        assertFalse(tower.hasCollision(nonCollidingBlock));
        
        // Test tower statistics
        assertTrue(tower.getAverageAlignment() >= 0);
        assertTrue(tower.getInstabilityScore() >= 0);
    }

    @Test
    public void testScoreSystemIntegration() {
        gameEngine.startNewGame(DifficultyLevel.HARD);
        
        var scoreManager = gameEngine.getScoreManager();
        var scoreStorage = gameEngine.getScoreStorage();
        
        // Test initial score state
        assertEquals(0, scoreManager.getCurrentScore());
        assertEquals(0, scoreManager.getBlocksPlaced());
        assertEquals(DifficultyLevel.HARD, scoreManager.getDifficulty());
        
        // Simulate scoring
        Block block1 = new Block(100, 100, 50, 30, Color.BLUE);
        Block block2 = new Block(100, 70, 50, 30, Color.RED); // Perfect alignment
        
        int points = scoreManager.addBlockScore(block2, block1);
        assertTrue(points > 0);
        assertTrue(scoreManager.getCurrentScore() > 0);
        assertEquals(1, scoreManager.getBlocksPlaced());
        
        // Test final score calculation
        int finalScore = scoreManager.calculateFinalScore();
        assertTrue(finalScore >= scoreManager.getCurrentScore());
        
        // Test high score qualification
        boolean qualifies = scoreStorage.qualifiesForHighScore(finalScore);
        // Should qualify since we cleared scores or it's a decent score
        
        // Test adding high score
        if (qualifies) {
            boolean added = gameEngine.addHighScore("TST");
            assertTrue(added);
        }
    }

    @Test
    public void testCraneAnimationIntegration() {
        gameEngine.startNewGame(DifficultyLevel.NORMAL);
        
        var crane = gameEngine.getCrane();
        
        // Test initial crane state
        assertFalse(crane.isAnimating());
        assertTrue(crane.isReadyForNewBlock());
        
        // Create a block for the crane
        Block testBlock = new Block(100, 100, 50, 30, Color.BLUE);
        crane.setCurrentBlock(testBlock);
        
        assertEquals(testBlock, crane.getCurrentBlock());
        assertFalse(crane.isReadyForNewBlock());
        
        // Test crane movement
        double initialX = crane.getX();
        for (int i = 0; i < 10; i++) {
            crane.update(16);
        }
        // Crane should have moved (unless at boundary)
        
        // Test block dropping
        crane.dropBlock();
        assertTrue(crane.isAnimating());
        assertTrue(testBlock.isDropped());
        
        // Update animation
        for (int i = 0; i < 20; i++) {
            crane.update(25);
        }
        
        // Animation should progress or complete
        assertNotNull(crane.getStatus());
    }

    @Test
    public void testFontSystemIntegration() {
        // Test font manager singleton
        FontManager fm1 = FontManager.getInstance();
        FontManager fm2 = FontManager.getInstance();
        assertSame(fm1, fm2);
        
        // Test all font variants
        assertNotNull(fm1.getLogoFont());
        assertNotNull(fm1.getMenuFont());
        assertNotNull(fm1.getGameFont());
        assertNotNull(fm1.getScoreFont());
        assertNotNull(fm1.getSmallFont());
        
        // Test font sizes are different
        assertTrue(fm1.getLogoFont().getSize() > fm1.getGameFont().getSize());
        assertTrue(fm1.getMenuFont().getSize() > fm1.getSmallFont().getSize());
        
        // Test font name
        String fontName = fm1.getFontName();
        assertTrue(fontName.equals("Ubuntu Mono") || fontName.equals("Monospaced"));
    }

    @Test
    public void testASCIILogoRendering() {
        // Test logo dimensions
        assertTrue(asciiLogo.getWidth() > 0);
        assertTrue(asciiLogo.getHeight() > 0);
        
        // Test rendering doesn't crash
        BufferedImage testImage = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = testImage.createGraphics();
        
        try {
            // Should not throw exception
            asciiLogo.render(g2d, 100, 100);
            asciiLogo.renderWithBackground(g2d, 100, 100, true);
        } finally {
            g2d.dispose();
        }
        
        // Test console output doesn't crash
        asciiLogo.printToConsole();
    }

    @Test
    public void testDifficultySystemIntegration() {
        // Test all difficulty levels
        for (DifficultyLevel difficulty : DifficultyLevel.values()) {
            gameEngine.startNewGame(difficulty);
            
            assertEquals(difficulty, gameEngine.getCurrentDifficulty());
            assertEquals(difficulty, gameEngine.getScoreManager().getDifficulty());
            
            // Test difficulty properties
            assertTrue(difficulty.getSpeedMultiplier() > 0);
            assertTrue(difficulty.getScoreMultiplier() > 0);
            assertNotNull(difficulty.getDisplayName());
            assertNotNull(difficulty.toString());
        }
    }

    @Test
    public void testGameRenderingIntegration() {
        gameEngine.startNewGame(DifficultyLevel.NORMAL);
        
        // Create a test graphics context
        BufferedImage testImage = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = testImage.createGraphics();
        
        try {
            // Should not throw exception
            gameEngine.render(g2d);
        } finally {
            g2d.dispose();
        }
    }

    @Test
    public void testHighScoreValidation() {
        // Test valid nicknames
        assertTrue(HighScore.isValidNickname("ABC"));
        assertTrue(HighScore.isValidNickname("A1B"));
        assertTrue(HighScore.isValidNickname("123"));
        assertTrue(HighScore.isValidNickname("X"));
        
        // Test invalid nicknames
        assertFalse(HighScore.isValidNickname(null));
        assertFalse(HighScore.isValidNickname(""));
        assertFalse(HighScore.isValidNickname("ABCD")); // Too long
        assertFalse(HighScore.isValidNickname("A@B")); // Invalid character
        
        // Test high score creation and comparison
        HighScore score1 = new HighScore("ABC", 10000, DifficultyLevel.NORMAL);
        HighScore score2 = new HighScore("XYZ", 5000, DifficultyLevel.NORMAL);
        
        assertTrue(score1.compareTo(score2) < 0); // Higher score comes first
        assertEquals("ABC", score1.getDisplayNickname());
        assertEquals("Normal", score1.getDifficultyName());
    }

    @Test
    public void testGameStateTransitions() {
        TestGameStateListener listener = new TestGameStateListener();
        gameEngine.setStateListener(listener);
        
        // Test state transitions
        assertEquals(GameState.MENU, gameEngine.getCurrentState());
        
        gameEngine.startNewGame(DifficultyLevel.EASY);
        assertEquals(GameState.PLAYING, gameEngine.getCurrentState());
        assertEquals(GameState.PLAYING, listener.lastState);
        
        gameEngine.returnToMenu();
        assertEquals(GameState.MENU, gameEngine.getCurrentState());
        assertEquals(GameState.MENU, listener.lastState);
        
        gameEngine.showHighScores();
        assertEquals(GameState.HIGH_SCORES, gameEngine.getCurrentState());
        assertEquals(GameState.HIGH_SCORES, listener.lastState);
    }

    @Test
    public void testSystemRequirements() {
        // Test Java version
        String javaVersion = System.getProperty("java.version");
        assertNotNull(javaVersion);
        
        // Test system properties
        assertNotNull(System.getProperty("os.name"));
        assertNotNull(System.getProperty("user.home"));
        
        // Test headless mode works
        assertTrue(java.awt.GraphicsEnvironment.isHeadless());
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