package quizzzical.timer;
/**
 *
 * @author tasol
 */

public class TimeoutHandler
{
    //handle what happens when quiz time expires
    public void handleTimeout()
    {
        System.out.println("Time has expired!");
        
        //automatic submit the quiz
        submitQuiz();
    }
    
    //submit the quiz
    private void submitQuiz()
    {
        System.out.println("Submitting Quiz automatically...");
        System.out.println("Quiz submitted successfully!");
    }
    
}