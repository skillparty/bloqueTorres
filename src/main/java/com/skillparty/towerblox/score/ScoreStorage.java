package com.skillparty.towerblox.score;

import com.skillparty.towerblox.game.DifficultyLevel;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Handles persistence of high scores to file system
 */
public class ScoreStorage {
    private static final String DATA_DIR = "data";
    private static final String SCORES_FILE = "highscores.dat";
    private static final String BACKUP_FILE = "highscores.backup";
    private static final int MAX_SCORES = 10;
    
    private List<HighScore> highScores;
    private Path dataPath;
    private Path scoresPath;
    private Path backupPath;

    /**
     * Creates a new ScoreStorage instance and loads existing scores
     */
    public ScoreStorage() {
        initializePaths();
        highScores = new ArrayList<>();
        loadScores();
    }

    /**
     * Initializes file paths for data storage
     */
    private void initializePaths() {
        try {
            // Try to use user's home directory first
            String userHome = System.getProperty("user.home");
            dataPath = Paths.get(userHome, ".towerblox", DATA_DIR);
            
            // Create directories if they don't exist
            Files.createDirectories(dataPath);
            
        } catch (Exception e) {
            // Fallback to current directory
            dataPath = Paths.get(DATA_DIR);
            try {
                Files.createDirectories(dataPath);
            } catch (IOException ex) {
                System.err.println("Warning: Could not create data directory: " + ex.getMessage());
                dataPath = Paths.get(".");
            }
        }
        
        scoresPath = dataPath.resolve(SCORES_FILE);
        backupPath = dataPath.resolve(BACKUP_FILE);
    }

