package quizzzical.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.Duration;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import quizzzical.data.QuizStore;
import quizzzical.model.Question;
import quizzzical.model.QuestionReview;
import quizzzical.model.QuestionType;
import quizzzical.model.Quiz;
import quizzzical.model.QuizResult;
import quizzzical.model.Student;
import quizzzical.service.ReportService;
import quizzzical.timer.QuizTimer;
import quizzzical.timer.TimeoutHandler;
import quizzzical.timer.TimerPanel;

public class StudentPage extends JFrame
{
    private final QuizStore store;
    private final Student student;
    private final Quiz quiz;
    private final List<Question> questions;
    private final Map<String, String> answers = new LinkedHashMap<>();
    private final JPanel questionHost = new JPanel(new BorderLayout());
    private final JLabel progressLabel = new JLabel(" ");
    private final JButton backButton = new JButton("Back");
    private final JButton nextButton = new JButton("Next");
    private final JButton submitButton = new JButton("Submit quiz");
    private final TimerPanel timerPanel;
    private final QuizTimer timer;
    private int currentIndex;
    private AnswerEditor currentEditor;
    private boolean submitted;

    public StudentPage(QuizStore store, Student student)
    {
        this(store, student, store.getQuiz());
    }

    public StudentPage(QuizStore store, Student student, Quiz quiz)
    {
        this.store = store;
        this.student = student;
        this.quiz = quiz;
        this.questions = quiz.getQuestions();
        if(quiz.isShuffleQuestions()) Collections.shuffle(questions);

        int durationSeconds = quiz.getDurationMinutes() * 60;
        if(quiz.getDueAt() != null)
        {
            long secondsUntilDue = Duration.between(LocalDateTime.now(), quiz.getDueAt()).getSeconds();
            durationSeconds = (int) Math.min(durationSeconds, Math.max(1, secondsUntilDue));
        }
        timerPanel = new TimerPanel(durationSeconds);
        timer = new QuizTimer(durationSeconds, timerPanel, new TimeoutHandler(() -> submitQuiz(true)));

        setTitle("Quizzzical | " + quiz.getTitle());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(800, 620));
        setSize(980, 720);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 8));
        add(new BrandHeader("WELCOME, " + student.getFullName().toUpperCase() + "  •  " + student.getStudentNumber()), BorderLayout.NORTH);

        JPanel top = new JPanel(new BorderLayout(10, 5));
        top.setBorder(BorderFactory.createEmptyBorder(4, 14, 4, 14));
        JLabel quizTitle = new JLabel(quiz.getTitle());
        quizTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        quizTitle.setForeground(new Color(53, 42, 99));
        top.add(quizTitle, BorderLayout.WEST);
        top.add(timerPanel, BorderLayout.EAST);

        questionHost.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        JPanel center = new JPanel(new BorderLayout(8, 8));
        center.add(top, BorderLayout.NORTH);
        center.add(questionHost, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout(10, 6));
        footer.setBorder(BorderFactory.createEmptyBorder(8, 18, 14, 18));
        progressLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        footer.add(progressLabel, BorderLayout.NORTH);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 2));
        buttons.add(backButton);
        buttons.add(nextButton);
        buttons.add(submitButton);
        footer.add(buttons, BorderLayout.SOUTH);
        add(footer, BorderLayout.SOUTH);

        backButton.addActionListener(event -> navigate(-1));
        nextButton.addActionListener(event -> navigate(1));
        submitButton.addActionListener(event -> submitQuiz(false));
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent event)
            {
                if(!submitted && JOptionPane.showConfirmDialog(StudentPage.this,
                        "Leaving the quiz will submit your current answers immediately. Continue?",
                        "Submit and exit", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                {
                    submitQuiz(true);
                }
            }
        });

        showQuestion();
        timer.start();
    }

    private void navigate(int direction)
    {
        saveCurrentAnswer();
        currentIndex += direction;
        showQuestion();
    }

    private void showQuestion()
    {
        Question question = questions.get(currentIndex);
        currentEditor = new AnswerEditor(question, answers.get(question.getId()));
        questionHost.removeAll();
        JPanel card = new JPanel(new BorderLayout(8, 12));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 220, 241)),
                BorderFactory.createEmptyBorder(20, 22, 20, 22)));
        JLabel type = new JLabel(question.getType().toString().toUpperCase() + "   •   " + question.getPoints() + " POINT(S)");
        type.setFont(new Font("SansSerif", Font.BOLD, 11));
        type.setForeground(new Color(111, 87, 190));
        JPanel questionHeader = new JPanel(new BorderLayout());
        questionHeader.setOpaque(false);
        questionHeader.add(type, BorderLayout.WEST);
        if(!question.getHint().trim().isEmpty())
        {
            JButton hintButton = new JButton("Show hint");
            hintButton.addActionListener(event -> JOptionPane.showMessageDialog(this,
                    question.getHint(), "Question hint", JOptionPane.INFORMATION_MESSAGE));
            questionHeader.add(hintButton, BorderLayout.EAST);
        }
        JTextArea prompt = new JTextArea(question.getPrompt());
        prompt.setWrapStyleWord(true);
        prompt.setLineWrap(true);
        prompt.setEditable(false);
        prompt.setOpaque(false);
        prompt.setFont(new Font("SansSerif", Font.BOLD, 19));
        prompt.setForeground(new Color(42, 38, 60));
        prompt.setRows(3);
        card.add(questionHeader, BorderLayout.NORTH);
        card.add(prompt, BorderLayout.CENTER);
        card.add(currentEditor, BorderLayout.SOUTH);
        questionHost.add(card, BorderLayout.CENTER);
        progressLabel.setText("Question " + (currentIndex + 1) + " of " + questions.size()
                + "     |     Answered: " + countAnswered() + " / " + questions.size());
        backButton.setEnabled(currentIndex > 0);
        nextButton.setEnabled(currentIndex < questions.size() - 1);
        questionHost.revalidate();
        questionHost.repaint();
    }

    private int countAnswered()
    {
        int count = 0;
        for(Question question : questions)
        {
            String value = answers.get(question.getId());
            if(currentEditor != null && currentEditor.question.getId().equals(question.getId()))
                value = currentEditor.readAnswer();
            if(value != null && !value.trim().isEmpty()) count++;
        }
        return count;
    }

    private void saveCurrentAnswer()
    {
        if(currentEditor != null) answers.put(currentEditor.question.getId(), currentEditor.readAnswer());
    }

    private boolean isAnswerComplete(Question question, String answer)
    {
        if(answer == null || answer.trim().isEmpty()) return false;
        if(question.getType() == QuestionType.MATCHING)
            return currentMatchingCount(answer) == question.getMatchingPairs().size();
        return true;
    }

    private int currentMatchingCount(String answer)
    {
        int count = 0;
        for(String part : answer.split(";"))
        {
            String[] pair = part.split("=", 2);
            if(pair.length == 2 && !pair[1].trim().isEmpty()) count++;
        }
        return count;
    }

    private void submitQuiz(boolean automatic)
    {
        if(submitted) return;
        saveCurrentAnswer();

        if(!automatic)
        {
            int firstMissing = -1;
            for(int i = 0; i < questions.size(); i++)
            {
                Question question = questions.get(i);
                String answer = answers.get(question.getId());
                if(!isAnswerComplete(question, answer)) { firstMissing = i; break; }
            }
            if(firstMissing >= 0)
            {
                currentIndex = firstMissing;
                showQuestion();
                JOptionPane.showMessageDialog(this, "Please answer every question before submitting. You have been taken to the first unanswered question.",
                        "Unanswered question", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if(JOptionPane.showConfirmDialog(this, "Submit your quiz now? You will not be able to change your answers afterwards.",
                    "Confirm submission", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        }

        submitted = true;
        int remaining = timer.getRemainingSeconds();
        timer.stop();
        int timeTaken = Math.max(0, timer.getTotalSeconds() - remaining);
        int score = 0;
        int total = 0;
        List<QuestionReview> reviews = new ArrayList<>();
        for(Question question : questions)
        {
            String answer = answers.getOrDefault(question.getId(), "");
            boolean correct = question.isCorrect(answer);
            int awarded = correct ? question.getPoints() : 0;
            score += awarded;
            total += question.getPoints();
            reviews.add(new QuestionReview(question.getPrompt(), answer,
                    question.getCorrectAnswerDisplay(), awarded, question.getPoints()));
        }
        int percentage = total == 0 ? 0 : (int) Math.round(score * 100.0 / total);
        String status;
        if(percentage >= quiz.getDistinctionMark()) status = "Passed (Distinction)";
        else if(percentage >= quiz.getPassMark()) status = "Passed";
        else status = "Failed";

        QuizResult result = new QuizResult(quiz.getId(), student.getUsername(), student.getStudentNumber(),
                student.getFullName(), quiz.getTitle(), score, total, percentage, timeTaken, status, reviews);
        try
        {
            store.addResult(result);
        }
        catch(IllegalStateException exception)
        {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Submission not saved", JOptionPane.ERROR_MESSAGE);
            dispose();
            new StudentDashboard(store, student).setVisible(true);
            return;
        }
        dispose();
        new ResultsPage(store, student, result, automatic).setVisible(true);
    }

    private class AnswerEditor extends JPanel
    {
        private final Question question;
        private final List<JRadioButton> radioButtons = new ArrayList<>();
        private final List<JCheckBox> checkBoxes = new ArrayList<>();
        private final Map<String, JComboBox<String>> matchingBoxes = new LinkedHashMap<>();
        private JTextField textField;

        AnswerEditor(Question question, String savedAnswer)
        {
            this.question = question;
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(6, 4, 2, 4));
            JPanel answersPanel = new JPanel(new GridLayout(0, 1, 4, 7));
            answersPanel.setBackground(Color.WHITE);

            if(question.getType() == QuestionType.SINGLE_CHOICE || question.getType() == QuestionType.TRUE_FALSE)
            {
                ButtonGroup group = new ButtonGroup();
                List<String> options = question.getOptions();
                if(question.getType() == QuestionType.SINGLE_CHOICE) Collections.shuffle(options);
                for(String option : options)
                {
                    JRadioButton button = new JRadioButton(option);
                    button.setBackground(Color.WHITE);
                    button.setFont(new Font("SansSerif", Font.PLAIN, 15));
                    if(savedAnswer != null && savedAnswer.equals(option)) button.setSelected(true);
                    radioButtons.add(button);
                    group.add(button);
                    answersPanel.add(button);
                }
            }
            else if(question.getType() == QuestionType.FILL_IN)
            {
                textField = new JTextField(savedAnswer == null ? "" : savedAnswer, 30);
                textField.setFont(new Font("SansSerif", Font.PLAIN, 16));
                answersPanel.add(new JLabel("Type your answer:"));
                answersPanel.add(textField);
            }
            else if(question.getType() == QuestionType.MULTI_SELECT)
            {
                List<String> options = question.getOptions();
                Collections.shuffle(options);
                List<String> selected = new ArrayList<>();
                if(savedAnswer != null) selected = java.util.Arrays.asList(savedAnswer.split(";"));
                for(String option : options)
                {
                    JCheckBox check = new JCheckBox(option);
                    check.setBackground(Color.WHITE);
                    check.setFont(new Font("SansSerif", Font.PLAIN, 15));
                    check.setSelected(selected.contains(option));
                    checkBoxes.add(check);
                    answersPanel.add(check);
                }
                JLabel note = new JLabel("You may tick more than one answer.");
                note.setForeground(new Color(105, 100, 120));
                answersPanel.add(note);
            }
            else if(question.getType() == QuestionType.MATCHING)
            {
                Map<String, String> saved = parsePairs(savedAnswer);
                List<String> rightOptions = new ArrayList<>(question.getMatchingPairs().values());
                Collections.shuffle(rightOptions);
                for(String left : question.getMatchingPairs().keySet())
                {
                    JPanel row = new JPanel(new BorderLayout(12, 4));
                    row.setBackground(Color.WHITE);
                    JLabel leftLabel = new JLabel(left);
                    leftLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
                    JComboBox<String> choice = new JComboBox<>();
                    choice.addItem("-- select a match --");
                    for(String right : rightOptions) choice.addItem(right);
                    if(saved.containsKey(left)) choice.setSelectedItem(saved.get(left));
                    matchingBoxes.put(left, choice);
                    row.add(leftLabel, BorderLayout.WEST);
                    row.add(choice, BorderLayout.CENTER);
                    answersPanel.add(row);
                }
            }
            add(answersPanel, BorderLayout.CENTER);
        }

        String readAnswer()
        {
            if(textField != null) return textField.getText().trim();
            if(!radioButtons.isEmpty())
            {
                for(JRadioButton button : radioButtons) if(button.isSelected()) return button.getText();
                return "";
            }
            if(!checkBoxes.isEmpty())
            {
                StringBuilder value = new StringBuilder();
                for(JCheckBox check : checkBoxes)
                {
                    if(check.isSelected())
                    {
                        if(value.length() > 0) value.append(';');
                        value.append(check.getText());
                    }
                }
                return value.toString();
            }
            if(!matchingBoxes.isEmpty())
            {
                StringBuilder value = new StringBuilder();
                for(Map.Entry<String, JComboBox<String>> entry : matchingBoxes.entrySet())
                {
                    Object selected = entry.getValue().getSelectedItem();
                    if(selected == null || selected.toString().startsWith("--")) continue;
                    if(value.length() > 0) value.append(';');
                    value.append(entry.getKey()).append('=').append(selected);
                }
                return value.toString();
            }
            return "";
        }

        private Map<String, String> parsePairs(String value)
        {
            Map<String, String> pairs = new LinkedHashMap<>();
            if(value == null) return pairs;
            for(String item : value.split(";"))
            {
                String[] parts = item.split("=", 2);
                if(parts.length == 2) pairs.put(parts[0], parts[1]);
            }
            return pairs;
        }
    }
}
