package com.gluonapplication;

import java.util.HashMap;
import java.util.Map;

public class Score {
    private int correctAnswers;
    private int totalQuestions;
    private Map<String, Boolean> unlockedLevels; // Tracks which levels are unlocked
    private String currentCategory;
    private String currentDifficulty;

    public Score() {
        this.correctAnswers = 0;
        this.totalQuestions = 0;
        this.unlockedLevels = new HashMap<>();
        initializeLevels();
    }

    private void initializeLevels() {
        // Initialize all levels as locked except Easy level for all categories
        String[] categories = {"Lilotho", "Lipapali", "Maele", "Moetlo", "Litsomo"};
        String[] difficulties = {"Easy", "Medium", "Hard"};

        for (String category : categories) {
            for (String difficulty : difficulties) {
                String key = getLevelKey(category, difficulty);
                // Only unlock Easy levels initially
                unlockedLevels.put(key, difficulty.equals("Easy"));
            }
        }
    }

    public void incrementCorrectAnswers() {
        correctAnswers++;
    }

    public void incrementTotalQuestions() {
        totalQuestions++;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public double getScorePercentage() {
        if (totalQuestions == 0) {
            return 0.0;
        }
        return (correctAnswers / (double) totalQuestions) * 100;
    }

    public void resetScore() {
        this.correctAnswers = 0;
        this.totalQuestions = 0;
    }

    public void setCurrentLevel(String category, String difficulty) {
        this.currentCategory = category;
        this.currentDifficulty = difficulty;
    }

    public void updateLevelProgress() {
        if (currentCategory == null || currentDifficulty == null) {
            return;
        }

        // Check if player passed the level (3/5 or more correct answers)
        boolean passed = correctAnswers >= 3 && totalQuestions == 5;

        if (passed) {
            unlockNextLevel();
        }
    }

    private void unlockNextLevel() {
        String nextDifficulty = getNextDifficulty(currentDifficulty);
        if (nextDifficulty != null) {
            String key = getLevelKey(currentCategory, nextDifficulty);
            unlockedLevels.put(key, true);
        }
    }

    private String getNextDifficulty(String currentDifficulty) {
        switch (currentDifficulty) {
            case "Easy": return "Medium";
            case "Medium": return "Hard";
            default: return null; // No level after Hard
        }
    }

    public boolean isLevelUnlocked(String category, String difficulty) {
        String key = getLevelKey(category, difficulty);
        return unlockedLevels.getOrDefault(key, false);
    }

    private String getLevelKey(String category, String difficulty) {
        return category + "_" + difficulty;
    }

    public String getCurrentCategory() {
        return currentCategory;
    }

    public String getCurrentDifficulty() {
        return currentDifficulty;
    }
}