
package quizzzical.ui;

/**
 *
 * @author tasol
 */

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class QuestionEditorDialog
{
    public static void main(String[] args)
    {
        //creates the application on the Swing event thread
        SwingUtilities.invokeLater(() ->
        {
            //creates the application window
            JFrame frame = new JFrame("Quizzzical Assessment");

            //creates the quiz-taking screen
            QuizDetailsDialog quizPanel = new QuizDetailsDialog(
                    600,
                    "ADMIN"
            );

            //creates the answer options
            String[] options = {
                "A. JFrame",
                "B. JButton",
                "C. JPanel",
                "D. JLabel"
            };

            //displays the sample question
            quizPanel.setQuestion(1, "Which Java component is used to create a clickable button?", options);

            //adds the quiz panel to the window
            frame.add(quizPanel);

            //configures the window
            frame.setSize(700, 500);

            frame.setLocationRelativeTo(null);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            //displays the application
            frame.setVisible(true);
        });
    }
}
