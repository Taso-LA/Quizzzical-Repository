package quizzzical;
/**
 * @author Quizzzical Team
 */

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import quizzzical.data.QuizStore;
import quizzzical.ui.LoginPage;


public class Main
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() ->
        {
            try
            {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            catch(Exception exception)
            {
                // use the default swing look and feel
            }

            QuizStore store = new QuizStore();
            new LoginPage(store).setVisible(true);
        });
    }
}
