package quizzzical.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import quizzzical.data.QuizStore;
import quizzzical.model.QuestionReview;
import quizzzical.model.QuizResult;
import quizzzical.model.Student;
import quizzzical.service.ReportService;

public class ResultsPage extends JFrame
{
    private final QuizStore store;
    private final Student student;
    private final QuizResult result;

    public ResultsPage(QuizStore store, Student student, QuizResult result, boolean automatic)
    {
        this.store = store;
        this.student = student;
        this.result = result;
        setTitle("Quizzzical | Your result");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(850, 620));
        setSize(1000, 720);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));
        add(new BrandHeader("YOUR QUIZ REPORT  •  " + student.getFullName().toUpperCase()), BorderLayout.NORTH);

        JPanel summary = new JPanel(new GridLayout(1, 4, 8, 8));
        summary.setBorder(BorderFactory.createEmptyBorder(10, 12, 5, 12));
        summary.add(summaryCard("Score", result.getScore() + " / " + result.getTotalPoints()));
        summary.add(summaryCard("Grade", result.getPercentage() + "%"));
        summary.add(summaryCard("Result", result.getStatus()));
        summary.add(summaryCard("Time taken", ReportService.formatDuration(result.getTimeTakenSeconds())));

        DefaultTableModel model = new DefaultTableModel(new Object[] {"#", "Question", "Your answer", "Correct answer", "Points"}, 0)
        {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        int number = 1;
        for(QuestionReview review : result.getReviews())
        {
            model.addRow(new Object[] { number++, review.getQuestion(), empty(review.getStudentAnswer()),
                review.getCorrectAnswer(), review.getPointsAwarded() + " / " + review.getPointsPossible() });
        }
        JTable table = new JTable(model);
        table.setRowHeight(42);
        table.setAutoCreateRowSorter(true);
        table.getColumnModel().getColumn(1).setPreferredWidth(270);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(190);

        JPanel center = new JPanel(new BorderLayout(6, 6));
        center.add(summary, BorderLayout.NORTH);
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        JLabel note = new JLabel(automatic ? "The quiz was submitted automatically when the timer ended or the quiz was exited." : "Your answers have been submitted and saved.");
        note.setBorder(BorderFactory.createEmptyBorder(4, 14, 4, 14));
        note.setForeground(new Color(90, 85, 110));
        center.add(note, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        JButton export = new JButton("Save report as text file");
        JButton logout = new JButton("Back to student dashboard");
        buttons.add(export);
        buttons.add(logout);
        add(buttons, BorderLayout.SOUTH);
        export.addActionListener(event -> exportReport());
        logout.addActionListener(event -> returnToDashboard());
        addWindowListener(new WindowAdapter()
        {
            @Override public void windowClosing(WindowEvent event) { returnToDashboard(); }
        });
    }


    private void returnToDashboard()
    {
        dispose();
        new StudentDashboard(store, student).setVisible(true);
    }

    private JPanel summaryCard(String title, String value)
    {
        JPanel panel = new JPanel(new BorderLayout(3, 4));
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(226, 220, 241)),
                BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        JLabel heading = new JLabel(title, JLabel.CENTER);
        heading.setForeground(new Color(100, 95, 115));
        JLabel data = new JLabel(value, JLabel.CENTER);
        data.setFont(new Font("SansSerif", Font.BOLD, 17));
        data.setForeground(new Color(76, 57, 150));
        panel.add(heading, BorderLayout.NORTH);
        panel.add(data, BorderLayout.CENTER);
        return panel;
    }

    private String empty(String value)
    {
        return value == null || value.trim().isEmpty() ? "Not answered" : value;
    }

    private void exportReport()
    {
        javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
        chooser.setSelectedFile(new File("Quizzzical_" + student.getStudentNumber() + "_Report.txt"));
        if(chooser.showSaveDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION)
        {
            try
            {
                ReportService.exportText(result, chooser.getSelectedFile().toPath());
                JOptionPane.showMessageDialog(this, "Your report has been saved.");
            }
            catch(Exception exception)
            {
                JOptionPane.showMessageDialog(this, "Could not save the report: " + exception.getMessage(), "Save error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
