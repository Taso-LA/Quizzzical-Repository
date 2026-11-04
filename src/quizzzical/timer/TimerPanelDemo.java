package quizzzical.timer;
/**
 *
 * @author tasol
 */

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class TimerPanelDemo
{
    public static void main(String[] args)
    {
        //creates the graphical window
        SwingUtilities.invokeLater(() ->
        {
            JFrame frame = new JFrame("Quizzzical");

            //creates the timer panel
            TimerPanel timerPanel = new TimerPanel();

            //adds the panel to the window
            frame.add(timerPanel);

            //sets window properties
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            frame.setSize(450, 220);

            frame.setLocationRelativeTo(null);

            //displays the window
            frame.setVisible(true);
        });
    }
}
