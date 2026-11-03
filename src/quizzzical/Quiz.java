/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
        questions.add(q);
    }

    public void shuffleQuestions() {
        Collections.shuffle(questions);
    }

    public void gradeQuiz(ArrayList<String> studentAnswers) {
        score = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).checkAnswer(studentAnswers.get(i))) {
                score += questions.get(i).getPoints();
            }
        }
    }

    public int getScore() { return score; }
    public int getTotalQuestions() { return questions.size(); }
    public Question getQuestion(int index) { return questions.get(index); }
}