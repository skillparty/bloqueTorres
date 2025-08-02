package com.skillparty.towerblox;

import com.skillparty.towerblox.game.physics.Block;
import com.skillparty.towerblox.game.physics.Tower;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.awt.Color;

/**
 * Unit tests for Tower class
 */
public class TowerTest {
    private Tower tower;
    private static final int GAME_WIDTH = 800;
    private static final int GROUND_LEVEL = 550;
    private static final double DELTA = 0.001;

    @Before
    public void setUp() {
        tower = new Tower(GAME_WIDTH, GROUND_LEVEL);
    }

    @Test
    public void testInitialState() {
        assertTrue(tower.isEmpty());
        assertEquals(0, tower.getHeight());
        assertEquals(0, tower.getMaxHeight());
        assertTrue(tower.isStable());
        assertEquals(0.0, tower.getInstabilityScore(), DELTA);
        assertEquals(0.0, tower.getTiltAngle(), DELTA);
    }

    @Test
    public void testAddSingleBlock() {
        Block block = new Block(100, 100, 50, 30, Color.BLUE);
        tower.addBlock(block);
        
        assertFalse(tower.isEmpty());
        assertEquals(1, tower.getHeight());
        assertEquals(1, tower.getMaxHeight());
        assertTrue(tower.isStable()); // Single block should always be stable
        assertTrue(tower.getBlocks().contains(block));
    }

    @Test
    public void testAddMultipleBlocks() {
        Block block1 = new Block(100, 100, 50, 30, Color.BLUE);
        Block block2 = new Block(100, 70, 50, 30, Color.RED);
        Block block3 = new Block(105, 40, 50, 30, Color.GREEN);
        
        tower.addBlock(block1);
        tower.addBlock(block2);
        tower.addBlock(block3);
        
        assertEquals(3, tower.getHeight());
        assertEquals(3, tower.getMaxHeight());
        assertEquals(3, tower.getBlocks().size());
    }

    @Test
    public void testPerfectAlignment() {
        Block block1 = new Block(100, 100, 50, 30, Color.BLUE);
        Block block2 = new Block(100, 70, 50, 30, Color.RED); // Perfect alignment
        
        tower.addBlock(block1);
        tower.addBlock(block2);
        
        assertTrue(tower.isStable());
        assertEquals(100.0, tower.getAverageAlignment(), DELTA);
        assertEquals(1, tower.getPerfectAlignments());
    }

    @Test
    public void testMisalignment() {
        Block block1 = new Block(100, 100, 50, 30, Color.BLUE);
        Block block2 = new Block(150, 70, 50, 30, Color.RED); // Misaligned
        
        tower.addBlock(block1);
        tower.addBlock(block2);
        
        assertTrue(tower.getAverageAlignment() < 100.0);
        assertEquals(0, tower.getPerfectAlignments());
        assertTrue(tower.getInstabilityScore() > 0);
    }

    @Test
    public void testTopBlock() {
        Block block1 = new Block(100, 100, 50, 30, Color.BLUE);
        Block block2 = new Block(100, 70, 50, 30, Color.RED);
        Block block3 = new Block(100, 40, 50, 30, Color.GREEN);
        
        tower.addBlock(block1);
        tower.addBlock(block2);
        tower.addBlock(block3);
        
        Block topBlock = tower.getTopBlock();
        assertNotNull(topBlock);
        assertEquals(block3, topBlock); // Should be the highest block (lowest Y)
    }

    @Test
    public void testCollisionDetection() {
        Block block1 = new Block(100, 100, 50, 30, Color.BLUE);
        tower.addBlock(block1);
        
        Block collidingBlock = new Block(120, 110, 50, 30, Color.RED);
        Block nonCollidingBlock = new Block(200, 200, 50, 30, Color.GREEN);
        
        assertTrue(tower.hasCollision(collidingBlock));
        assertFalse(tower.hasCollision(nonCollidingBlock));
    }

