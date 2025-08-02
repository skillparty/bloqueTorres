package com.skillparty.towerblox;

import com.skillparty.towerblox.game.GameOverManager;
import com.skillparty.towerblox.game.physics.Block;
import com.skillparty.towerblox.game.physics.Tower;
import com.skillparty.towerblox.utils.Constants;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.awt.Color;

/**
 * Unit tests for GameOverManager class
 */
public class GameOverManagerTest {
    private GameOverManager gameOverManager;
    private Tower tower;

    @Before
    public void setUp() {
        tower = new Tower(Constants.GAME_WIDTH, Constants.GROUND_LEVEL);
        gameOverManager = new GameOverManager(tower);
    }

    @Test
    public void testInitialState() {
        assertFalse(gameOverManager.isGameOver());
        assertNull(gameOverManager.getGameOverReason());
        assertEquals(0, gameOverManager.getGameOverTime());
    }

    @Test
    public void testNoGameOverWithEmptyTower() {
        GameOverManager.GameOverReason reason = gameOverManager.checkGameOverConditions(null);
        assertNull(reason);
        assertFalse(gameOverManager.isGameOver());
    }

    @Test
    public void testBlockFellOffScreen() {
        Block block = new Block(-100, 100, 50, 30, Color.BLUE);
        block.drop();
        
        GameOverManager.GameOverReason reason = gameOverManager.checkGameOverConditions(block);
        assertEquals(GameOverManager.GameOverReason.BLOCK_FELL_OFF, reason);
        assertTrue(gameOverManager.isGameOver());
    }

    @Test
    public void testBlockFellOffRightSide() {
        Block block = new Block(Constants.GAME_WIDTH + 100, 100, 50, 30, Color.BLUE);
        block.drop();
        
        GameOverManager.GameOverReason reason = gameOverManager.checkGameOverConditions(block);
        assertEquals(GameOverManager.GameOverReason.BLOCK_FELL_OFF, reason);
        assertTrue(gameOverManager.isGameOver());
    }

    @Test
    public void testBlockFellBelowScreen() {
        Block block = new Block(100, Constants.GAME_HEIGHT + 200, 50, 30, Color.BLUE);
        block.drop();
        
        GameOverManager.GameOverReason reason = gameOverManager.checkGameOverConditions(block);
        assertEquals(GameOverManager.GameOverReason.BLOCK_FELL_OFF, reason);
        assertTrue(gameOverManager.isGameOver());
    }

    @Test
    public void testBlockInBoundsNoGameOver() {
        Block block = new Block(100, 100, 50, 30, Color.BLUE);
        block.drop();
        
        GameOverManager.GameOverReason reason = gameOverManager.checkGameOverConditions(block);
        assertNull(reason);
        assertFalse(gameOverManager.isGameOver());
    }

    @Test
    public void testNonDroppedBlockNoGameOver() {
        Block block = new Block(-100, 100, 50, 30, Color.BLUE);
        // Don't drop the block
        
        GameOverManager.GameOverReason reason = gameOverManager.checkGameOverConditions(block);
        assertNull(reason);
        assertFalse(gameOverManager.isGameOver());
    }

    @Test
    public void testManualGameOver() {
        GameOverManager.GameOverReason reason = gameOverManager.triggerManualGameOver(
            GameOverManager.GameOverReason.MANUAL_QUIT);
        
        assertEquals(GameOverManager.GameOverReason.MANUAL_QUIT, reason);
        assertTrue(gameOverManager.isGameOver());
        assertEquals(GameOverManager.GameOverReason.MANUAL_QUIT, gameOverManager.getGameOverReason());
        assertTrue(gameOverManager.getGameOverTime() > 0);
    }

    @Test
    public void testGameOverOnlyTriggersOnce() {
        // First trigger
        GameOverManager.GameOverReason reason1 = gameOverManager.triggerManualGameOver(
            GameOverManager.GameOverReason.MANUAL_QUIT);
        
        // Second trigger should return the same reason
        GameOverManager.GameOverReason reason2 = gameOverManager.triggerManualGameOver(
            GameOverManager.GameOverReason.PHYSICS_ERROR);
        
        assertEquals(reason1, reason2);
        assertEquals(GameOverManager.GameOverReason.MANUAL_QUIT, reason2);
    }

    @Test
    public void testReset() {
        gameOverManager.triggerManualGameOver(GameOverManager.GameOverReason.MANUAL_QUIT);
        assertTrue(gameOverManager.isGameOver());
        
        gameOverManager.reset();
        
        assertFalse(gameOverManager.isGameOver());
        assertNull(gameOverManager.getGameOverReason());
        assertEquals(0, gameOverManager.getGameOverTime());
    }

