package app;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.util.function.Supplier;

/**
 * A code editor with the fixed "class + main" frame around it,
 * a Run button, and an output area underneath.
 */
final class RunPanel extends JPanel {
    private final JTextPane output = new JTextPane();
    private final JButton runButton = Theme.button("Run", true);
    private final CodeEditor editor;

    RunPanel(String code, boolean editable, String hint) {
        super(new BorderLayout());
        setOpaque(false);
        editor = new CodeEditor(code, editable);
        runButton.setIcon(Theme.playIcon());
        runButton.setIconTextGap(8);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(0, 0, 8, 0));
        if (hint != null) top.add(Theme.label(hint, Theme.UI_SMALL, Theme.MUTED), BorderLayout.WEST);
        top.add(runButton, BorderLayout.EAST);

        JPanel codeBox = new JPanel(new BorderLayout());
        codeBox.setBackground(Theme.CODE_BG);
        codeBox.add(frameLabel("public class Main {<br>&nbsp;&nbsp;&nbsp;&nbsp;public static void main(String[] args) {"), BorderLayout.NORTH);
        codeBox.add(editor.inScrollPane(), BorderLayout.CENTER);
        codeBox.add(frameLabel("&nbsp;&nbsp;&nbsp;&nbsp;}<br>}"), BorderLayout.SOUTH);
        codeBox.setBorder(BorderFactory.createLineBorder(Theme.CODE_GUTTER, 1));

        output.setEditable(false);
        output.setFont(Theme.MONO);
        output.setBackground(new Color(0x14161f));
        output.setForeground(Theme.CODE_TEXT);
        output.setBorder(new EmptyBorder(10, 12, 10, 12));
        output.setTransferHandler(null);
        output.setHighlighter(null);
        JScrollPane outScroll = new JScrollPane(output);
        outScroll.setBorder(BorderFactory.createEmptyBorder());

        JPanel outBox = new JPanel(new BorderLayout());
        outBox.setBackground(new Color(0x14161f));
        JLabel outTitle = Theme.label("  OUTPUT", Theme.UI_SMALL.deriveFont(Font.BOLD), Theme.CODE_CM);
        outTitle.setBorder(new EmptyBorder(6, 4, 0, 0));
        outBox.add(outTitle, BorderLayout.NORTH);
        outBox.add(outScroll, BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, codeBox, outBox);
        split.setResizeWeight(0.65);
        split.setBorder(BorderFactory.createEmptyBorder());
        split.setDividerSize(6);

        add(top, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);

        showText("Press Run to run the program.", Theme.CODE_CM);
        runButton.addActionListener(e -> run(editor::getText));
    }

    CodeEditor editor() {
        return editor;
    }

    private static JLabel frameLabel(String html) {
        JLabel l = new JLabel("<html><span style='color:#7f8aa8'>" + html + "</span></html>");
        l.setFont(Theme.MONO);
        l.setOpaque(false);
        l.setBorder(new EmptyBorder(6, 14, 6, 10));
        l.setToolTipText("This frame is written for you. Type your code between these lines.");
        return l;
    }

    private void run(Supplier<String> code) {
        runButton.setEnabled(false);
        showText("Running...", Theme.CODE_CM);
        String body = code.get();
        new SwingWorker<CodeRunner.Result, Void>() {
            @Override
            protected CodeRunner.Result doInBackground() {
                return CodeRunner.run(body);
            }

            @Override
            protected void done() {
                runButton.setEnabled(true);
                try {
                    CodeRunner.Result r = get();
                    if (!r.compiled()) {
                        showText(r.output(), Theme.CODE_ERR);
                    } else {
                        String text = r.output().isEmpty() ? "(the program printed nothing)" : r.output();
                        showText(text, r.output().isEmpty() ? Theme.CODE_CM : Theme.CODE_TEXT);
                        if (r.timedOut()) appendText("\n\nStopped: the program ran for more than 5 seconds.", Theme.CODE_ERR);
                    }
                } catch (Exception ex) {
                    showText("Something went wrong: " + ex.getMessage(), Theme.CODE_ERR);
                }
            }
        }.execute();
    }

    private void showText(String text, Color color) {
        output.setText("");
        appendText(text, color);
    }

    private void appendText(String text, Color color) {
        StyledDocument doc = output.getStyledDocument();
        SimpleAttributeSet s = new SimpleAttributeSet();
        StyleConstants.setForeground(s, color);
        try {
            doc.insertString(doc.getLength(), text, s);
        } catch (Exception ignored) {
        }
        output.setCaretPosition(0);
    }
}
