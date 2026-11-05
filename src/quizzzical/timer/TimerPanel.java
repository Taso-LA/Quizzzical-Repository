
package quizzzical.timer;

/**
 *
 * @author tasol
 */

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;

public class TimerPanel extends JPanel
{
    //labels used to display timer information
    private JLabel titleLabel;
    private JLabel timeLabel;
    private JLabel statusLabel;

    //progress bar used to display quiz progress
    private JProgressBar progressBar;

    //buttons used to control the timer
    private JButton startButton;
    private JButton pauseButton;
    private JButton resumeButton;
    private JButton stopButton;

    //reference to the countdown object
    private QuizTimer quizTimer;

    //stores the current user role
    private String userRole = "STUDENT";

    //checks whether the user can pause and resume
    private boolean canPauseResume = false;

    //constructor
    public TimerPanel()
    {
        //sets the main panel layout
        setLayout(new BorderLayout(15, 15));

        //sets the background colour
        setBackground(new Color(241, 245, 250));

        //adds spacing around the panel
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        //creates the title label
        titleLabel = new JLabel(
                "QUIZZZICAL TEST!",
                SwingConstants.CENTER
        );

        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(24, 49, 83));

        //creates the time label
        timeLabel = new JLabel(
                "00:00:00",
                SwingConstants.CENTER
        );

        timeLabel.setFont(new Font("Arial", Font.BOLD, 34));
        timeLabel.setForeground(new Color(24, 49, 83));

        //creates the progress bar
        progressBar = new JProgressBar(0, 100);

        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setString("0% completed");

        progressBar.setForeground(new Color(22, 132, 71));
        progressBar.setBackground(new Color(220, 228, 238));

        progressBar.setPreferredSize(new Dimension(420, 20));

