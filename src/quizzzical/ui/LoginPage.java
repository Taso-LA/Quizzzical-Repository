package quizzzical.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import quizzzical.data.QuizStore;
import quizzzical.model.Student;

public class LoginPage extends JFrame
{
    private final QuizStore store;
    private final JTextField usernameField = new JTextField(19);
    private final JPasswordField passwordField = new JPasswordField(19);

    public LoginPage(QuizStore store)
    {
        this.store = store;
        setTitle("Quizzzical | Sign in");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(580, 510));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        add(new BrandHeader("A smarter way to learn, practise and prove what you know."), BorderLayout.NORTH);

        JPanel background = new JPanel(new GridBagLayout());
        background.setBackground(new Color(246, 244, 252));
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 220, 241)),
                BorderFactory.createEmptyBorder(24, 30, 24, 30)));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        JLabel welcome = new JLabel("Welcome to Quizzzical", JLabel.CENTER);
        welcome.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 25));
        welcome.setForeground(new Color(49, 39, 91));
        card.add(welcome, c);
        c.gridy++;
        JLabel info = new JLabel("Sign in to your student or administrator account", JLabel.CENTER);
        info.setForeground(new Color(105, 100, 120));
        card.add(info, c);
        c.gridwidth = 1; c.gridy++; c.gridx = 0;
        card.add(new JLabel("Username"), c);
        c.gridx = 1; card.add(usernameField, c);
        c.gridy++; c.gridx = 0;
        card.add(new JLabel("Password"), c);
        c.gridx = 1; card.add(passwordField, c);
        c.gridy++; c.gridx = 0; c.gridwidth = 2;
        JButton loginButton = new JButton("SIGN IN");
        styleButton(loginButton);
        card.add(loginButton, c);
        c.gridy++;
        JButton signupButton = new JButton("CREATE A STUDENT ACCOUNT");
        signupButton.setFocusPainted(false);
        card.add(signupButton, c);
        c.gridy++;
        JLabel hint = new JLabel("New student? Create an account first. Admin access is assigned by the school.", JLabel.CENTER);
        hint.setFont(new Font("SansSerif", Font.PLAIN, 11));
        hint.setForeground(new Color(115, 110, 130));
        card.add(hint, c);
        background.add(card);
        add(background, BorderLayout.CENTER);

        loginButton.addActionListener(event -> login());
        passwordField.addActionListener(event -> login());
        signupButton.addActionListener(event -> StudentSignUpDialog.showDialog(this, store));
        getRootPane().setDefaultButton(loginButton);
    }

    private void styleButton(JButton button)
    {
        button.setBackground(new Color(105, 78, 190));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(180, 38));
    }

    private void login()
    {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        if(username.isEmpty() || password.isEmpty())
        {
            JOptionPane.showMessageDialog(this, "Enter both your username and password.", "Missing details", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if(store.authenticateAdmin(username, password))
        {
            dispose();
            new AdminPage(store).setVisible(true);
            return;
        }
        if(store.authenticateStudent(username, password))
        {
            Student student = store.getStudent(username);
            dispose();
            new StudentDashboard(store, student).setVisible(true);
            return;
        }
        JOptionPane.showMessageDialog(this, "The username or password is incorrect.", "Sign-in failed", JOptionPane.ERROR_MESSAGE);
        passwordField.setText("");
    }
}
