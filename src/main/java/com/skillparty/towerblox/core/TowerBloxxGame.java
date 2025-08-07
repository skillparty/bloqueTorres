package com.skillparty.towerblox.core;

import com.skillparty.towerblox.physics.PhysicsEngine;
import com.skillparty.towerblox.physics.CraneSystem;
import com.skillparty.towerblox.physics.BlockSystem;
import com.skillparty.towerblox.rendering.RenderingEngine;
import com.skillparty.towerblox.ui.components.CityBackground;
import com.skillparty.towerblox.game.DifficultyLevel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Main refactored Tower Bloxx game controller
 * Professional architecture with modular components
 * 
 * @author joseAlejandro
 * @version 2.0 - Production Ready
 */
public class TowerBloxxGame extends JPanel implements KeyListener {
    
    // Core systems
    private GameLoop gameLoop;
    private PhysicsEngine physicsEngine;
    private RenderingEngine renderingEngine;
    private CraneSystem craneSystem;
    
    // Game state
    private GameState currentState;
    private DifficultyLevel difficulty;
    private int score;
    private int towerHeight;
    private double towerStability;
    
    // Tower management
    private List<BlockSystem> blocks;
    private BlockSystem currentBlock;
    private static final int GROUND_Y = 650;
    
    // Background
    private CityBackground background;
    
    // Performance monitoring
    private boolean showDebugInfo = true;
    private double currentFPS;
    private long memoryUsage;
    
    /**
     * Game states
     */
    public enum GameState {
        MENU,
        PLAYING,
        PAUSED,
        GAME_OVER,
        VICTORY
    }
    
    /**
     * Constructs the game
     */
    public TowerBloxxGame() {
        setPreferredSize(new Dimension(1280, 720));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        setFocusable(true);
        addKeyListener(this);
        
        initializeSystems();
        startGame();
    }
    
    /**
     * Initializes all game systems
     */
    private void initializeSystems() {
        // Initialize physics
        physicsEngine = new PhysicsEngine();
        physicsEngine.addCollisionListener(this::onCollision);
        
        // Initialize rendering
        renderingEngine = new RenderingEngine(1280, 720);
        renderingEngine.setQuality(RenderingEngine.RenderQuality.HIGH);
        
        // Initialize crane
        craneSystem = new CraneSystem(1280, 720);
        craneSystem.setMovementPattern(CraneSystem.MovementPattern.PENDULUM);
        
        // Initialize background
        background = new CityBackground(1280, 720, 600);
        
        // Initialize game state
        blocks = new ArrayList<>();
        currentState = GameState.MENU;
        difficulty = DifficultyLevel.NORMAL;
        score = 0;
        towerHeight = 0;
        towerStability = 100.0;
        
        // Initialize game loop
        gameLoop = new GameLoop(
            this::update,
            this::render
        );
        
        gameLoop.setPerformanceCallback((fps, cpu, memory) -> {
            currentFPS = fps;
            memoryUsage = memory;
        });
    }
    
    /**
     * Starts the game
     */
    public void startGame() {
        currentState = GameState.PLAYING;
        
        // Create foundation
        createFoundation();
        
        // Spawn first block
        spawnNewBlock();
        
        // Start game loop
        gameLoop.start();
        
        System.out.println("🎮 javaBloxx 2.0 Started - Professional Refactor Complete!");
    }
    
    /**
     * Creates the foundation
     */
    private void createFoundation() {
        BlockSystem foundation = new BlockSystem(
            640 - 60, GROUND_Y,
            BlockSystem.BlockType.STEEL,
            0
        );
        
        // Make foundation static
        foundation.getPhysicsBody().isStatic = true;
        foundation.getPhysicsBody().hasGravity = false;
        foundation.getPhysicsBody().width = 120;
        foundation.getPhysicsBody().height = 50;
        
        blocks.add(foundation);
        physicsEngine.addBody(foundation.getPhysicsBody());
    }
    
    /**
     * Spawns a new block
     */
    private void spawnNewBlock() {
        // Determine block type based on height
        BlockSystem.BlockType type = BlockSystem.BlockType.NORMAL;
        if (towerHeight > 50) {
            type = BlockSystem.BlockType.GOLD;
        } else if (towerHeight > 30) {
            type = BlockSystem.BlockType.STEEL;
        } else if (towerHeight > 15 && Math.random() < 0.3) {
            type = BlockSystem.BlockType.GLASS;
        }
        
        // Create block at crane position
        currentBlock = new BlockSystem(
            craneSystem.getX() - 40,
            craneSystem.getY() + 20,
            type,
            towerHeight + 1
        );
        
        // Attach to crane
        craneSystem.attachBlock(currentBlock.getPhysicsBody());
    }
    
