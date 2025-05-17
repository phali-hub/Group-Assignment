package com.gluonapplication;

import java.util.ArrayList;
import java.util.List;

public class Category {
    private String name;
    private List<Question> questions;

    public Category(String name) {
        this.name = name;
        this.questions = new ArrayList<>();
    }

    public void addQuestion(Question question) {
        questions.add(question);
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public String getName() {
        return name;
    }
}
