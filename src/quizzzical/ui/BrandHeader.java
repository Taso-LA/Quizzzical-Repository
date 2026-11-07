package quizzzical.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class BrandHeader extends JPanel
{
    public BrandHeader(String subtitle)
    {
        setLayout(new BorderLayout(12, 4));
        setBackground(new Color(35, 27, 70));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 22, 14, 22));

        JPanel brand = new JPanel(new BorderLayout(10, 0));
        brand.setOpaque(false);
        JLabel q = new JLabel("Q", SwingConstants.CENTER)
        {
            @Override
            protected void paintComponent(Graphics graphics)
            {
                Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(new Color(187, 164, 255));
                g.fillOval(2, 3, 48, 48);
                g.setColor(new Color(35, 27, 70));
                g.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 45));
                g.drawString("Q", 6, 42);
                g.setColor(new Color(255, 210, 120));
                g.setStroke(new java.awt.BasicStroke(4f, java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND));
                g.drawLine(33, 39, 48, 53);
                g.dispose();
            }
        };
        q.setPreferredSize(new java.awt.Dimension(54, 54));
        JLabel name = new JLabel("uizzzical");
        name.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 36));
        name.setForeground(Color.WHITE);
        brand.add(q, BorderLayout.WEST);
        brand.add(name, BorderLayout.CENTER);

        JLabel message = new JLabel(subtitle);
        message.setFont(new Font("SansSerif", Font.PLAIN, 13));
        message.setForeground(new Color(220, 214, 245));
        add(brand, BorderLayout.WEST);
        add(message, BorderLayout.SOUTH);
    }
}
