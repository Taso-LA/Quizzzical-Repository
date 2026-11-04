package quizzzical.timer;
/**
 *
 * @author tasol
 */

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

public class TimerPanel extends JPanel
{
    //labels used to display timer information
    private JLabel titleLabel;
    private JLabel timeLabel;
    private JLabel statusLabel;

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

        //adds labels to the panel
        add(titleLabel, BorderLayout.NORTH);

        add(timeLabel, BorderLayout.CENTER);

        add(statusLabel, BorderLayout.SOUTH);
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
    }

    //restores the normal timer appearance
    public void setStatusMessage(String message)
    {
        statusLabel.setText(message);

        statusLabel.setForeground(Color.GREEN.darker());

        timeLabel.setForeground(new Color(30, 60, 120));
    }
}