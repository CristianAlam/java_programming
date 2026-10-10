package app;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** Tutorial section 8: what you can do by the end of the unit, with saved checkboxes. */
final class ChecklistPanel extends JPanel {
    private static final String[] GOALS = {
            "Explain in your own words what a simple program does",
            "Write an algorithm in plain words for a simple task",
            "Turn that algorithm into a program and run it",
            "Describe the structure of a program: class + main",
            "Create objects with new and call their methods using a given interface",
            "Run, debug and step through a program in IntelliJ",
    };

    ChecklistPanel(Store store) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Theme.SURFACE);
        setBorder(new EmptyBorder(22, 28, 22, 28));

        add(left(Theme.label("8. Checklist", Theme.TITLE, Theme.TEXT)));
        add(Box.createVerticalStrut(6));
        add(left(Theme.label("By the end of Unit 1 you can...", Theme.UI, Theme.MUTED)));
        add(Box.createVerticalStrut(14));

        JProgressBar bar = new JProgressBar(0, GOALS.length);
        bar.setForeground(Theme.TIP);
        bar.setStringPainted(true);
        bar.setFont(Theme.UI_SMALL);
        bar.setMaximumSize(new Dimension(520, 22));
        add(left(bar));
        add(Box.createVerticalStrut(14));

        List<JCheckBox> boxes = new ArrayList<>();
        Runnable update = () -> {
            int done = (int) boxes.stream().filter(AbstractButton::isSelected).count();
            bar.setValue(done);
            bar.setString(done + " of " + GOALS.length + " done");
        };
        for (int i = 0; i < GOALS.length; i++) {
            String key = "checklist." + i;
            JCheckBox cb = new JCheckBox(GOALS[i], Boolean.parseBoolean(store.get(key, "false")));
            cb.setFont(Theme.UI);
            cb.setOpaque(false);
            cb.setFocusPainted(false);
            cb.setBorder(new EmptyBorder(6, 2, 6, 2));
            cb.addActionListener(e -> {
                store.put(key, String.valueOf(cb.isSelected()));
                update.run();
            });
            boxes.add(cb);
            add(left(cb));
        }
        update.run();

        add(Box.createVerticalStrut(24));
        add(left(Theme.label("What's next", Theme.UI_BOLD.deriveFont(16f), Theme.TEXT)));
        add(Box.createVerticalStrut(6));
        add(left(Theme.label("Open the Examples tab, run each example, then solve the exercises in the Homework tab.",
                Theme.UI, Theme.MUTED)));
        add(Box.createVerticalGlue());
    }

    private static JComponent left(JComponent c) {
        c.setAlignmentX(LEFT_ALIGNMENT);
        return c;
    }
}