    /**
     * Loads high scores from file
     */
    public void loadScores() {
        highScores.clear();
        
        if (!Files.exists(scoresPath)) {
            System.out.println("No existing high scores file found, starting fresh");
            createDefaultScores();
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(Files.newInputStream(scoresPath)))) {
            
            @SuppressWarnings("unchecked")
            List<HighScore> loadedScores = (List<HighScore>) ois.readObject();
            
            if (loadedScores != null) {
                highScores.addAll(loadedScores);
                Collections.sort(highScores); // Ensure proper sorting
                
                // Limit to MAX_SCORES
                if (highScores.size() > MAX_SCORES) {
                    highScores = highScores.subList(0, MAX_SCORES);
                }
                
                System.out.println("Loaded " + highScores.size() + " high scores");
            }
            
        } catch (Exception e) {
            System.err.println("Error loading high scores: " + e.getMessage());
            
            // Try to load from backup
            if (loadFromBackup()) {
                System.out.println("Successfully recovered from backup");
            } else {
                System.out.println("Creating fresh high scores");
                createDefaultScores();
            }
        }
    }

    /**
     * Attempts to load scores from backup file
     */
    private boolean loadFromBackup() {
        if (!Files.exists(backupPath)) {
            return false;
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(Files.newInputStream(backupPath)))) {
            
            @SuppressWarnings("unchecked")
            List<HighScore> backupScores = (List<HighScore>) ois.readObject();
            
            if (backupScores != null) {
                highScores.clear();
                highScores.addAll(backupScores);
                Collections.sort(highScores);
                return true;
            }
            
        } catch (Exception e) {
            System.err.println("Error loading backup scores: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Creates default high scores for new installations
     */
    private void createDefaultScores() {
        highScores.clear();
        
        // Add some default scores to make the leaderboard interesting
        LocalDateTime baseDate = LocalDateTime.now().minusDays(30);
        
        highScores.add(new HighScore("DEV", 50000, DifficultyLevel.HARD, baseDate.plusDays(1)));
        highScores.add(new HighScore("PRO", 35000, DifficultyLevel.HARD, baseDate.plusDays(2)));
        highScores.add(new HighScore("ACE", 28000, DifficultyLevel.NORMAL, baseDate.plusDays(3)));
        highScores.add(new HighScore("TOP", 22000, DifficultyLevel.NORMAL, baseDate.plusDays(4)));
        highScores.add(new HighScore("WIN", 18000, DifficultyLevel.NORMAL, baseDate.plusDays(5)));
        highScores.add(new HighScore("GG ", 15000, DifficultyLevel.EASY, baseDate.plusDays(6)));
        highScores.add(new HighScore("OK ", 12000, DifficultyLevel.EASY, baseDate.plusDays(7)));
        highScores.add(new HighScore("HI ", 8000, DifficultyLevel.EASY, baseDate.plusDays(8)));
        highScores.add(new HighScore("GO ", 5000, DifficultyLevel.EASY, baseDate.plusDays(9)));
        highScores.add(new HighScore("TRY", 2000, DifficultyLevel.EASY, baseDate.plusDays(10)));
        
        Collections.sort(highScores);
        saveScores(); // Save the default scores
    }

    /**
     * Saves high scores to file with backup
     */
    public void saveScores() {
        try {
            // Create backup of existing file
            if (Files.exists(scoresPath)) {
                Files.copy(scoresPath, backupPath, StandardCopyOption.REPLACE_EXISTING);
            }
            
            // Save current scores
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new BufferedOutputStream(Files.newOutputStream(scoresPath)))) {
                
                oos.writeObject(new ArrayList<>(highScores));
                oos.flush();
            }
            
            System.out.println("High scores saved successfully");
            
        } catch (IOException e) {
            System.err.println("Error saving high scores: " + e.getMessage());
            throw new RuntimeException("Failed to save high scores", e);
        }
    }

    /**
     * Adds a new high score if it qualifies for the top 10
     * @param score The score to add
     * @return true if the score was added, false otherwise
     */
    public boolean addScore(HighScore score) {
        if (score == null) {
            return false;
        }
        
        // Add the score
        highScores.add(score);
        
        // Sort the list
        Collections.sort(highScores);
        
        // Keep only top MAX_SCORES
        if (highScores.size() > MAX_SCORES) {
            highScores = highScores.subList(0, MAX_SCORES);
        }
        
        // Check if the score made it to the top 10
        boolean qualified = highScores.contains(score);
        
        if (qualified) {
            saveScores();
        }
        
        return qualified;
    }

    /**
     * Checks if a score qualifies for the high score table
     * @param score The score to check
     * @return true if it would make the top 10
     */
    public boolean qualifiesForHighScore(int score) {
        if (highScores.size() < MAX_SCORES) {
            return true; // Always qualifies if we don't have 10 scores yet
        }
        
        HighScore lowestScore = highScores.get(highScores.size() - 1);
        return score > lowestScore.getScore();
    }

    /**
     * Gets the rank a score would have (1-based)
     * @param score The score to check
     * @return The rank (1 = highest), or -1 if it doesn't qualify
     */
    public int getScoreRank(int score) {
        for (int i = 0; i < highScores.size(); i++) {
            if (score > highScores.get(i).getScore()) {
                return i + 1;
            }
        }
        
        // If we get here, check if it would still make the top 10
        if (highScores.size() < MAX_SCORES) {
            return highScores.size() + 1;
        }
        
        return -1; // Doesn't qualify
    }

    /**
     * Gets a copy of the high scores list
     */
    public List<HighScore> getHighScores() {
        return new ArrayList<>(highScores);
    }

    /**
     * Gets the top N high scores
     */
    public List<HighScore> getTopScores(int count) {
        int limit = Math.min(count, highScores.size());
        return new ArrayList<>(highScores.subList(0, limit));
    }

    /**
     * Clears all high scores (for testing or reset)
     */
    public void clearScores() {
        highScores.clear();
        saveScores();
    }

    /**
     * Gets statistics about the high scores
     */
    public String getStatistics() {
        if (highScores.isEmpty()) {
            return "No high scores recorded";
        }
        
        int totalScores = highScores.size();
        int highestScore = highScores.get(0).getScore();
        int lowestScore = highScores.get(totalScores - 1).getScore();
        
        // Count by difficulty
        long easyCount = highScores.stream()
                .filter(hs -> hs.getDifficulty() == DifficultyLevel.EASY)
                .count();
        long normalCount = highScores.stream()
                .filter(hs -> hs.getDifficulty() == DifficultyLevel.NORMAL)
                .count();
        long hardCount = highScores.stream()
                .filter(hs -> hs.getDifficulty() == DifficultyLevel.HARD)
                .count();
        
        return String.format(
            "High Scores: %d total | Highest: %d | Lowest: %d | Easy: %d | Normal: %d | Hard: %d",
            totalScores, highestScore, lowestScore, easyCount, normalCount, hardCount
        );
    }

    /**
     * Gets the data directory path
     */
    public Path getDataPath() {
        return dataPath;
    }

    /**
     * Checks if the scores file exists
     */
    public boolean scoresFileExists() {
        return Files.exists(scoresPath);
    }
}