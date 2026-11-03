package quizzzical;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/** Admin point of view. Placeholder: a grades table for the group to fill in. */
public class AdminPage extends JFrame {

public AdminPage() {
super("Quizzical!! - Admin");
setDefaultCloseOperation(EXIT_ON_CLOSE);
setContentPane(buildContent(() -> {
dispose();
new LoginPage().setVisible(true);
}));
setSize(900, 600);
setLocationRelativeTo(null);
}

public static JPanel buildContent(Runnable onLogout) {
JPanel root = new JPanel(new BorderLayout());
root.setBackground(LoginPage.LIGHT);
root.add(LoginPage.header("Admin view", onLogout), BorderLayout.NORTH);

// Each student's grades (replace the sample row with real results later)
DefaultTableModel model = new DefaultTableModel(
new Object[][]{{"student51", "-", "-"}},
new String[]{"Student", "Quiz", "Grade"}) {
@Override public boolean isCellEditable(int r, int c) { return false; }
};
JTable table = new JTable(model);
table.setRowHeight(30);
table.setFont(new Font("SansSerif", Font.PLAIN, 16));
table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 16));

JScrollPane scroll = new JScrollPane(table);
scroll.setColumnHeaderView(table.getTableHeader());
scroll.setBorder(BorderFactory.createEmptyBorder(24, 24, 12, 24));
scroll.getViewport().setBackground(Color.WHITE);
root.add(scroll, BorderLayout.CENTER);

JLabel note = new JLabel("Student grades will appear here once quizzes are completed.");
note.setForeground(LoginPage.NAVY);
note.setBorder(BorderFactory.createEmptyBorder(0, 24, 20, 24));
root.add(note, BorderLayout.SOUTH);
return root;
}

// Lets you run this page on its own (Run File) to check how it looks
public static void main(String[] args) {
SwingUtilities.invokeLater(() -> new AdminPage().setVisible(true));
}
}