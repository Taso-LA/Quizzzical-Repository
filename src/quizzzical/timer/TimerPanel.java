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
        //sets the panel layout
        setLayout(new BorderLayout(10, 10));

        //sets the panel background
        setBackground(Color.WHITE);

        //adds space around the panel
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

    //changes the displayed time
    public void setTime(String time)
    {
        timeLabel.setText(time);
    }

    //displays a warning message
    public void showWarning(String message)
    {
        statusLabel.setText(message);
        
        statusLabel.setForeground(Color.RED);
    }

    //changes the status message
    public void setStatusMessage(String message)
    {
        statusLabel.setText(message);

        statusLabel.setForeground(Color.GREEN.darker());
    }
}
