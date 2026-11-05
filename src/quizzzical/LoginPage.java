package quizzzical;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
* Login page for Quizzical auhor M.M !!
*
* Flow: Admin login + correct admin details -> AdminPage
* Student login + correct student details -> QuizPage
* anything else -> prints "Error", shows an error box, stays on the login page
*
* Launch with: new LoginPage().setVisible(true);
*/
public class LoginPage extends JFrame {

// ===== Login details: change them here =====
static final String ADMIN_USER = "admin101";
static final String ADMIN_PASS = "123456789";
static final String STUDENT_USER = "student51";
static final String STUDENT_PASS = "518709123";

// ===== Shared look (same blues as the blueprint) =====
static final Color BLUE = new Color(0x0B4DA2);
static final Color NAVY = new Color(0x082F66);
static final Color LIGHT = new Color(0xEAF1FB);
static final Color MID = new Color(0xC9DBF5);
static final Color ACCENT = new Color(0xF5A623);

interface LoginListener {
void onLogin(String role, String username, String password);
}

private final LoginPanel panel;

public LoginPage() {
super("Quizzical!! - Login");
setDefaultCloseOperation(EXIT_ON_CLOSE);
panel = new LoginPanel((role, user, pass) -> attempt(role, user, pass));
setContentPane(panel);
setSize(900, 600);
setLocationRelativeTo(null);
}

/** The role must match the details: admin details only work on Admin login, etc. */
public static boolean isValid(String role, String user, String pass) {
if ("Admin".equals(role)) return ADMIN_USER.equals(user) && ADMIN_PASS.equals(pass);
if ("Student".equals(role)) return STUDENT_USER.equals(user) && STUDENT_PASS.equals(pass);
return false;
}

private void attempt(String role, String user, String pass) {
if (isValid(role, user, pass)) {
dispose();
if ("Admin".equals(role)) new AdminPage().setVisible(true);
else new QuizPage(user).setVisible(true);
} else {
System.out.println("Error");
JOptionPane.showMessageDialog(this,
"Incorrect username or password for " + role + " login.",
"Error", JOptionPane.ERROR_MESSAGE);
panel.clear(); // back to a fresh login page
}
}

/** Blue title bar with a Logout button, used by the admin and quiz pages. */
static JPanel header(String title, Runnable onLogout) {
JPanel bar = new JPanel(new BorderLayout());
bar.setBackground(BLUE);
bar.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));
JLabel label = new JLabel(title);
label.setForeground(Color.WHITE);
label.setFont(new Font("SansSerif", Font.BOLD, 22));
JButton logout = new JButton("Logout");
logout.setFocusPainted(false);
logout.addActionListener(e -> onLogout.run());
bar.add(label, BorderLayout.WEST);
bar.add(logout, BorderLayout.EAST);
return bar;
}

// ================= The login screen (matches the sketch) =================
static class LoginPanel extends JPanel {
private final JTextField userField = new JTextField(18);
private final JPasswordField passField = new JPasswordField(18);

LoginPanel(LoginListener listener) {
super(new GridBagLayout());

JPanel card = new JPanel(new GridBagLayout()) {
@Override protected void paintComponent(Graphics g) {
Graphics2D g2 = (Graphics2D) g.create();
g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
RoundRectangle2D r = new RoundRectangle2D.Float(1, 1, getWidth() - 3, getHeight() - 3, 40, 40);
g2.setColor(Color.WHITE);
g2.fill(r);
g2.setColor(BLUE);
g2.setStroke(new BasicStroke(2f));
g2.draw(r);
g2.dispose();
}
};
card.setOpaque(false);
card.setBorder(BorderFactory.createEmptyBorder(30, 50, 34, 50));

GridBagConstraints c = new GridBagConstraints();
c.insets = new Insets(8, 8, 8, 8);
c.fill = GridBagConstraints.HORIZONTAL;

// Title + tagline
JLabel title = new JLabel("<html><b><span style='font-size:46px;color:#0B4DA2'>Quizzical</span>"
+ "<span style='font-size:46px;color:#F5A623'>!!</span></b></html>", SwingConstants.CENTER);
c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
card.add(title, c);

JLabel tagline = new JLabel("Test yourself...", SwingConstants.CENTER);
tagline.setFont(new Font("SansSerif", Font.PLAIN, 20));
tagline.setForeground(NAVY);
c.gridy = 1; c.insets = new Insets(0, 8, 22, 8);
card.add(tagline, c);

// Username / Password
c.insets = new Insets(8, 8, 8, 8);
c.gridwidth = 1;
addRow(card, c, 2, "Username:", userField);
addRow(card, c, 3, "Password:", passField);

// Admin login / Student login
JPanel buttons = new JPanel(new GridLayout(1, 2, 18, 0));
buttons.setOpaque(false);
RoundButton admin = new RoundButton("Admin login");
RoundButton student = new RoundButton("Student login");
admin.addActionListener(e -> listener.onLogin("Admin", userField.getText().trim(),
new String(passField.getPassword())));
student.addActionListener(e -> listener.onLogin("Student", userField.getText().trim(),
new String(passField.getPassword())));
buttons.add(admin);
buttons.add(student);
c.gridx = 0; c.gridy = 4; c.gridwidth = 2; c.insets = new Insets(22, 8, 0, 8);
card.add(buttons, c);

add(card);
}

private void addRow(JPanel card, GridBagConstraints c, int row, String text, JComponent field) {
JLabel label = new JLabel(text);
label.setFont(new Font("SansSerif", Font.BOLD, 17));
label.setForeground(NAVY);
field.setFont(new Font("SansSerif", Font.PLAIN, 17));
c.gridx = 0; c.gridy = row; c.weightx = 0;
card.add(label, c);
c.gridx = 1; c.weightx = 1;
card.add(field, c);
}

void clear() {
userField.setText("");
passField.setText("");
userField.requestFocusInWindow();
}

@Override protected void paintComponent(Graphics g) {
Graphics2D g2 = (Graphics2D) g.create();
g2.setPaint(new GradientPaint(0, 0, Color.WHITE, 0, getHeight(), MID));
g2.fillRect(0, 0, getWidth(), getHeight());
g2.dispose();
}
}

/** Pill-shaped button in the blueprint blue. */
static class RoundButton extends JButton {
private boolean hover;

RoundButton(String text) {
super(text);
setFont(new Font("SansSerif", Font.BOLD, 17));
setForeground(Color.WHITE);
setFocusPainted(false);
setBorderPainted(false);
setContentAreaFilled(false);
setBorder(BorderFactory.createEmptyBorder(12, 28, 12, 28));
setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
addMouseListener(new java.awt.event.MouseAdapter() {
@Override public void mouseEntered(java.awt.event.MouseEvent e) { hover = true; repaint(); }
@Override public void mouseExited(java.awt.event.MouseEvent e) { hover = false; repaint(); }
});
}

@Override protected void paintComponent(Graphics g) {
Graphics2D g2 = (Graphics2D) g.create();
g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
g2.setColor(hover ? NAVY : BLUE);
g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), getHeight(), getHeight()));
g2.dispose();
super.paintComponent(g);
}
}

public static void main(String[] args) {
SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
}
}