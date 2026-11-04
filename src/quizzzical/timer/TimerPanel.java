package quizzzical.timer;
/**
 *
 * @author tasol
 */

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Font;

public class TimerPanel extends JPanel
{
    //labels used to display timer information
    private JLabel titleLabel;
    private JLabel timeLabel;
    private JLabel statusLabel;
    
    //buttons used to control the timer
    private JButton startButton;
    private JButton pauseButton;
    private JButton resumeButton;
    private JButton stopButton;

    //reference to the countdown object
    private QuizTimer quizTimer;
    
    //constructor
    public TimerPanel()
    {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        //creates the title label
        titleLabel = new JLabel("QUIZZICAL - QUIZ TIMER", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        //creates the time label
        timeLabel = new JLabel("00:10:00", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 48));
        timeLabel.setForeground(new Color(30, 60, 120));

        //creates the status label
        statusLabel = new JLabel("Quiz in progress", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        statusLabel.setForeground(Color.GREEN.darker());
        
        //creates the four buttons
        startButton = new JButton("Start");
        pauseButton = new JButton("Pause");
        resumeButton = new JButton("Resume");
        stopButton = new JButton("Stop");

        //initially disable buttons that cannot be used yet
        pauseButton.setEnabled(false);
        resumeButton.setEnabled(false);
        stopButton.setEnabled(false);
        
        //creates a panel to hold the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setBackground(Color.WHITE);
        
        //adds buttons to the button panel
        buttonPanel.add(startButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(resumeButton);
        buttonPanel.add(stopButton);
        
        //creates a panel for the status and buttons
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(statusLabel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        //adds components to the main timer panel
        add(titleLabel, BorderLayout.NORTH);
        add(timeLabel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        
        //connect the Start button
        startButton.addActionListener(event ->
        {
            if(quizTimer != null)
            {
                quizTimer.start();
            }
        });
        
        //connect the Pause button
        pauseButton.addActionListener(event ->
        {

            if(quizTimer != null)
            {
                quizTimer.pause();
            }
        });

        //connect the Resume button
        resumeButton.addActionListener(event ->
        {
            if(quizTimer != null)
            {
                quizTimer.resume();
            }
        });

        //connect the Stop button
        stopButton.addActionListener(event ->
        {
            if(quizTimer != null)
            {
                quizTimer.stop();
            }
        });
    }
    
    //connect this panel to the countdown
    public void setQuizTimer(QuizTimer quizTimer)
    {
        this.quizTimer = quizTimer;
    }

    //updates the displayed time
    public void setTime(String time)
    {
        timeLabel.setText(time);
    }

    //displays the five-minute warning
    public void showFiveMinuteWarning()
    {
        showWarning("WARNING: 5 minutes remaining!", new Color(180, 130, 0));
    }

    //displays the two-minute warning
    public void showTwoMinuteWarning()
    {
        showWarning("WARNING: 2 minutes remaining!", new Color(220, 110, 0));
    }

    //display the one-minute warning
    public void showOneMinuteWarning()
    {
        showWarning("FINAL WARNING: 1 minute remaining!", Color.RED);
    }

    //displays a warning using a selected colour
    private void showWarning(String message, Color warningColor)
    {
        statusLabel.setText(message);
        statusLabel.setForeground(warningColor);
        timeLabel.setForeground(warningColor);
    }

    //displays the expired message
    public void showExpired()
    {
        statusLabel.setText("TIME EXPIRED - QUIZ SUBMITTED");
        statusLabel.setForeground(new Color(150, 0, 0));
        timeLabel.setForeground(new Color(150, 0, 0));
        
        //disable all buttons after submission
        startButton.setEnabled(false);
        pauseButton.setEnabled(false);
        resumeButton.setEnabled(false);
        stopButton.setEnabled(false);
    }
    
    //update buttons when the timer is running
    public void showRunning()
    {
        statusLabel.setText("Quiz in progress");
        statusLabel.setForeground(Color.GREEN.darker());
        timeLabel.setForeground(new Color(30, 60, 120));

        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
        resumeButton.setEnabled(false);
        stopButton.setEnabled(true);
    }
    
    //update buttons when the timer is paused
    public void showPaused()
    {
        statusLabel.setText("Quiz paused");
        statusLabel.setForeground(new Color(180, 130, 0));

        startButton.setEnabled(false);
        pauseButton.setEnabled(false);
        resumeButton.setEnabled(true);
        stopButton.setEnabled(true);
    }
    
    //update buttons when the timer is stopped
    public void showStopped()
    {
        statusLabel.setText("Timer stopped");
        statusLabel.setForeground(Color.RED);

        startButton.setEnabled(false);
        pauseButton.setEnabled(false);
        resumeButton.setEnabled(false);
        stopButton.setEnabled(false);
    }
}