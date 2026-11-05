package quizzzical;

public class MultipleChoiceQuestion extends Question {
    private String[] options;

    public MultipleChoiceQuestion(String text, String[] options, String correct, int points) {
        super(text, correct, points);
        this.options = options.clone();
    }

    public String[] getOptions() {
        return options.clone();
    }

    @Override
    public boolean checkAnswer(String userAnswer) {
        if (userAnswer == null) {
            return false;
        }
        return correctAnswer.trim().equalsIgnoreCase(userAnswer.trim());
    }

    @Override
    public String getQuestionType() {
        return "MULTIPLE";
    }
}