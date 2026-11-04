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
        QuizTimer quizTimer = new QuizTimer(10); 
        
        quizTimer.start();
        
        try
        {
            Thread.sleep(12000);
        }
        catch(InterruptedException e)
        {
            System.out.println("Demo interrupted!");
            
            Thread.currentThread().interrupt();
        }
    }
}
