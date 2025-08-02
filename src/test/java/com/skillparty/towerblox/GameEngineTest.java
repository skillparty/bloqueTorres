package com.skillparty.towerblox;

import com.skillparty.towerblox.game.DifficultyLevel;
import com.skillparty.towerblox.game.GameEngine;
import com.skillparty.towerblox.game.GameState;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 * Unit tests for GameEngine class
 */
public class GameEngineTest {
    private GameEngine gameEngine;
    private TestGameStateListener stateListener;
    
    // Test listener to capture events
    private static class TestGameStateListener implements GameEngine.GameStateListener {
        public GameState lastState;
        public int lastScore;
        public String lastGameOverReason;
        public int lastFinalScore;
        public int lastBlockScore;
        public int lastCombo;
        
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
            lastBlockScore = score;
            lastCombo = combo;
        }
    }

    @Before
    public void setUp() {
        gameEngine = new GameEngine();
        stateListener = new TestGameStateListener();
        gameEngine.setStateListener(stateListener);
    }

    @Test
    public void testInitialState() {
        assertEquals(GameState.MENU, gameEngine.getCurrentState());
        assertFalse(gameEngine.isRunning());
        assertFalse(gameEngine.isPaused());
        assertNotNull(gameEngine.getScoreManager());
        assertNotNull(gameEngine.getScoreStorage());
        assertNotNull(gameEngine.getTower());
        assertNotNull(gameEngine.getCrane());
    }

    @Test
    public void testStartNewGame() {
        gameEngine.startNewGame(DifficultyLevel.NORMAL);
        
        assertEquals(GameState.PLAYING, gameEngine.getCurrentState());
        assertEquals(DifficultyLevel.NORMAL, gameEngine.getCurrentDifficulty());
        assertEquals(DifficultyLevel.NORMAL, stateListener.lastState);
    }

    @Test
    public void testDifficultySettings() {
        // Test easy difficulty
        gameEngine.startNewGame(DifficultyLevel.EASY);
        assertEquals(DifficultyLevel.EASY, gameEngine.getCurrentDifficulty());
        
        // Test hard difficulty
        gameEngine.startNewGame(DifficultyLevel.HARD);
        assertEquals(DifficultyLevel.HARD, gameEngine.getCurrentDifficulty());
    }

    @Test
    public void testPauseResume() {
        gameEngine.startNewGame(DifficultyLevel.NORMAL);
        
        assertFalse(gameEngine.isPaused());
        
        gameEngine.pauseGame();
        assertTrue(gameEngine.isPaused());
        
        gameEngine.resumeGame();
        assertFalse(gameEngine.isPaused());
        
        gameEngine.togglePause();
        assertTrue(gameEngine.isPaused());
        
        gameEngine.togglePause();
        assertFalse(gameEngine.isPaused());
    }

    @Test
    public void testReturnToMenu() {
        gameEngine.startNewGame(DifficultyLevel.NORMAL);
        assertEquals(GameState.PLAYING, gameEngine.getCurrentState());
        
        gameEngine.returnToMenu();
        assertEquals(GameState.MENU, gameEngine.getCurrentState());
        assertEquals(GameState.MENU, stateListener.lastState);
    }

    @Test
    public void testShowHighScores() {
        gameEngine.showHighScores();
        assertEquals(GameState.HIGH_SCORES, gameEngine.getCurrentState());
        assertEquals(GameState.HIGH_SCORES, stateListener.lastState);
    }

    @Test
    public void testGameTime() {
        // Game time should be 0 when not playing
        assertEquals(0, gameEngine.getGameTime());
        
        gameEngine.startNewGame(DifficultyLevel.NORMAL);
        
        // Small delay to ensure time passes
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Game time should be positive when playing
        assertTrue(gameEngine.getGameTime() > 0);
    }

    @Test
    public void testScoreManager() {
        gameEngine.startNewGame(DifficultyLevel.NORMAL);
        
        assertNotNull(gameEngine.getScoreManager());
        assertEquals(DifficultyLevel.NORMAL, gameEngine.getScoreManager().getDifficulty());
        assertEquals(0, gameEngine.getScoreManager().getCurrentScore());
    }

    @Test
    public void testTowerAndCrane() {
        gameEngine.startNewGame(DifficultyLevel.NORMAL);
        
        assertNotNull(gameEngine.getTower());
        assertNotNull(gameEngine.getCrane());
        
        assertTrue(gameEngine.getTower().isEmpty());
        assertEquals(0, gameEngine.getTower().getHeight());
    }

    @Test
    public void testGameOverReason() {
        // Initially no game over reason
        assertEquals("", gameEngine.getGameOverReason());
        
        // After starting a game, still no reason
        gameEngine.startNewGame(DifficultyLevel.NORMAL);
        assertEquals("", gameEngine.getGameOverReason());
    }

    @Test
    public void testFPSInitialization() {
        // FPS should be 0 initially
        assertEquals(0.0, gameEngine.getFPS(), 0.1);
    }

    @Test
    public void testMultipleGameStarts() {
        // Start first game
        gameEngine.startNewGame(DifficultyLevel.EASY);
        assertEquals(DifficultyLevel.EASY, gameEngine.getCurrentDifficulty());
        
        // Start second game with different difficulty
        gameEngine.startNewGame(DifficultyLevel.HARD);
        assertEquals(DifficultyLevel.HARD, gameEngine.getCurrentDifficulty());
        assertEquals(GameState.PLAYING, gameEngine.getCurrentState());
    }

    @Test
    public void testStateListener() {
        assertNull(stateListener.lastState);
        
        gameEngine.startNewGame(DifficultyLevel.NORMAL);
        assertEquals(GameState.PLAYING, stateListener.lastState);
        
        gameEngine.returnToMenu();
        assertEquals(GameState.MENU, stateListener.lastState);
        
        gameEngine.showHighScores();
        assertEquals(GameState.HIGH_SCORES, stateListener.lastState);
    }

    @Test
    public void testStopEngine() {
        assertFalse(gameEngine.isRunning());
        
        gameEngine.stopEngine();
        assertFalse(gameEngine.isRunning());
    }

    @Test
    public void testUpdate() {
        gameEngine.startNewGame(DifficultyLevel.NORMAL);
        
        // Update should not crash
        gameEngine.update(16); // Simulate 16ms frame time
        
        // Game should still be in playing state
        assertEquals(GameState.PLAYING, gameEngine.getCurrentState());
    }

    @Test
    public void testUpdateWhenPaused() {
        gameEngine.startNewGame(DifficultyLevel.NORMAL);
        gameEngine.pauseGame();
        
        // Update when paused should not crash
        gameEngine.update(16);
        
        // Should still be paused
        assertTrue(gameEngine.isPaused());
        assertEquals(GameState.PLAYING, gameEngine.getCurrentState());
    }

    @Test
    public void testUpdateWhenNotPlaying() {
        // Update when not playing should not crash
        gameEngine.update(16);
        
        assertEquals(GameState.MENU, gameEngine.getCurrentState());
    }
}