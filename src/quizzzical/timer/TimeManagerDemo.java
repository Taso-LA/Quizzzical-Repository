package quizzzical.timer;
/**
 *
 * @author tasol
 */

public class TimeManagerDemo
{
    public static void main(String[] args)
    {
        //creating a time manager object
        TimeManager timeManager = new TimeManager();
        
        //setting the duration of the quiz to 30 minutes
        int quizDuration = timeManager.convertToSeconds(0, 30, 0);
        
        //setting the elapsed time to 8 minutes and 15 seconds
        int elapsedTime = timeManager.convertToSeconds(0, 8, 15);
        
        //calculating the remaining time
        int remainingTime = timeManager.calculateRemainingTime(quizDuration, elapsedTime);
        
        //displaying the results
        System.out.println("Quiz duration: " + timeManager.formatTime(quizDuration));
        System.out.println("Elapsed time: " + timeManager.formatTime(elapsedTime));
        System.out.println("Remaining time: " + timeManager.formatTime(remainingTime));
        
    }
}