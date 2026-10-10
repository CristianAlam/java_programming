package app;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** The Tutorial tab: a list of sections on the left, the selected section on the right. */
final class TutorialPanel extends JPanel {
    private record Section(String title, String page) {
        @Override
        public String toString() { return title; }
    }

    private static final Section[] SECTIONS = {
            new Section("1. What is Computer Science?", "tutorial/01-cs.html"),
            new Section("2. Algorithms", "tutorial/02-algorithms.html"),
            new Section("3. Programming Languages", "tutorial/03-languages.html"),
            new Section("4. What is a Program?", "tutorial/04-program.html"),
            new Section("5. Class + main", "tutorial/05-structure.html"),
            new Section("6. Objects", "tutorial/06-objects.html"),
            new Section("    Try it: Bucket simulator", "#simulator"),
            new Section("7. Working in IntelliJ", "tutorial/07-intellij.html"),
            new Section("8. Checklist", "#checklist"),
    };

    TutorialPanel(Store store) {
        super(new BorderLayout());
        setBackground(Theme.BG);

        CardLayout cards = new CardLayout();
        JPanel content = new JPanel(cards);
        List<Component> pages = new ArrayList<>();
        for (Section s : SECTIONS) {
            Component page = switch (s.page()) {
                case "#simulator" -> new BucketSimulator();
                case "#checklist" -> new ChecklistPanel(store);
                default -> Theme.scroll(Theme.htmlView(Theme.resource(s.page())));
            };
            pages.add(page);
            content.add(page, s.title());
        }

        JList<Section> nav = Theme.navList(SECTIONS);
        nav.addListSelectionListener(e -> {
            Section s = nav.getSelectedValue();
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
        JLabel heading = Theme.label("  UNIT 1 · INTRODUCTION", Theme.UI_SMALL.deriveFont(Font.BOLD), Theme.MUTED);
        heading.setBorder(new EmptyBorder(4, 6, 10, 0));
        left.add(heading, BorderLayout.NORTH);
        left.add(nav, BorderLayout.CENTER);
        left.setPreferredSize(new Dimension(270, 100));

        add(left, BorderLayout.WEST);
        add(right, BorderLayout.CENTER);
    }
}
