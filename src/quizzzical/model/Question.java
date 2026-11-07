package quizzzical.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Question implements Serializable
{
    private static final long serialVersionUID = 1L;

    private final String id;
    private QuestionType type;
    private String prompt;
    private int points;
    private List<String> options;
    private Map<String, String> matchingPairs;
    private String correctAnswer;
    private String hint;

    public Question(QuestionType type, String prompt, int points,
                    List<String> options, Map<String, String> matchingPairs,
                    String correctAnswer)
    {
        this(type, prompt, points, options, matchingPairs, correctAnswer, "");
    }

    public Question(QuestionType type, String prompt, int points,
                    List<String> options, Map<String, String> matchingPairs,
                    String correctAnswer, String hint)
    {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.prompt = prompt;
        this.points = points;
        this.options = new ArrayList<>(options);
        this.matchingPairs = new LinkedHashMap<>(matchingPairs);
        this.correctAnswer = correctAnswer;
        this.hint = hint;
    }

    public String getId() { return id; }
    public QuestionType getType() { return type; }
    public String getPrompt() { return prompt; }
    public int getPoints() { return points; }
    public List<String> getOptions() { return new ArrayList<>(options); }
    public Map<String, String> getMatchingPairs() { return new LinkedHashMap<>(matchingPairs); }
    public String getCorrectAnswer() { return correctAnswer; }
    public String getHint() { return hint == null ? "" : hint; }

    public void update(QuestionType type, String prompt, int points,
                       List<String> options, Map<String, String> matchingPairs,
                       String correctAnswer)
    {
        update(type, prompt, points, options, matchingPairs, correctAnswer, getHint());
    }

    public void update(QuestionType type, String prompt, int points,
                       List<String> options, Map<String, String> matchingPairs,
                       String correctAnswer, String hint)
    {
        this.type = type;
        this.prompt = prompt;
        this.points = points;
        this.options = new ArrayList<>(options);
        this.matchingPairs = new LinkedHashMap<>(matchingPairs);
        this.correctAnswer = correctAnswer;
        this.hint = hint;
    }

    public boolean isCorrect(String answer)
    {
        if(answer == null || answer.trim().isEmpty()) return false;
        if(type == QuestionType.MULTI_SELECT) return normalizeSet(answer).equals(normalizeSet(correctAnswer));
        if(type == QuestionType.MATCHING) return normalizePairs(answer).equals(normalizePairs(correctAnswer));
        return normalize(answer).equals(normalize(correctAnswer));
    }

    public String getCorrectAnswerDisplay()
    {
        if(type == QuestionType.MATCHING)
        {
            StringBuilder text = new StringBuilder();
            for(Map.Entry<String, String> pair : matchingPairs.entrySet())
            {
                if(text.length() > 0) text.append("; ");
                text.append(pair.getKey()).append(" = ").append(pair.getValue());
            }
            return text.toString();
        }
        return correctAnswer;
    }

    private String normalize(String value)
    {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }

    private Set<String> normalizeSet(String value)
    {
        Set<String> values = new LinkedHashSet<>();
        if(value != null) for(String item : value.split(";")) if(!item.trim().isEmpty()) values.add(normalize(item));
        return values;
    }

    private Map<String, String> normalizePairs(String value)
    {
        Map<String, String> pairs = new LinkedHashMap<>();
        if(value != null)
        {
            for(String item : value.split(";"))
            {
                String[] parts = item.split("=", 2);
                if(parts.length == 2 && !parts[0].trim().isEmpty()) pairs.put(normalize(parts[0]), normalize(parts[1]));
            }
        }
        return pairs;
    }
}