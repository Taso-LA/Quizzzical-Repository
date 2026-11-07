package quizzzical.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Announcement implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final String title;
    private final String message;
    private final LocalDateTime createdAt;

    public Announcement(String title, String message)
    {
        this.title = title;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }

    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
