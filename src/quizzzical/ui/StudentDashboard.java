package quizzzical.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import quizzzical.data.QuizStore;
import quizzzical.model.Announcement;
import quizzzical.model.Quiz;
import quizzzical.model.QuizResult;
import quizzzical.model.Student;
import quizzzical.service.ReportService;

/** Student landing area: assessment calendar, progress, activities, messages and profile. */
public class StudentDashboard extends JFrame
{
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
    private final QuizStore store;
    private final Student student;
    private final JPanel content = new JPanel(new BorderLayout());
    private String currentPage = "home";
    private final Timer dashboardRefreshTimer;
    private final JLabel summaryLabel = new JLabel(" ");
    private final DefaultTableModel performanceModel = model("Assessment", "Subject", "Topic", "Grade", "Result", "Time taken", "Submitted");
    private final DefaultTableModel activityModel = model("Activity", "Details", "Date / status");
    private final JTable performanceTable = new JTable(performanceModel);
    private final JTable activityTable = new JTable(activityModel);
    private final JPanel quizGrid = new JPanel(new GridLayout(0, 2, 12, 12));
    private final JPanel homePanel = new JPanel(new BorderLayout(10, 10));

    public StudentDashboard(QuizStore store, Student student)
    {
        this.store = store;
        this.student = student;
        setTitle("Quizzzical | Student home");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(980, 680));
        setSize(1180, 790);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 8));
        add(new BrandHeader("STUDENT SPACE  •  WELCOME, " + student.getFullName().toUpperCase()
                + "  •  " + student.getStudentNumber()), BorderLayout.NORTH);

        JPanel shell = new JPanel(new BorderLayout(8, 8));
        shell.setBorder(BorderFactory.createEmptyBorder(8, 12, 12, 12));
        JPanel menu = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 4));
        menu.setBackground(new Color(246, 244, 252));
        addMenuButton(menu, "Home", "home");
        addMenuButton(menu, "Quiz grid", "quizzes");
        addMenuButton(menu, "Performance", "performance");
        addMenuButton(menu, "Activities", "activities");
        addMenuButton(menu, "Messages", "messages");
        addMenuButton(menu, "Profile", "profile");
        JButton logout = new JButton("Sign out");
        menu.add(logout);
        logout.addActionListener(event -> logout());
        shell.add(menu, BorderLayout.NORTH);
        shell.add(content, BorderLayout.CENTER);
        add(shell, BorderLayout.CENTER);

        performanceTable.setRowHeight(28);
        activityTable.setRowHeight(28);
        showPage("home");
        refreshAll();
        dashboardRefreshTimer = new Timer(15000, event ->
        {
            if(currentPage.equals("home") || currentPage.equals("quizzes") || currentPage.equals("activities")) refreshAll();
        });
        dashboardRefreshTimer.start();
        addWindowListener(new WindowAdapter()
        {
            @Override public void windowClosed(WindowEvent event) { dashboardRefreshTimer.stop(); }
        });
    }

    private void addMenuButton(JPanel menu, String label, String page)
    {
        JButton button = new JButton(label);
        menu.add(button);
        button.addActionListener(event -> showPage(page));
    }

    private void showPage(String page)
    {
        currentPage = page;
        content.removeAll();
        if(page.equals("home")) content.add(createHomePanel(), BorderLayout.CENTER);
        else if(page.equals("quizzes")) content.add(createQuizGridPanel(), BorderLayout.CENTER);
        else if(page.equals("performance")) content.add(createPerformancePanel(), BorderLayout.CENTER);
        else if(page.equals("activities")) content.add(createActivitiesPanel(), BorderLayout.CENTER);
        else if(page.equals("messages")) content.add(createMessagesPanel(), BorderLayout.CENTER);
        else if(page.equals("profile")) content.add(createProfilePanel(), BorderLayout.CENTER);
        content.revalidate();
        content.repaint();
    }

    private JPanel createHomePanel()
    {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 10, 10, 10));
        JLabel heading = new JLabel("Your learning overview");
        heading.setFont(new Font("SansSerif", Font.BOLD, 23));
        JPanel top = new JPanel(new BorderLayout(6, 8));
        top.add(heading, BorderLayout.NORTH);
        summaryLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        top.add(summaryLabel, BorderLayout.CENTER);
        panel.add(top, BorderLayout.NORTH);

        JPanel overview = new JPanel(new BorderLayout(8, 8));
        JLabel nextHeading = new JLabel("Your assessment calendar");
        nextHeading.setFont(new Font("SansSerif", Font.BOLD, 17));
        overview.add(nextHeading, BorderLayout.NORTH);
        overview.add(new JScrollPane(createScheduleTable()), BorderLayout.CENTER);
        JButton viewQuizzes = new JButton("Open quiz grid");
        viewQuizzes.addActionListener(event -> showPage("quizzes"));
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(viewQuizzes);
        overview.add(bottom, BorderLayout.SOUTH);
        panel.add(overview, BorderLayout.CENTER);
        return panel;
    }

    private JTable createScheduleTable()
    {
        DefaultTableModel model = model("Assessment", "Subject / topic", "Opens", "Due", "Status");
        LocalDateTime now = LocalDateTime.now();
        for(Quiz quiz : store.getQuizzes())
        {
            QuizResult result = store.getResultForStudentAndQuiz(student.getUsername(), quiz.getId());
            String status = getQuizStatus(quiz, result, now);
            model.addRow(new Object[] { quiz.getTitle(), quiz.getSubject() + " / " + quiz.getCategory(),
                formatDate(quiz.getAvailableAt()), formatDate(quiz.getDueAt()), status });
        }
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setAutoCreateRowSorter(true);
        return table;
    }

    private JPanel createQuizGridPanel()
    {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel titleRow = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Assessments");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleRow.add(title, BorderLayout.WEST);
        JButton refresh = new JButton("Refresh schedule");
        refresh.addActionListener(event -> refreshAll());
        titleRow.add(refresh, BorderLayout.EAST);
        panel.add(titleRow, BorderLayout.NORTH);
        quizGrid.removeAll();
        LocalDateTime now = LocalDateTime.now();
        for(Quiz quiz : store.getQuizzes()) quizGrid.add(createQuizCard(quiz, now));
        if(store.getQuizzes().isEmpty()) quizGrid.add(new JLabel("No assessments have been created yet."));
        panel.add(new JScrollPane(quizGrid), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createQuizCard(Quiz quiz, LocalDateTime now)
    {
        QuizResult result = store.getResultForStudentAndQuiz(student.getUsername(), quiz.getId());
        String status = getQuizStatus(quiz, result, now);
        JPanel card = new JPanel(new BorderLayout(6, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220, 215, 238)),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));
        JLabel name = new JLabel(quiz.getTitle());
        name.setFont(new Font("SansSerif", Font.BOLD, 17));
        name.setForeground(new Color(54, 42, 105));
        JLabel category = new JLabel(quiz.getAssessmentType() + "  •  " + quiz.getSubject() + "  •  " + quiz.getCategory());
        category.setForeground(new Color(100, 94, 118));
        JTextArea details = new JTextArea("Time limit: " + quiz.getDurationMinutes() + " minutes\n"
                + "Opens: " + formatDate(quiz.getAvailableAt()) + "\nDue: " + formatDate(quiz.getDueAt())
                + "\nQuestions: " + quiz.getQuestions().size());
        details.setEditable(false);
        details.setOpaque(false);
        details.setFont(new Font("SansSerif", Font.PLAIN, 12));
        JPanel north = new JPanel(new BorderLayout(3, 3));
        north.setOpaque(false);
        north.add(name, BorderLayout.NORTH);
        north.add(category, BorderLayout.SOUTH);
        JLabel statusLabel = new JLabel(status);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        statusLabel.setForeground(status.contains("available") || status.equals("Available now") ? new Color(30, 125, 78) : new Color(105, 83, 165));
        JButton action = new JButton(result == null ? "View status" : "View result");
        if(result == null && quiz.isAvailableNow(now)) action.setText("Start assessment");
        if(result == null && (!quiz.isPublished() || quiz.getQuestions().isEmpty() || quiz.isMissed(now))) action.setEnabled(false);
        action.addActionListener(event ->
        {
            QuizResult saved = store.getResultForStudentAndQuiz(student.getUsername(), quiz.getId());
            if(saved != null)
            {
                new ResultsPage(store, student, saved, false).setVisible(true);
                dispose();
            }
            else if(quiz.isAvailableNow(LocalDateTime.now()) && !store.hasAttempted(student.getUsername(), quiz.getId()))
            {
                dispose();
                new StudentPage(store, student, quiz).setVisible(true);
            }
            else
            {
                JOptionPane.showMessageDialog(this, status + "\nCheck the opening and due dates on this card.",
                        "Assessment status", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(statusLabel, BorderLayout.WEST);
        bottom.add(action, BorderLayout.EAST);
        card.add(north, BorderLayout.NORTH);
        card.add(details, BorderLayout.CENTER);
        card.add(bottom, BorderLayout.SOUTH);
        return card;
    }

    private String getQuizStatus(Quiz quiz, QuizResult result, LocalDateTime now)
    {
        if(result != null) return result.getStatus() + " • " + result.getPercentage() + "%";
        if(!quiz.isPublished()) return "On hold";
        if(quiz.getQuestions().isEmpty()) return "Not ready";
        if(quiz.isMissed(now)) return "Missed";
        if(quiz.isUpcoming(now)) return "Upcoming";
        if(quiz.isAvailableNow(now))
        {
            if(quiz.getDueAt() != null && Duration.between(now, quiz.getDueAt()).toHours() <= 24)
                return "Due soon — available";
            return "Available now";
        }
        if(quiz.getDueAt() != null && now.isAfter(quiz.getDueAt())) return "Missed";
        return "Scheduled";
    }

    private JPanel createPerformancePanel()
    {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 10, 10, 10));
        JLabel heading = new JLabel("Performance over time");
        heading.setFont(new Font("SansSerif", Font.BOLD, 22));
        panel.add(heading, BorderLayout.NORTH);
        performanceModel.setRowCount(0);
        List<QuizResult> results = store.getResultsForStudent(student.getUsername());
        int total = 0;
        int passed = 0;
        for(QuizResult result : results)
        {
            total += result.getPercentage();
            if(result.getStatus().startsWith("Passed")) passed++;
            Quiz quiz = store.getQuizById(result.getQuizId());
            performanceModel.addRow(new Object[] { result.getQuizTitle(), quiz == null ? "—" : quiz.getSubject(),
                quiz == null ? "—" : quiz.getCategory(), result.getPercentage() + "%", result.getStatus(),
                ReportService.formatDuration(result.getTimeTakenSeconds()), formatDate(result.getSubmittedAt()) });
        }
        JPanel metrics = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 5));
        metrics.add(new JLabel("Assessments completed: " + results.size()));
        metrics.add(new JLabel("Average grade: " + (results.isEmpty() ? 0 : Math.round(total / (double) results.size())) + "%"));
        metrics.add(new JLabel("Passed: " + passed));
        metrics.add(new JLabel("Failed: " + (results.size() - passed)));
        JPanel center = new JPanel(new BorderLayout(5, 5));
        center.add(metrics, BorderLayout.NORTH);
        center.add(new JScrollPane(performanceTable), BorderLayout.CENTER);
        panel.add(center, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createActivitiesPanel()
    {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 10, 10, 10));
        JLabel heading = new JLabel("Recent activities");
        heading.setFont(new Font("SansSerif", Font.BOLD, 22));
        panel.add(heading, BorderLayout.NORTH);
        activityModel.setRowCount(0);
        for(QuizResult result : store.getResultsForStudent(student.getUsername()))
            activityModel.addRow(new Object[] { "Assessment submitted", result.getQuizTitle() + " — " + result.getPercentage() + "% — " + result.getStatus(), formatDate(result.getSubmittedAt()) });
        LocalDateTime now = LocalDateTime.now();
        for(Quiz quiz : store.getQuizzes())
        {
            if(store.hasAttempted(student.getUsername(), quiz.getId())) continue;
            String status = getQuizStatus(quiz, null, now);
            if(status.equals("Upcoming") || status.startsWith("Due soon") || status.equals("Missed") || status.equals("On hold"))
                activityModel.addRow(new Object[] { status, quiz.getTitle() + " — " + quiz.getSubject() + " / " + quiz.getCategory(), formatDate(quiz.getDueAt()) });
        }
        panel.add(new JScrollPane(activityTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMessagesPanel()
    {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 10, 10, 10));
        JLabel heading = new JLabel("Messages and announcements");
        heading.setFont(new Font("SansSerif", Font.BOLD, 22));
        panel.add(heading, BorderLayout.NORTH);
        JTextArea messages = new JTextArea();
        messages.setEditable(false);
        messages.setLineWrap(true);
        messages.setWrapStyleWord(true);
        StringBuilder text = new StringBuilder();
        for(Announcement announcement : store.getAnnouncements())
        {
            text.append(announcement.getTitle()).append("\n")
                .append(announcement.getCreatedAt().format(DATE_FORMAT)).append("\n")
                .append(announcement.getMessage()).append("\n\n----------------------------------------\n\n");
        }
        if(text.length() == 0) text.append("There are no messages yet.");
        messages.setText(text.toString());
        panel.add(new JScrollPane(messages), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createProfilePanel()
    {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel heading = new JLabel("My profile");
        heading.setFont(new Font("SansSerif", Font.BOLD, 22));
        panel.add(heading, BorderLayout.NORTH);
        JPanel details = new JPanel(new GridLayout(0, 2, 10, 12));
        details.add(new JLabel("Username")); details.add(new JLabel(student.getUsername()));
        details.add(new JLabel("Student number")); details.add(new JLabel(student.getStudentNumber()));
        details.add(new JLabel("Surname")); details.add(new JLabel(student.getSurname()));
        details.add(new JLabel("Initials")); details.add(new JLabel(student.getInitials()));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton edit = new JButton("Edit name and initials");
        JButton password = new JButton("Change password");
        actions.add(edit); actions.add(password);
        edit.addActionListener(event -> editProfile());
        password.addActionListener(event -> changePassword());
        details.add(actions);
        panel.add(details, BorderLayout.CENTER);
        return panel;
    }

    private void editProfile()
    {
        JTextField surname = new JTextField(student.getSurname(), 20);
        JTextField initials = new JTextField(student.getInitials(), 20);
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Surname")); form.add(surname);
        form.add(new JLabel("Initials")); form.add(initials);
        if(JOptionPane.showConfirmDialog(this, form, "Edit profile", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
        {
            try
            {
                store.updateStudentProfile(student.getUsername(), surname.getText(), initials.getText());
                refreshAll();
                showPage("profile");
            }
            catch(IllegalArgumentException exception)
            {
                JOptionPane.showMessageDialog(this, exception.getMessage(), "Profile not saved", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void changePassword()
    {
        JPasswordField first = new JPasswordField(18);
        JPasswordField second = new JPasswordField(18);
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("New password")); form.add(first);
        form.add(new JLabel("Confirm password")); form.add(second);
        if(JOptionPane.showConfirmDialog(this, form, "Change password", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
        {
            String password = new String(first.getPassword());
            if(!password.equals(new String(second.getPassword())))
            {
                JOptionPane.showMessageDialog(this, "The passwords do not match.", "Check password", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try
            {
                store.changeStudentPassword(student.getUsername(), password);
                JOptionPane.showMessageDialog(this, "Your password has been updated.");
            }
            catch(IllegalArgumentException exception)
            {
                JOptionPane.showMessageDialog(this, exception.getMessage(), "Password not saved", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void refreshAll()
    {
        List<QuizResult> results = store.getResultsForStudent(student.getUsername());
        int average = 0;
        for(QuizResult result : results) average += result.getPercentage();
        average = results.isEmpty() ? 0 : Math.round(average / (float) results.size());
        int available = 0;
        int upcoming = 0;
        for(Quiz quiz : store.getQuizzes())
        {
            if(store.hasAttempted(student.getUsername(), quiz.getId())) continue;
            String status = getQuizStatus(quiz, null, LocalDateTime.now());
            if(status.equals("Available now")) available++;
            if(status.equals("Upcoming")) upcoming++;
            if(status.startsWith("Due soon")) available++;
        }
        summaryLabel.setText("Completed: " + results.size() + "     •     Average: " + average + "%     •     Available now: " + available + "     •     Upcoming: " + upcoming);
        showPage(currentPage);
        content.revalidate();
        content.repaint();
    }

    private String formatDate(LocalDateTime date)
    {
        return date == null ? "Not set" : date.format(DATE_FORMAT);
    }

    private static DefaultTableModel model(String... columns)
    {
        return new DefaultTableModel(columns, 0)
        {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
    }

    private void logout()
    {
        if(JOptionPane.showConfirmDialog(this, "Sign out of Quizzzical?", "Sign out", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
        {
            dispose();
            new LoginPage(store).setVisible(true);
        }
    }
}
