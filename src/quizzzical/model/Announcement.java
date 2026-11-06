package quizzzical.model;

import quizzzical.model.Question;

public class Announcement extends Question {
    private String[] options;

    public Announcement(String text, String[] options, String correct, int points) {
        super(text, correct, points);
        this.options = options.clone();
    }

    public String[] getOptions() {
        return options.clone();
    }

    @Override
    public boolean checkAnswer(String userAnswer) {
        if (userAnswer == null) {
            return false;
        }
        return correctAnswer.trim().equalsIgnoreCase(userAnswer.trim());
    }

    @Override // inheririting
    public String getQuestionType() {
        return "MULTIPLE";
    }
}