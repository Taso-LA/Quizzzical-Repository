package quizzzical.ui;

import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import quizzzical.data.QuizStore;

public class AccountDialog
{
    public static void addStudent(JFrame owner, QuizStore store)
    {
        JTextField surname = new JTextField();
        JTextField initials = new JTextField();
        JTextField number = new JTextField();
        JTextField username = new JTextField();
        JPasswordField password = new JPasswordField();
        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 7));
        panel.add(new JLabel("Surname")); panel.add(surname);
        panel.add(new JLabel("Initials")); panel.add(initials);
        panel.add(new JLabel("Student number")); panel.add(number);
        panel.add(new JLabel("Username")); panel.add(username);
        panel.add(new JLabel("Temporary password")); panel.add(password);
        if(JOptionPane.showConfirmDialog(owner, panel, "Create student account", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        try
        {
            store.addStudent(surname.getText(), initials.getText(), number.getText(), username.getText(), new String(password.getPassword()));
        }
        catch(IllegalArgumentException exception)
        {
            JOptionPane.showMessageDialog(owner, exception.getMessage(), "Account not created", JOptionPane.WARNING_MESSAGE);
            return;
        }
    }

    public static void resetPassword(JFrame owner, QuizStore store, String username)
    {
        JPasswordField password = new JPasswordField();
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Enter the new password (at least 8 characters):"));
        panel.add(password);
        if(JOptionPane.showConfirmDialog(owner, panel, "Reset student password", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        try
        {
            store.resetStudentPassword(username, new String(password.getPassword()));
            JOptionPane.showMessageDialog(owner, "Student password updated.");
        }
        catch(IllegalArgumentException exception)
        {
            JOptionPane.showMessageDialog(owner, exception.getMessage(), "Password not changed", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void changeAdmin(JFrame owner, QuizStore store)
    {
        JTextField username = new JTextField(store.getAdminUsername());
        JPasswordField password = new JPasswordField();
        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 7));
        panel.add(new JLabel("New admin username")); panel.add(username);
        panel.add(new JLabel("New admin password")); panel.add(password);
        if(JOptionPane.showConfirmDialog(owner, panel, "Change administrator login", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        try
        {
            store.updateAdminCredentials(username.getText(), new String(password.getPassword()));
            JOptionPane.showMessageDialog(owner, "Administrator sign-in details updated. Keep them somewhere safe.");
        }
        catch(IllegalArgumentException exception)
        {
            JOptionPane.showMessageDialog(owner, exception.getMessage(), "Details not changed", JOptionPane.WARNING_MESSAGE);
        }
    }
}
