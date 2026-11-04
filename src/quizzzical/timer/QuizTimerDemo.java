package quizzzical.timer;

/**
 *
 * @author tasol
 */

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class QuizTimerDemo
{
    public static void main(String[] args)
    {
        //creates the graphical interface on the EDT
        SwingUtilities.invokeLater(() ->
        {
            //creates the application window
            JFrame frame = new JFrame("Quizzzical");

            //creates the graphical timer panel
            TimerPanel timerPanel = new TimerPanel();

            //creates the countdown
            QuizTimer quizTimer = new QuizTimer(135);

            //connects the countdown to the panel
            quizTimer.setTimerPanel(timerPanel);

            //adds the panel to the window
            frame.add(timerPanel);

            //sets window properties
            frame.setSize(550, 250);

            frame.setLocationRelativeTo(null);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            //displays the window
            frame.setVisible(true);
        });
    }
}