    @Test
    public void testTowerCenterX() {
        Block block1 = new Block(100, 100, 50, 30, Color.BLUE);
        Block block2 = new Block(100, 70, 50, 30, Color.RED);
        
        tower.addBlock(block1);
        tower.addBlock(block2);
        
        double centerX = tower.getTowerCenterX();
        assertEquals(125.0, centerX, 1.0); // Center of 50-width blocks at x=100
    }

    @Test
    public void testTowerHeightPixels() {
        Block block1 = new Block(100, 100, 50, 30, Color.BLUE);
        Block block2 = new Block(100, 70, 50, 30, Color.RED);
        
        tower.addBlock(block1);
        tower.addBlock(block2);
        
        int heightPixels = tower.getTowerHeightPixels();
        assertEquals(60, heightPixels); // From y=70 to y=130 (100+30)
    }

    @Test
    public void testReset() {
        Block block1 = new Block(100, 100, 50, 30, Color.BLUE);
        Block block2 = new Block(150, 70, 50, 30, Color.RED);
        
        tower.addBlock(block1);
        tower.addBlock(block2);
        
        assertFalse(tower.isEmpty());
        assertTrue(tower.getInstabilityScore() > 0);
        
        tower.reset();
        
        assertTrue(tower.isEmpty());
        assertEquals(0, tower.getHeight());
        assertEquals(0, tower.getMaxHeight());
        assertTrue(tower.isStable());
        assertEquals(0.0, tower.getInstabilityScore(), DELTA);
    }

    @Test
    public void testStabilityCalculation() {
        // Create a very unstable tower
        Block block1 = new Block(100, 100, 50, 30, Color.BLUE);
        Block block2 = new Block(200, 70, 50, 30, Color.RED); // Very misaligned
        Block block3 = new Block(300, 40, 50, 30, Color.GREEN); // Even more misaligned
        
        tower.addBlock(block1);
        tower.addBlock(block2);
        tower.addBlock(block3);
        
        assertTrue(tower.getInstabilityScore() > 0.5); // Should be quite unstable
        assertFalse(tower.isStable()); // Should be marked as unstable
    }

    @Test
    public void testTiltAngleCalculation() {
        // Create a tilted tower
        Block block1 = new Block(100, 100, 50, 30, Color.BLUE);
        Block block2 = new Block(110, 70, 50, 30, Color.RED);
        Block block3 = new Block(120, 40, 50, 30, Color.GREEN);
        
        tower.addBlock(block1);
        tower.addBlock(block2);
        tower.addBlock(block3);
        
        double tiltAngle = tower.getTiltAngle();
        assertTrue(Math.abs(tiltAngle) > 0); // Should have some tilt
    }

    @Test
    public void testStatistics() {
        Block block1 = new Block(100, 100, 50, 30, Color.BLUE);
        Block block2 = new Block(100, 70, 50, 30, Color.RED);
        
        tower.addBlock(block1);
        tower.addBlock(block2);
        
        String stats = tower.getStatistics();
        assertNotNull(stats);
        assertTrue(stats.contains("Height: 2"));
        assertTrue(stats.contains("Max: 2"));
        assertTrue(stats.contains("Stability:"));
        assertTrue(stats.contains("Perfect: 1"));
    }

    @Test
    public void testEmptyTowerTopBlock() {
        assertNull(tower.getTopBlock());
    }

    @Test
    public void testNullBlockHandling() {
        tower.addBlock(null);
        assertTrue(tower.isEmpty());
        
        assertFalse(tower.hasCollision(null));
    }

    @Test
    public void testBlocksCopyProtection() {
        Block block = new Block(100, 100, 50, 30, Color.BLUE);
        tower.addBlock(block);
        
        // Get blocks list and try to modify it
        tower.getBlocks().clear();
        
        // Original tower should still have the block
        assertEquals(1, tower.getHeight());
        assertFalse(tower.isEmpty());
    }
}