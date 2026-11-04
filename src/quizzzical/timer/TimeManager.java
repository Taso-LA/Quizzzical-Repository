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
        //check whether the time values are valid
        if (hours < 0 || minutes < 0 || minutes > 59 || seconds < 0 || seconds > 59)
        {
            throw new IllegalArgumentException("Invalid time entered");
        }
        
        int totalSeconds = (hours * 3600) + (minutes * 60) + seconds;
        
        return totalSeconds;
    }
    
    //check whether total seconds are valid
    private void checkTotalSeconds(int totalSeconds)
    {
        if (totalSeconds < 0)
        {
            throw new IllegalArgumentException("Total seconds cannot be negative");
        }
    }
    
    //calculate the remaining time in a quiz
    public int calculateRemainingTime(int totalSeconds, int elapsedSeconds)
    {
        //check that the time values are valid
        checkTotalSeconds(totalSeconds);
        checkTotalSeconds(elapsedSeconds);

        //subtract elapsed time from total time
        int remainingTime = totalSeconds - elapsedSeconds;

        //remaining time must never be negative
        if (remainingTime < 0) 
        {
            remainingTime = 0;
        }

        return remainingTime;
    }
    
    //To get hours from total seconds
    public int getHours(int totalSeconds)
    {
        checkTotalSeconds(totalSeconds);
        
        int hours = totalSeconds / 3600;
        
        return hours;
    }
    
    //To get minutes from total seconds
    public int getMinutes(int totalSeconds)
    {
        checkTotalSeconds(totalSeconds);
        
        int minutes = (totalSeconds % 3600) / 60;
        
        return minutes;
    }
    
    //To get seconds from total seconds
    public int getSeconds(int totalSeconds)
    {
        checkTotalSeconds(totalSeconds);
        
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
