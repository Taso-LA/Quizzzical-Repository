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
        
        //test 1: valid time
        int totalSeconds = timeManager.convertToSeconds(07, 03, 20);
        
        System.out.println("Total seconds: " + totalSeconds);
        System.out.println("Formatted time: " + timeManager.formatTime(totalSeconds));
        
        //test 2: invalid time
        try
        {
            timeManager.convertToSeconds(07, 75, 20);
        } catch(IllegalArgumentException e)
        {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
