
package quizzzical.ui;

/**
 *
 * @author tasol
 */

import quizzzical.timer.QuizTimer;
import quizzzical.timer.TimerPanel;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;
import quizzzical.model.QuestionReview;

public class QuizDetailsDialog extends JPanel
{
    //timer components
    private TimerPanel timerPanel;
    private QuizTimer quizTimer;

    //panel used to display quiz questions
    private QuestionReview questionPanel;

    //panel used to hold quiz controls
    private JPanel bottomPanel;

    //button used to submit the quiz
    private JButton submitButton;

    //constructor
    public QuizDetailsDialog(int quizDuration, String userRole)
    {
        //sets the main layout
        setLayout(new BorderLayout(15, 15));

        //sets the background colour
        setBackground(new Color(241, 245, 250));

        //adds spacing around the panel
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        //creates the timer panel
        timerPanel = new TimerPanel();

        //sets the user's role
        timerPanel.setUserRole(userRole);

        //creates the countdown
        quizTimer = new QuizTimer(quizDuration);

        //connects the timer to the graphical panel
        quizTimer.setTimerPanel(timerPanel);

        //creates the question panel
        questionPanel = new QuestionReview();

        //creates the submit button
        submitButton = new JButton("Submit Quiz");

        submitButton.setFont(new Font("Arial", Font.PLAIN, 12));

        submitButton.setPreferredSize(new Dimension(110, 28));

        submitButton.setFocusPainted(false);

        //creates a panel to hold the submit button
        bottomPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 5, 5)
        );

        bottomPanel.setBackground(new Color(241, 245, 250));

        bottomPanel.add(submitButton);

        //adds components to the quiz-taking screen
        add(timerPanel, BorderLayout.NORTH);

        add(questionPanel, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);

        //connects the Submit Quiz button
        submitButton.addActionListener(event ->
        {
            submitQuiz();
        });
    }

    //displays a question and its answer options
    public void setQuestion(int questionNumber, String question, String[] options)
    {
        questionPanel.setQuestion(
                questionNumber,
                question,
                options
        );
    }

    //gets the student's selected answer
    public String getSelectedAnswer()
    {
        return questionPanel.getSelectedAnswer();
    }

    //clears the selected answer
    public void clearAnswer()
    {
        questionPanel.clearAnswer();
    }

    //handles quiz submission
    private void submitQuiz()
    {
        //asks the student to confirm submission
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to submit your quiz?",
                "Confirm Submission",
                JOptionPane.YES_NO_OPTION
        );

        //checks whether the student confirmed
        if(choice == JOptionPane.YES_OPTION)
        {
            //stops the countdown
            quizTimer.stop();

            //disables the submit button
            submitButton.setEnabled(false);

            JOptionPane.showMessageDialog(
                    this,
                    "Quiz submission demonstration completed.\n"
                    + "Answer storage and grading will be connected later.",
                    "Quiz Submission",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
}
