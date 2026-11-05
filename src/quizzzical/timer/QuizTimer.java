package quizzzical.timer;
/**
 *
 * @author tasol
 */

import javax.swing.Timer;

public class QuizTimer
{
    //stores the original quiz duration
    private int totalQuizSeconds;

    //stores the time remaining in seconds
    private int remainingSeconds;

    //stores the time when countdown starts or resumes
    private int timeAtLastStart;

    //stores the starting point for time measurement
    private long startTime;

    //controls countdown
    private Timer countdownTimer;

    //helps us format and calculate time
    private TimeManager timeManager;

    //allows QuizTimer to use the TimeoutHandler
    private TimeoutHandler timeoutHandler;

    //connects to the graphical timer panel
    private TimerPanel timerPanel;

    //checks the timer's state
    private boolean hasStarted = false;
    private boolean isPaused = false;
    private boolean isFinished = false;

    //checks whether the user can pause and resume
    private boolean canPauseResume = false;

    //checks warnings that have been displayed
    private boolean fiveMinuteWarningShown = false;
    private boolean twoMinuteWarningShown = false;
    private boolean oneMinuteWarningShown = false;

    //constructor
    public QuizTimer(int quizDuration)
    {
        if(quizDuration <= 0)
        {
            throw new IllegalArgumentException("Duration must be greater than zero");
        }

        //stores the original quiz duration
        totalQuizSeconds = quizDuration;

        //stores the remaining time
        remainingSeconds = quizDuration;

        timeManager = new TimeManager();
        timeoutHandler = new TimeoutHandler();

        //updates the countdown every second
        countdownTimer = new Timer(1000, event -> updateTimer());
    }

    //sets the user permissions
    public void setUserRole(String userRole)
    {
        canPauseResume = userRole != null && (userRole.equalsIgnoreCase("TEACHER") || userRole.equalsIgnoreCase("EDUCATOR") || userRole.equalsIgnoreCase("ADMIN") || userRole.equalsIgnoreCase("MODERATOR"));
    }

    //connects the timer to its graphical panel
    public void setTimerPanel(TimerPanel timerPanel)
    {
        this.timerPanel = timerPanel;

        if(timerPanel != null)
        {
            timerPanel.setQuizTimer(this);

            //displays the actual quiz duration
            timerPanel.setTime(timeManager.formatTime(remainingSeconds));

            //updates the circular progress
            timerPanel.setProgress(totalQuizSeconds, remainingSeconds);
        }
    }

    //starts the countdown
    public void start()
    {
        if(!hasStarted && !isFinished)
        {
            hasStarted = true;

            //remembers the starting time
            timeAtLastStart = remainingSeconds;

            //records when countdown starts
            startTime = System.nanoTime();

            System.out.println("Time starts!");

            //updates graphical controls
            if(timerPanel != null)
            {
                timerPanel.showRunning();
            }

            displayTime();

            checkStartingWarnings();

            countdownTimer.start();
        }
    }

    //pauses the countdown
    public void pause()
    {
        if(canPauseResume && hasStarted && !isPaused && !isFinished)
        {
            //remembers the previous remaining time
            int previousRemaining = remainingSeconds;

            //calculates the actual remaining time
            updateRemainingTime();

            //checks whether a warning was crossed
            checkWarnings(previousRemaining);

            displayTime();

            //checks whether time expired before the pause
            if(remainingSeconds <= 0)
            {
                finishTimer();
                return;
            }

            //stops the countdown
            countdownTimer.stop();

            isPaused = true;

            System.out.println("Time paused!");

            //updates graphical controls
            if(timerPanel != null)
            {
                timerPanel.showPaused();
            }
        }
    }

    //resumes the countdown
    public void resume()
    {
        if(canPauseResume && hasStarted && isPaused
                && !isFinished && remainingSeconds > 0)
        {
            isPaused = false;

            //stores the time remaining when resuming
            timeAtLastStart = remainingSeconds;

            //starts measuring time again
            startTime = System.nanoTime();

            System.out.println("Time resumed!");

            //updates graphical controls
            if(timerPanel != null)
            {
                timerPanel.showRunning();
            }

            countdownTimer.start();
        }
    }

