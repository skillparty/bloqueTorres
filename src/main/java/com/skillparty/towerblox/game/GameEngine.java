package com.skillparty.towerblox.game;

import com.skillparty.towerblox.game.physics.Block;
import com.skillparty.towerblox.game.physics.Crane;
import com.skillparty.towerblox.game.physics.Tower;
import com.skillparty.towerblox.score.ScoreManager;
import com.skillparty.towerblox.score.ScoreStorage;
import com.skillparty.towerblox.score.HighScore;
import com.skillparty.towerblox.ui.components.CityBackground;
import com.skillparty.towerblox.effects.AdvancedFeaturesManager;

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
    private CityBackground cityBackground;
    private AdvancedFeaturesManager advancedFeatures;
    
    // Camera system for following the tower
    private double cameraY = 0; // Camera offset (negative values move view up)
    private double targetCameraY = 0; // Target camera position for smooth movement
    private static final int CAMERA_TRIGGER_HEIGHT = 10; // Start moving camera at floor 10 (earlier)
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
                currentBlock.update();
                
                // Add falling effects for dropped blocks
                if (advancedFeatures != null && Math.random() < 0.3) { // 30% chance per frame
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
            
            // Render the falling block if it exists and is dropped
            Block currentBlock = crane.getCurrentBlock();
            if (currentBlock != null && currentBlock.isDropped()) {
                currentBlock.render(g2d);
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
     * Renders game UI elements
     */
    private void renderGameUI(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.drawString("Score: " + scoreManager.getCurrentScore(), 10, 25);
        g2d.drawString("Height: " + tower.getHeight(), 10, 45);
        g2d.drawString("Blocks: " + scoreManager.getBlocksPlaced(), 10, 65);
        g2d.drawString("Combo: " + scoreManager.getCurrentCombo(), 10, 85);
        g2d.drawString("Lives: " + lives, 10, 105);
        g2d.drawString("Difficulty: " + currentDifficulty.getDisplayName(), 10, 125);
        
        // Show camera status when active
        if (tower.getHeight() >= CAMERA_TRIGGER_HEIGHT) {
            // Flash effect when camera is newly activated
            long timeSinceActivation = System.currentTimeMillis() - cameraActivationTime;
            if (timeSinceActivation < 2000) { // Flash for 2 seconds
                int alpha = (int)(128 + 127 * Math.sin(timeSinceActivation * 0.01));
                g2d.setColor(new Color(0, 255, 150, alpha));
            } else {
                g2d.setColor(new Color(0, 255, 150)); // Bright green
            }
            
            g2d.drawString("CAMERA FOLLOWING", 10, 145);
            g2d.setColor(new Color(200, 200, 200));
            g2d.drawString(String.format("Altitude: %.0fm", Math.abs(cameraY) / 10), 10, 165);
            g2d.setColor(Color.WHITE);
        } else if (tower.getHeight() >= CAMERA_TRIGGER_HEIGHT - 3) {
            // Show warning when approaching camera trigger
            g2d.setColor(new Color(255, 200, 0)); // Orange
            g2d.drawString("Camera activating soon...", 10, 145);
            g2d.setColor(Color.WHITE);
        }
        
        if (fps > 0) {
            g2d.drawString(String.format("FPS: %.1f", fps), GAME_WIDTH - 80, 25);
        }
        
        // Show advanced features info
        if (advancedFeatures != null) {
            g2d.setColor(new Color(255, 200, 100));
            g2d.drawString("Effects: " + advancedFeatures.getPerformanceInfo(), GAME_WIDTH - 200, 45);
            g2d.setColor(Color.WHITE);
        }
        
        // Show controls
        g2d.drawString("Press SPACE to drop block", GAME_WIDTH / 2 - 80, GAME_HEIGHT - 20);
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
                } else if (points > 500) { // Good placement threshold
                    advancedFeatures.onGoodBlockPlacement(blockCenterX, blockTopY, currentBlock);
                }
                
                // Check for height transitions (major phase changes)
                advancedFeatures.onHeightTransition(blockCenterX, blockTopY, newHeight, previousHeight);
                
                // Check for milestone achievements
                if (newHeight % 10 == 0) { // Every 10 blocks
                    advancedFeatures.onMilestoneReached(blockCenterX, blockTopY - 50, newHeight);
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
            
            System.out.println("Block placed! Points: " + points + " | Total: " + scoreManager.getCurrentScore() + 
                             " | Height: " + tower.getHeight() + " | Lives: " + lives);
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
        
        System.out.println("Block lost! Lives remaining: " + lives);
        
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
        Block.BlockType blockType = determineBlockType(towerHeight);
        Color blockColor = getBlockColorForType(blockType, towerHeight);
        
        // Block size varies slightly based on type
        int width = getBlockWidthForType(blockType);
        int height = getBlockHeightForType(blockType);
        
        Block newBlock = new Block(crane.getX() - width/2, crane.getY() + 20, width, height, blockColor, blockType);
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
            
            if (towerHeight > 0 && towerHeight % 10 == 0) {
                System.out.println("🚀 SPEED INCREASED - Height: " + towerHeight + ", Speed: " + String.format("%.1fx", speedMultiplier));
            }
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
     * Adjusts crane height based on tower height
     */
    private void adjustCraneHeight() {
        if (tower != null && !tower.isEmpty()) {
            // Get the top block of the tower
            Block topBlock = tower.getTopBlock();
            if (topBlock != null) {
                int towerHeight = tower.getHeight();
                double newCraneY;
                
                if (towerHeight >= 15) {
                    // After level 15, maintain constant distance with the last block
                    // Distance should be MUCH LARGER for challenging gameplay
                    double CONSTANT_DISTANCE = 300; // Much larger distance for challenging drop
                    newCraneY = topBlock.getY() - CONSTANT_DISTANCE;
                    
                    // Allow crane to go higher when needed, but set reasonable limits
                    // Minimum Y can be negative (above screen) for very tall towers
                    double minAllowedY = -500; // Allow crane to go much higher for very tall towers
                    newCraneY = Math.max(minAllowedY, newCraneY);
                    
                    System.out.println("🏗️ ADVANCED CRANE MODE - Maintaining " + CONSTANT_DISTANCE + " pixels distance with top block");
                } else {
                    // Before level 15, use progressive logic that scales with tower height
                    // Start with a large base distance and reduce it as tower grows
                    double baseDistance = 250; // Large base distance for challenging gameplay
                    double heightAdjustment = towerHeight * 5; // Reduce distance as tower grows
                    newCraneY = topBlock.getY() - (baseDistance - heightAdjustment);
                    
                    // For early levels, still maintain minimum challenge distance
                    double minChallengeDistance = topBlock.getY() - 200; // Minimum 200 pixels drop distance
                    newCraneY = Math.min(newCraneY, minChallengeDistance);
                    
                    // Don't let crane go too high initially
                    newCraneY = Math.max(-100, newCraneY);
                }
                
                crane.setY(newCraneY);
                
                System.out.println("Crane adjusted to Y: " + newCraneY + " (Tower top: " + topBlock.getY() + ", Tower height: " + towerHeight + ", Drop distance: " + (topBlock.getY() - newCraneY) + ")");
            }
        }
    }
    
    /**
     * Updates the camera system to follow the tower
     */
    private void updateCamera() {
        if (tower == null || tower.isEmpty()) {
            return;
        }
        
        int towerHeight = tower.getHeight();
        
        // Start moving camera when tower reaches trigger height
        if (towerHeight >= CAMERA_TRIGGER_HEIGHT) {
            // Activate camera if not already activated
            if (!cameraActivated) {
                cameraActivated = true;
                cameraActivationTime = System.currentTimeMillis();
                System.out.println("🎥 CAMERA SYSTEM ACTIVATED - Following tower at height " + towerHeight);
            }
            
            // Calculate how much the camera should move based on tower height
            Block topBlock = tower.getTopBlock();
            if (topBlock != null) {
                // Calculate target camera position to keep the crane and tower visible
                double towerTop = topBlock.getY();
                double craneY = crane.getY();
                
                // Different camera behavior for different tower heights
                if (towerHeight >= 15) {
                    // For tall towers (15+), focus on keeping both crane and tower top visible
                    // The camera should continue moving up as the tower grows
                    
                    // Find the highest point (lowest Y value) between crane and tower top
                    double highestPoint = Math.min(craneY, towerTop);
                    
                    // Keep the highest point at about 20% from top of screen for better visibility
                    double desiredViewHeight = GAME_HEIGHT * 0.20; // 20% from top
                    
                    // Calculate camera offset - camera Y is negative when moving up
                    targetCameraY = -(highestPoint - desiredViewHeight);
                    
                    // Ensure camera continues moving up for very tall towers
                    // Force minimum upward movement based on tower height
                    double forcedUpwardMovement = (towerHeight - 15) * 25; // 25 pixels per block above 15
                    double minCameraY = -(150 + forcedUpwardMovement); // Base -150 + additional movement
                    targetCameraY = Math.min(targetCameraY, minCameraY);
                    
                } else {
                    // For shorter towers (10-14), use progressive movement
                    double highestPoint = Math.min(craneY, towerTop);
                    double desiredViewHeight = GAME_HEIGHT * 0.30; // 30% from top gives more room
                    
                    // Calculate camera offset
                    targetCameraY = -(highestPoint - desiredViewHeight);
                    
                    // Ensure minimum camera movement based on tower height
                    double minCameraMovement = (towerHeight - CAMERA_TRIGGER_HEIGHT) * 30; // 30 pixels per block
                    targetCameraY = Math.min(targetCameraY, -minCameraMovement);
                }
                
                // Debug for camera tracking (show more frequently for testing)
                if (towerHeight >= 10 && towerHeight % 2 == 0) {
                    System.out.println("📹 Camera tracking - Height: " + towerHeight + 
                                     ", Target Y: " + targetCameraY + ", Current Y: " + cameraY + 
                                     ", Tower top: " + towerTop + ", Crane Y: " + craneY);
                }
            }
        } else {
            // Reset camera when tower is low
            targetCameraY = 0;
            cameraActivated = false;
        }
        
        // Smooth camera movement with adaptive speed
        double distance = Math.abs(targetCameraY - cameraY);
        double adaptiveFactor = CAMERA_SMOOTH_FACTOR;
        
        // Move faster when far from target, especially for high towers
        if (distance > 100) {
            adaptiveFactor *= 3;
        } else if (distance > 50) {
            adaptiveFactor *= 2;
        }
        
        cameraY += (targetCameraY - cameraY) * adaptiveFactor;
        
        // Snap to target if very close (prevents infinite small movements)
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
    
    public int getLives() { return lives; }

    // Setters
    public void setStateListener(GameStateListener listener) {
        this.stateListener = listener;
    }
}