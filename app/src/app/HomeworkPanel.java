package app;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The Homework tab. Each exercise has instructions, and a written-answer box,
 * a code editor, or both. Everything is saved automatically.
 */
final class HomeworkPanel extends JPanel {
    private static final Unit.Exercise[] EXERCISES = Unit.CURRENT.homework.toArray(new Unit.Exercise[0]);

    private final Store store;
    private final Map<String, JTextComponent> written = new LinkedHashMap<>();
    private final Map<String, JTextComponent> code = new LinkedHashMap<>();

    HomeworkPanel(Store store, Frame owner) {
        super(new BorderLayout());
        this.store = store;
        setBackground(Theme.BG);

        CardLayout cards = new CardLayout();
        JPanel content = new JPanel(cards);
        content.setOpaque(false);
        for (Unit.Exercise ex : EXERCISES) content.add(page(ex), ex.id());

        JList<Unit.Exercise> nav = Theme.navList(EXERCISES);
        nav.addListSelectionListener(e -> {
            if (nav.getSelectedValue() != null) cards.show(content, nav.getSelectedValue().id());
        });
        nav.setSelectedIndex(0);

        JPanel left = new JPanel(new BorderLayout());
        left.setBackground(Theme.BG);
        left.setBorder(new EmptyBorder(14, 8, 14, 4));
        JLabel heading = Theme.label("  HOMEWORK", Theme.UI_SMALL.deriveFont(Font.BOLD), Theme.MUTED);
        heading.setBorder(new EmptyBorder(4, 6, 10, 0));
        left.add(heading, BorderLayout.NORTH);
        left.add(nav, BorderLayout.CENTER);
        left.setPreferredSize(new Dimension(270, 100));

        add(topBar(owner), BorderLayout.NORTH);
        add(left, BorderLayout.WEST);
        add(content, BorderLayout.CENTER);
    }

    private JComponent topBar(Frame owner) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Theme.SURFACE);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER), new EmptyBorder(10, 18, 10, 18)));

        JLabel saved = Theme.label("Your work is saved automatically.", Theme.UI_SMALL, Theme.MUTED);

        JButton export = Theme.button("Export for teacher...", true);
        export.addActionListener(e -> exportForTeacher(owner));

        bar.add(saved, BorderLayout.WEST);
        bar.add(export, BorderLayout.EAST);
        return bar;
    }

    private JComponent page(Unit.Exercise ex) {
        String base = "homework/" + ex.id();
        String starterCode = Theme.resource(base + ".code");
        String starterWritten = Theme.resource(base + ".written");
        boolean hasCode = !starterCode.isEmpty();
        boolean hasWritten = !starterWritten.isEmpty();

        JEditorPane instructions = Theme.htmlView("<h1>Exercise " + ex.title() + "</h1>" + Theme.resource(base + ".html"));
        JScrollPane instructionsScroll = Theme.scroll(instructions);

        JComponent writtenPanel = null;
        if (hasWritten) {
            JTextArea area = new JTextArea(store.get(ex.id() + ".written", starterWritten));
            area.setFont(Theme.MONO.deriveFont(14f));
            area.setLineWrap(true);
            area.setWrapStyleWord(true);
            area.setBorder(new EmptyBorder(10, 12, 10, 12));
            area.setTransferHandler(null);
            area.setCaretPosition(0);
            onChange(area, () -> store.put(ex.id() + ".written", area.getText()));
            written.put(ex.id(), area);

            JPanel wp = new JPanel(new BorderLayout(0, 8));
            wp.setOpaque(false);
            wp.add(Theme.label("Your written answer", Theme.UI_BOLD, Theme.TEXT), BorderLayout.NORTH);
            wp.add(Theme.scroll(area), BorderLayout.CENTER);
            writtenPanel = wp;
        }

        JComponent codePanel = null;
        if (hasCode) {
            RunPanel run = new RunPanel(store.get(ex.id() + ".code", starterCode), true, ex.mode(),
                    "Type your code here, then press Run.");
            CodeEditor editor = run.editor();
            onChange(editor, () -> store.put(ex.id() + ".code", editor.getText()));
            JTextArea typed = run.input();
            typed.setText(store.get(ex.id() + ".input", ""));
            onChange(typed, () -> store.put(ex.id() + ".input", typed.getText()));
            code.put(ex.id(), editor);
            codePanel = run;
        }

        JComponent work;
        if (writtenPanel != null && codePanel != null) {
            JSplitPane both = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, writtenPanel, codePanel);
            both.setResizeWeight(0.4);
            both.setBorder(BorderFactory.createEmptyBorder());
            both.setDividerSize(10);
            both.setOpaque(false);
            work = both;
        } else {
            work = writtenPanel != null ? writtenPanel : codePanel;
        }
        work.setBorder(new EmptyBorder(12, 0, 0, 0));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, instructionsScroll, work);
        split.setResizeWeight(0.38);
        split.setBorder(BorderFactory.createEmptyBorder());
        split.setDividerSize(8);
        split.setOpaque(false);

        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(14, 6, 14, 16));
        p.add(split);
        return p;
    }

    private static void onChange(JTextComponent c, Runnable r) {
        c.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { r.run(); }
            public void removeUpdate(DocumentEvent e) { r.run(); }
            public void changedUpdate(DocumentEvent e) { }
        });
    }

    /** Writes all answers into one text file the student can send to the teacher. */
    private void exportForTeacher(Frame owner) {
        LocalDateTime now = LocalDateTime.now();
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save your homework");
        chooser.setSelectedFile(Path.of(System.getProperty("user.home"), "Desktop",
                "Unit" + Unit.CURRENT.number + "_Homework_" + now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".txt").toFile());
        if (chooser.showSaveDialog(owner) != JFileChooser.APPROVE_OPTION) return;

        StringBuilder sb = new StringBuilder();
        sb.append("UNIT ").append(Unit.CURRENT.number).append(" HOMEWORK\n");
        sb.append("Exported: ").append(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).append("\n");
        for (Unit.Exercise ex : EXERCISES) {
            sb.append("\n").append("=".repeat(60)).append("\nExercise ").append(ex.title()).append("\n").append("=".repeat(60)).append('\n');
            if (written.containsKey(ex.id())) {
                sb.append("\n--- Written answer ---\n").append(written.get(ex.id()).getText().strip()).append('\n');
            }
            if (code.containsKey(ex.id())) {
                sb.append(ex.mode() == Unit.Mode.CLASS ? "\n--- Code (whole class) ---\n" : "\n--- Code (inside main) ---\n").append(code.get(ex.id()).getText().strip()).append('\n');
            }
        }
        try {
            Files.writeString(chooser.getSelectedFile().toPath(), sb.toString(), StandardCharsets.UTF_8);
            JOptionPane.showMessageDialog(owner, "Saved! Send this file to your teacher:\n" + chooser.getSelectedFile(),
                    "Homework exported", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(owner, "Could not save the file: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
