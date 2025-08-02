package com.skillparty.towerblox.game;

import com.skillparty.towerblox.game.physics.Block;
import com.skillparty.towerblox.game.physics.Crane;
import com.skillparty.towerblox.game.physics.Tower;
import com.skillparty.towerblox.score.ScoreManager;
import com.skillparty.towerblox.score.ScoreStorage;
import com.skillparty.towerblox.score.HighScore;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

/**
 * Main game engine that manages the game loop, state transitions, and core gameplay
 */
public class GameEngine implements KeyListener {
    // Game constants
    private static final int TARGET_FPS = 60;
    private static final long FRAME_TIME = 1000 / TARGET_FPS; // milliseconds per frame
    private static final int GAME_WIDTH = 800;
    private static final int GAME_HEIGHT = 600;
    private static final int GROUND_LEVEL = GAME_HEIGHT - 50;
    
    // Game state
    private GameState currentState;
    private DifficultyLevel currentDifficulty;
    private boolean running;
    private boolean paused;
    
    // Game objects
    private Tower tower;
    private Crane crane;
    private ScoreManager scoreManager;
    private ScoreStorage scoreStorage;
    private Random random;
    
    // Timing
    private long lastUpdateTime;
    private long gameStartTime;
    private int frameCount;
    private double fps;
    
    // Game mechanics
    private boolean blockDropped;
    private boolean gameOverTriggered;
    private String gameOverReason;
    
    // Callbacks for UI updates
    private GameStateListener stateListener;
    
    /**
     * Interface for listening to game state changes
     */
    public interface GameStateListener {
        void onStateChanged(GameState newState);
        void onScoreChanged(int newScore);
        void onGameOver(String reason, int finalScore);
        void onBlockPlaced(int score, int combo);
    }

    /**
     * Creates a new game engine
     */
    public GameEngine() {
        this.currentState = GameState.MENU;
        this.running = false;
        this.paused = false;
        this.random = new Random();
        this.scoreStorage = new ScoreStorage();
        
        initializeGame();
    }

    /**
     * Initializes game objects
     */
    private void initializeGame() {
        this.tower = new Tower(GAME_WIDTH, GROUND_LEVEL);
        this.crane = new Crane(GAME_WIDTH / 2, 50, GAME_WIDTH);
        this.currentDifficulty = DifficultyLevel.NORMAL;
        this.scoreManager = new ScoreManager(currentDifficulty);
        
        resetGameState();
    }

    /**
     * Resets the game state for a new game
     */
    private void resetGameState() {
        blockDropped = false;
        gameOverTriggered = false;
        gameOverReason = "";
        frameCount = 0;
        fps = 0;
        
        if (tower != null) tower.reset();
        if (crane != null) crane.reset();
        if (scoreManager != null) scoreManager.reset();
        
        // Create first block
        if (crane != null) {
            createNewBlock();
        }
    }

    /**
     * Starts a new game with the specified difficulty
     */
    public void startNewGame(DifficultyLevel difficulty) {
        this.currentDifficulty = difficulty;
        this.scoreManager = new ScoreManager(difficulty);
        
        resetGameState();
        
        // Apply difficulty settings
        if (crane != null) {
            crane.setSpeed(crane.getBaseSpeed() * difficulty.getSpeedMultiplier());
        }
        
        this.currentState = GameState.PLAYING;
        this.gameStartTime = System.currentTimeMillis();
        
        if (stateListener != null) {
            stateListener.onStateChanged(currentState);
        }
        
        System.out.println("New game started - Difficulty: " + difficulty.getDisplayName());
    }

    /**
     * Main game loop - called by external timer
     */
    public void gameLoop() {
        if (!running) {
            running = true;
            lastUpdateTime = System.currentTimeMillis();
        }
        
        long currentTime = System.currentTimeMillis();
        long deltaTime = currentTime - lastUpdateTime;
        
        if (deltaTime >= FRAME_TIME) {
            update(deltaTime);
            
            // Calculate FPS
            frameCount++;
            if (frameCount % 60 == 0) {
                fps = 1000.0 / deltaTime;
            }
            
            lastUpdateTime = currentTime;
        }
    }

    /**
     * Updates game state
     */
    public void update(long deltaTime) {
        if (paused || currentState != GameState.PLAYING) {
            return;
        }
        
        // Update game objects
        if (crane != null) {
            crane.update(deltaTime);
        }
        
        if (tower != null) {
            tower.update(deltaTime);
            
            // Check tower stability
            if (!tower.isStable() && !gameOverTriggered) {
                triggerGameOver("Tower collapsed!");
            }
        }
        
        // Check if current block has landed
        Block currentBlock = crane.getCurrentBlock();
        if (currentBlock != null && currentBlock.isDropped() && !blockDropped) {
            checkBlockLanding(currentBlock);
        }
    }

    /**
     * Renders the game
     */
    public void render(Graphics2D g2d) {
        if (currentState != GameState.PLAYING) {
            return;
        }
        
        // Clear background
        g2d.setColor(new Color(135, 206, 235)); // Sky blue
        g2d.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        
        // Draw ground
        g2d.setColor(new Color(34, 139, 34)); // Forest green
        g2d.fillRect(0, GROUND_LEVEL, GAME_WIDTH, GAME_HEIGHT - GROUND_LEVEL);
        
        // Render game objects
        if (tower != null) {
            tower.render(g2d);
        }
        
        if (crane != null) {
            crane.render(g2d);
        }
        
        // Render UI elements
        renderGameUI(g2d);
    }

