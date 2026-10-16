package app;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** The Tutorial tab: a list of sections on the left, the selected section on the right. */
final class TutorialPanel extends JPanel {
    private static final Unit.Section[] SECTIONS = Unit.CURRENT.tutorial.toArray(new Unit.Section[0]);

    TutorialPanel(Store store) {
        super(new BorderLayout());
        setBackground(Theme.BG);

        CardLayout cards = new CardLayout();
        JPanel content = new JPanel(cards);
        List<Component> pages = new ArrayList<>();
        for (Unit.Section s : SECTIONS) {
            Component page = switch (s.page()) {
                case "#bucket" -> new BucketSimulator();
                case "#tracer" -> new TraceSimulator();
                case "#quiz" -> new QuizPanel(s.title().strip());
                case "#checklist" -> new ChecklistPanel(store, s.title().strip());
                default -> Theme.scroll(Theme.htmlView(Theme.resource(s.page())));
            };
            pages.add(page);
            content.add(page, s.title());
        }

        JList<Unit.Section> nav = Theme.navList(SECTIONS);
        nav.addListSelectionListener(e -> {
            Unit.Section s = nav.getSelectedValue();
            if (s == null) return;
            cards.show(content, s.title());
            Component page = pages.get(nav.getSelectedIndex());
            if (page instanceof JScrollPane sp) {
                SwingUtilities.invokeLater(() -> sp.getVerticalScrollBar().setValue(0));
            }
        });
        nav.setSelectedIndex(0);

        JButton prev = Theme.button("←  Previous", false);
        JButton next = Theme.button("Next  →", true);
        prev.addActionListener(e -> nav.setSelectedIndex(Math.max(0, nav.getSelectedIndex() - 1)));
        next.addActionListener(e -> nav.setSelectedIndex(Math.min(SECTIONS.length - 1, nav.getSelectedIndex() + 1)));
        JPanel navButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        navButtons.setOpaque(false);
        navButtons.setBorder(new EmptyBorder(10, 0, 0, 0));
        navButtons.add(prev);
        navButtons.add(next);

        JPanel right = new JPanel(new BorderLayout());
        right.setOpaque(false);
        right.setBorder(new EmptyBorder(14, 6, 14, 16));
        right.add(content, BorderLayout.CENTER);
        right.add(navButtons, BorderLayout.SOUTH);

        JPanel left = new JPanel(new BorderLayout());
        left.setBackground(Theme.BG);
        left.setBorder(new EmptyBorder(14, 8, 14, 4));
        JLabel heading = Theme.label("  " + Unit.CURRENT.heading, Theme.UI_SMALL.deriveFont(Font.BOLD), Theme.MUTED);
        heading.setBorder(new EmptyBorder(4, 6, 10, 0));
        left.add(heading, BorderLayout.NORTH);
        left.add(nav, BorderLayout.CENTER);
        left.setPreferredSize(new Dimension(270, 100));

        add(left, BorderLayout.WEST);
        add(right, BorderLayout.CENTER);
    }
}
