package app;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/** Unit 1 desktop app: Tutorial, Examples and Homework in one window. */
public final class Main {
    static final String TITLE = "Unit 1 - Introduction to Computer Science";

    private Main() {}

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "lcd");
        System.setProperty("swing.aatext", "true");
        SwingUtilities.invokeLater(Main::createWindow);
    }

    private static void createWindow() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        Theme.installDefaults();
        Store store = Store.load();

        JFrame frame = new JFrame(TITLE);
        frame.setIconImage(Theme.appIcon());
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("   Tutorial   ", new TutorialPanel(store));
        tabs.addTab("   Examples   ", new ExamplesPanel());
        tabs.addTab("   Homework   ", new HomeworkPanel(store, frame));
        tabs.setBackground(Theme.BG);

        JLabel saved = Theme.label("Unit 1 · Computer Science in Java", Theme.UI_SMALL, Theme.MUTED);
        JLabel guard = Theme.label(" ", Theme.UI_SMALL, Theme.MUTED);
        JPanel status = new JPanel(new BorderLayout());
        status.setBackground(Theme.SURFACE);
        status.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER), new EmptyBorder(4, 14, 4, 14)));
        status.add(saved, BorderLayout.WEST);
        status.add(guard, BorderLayout.EAST);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG);
        root.setBorder(new EmptyBorder(8, 8, 0, 8));
        root.add(tabs, BorderLayout.CENTER);
        frame.add(root, BorderLayout.CENTER);
        frame.add(status, BorderLayout.SOUTH);

        Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        frame.setSize(Math.min(1280, screen.width), Math.min(840, screen.height));
        frame.setMinimumSize(new Dimension(900, 600));
        frame.setLocationRelativeTo(null);

        frame.addWindowListener(new WindowAdapter() {
            private boolean protectedOn;

            private void protect() {
                if (protectedOn) return;
                protectedOn = ScreenGuard.protect(TITLE);
                guard.setText(protectedOn ? "Screen capture blocked" : "Screen capture not blocked");
                guard.setForeground(protectedOn ? Theme.TIP : Theme.MUTED);
            }

            @Override
            public void windowOpened(WindowEvent e) { protect(); }

            @Override
            public void windowActivated(WindowEvent e) { protect(); }

            @Override
            public void windowClosing(WindowEvent e) {
                store.saveNow();
                frame.dispose();
                System.exit(0);
            }
        });
        frame.setVisible(true);
    }
}