    //stops the countdown permanently
    public void stop()
    {
        if(hasStarted && !isFinished)
        {
            //updates the time before stopping
            if(!isPaused)
            {
                int previousRemaining = remainingSeconds;

                updateRemainingTime();

                checkWarnings(previousRemaining);

                displayTime();

                //checks whether time expired
                if(remainingSeconds <= 0)
                {
                    finishTimer();
                    return;
                }
            }

            //stops the countdown
            countdownTimer.stop();

            isFinished = true;
            isPaused = false;

            System.out.println("Time stopped!");

            //updates graphical controls
            if(timerPanel != null)
            {
                timerPanel.showStopped();
            }
        }
    }

    //calculates the actual remaining time
    private void updateRemainingTime()
    {
        //calculates how much time has passed
        long elapsedNanoseconds = System.nanoTime() - startTime;

        //converts nanoseconds into seconds
        long elapsedSeconds = elapsedNanoseconds / 1_000_000_000L;

        //calculates the remaining time
        long timeLeft = timeAtLastStart - elapsedSeconds;

        //prevents negative time
        if(timeLeft < 0)
        {
            remainingSeconds = 0;
        }
        else
        {
            remainingSeconds = (int) timeLeft;
        }
    }

    //updates the remaining time
    private void updateTimer()
    {
        //remembers the previous remaining time
        int previousRemaining = remainingSeconds;

        //calculates the actual remaining time
        updateRemainingTime();

        //displays the updated time
        displayTime();

        //checks the warning system
        checkWarnings(previousRemaining);

        //checks whether time has expired
        if(remainingSeconds <= 0)
        {
            finishTimer();
        }
    }

    //finishes the countdown when time expires
    private void finishTimer()
    {
        if(isFinished)
        {
            return;
        }

        countdownTimer.stop();

        remainingSeconds = 0;

        isFinished = true;
        isPaused = false;

        displayTime();

        System.out.println("Time is up!");

        //handles automatic quiz submission
        timeoutHandler.handleTimeout();

        //updates the graphical timer
        if(timerPanel != null)
        {
            timerPanel.setProgress(100, 0);
            timerPanel.showExpired();
        }
    }

    //displays the remaining time
    private void displayTime()
    {
        String formattedTime = timeManager.formatTime(remainingSeconds);

        System.out.println("Time remaining: " + formattedTime);

        //updates the graphical timer panel
        if(timerPanel != null)
        {
            timerPanel.setTime(formattedTime);

            //updates the circular progress
            timerPanel.setProgress(totalQuizSeconds, remainingSeconds);
        }
    }

    //checks warnings when the quiz first starts
    private void checkStartingWarnings()
    {
        if(remainingSeconds == 300)
        {
            showFiveMinuteWarning();
        }

        if(remainingSeconds == 120)
        {
            showTwoMinuteWarning();
        }

        if(remainingSeconds == 60)
        {
            showOneMinuteWarning();
        }
    }

    //checks warnings that must be displayed
    private void checkWarnings(int previousRemaining)
    {
        if(!fiveMinuteWarningShown && previousRemaining > 300 && remainingSeconds <= 300)
        {
            showFiveMinuteWarning();
        }

        if(!twoMinuteWarningShown && previousRemaining > 120 && remainingSeconds <= 120)
        {
            showTwoMinuteWarning();
        }

        if(!oneMinuteWarningShown && previousRemaining > 60 && remainingSeconds <= 60)
        {
            showOneMinuteWarning();
        }
    }

    //displays the five-minute warning
    private void showFiveMinuteWarning()
    {
        if(timerPanel != null)
        {
            timerPanel.showFiveMinuteWarning();
        }

        System.out.println("WARNING: 5 minutes remaining!");

        fiveMinuteWarningShown = true;
    }

    //displays the two-minute warning
    private void showTwoMinuteWarning()
    {
        if(timerPanel != null)
        {
            timerPanel.showTwoMinuteWarning();
        }

        System.out.println("WARNING: 2 minutes remaining!");

        twoMinuteWarningShown = true;
    }

    //displays the one-minute warning
    private void showOneMinuteWarning()
    {
        if(timerPanel != null)
        {
            timerPanel.showOneMinuteWarning();
        }

        System.out.println("FINAL WARNING: 1 minute remaining!");

        oneMinuteWarningShown = true;
    }
}

