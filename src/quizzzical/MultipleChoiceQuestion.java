/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package quizzzical;

public class MultipleChoiceQuestion extends Question {
    private String[] options;

    public MultipleChoiceQuestion(String text, String[] options, String correct, int points) {
        super(text, correct, points);
        this.options = options;
    }
    public String[] getOptions() { return options; }

    @Override
    public boolean checkAnswer(String userAnswer) {
        return correctAnswer.equalsIgnoreCase(userAnswer.trim());
    }
    @Override
    public String getQuestionType() { return "MULTIPLE"; }
}
