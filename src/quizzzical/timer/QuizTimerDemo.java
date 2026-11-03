package quizzzical.timer;

/**
 *
 * @author tasol
 */

public class QuizTimerDemo
{
    public static void main(String[] args) 
    {
        //creatind a timer with 10 seconds
        QuizTimer quizTimer = new QuizTimer(10);
        
        //starts the countdown
        quizTimer.start();
        
        //keeps the demo running for 11 seconds
        try
        {
            Thread.sleep(11000);
        }
        catch(InterruptedException e)
        {
            System.out.println("Demo interrupted!");
            
            Thread.currentThread().interrupt();
        }
    }
}
