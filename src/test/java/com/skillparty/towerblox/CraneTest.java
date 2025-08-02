package com.skillparty.towerblox;

import com.skillparty.towerblox.game.physics.Block;
import com.skillparty.towerblox.game.physics.Crane;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.awt.Color;

/**
 * Unit tests for Crane class
 */
public class CraneTest {
    private Crane crane;
    private static final int GAME_WIDTH = 800;
    private static final double DELTA = 0.001;

    @Before
    public void setUp() {
        crane = new Crane(GAME_WIDTH / 2, 50, GAME_WIDTH);
    }

    @Test
    public void testInitialState() {
        assertEquals(GAME_WIDTH / 2.0, crane.getX(), DELTA);
        assertEquals(50.0, crane.getY(), DELTA);
        assertTrue(crane.isMovingRight());
        assertFalse(crane.isAnimating());
        assertNull(crane.getCurrentBlock());
        assertTrue(crane.isReadyForNewBlock());
    }

    @Test
    public void testMovement() {
        double initialX = crane.getX();
        
        // Simulate movement updates
        for (int i = 0; i < 10; i++) {
            crane.update(16); // ~60fps timing
        }
        
        // Crane should have moved
        assertNotEquals(initialX, crane.getX(), DELTA);
    }

    @Test
    public void testBoundaryCollision() {
        // Move crane to right boundary
        crane.setX(GAME_WIDTH - 50);
        assertTrue(crane.isMovingRight());
        
        // Update several times to trigger boundary collision
        for (int i = 0; i < 100; i++) {
            crane.update(16);
        }
        
        // Should now be moving left
        assertFalse(crane.isMovingRight());
    }

    @Test
    public void testLeftBoundaryCollision() {
        // Move crane to left boundary
        crane.setX(50);
        crane.setMovingRight(false);
        
        // Update several times to trigger boundary collision
        for (int i = 0; i < 100; i++) {
            crane.update(16);
        }
        
        // Should now be moving right
        assertTrue(crane.isMovingRight());
    }

    @Test
    public void testSetCurrentBlock() {
        Block block = new Block(100, 100, 50, 30, Color.BLUE);
        crane.setCurrentBlock(block);
        
        assertEquals(block, crane.getCurrentBlock());
        assertFalse(crane.isReadyForNewBlock());
        
        // Block should be positioned at crane location
        assertEquals(crane.getX() - block.getWidth() / 2, block.getX(), DELTA);
    }

    @Test
    public void testDropBlock() {
        Block block = new Block(100, 100, 50, 30, Color.BLUE);
        crane.setCurrentBlock(block);
        
        assertFalse(block.isDropped());
        assertFalse(crane.isAnimating());
        
        crane.dropBlock();
        
        assertTrue(block.isDropped());
        assertTrue(crane.isAnimating());
    }

    @Test
    public void testDropBlockWithoutBlock() {
        // Should not crash when trying to drop without a block
        crane.dropBlock();
        assertFalse(crane.isAnimating());
    }

    @Test
    public void testSpeedAdjustment() {
        double originalSpeed = crane.getSpeed();
        
        crane.setSpeed(5.0);
        assertEquals(5.0, crane.getSpeed(), DELTA);
        
        // Test speed clamping
        crane.setSpeed(15.0); // Above max
        assertEquals(10.0, crane.getSpeed(), DELTA);
        
        crane.setSpeed(0.1); // Below min
        assertEquals(0.5, crane.getSpeed(), DELTA);
    }

    @Test
    public void testAnimation() {
        Block block = new Block(100, 100, 50, 30, Color.BLUE);
        crane.setCurrentBlock(block);
        
        crane.dropBlock();
        assertTrue(crane.isAnimating());
        assertTrue(crane.isOpening());
        assertFalse(crane.isClosing());
        
        // Simulate animation progression
        for (int i = 0; i < 10; i++) {
            crane.update(25); // Slightly longer than frame time to trigger updates
        }
        
        // Animation should progress
        assertTrue(crane.getAnimationFrame() > 0);
    }

    @Test
    public void testReset() {
        Block block = new Block(100, 100, 50, 30, Color.BLUE);
        crane.setCurrentBlock(block);
        crane.setSpeed(5.0);
        crane.setX(200);
        crane.setMovingRight(false);
        crane.dropBlock();
        
        crane.reset();
        
        assertEquals(GAME_WIDTH / 2.0, crane.getX(), DELTA);
        assertEquals(crane.getBaseSpeed(), crane.getSpeed(), DELTA);
        assertTrue(crane.isMovingRight());
        assertNull(crane.getCurrentBlock());
        assertFalse(crane.isAnimating());
        assertTrue(crane.isReadyForNewBlock());
    }

    @Test
    public void testDropZone() {
        Block block = new Block(100, 100, 50, 30, Color.BLUE);
        crane.setCurrentBlock(block);
        
        double leftZone = crane.getDropZoneLeft();
        double rightZone = crane.getDropZoneRight();
        
        assertTrue(leftZone < rightZone);
        assertEquals(crane.getX() - block.getWidth() / 2, leftZone, DELTA);
        assertEquals(crane.getX() + block.getWidth() / 2, rightZone, DELTA);
    }

    @Test
    public void testDropZoneWithoutBlock() {
        double leftZone = crane.getDropZoneLeft();
        double rightZone = crane.getDropZoneRight();
        
        assertTrue(leftZone < rightZone);
        // Should use default zone size when no block
        assertEquals(50.0, rightZone - leftZone, DELTA);
    }

    @Test
    public void testStatus() {
        String status = crane.getStatus();
        assertNotNull(status);
        assertTrue(status.contains("Pos:"));
        assertTrue(status.contains("Speed:"));
        assertTrue(status.contains("Block:"));
        assertTrue(status.contains("Animation:"));
    }

    @Test
    public void testBlockPositionUpdate() {
        Block block = new Block(100, 100, 50, 30, Color.BLUE);
        crane.setCurrentBlock(block);
        
        double initialBlockX = block.getX();
        
        // Move crane
        crane.setX(crane.getX() + 50);
        crane.update(16);
        
        // Block should move with crane
        assertNotEquals(initialBlockX, block.getX(), DELTA);
        assertEquals(crane.getX() - block.getWidth() / 2, block.getX(), DELTA);
    }

    @Test
    public void testBlockPositionAfterDrop() {
        Block block = new Block(100, 100, 50, 30, Color.BLUE);
        crane.setCurrentBlock(block);
        
        crane.dropBlock();
        
        // Move crane after dropping
        double craneXBeforeMove = crane.getX();
        crane.setX(crane.getX() + 100);
        crane.update(16);
        
        // Block should not move with crane after being dropped
        assertEquals(craneXBeforeMove - block.getWidth() / 2, block.getX(), DELTA);
    }

    @Test
    public void testXPositionClamping() {
        // Test left boundary clamping
        crane.setX(-100);
        assertTrue(crane.getX() >= 0);
        
        // Test right boundary clamping
        crane.setX(GAME_WIDTH + 100);
        assertTrue(crane.getX() <= GAME_WIDTH);
    }

    @Test
    public void testReadyForNewBlockAfterDrop() {
        Block block = new Block(100, 100, 50, 30, Color.BLUE);
        crane.setCurrentBlock(block);
        
        assertFalse(crane.isReadyForNewBlock());
        
        crane.dropBlock();
        
        assertTrue(crane.isReadyForNewBlock()); // Should be ready after dropping
    }
}