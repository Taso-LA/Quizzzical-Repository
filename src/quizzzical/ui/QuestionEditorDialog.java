package quizzzical.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import quizzzical.model.Question;
import quizzzical.model.QuestionType;

public class QuestionEditorDialog extends JDialog
{
    private final JComboBox<QuestionType> typeBox = new JComboBox<>(QuestionType.values());
    private final JTextArea promptArea = new JTextArea(3, 35);
    private final JTextArea hintArea = new JTextArea(2, 35);
    private final JTextArea optionsArea = new JTextArea(4, 35);
    private final JTextArea pairsArea = new JTextArea(4, 35);
    private final JTextField correctField = new JTextField(28);
    private final JSpinner pointsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
    private final JLabel instructions = new JLabel(" ");
    private Question result;
    private final Question existing;

    private QuestionEditorDialog(JFrame owner, Question existing)
    {
        super(owner, existing == null ? "Add question" : "Edit question", true);
        this.existing = existing;
        setLayout(new BorderLayout(8, 8));
        JPanel fields = new JPanel(new GridLayout(0, 1, 5, 5));
        fields.setBorder(BorderFactory.createEmptyBorder(12, 14, 5, 14));
        fields.add(new JLabel("Question type"));
        fields.add(typeBox);
        fields.add(new JLabel("Question text"));
        fields.add(new JScrollPane(promptArea));
        fields.add(new JLabel("Optional hint shown to students"));
        fields.add(new JScrollPane(hintArea));
        fields.add(new JLabel("Answer options (one per line; used for multiple choice and tick-all)"));
        fields.add(new JScrollPane(optionsArea));
        fields.add(new JLabel("Matching pairs (one pair per line, written as Left = Right)"));
        fields.add(new JScrollPane(pairsArea));
        fields.add(new JLabel("Correct answer (use semicolons between tick-all answers)"));
        fields.add(correctField);
        JPanel bottom = new JPanel(new BorderLayout(5, 5));
        bottom.setBorder(BorderFactory.createEmptyBorder(0, 14, 12, 14));
        bottom.add(instructions, BorderLayout.CENTER);
        JPanel pointsPanel = new JPanel();
        pointsPanel.add(new JLabel("Points:"));
        pointsPanel.add(pointsSpinner);
        bottom.add(pointsPanel, BorderLayout.NORTH);
        JPanel buttons = new JPanel();
        javax.swing.JButton saveButton = new javax.swing.JButton("Save question");
        javax.swing.JButton cancelButton = new javax.swing.JButton("Cancel");
        buttons.add(saveButton);
        buttons.add(cancelButton);
        bottom.add(buttons, BorderLayout.SOUTH);
        add(fields, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        typeBox.addActionListener(event -> updateInstructions());
        saveButton.addActionListener(event -> saveQuestion());
        cancelButton.addActionListener(event -> dispose());
        if(existing != null) loadExisting();
        updateInstructions();
        setPreferredSize(new Dimension(620, 690));
        pack();
        setLocationRelativeTo(owner);
    }

    public static Question showDialog(JFrame owner, Question existing)
    {
        QuestionEditorDialog dialog = new QuestionEditorDialog(owner, existing);
        dialog.setVisible(true);
        return dialog.result;
    }

    private void loadExisting()
    {
        typeBox.setSelectedItem(existing.getType());
        promptArea.setText(existing.getPrompt());
        hintArea.setText(existing.getHint());
        pointsSpinner.setValue(existing.getPoints());
        optionsArea.setText(String.join("\n", existing.getOptions()));
        StringBuilder pairs = new StringBuilder();
        for(Map.Entry<String, String> pair : existing.getMatchingPairs().entrySet())
        {
            if(pairs.length() > 0) pairs.append("\n");
            pairs.append(pair.getKey()).append(" = ").append(pair.getValue());
        }
        pairsArea.setText(pairs.toString());
        correctField.setText(existing.getCorrectAnswer());
    }

    private void updateInstructions()
    {
        QuestionType type = (QuestionType) typeBox.getSelectedItem();
        if(type == QuestionType.SINGLE_CHOICE) instructions.setText("Enter one correct option exactly as it appears in the options list.");
        else if(type == QuestionType.TRUE_FALSE) instructions.setText("Correct answer must be True or False.");
        else if(type == QuestionType.FILL_IN) instructions.setText("Enter the expected answer. Capital letters are ignored when marking.");
        else if(type == QuestionType.MULTI_SELECT) instructions.setText("Enter every correct option in the correct answer box, separated by semicolons.");
        else instructions.setText("Enter matching pairs as Left = Right, one pair per line.");
    }

    private void saveQuestion()
    {
        try
        {
            QuestionType type = (QuestionType) typeBox.getSelectedItem();
            String prompt = promptArea.getText().trim();
            int points = (Integer) pointsSpinner.getValue();
            if(prompt.isEmpty()) throw new IllegalArgumentException("Enter the question text.");
            List<String> options = new ArrayList<>();
            for(String line : optionsArea.getText().split("\\R")) if(!line.trim().isEmpty()) options.add(line.trim());
            Map<String, String> pairs = new LinkedHashMap<>();
            for(String line : pairsArea.getText().split("\\R"))
            {
                if(line.trim().isEmpty()) continue;
                String[] split = line.split("=", 2);
                if(split.length != 2 || split[0].trim().isEmpty() || split[1].trim().isEmpty())
                    throw new IllegalArgumentException("Each matching pair must look like Left = Right.");
                pairs.put(split[0].trim(), split[1].trim());
            }
            String correct = correctField.getText().trim();

            if(type == QuestionType.TRUE_FALSE)
            {
                options.clear();
                options.add("True");
                options.add("False");
                if(!correct.equalsIgnoreCase("true") && !correct.equalsIgnoreCase("false"))
                    throw new IllegalArgumentException("For true/false, enter True or False as the correct answer.");
                correct = correct.substring(0, 1).toUpperCase() + correct.substring(1).toLowerCase();
            }
            else if(type == QuestionType.SINGLE_CHOICE || type == QuestionType.MULTI_SELECT)
            {
                if(options.size() < 2) throw new IllegalArgumentException("Enter at least two answer options, one per line.");
                if(type == QuestionType.SINGLE_CHOICE && !options.contains(correct))
                    throw new IllegalArgumentException("The correct answer must exactly match one of the options.");
                if(type == QuestionType.MULTI_SELECT)
                {
                    if(correct.isEmpty()) throw new IllegalArgumentException("Enter the correct answers separated by semicolons.");
                    for(String answer : correct.split(";"))
                        if(!options.contains(answer.trim())) throw new IllegalArgumentException("Every correct tick answer must match an option exactly.");
                }
            }
            else if(type == QuestionType.FILL_IN)
            {
                if(correct.isEmpty()) throw new IllegalArgumentException("Enter the expected answer.");
            }
            else if(type == QuestionType.MATCHING)
            {
                if(pairs.size() < 2) throw new IllegalArgumentException("Enter at least two matching pairs.");
                correct = "";
                for(Map.Entry<String, String> pair : pairs.entrySet())
                {
                    if(!correct.isEmpty()) correct += ";";
                    correct += pair.getKey() + "=" + pair.getValue();
                }
            }

            if(existing == null)
                result = new Question(type, prompt, points, options, pairs, correct, hintArea.getText().trim());
            else
            {
                existing.update(type, prompt, points, options, pairs, correct, hintArea.getText().trim());
                result = existing;
            }
            dispose();
        }
        catch(IllegalArgumentException exception)
        {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Check the question", JOptionPane.WARNING_MESSAGE);
        }
    }
}
