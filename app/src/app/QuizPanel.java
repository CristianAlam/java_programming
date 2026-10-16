package app;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** "What's the value?" cards from unit.properties (quiz.N = question | answer). Click a card to show its answer. */
final class QuizPanel extends JPanel {
    QuizPanel(String title) {
        super(new BorderLayout(0, 16));
        setBackground(Theme.SURFACE);
        setBorder(new EmptyBorder(22, 28, 22, 28));

        JPanel head = new JPanel();
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        head.setOpaque(false);
        head.add(Theme.label(title, Theme.TITLE, Theme.TEXT));
        head.add(Box.createVerticalStrut(6));
        head.add(Theme.label("Work out the value in your head first. Then click the card to check.", Theme.UI, Theme.MUTED));

        JPanel grid = new JPanel(new GridLayout(0, 4, 10, 10));
        grid.setOpaque(false);
        for (Unit.QuizItem q : Unit.CURRENT.quiz) grid.add(card(q));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(grid, BorderLayout.NORTH);

        add(head, BorderLayout.NORTH);
        add(top, BorderLayout.CENTER);
    }

    private static JButton card(Unit.QuizItem q) {
        JButton b = Theme.button("", false);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setBorder(new EmptyBorder(12, 14, 12, 14));
        boolean[] shown = {false};
        Runnable update = () -> b.setText("<html><span style='font-family:Consolas,monospace;font-size:15pt'>" + escape(q.question())
                + "</span><br><span style='color:" + (shown[0] ? "#0f9d7a'><b>= " + escape(q.answer()) + "</b>" : "#5d6478'>?")
                + "</span></html>");
        update.run();
        b.addActionListener(e -> {
            shown[0] = !shown[0];
            update.run();
        });
        return b;
    }

    private static String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
