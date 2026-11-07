package quizzzical.timer;

import javax.swing.Timer;

public class QuizTimer
{
    private final int totalSeconds;
    private int remainingSeconds;
    private final Timer countdown;
    private final TimerPanel panel;
    private final TimeoutHandler timeoutHandler;
    private long lastStartNanos;
    private boolean running;
    private boolean finished;

    public QuizTimer(int totalSeconds, TimerPanel panel, TimeoutHandler timeoutHandler)
    {
        if(totalSeconds < 1) throw new IllegalArgumentException("Timer duration must be positive.");
        this.totalSeconds = totalSeconds;
        this.remainingSeconds = totalSeconds;
        this.panel = panel;
        this.timeoutHandler = timeoutHandler;
        countdown = new Timer(250, event -> update());
        if(panel != null) panel.updateTime(remainingSeconds);
    }

    public void start()
    {
        if(!running && !finished)
        {
            running = true;
            lastStartNanos = System.nanoTime();
            if(panel != null) panel.setStatus("Quiz in progress - pausing is disabled");
            countdown.start();
        }
    }

    private void update()
    {
        if(!running || finished) return;
        long elapsed = (System.nanoTime() - lastStartNanos) / 1_000_000_000L;
        int newRemaining = Math.max(0, remainingSeconds - (int) elapsed);
        if(newRemaining != remainingSeconds)
        {
            remainingSeconds = newRemaining;
            lastStartNanos = System.nanoTime();
            if(panel != null) panel.updateTime(remainingSeconds);
        }
        if(remainingSeconds <= 0)
        {
            finish();
            timeoutHandler.handleTimeout();
        }
    }

    public int getRemainingSeconds()
    {
        if(running && !finished)
        {
            long elapsed = (System.nanoTime() - lastStartNanos) / 1_000_000_000L;
            return Math.max(0, remainingSeconds - (int) elapsed);
        }
        return remainingSeconds;
    }

    public void stop()
    {
        remainingSeconds = getRemainingSeconds();
        finish();
    }

    private void finish()
    {
        countdown.stop();
        running = false;
        finished = true;
        if(panel != null) panel.setStatus("Timer stopped");
    }

    public int getTotalSeconds() { return totalSeconds; }
}
