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
        
        //setting the duration of the quiz to 50 minutes
        int quizDuration = timeManager.convertToSeconds(0, 50, 0);
        
        //setting the elapsed time to 12 minutes and 35 seconds
        int elapsedTime = timeManager.convertToSeconds(0, 12, 35);
        
        //calculating the remaining time
        int remainingTime = timeManager.calculateRemainingTime(quizDuration, elapsedTime);
        
        //displaying the results
        System.out.println("Quizzzical Timer");
        System.out.println("________________");
        
        System.out.println("Quiz duration: " + timeManager.formatTime(quizDuration));
        System.out.println("Elapsed time: " + timeManager.formatTime(elapsedTime));
        System.out.println("Remaining time: " + timeManager.formatTime(remainingTime));
        
        //testing what happens when time expires
        int expiredTime = timeManager.calculateRemainingTime(quizDuration, 2800);
        
        System.out.println("After time expires: " + timeManager.formatTime(expiredTime));
    }
}