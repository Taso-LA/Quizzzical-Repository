package quizzzical.model;
/**
 *
 * @author tasol
 */

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.BorderFactory;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Font;

public class QuestionReview extends JPanel
{
    //label used to display the question
    private JLabel questionNumberLabel;

    //text area used to display the question
    private JTextArea questionTextArea;

    //panel used to hold answer options
    private JPanel optionsPanel;

    //stores the answer options
    private JRadioButton[] optionButtons;

    //groups radio buttons together
    private ButtonGroup answerGroup;

    //constructor
    public QuestionReview()
    {
        //sets the main layout
        setLayout(new BorderLayout(10, 15));

        //sets the background colour
        setBackground(Color.WHITE);

        //adds spacing around the panel
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        //creates the question number label
        questionNumberLabel = new JLabel("QUESTION 1");

        questionNumberLabel.setFont(
                new Font("Arial", Font.BOLD, 14)
        );

        questionNumberLabel.setForeground(new Color(24, 49, 83));

        //creates the question text area
        questionTextArea = new JTextArea(
                "Your question will appear here."
        );

        questionTextArea.setFont(
                new Font("Arial", Font.PLAIN, 16)
        );

        questionTextArea.setLineWrap(true);
        questionTextArea.setWrapStyleWord(true);

        questionTextArea.setEditable(false);
        questionTextArea.setFocusable(false);

        questionTextArea.setBackground(Color.WHITE);

        //creates the options panel
        optionsPanel = new JPanel(new GridLayout(0, 1, 5, 8));

        optionsPanel.setBackground(Color.WHITE);

        //groups the question information together
        JPanel questionInformationPanel = new JPanel(
                new BorderLayout(5, 10)
        );

        questionInformationPanel.setBackground(Color.WHITE);

        questionInformationPanel.add(
                questionNumberLabel,
                BorderLayout.NORTH
        );

        questionInformationPanel.add(
                questionTextArea,
                BorderLayout.CENTER
        );

        //adds components to the main panel
        add(questionInformationPanel, BorderLayout.NORTH);

        add(optionsPanel, BorderLayout.CENTER);
    }

    //displays a question and its answer options
    public void setQuestion(int questionNumber, String question, String[] options)
    {
        //updates the question number
        questionNumberLabel.setText("QUESTION " + questionNumber);

        //updates the question
        questionTextArea.setText(question);

        //removes previous answer options
        optionsPanel.removeAll();

        //creates a new answer group
        answerGroup = new ButtonGroup();

        //creates radio buttons for the new options
        optionButtons = new JRadioButton[options.length];

        //adds each answer option
        for(int i = 0; i < options.length; i++)
        {
            optionButtons[i] = new JRadioButton(options[i]);

            optionButtons[i].setFont(
                    new Font("Arial", Font.PLAIN, 14)
            );

            optionButtons[i].setBackground(Color.WHITE);

            //groups the radio buttons
            answerGroup.add(optionButtons[i]);

            //adds the option to the panel
            optionsPanel.add(optionButtons[i]);
        }

        //refreshes the panel
        revalidate();
        repaint();
    }

    //gets the selected answer
    public String getSelectedAnswer()
    {
        if(optionButtons == null)
        {
            return null;
        }

        //checks which answer was selected
        for(int i = 0; i < optionButtons.length; i++)
        {
            if(optionButtons[i].isSelected())
            {
                return optionButtons[i].getText();
            }
        }

        //returns null when no answer is selected
        return null;
    }

    //clears the selected answer
    public void clearAnswer()
    {
        if(answerGroup != null)
        {
            answerGroup.clearSelection();
        }
    }
}
