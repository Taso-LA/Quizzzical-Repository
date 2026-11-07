package quizzzical.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import quizzzical.data.QuizStore;
import quizzzical.model.Question;
import quizzzical.model.Quiz;
import quizzzical.model.QuizResult;
import quizzzical.model.Student;
import quizzzical.service.ReportService;

/** Administrator workspace for assessments, question banks, students and reporting. */
public class AdminPage extends JFrame
{
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
    private final QuizStore store;
    private final JPanel content = new JPanel(new CardLayout());
    private String currentPage = "home";
    private String selectedQuizId;

    private final DefaultTableModel dashboardModel = model("Student", "Student number", "Assessment", "Grade", "Result", "Status", "Time taken", "Submitted");
    private final DefaultTableModel quizModel = model("Assessment", "Subject", "Topic / category", "Type", "Opens", "Due", "Minutes", "Questions", "Visibility", "Quiz ID");
    private final DefaultTableModel questionModel = model("No.", "Question", "Question type", "Points", "Hint");
    private final DefaultTableModel accountModel = model("Surname", "Initials", "Student number", "Username", "Account status");
    private final DefaultTableModel reportModel = model("Student", "Student number", "Assessment", "Score", "Grade", "Result", "Time taken", "Submitted");
    private final JTable dashboardTable = new JTable(dashboardModel);
    private final JTable quizTable = new JTable(quizModel);
    private final JTable questionTable = new JTable(questionModel);
    private final JTable accountTable = new JTable(accountModel);
    private final JTable reportTable = new JTable(reportModel);
    private final JLabel studentCount = metricValue();
    private final JLabel assessmentCount = metricValue();
    private final JLabel wroteCount = metricValue();
    private final JLabel passedCount = metricValue();
    private final JLabel failedCount = metricValue();
    private final JLabel waitingCount = metricValue();

    public AdminPage(QuizStore store)
    {
        this.store = store;
        if(!store.getQuizzes().isEmpty()) selectedQuizId = store.getQuizzes().get(0).getId();
        setTitle("Quizzzical | Administrator workspace");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(1080, 700));
        setSize(1280, 820);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        add(new BrandHeader("ADMINISTRATOR SPACE  •  Build, schedule, monitor and report assessments"), BorderLayout.NORTH);