    /**
     * Main update method
     */
    private void update(double deltaTime) {
        if (currentState != GameState.PLAYING) {
            return;
        }
        
        // Update physics
        physicsEngine.update(deltaTime);
        
        // Update crane
        craneSystem.update(deltaTime);
        
        // Update blocks
        for (BlockSystem block : blocks) {
            block.update(deltaTime);
        }
        
        if (currentBlock != null) {
            currentBlock.update(deltaTime);
        }
        
        // Update background
        background.update();
        
        // Update camera to follow tower
        updateCamera();
        
        // Check tower stability
        updateTowerStability();
        
        // Check game over conditions
        checkGameOver();
    }
    
    /**
     * Updates camera position
     */
    private void updateCamera() {
        if (towerHeight > 5) {
            double targetY = GROUND_Y - (towerHeight * 45);
            renderingEngine.setCameraPosition(640, targetY);
            
            // Adjust crane cable length as tower grows
            craneSystem.adjustCableLength(200 + Math.min(100, towerHeight * 2));
        }
    }
    
    /**
     * Updates tower stability
     */
    private void updateTowerStability() {
        if (blocks.size() <= 1) return;
        
        towerStability = 100.0;
        double centerX = blocks.get(0).getX() + blocks.get(0).getWidth() / 2;
        
        for (int i = 1; i < blocks.size(); i++) {
            BlockSystem block = blocks.get(i);
            double blockCenterX = block.getX() + block.getWidth() / 2;
            double offset = Math.abs(blockCenterX - centerX);
            
            // Reduce stability based on offset
            towerStability -= offset * 0.5;
        }
        
        towerStability = Math.max(0, Math.min(100, towerStability));
    }
    
    /**
     * Checks game over conditions
     */
    private void checkGameOver() {
        if (towerStability <= 0) {
            gameOver("Tower Collapsed!");
        } else if (towerHeight >= 100) {
            victory();
        }
    }
    
    /**
     * Main render method
     */
    private void render(double interpolation) {
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Enable antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Calculate camera offset
        double cameraY = 0;
        if (towerHeight > 5) {
            cameraY = (towerHeight - 5) * 45;
        }
        
        // Render background
        background.render(g2d, (int)cameraY, 0.0);
        
        // Render blocks
        for (BlockSystem block : blocks) {
            block.render(g2d, cameraY);
        }
        
        if (currentBlock != null && craneSystem.hasBlock()) {
            currentBlock.render(g2d, cameraY);
        }
        
        // Render crane
        craneSystem.render(g2d, cameraY);
        
        // Render UI
        renderUI(g2d);
        
        // Render debug info
        if (showDebugInfo) {
            renderDebugInfo(g2d);
        }
    }
    
    /**
     * Renders UI elements
     */
    private void renderUI(Graphics2D g) {
        // Score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Score: " + score, 20, 40);
        
        // Tower height
        g.drawString("Height: " + towerHeight, 20, 70);
        
        // Stability bar
        renderStabilityBar(g);
    }
    
    /**
     * Renders stability bar
     */
    private void renderStabilityBar(Graphics2D g) {
        int barX = 20;
        int barY = 90;
        int barWidth = 200;
        int barHeight = 20;
        
        // Background
        g.setColor(Color.DARK_GRAY);
        g.fillRect(barX, barY, barWidth, barHeight);
        
        // Stability fill
        Color stabilityColor;
        if (towerStability > 70) {
            stabilityColor = Color.GREEN;
        } else if (towerStability > 30) {
            stabilityColor = Color.YELLOW;
        } else {
            stabilityColor = Color.RED;
        }
        
        g.setColor(stabilityColor);
        g.fillRect(barX, barY, (int)(barWidth * towerStability / 100), barHeight);
        
        // Border
        g.setColor(Color.WHITE);
        g.drawRect(barX, barY, barWidth, barHeight);
        
        // Label
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("Stability: " + String.format("%.1f%%", towerStability), barX, barY - 5);
    }
    
