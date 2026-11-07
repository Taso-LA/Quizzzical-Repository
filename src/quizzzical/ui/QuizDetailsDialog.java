package quizzzical.ui;

import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import quizzzical.model.Quiz;

/** Creates or edits assessment details, availability dates and due dates. */
public class QuizDetailsDialog
{
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static Quiz showDialog(JFrame owner, Quiz existing)
    {
        boolean creating = existing == null;
        JTextField title = new JTextField(creating ? "New assessment" : existing.getTitle(), 24);
        JTextField subject = new JTextField(creating ? "Computer Science" : existing.getSubject(), 20);
        JTextField category = new JTextField(creating ? "Java" : existing.getCategory(), 20);
        JComboBox<String> type = new JComboBox<>(new String[] { "Quiz", "Test", "Assessment", "Practice" });
        type.setSelectedItem(creating ? "Quiz" : existing.getAssessmentType());
        JSpinner minutes = new JSpinner(new SpinnerNumberModel(creating ? 15 : existing.getDurationMinutes(), 1, 240, 1));
        JSpinner pass = new JSpinner(new SpinnerNumberModel(creating ? 50 : existing.getPassMark(), 1, 100, 1));
        JSpinner distinction = new JSpinner(new SpinnerNumberModel(creating ? 75 : existing.getDistinctionMark(), 1, 100, 1));
        JCheckBox shuffle = new JCheckBox("Shuffle question order", !creating && existing.isShuffleQuestions());
        JCheckBox published = new JCheckBox("Visible on the student dashboard", !creating && existing.isPublished());
        JTextField opens = new JTextField(creating ? LocalDateTime.now().plusMinutes(5).format(FORMAT)
                : format(existing.getAvailableAt()), 18);
        JTextField due = new JTextField(creating ? LocalDateTime.now().plusDays(7).format(FORMAT)
                : format(existing.getDueAt()), 18);

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.add(new JLabel("Assessment title")); panel.add(title);
        panel.add(new JLabel("Subject")); panel.add(subject);
        panel.add(new JLabel("Topic / category")); panel.add(category);
        panel.add(new JLabel("Assessment type")); panel.add(type);
        panel.add(new JLabel("Time limit (minutes)")); panel.add(minutes);
        panel.add(new JLabel("Pass mark (%)")); panel.add(pass);
        panel.add(new JLabel("Distinction mark (%)")); panel.add(distinction);
        panel.add(new JLabel("Available from (yyyy-MM-dd HH:mm)")); panel.add(opens);
        panel.add(new JLabel("Due by (yyyy-MM-dd HH:mm)")); panel.add(due);
        panel.add(shuffle); panel.add(published);
        panel.add(new JLabel("Leave a date blank for no time limit.")); panel.add(new JLabel("Uses this computer's local time."));

        int answer = JOptionPane.showConfirmDialog(owner, panel,
                creating ? "Create and schedule assessment" : "Edit assessment", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if(answer != JOptionPane.OK_OPTION) return null;

        try
        {
            String titleValue = title.getText().trim();
            if(titleValue.isEmpty() || subject.getText().trim().isEmpty() || category.getText().trim().isEmpty())
                throw new IllegalArgumentException("Title, subject and topic/category are required.");
            int passValue = (Integer) pass.getValue();
            int distinctionValue = (Integer) distinction.getValue();
            if(distinctionValue <= passValue) throw new IllegalArgumentException("The distinction mark must be higher than the pass mark.");
            LocalDateTime opensAt = parseDate(opens.getText());
            LocalDateTime dueAt = parseDate(due.getText());
            if(opensAt != null && dueAt != null && !dueAt.isAfter(opensAt))
                throw new IllegalArgumentException("The due date must be after the opening date.");

            Quiz quiz = creating ? new Quiz(titleValue, (Integer) minutes.getValue(), passValue,
                    distinctionValue, shuffle.isSelected(), published.isSelected(), java.util.Collections.emptyList()) : existing;
            quiz.updateSettings(titleValue, (Integer) minutes.getValue(), passValue,
                    distinctionValue, shuffle.isSelected(), published.isSelected());
            quiz.updateDetails(subject.getText().trim(), category.getText().trim(),
                    String.valueOf(type.getSelectedItem()), opensAt, dueAt);
            return quiz;
        }
        catch(DateTimeParseException exception)
        {
            JOptionPane.showMessageDialog(owner, "Use the date format yyyy-MM-dd HH:mm, for example 2026-11-12 09:30.",
                    "Invalid date", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        catch(IllegalArgumentException exception)
        {
            JOptionPane.showMessageDialog(owner, exception.getMessage(), "Check assessment details", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    private static LocalDateTime parseDate(String value)
    {
        if(value == null || value.trim().isEmpty()) return null;
        return LocalDateTime.parse(value.trim(), FORMAT);
    }

    private static String format(LocalDateTime value)
    {
        return value == null ? "" : value.format(FORMAT);
    }
}
