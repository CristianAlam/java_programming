package app;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Set;

/**
 * A small Java code editor: syntax colors, line numbers, Tab = 4 spaces,
 * auto-indent, Ctrl+/ to comment lines, Ctrl+Z / Ctrl+Y undo and redo.
 * Copy, cut and paste are switched off so code can't be moved in or out of the app.
 */
final class CodeEditor extends JTextPane {
    private static final Set<String> KEYWORDS = Set.of(
            "public", "private", "protected", "class", "static", "void", "new", "int", "double",
            "boolean", "char", "if", "else", "for", "while", "do", "return", "true", "false",
            "null", "final", "this", "import", "package", "break", "continue");

    private final SimpleAttributeSet plain = style(Theme.CODE_TEXT, false);
    private final SimpleAttributeSet keyword = style(Theme.CODE_KW, false);
    private final SimpleAttributeSet string = style(Theme.CODE_STR, false);
    private final SimpleAttributeSet comment = style(Theme.CODE_CM, true);
    private final SimpleAttributeSet type = style(Theme.CODE_TY, false);
    private final SimpleAttributeSet number = style(Theme.CODE_NUM, false);
    private boolean highlightQueued;

    CodeEditor(String text, boolean editable) {
        setFont(Theme.MONO);
        setBackground(Theme.CODE_BG);
        setForeground(Theme.CODE_TEXT);
        setCaretColor(Color.WHITE);
        setSelectionColor(new Color(0x3a4466));
        setSelectedTextColor(Theme.CODE_TEXT);
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setEditable(editable);
        setTransferHandler(null);
        setDragEnabled(false);
        setText(text);
        highlight();
        setCaretPosition(0);

        getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { queueHighlight(); }
            public void removeUpdate(DocumentEvent e) { queueHighlight(); }
            public void changedUpdate(DocumentEvent e) { }
        });
        if (editable) installKeys();
    }

    /** Wraps the editor in a scroll pane with line numbers. */
    JScrollPane inScrollPane() {
        JScrollPane sp = new JScrollPane(this);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setRowHeaderView(new LineNumbers(this));
        sp.getViewport().setBackground(Theme.CODE_BG);
        sp.getVerticalScrollBar().setUnitIncrement(18);
        return sp;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        Container parent = getParent();
        return parent == null || getUI().getPreferredSize(this).width <= parent.getSize().width;
    }

    private static SimpleAttributeSet style(Color c, boolean italic) {
        SimpleAttributeSet s = new SimpleAttributeSet();
        StyleConstants.setForeground(s, c);
        StyleConstants.setItalic(s, italic);
        return s;
    }

    private void queueHighlight() {
        if (highlightQueued) return;
        highlightQueued = true;
        SwingUtilities.invokeLater(() -> {
            highlightQueued = false;
            highlight();
        });
    }

    /** Re-colors the whole document with a tiny Java tokenizer. */
    private void highlight() {
        StyledDocument doc = getStyledDocument();
        String s;
        try {
            s = doc.getText(0, doc.getLength());
        } catch (BadLocationException e) {
            return;
        }
        doc.setCharacterAttributes(0, s.length(), plain, true);
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (c == '/' && i + 1 < s.length() && s.charAt(i + 1) == '/') {
                int end = s.indexOf('\n', i);
                if (end < 0) end = s.length();
                doc.setCharacterAttributes(i, end - i, comment, true);
                i = end;
            } else if (c == '"' || c == '\'') {
                int j = i + 1;
                while (j < s.length() && s.charAt(j) != c && s.charAt(j) != '\n') {
                    if (s.charAt(j) == '\\') j++;
                    j++;
                }
                j = Math.min(j + 1, s.length());
                doc.setCharacterAttributes(i, j - i, string, true);
                i = j;
            } else if (Character.isDigit(c)) {
                int j = i;
                while (j < s.length() && (Character.isLetterOrDigit(s.charAt(j)) || s.charAt(j) == '.')) j++;
                doc.setCharacterAttributes(i, j - i, number, true);
                i = j;
            } else if (Character.isJavaIdentifierStart(c)) {
                int j = i;
                while (j < s.length() && Character.isJavaIdentifierPart(s.charAt(j))) j++;
                String word = s.substring(i, j);
                if (KEYWORDS.contains(word)) doc.setCharacterAttributes(i, j - i, keyword, true);
                else if (Character.isUpperCase(c)) doc.setCharacterAttributes(i, j - i, type, true);
                i = j;
            } else {
                i++;
            }
        }
    }

    private void installKeys() {
        UndoManager undo = new UndoManager();
        getDocument().addUndoableEditListener(e -> {
            // Ignore color changes made by the highlighter; only undo real typing.
            if (e.getEdit() instanceof AbstractDocument.DefaultDocumentEvent de
                    && de.getType() == DocumentEvent.EventType.CHANGE) return;
            undo.addEdit(e.getEdit());
        });

        InputMap im = getInputMap();
        ActionMap am = getActionMap();
        int ctrl = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ctrl), "editor-undo");
        am.put("editor-undo", action(() -> { if (undo.canUndo()) undo.undo(); }));
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ctrl), "editor-redo");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ctrl | InputEvent.SHIFT_DOWN_MASK), "editor-redo");
        am.put("editor-redo", action(() -> { if (undo.canRedo()) undo.redo(); }));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), "editor-tab");
        am.put("editor-tab", action(() -> replaceSelection("    ")));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "editor-enter");
        am.put("editor-enter", action(this::newLineWithIndent));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, ctrl), "editor-comment");
        am.put("editor-comment", action(this::toggleComment));
    }

    private static Action action(Runnable r) {
        return new AbstractAction() {
            public void actionPerformed(ActionEvent e) { r.run(); }
        };
    }

    private void newLineWithIndent() {
        try {
            Document doc = getDocument();
            int pos = getCaretPosition();
            String before = doc.getText(0, pos);
            String line = before.substring(before.lastIndexOf('\n') + 1);
            String indent = line.substring(0, line.length() - line.stripLeading().length());
            if (line.strip().endsWith("{")) indent += "    ";
            replaceSelection("\n" + indent);
        } catch (BadLocationException ignored) {
        }
    }

    /** Ctrl+/ : adds "// " to the selected lines, or removes it if they all have it. */
    private void toggleComment() {
        try {
            Document doc = getDocument();
            String text = doc.getText(0, doc.getLength());
            int start = text.lastIndexOf('\n', Math.max(0, getSelectionStart() - 1)) + 1;
            if (getSelectionStart() == 0) start = 0;
            int endSel = Math.max(getSelectionStart(), getSelectionEnd() - (getSelectionEnd() > getSelectionStart() ? 1 : 0));
            int end = text.indexOf('\n', endSel);
            if (end < 0) end = text.length();

            String[] lines = text.substring(start, end).split("\n", -1);
            boolean allCommented = true;
            for (String l : lines) {
                if (!l.isBlank() && !l.stripLeading().startsWith("//")) allCommented = false;
            }
            StringBuilder sb = new StringBuilder();
            for (int k = 0; k < lines.length; k++) {
                String l = lines[k];
                if (allCommented) {
                    int at = l.indexOf("//");
                    if (at >= 0) {
                        int cut = (at + 2 < l.length() && l.charAt(at + 2) == ' ') ? 3 : 2;
                        l = l.substring(0, at) + l.substring(at + cut);
                    }
                } else if (!l.isBlank()) {
                    l = "// " + l;
                }
                sb.append(l);
                if (k < lines.length - 1) sb.append('\n');
            }
            doc.remove(start, end - start);
            doc.insertString(start, sb.toString(), plain);
            select(start, start + sb.length());
        } catch (BadLocationException ignored) {
        }
    }

    /** Line numbers painted next to the editor. */
    private static final class LineNumbers extends JComponent {
        private final JTextPane editor;

        LineNumbers(JTextPane editor) {
            this.editor = editor;
            setFont(Theme.MONO.deriveFont(12.5f));
            editor.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) { revalidate(); repaint(); }
                public void removeUpdate(DocumentEvent e) { revalidate(); repaint(); }
                public void changedUpdate(DocumentEvent e) { }
            });
        }

        @Override
        public Dimension getPreferredSize() {
            int lines = editor.getDocument().getDefaultRootElement().getElementCount();
            int digits = Math.max(2, String.valueOf(lines).length());
            return new Dimension(getFontMetrics(getFont()).charWidth('0') * digits + 22, editor.getPreferredSize().height);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(Theme.CODE_GUTTER);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(Theme.CODE_CM);
            g2.setFont(getFont());
            Element root = editor.getDocument().getDefaultRootElement();
            FontMetrics fm = g2.getFontMetrics();
            for (int i = 0; i < root.getElementCount(); i++) {
                try {
                    Rectangle r = editor.modelToView2D(root.getElement(i).getStartOffset()).getBounds();
                    String n = String.valueOf(i + 1);
                    int y = r.y + (r.height + fm.getAscent() - fm.getDescent()) / 2;
                    g2.drawString(n, getWidth() - 10 - fm.stringWidth(n), y);
                } catch (BadLocationException ignored) {
                }
            }
        }
    }
}
