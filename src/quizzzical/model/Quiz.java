package quizzzical.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Quiz implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String id;
    private String title;
    private int durationMinutes;
    private int passMark;
    private int distinctionMark;
    private boolean shuffleQuestions;
    private boolean published;
    private List<Question> questions;
    private String subject;
    private String category;
    private String assessmentType;
    private LocalDateTime availableAt;
    private LocalDateTime dueAt;

    public Quiz(String title, int durationMinutes, int passMark, int distinctionMark,
                boolean shuffleQuestions, boolean published, List<Question> questions)
    {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.durationMinutes = durationMinutes;
        this.passMark = passMark;
        this.distinctionMark = distinctionMark;
        this.shuffleQuestions = shuffleQuestions;
        this.published = published;
        this.questions = new ArrayList<>(questions);
        this.subject = "General";
        this.category = "General";
        this.assessmentType = "Quiz";
    }

    public String getId()
    {
        if(id == null) id = UUID.randomUUID().toString();
        return id;
    }
    public String getTitle() { return title; }
    public int getDurationMinutes() { return durationMinutes; }
    public int getPassMark() { return passMark; }
    public int getDistinctionMark() { return distinctionMark; }
    public boolean isShuffleQuestions() { return shuffleQuestions; }
    public boolean isPublished() { return published; }
    public List<Question> getQuestions() { return questions == null ? new ArrayList<>() : new ArrayList<>(questions); }
    public String getSubject() { return subject == null || subject.trim().isEmpty() ? "General" : subject; }
    public String getCategory() { return category == null || category.trim().isEmpty() ? "General" : category; }
    public String getAssessmentType() { return assessmentType == null ? "Quiz" : assessmentType; }
    public LocalDateTime getAvailableAt() { return availableAt; }
    public LocalDateTime getDueAt() { return dueAt; }

    public void updateSettings(String title, int durationMinutes, int passMark,
                               int distinctionMark, boolean shuffleQuestions,
                               boolean published)
    {
        this.title = title;
        this.durationMinutes = durationMinutes;
        this.passMark = passMark;
        this.distinctionMark = distinctionMark;
        this.shuffleQuestions = shuffleQuestions;
        this.published = published;
    }

    public void updateDetails(String subject, String category, String assessmentType,
                              LocalDateTime availableAt, LocalDateTime dueAt)
    {
        this.subject = subject;
        this.category = category;
        this.assessmentType = assessmentType;
        this.availableAt = availableAt;
        this.dueAt = dueAt;
    }

    public boolean isAvailableNow(LocalDateTime now)
    {
        return published && (availableAt == null || !now.isBefore(availableAt))
                && (dueAt == null || !now.isAfter(dueAt)) && !getQuestions().isEmpty();
    }

    public boolean isUpcoming(LocalDateTime now)
    {
        return published && availableAt != null && now.isBefore(availableAt);
    }

    public boolean isMissed(LocalDateTime now)
    {
        return dueAt != null && now.isAfter(dueAt);
    }

    public void addQuestion(Question question) { questions.add(question); }
    public void removeQuestion(Question question) { questions.remove(question); }
    public void clearQuestions() { questions.clear(); }

    public void shuffleQuestions()
    {
        Collections.shuffle(questions);
    }

    @Override
    public String toString()
    {
        return getTitle() + " — " + getSubject() + " / " + getCategory();
    }
}
