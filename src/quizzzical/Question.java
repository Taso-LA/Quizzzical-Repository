/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package quizzzical;

public abstract class Question {
    protected String questionText;
    protected String correctAnswer;
    protected int points;

    public Question(String questionText, String correctAnswer, int points) {
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;
        this.points = points;
    }

    public String getQuestionText() { return questionText; }
    public String getCorrectAnswer() { return correctAnswer; }
    public int getPoints() { return points; }

    public abstract boolean checkAnswer(String userAnswer);
    public abstract String getQuestionType();
}
