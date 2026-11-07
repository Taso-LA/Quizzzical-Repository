package quizzzical.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import quizzzical.model.QuestionReview;
import quizzzical.model.QuizResult;

public final class ReportService
{
    private ReportService() { }

    public static String formatDuration(int seconds)
    {
        return String.format("%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, seconds % 60);
    }

    public static String buildTextReport(QuizResult result)
    {
        StringBuilder report = new StringBuilder();
        report.append("QUIZZZICAL - STUDENT RESULT REPORT\n");
        report.append("=================================\n\n");
        report.append("Student: ").append(result.getStudentName()).append("\n");
        report.append("Student number: ").append(result.getStudentNumber()).append("\n");
        report.append("Username: ").append(result.getUsername()).append("\n");
        report.append("Quiz: ").append(result.getQuizTitle()).append("\n");
        report.append("Score: ").append(result.getScore()).append(" / ").append(result.getTotalPoints()).append("\n");
        report.append("Grade: ").append(result.getPercentage()).append("%\n");
        report.append("Result: ").append(result.getStatus()).append("\n");
        report.append("Time taken: ").append(formatDuration(result.getTimeTakenSeconds())).append("\n");
        report.append("Submitted: ").append(result.getSubmittedAt().toString().replace('T', ' ')).append("\n\n");
        report.append("QUESTION REVIEW\n---------------\n");
        int number = 1;
        for(QuestionReview review : result.getReviews())
        {
            report.append(number++).append(". ").append(review.getQuestion()).append("\n");
            report.append("   Your answer: ").append(emptyAsNotAnswered(review.getStudentAnswer())).append("\n");
            report.append("   Correct answer: ").append(review.getCorrectAnswer()).append("\n");
            report.append("   Points: ").append(review.getPointsAwarded()).append(" / ").append(review.getPointsPossible()).append("\n\n");
        }
        return report.toString();
    }

    public static void exportText(QuizResult result, Path path) throws IOException
    {
        Files.writeString(path, buildTextReport(result), StandardCharsets.UTF_8);
    }

    public static void exportCsv(List<QuizResult> results, Path path) throws IOException
    {
        StringBuilder csv = new StringBuilder();
        csv.append("Student,Surname and initials,Student number,Quiz,Score,Total points,Grade,Result,Time taken,Submitted\n");
        for(QuizResult result : results)
        {
            csv.append(csv(result.getUsername())).append(',')
                    .append(csv(result.getStudentName())).append(',')
                    .append(csv(result.getStudentNumber())).append(',')
                    .append(csv(result.getQuizTitle())).append(',')
                    .append(result.getScore()).append(',')
                    .append(result.getTotalPoints()).append(',')
                    .append(result.getPercentage()).append("%,")
                    .append(csv(result.getStatus())).append(',')
                    .append(csv(formatDuration(result.getTimeTakenSeconds()))).append(',')
                    .append(csv(result.getSubmittedAt().toString().replace('T', ' '))).append('\n');
        }
        Files.writeString(path, csv.toString(), StandardCharsets.UTF_8);
    }

    private static String csv(String value)
    {
        String safe = value == null ? "" : value;
        return "\"" + safe.replace("\"", "\"\"") + "\"";
    }

    private static String emptyAsNotAnswered(String value)
    {
        return value == null || value.trim().isEmpty() ? "Not answered" : value;
    }
}
