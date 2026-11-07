package quizzzical.ui;

import java.awt.GridLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import quizzzical.data.QuizStore;

/** Student self-registration. New accounts return to sign-in and do not launch a quiz. */
public class StudentSignUpDialog extends JDialog
{
    public static void showDialog(JFrame owner, QuizStore store)
    {
        JTextField surname = new JTextField(18);
        JTextField initials = new JTextField(18);
        JTextField studentNumber = new JTextField(18);
        JTextField username = new JTextField(18);
        JPasswordField password = new JPasswordField(18);
        JPasswordField confirm = new JPasswordField(18);
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Surname")); form.add(surname);
        form.add(new JLabel("Initials")); form.add(initials);
        form.add(new JLabel("Student number")); form.add(studentNumber);
        form.add(new JLabel("Choose username")); form.add(username);
        form.add(new JLabel("Create password (8+ characters)")); form.add(password);
        form.add(new JLabel("Confirm password")); form.add(confirm);
        int answer = JOptionPane.showConfirmDialog(owner, form, "Create a student account",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if(answer != JOptionPane.OK_OPTION) return;
        String newPassword = new String(password.getPassword());
        if(!newPassword.equals(new String(confirm.getPassword())))
        {
            JOptionPane.showMessageDialog(owner, "The passwords do not match.", "Account not created", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try
        {
            store.registerStudent(surname.getText(), initials.getText(), studentNumber.getText(), username.getText(), newPassword);
            JOptionPane.showMessageDialog(owner, "Your student account has been created. Sign in to view your dashboard and assessment schedule.",
                    "Account created", JOptionPane.INFORMATION_MESSAGE);
        }
        catch(IllegalArgumentException exception)
        {
            JOptionPane.showMessageDialog(owner, exception.getMessage(), "Account not created", JOptionPane.WARNING_MESSAGE);
        }
    }
}
