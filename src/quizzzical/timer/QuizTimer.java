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
    
    //controlls countdown
    private Timer countdownTimer;
    
    //helps us format and calculate time
    private TimeManager timeManager;
    
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
        
        //updates the countdown every second
        countdownTimer = new Timer(1000, event -> updateTimer());
    }
    
    //starts the countdown
    public void start()
    {
        if(!hasStarted && !isFinished)
        {
            hasStarted = true;
            
            System.out.println("Time starts!");
            
            displayTime();
            
            checkWarnings();
            
            countdownTimer.start();
        }
    }
    
    //pauses the countdown
    public void pause()
    {
        if(countdownTimer.isRunning())
        {
            countdownTimer.stop();
            
            isPaused = true;
            
            System.out.println("Time paused!");
        }
    }
    
    //resumes the countdown
    public void resume()
    {
        if(hasStarted && !isFinished && remainingSeconds > 0 && !countdownTimer.isRunning())
        {
            isPaused = false;
            
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
    
    //updates the remaining time
    private void updateTimer()
    {
        remainingSeconds--;
        
        displayTime();
        
        checkWarnings();
        
        if(remainingSeconds <= 0)
        {
            countdownTimer.stop();
            
            isFinished = true;
            isPaused = false;
            
            System.out.println("Time is up...");
            System.out.println("Automatic submittion!");
        }
        
    }
    
    //displays the remaining time
    private void displayTime()
    {
        System.out.println("Time remaining: " + timeManager.formatTime(remainingSeconds));
    }
    
    //checks warning that must be displayed
    private void checkWarnings()
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
