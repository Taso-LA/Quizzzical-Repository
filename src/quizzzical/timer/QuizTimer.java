package quizzzical.timer;
/**
 *
 * @author tasol
 */

import javax.swing.Timer;

public class QuizTimer 
{
    //stores the time remaining in seconds
    private int remainingSeconds;
    
    //stores he time when countdown statrs or resumes
    private int timeAtLastStart;
    
    //stores the starting point for time measurement
    private long startTime;
    
    //controlls countdown
    private Timer countdownTimer;
    
    //helps us format and calculate time
    private TimeManager timeManager;
    
    //allows QuizTimer to use the TimeoutHandler
    private TimeoutHandler timeoutHandler;
    
    //checks the timer's state
    private boolean hasStarted = false;
    private boolean isPaused = false;
    private boolean isFinished = false;
    
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
        
        remainingSeconds = quizDuration;
        
        timeManager = new TimeManager();
        timeoutHandler = new TimeoutHandler();
        
        //updates the countdown every second
        countdownTimer = new Timer(1000, event -> updateTimer());
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
            
            displayTime();
            
            checkStartingWarnings();
            
            countdownTimer.start();
        }
    }
    
    //pauses the countdown
    public void pause()
    {
        if(hasStarted && !isPaused && !isFinished)
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
        }
    }
    
    //resumes the countdown
    public void resume()
    {
        if(hasStarted && isPaused && !isFinished && remainingSeconds > 0)
        {
            isPaused = false;
            
            //stores the time remaining when resuming
            timeAtLastStart = remainingSeconds;
            
            //starts measuring time againg
            startTime = System.nanoTime();
            
            System.out.println("Time resumed!");
            
            countdownTimer.start();
        }
    }
    
    //stops the countdown permanently
    public void stop()
    {
        if(!isFinished)
        {
            countdownTimer.stop();
            
            isFinished = true;
            isPaused = false;
            
            System.out.println("Time stopped!");
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
    
    //finish the countdown when time expires
    private void finishTimer()
    {
        countdownTimer.stop();

        isFinished = true;
        isPaused = false;

        System.out.println("Time is up!");

        //tells TimeoutHandler to handle submission
        timeoutHandler.handleTimeout();
    }

    //displays the remaining time
    private void displayTime()
    {
        System.out.println("Time remaining: " + timeManager.formatTime(remainingSeconds));
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
    
    //checks warning that must be displayed
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

    //display the five minutes warning
    private void showFiveMinuteWarning()
    {
        System.out.println("Warning 5 minutes remaining!");
        
        fiveMinuteWarningShown = true;
    }
    
    //display the two minutes warning
    private void showTwoMinuteWarning()
    {
        System.out.println("Warning 2 minute remaining!");
        
        twoMinuteWarningShown = true;
    }
    
    //display the one minutes warning
    private void showOneMinuteWarning()
    {
        System.out.println("Warning 1 minute remaining!");
        
        oneMinuteWarningShown = true;
    }
}
