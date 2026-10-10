package app;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** The Examples tab: each example's code, ready to run. Students may change it and run again. */
final class ExamplesPanel extends JPanel {
    private record Example(String title, String file, String description) {
        @Override
        public String toString() { return title; }
    }

    private static final Example[] EXAMPLES = {
            new Example("1. println vs print", "ex1", "println moves to a new line after printing; print stays on the same line."),
            new Example("2. Order matters", "ex2", "Commands run from top to bottom, one after another."),
            new Example("3. A text picture", "ex3", "Drawing with characters. Notice \\\\ prints a single backslash."),
            new Example("4. Special characters", "ex4", "\\\" prints a quote, \\n starts a new line, \\t inserts a tab."),
            new Example("5. First objects", "ex5", "Create a bucket with new, then call its methods with a dot."),
            new Example("6. Two separate objects", "ex6", "Two objects of the same class each have their own state."),
            new Example("7. Pouring between buckets", "ex7", "Objects can work together: big pours into small."),
            new Example("8. Asking an object a question", "ex8", "Some methods return a value that you can print."),
            new Example("9. From algorithm to program", "ex9", "Turning a plain-words algorithm into code, step by step."),
    };

    ExamplesPanel() {
        super(new BorderLayout());
        setBackground(Theme.BG);

        CardLayout cards = new CardLayout();
        JPanel content = new JPanel(cards);
        content.setOpaque(false);
        for (Example ex : EXAMPLES) content.add(page(ex), ex.title());

        JList<Example> nav = Theme.navList(EXAMPLES);
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

    private static JComponent page(Example ex) {
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

        RunPanel run = new RunPanel(Theme.resource("examples/" + ex.file() + ".txt"), true,
                "Run it, compare the output with the code, then try changing something.");
        p.add(head, BorderLayout.NORTH);
        p.add(run, BorderLayout.CENTER);
        return p;
    }
}
