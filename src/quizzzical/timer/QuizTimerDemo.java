package quizzzical.timer;

/**
 *
 * @author tasol
 */

public class QuizTimerDemo
{
    public static void main(String[] args) 
    {
        //start with 5 minutes and 2 seconds
        QuizTimer quizTimer = new QuizTimer(60); 
        
        quizTimer.start();
        
        try
        {
            //let the timer run for 3 seconds
            Thread.sleep(3500);
            
            //stop the timer
            quizTimer.stop();
        }
        catch(InterruptedException e)
        {
            System.out.println("Demo interrupted!");
            
            Thread.currentThread().interrupt();
        }
    }
}
