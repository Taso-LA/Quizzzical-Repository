package quizzzical;

import javax.swing.*;
import java.awt.*;

/** Student quiz page. Placeholder: swap the centre label for the group's Quiz screen. */
public class QuizPage extends JFrame {

public QuizPage(String student) {
super("Quizzical!! - Quiz");
setDefaultCloseOperation(EXIT_ON_CLOSE);
setContentPane(buildContent(student, () -> {
dispose();
new LoginPage().setVisible(true);
}));
setSize(900, 600);
setLocationRelativeTo(null);
}

public static JPanel buildContent(String student, Runnable onLogout) {
JPanel root = new JPanel(new BorderLayout());
root.setBackground(LoginPage.LIGHT);
root.add(LoginPage.header("Quiz | " + student, onLogout), BorderLayout.NORTH);

JLabel body = new JLabel("Quiz questions go here", SwingConstants.CENTER);
body.setFont(new Font("SansSerif", Font.BOLD, 30));
body.setForeground(LoginPage.BLUE);
root.add(body, BorderLayout.CENTER);

JLabel note = new JLabel("Your grade will be shown after each quiz.");
note.setForeground(LoginPage.NAVY);
note.setBorder(BorderFactory.createEmptyBorder(0, 24, 20, 24));
root.add(note, BorderLayout.SOUTH);
return root;
}

// Lets you run this page on its own (Run File) to check how it looks
public static void main(String[] args) {
SwingUtilities.invokeLater(() -> new QuizPage("student51").setVisible(true));
}
}