    /**
     * Renders debug information
     */
    private void renderDebugInfo(Graphics2D g) {
        g.setColor(new Color(255, 255, 255, 200));
        g.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        int y = 150;
        g.drawString(String.format("FPS: %.1f", currentFPS), 20, y);
        g.drawString("Memory: " + memoryUsage + " MB", 20, y + 15);
        g.drawString("Physics Bodies: " + physicsEngine.getBodies().size(), 20, y + 30);
        g.drawString("Blocks: " + blocks.size(), 20, y + 45);
        g.drawString("State: " + currentState, 20, y + 60);
    }
    
    /**
     * Handles collision events
     */
    private void onCollision(PhysicsEngine.PhysicsBody bodyA, 
                            PhysicsEngine.PhysicsBody bodyB,
                            PhysicsEngine.CollisionInfo info) {
        // Check if current block landed
        if (currentBlock != null && bodyA.userData == currentBlock) {
            onBlockLanded();
        }
    }
    
    /**
     * Handles block landing
     */
    private void onBlockLanded() {
        if (currentBlock == null) return;
        
        // Calculate landing quality
        double landingX = currentBlock.getX() + currentBlock.getWidth() / 2;
        double targetX = 640;
        
        if (blocks.size() > 0) {
            BlockSystem topBlock = blocks.get(blocks.size() - 1);
            targetX = topBlock.getX() + topBlock.getWidth() / 2;
        }
        
        double offset = Math.abs(landingX - targetX);
        
        // Calculate score
        int blockScore = 100;
        if (offset < 5) {
            blockScore = 500; // Perfect!
            currentBlock.onPerfectLand();
            renderingEngine.shakeCamera(5, 0.2);
        } else if (offset < 20) {
            blockScore = 200; // Good
        }
        
        score += blockScore;
        
        // Add block to tower
        blocks.add(currentBlock);
        physicsEngine.addBody(currentBlock.getPhysicsBody());
        currentBlock.onLand(currentBlock.getPhysicsBody().vy);
        
        // Update tower height
        towerHeight++;
        
        // Spawn next block
        currentBlock = null;
        SwingUtilities.invokeLater(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {}
            spawnNewBlock();
        });
    }
    
    /**
     * Drops the current block
     */
    private void dropBlock() {
        if (currentBlock != null && craneSystem.hasBlock()) {
            PhysicsEngine.PhysicsBody droppedBlock = craneSystem.releaseBlock();
            if (droppedBlock != null) {
                physicsEngine.addBody(droppedBlock);
            }
        }
    }
    
    /**
     * Game over
     */
    private void gameOver(String reason) {
        currentState = GameState.GAME_OVER;
        gameLoop.stop();
        System.out.println("💀 Game Over: " + reason);
        System.out.println("Final Score: " + score);
        System.out.println("Tower Height: " + towerHeight);
    }
    
    /**
     * Victory
     */
    private void victory() {
        currentState = GameState.VICTORY;
        gameLoop.stop();
        System.out.println("🏆 Victory! Tower completed!");
        System.out.println("Final Score: " + score);
    }
    
    // Input handling
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SPACE:
                if (currentState == GameState.PLAYING) {
                    dropBlock();
                }
                break;
                
            case KeyEvent.VK_P:
                if (currentState == GameState.PLAYING) {
                    currentState = GameState.PAUSED;
                    gameLoop.pause();
                } else if (currentState == GameState.PAUSED) {
                    currentState = GameState.PLAYING;
                    gameLoop.resume();
                }
                break;
                
            case KeyEvent.VK_D:
                showDebugInfo = !showDebugInfo;
                break;
                
            case KeyEvent.VK_R:
                if (currentState == GameState.GAME_OVER) {
                    restartGame();
                }
                break;
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {}
    
    @Override
    public void keyTyped(KeyEvent e) {}
    
    /**
     * Restarts the game
     */
    private void restartGame() {
        // Clear physics
        physicsEngine.clearBodies();
        
        // Clear blocks
        blocks.clear();
        
        // Reset state
        score = 0;
        towerHeight = 0;
        towerStability = 100.0;
        currentBlock = null;
        
        // Restart
        startGame();
    }
    
    /**
     * Main entry point
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("javaBloxx 2.0 - by joseAlejandro");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            
            TowerBloxxGame game = new TowerBloxxGame();
            frame.add(game);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
            System.out.println("🚀 javaBloxx 2.0 - Professional Refactor");
            System.out.println("📦 Modular Architecture");
            System.out.println("⚡ 60 FPS Game Loop");
            System.out.println("🎮 Physics Engine");
            System.out.println("🎨 Scalable Rendering");
            System.out.println("✨ Enhanced Animations");
        });
    }
}
