package quizzzical.timer;
/**
 *
 * @author tasol
 */

//Time Manger will be responsible for time calculations and time formatting
public class TimeManager
{
    //converting hours, minutes and seconds into total seconds
    public int convertToSeconds(int hours, int minutes, int seconds)
    {
        int totalSeconds = (hours * 3600) + (minutes * 60) + seconds;
        
        return totalSeconds;
    }
    
    //To get hours from total seconds
    public int getHours(int totalSeconds)
    {
        int hours = totalSeconds / 3600;
        
        return hours;
    }
    
    //To get minutes from total seconds
    public int getMinutes(int totalSeconds)
    {
        int minutes = (totalSeconds % 3600) / 60;
        
        return minutes;
    }
    
    //To get seconds from total seconds
    public int getSeconds(int totalSeconds)
    {
        int seconds = totalSeconds % 60;
        
        return seconds;
    }
    
    //Displaying time in HH:MM:SS format
    public String formatTime(int totalSeconds)
    {
        int hours = getHours(totalSeconds);
        int minutes = getMinutes(totalSeconds);
        int seconds = getSeconds(totalSeconds);
        
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