    /**
     * Renders game UI elements
     */
    private void renderGameUI(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.drawString("Score: " + scoreManager.getCurrentScore(), 10, 25);
        g2d.drawString("Height: " + tower.getHeight(), 10, 45);
        g2d.drawString("Blocks: " + scoreManager.getBlocksPlaced(), 10, 65);
        g2d.drawString("Combo: " + scoreManager.getCurrentCombo(), 10, 85);
        g2d.drawString("Difficulty: " + currentDifficulty.getDisplayName(), 10, 105);
        
        if (fps > 0) {
            g2d.drawString(String.format("FPS: %.1f", fps), GAME_WIDTH - 80, 25);
        }
        
        // Show controls
        g2d.drawString("Press SPACE to drop block", GAME_WIDTH / 2 - 80, GAME_HEIGHT - 20);
    }

    /**
     * Handles block landing and scoring
     */
    private void checkBlockLanding(Block currentBlock) {
        if (currentBlock.collidesWithGround(GROUND_LEVEL) || tower.hasCollision(currentBlock)) {
            // Block has landed
            blockDropped = true;
            currentBlock.makeStable();
            
            // Add to tower
            Block previousTop = tower.getTopBlock();
            tower.addBlock(currentBlock);
            
            // Calculate score
            int points = scoreManager.addBlockScore(currentBlock, previousTop);
            
            // Notify listeners
            if (stateListener != null) {
                stateListener.onScoreChanged(scoreManager.getCurrentScore());
                stateListener.onBlockPlaced(points, scoreManager.getCurrentCombo());
            }
            
            // Create next block
            createNewBlock();
            blockDropped = false;
            
            System.out.println("Block placed! Points: " + points + " | Total: " + scoreManager.getCurrentScore());
        }
    }

    /**
     * Creates a new block for the crane
     */
    private void createNewBlock() {
        // Random block color
        Color[] colors = {
            Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, 
            Color.ORANGE, Color.MAGENTA, Color.CYAN, Color.PINK
        };
        Color blockColor = colors[random.nextInt(colors.length)];
        
        // Standard block size with slight variation
        int width = 80 + random.nextInt(20);
        int height = 30 + random.nextInt(10);
        
        Block newBlock = new Block(crane.getX() - width/2, crane.getY() + 20, width, height, blockColor);
        crane.setCurrentBlock(newBlock);
    }

    /**
     * Triggers game over
     */
    private void triggerGameOver(String reason) {
        if (gameOverTriggered) return;
        
        gameOverTriggered = true;
        gameOverReason = reason;
        
        int finalScore = scoreManager.calculateFinalScore();
        
        // Check if score qualifies for high score table
        boolean qualifies = scoreStorage.qualifiesForHighScore(finalScore);
        
        if (stateListener != null) {
            stateListener.onGameOver(reason, finalScore);
        }
        
        this.currentState = GameState.GAME_OVER;
        
        System.out.println("Game Over: " + reason);
        System.out.println("Final Score: " + finalScore);
        System.out.println(scoreManager.getGameSummary());
        
        if (qualifies) {
            System.out.println("New high score! Rank: " + scoreStorage.getScoreRank(finalScore));
        }
    }

    /**
     * Adds a high score entry
     */
    public boolean addHighScore(String nickname) {
        if (currentState != GameState.GAME_OVER) {
            return false;
        }
        
        int finalScore = scoreManager.calculateFinalScore();
        HighScore highScore = new HighScore(nickname, finalScore, currentDifficulty);
        
        return scoreStorage.addScore(highScore);
    }

    /**
     * Handles key press events
     */
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SPACE:
                if (currentState == GameState.PLAYING && crane != null && !blockDropped) {
                    crane.dropBlock();
                }
                break;
                
            case KeyEvent.VK_ESCAPE:
                if (currentState == GameState.PLAYING) {
                    pauseGame();
                } else if (currentState == GameState.GAME_OVER) {
                    returnToMenu();
                }
                break;
                
            case KeyEvent.VK_P:
                if (currentState == GameState.PLAYING) {
                    togglePause();
                }
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Not used
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    // Game control methods
    public void pauseGame() {
        paused = true;
    }

    public void resumeGame() {
        paused = false;
    }

    public void togglePause() {
        paused = !paused;
    }

    public void returnToMenu() {
        currentState = GameState.MENU;
        if (stateListener != null) {
            stateListener.onStateChanged(currentState);
        }
    }

    public void showHighScores() {
        currentState = GameState.HIGH_SCORES;
        if (stateListener != null) {
            stateListener.onStateChanged(currentState);
        }
    }

    public void stopEngine() {
        running = false;
    }

    // Getters
    public GameState getCurrentState() { return currentState; }
    public DifficultyLevel getCurrentDifficulty() { return currentDifficulty; }
    public boolean isRunning() { return running; }
    public boolean isPaused() { return paused; }
    public ScoreManager getScoreManager() { return scoreManager; }
    public ScoreStorage getScoreStorage() { return scoreStorage; }
    public Tower getTower() { return tower; }
    public Crane getCrane() { return crane; }
    public String getGameOverReason() { return gameOverReason; }
    public double getFPS() { return fps; }
    public long getGameTime() { 
        return currentState == GameState.PLAYING ? System.currentTimeMillis() - gameStartTime : 0; 
    }

    // Setters
    public void setStateListener(GameStateListener listener) {
        this.stateListener = listener;
    }
}