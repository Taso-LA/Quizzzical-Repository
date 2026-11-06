package quizzzical.model;

import quizzzical.ui.LoginPage;
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
* Student quiz page.
* - Countdown timer in the top bar; turns RED in the last 60 seconds.
* - The quiz is submitted automatically when the timer hits zero,
* or by the student pressing "Submit quiz".
* Placeholder: the centre label for the group's quiz questions.
*/
public class QuizResults extends JFrame {

// Quiz time limit in seconds (300 = 5 minutes). Change it here.
static final int QUIZ_SECONDS = 300;

// The timer turns red when this many seconds (or fewer) are left.
static final int WARNING_SECONDS = 60;

static final Color RED = new Color(0xD32F2F);

public QuizResults(String student) {
super("Quizzical!! - Quiz");
setDefaultCloseOperation(EXIT_ON_CLOSE);
setContentPane(buildContent(student, QUIZ_SECONDS, () -> {
dispose();
new LoginPage().setVisible(true);
}));
setSize(900, 600);
setLocationRelativeTo(null);
}

/** onExit runs after Logout, or after the quiz is submitted. */
public static JPanel buildContent(String student, int seconds, Runnable onExit) {
JPanel root = new JPanel(new BorderLayout());
root.setBackground(LoginPage.LIGHT);

QuizTimer timer = new QuizTimer(seconds);
AtomicBoolean submitted = new AtomicBoolean(false);

// Logout: stop the timer so it never keeps running in the background
Runnable leave = () -> {
submitted.set(true);
timer.stop();
onExit.run();
};

// Submit: used by the Submit button (auto = false) and by the timer at zero (auto = true)
Consumer<Boolean> submit = auto -> {
if (!submitted.compareAndSet(false, true)) return; // only ever submit once
timer.stop();
// TODO (group): mark the student's answers and work out the grade here
String msg = auto ? "Time is up! Your quiz was submitted automatically."
: "Your quiz has been submitted.";
JOptionPane.showMessageDialog(root, msg + "\nYour grade: (coming soon)",
"Quizzical!!", JOptionPane.INFORMATION_MESSAGE);
onExit.run();
};

// Top bar: title on the left, TIMER in the middle, Logout on the right
JPanel bar = LoginPage.header("Quiz | " + student, leave);
JPanel middle = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0)); // keeps the red badge snug
middle.setOpaque(false);
middle.add(timer.label());
bar.add(middle, BorderLayout.CENTER);
root.add(bar, BorderLayout.NORTH);

JLabel body = new JLabel("Quiz questions go here", SwingConstants.CENTER);
body.setFont(new Font("SansSerif", Font.BOLD, 30));
body.setForeground(LoginPage.BLUE);
root.add(body, BorderLayout.CENTER);

// Bottom: note on the left, Submit button on the right
JPanel footer = new JPanel(new BorderLayout());
footer.setOpaque(false);
footer.setBorder(BorderFactory.createEmptyBorder(10, 24, 20, 24));
JLabel note = new JLabel("Your grade will be shown after each quiz.");
note.setForeground(LoginPage.NAVY);
LoginPage.RoundButton submitBtn = new LoginPage.RoundButton("Submit quiz");
submitBtn.addActionListener(e -> {
int ok = JOptionPane.showConfirmDialog(root, "Submit your quiz now?",
"Submit", JOptionPane.YES_NO_OPTION);
if (ok == JOptionPane.YES_OPTION) submit.accept(false);
});
footer.add(note, BorderLayout.WEST);
footer.add(submitBtn, BorderLayout.EAST);
root.add(footer, BorderLayout.SOUTH);

// Time hits zero -> submit automatically
timer.onFinish(() -> submit.accept(true));
timer.start();
return root;
}

/** Countdown timer label (mm:ss). Turns red in the last WARNING_SECONDS. */
static class QuizTimer {
private final JLabel label = new JLabel("", SwingConstants.CENTER);
private final Timer timer;
private int remaining;
private Runnable finish = () -> {};

QuizTimer(int seconds) {
remaining = seconds;
label.setFont(new Font("SansSerif", Font.BOLD, 24));
label.setBorder(BorderFactory.createEmptyBorder(4, 18, 4, 18));
show();
timer = new Timer(1000, e -> {
remaining--;
show();
if (remaining <= 0) {
((Timer) e.getSource()).stop();
finish.run();
}
});
}

JLabel label() { return label; }
int secondsLeft() { return remaining; }
boolean isWarning() { return remaining <= WARNING_SECONDS; }
void onFinish(Runnable r) { finish = r; }
void start() { timer.start(); }
void stop() { timer.stop(); }

private void show() {
label.setText(String.format("Time left: %02d:%02d", remaining / 60, remaining % 60));
label.setForeground(Color.WHITE);
// Red background so it stays clearly visible on the blue top bar
label.setOpaque(isWarning());
label.setBackground(RED);
label.repaint();
}
}

// Lets you run this page on its own (Run File) to check how it looks
public static void main(String[] args) {
SwingUtilities.invokeLater(() -> new QuizResults("student51").setVisible(true));
}
}