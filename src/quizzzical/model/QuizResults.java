package quizzzical.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class QuizResult implements Serializable
{
    private static final long serialVersionUID = 1L;

    private final String username;
    private final String studentNumber;
    private final String studentName;
    private final String quizTitle;
    private final int score;
    private final int totalPoints;
    private final int percentage;
    private final int timeTakenSeconds;
    private final String status;
    private final LocalDateTime submittedAt;
    private final List<QuestionReview> reviews;
    private String quizId;

    public QuizResult(String username, String studentNumber, String studentName,
                      String quizTitle, int score, int totalPoints, int percentage,
                      int timeTakenSeconds, String status, List<QuestionReview> reviews)
    {
        this(null, username, studentNumber, studentName, quizTitle, score, totalPoints,
                percentage, timeTakenSeconds, status, reviews);
    }

    public QuizResult(String quizId, String username, String studentNumber, String studentName,
                      String quizTitle, int score, int totalPoints, int percentage,
                      int timeTakenSeconds, String status, List<QuestionReview> reviews)
    {
        this.quizId = quizId;
        this.username = username;
        this.studentNumber = studentNumber;
        this.studentName = studentName;
        this.quizTitle = quizTitle;
        this.score = score;
        this.totalPoints = totalPoints;
        this.percentage = percentage;
        this.timeTakenSeconds = timeTakenSeconds;
        this.status = status;
        this.submittedAt = LocalDateTime.now();
        this.reviews = new ArrayList<>(reviews);
    }

    public String getQuizId() { return quizId; }
    public void assignQuizId(String quizId) { if(this.quizId == null) this.quizId = quizId; }
    public String getUsername() { return username; }
    public String getStudentNumber() { return studentNumber; }
    public String getStudentName() { return studentName; }
    public String getQuizTitle() { return quizTitle; }
    public int getScore() { return score; }
    public int getTotalPoints() { return totalPoints; }
    public int getPercentage() { return percentage; }
    public int getTimeTakenSeconds() { return timeTakenSeconds; }
    public String getStatus() { return status; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public List<QuestionReview> getReviews() { return new ArrayList<>(reviews); }
}
