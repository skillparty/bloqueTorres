package com.skillparty.towerblox;

import com.skillparty.towerblox.game.DifficultyLevel;
import com.skillparty.towerblox.score.HighScore;
import com.skillparty.towerblox.score.ScoreStorage;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Unit tests for ScoreStorage class
 */
public class ScoreStorageTest {
    private ScoreStorage scoreStorage;
    private Path tempDataPath;

    @Before
    public void setUp() throws IOException {
        // Create a temporary directory for testing
        tempDataPath = Files.createTempDirectory("towerblox-test");
        scoreStorage = new ScoreStorage();
    }

    @After
    public void tearDown() throws IOException {
        // Clean up temporary files
        if (tempDataPath != null && Files.exists(tempDataPath)) {
            Files.walk(tempDataPath)
                .sorted((a, b) -> b.compareTo(a)) // Delete files before directories
                .forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException e) {
                        // Ignore cleanup errors
                    }
                });
        }
    }

    @Test
    public void testInitialLoad() {
        List<HighScore> scores = scoreStorage.getHighScores();
        assertNotNull(scores);
        assertTrue(scores.size() <= 10); // Should not exceed max scores
    }

    @Test
    public void testAddQualifyingScore() {
        HighScore newScore = new HighScore("TST", 100000, DifficultyLevel.HARD);
        boolean added = scoreStorage.addScore(newScore);
        
        assertTrue(added);
        List<HighScore> scores = scoreStorage.getHighScores();
        assertTrue(scores.contains(newScore));
        assertEquals(newScore, scores.get(0)); // Should be at the top
    }

    @Test
    public void testAddNonQualifyingScore() {
        // First, fill up with high scores
        for (int i = 0; i < 10; i++) {
            scoreStorage.addScore(new HighScore("HI" + i, 50000 + i * 1000, DifficultyLevel.NORMAL));
        }
        
        // Try to add a low score
        HighScore lowScore = new HighScore("LOW", 1000, DifficultyLevel.EASY);
        boolean added = scoreStorage.addScore(lowScore);
        
        assertFalse(added);
        List<HighScore> scores = scoreStorage.getHighScores();
        assertFalse(scores.contains(lowScore));
    }

    @Test
    public void testScoreQualification() {
        assertTrue(scoreStorage.qualifiesForHighScore(100000)); // High score should qualify
        
        // Add some scores to fill the table
        for (int i = 0; i < 10; i++) {
            scoreStorage.addScore(new HighScore("TST", 10000 + i * 1000, DifficultyLevel.NORMAL));
        }
        
        assertTrue(scoreStorage.qualifiesForHighScore(25000)); // Higher than lowest
        assertFalse(scoreStorage.qualifiesForHighScore(5000)); // Lower than lowest
    }

    @Test
    public void testScoreRanking() {
        // Add some known scores
        scoreStorage.clearScores();
        scoreStorage.addScore(new HighScore("1ST", 30000, DifficultyLevel.HARD));
        scoreStorage.addScore(new HighScore("2ND", 20000, DifficultyLevel.NORMAL));
        scoreStorage.addScore(new HighScore("3RD", 10000, DifficultyLevel.EASY));
        
        assertEquals(1, scoreStorage.getScoreRank(35000)); // Would be 1st
        assertEquals(2, scoreStorage.getScoreRank(25000)); // Would be 2nd
        assertEquals(4, scoreStorage.getScoreRank(15000)); // Would be 4th
        assertEquals(5, scoreStorage.getScoreRank(5000));  // Would be 5th (last)
    }

    @Test
    public void testTopScores() {
        scoreStorage.clearScores();
        
        // Add 5 scores
        for (int i = 0; i < 5; i++) {
            scoreStorage.addScore(new HighScore("T" + i, (5 - i) * 1000, DifficultyLevel.NORMAL));
        }
        
        List<HighScore> top3 = scoreStorage.getTopScores(3);
        assertEquals(3, top3.size());
        assertEquals(5000, top3.get(0).getScore()); // Highest score first
        assertEquals(4000, top3.get(1).getScore());
        assertEquals(3000, top3.get(2).getScore());
    }

    @Test
    public void testClearScores() {
        scoreStorage.addScore(new HighScore("TST", 10000, DifficultyLevel.NORMAL));
        assertFalse(scoreStorage.getHighScores().isEmpty());
        
        scoreStorage.clearScores();
        assertTrue(scoreStorage.getHighScores().isEmpty());
    }

    @Test
    public void testScoreSorting() {
        scoreStorage.clearScores();
        
        // Add scores in random order
        scoreStorage.addScore(new HighScore("MID", 15000, DifficultyLevel.NORMAL));
        scoreStorage.addScore(new HighScore("LOW", 5000, DifficultyLevel.EASY));
        scoreStorage.addScore(new HighScore("HI ", 25000, DifficultyLevel.HARD));
        
        List<HighScore> scores = scoreStorage.getHighScores();
        assertEquals(3, scores.size());
        
        // Should be sorted by score descending
        assertEquals(25000, scores.get(0).getScore());
        assertEquals(15000, scores.get(1).getScore());
        assertEquals(5000, scores.get(2).getScore());
    }

    @Test
    public void testMaxScoresLimit() {
        scoreStorage.clearScores();
        
        // Add more than 10 scores
        for (int i = 0; i < 15; i++) {
            scoreStorage.addScore(new HighScore("T" + i, i * 1000, DifficultyLevel.NORMAL));
        }
        
        List<HighScore> scores = scoreStorage.getHighScores();
        assertEquals(10, scores.size()); // Should be limited to 10
        
        // Should keep the highest scores
        assertEquals(14000, scores.get(0).getScore()); // Highest
        assertEquals(5000, scores.get(9).getScore());  // 10th highest
    }

    @Test
    public void testStatistics() {
        scoreStorage.clearScores();
        scoreStorage.addScore(new HighScore("E1 ", 1000, DifficultyLevel.EASY));
        scoreStorage.addScore(new HighScore("N1 ", 2000, DifficultyLevel.NORMAL));
        scoreStorage.addScore(new HighScore("H1 ", 3000, DifficultyLevel.HARD));
        
        String stats = scoreStorage.getStatistics();
        assertNotNull(stats);
        assertTrue(stats.contains("3 total"));
        assertTrue(stats.contains("Highest: 3000"));
        assertTrue(stats.contains("Lowest: 1000"));
        assertTrue(stats.contains("Easy: 1"));
        assertTrue(stats.contains("Normal: 1"));
        assertTrue(stats.contains("Hard: 1"));
    }

    @Test
    public void testEmptyStatistics() {
        scoreStorage.clearScores();
        String stats = scoreStorage.getStatistics();
        assertEquals("No high scores recorded", stats);
    }

    @Test
    public void testNullScoreHandling() {
        boolean added = scoreStorage.addScore(null);
        assertFalse(added);
    }

    @Test
    public void testDataPath() {
        Path dataPath = scoreStorage.getDataPath();
        assertNotNull(dataPath);
        assertTrue(Files.exists(dataPath) || Files.isDirectory(dataPath.getParent()));
    }
}