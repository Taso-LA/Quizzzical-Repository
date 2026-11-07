package quizzzical.timer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

public class TimerPanel extends JPanel
{
    private final JLabel timeLabel;
    private final JLabel statusLabel;
    private final JProgressBar progressBar;
    private final TimeManager timeManager = new TimeManager();
    private final int totalSeconds;

    public TimerPanel(int totalSeconds)
    {
        this.totalSeconds = totalSeconds;
        setLayout(new BorderLayout(8, 5));
        setBackground(new Color(248, 247, 255));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 215, 245)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        JLabel title = new JLabel("TIME REMAINING", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 11));
        title.setForeground(new Color(100, 82, 160));
        timeLabel = new JLabel(timeManager.formatTime(totalSeconds), SwingConstants.CENTER);
        timeLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        timeLabel.setForeground(new Color(55, 45, 100));
        statusLabel = new JLabel("Quiz in progress", SwingConstants.CENTER);
        statusLabel.setForeground(new Color(90, 90, 110));
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(100);
        progressBar.setStringPainted(true);
        progressBar.setString("100% time remaining");
        progressBar.setForeground(new Color(102, 76, 190));

        JPanel center = new JPanel(new BorderLayout(3, 3));
        center.setOpaque(false);
        center.add(timeLabel, BorderLayout.CENTER);
        center.add(progressBar, BorderLayout.SOUTH);
        add(title, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
    }

    public void updateTime(int seconds)
    {
        int safe = Math.max(0, seconds);
        timeLabel.setText(timeManager.formatTime(safe));
        int percent = totalSeconds == 0 ? 0 : (int) Math.round(safe * 100.0 / totalSeconds);
        progressBar.setValue(percent);
        progressBar.setString(percent + "% time remaining");
        if(safe <= 60) timeLabel.setForeground(new Color(190, 55, 65));
        else timeLabel.setForeground(new Color(55, 45, 100));
    }

    public void setStatus(String text) { statusLabel.setText(text); }
}
