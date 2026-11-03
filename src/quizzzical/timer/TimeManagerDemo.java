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
        
        //converts 07 hours, 03 minutes, 20 seconds
        int totalSeconds = timeManager.convertToSeconds(07, 03, 20);
        
        //displays the results
        System.out.println("Total Seconds: " + totalSeconds);
        
        //displays formatted time
        System.out.println("Formatted Time: " + timeManager.formatTime(totalSeconds));
    }
}
