/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/*ABOUT
This class is for the True or False skeleton or template hence  "TFQuestion"
this class will be an extend from the "Question.java" class.
CM VILAKAZI AKA-P4TR0N
*/
package quizzzical;
/**
 *
 * @author CM Vilakazi 260996297@mycput.ac.za
 */
public class TFQuestion extends Question {
    public TFQuestion(String text, String correct, int points){
        /*since this is a child class we inherite these variables in the 
        arguments eg text, correct, points from the parent class --"Questio.java"
        public Question(String questionText, String correctAnswer, int points)
        */
        super(text, correct, points);
    }

    @Override
    public boolean checkAnswer(String userAnswer) {
        String A = userAnswer.trim();
         return correctAnswer.equalsIgnoreCase(A);
         /*this function "trim()" will remove spaces if user mistakenly enter a space
         button, A will be a formatted variable with no space.
         
         and the other one will compare the student answer with the
         oe in the memo ignoring if theres uppercase or note
         */
    }

    @Override
    public String getQuestionType() {
         return "TRUE_FALSE";
         
         /*gents note that "TRUE_FALSE" we will use it as a reference to identify this
         type of a question for an if statement
         */
    }
    
}
