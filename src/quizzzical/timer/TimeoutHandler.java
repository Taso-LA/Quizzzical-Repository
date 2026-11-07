package quizzzical.timer;

public class TimeoutHandler
{
    private final Runnable action;

    public TimeoutHandler(Runnable action)
    {
        this.action = action;
    }

    public void handleTimeout()
    {
        if(action != null) action.run();
    }
}
