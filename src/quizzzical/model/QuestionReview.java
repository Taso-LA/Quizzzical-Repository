package quizzzical.model;

import java.io.Serializable;

public class QuestionReview implements Serializable
{
    private static final long serialVersionUID = 1L;

    private final String question;
    private final String studentAnswer;
    private final String correctAnswer;
    private final int pointsAwarded;
    private final int pointsPossible;

    public QuestionReview(String question, String studentAnswer, String correctAnswer,
                          int pointsAwarded, int pointsPossible)
    {
        this.question = question;
        this.studentAnswer = studentAnswer;
        this.correctAnswer = correctAnswer;
        this.pointsAwarded = pointsAwarded;
        this.pointsPossible = pointsPossible;
    }

    public String getQuestion() { return question; }
    public String getStudentAnswer() { return studentAnswer; }
    public String getCorrectAnswer() { return correctAnswer; }
    public int getPointsAwarded() { return pointsAwarded; }
    public int getPointsPossible() { return pointsPossible; }
}
