package quizzzical.timer;

/**
 *
 * @author tasol
 */

public class QuizTimerDemo
{
    public static void main(String[] args) 
    {
        //start with 5 seconds
        QuizTimer quizTimer = new QuizTimer(5); 
        
        quizTimer.start();
        
        try
        {
            //let the timer run for 7 seconds
            Thread.sleep(7000);
            
            //stop the timer
            //quizTimer.stop();
        }
        catch(InterruptedException e)
        {
            System.out.println("Demo interrupted!");
            
            Thread.currentThread().interrupt();
        }
    }
}
