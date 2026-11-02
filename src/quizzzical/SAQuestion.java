/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package quizzzical;

/**
 *
 * @author user
 */
public class SAQuestion extends Question {
    public SAQuestion(String text, String correct, int points){
        super(text, correct, points);
    }

    @Override
    public boolean checkAnswer(String userAnswer) {
        if(userAnswer == null){
            return false;
        }
        return correctAnswer.trim().equalsIgnoreCase(userAnswer.trim());
    }

    @Override
    public String getQuestionType() {
        return "SHOT_ANSWER";
    }
}
