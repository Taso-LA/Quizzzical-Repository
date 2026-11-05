package quizzzical;

import java.util.ArrayList;
import java.util.Collections;

public class Quiz {
    private ArrayList<Question> questions;
    private int score = 0;

    public Quiz() {
        questions = new ArrayList<>();
    }

    public void addQuestion(Question q) {
        if (q != null) {
            questions.add(q);
        }
    }

    public void shuffleQuestions() {
        Collections.shuffle(questions);
    }

    public void gradeQuiz(ArrayList<String> studentAnswers) {
        score = 0;
        if (studentAnswers == null) {
            return;
        }
        for (int i = 0; i < questions.size(); i++) {
            String answer = null;
            if (i < studentAnswers.size()) {
                answer = studentAnswers.get(i);
            }
            // checkAnswer already handles null, so a missing answer = wrong
            if (questions.get(i).checkAnswer(answer)) {
                score += questions.get(i).getPoints();
            }
        }
    }

    public int getScore() {
        return score;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public int getNumQuestions() {
        return questions.size();
    }
}