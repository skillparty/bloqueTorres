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
import java.awt.BasicStroke;
import java.awt.FontMetrics;
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
    private MovementRecorder movementRecorder;
    
    // Professional gameplay enhancement systems
    private GameplayEnhancer gameplayEnhancer;
    private GameFeedbackSystem feedbackSystem;
    
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
        this.movementRecorder = new MovementRecorder();
        
        // Initialize professional gameplay systems
        this.gameplayEnhancer = new GameplayEnhancer();
        this.feedbackSystem = new GameFeedbackSystem();
        
        System.out.println("🎮 MovementRecorder creado en GameEngine");
        System.out.println("🎯 Professional gameplay systems initialized");
        
        // Conectar el MovementRecorder con la grúa
        if (crane != null) {
            crane.setMovementRecorder(movementRecorder);
            System.out.println("🏗️ MovementRecorder conectado a la grúa");
        }
        
        resetGameState();
    }

    /**
     * Resets the game state for a new game
     */
    private void resetGameState() {
        blockDropped = false; // Ensure we start with ability to create blocks
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
        
        // Reset professional gameplay systems
        if (gameplayEnhancer != null) gameplayEnhancer.reset();
        if (feedbackSystem != null) feedbackSystem.reset();
        
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
        
        // Professional mode specific features
        if (difficulty == DifficultyLevel.PROFESSIONAL) {
            System.out.println("🏆 Professional Mode Activated!");
            System.out.println("✨ Enhanced UI enabled");
            System.out.println("📷 Advanced camera system enabled");
            System.out.println("🏗️ Professional crane physics enabled");
            
            // PROFESSIONAL MODE BLOCK GENERATION FIX
            blockDropped = false; // Force reset to ensure first block gets created
            
            // Enable advanced features if available
            if (advancedFeatures != null) {
                advancedFeatures.enableProfessionalMode(true);
            }
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
        
        // Update professional city background
        if (cityBackground != null) {
            cityBackground.update();
        }
        
        // Update advanced features (particle effects, etc.)
        if (advancedFeatures != null) {
            advancedFeatures.update(deltaTime / 1000.0); // Convert to seconds
        }
        
        // Update professional feedback system
        if (feedbackSystem != null) {
            feedbackSystem.update(deltaTime);
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
        
        // PROFESSIONAL MODE ENHANCED FIX: More aggressive block generation
        if (currentDifficulty == DifficultyLevel.PROFESSIONAL) {
            if (crane.getCurrentBlock() == null) {
                blockDropped = false; // Force reset
                createNewBlock();
            }
            // Also check if block has been dropped for too long without landing
            else if (crane.getCurrentBlock() != null && crane.getCurrentBlock().isDropped()) {
                Block currentProfBlock = crane.getCurrentBlock();
                // If block has been falling for too long or is out of bounds, force reset
                if (currentProfBlock.getY() > GAME_HEIGHT + 100) {
                    handleBlockLost();
                }
            }
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
        
        // Render professional dynamic city background
        if (cityBackground != null) {
            int towerHeight = tower != null ? tower.getHeight() : 0;
            cityBackground.render(g2d, towerHeight, cameraY);
        } else {
            // Fallback background
            g2d.setColor(new Color(135, 206, 235)); // Sky blue
            g2d.fillRect(0, (int)-cameraY, GAME_WIDTH, GAME_HEIGHT);
        }
        
        // Draw ground
        g2d.setColor(new Color(34, 139, 34)); // Forest green
        g2d.fillRect(0, GROUND_LEVEL, GAME_WIDTH, GAME_HEIGHT - GROUND_LEVEL);
        // }
        
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
        
        // Render professional feedback system (with screen shake and effects)
        if (feedbackSystem != null) {
            feedbackSystem.render(g2d, GAME_WIDTH, GAME_HEIGHT);
        }
        
        // Render UI elements (fixed position)
        renderGameUI(g2d);
    }

    /**
     * Renderiza UI del juego con información de desafío mejorada
     */
    private void renderGameUI(Graphics2D g2d) {
        // Información esencial con mejor visibilidad
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.setColor(Color.WHITE);
        
        // Fondo semi-transparente para mejor legibilidad
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRoundRect(5, 5, 250, 120, 10, 10);
        
        g2d.setColor(Color.WHITE);
        g2d.drawString("Score: " + scoreManager.getCurrentScore(), 15, 25);
        g2d.drawString("Height: " + tower.getHeight() + "/163", 15, 45);
        
        // Vidas con iconos visuales
        g2d.drawString("Lives: ", 15, 65);
        for (int i = 0; i < MAX_LIVES; i++) {
            g2d.setColor(i < lives ? Color.RED : Color.DARK_GRAY);
            g2d.fillOval(80 + i * 20, 55, 12, 12);
        }
        
        // NUEVO: Información de desafío
        g2d.setColor(Color.YELLOW);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        
        // Patrón de movimiento actual
        String pattern = getMovementPatternName(tower.getHeight());
        g2d.drawString("Pattern: " + pattern, 15, 85);
        
        // Velocidad actual
        double speedMultiplier = crane.getSpeed() / crane.getBaseSpeed();
        g2d.drawString(String.format("Speed: %.1fx", speedMultiplier), 15, 100);
        
        // Rango de movimiento
        double rangePercent = (crane.getSwingRange() / (GAME_WIDTH * 0.3)) * 100;
        g2d.drawString(String.format("Range: %.0f%%", rangePercent), 15, 115);
        
        // NUEVO: Indicador de timing perfecto
        renderTimingIndicator(g2d);
        
        // NUEVO: Mini-mapa de la torre
        renderTowerMinimap(g2d);
        
        // Controles mejorados
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("SPACE = Drop Block | P = Pause | ESC = Menu", GAME_WIDTH / 2 - 150, GAME_HEIGHT - 20);
    }
    
    /**
     * Obtiene el nombre del patrón de movimiento actual
     */
    private String getMovementPatternName(int towerHeight) {
        if (towerHeight <= 10) return "STEADY";
        if (towerHeight <= 25) return "ACCELERATING";
        if (towerHeight <= 50) return "ERRATIC";
        if (towerHeight <= 75) return "PRECISION";
        if (towerHeight <= 100) return "CHAOTIC";
        return "EXTREME";
    }
    
    /**
     * Renderiza un indicador de timing perfecto
     */
    private void renderTimingIndicator(Graphics2D g2d) {
        // Posición en la esquina superior derecha
        int x = GAME_WIDTH - 150;
        int y = 30;
        
        // Fondo
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRoundRect(x - 10, y - 20, 140, 40, 8, 8);
        
        // Calcular si estamos en zona de timing perfecto
        double craneX = crane.getX();
        double centerX = GAME_WIDTH / 2.0;
        double distance = Math.abs(craneX - centerX);
        double maxDistance = crane.getSwingRange();
        
        // Zona perfecta es el 20% central
        boolean inPerfectZone = distance < (maxDistance * 0.2);
        
        // Indicador visual
        g2d.setColor(inPerfectZone ? Color.GREEN : Color.RED);
        g2d.fillOval(x, y - 10, 20, 20);
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString(inPerfectZone ? "PERFECT!" : "TIMING", x + 25, y);
        
        // Barra de precisión
        int barWidth = 80;
        int barX = x + 25;
        int barY = y + 8;
        
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(barX, barY, barWidth, 4);
        
        // Zona perfecta en la barra
        int perfectStart = barX + barWidth * 2 / 5;
        int perfectWidth = barWidth / 5;
        g2d.setColor(new Color(0, 255, 0, 100));
        g2d.fillRect(perfectStart, barY, perfectWidth, 4);
        
        // Posición actual
        int currentPos = barX + (int)((distance / maxDistance) * (barWidth / 2));
        if (craneX < centerX) currentPos = barX + barWidth / 2 - (int)((distance / maxDistance) * (barWidth / 2));
        else currentPos = barX + barWidth / 2 + (int)((distance / maxDistance) * (barWidth / 2));
        
        g2d.setColor(Color.YELLOW);
        g2d.fillRect(currentPos - 1, barY - 2, 2, 8);
    }
    
    /**
     * Renderiza un mini-mapa de la torre
     */
    private void renderTowerMinimap(Graphics2D g2d) {
        if (tower.isEmpty()) return;
        
        int mapX = GAME_WIDTH - 60;
        int mapY = 100;
        int mapWidth = 50;
        int mapHeight = 200;
        
        // Fondo del mini-mapa
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRoundRect(mapX - 5, mapY - 5, mapWidth + 10, mapHeight + 10, 8, 8);
        
        // Borde
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRoundRect(mapX - 5, mapY - 5, mapWidth + 10, mapHeight + 10, 8, 8);
        
        // Dibujar bloques de la torre (últimos 20)
        int towerHeight = tower.getHeight();
        int startBlock = Math.max(0, towerHeight - 20);
        
        for (int i = startBlock; i < towerHeight; i++) {
            int blockY = mapY + mapHeight - ((i - startBlock + 1) * (mapHeight / 20));
            
            // Color según estabilidad del bloque
            Block block = tower.getBlocks().get(i);
            double stability = block.getStability();
            Color blockColor;
            if (stability >= 0.9) blockColor = Color.GREEN;
            else if (stability >= 0.7) blockColor = Color.YELLOW;
            else if (stability >= 0.5) blockColor = Color.ORANGE;
            else blockColor = Color.RED;
            
            g2d.setColor(blockColor);
            g2d.fillRect(mapX + 5, blockY, mapWidth - 10, mapHeight / 20 - 1);
        }
        
        // Indicador de posición de la grúa
        g2d.setColor(Color.CYAN);
        g2d.fillRect(mapX + 2, mapY - 15, mapWidth - 4, 3);
        
        // Etiqueta
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        g2d.drawString("Tower", mapX, mapY - 20);
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
            
            // Professional gameplay analysis and feedback
            GameplayEnhancer.GameplayFeedback feedback = gameplayEnhancer.analyzeBlockPlacement(
                currentBlock, previousTop, tower, crane, System.currentTimeMillis()
            );
            
            // Calculate enhanced score using existing ScoreManager method
            int points = scoreManager.addBlockScore(currentBlock, previousTop);
            
            // Add bonus points based on professional analysis
            if (feedback.score > points) {
                scoreManager.addBonusPoints(feedback.score - points);
            }
            
            // Professional visual feedback
            int blockCenterX = (int)(currentBlock.getX() + currentBlock.getWidth() / 2);
            int blockTopY = (int)currentBlock.getY();
            
            if (feedbackSystem != null) {
                feedbackSystem.addPlacementFeedback(blockCenterX, blockTopY - 30, feedback);
            }
            
            // Enhanced audio feedback based on quality
            if (soundManager != null) {
                switch (feedback.quality) {
                    case PERFECT:
                        soundManager.playSound(SoundManager.SoundType.PERFECT_PLACEMENT);
                        break;
                    case EXCELLENT:
                    case GOOD:
                        soundManager.playSound(SoundManager.SoundType.BLOCK_LAND);
                        break;
                    default:
                        // Basic landing sound for lower quality placements
                        break;
                }
                
                // Special combo sound
                if (feedback.isCombo && feedback.comboCount >= 5) {
                    soundManager.playSound(SoundManager.SoundType.MILESTONE_REACHED);
                }
            }
            
            // Legacy effects system (keeping for compatibility)
            if (advancedFeatures != null) {
                if (feedback.quality == GameplayEnhancer.PlacementQuality.PERFECT) {
                    advancedFeatures.onPerfectBlockPlacement(blockCenterX, blockTopY, currentBlock);
                } else if (feedback.quality == GameplayEnhancer.PlacementQuality.GOOD || 
                          feedback.quality == GameplayEnhancer.PlacementQuality.EXCELLENT) {
                    advancedFeatures.onGoodBlockPlacement(blockCenterX, blockTopY, currentBlock);
                }
                
                // Height transitions and milestones
                advancedFeatures.onHeightTransition(blockCenterX, blockTopY, newHeight, previousHeight);
                
                if (newHeight % 10 == 0) {
                    advancedFeatures.onMilestoneReached(blockCenterX, blockTopY - 50, newHeight);
                }
            }
            
            // Adaptive difficulty adjustment based on player performance
            if (gameplayEnhancer != null) {
                double suggestedSpeedMultiplier = gameplayEnhancer.suggestSpeedMultiplier(
                    crane.getSpeed(), tower.getHeight()
                );
                // Apply gradual speed adjustment for smooth gameplay
                double currentSpeed = crane.getSpeed();
                double targetSpeed = crane.getBaseSpeed() * suggestedSpeedMultiplier;
                double adjustedSpeed = currentSpeed + (targetSpeed - currentSpeed) * 0.1; // 10% adjustment per block
                crane.setSpeed(adjustedSpeed);
            }
            
            // Notify listeners with enhanced information
            if (stateListener != null) {
                stateListener.onScoreChanged(scoreManager.getCurrentScore());
                stateListener.onBlockPlaced(points, Math.max(feedback.comboCount, scoreManager.getCurrentCombo()));
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
        
        // DESHABILITADO: No reproducir patrones automáticamente en el juego principal
        // Los patrones grabados son solo para el modo de práctica/demostración
    }
    
    /**
     * Adjusts crane speed based on tower height for progressive difficulty
     */
    private void adjustCraneSpeed() {
        if (tower != null && crane != null) {
            int towerHeight = tower.getHeight();
            double baseSpeed = crane.getBaseSpeed();
            double speedMultiplier = 1.0;
            
            // Progressive speed increase every 5 levels (más agresivo)
            if (towerHeight >= 5) {
                speedMultiplier += (towerHeight / 5) * 0.2; // +20% speed every 5 levels
            }
            
            // Apply difficulty multiplier
            speedMultiplier *= currentDifficulty.getSpeedMultiplier();
            
            double newSpeed = baseSpeed * speedMultiplier;
            crane.setSpeed(newSpeed);
            
            System.out.println("🏗️ Speed adjusted: " + String.format("%.2f", newSpeed) + " (Height: " + towerHeight + ")");
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
     * Sistema de cámara inteligente que sigue perfectamente la acción
     * El corazón visual del juego - mantiene la tensión y el enfoque
     */
    private void updateCamera() {
        if (tower == null) {
            return;
        }
        
        int towerHeight = tower.getHeight();
        
        // Activar cámara más temprano para mejor seguimiento
        if (towerHeight >= 3) { // Reducido de 7 a 3
            if (!cameraActivated) {
                cameraActivated = true;
                cameraActivationTime = System.currentTimeMillis();
            }
            
            // NUEVO: Sistema de cámara predictiva que anticipa el movimiento
            double craneY = crane.getY();
            double craneX = crane.getX();
            
            // Calcular posición del bloque actual o futuro
            Block currentBlock = crane.getCurrentBlock();
            double blockY = craneY + 60; // Posición del bloque en la grúa
            
            // NUEVO: Predicción de trayectoria si el bloque está cayendo
            if (currentBlock != null && currentBlock.isDropped()) {
                // Predecir dónde estará el bloque en los próximos frames
                double futureY = currentBlock.getY() + currentBlock.getVelocityY() * 0.5; // 0.5 segundos adelante
                blockY = Math.min(blockY, futureY); // Usar la posición más baja
            }
            
            // NUEVO: Cámara dinámica que se adapta al contexto
            CameraMode mode = determineCameraMode(towerHeight, currentBlock);
            targetCameraY = calculateCameraPosition(mode, craneY, blockY, towerHeight);
            
            // NUEVO: Suavizado adaptativo según la situación
            double cameraSpeed = calculateCameraSpeed(mode, towerHeight);
            
            // NUEVO: Compensación por movimiento horizontal de la grúa
            double horizontalInfluence = calculateHorizontalInfluence(craneX);
            targetCameraY += horizontalInfluence;
            
            // Aplicar movimiento de cámara
            cameraY += (targetCameraY - cameraY) * cameraSpeed;
            
        } else {
            // Transición suave al inicio
            targetCameraY = 0;
            cameraActivated = false;
            cameraY += (targetCameraY - cameraY) * 0.1;
        }
        
        // Snap final para evitar micro-movimientos
        if (Math.abs(targetCameraY - cameraY) < 0.5) {
            cameraY = targetCameraY;
        }
    }
    
    /**
     * Modos de cámara según el contexto del juego
     */
    private enum CameraMode {
        FOLLOWING,      // Siguiendo la grúa normalmente
        TRACKING_DROP,  // Siguiendo un bloque que cae
        ANTICIPATING,   // Anticipando el próximo movimiento
        PRECISION,      // Modo de precisión para torres altas
        DRAMATIC        // Modo dramático para momentos críticos
    }
    
    /**
     * Determina el modo de cámara apropiado
     */
    private CameraMode determineCameraMode(int towerHeight, Block currentBlock) {
        // Bloque cayendo - prioridad máxima
        if (currentBlock != null && currentBlock.isDropped()) {
            return CameraMode.TRACKING_DROP;
        }
        
        // Torres muy altas requieren precisión
        if (towerHeight > 75) {
            return CameraMode.PRECISION;
        }
        
        // Momentos críticos (pocas vidas, torre inestable)
        if (lives <= 1 || (tower != null && !tower.isEmpty() && tower.getInstabilityScore() > 0.7)) {
            return CameraMode.DRAMATIC;
        }
        
        // Anticipar próximo movimiento
        if (crane.isAnimating()) {
            return CameraMode.ANTICIPATING;
        }
        
        // Modo normal
        return CameraMode.FOLLOWING;
    }
    
    /**
     * Calcula la posición ideal de la cámara según el modo
     */
    private double calculateCameraPosition(CameraMode mode, double craneY, double blockY, int towerHeight) {
        double screenCenter = GAME_HEIGHT * 0.5;
        double upperThird = GAME_HEIGHT * 0.35;
        double lowerThird = GAME_HEIGHT * 0.65;
        
        switch (mode) {
            case FOLLOWING:
                // Mantener la grúa en el tercio superior
                return upperThird - craneY;
                
            case TRACKING_DROP:
                // Seguir el bloque que cae, manteniéndolo centrado
                return screenCenter - blockY;
                
            case ANTICIPATING:
                // Posición intermedia entre grúa y torre
                double towerTop = tower.isEmpty() ? GROUND_LEVEL : tower.getTopBlock().getY();
                double midPoint = (craneY + towerTop) / 2;
                return screenCenter - midPoint;
                
            case PRECISION:
                // Zoom más cercano para torres altas
                return upperThird - craneY + (towerHeight - 75) * 2;
                
            case DRAMATIC:
                // Ángulo más dinámico
                return lowerThird - blockY;
                
            default:
                return upperThird - craneY;
        }
    }
    
    /**
     * Calcula la velocidad de la cámara según el contexto
     */
    private double calculateCameraSpeed(CameraMode mode, int towerHeight) {
        double baseSpeed = 0.15;
        
        switch (mode) {
            case FOLLOWING:
                return baseSpeed + (towerHeight * 0.002); // Más rápida con altura
                
            case TRACKING_DROP:
                return 0.35; // Muy rápida para seguir bloques cayendo
                
            case ANTICIPATING:
                return 0.25; // Rápida para anticipar
                
            case PRECISION:
                return 0.08; // Muy suave para precisión
                
            case DRAMATIC:
                return 0.12; // Moderada para efecto dramático
                
            default:
                return baseSpeed;
        }
    }
    
    /**
     * Calcula la influencia del movimiento horizontal en la cámara
     */
    private double calculateHorizontalInfluence(double craneX) {
        // La cámara se inclina ligeramente según la posición horizontal de la grúa
        double screenCenter = GAME_WIDTH / 2.0;
        double horizontalOffset = (craneX - screenCenter) / GAME_WIDTH;
        
        // Efecto sutil de paralaje
        return horizontalOffset * 15; // Máximo 15 píxeles de influencia
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
    public MovementRecorder getMovementRecorder() { return movementRecorder; }
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
