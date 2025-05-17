package com.gluonapplication;

import javafx.scene.image.Image;
import java.util.List;
import java.util.Objects;

public class Question {
    private final String questionText;
    private final List<Image> optionImages;
    private final Image correctImage;
    private final String correctAnswer;
    private final String questionVideo;
    private final String questionType; // "image", "video", or "text"
    private final String difficulty;   // "easy", "medium", "hard"

    public Question(String questionText,
                    List<Image> optionImages,
                    Image correctImage,
                    String correctAnswer,
                    String questionVideo,
                    String questionType,
                    String difficulty) {
        this.questionText = Objects.requireNonNull(questionText, "Question text cannot be null");
        this.optionImages = Objects.requireNonNull(optionImages, "Option images cannot be null");
        this.correctImage = correctImage; // Can be null for video questions
        this.correctAnswer = Objects.requireNonNull(correctAnswer, "Correct answer cannot be null");
        this.questionVideo = questionVideo; // Can be null for image questions
        this.questionType = validateQuestionType(questionType);
        this.difficulty = validateDifficulty(difficulty);
    }

    // Validation methods
    private String validateQuestionType(String type) {
        if (!List.of("image", "video", "text").contains(type)) {
            throw new IllegalArgumentException("Invalid question type: " + type);
        }
        return type;
    }

    private String validateDifficulty(String difficulty) {
        if (!List.of("easy", "medium", "hard").contains(difficulty)) {
            throw new IllegalArgumentException("Invalid difficulty: " + difficulty);
        }
        return difficulty;
    }

    // Getters
    public String getQuestionText() {
        return questionText;
    }

    public List<Image> getOptionImages() {
        return List.copyOf(optionImages); // Return immutable copy
    }

    public Image getCorrectImage() {
        return correctImage;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String getQuestionVideo() {
        return questionVideo;
    }

    public String getQuestionType() {
        return questionType;
    }

    public String getDifficulty() {
        return difficulty;
    }

    // Utility methods
    public boolean isImageQuestion() {
        return "image".equals(questionType);
    }

    public boolean isVideoQuestion() {
        return "video".equals(questionType);
    }

    public boolean isTextQuestion() {
        return "text".equals(questionType);
    }

    @Override
    public String toString() {
        return "Question{" +
                "text='" + questionText + '\'' +
                ", type='" + questionType + '\'' +
                ", difficulty='" + difficulty + '\'' +
                '}';
    }
}