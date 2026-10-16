package app;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** The Examples tab: each example's code, ready to run. Students may change it and run again. */
final class ExamplesPanel extends JPanel {
    private static final Unit.Example[] EXAMPLES = Unit.CURRENT.examples.toArray(new Unit.Example[0]);

    ExamplesPanel() {
        super(new BorderLayout());
        setBackground(Theme.BG);

        CardLayout cards = new CardLayout();
        JPanel content = new JPanel(cards);
        content.setOpaque(false);
        for (Unit.Example ex : EXAMPLES) content.add(page(ex), ex.title());

        JList<Unit.Example> nav = Theme.navList(EXAMPLES);
        nav.addListSelectionListener(e -> {
            if (nav.getSelectedValue() != null) cards.show(content, nav.getSelectedValue().title());
        });
        nav.setSelectedIndex(0);

        JPanel left = new JPanel(new BorderLayout());
        left.setBackground(Theme.BG);
        left.setBorder(new EmptyBorder(14, 8, 14, 4));
        JLabel heading = Theme.label("  EXAMPLES", Theme.UI_SMALL.deriveFont(Font.BOLD), Theme.MUTED);
        heading.setBorder(new EmptyBorder(4, 6, 10, 0));
        left.add(heading, BorderLayout.NORTH);
        left.add(nav, BorderLayout.CENTER);
        left.setPreferredSize(new Dimension(270, 100));

        add(left, BorderLayout.WEST);
        add(content, BorderLayout.CENTER);
    }

    private static JComponent page(Unit.Example ex) {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(Theme.SURFACE);
        p.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(14, 6, 14, 16),
                BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Theme.BORDER), new EmptyBorder(18, 22, 18, 22))));

        JPanel head = new JPanel();
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        head.setOpaque(false);
        head.add(Theme.label("Example " + ex.title(), Theme.TITLE, Theme.TEXT));
        head.add(Box.createVerticalStrut(6));
        head.add(Theme.label(ex.description(), Theme.UI, Theme.MUTED));

        RunPanel run = new RunPanel(Theme.resource("examples/" + ex.file() + ".txt"), true, ex.mode(),
                "Run it, compare the output with the code, then try changing something.");
        run.input().setText(Theme.resource("examples/" + ex.file() + ".input"));   // sample input, if the example reads any
        p.add(head, BorderLayout.NORTH);
        p.add(run, BorderLayout.CENTER);
        return p;
    }
}
