package quizzzical.model;

public enum QuestionType
{
    SINGLE_CHOICE("Multiple choice - choose one"),
    TRUE_FALSE("True or false"),
    FILL_IN("Fill in the answer"),
    MULTI_SELECT("Tick all correct answers"),
    MATCHING("Match the columns");

    private final String displayName;

    QuestionType(String displayName)
    {
        this.displayName = displayName;
    }

    @Override
    public String toString()
    {
        return displayName;
    }
}