        JPanel shell = new JPanel(new BorderLayout(8, 8));
        shell.setBorder(BorderFactory.createEmptyBorder(8, 10, 10, 10));
        JPanel menu = new JPanel(new GridLayout(0, 1, 5, 5));
        menu.setPreferredSize(new Dimension(165, 10));
        menu.setBackground(new Color(246, 244, 252));
        addMenu(menu, "Home", "home");
        addMenu(menu, "Assessments", "quizzes");
        addMenu(menu, "Question bank", "questions");
        addMenu(menu, "Students", "students");
        addMenu(menu, "Results / reports", "reports");
        addMenu(menu, "Messages", "messages");
        addMenu(menu, "Admin profile", "profile");
        JButton logout = new JButton("Logout");
        menu.add(logout);
        logout.addActionListener(event -> logout());
        shell.add(menu, BorderLayout.WEST);
        shell.add(content, BorderLayout.CENTER);
        add(shell, BorderLayout.CENTER);
        addWindowListener(new WindowAdapter()
        {
            @Override public void windowClosing(WindowEvent event) { logout(); }
        });
        configureTables();
        quizTable.getColumnModel().getColumn(9).setMinWidth(0);
        quizTable.getColumnModel().getColumn(9).setMaxWidth(0);
        quizTable.getColumnModel().getColumn(9).setPreferredWidth(0);
        refreshAll();
        showPage("home");
    }

    private static DefaultTableModel model(String... columns)
    {
        return new DefaultTableModel(columns, 0)
        {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
    }

    private void addMenu(JPanel menu, String label, String page)
    {
        JButton button = new JButton(label);
        button.setFocusPainted(false);
        menu.add(button);
        button.addActionListener(event -> showPage(page));
    }

    private void showPage(String page)
    {
        currentPage = page;
        content.removeAll();
        if(page.equals("home")) content.add(createDashboardPanel());
        else if(page.equals("quizzes")) content.add(createQuizPanel());
        else if(page.equals("questions")) content.add(createQuestionPanel());
        else if(page.equals("students")) content.add(createStudentPanel());
        else if(page.equals("reports")) content.add(createReportsPanel());
        else if(page.equals("messages")) content.add(createMessagesPanel());
        else if(page.equals("profile")) content.add(createProfilePanel());
        content.revalidate();
        content.repaint();
    }

    private void configureTables()
    {
        for(JTable table : new JTable[] { dashboardTable, quizTable, questionTable, accountTable, reportTable })
        {
            table.setRowHeight(28);
            table.setAutoCreateRowSorter(true);
            table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        }
    }

    private JLabel metricValue()
    {
        JLabel label = new JLabel("0", JLabel.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 25));
        label.setForeground(new Color(70, 54, 130));
        return label;
    }

    private JPanel metricCard(String title, JLabel value)
    {
        JPanel card = new JPanel(new BorderLayout(3, 4));
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(226, 220, 241)),
                BorderFactory.createEmptyBorder(10, 8, 10, 8)));
        JLabel label = new JLabel(title, JLabel.CENTER);
        label.setForeground(new Color(100, 96, 115));
        card.add(label, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);
        return card;
    }

    private JPanel pagePanel(String title, String subtitle)
    {
        JPanel panel = new JPanel(new BorderLayout(9, 9));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JPanel heading = new JPanel(new BorderLayout(5, 4));
        JLabel label = new JLabel(title);
        label.setFont(new Font("SansSerif", Font.BOLD, 22));
        heading.add(label, BorderLayout.NORTH);
        JLabel sub = new JLabel(subtitle);
        sub.setForeground(new Color(104, 98, 120));
        heading.add(sub, BorderLayout.SOUTH);
        panel.add(heading, BorderLayout.NORTH);
        return panel;
    }

    private JPanel createDashboardPanel()
    {
        JPanel panel = pagePanel("Administrator overview", "A live summary of student activity and assessment performance.");
        JPanel metrics = new JPanel(new GridLayout(2, 3, 10, 10));
        metrics.add(metricCard("Student accounts", studentCount));
        metrics.add(metricCard("Assessments", assessmentCount));
        metrics.add(metricCard("Students who wrote", wroteCount));
        metrics.add(metricCard("Passed", passedCount));
        metrics.add(metricCard("Failed", failedCount));
        metrics.add(metricCard("Pending assessment slots", waitingCount));
        panel.add(metrics, BorderLayout.CENTER);
        JPanel bottom = new JPanel(new BorderLayout(5, 5));
        JLabel label = new JLabel("Latest student assessment activity");
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        bottom.add(label, BorderLayout.NORTH);
        bottom.add(new JScrollPane(dashboardTable), BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createQuizPanel()
    {
        JPanel panel = pagePanel("Assessments and calendar", "Create quizzes and tests, set their subject and topic, and control when students can open them.");
        panel.add(new JScrollPane(quizTable), BorderLayout.CENTER);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton create = new JButton("Create / schedule assessment");
        JButton edit = new JButton("Edit selected");
        JButton questions = new JButton("Manage questions");
        JButton visibility = new JButton("Publish / put on hold");
        JButton delete = new JButton("Delete selected");
        buttons.add(create); buttons.add(edit); buttons.add(questions); buttons.add(visibility); buttons.add(delete);
        panel.add(buttons, BorderLayout.SOUTH);
        create.addActionListener(event -> createAssessment());
        edit.addActionListener(event -> editAssessment());
        questions.addActionListener(event ->
        {
            if(selectQuizFromTable()) showPage("questions");
        });
        visibility.addActionListener(event -> toggleVisibility());
        delete.addActionListener(event -> deleteAssessment());
        return panel;
    }

    private JPanel createQuestionPanel()
    {
        JPanel panel = pagePanel("Question bank", "Add hints and build single choice, true/false, fill-in, tick-all and matching questions.");
        JPanel selector = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<Quiz> choice = new JComboBox<>();
        for(Quiz quiz : store.getQuizzes()) choice.addItem(quiz);
        Quiz current = getSelectedQuiz();
        if(current != null) choice.setSelectedItem(current);
        choice.addActionListener(event ->
        {
            Quiz selected = (Quiz) choice.getSelectedItem();
            if(selected != null)
            {
                selectedQuizId = selected.getId();
                refreshQuestionTable();
            }
        });
        selector.add(new JLabel("Assessment:")); selector.add(choice);
        JPanel center = new JPanel(new BorderLayout(6, 6));
        center.add(selector, BorderLayout.NORTH);
        center.add(new JScrollPane(questionTable), BorderLayout.CENTER);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton add = new JButton("Add question");
        JButton edit = new JButton("Edit selected");
        JButton delete = new JButton("Delete selected");
        JButton clear = new JButton("Clear question bank");
        buttons.add(add); buttons.add(edit); buttons.add(delete); buttons.add(clear);
        center.add(buttons, BorderLayout.SOUTH);
        panel.add(center, BorderLayout.CENTER);
        add.addActionListener(event -> addQuestion());
        edit.addActionListener(event -> editQuestion());
        delete.addActionListener(event -> deleteQuestion());
        clear.addActionListener(event -> clearQuestions());
        refreshQuestionTable();
        return panel;
    }

    private JPanel createStudentPanel()
    {
        JPanel panel = pagePanel("Student accounts", "Create accounts, reset passwords and monitor each student's assessment record.");
        panel.add(new JScrollPane(accountTable), BorderLayout.CENTER);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton add = new JButton("Add student");
        JButton reset = new JButton("Reset selected password");
        JButton delete = new JButton("Delete selected student");
        buttons.add(add); buttons.add(reset); buttons.add(delete);
        panel.add(buttons, BorderLayout.SOUTH);
        add.addActionListener(event -> { AccountDialog.addStudent(this, store); refreshAll(); showPage("students"); });
        reset.addActionListener(event ->
        {
            int row = accountTable.getSelectedRow();
            if(row < 0) { showSelectStudent(); return; }
            int modelRow = accountTable.convertRowIndexToModel(row);
            AccountDialog.resetPassword(this, store, String.valueOf(accountModel.getValueAt(modelRow, 3)));
        });
        delete.addActionListener(event ->
        {
            int row = accountTable.getSelectedRow();
            if(row < 0) { showSelectStudent(); return; }
            int modelRow = accountTable.convertRowIndexToModel(row);
            String username = String.valueOf(accountModel.getValueAt(modelRow, 3));
            if(JOptionPane.showConfirmDialog(this, "Delete this student account and all its saved results?", "Delete student", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
            {
                store.removeStudent(username);
                refreshAll();
                showPage("students");
            }
        });
        return panel;
    }

    private JPanel createReportsPanel()
    {
        JPanel panel = pagePanel("Results and reports", "Review every saved attempt, grade, pass status and time taken.");
        panel.add(new JScrollPane(reportTable), BorderLayout.CENTER);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton view = new JButton("View selected report");
        JButton export = new JButton("Export all reports (CSV)");
        JButton clear = new JButton("Clear all results / unlock attempts");
        buttons.add(view); buttons.add(export); buttons.add(clear);
        panel.add(buttons, BorderLayout.SOUTH);
        view.addActionListener(event -> viewReport());
        export.addActionListener(event -> exportReports());
        clear.addActionListener(event -> clearResults());
        return panel;
    }

    private JPanel createMessagesPanel()
    {
        JPanel panel = pagePanel("Messages and announcements", "Send a notice to the message area on every student dashboard.");
        JTextArea message = new JTextArea(8, 50);
        message.setLineWrap(true);
        message.setWrapStyleWord(true);
        JPanel editor = new JPanel(new BorderLayout(5, 5));
        JTextField title = new JTextField("Quizzzical announcement");
        editor.add(new JLabel("Announcement title"), BorderLayout.NORTH);
        editor.add(title, BorderLayout.CENTER);
        editor.add(new JScrollPane(message), BorderLayout.SOUTH);
        JButton send = new JButton("Publish announcement");
        send.addActionListener(event ->
        {
            try
            {
                store.addAnnouncement(title.getText(), message.getText());
                title.setText("Quizzzical announcement");
                message.setText("");
                JOptionPane.showMessageDialog(this, "Announcement published to student Messages.");
            }
            catch(IllegalArgumentException exception)
            {
                JOptionPane.showMessageDialog(this, exception.getMessage(), "Announcement not published", JOptionPane.WARNING_MESSAGE);
            }
        });
        panel.add(editor, BorderLayout.CENTER);
        panel.add(send, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createProfilePanel()
    {
        JPanel panel = pagePanel("Administrator account", "Manage the sign-in credentials used to access this control centre.");
        JPanel details = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        details.add(new JLabel("Current username: " + store.getAdminUsername()));
        JButton change = new JButton("Change administrator username and password");
        change.addActionListener(event -> AccountDialog.changeAdmin(this, store));
        details.add(change);
        panel.add(details, BorderLayout.CENTER);
        return panel;
    }

    private Quiz getSelectedQuiz()
    {
        if(selectedQuizId != null)
        {
            Quiz selected = store.getQuizById(selectedQuizId);
            if(selected != null) return selected;
        }
        List<Quiz> quizzes = store.getQuizzes();
        if(quizzes.isEmpty()) return null;
        selectedQuizId = quizzes.get(0).getId();
        return quizzes.get(0);
    }

    private boolean selectQuizFromTable()
    {
        int row = quizTable.getSelectedRow();
        if(row < 0)
        {
            JOptionPane.showMessageDialog(this, "Select an assessment first.");
            return false;
        }
        int modelRow = quizTable.convertRowIndexToModel(row);
        selectedQuizId = String.valueOf(quizModel.getValueAt(modelRow, 9));
        return store.getQuizById(selectedQuizId) != null;
    }

    private Quiz getSelectedQuizFromTable()
    {
        return selectQuizFromTable() ? getSelectedQuiz() : null;
    }

    private void createAssessment()
    {
        Quiz quiz = QuizDetailsDialog.showDialog(this, null);
        if(quiz != null)
        {
            store.addQuiz(quiz);
            selectedQuizId = quiz.getId();
            refreshAll();
            showPage("questions");
        }
    }

    private void editAssessment()
    {
        Quiz quiz = getSelectedQuizFromTable();
        if(quiz == null) return;
        Quiz edited = QuizDetailsDialog.showDialog(this, quiz);
        if(edited != null)
        {
            store.saveQuiz(edited);
            selectedQuizId = edited.getId();
            refreshAll();
            showPage("quizzes");
        }
    }

    private void toggleVisibility()
    {
        Quiz quiz = getSelectedQuizFromTable();
        if(quiz == null) return;
        quiz.updateSettings(quiz.getTitle(), quiz.getDurationMinutes(), quiz.getPassMark(),
                quiz.getDistinctionMark(), quiz.isShuffleQuestions(), !quiz.isPublished());
        store.saveQuiz(quiz);
        refreshAll();
        showPage("quizzes");
    }

    private void deleteAssessment()
    {
        Quiz quiz = getSelectedQuizFromTable();
        if(quiz == null) return;
        if(JOptionPane.showConfirmDialog(this, "Delete '" + quiz.getTitle() + "' and its saved results?", "Delete assessment", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
        {
            try
            {
                store.removeQuiz(quiz.getId());
                selectedQuizId = store.getQuizzes().get(0).getId();
                refreshAll();
                showPage("quizzes");
            }
            catch(IllegalArgumentException exception)
            {
                JOptionPane.showMessageDialog(this, exception.getMessage(), "Cannot delete", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void refreshQuestionTable()
    {
        questionModel.setRowCount(0);
        Quiz quiz = getSelectedQuiz();
        if(quiz == null) return;
        int number = 1;
        for(Question question : quiz.getQuestions())
            questionModel.addRow(new Object[] { number++, question.getPrompt(), question.getType(), question.getPoints(), question.getHint().isEmpty() ? "—" : "Yes" });
    }

    private void addQuestion()
    {
        Quiz quiz = getSelectedQuiz();
        if(quiz == null) { JOptionPane.showMessageDialog(this, "Create an assessment first."); return; }
        Question question = QuestionEditorDialog.showDialog(this, null);
        if(question != null)
        {
            quiz.addQuestion(question);
            store.saveQuiz(quiz);
            refreshAll();
            showPage("questions");
        }
    }

    private void editQuestion()
    {
        Quiz quiz = getSelectedQuiz();
        int row = questionTable.getSelectedRow();
        if(quiz == null || row < 0) { JOptionPane.showMessageDialog(this, "Select a question first."); return; }
        int modelRow = questionTable.convertRowIndexToModel(row);
        int index = (Integer) questionModel.getValueAt(modelRow, 0) - 1;
        List<Question> questions = quiz.getQuestions();
        if(index >= 0 && index < questions.size())
        {
            if(QuestionEditorDialog.showDialog(this, questions.get(index)) != null)
            {
                store.saveQuiz(quiz);
                refreshAll();
                showPage("questions");
            }
        }
    }

    private void deleteQuestion()
    {
        Quiz quiz = getSelectedQuiz();
        int row = questionTable.getSelectedRow();
        if(quiz == null || row < 0) { JOptionPane.showMessageDialog(this, "Select a question first."); return; }
        int modelRow = questionTable.convertRowIndexToModel(row);
        int index = (Integer) questionModel.getValueAt(modelRow, 0) - 1;
        List<Question> questions = quiz.getQuestions();
        if(index >= 0 && index < questions.size())
        {
            quiz.removeQuestion(questions.get(index));
            store.saveQuiz(quiz);
            refreshAll();
            showPage("questions");
        }
    }

    private void clearQuestions()
    {
        Quiz quiz = getSelectedQuiz();
        if(quiz == null) return;
        if(JOptionPane.showConfirmDialog(this, "Delete every question in '" + quiz.getTitle() + "'?", "Clear question bank", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
        {
            quiz.clearQuestions();
            store.saveQuiz(quiz);
            refreshAll();
            showPage("questions");
        }
    }

    private void refreshAll()
    {
        List<Student> students = store.getStudents();
        List<QuizResult> results = store.getResults();
        int passed = 0;
        int failed = 0;
        Set<String> studentsWhoWrote = new HashSet<>();
        for(QuizResult result : results)
        {
            studentsWhoWrote.add(result.getUsername());
            if(result.getStatus().startsWith("Passed")) passed++;
            else if(result.getStatus().equals("Failed")) failed++;
        }
        studentCount.setText(String.valueOf(students.size()));
        assessmentCount.setText(String.valueOf(store.getQuizzes().size()));
        wroteCount.setText(String.valueOf(studentsWhoWrote.size()));
        passedCount.setText(String.valueOf(passed));
        failedCount.setText(String.valueOf(failed));
        waitingCount.setText(String.valueOf(Math.max(0, students.size() * store.getQuizzes().size() - results.size())));

        dashboardModel.setRowCount(0);
        accountModel.setRowCount(0);
        for(Student student : students)
        {
            accountModel.addRow(new Object[] { student.getSurname(), student.getInitials(), student.getStudentNumber(), student.getUsername(), "Active" });
            for(Quiz quiz : store.getQuizzes())
            {
                QuizResult result = store.getResultForStudentAndQuiz(student.getUsername(), quiz.getId());
                if(result == null)
                {
                    String status = !quiz.isPublished() ? "On hold" : quiz.isMissed(LocalDateTime.now()) ? "Missed"
                            : quiz.isUpcoming(LocalDateTime.now()) ? "Upcoming" : quiz.getQuestions().isEmpty() ? "Not ready" : "Not written";
                    dashboardModel.addRow(new Object[] { student.getFullName(), student.getStudentNumber(), quiz.getTitle(),
                        "—", "Not yet submitted", status, "—", "—" });
                }
                else
                {
                    dashboardModel.addRow(new Object[] { result.getStudentName(), result.getStudentNumber(), result.getQuizTitle(),
                        result.getPercentage() + "%", result.getStatus(), "Submitted", ReportService.formatDuration(result.getTimeTakenSeconds()),
                        formatDate(result.getSubmittedAt()) });
                }
            }
        }

        quizModel.setRowCount(0);
        for(Quiz quiz : store.getQuizzes())
        {
            quizModel.addRow(new Object[] { quiz.getTitle(), quiz.getSubject(), quiz.getCategory(), quiz.getAssessmentType(),
                formatDate(quiz.getAvailableAt()), formatDate(quiz.getDueAt()), quiz.getDurationMinutes(),
                quiz.getQuestions().size(), quiz.isPublished() ? "Visible to students" : "On hold", quiz.getId() });
        }
        reportModel.setRowCount(0);
        for(QuizResult result : results)
        {
            reportModel.addRow(new Object[] { result.getStudentName(), result.getStudentNumber(), result.getQuizTitle(),
                result.getScore() + "/" + result.getTotalPoints(), result.getPercentage() + "%", result.getStatus(),
                ReportService.formatDuration(result.getTimeTakenSeconds()), formatDate(result.getSubmittedAt()) });
        }
        refreshQuestionTable();
        setTitle("Quizzzical | Administrator workspace | " + store.getQuizzes().size() + " assessment(s)");
    }

    private String formatDate(LocalDateTime date)
    {
        return date == null ? "Not set" : date.format(DATE_FORMAT);
    }

    private void showSelectStudent()
    {
        JOptionPane.showMessageDialog(this, "Select a student first.");
    }

    private void viewReport()
    {
        int row = reportTable.getSelectedRow();
        if(row < 0) { JOptionPane.showMessageDialog(this, "Select a result first."); return; }
        int modelRow = reportTable.convertRowIndexToModel(row);
        List<QuizResult> results = store.getResults();
        if(modelRow < 0 || modelRow >= results.size()) return;
        QuizResult selected = results.get(modelRow);
        JTextArea area = new JTextArea(ReportService.buildTextReport(selected));
        area.setEditable(false);
        area.setCaretPosition(0);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Student quiz report", JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportReports()
    {
        javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
        chooser.setSelectedFile(new File("Quizzzical_Reports.csv"));
        if(chooser.showSaveDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION)
        {
            try
            {
                ReportService.exportCsv(store.getResults(), chooser.getSelectedFile().toPath());
                JOptionPane.showMessageDialog(this, "Reports exported successfully.");
            }
            catch(Exception exception)
            {
                JOptionPane.showMessageDialog(this, "Could not export reports: " + exception.getMessage(), "Export error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearResults()
    {
        if(JOptionPane.showConfirmDialog(this, "Clear all submitted results? Students will be able to retake their assessments.",
                "Clear results", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
        {
            store.clearResults();
            refreshAll();
            showPage("reports");
        }
    }

    private void logout()
    {
        if(JOptionPane.showConfirmDialog(this, "Log out of the administrator account?", "Confirm logout", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
        {
            dispose();
            new LoginPage(store).setVisible(true);
        }
    }
}
