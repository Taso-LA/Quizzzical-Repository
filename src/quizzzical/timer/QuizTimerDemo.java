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
        //creates the application on the Swing event thread
        SwingUtilities.invokeLater(() ->
        {
            //creates the application window
            JFrame frame = new JFrame("Quizzzical");

            //creates the graphical timer panel
            TimerPanel timerPanel = new TimerPanel();

            //sets the user role
            timerPanel.setUserRole("ADMIN");

            //creates the countdown with 10 seconds
            QuizTimer quizTimer = new QuizTimer(125);

            //connects the timer to the graphical panel
            quizTimer.setTimerPanel(timerPanel);

            //adds the timer panel to the window
            frame.add(timerPanel);

            //configures the window
            frame.setSize(600, 230);

            frame.setLocationRelativeTo(null);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            //displays the application
            frame.setVisible(true);
        });
    }
}