    @Test
    public void testGameOverDetails() {
        String details = gameOverManager.getGameOverDetails();
        assertTrue(details.contains("Game is still active"));
        
        gameOverManager.triggerManualGameOver(GameOverManager.GameOverReason.TOWER_COLLAPSED);
        
        details = gameOverManager.getGameOverDetails();
        assertTrue(details.contains("Game Over"));
        assertTrue(details.contains("Tower collapsed"));
        assertTrue(details.contains("Tower Height"));
        assertTrue(details.contains("Tower Stability"));
    }

    @Test
    public void testImprovementTips() {
        // Test different game over reasons
        gameOverManager.triggerManualGameOver(GameOverManager.GameOverReason.TOWER_COLLAPSED);
        String tip = gameOverManager.getImprovementTip();
        assertTrue(tip.contains("align blocks"));
        
        gameOverManager.reset();
        gameOverManager.triggerManualGameOver(GameOverManager.GameOverReason.BLOCK_FELL_OFF);
        tip = gameOverManager.getImprovementTip();
        assertTrue(tip.contains("Time your drops"));
        
        gameOverManager.reset();
        gameOverManager.triggerManualGameOver(GameOverManager.GameOverReason.TOWER_TOO_UNSTABLE);
        tip = gameOverManager.getImprovementTip();
        assertTrue(tip.contains("perfect alignment"));
        
        gameOverManager.reset();
        gameOverManager.triggerManualGameOver(GameOverManager.GameOverReason.BLOCK_MISSED_TOWER);
        tip = gameOverManager.getImprovementTip();
        assertTrue(tip.contains("crane position"));
        
        gameOverManager.reset();
        gameOverManager.triggerManualGameOver(GameOverManager.GameOverReason.PHYSICS_ERROR);
        tip = gameOverManager.getImprovementTip();
        assertTrue(tip.contains("technical issue"));
        
        gameOverManager.reset();
        gameOverManager.triggerManualGameOver(GameOverManager.GameOverReason.MANUAL_QUIT);
        tip = gameOverManager.getImprovementTip();
        assertTrue(tip.contains("Thanks for playing"));
    }

    @Test
    public void testSetTower() {
        Tower newTower = new Tower(400, 300);
        gameOverManager.setTower(newTower);
        
        // Should not crash and should work with new tower
        GameOverManager.GameOverReason reason = gameOverManager.checkGameOverConditions(null);
        assertNull(reason);
    }

    @Test
    public void testGameOverReasonMessages() {
        assertEquals("Tower collapsed due to instability!", 
                    GameOverManager.GameOverReason.TOWER_COLLAPSED.getMessage());
        assertEquals("Block fell off the screen!", 
                    GameOverManager.GameOverReason.BLOCK_FELL_OFF.getMessage());
        assertEquals("Tower became too unstable!", 
                    GameOverManager.GameOverReason.TOWER_TOO_UNSTABLE.getMessage());
        assertEquals("Block missed the tower completely!", 
                    GameOverManager.GameOverReason.BLOCK_MISSED_TOWER.getMessage());
        assertEquals("Physics error occurred!", 
                    GameOverManager.GameOverReason.PHYSICS_ERROR.getMessage());
        assertEquals("Player quit the game", 
                    GameOverManager.GameOverReason.MANUAL_QUIT.getMessage());
    }

    @Test
    public void testCheckGameOverAfterGameOver() {
        gameOverManager.triggerManualGameOver(GameOverManager.GameOverReason.MANUAL_QUIT);
        
        // Subsequent checks should return the same reason
        Block block = new Block(100, 100, 50, 30, Color.BLUE);
        GameOverManager.GameOverReason reason = gameOverManager.checkGameOverConditions(block);
        
        assertEquals(GameOverManager.GameOverReason.MANUAL_QUIT, reason);
    }

    @Test
    public void testTowerWithBlocks() {
        // Add some blocks to tower
        Block block1 = new Block(100, Constants.GROUND_LEVEL - 30, 50, 30, Color.BLUE);
        Block block2 = new Block(100, Constants.GROUND_LEVEL - 60, 50, 30, Color.RED);
        
        tower.addBlock(block1);
        tower.addBlock(block2);
        
        // Should not trigger game over with stable tower
        GameOverManager.GameOverReason reason = gameOverManager.checkGameOverConditions(null);
        assertNull(reason);
    }
}