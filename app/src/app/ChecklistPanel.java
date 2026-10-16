package app;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** The last tutorial section: what you can do by the end of the unit, with saved checkboxes. */
final class ChecklistPanel extends JPanel {
    private static final String[] GOALS = Unit.CURRENT.checklist.toArray(new String[0]);

    ChecklistPanel(Store store, String title) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Theme.SURFACE);
        setBorder(new EmptyBorder(22, 28, 22, 28));

        add(left(Theme.label(title, Theme.TITLE, Theme.TEXT)));
        add(Box.createVerticalStrut(6));
        add(left(Theme.label("By the end of Unit " + Unit.CURRENT.number + " you can...", Theme.UI, Theme.MUTED)));
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
        add(left(Theme.label(Unit.CURRENT.checklistNext, Theme.UI, Theme.MUTED)));
        add(Box.createVerticalGlue());
    }

    private static JComponent left(JComponent c) {
        c.setAlignmentX(LEFT_ALIGNMENT);
        return c;
    }
}