        //creates a panel to hold the time and progress bar
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));

        centerPanel.setBackground(new Color(241, 245, 250));

        centerPanel.add(timeLabel, BorderLayout.CENTER);
        centerPanel.add(progressBar, BorderLayout.SOUTH);

        //creates the status label
        statusLabel = new JLabel(
                "Ready to start",
                SwingConstants.CENTER
        );

        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusLabel.setForeground(new Color(22, 132, 71));

        //creates the four buttons
        startButton = new JButton("Start");
        pauseButton = new JButton("Pause");
        resumeButton = new JButton("Resume");
        stopButton = new JButton("Stop");

        //styles the buttons
        styleButton(startButton);
        styleButton(pauseButton);
        styleButton(resumeButton);
        styleButton(stopButton);

        //initially disables buttons that cannot be used yet
        pauseButton.setEnabled(false);
        resumeButton.setEnabled(false);
        stopButton.setEnabled(false);

        //creates a panel to hold the buttons
        JPanel buttonPanel = new JPanel();

        buttonPanel.setLayout(
                new FlowLayout(FlowLayout.CENTER, 6, 3)
        );

        buttonPanel.setBackground(new Color(241, 245, 250));

        //adds buttons to the button panel
        buttonPanel.add(startButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(resumeButton);
        buttonPanel.add(stopButton);

        //creates a panel for the status and buttons
        JPanel bottomPanel = new JPanel(
                new BorderLayout(5, 5)
        );

        bottomPanel.setBackground(new Color(241, 245, 250));

        bottomPanel.add(statusLabel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        //adds components to the main timer panel
        add(titleLabel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        //connects the Start button
        startButton.addActionListener(event ->
        {
            if(quizTimer != null)
            {
                quizTimer.start();
            }
        });

        //connects the Pause button
        pauseButton.addActionListener(event ->
        {
            if(quizTimer != null && canPauseResume)
            {
                quizTimer.pause();
            }
        });

        //connects the Resume button
        resumeButton.addActionListener(event ->
        {
            if(quizTimer != null && canPauseResume)
            {
                quizTimer.resume();
            }
        });

        //connects the Stop button
        stopButton.addActionListener(event ->
        {
            if(quizTimer != null)
            {
                quizTimer.stop();
            }
        });

        //sets the default user role
        setUserRole("STUDENT");
    }

    //styles the buttons
    private void styleButton(JButton button)
    {
        button.setFont(new Font("Arial", Font.PLAIN, 12));

        button.setPreferredSize(new Dimension(75, 28));

        button.setFocusPainted(false);
    }

    //sets the user role
    public void setUserRole(String userRole)
    {
        if(userRole == null)
        {
            userRole = "STUDENT";
        }

        this.userRole = userRole;

        //checks which roles can pause and resume
        canPauseResume =
                userRole.equalsIgnoreCase("TEACHER")
                || userRole.equalsIgnoreCase("EDUCATOR")
                || userRole.equalsIgnoreCase("ADMIN")
                || userRole.equalsIgnoreCase("MODERATOR");

        //hides pause and resume buttons from students
        pauseButton.setVisible(canPauseResume);
        resumeButton.setVisible(canPauseResume);

        //updates the timer permissions
        if(quizTimer != null)
        {
            quizTimer.setUserRole(userRole);
        }

        revalidate();
        repaint();
    }

    //connects this panel to the countdown
    public void setQuizTimer(QuizTimer quizTimer)
    {
        this.quizTimer = quizTimer;

        if(quizTimer != null)
        {
            quizTimer.setUserRole(userRole);
        }
    }

    //updates the displayed time
    public void setTime(String time)
    {
        timeLabel.setText(time);
    }

    //updates the quiz progress bar
    public void setProgress(int totalSeconds, int remainingSeconds)
    {
        if(totalSeconds <= 0)
        {
            return;
        }

        //calculates the time already used
        long elapsedSeconds = (long) totalSeconds - remainingSeconds;

        if(elapsedSeconds < 0)
        {
            elapsedSeconds = 0;
        }

        //calculates the percentage completed
        int progress = (int) ((elapsedSeconds * 100) / totalSeconds);

        if(progress > 100)
        {
            progress = 100;
        }

        //updates the progress bar
        progressBar.setValue(progress);
        progressBar.setString(progress + "% completed");
    }

    //displays the five-minute warning
    public void showFiveMinuteWarning()
    {
        showWarning(
                "WARNING: 5 minutes remaining!",
                new Color(180, 130, 0)
        );
    }

    //displays the two-minute warning
    public void showTwoMinuteWarning()
    {
        showWarning(
                "WARNING: 2 minutes remaining!",
                new Color(220, 110, 0)
        );
    }

    //displays the one-minute warning
    public void showOneMinuteWarning()
    {
        showWarning(
                "FINAL WARNING: 1 minute remaining!",
                Color.RED
        );
    }

    //displays a warning using a selected colour
    private void showWarning(String message, Color warningColor)
    {
        statusLabel.setText(message);

        statusLabel.setForeground(warningColor);

        timeLabel.setForeground(warningColor);

        //changes the progress bar colour
        progressBar.setForeground(warningColor);
    }

    //displays the expired message
    public void showExpired()
    {
        statusLabel.setText("TIME EXPIRED - QUIZ SUBMITTED");

        statusLabel.setForeground(new Color(150, 0, 0));
        timeLabel.setForeground(new Color(150, 0, 0));

        progressBar.setValue(100);
        progressBar.setString("100% completed");

        //disables all buttons after submission
        startButton.setEnabled(false);
        pauseButton.setEnabled(false);
        resumeButton.setEnabled(false);
        stopButton.setEnabled(false);
    }

    //updates buttons when the timer is running
    public void showRunning()
    {
        statusLabel.setText("Quiz in progress");

        statusLabel.setForeground(new Color(22, 132, 71));

        timeLabel.setForeground(new Color(24, 49, 83));

        progressBar.setForeground(new Color(22, 132, 71));

        startButton.setEnabled(false);
        pauseButton.setEnabled(canPauseResume);
        resumeButton.setEnabled(false);
        stopButton.setEnabled(true);
    }

    //updates buttons when the timer is paused
    public void showPaused()
    {
        statusLabel.setText("Quiz paused");

        statusLabel.setForeground(new Color(180, 130, 0));
        timeLabel.setForeground(new Color(180, 130, 0));

        progressBar.setForeground(new Color(217, 154, 24));

        startButton.setEnabled(false);
        pauseButton.setEnabled(false);
        resumeButton.setEnabled(canPauseResume);
        stopButton.setEnabled(true);
    }

    //updates buttons when the timer is stopped
    public void showStopped()
    {
        statusLabel.setText("Timer stopped");

        statusLabel.setForeground(new Color(181, 45, 53));
        timeLabel.setForeground(new Color(181, 45, 53));

        progressBar.setForeground(new Color(181, 45, 53));

        startButton.setEnabled(false);
        pauseButton.setEnabled(false);
        resumeButton.setEnabled(false);
        stopButton.setEnabled(false);
    }
}
