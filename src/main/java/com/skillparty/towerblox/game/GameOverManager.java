package com.skillparty.towerblox.game;

import com.skillparty.towerblox.game.physics.Block;
import com.skillparty.towerblox.game.physics.Tower;
import com.skillparty.towerblox.utils.Constants;

/**
 * Manages game over conditions and logic
 */
public class GameOverManager {
    
    public enum GameOverReason {
        TOWER_COLLAPSED("Tower collapsed due to instability!"),
        BLOCK_FELL_OFF("Block fell off the screen!"),
        TOWER_TOO_UNSTABLE("Tower became too unstable!"),
        BLOCK_MISSED_TOWER("Block missed the tower completely!"),
        PHYSICS_ERROR("Physics error occurred!"),
        MANUAL_QUIT("Player quit the game");
        
        private final String message;
        
        GameOverReason(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
    }
    
    private Tower tower;
    private boolean gameOverTriggered;
    private GameOverReason gameOverReason;
    private long gameOverTime;
    
    public GameOverManager(Tower tower) {
        this.tower = tower;
        this.gameOverTriggered = false;
        this.gameOverReason = null;
        this.gameOverTime = 0;
    }
    
    /**
     * Checks all game over conditions
     * @param currentBlock The currently active block (if any)
     * @return GameOverReason if game should end, null otherwise
     */
    public GameOverReason checkGameOverConditions(Block currentBlock) {
        if (gameOverTriggered) {
            return gameOverReason;
        }
        
        // Check tower stability
        if (checkTowerStability()) {
            return triggerGameOver(GameOverReason.TOWER_COLLAPSED);
        }
        
        // Check if current block fell off screen
        if (currentBlock != null && checkBlockFellOff(currentBlock)) {
            return triggerGameOver(GameOverReason.BLOCK_FELL_OFF);
        }
        
        // Check if block missed tower completely
        if (currentBlock != null && currentBlock.isStable() && checkBlockMissedTower(currentBlock)) {
            return triggerGameOver(GameOverReason.BLOCK_MISSED_TOWER);
        }
        
        // Check overall tower instability
        if (checkTowerInstability()) {
            return triggerGameOver(GameOverReason.TOWER_TOO_UNSTABLE);
        }
        
        return null; // Game continues
    }
    
    /**
     * Checks if the tower has collapsed
     */
    private boolean checkTowerStability() {
        if (tower == null || tower.getBlocks().isEmpty()) {
            return false;
        }
        
        // Check if any block has fallen below the ground level significantly
        for (Block block : tower.getBlocks()) {
            if (block.getY() + block.getHeight() > Constants.GROUND_LEVEL + 100) {
                return true; // Block fell too far below ground
            }
        }
        
        // Check if tower has tilted too much
        return tower.getTiltAngle() > Math.PI / 6; // More than 30 degrees tilt
    }
    
    /**
     * Checks if a block fell off the screen
     */
    private boolean checkBlockFellOff(Block block) {
        if (block == null || !block.isDropped()) {
            return false;
        }
        
        // Check if block is completely off screen
        return block.getX() + block.getWidth() < 0 || 
               block.getX() > Constants.GAME_WIDTH ||
               block.getY() > Constants.GAME_HEIGHT + 100;
    }
    
    /**
     * Checks if a block missed the tower completely
     */
    private boolean checkBlockMissedTower(Block block) {
        if (tower == null || tower.getBlocks().isEmpty() || block == null) {
            return false;
        }
        
        // If block is stable and on the ground but not part of the tower
        if (block.isStable() && 
            block.getY() + block.getHeight() >= Constants.GROUND_LEVEL - 10) {
            
            // Check if block is horizontally aligned with any tower block
            double blockCenter = block.getX() + block.getWidth() / 2;
            
            for (Block towerBlock : tower.getBlocks()) {
                double towerBlockCenter = towerBlock.getX() + towerBlock.getWidth() / 2;
                double distance = Math.abs(blockCenter - towerBlockCenter);
                
                // If within reasonable distance, it's not a complete miss
                if (distance < (block.getWidth() + towerBlock.getWidth()) / 2 + 50) {
                    return false;
                }
            }
            
            return true; // Block is too far from any tower block
        }
        
        return false;
    }
    
    /**
     * Checks if the tower is becoming too unstable
     */
    private boolean checkTowerInstability() {
        if (tower == null || tower.getBlocks().size() < Constants.MIN_BLOCKS_FOR_INSTABILITY) {
            return false;
        }
        
        // Calculate overall instability
        double instabilityScore = tower.getInstabilityScore();
        
        return instabilityScore > Constants.TOWER_INSTABILITY_THRESHOLD;
    }
    
    /**
     * Triggers game over with the specified reason
     */
    private GameOverReason triggerGameOver(GameOverReason reason) {
        if (!gameOverTriggered) {
            gameOverTriggered = true;
            gameOverReason = reason;
            gameOverTime = System.currentTimeMillis();
            
            System.out.println("Game Over: " + reason.getMessage());
        }
        
        return reason;
    }
    
    /**
     * Manually triggers game over (for quit, etc.)
     */
    public GameOverReason triggerManualGameOver(GameOverReason reason) {
        return triggerGameOver(reason);
    }
    
    /**
     * Resets the game over manager for a new game
     */
    public void reset() {
        gameOverTriggered = false;
        gameOverReason = null;
        gameOverTime = 0;
    }
    
    /**
     * Gets detailed game over information
     */
    public String getGameOverDetails() {
        if (!gameOverTriggered || gameOverReason == null) {
            return "Game is still active";
        }
        
        StringBuilder details = new StringBuilder();
        details.append("Game Over: ").append(gameOverReason.getMessage()).append("\n");
        
        if (tower != null) {
            details.append("Tower Height: ").append(tower.getHeight()).append(" blocks\n");
            details.append("Tower Stability: ").append(String.format("%.1f%%", 
                (1.0 - tower.getInstabilityScore()) * 100)).append("\n");
            details.append("Tower Tilt: ").append(String.format("%.1f°", 
                Math.toDegrees(tower.getTiltAngle()))).append("\n");
        }
        
        long gameTime = gameOverTime - (gameOverTime - 60000); // Approximate game time
        details.append("Game Duration: ").append(gameTime / 1000).append(" seconds");
        
        return details.toString();
    }
    
    /**
     * Provides suggestions for improvement based on game over reason
     */
    public String getImprovementTip() {
        if (gameOverReason == null) {
            return "";
        }
        
        switch (gameOverReason) {
            case TOWER_COLLAPSED:
                return "Tip: Try to align blocks more carefully to maintain stability!";
                
            case BLOCK_FELL_OFF:
                return "Tip: Time your drops better to keep blocks on screen!";
                
            case TOWER_TOO_UNSTABLE:
                return "Tip: Focus on perfect alignment to build a stable tower!";
                
            case BLOCK_MISSED_TOWER:
                return "Tip: Watch the crane position and drop blocks closer to the tower!";
                
            case PHYSICS_ERROR:
                return "Tip: This was a technical issue, try starting a new game!";
                
            case MANUAL_QUIT:
                return "Thanks for playing! Try again to beat your high score!";
                
            default:
                return "Keep practicing to improve your Tower Bloxx skills!";
        }
    }
    
    // Getters
    public boolean isGameOver() {
        return gameOverTriggered;
    }
    
    public GameOverReason getGameOverReason() {
        return gameOverReason;
    }
    
    public long getGameOverTime() {
        return gameOverTime;
    }
    
    public void setTower(Tower tower) {
        this.tower = tower;
    }
}