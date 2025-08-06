package com.skillparty.towerblox.game;

import com.skillparty.towerblox.game.physics.Block;
import com.skillparty.towerblox.game.physics.Crane;
import com.skillparty.towerblox.game.physics.Tower;
import com.skillparty.towerblox.score.ScoreManager;
import com.skillparty.towerblox.score.ScoreStorage;
import com.skillparty.towerblox.score.HighScore;
import com.skillparty.towerblox.ui.components.CityBackground;
import com.skillparty.towerblox.effects.AdvancedFeaturesManager;
import com.skillparty.towerblox.audio.SoundManager;

import java.awt.Color;
import java.awt.Font;
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
    private static final int GAME_WIDTH = 1280;
    private static final int GAME_HEIGHT = 720;
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
    private CityBackground cityBackground;
    private AdvancedFeaturesManager advancedFeatures;
    private SoundManager soundManager;
    
    // Camera system for following the tower
    private double cameraY = 0; // Camera offset (negative values move view up)
    private double targetCameraY = 0; // Target camera position for smooth movement
    private static final int CAMERA_TRIGGER_HEIGHT = 7; // Start moving camera at floor 7 (earlier)
    private static final double CAMERA_SMOOTH_FACTOR = 0.12; // How smoothly camera follows (increased for faster response)
    private boolean cameraActivated = false; // Track if camera has been activated
    private long cameraActivationTime = 0; // When camera was activated
    
    // Timing
    private long lastUpdateTime;
    private long gameStartTime;
    private int frameCount;
    private double fps;
    
    // Game mechanics
    private boolean blockDropped;
    private boolean gameOverTriggered;
    private String gameOverReason;
    
    // Lives system
    private int lives;
    private static final int MAX_LIVES = 3;
    
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
        this.cityBackground = new CityBackground(GAME_WIDTH, GAME_HEIGHT, GROUND_LEVEL);
        this.advancedFeatures = new AdvancedFeaturesManager();
        this.soundManager = new SoundManager();
        
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
        lives = MAX_LIVES;
        
        // Reset camera
        cameraY = 0;
        targetCameraY = 0;
        cameraActivated = false;
        cameraActivationTime = 0;
        
        if (tower != null) tower.reset();
        if (crane != null) crane.reset();
        if (scoreManager != null) scoreManager.reset();
        
        // The first block will be created automatically in the update loop
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
            
            // Update the current block if it exists and is dropped
            Block currentBlock = crane.getCurrentBlock();
            if (currentBlock != null && currentBlock.isDropped()) {
                // Use enhanced animation update instead of basic update
                double deltaTimeSeconds = deltaTime / 1000.0;
                currentBlock.updateWithAnimation(deltaTimeSeconds);
                
                // Check for landing collisions to trigger landing animation
                if (tower != null && tower.getHeight() > 0) {
                    Block topBlock = tower.getTopBlock();
                    if (topBlock != null && currentBlock.getY() + currentBlock.getHeight() >= topBlock.getY()) {
                        currentBlock.triggerLanding(topBlock.getY());
                    }
                } else if (currentBlock.getY() + currentBlock.getHeight() >= GAME_HEIGHT - 50) {
                    // Landing on ground
                    currentBlock.triggerLanding(GAME_HEIGHT - 50);
                }
                
                // Add falling effects for dropped blocks (reduced frequency due to enhanced animation)
                if (advancedFeatures != null && Math.random() < 0.1) { // Reduced to 10% chance per frame
                    int blockCenterX = (int)(currentBlock.getX() + currentBlock.getWidth() / 2);
                    int blockBottomY = (int)(currentBlock.getY() + currentBlock.getHeight());
                    advancedFeatures.onBlockFalling(blockCenterX, blockBottomY);
                }
            }
        }
        
        if (tower != null) {
            tower.update(deltaTime);
            
            // Check tower stability
            if (!tower.isStable() && !gameOverTriggered) {
                triggerGameOver("Tower collapsed!");
            }
        }
        
        // Update city background
        if (cityBackground != null) {
            cityBackground.update();
        }
        
        // Update advanced features (particle effects, etc.)
        if (advancedFeatures != null) {
            advancedFeatures.update(deltaTime / 1000.0); // Convert to seconds
        }
        
        // Update camera system
        updateCamera();
        
        // Check if current block has landed or fallen off screen
        Block currentBlock = crane.getCurrentBlock();
        if (currentBlock != null && currentBlock.isDropped() && !blockDropped) {
            // Check if block fell off screen (lost life)
            if (currentBlock.getY() > GAME_HEIGHT + 50 || 
                currentBlock.getX() + currentBlock.getWidth() < 0 || 
                currentBlock.getX() > GAME_WIDTH) {
                handleBlockLost();
            } else {
                checkBlockLanding(currentBlock);
            }
        }
        
        // If crane doesn't have a block and we're not in the middle of dropping, create one
        if (crane.getCurrentBlock() == null && !blockDropped) {
            createNewBlock();
        }
    }

    /**
     * Renders the game with camera transformation
     */
    public void render(Graphics2D g2d) {
        if (currentState != GameState.PLAYING) {
            return;
        }
        
        // Save the original transform
        var originalTransform = g2d.getTransform();
        
        // Apply camera transformation
        g2d.translate(0, cameraY);
        
        // Render dynamic city background
        if (cityBackground != null) {
            int towerHeight = tower != null ? tower.getHeight() : 0;
            cityBackground.render(g2d, towerHeight, cameraY);
        } else {
            // Fallback background
            g2d.setColor(new Color(135, 206, 235)); // Sky blue
            g2d.fillRect(0, (int)-cameraY, GAME_WIDTH, GAME_HEIGHT);
            
            // Draw ground
            g2d.setColor(new Color(34, 139, 34)); // Forest green
            g2d.fillRect(0, GROUND_LEVEL, GAME_WIDTH, GAME_HEIGHT - GROUND_LEVEL);
        }
        
        // Render game objects (they will be affected by camera)
        if (tower != null) {
            tower.render(g2d);
        }
        
        if (crane != null) {
            crane.render(g2d);
            
            // Render the falling block if it exists and is dropped (with enhanced animation)
            Block currentBlock = crane.getCurrentBlock();
            if (currentBlock != null && currentBlock.isDropped()) {
                currentBlock.renderWithAnimation(g2d, cameraY);
            }
        }
        
        // Render advanced features (particle effects, etc.) - affected by camera
        if (advancedFeatures != null) {
            advancedFeatures.render(g2d);
        }
        
        // Restore original transform for UI elements (UI should not move with camera)
        g2d.setTransform(originalTransform);
        
        // Render UI elements (fixed position)
        renderGameUI(g2d);
    }

    /**
     * Renders game UI elements - FINAL CLEAN VERSION
     */
    private void renderGameUI(Graphics2D g2d) {
        // Set font for better visibility
        g2d.setFont(new Font("Monospaced", Font.BOLD, 14));
        g2d.setColor(Color.WHITE);
        
        // Essential game info only
        g2d.drawString("Score: " + scoreManager.getCurrentScore(), 10, 25);
        g2d.drawString("Height: " + tower.getHeight(), 10, 45);
        g2d.drawString("Lives: " + lives, 10, 65);
        
        // Controls
        g2d.drawString("SPACE = Drop Block", GAME_WIDTH / 2 - 60, GAME_HEIGHT - 20);
    }

    /**
     * Handles block landing and scoring
     */
    private void checkBlockLanding(Block currentBlock) {
        boolean hasLanded = false;
        
        // Check if block hits the ground
        if (currentBlock.collidesWithGround(GROUND_LEVEL)) {
            // Position block exactly on the ground
            currentBlock.setY(GROUND_LEVEL - currentBlock.getHeight());
            currentBlock.setVelocityY(0);
            currentBlock.setVelocityX(0);
            hasLanded = true;
        }
        
        // Check if block hits another block in the tower
        else if (tower.hasCollision(currentBlock)) {
            // Find the topmost block it collides with and position it on top
            Block topCollision = tower.getTopCollisionBlock(currentBlock);
            if (topCollision != null) {
                currentBlock.setY(topCollision.getY() - currentBlock.getHeight());
                currentBlock.setVelocityY(0);
                currentBlock.setVelocityX(0);
                hasLanded = true;
            }
        }
        
        if (hasLanded) {
            // Block has landed successfully
            blockDropped = true;
            currentBlock.makeStable();
            
            // Play landing sound
            if (soundManager != null) {
                soundManager.playSound(SoundManager.SoundType.BLOCK_LAND);
            }
            
            // Add to tower
            Block previousTop = tower.getTopBlock();
            int previousHeight = tower.getHeight();
            tower.addBlock(currentBlock);
            int newHeight = tower.getHeight();
            
            // Calculate score
            int points = scoreManager.addBlockScore(currentBlock, previousTop);
            
            // Trigger visual effects based on placement quality
            if (advancedFeatures != null) {
                int blockCenterX = (int)(currentBlock.getX() + currentBlock.getWidth() / 2);
                int blockTopY = (int)currentBlock.getY();
                
                // Determine if it was a perfect placement (high points indicate good alignment)
                if (points > 1500) { // Perfect placement threshold
                    advancedFeatures.onPerfectBlockPlacement(blockCenterX, blockTopY, currentBlock);
                    // Play perfect placement sound
                    if (soundManager != null) {
                        soundManager.playSound(SoundManager.SoundType.PERFECT_PLACEMENT);
                    }
                } else if (points > 500) { // Good placement threshold
                    advancedFeatures.onGoodBlockPlacement(blockCenterX, blockTopY, currentBlock);
                }
                
                // Check for height transitions (major phase changes)
                advancedFeatures.onHeightTransition(blockCenterX, blockTopY, newHeight, previousHeight);
                
                // Check for milestone achievements
                if (newHeight % 10 == 0) { // Every 10 blocks
                    advancedFeatures.onMilestoneReached(blockCenterX, blockTopY - 50, newHeight);
                    // Play milestone sound
                    if (soundManager != null) {
                        soundManager.playSound(SoundManager.SoundType.MILESTONE_REACHED);
                    }
                }
            }
            
            // Notify listeners
            if (stateListener != null) {
                stateListener.onScoreChanged(scoreManager.getCurrentScore());
                stateListener.onBlockPlaced(points, scoreManager.getCurrentCombo());
            }
            
            // Remove the block from crane after it's been added to tower
            crane.setCurrentBlock(null);
            
            // Reset for next block
            blockDropped = false;
            
            // Block placed successfully
        }
    }

    /**
     * Handles when a block is lost (falls off screen)
     */
    private void handleBlockLost() {
        lives--;
        blockDropped = true;
        
        // Remove the lost block from crane
        crane.setCurrentBlock(null);
        
        // Block lost
        
        if (lives <= 0) {
            triggerGameOver("No more lives! All blocks fell off the screen.");
        } else {
            // Reset for next block (will be created in the main update loop)
            blockDropped = false;
            
            // Notify listeners about life lost
            if (stateListener != null) {
                // We can extend the interface later to handle life changes
            }
        }
    }

    /**
     * Creates a new block for the crane with appropriate type based on tower height
     */
    private void createNewBlock() {
        // Adjust crane height based on tower height
        adjustCraneHeight();
        
        // Adjust crane speed based on tower height for progressive difficulty
        adjustCraneSpeed();
        
        int towerHeight = tower != null ? tower.getHeight() : 0;
        
        // Update crane swing range based on tower height (Tower Bloxx 2005 mechanics)
        if (crane != null) {
            crane.updateSwingRange(towerHeight);
        }
        Block.BlockType blockType = determineBlockType(towerHeight);
        Color blockColor = getBlockColorForType(blockType, towerHeight);
        
        // Block size varies slightly based on type
        int width = getBlockWidthForType(blockType);
        int height = getBlockHeightForType(blockType);
        
        Block newBlock = new Block(crane.getX() - width/2, crane.getY() + 60, width, height, blockColor, blockType);
        crane.setCurrentBlock(newBlock);
    }
    
    /**
     * Adjusts crane speed based on tower height for progressive difficulty
     */
    private void adjustCraneSpeed() {
        if (tower != null) {
            int towerHeight = tower.getHeight();
            double baseSpeed = crane.getBaseSpeed();
            double speedMultiplier = 1.0;
            
            // Progressive speed increase every 10 levels
            if (towerHeight >= 10) {
                speedMultiplier += (towerHeight / 10) * 0.3; // +30% speed every 10 levels
            }
            
            // Apply difficulty multiplier
            speedMultiplier *= currentDifficulty.getSpeedMultiplier();
            
            double newSpeed = baseSpeed * speedMultiplier;
            crane.setSpeed(newSpeed);
            
            // Speed increased silently
        }
    }
    
    /**
     * Determines the block type based on tower height
     */
    private Block.BlockType determineBlockType(int towerHeight) {
        if (towerHeight == 0) {
            return Block.BlockType.FOUNDATION;
        } else if (towerHeight < 5) {
            return Block.BlockType.COMMERCIAL; // Ground floor shops
        } else if (towerHeight < 15) {
            return Block.BlockType.RESIDENTIAL; // Residential floors
        } else if (towerHeight < 25) {
            return Block.BlockType.OFFICE; // Office floors
        } else {
            return Block.BlockType.PENTHOUSE; // Luxury top floors
        }
    }
    
    /**
     * Gets appropriate color for block type
     */
    private Color getBlockColorForType(Block.BlockType blockType, int towerHeight) {
        switch (blockType) {
            case FOUNDATION:
                return new Color(120, 120, 120); // Gray concrete
            case COMMERCIAL:
                Color[] commercialColors = {
                    new Color(255, 69, 0),    // Orange Red
                    new Color(255, 140, 0),   // Dark Orange
                    new Color(255, 215, 0),   // Gold
                    new Color(50, 205, 50)    // Lime Green
                };
                return commercialColors[random.nextInt(commercialColors.length)];
            case RESIDENTIAL:
                Color[] residentialColors = {
                    new Color(220, 20, 60),   // Crimson
                    new Color(138, 43, 226),  // Blue Violet
                    new Color(255, 20, 147),  // Deep Pink
                    new Color(72, 61, 139)    // Dark Slate Blue
                };
                return residentialColors[random.nextInt(residentialColors.length)];
            case OFFICE:
                Color[] officeColors = {
                    new Color(30, 144, 255),  // Dodger Blue
                    new Color(0, 206, 209),   // Dark Turquoise
                    new Color(100, 149, 237), // Cornflower Blue
                    new Color(70, 130, 180)   // Steel Blue
                };
                return officeColors[random.nextInt(officeColors.length)];
            case PENTHOUSE:
                Color[] penthouseColors = {
                    new Color(255, 215, 0),   // Gold
                    new Color(255, 223, 0),   // Golden Yellow
                    new Color(218, 165, 32),  // Goldenrod
                    new Color(255, 248, 220)  // Cornsilk
                };
                return penthouseColors[random.nextInt(penthouseColors.length)];
            default:
                return new Color(100, 100, 100);
        }
    }
    
    /**
     * Gets appropriate width for block type
     */
    private int getBlockWidthForType(Block.BlockType blockType) {
        switch (blockType) {
            case FOUNDATION:
                return 100 + random.nextInt(20); // Wider foundation
            case COMMERCIAL:
                return 85 + random.nextInt(15);  // Slightly wider for shops
            case RESIDENTIAL:
                return 80 + random.nextInt(20);  // Standard residential
            case OFFICE:
                return 90 + random.nextInt(10);  // Uniform office size
            case PENTHOUSE:
                return 75 + random.nextInt(25);  // Varied luxury sizes
            default:
                return 80 + random.nextInt(20);
        }
    }
    
    /**
     * Gets appropriate height for block type
     */
    private int getBlockHeightForType(Block.BlockType blockType) {
        switch (blockType) {
            case FOUNDATION:
                return 40 + random.nextInt(10); // Taller foundation
            case COMMERCIAL:
                return 35 + random.nextInt(10); // Taller for shops
            case RESIDENTIAL:
                return 30 + random.nextInt(10); // Standard height
            case OFFICE:
                return 32 + random.nextInt(8);  // Uniform office height
            case PENTHOUSE:
                return 38 + random.nextInt(12); // Taller luxury floors
            default:
                return 30 + random.nextInt(10);
        }
    }
    
    /**
     * Adjusts crane height based on tower height - DEFINITIVE FIX
     */
    private void adjustCraneHeight() {
        if (tower != null && !tower.isEmpty()) {
            Block topBlock = tower.getTopBlock();
            if (topBlock != null) {
                int towerHeight = tower.getHeight();
                
                // FINAL FIX: Much larger distances to prevent any overlapping
                double dropDistance;
                if (towerHeight <= 20) {
                    dropDistance = 250 + (towerHeight * 15); // 265-550px for levels 1-20
                } else {
                    dropDistance = 550 + ((towerHeight - 20) * 35); // 575+ for levels 21+ (increased from 25 to 35)
                }
                
                // Position crane above tower with calculated distance
                double newCraneY = topBlock.getY() - dropDistance;
                
                // Allow crane to go very high for tall towers
                newCraneY = Math.max(newCraneY, -50000); // Increased limit to support towers up to 9999 blocks
                
                crane.setY(newCraneY);
                
                // Crane distances properly calculated to prevent overlapping
            }
        }
    }
    
    /**
     * Updates the camera system to follow the crane and block - FIXED FOR VISIBILITY
     */
    private void updateCamera() {
        if (tower == null || tower.isEmpty()) {
            return;
        }
        
        int towerHeight = tower.getHeight();
        
        // Start moving camera when tower reaches trigger height
        if (towerHeight >= CAMERA_TRIGGER_HEIGHT) {
            if (!cameraActivated) {
                cameraActivated = true;
            }
            
            // FIXED: Camera prioritizes following the crane and block
            Block topBlock = tower.getTopBlock();
            if (topBlock != null) {
                double towerTop = topBlock.getY();
                double craneY = crane.getY();
                
                // Get the block position (crane + 60)
                double blockY = craneY + 60;
                
                // Camera should keep the block visible at 35% from top of screen
                double desiredViewHeight = GAME_HEIGHT * 0.35;
                targetCameraY = desiredViewHeight - blockY;
                
                // Ensure progressive upward movement (more aggressive for high towers)
                double minCameraY;
                if (towerHeight <= 30) {
                    minCameraY = (towerHeight - CAMERA_TRIGGER_HEIGHT) * 30;
                } else {
                    // For very high towers, increase the camera movement speed
                    minCameraY = (towerHeight - CAMERA_TRIGGER_HEIGHT) * 45; // Increased from 30 to 45
                }
                targetCameraY = Math.max(targetCameraY, minCameraY);
                
                // Ensure the tower top is also visible (not below 85% of screen)
                double maxCameraForTower = desiredViewHeight - towerTop + (GAME_HEIGHT * 0.5);
                targetCameraY = Math.min(targetCameraY, maxCameraForTower);
            }
        } else {
            targetCameraY = 0;
            cameraActivated = false;
        }
        
        // Faster camera movement for better responsiveness
        // Adjust camera speed based on tower height for better tracking
        double cameraSpeed = 0.15;
        if (towerHeight > 30) {
            cameraSpeed = 0.25; // Increased speed for high towers
        } else if (towerHeight > 20) {
            cameraSpeed = 0.20; // Moderate increase for mid-high towers
        }
        
        cameraY += (targetCameraY - cameraY) * cameraSpeed;
        
        // Snap to target if very close
        if (Math.abs(targetCameraY - cameraY) < 1.0) {
            cameraY = targetCameraY;
        }
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
        
        // Play game over sound
        if (soundManager != null) {
            soundManager.playSound(SoundManager.SoundType.GAME_OVER);
        }
        
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
                    // Play drop sound
                    if (soundManager != null) {
                        soundManager.playSound(SoundManager.SoundType.BLOCK_DROP);
                    }
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
    
    public int getLives() { return lives; }

    // Setters
    public void setStateListener(GameStateListener listener) {
        this.stateListener = listener;
    }

}