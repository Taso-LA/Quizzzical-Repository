package quizzzical.model;

import quizzzical.model.Question;

public class Student extends Question {
    public Student(String text, String correct, int points) {
        super(text, correct, points);
    }

    @Override
    public boolean checkAnswer(String userAnswer) {
        if (userAnswer == null) {
            return false;
        }
        String a = userAnswer.trim();
        return correctAnswer.trim().equalsIgnoreCase(a);
    }

    @Override
    public String getQuestionType() {
        return "TRUE_FALSE";
    }
}