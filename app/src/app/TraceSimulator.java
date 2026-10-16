package app;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Steps through small programs one line at a time, showing the current line, the variables
 * in memory, the output, and the trace table as it fills in.
 * The programs come from tracer.txt in the unit folder (format described at the top of that file).
 */
final class TraceSimulator extends JPanel {
    private record Var(String name, String type, String scope) {}

    /** One step: the line that runs, values it sets, variables that disappear, printed text, and an explanation. */
    private record Step(int line, Map<String, String> set, List<String> drop, String out, String note) {}

    private record Program(String title, List<String> code, List<Var> vars, List<Step> steps) {}

    private static final Color CHANGED = new Color(0xfff4c2);
    private static final Color LINE_HL = new Color(0x2f3858);

    private final List<Program> programs = parse(Theme.resource("tracer.txt"));
    private final List<JButton> tabs = new ArrayList<>();
    private final CodeView codeView = new CodeView();
    private final MemoryView memoryView = new MemoryView();
    private final JTextArea output = new JTextArea(3, 20);
    private final JLabel note = new JLabel();
    private final DefaultTableModel tableModel = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(tableModel);
    private final JButton back = Theme.button("Back", false);
    private final JButton next = Theme.button("Next", true);
    private final JButton reset = Theme.button("Reset", false);
    private final JLabel count = Theme.label("", Theme.UI_SMALL, Theme.MUTED);

    private int prog;
    private int pos;          // number of steps already done
    private State state;

    TraceSimulator() {
        super(new BorderLayout(0, 12));
        setBackground(Theme.SURFACE);
        setBorder(new EmptyBorder(22, 28, 22, 28));

        JPanel head = new JPanel();
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        head.setOpaque(false);
        head.add(left(Theme.label("Try it: Trace simulator", Theme.TITLE, Theme.TEXT)));
        head.add(Box.createVerticalStrut(6));
        head.add(left(Theme.label("Pick a program. Before each click on Next, predict what will change.", Theme.UI, Theme.MUTED)));
        head.add(Box.createVerticalStrut(10));
        JPanel tabRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabRow.setOpaque(false);
        for (int i = 0; i < programs.size(); i++) {
            int index = i;
            JButton b = Theme.button(programs.get(i).title(), false);
            b.addActionListener(e -> { prog = index; pos = 0; render(); });
            tabs.add(b);
            tabRow.add(b);
            tabRow.add(Box.createHorizontalStrut(6));
        }
        head.add(left(tabRow));

        output.setEditable(false);
        output.setFont(Theme.MONO);
        output.setBackground(Theme.CODE_BG);
        output.setForeground(Theme.CODE_TEXT);
        output.setBorder(new EmptyBorder(8, 10, 8, 10));
        output.setTransferHandler(null);
        output.setHighlighter(null);

        JPanel right = new JPanel(new BorderLayout(0, 6));
        right.setOpaque(false);
        right.add(sideTitle("MEMORY"), BorderLayout.NORTH);
        right.add(memoryView, BorderLayout.CENTER);
        JPanel outPanel = new JPanel(new BorderLayout(0, 6));
        outPanel.setOpaque(false);
        outPanel.add(sideTitle("OUTPUT"), BorderLayout.NORTH);
        outPanel.add(output, BorderLayout.CENTER);
        right.add(outPanel, BorderLayout.SOUTH);

        JPanel top = new JPanel(new GridLayout(1, 2, 14, 0));
        top.setOpaque(false);
        JScrollPane codeScroll = new JScrollPane(codeView);
        codeScroll.setBorder(BorderFactory.createEmptyBorder());
        top.add(codeScroll);
        top.add(right);
        top.setPreferredSize(new Dimension(600, 300));

        note.setFont(Theme.UI);
        note.setForeground(Theme.TEXT);
        note.setOpaque(true);
        note.setBackground(Theme.BG);
        note.setVerticalAlignment(SwingConstants.TOP);
        note.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Theme.BORDER), new EmptyBorder(8, 12, 8, 12)));
        note.setPreferredSize(new Dimension(100, 58));

        table.setFont(Theme.MONO.deriveFont(13f));
        table.setRowHeight(26);
        table.setRowSelectionAllowed(false);
        table.setFocusable(false);
        table.setShowVerticalLines(false);
        table.setGridColor(Theme.BORDER);
        table.getTableHeader().setFont(Theme.MONO.deriveFont(Font.BOLD, 13f));
        table.getTableHeader().setReorderingAllowed(false);
        table.setTransferHandler(null);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean sel, boolean focus, int row, int col) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(t, value, false, false, row, col);
                c.setBorder(new EmptyBorder(0, 10, 0, 10));
                boolean changed = state != null && col > 0 && col <= varCount()
                        && state.rows.get(row).changed.contains(programs.get(prog).vars().get(col - 1).name());
                c.setBackground(changed ? CHANGED : row == t.getRowCount() - 1 ? Theme.ACCENT_SOFT : Color.WHITE);
                c.setFont(changed ? t.getFont().deriveFont(Font.BOLD) : t.getFont());
                return c;
            }
        });
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        tableScroll.getViewport().setBackground(Color.WHITE);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        controls.setOpaque(false);
        controls.add(back);
        controls.add(next);
        controls.add(reset);
        controls.add(Box.createHorizontalStrut(12));
        controls.add(count);
        back.addActionListener(e -> { pos--; render(); });
        next.addActionListener(e -> { pos++; render(); });
        reset.addActionListener(e -> { pos = 0; render(); });

        JPanel middle = new JPanel(new BorderLayout(0, 10));
        middle.setOpaque(false);
        middle.add(top, BorderLayout.NORTH);
        middle.add(note, BorderLayout.CENTER);

        JPanel lower = new JPanel(new BorderLayout(0, 10));
        lower.setOpaque(false);
        lower.add(tableScroll, BorderLayout.CENTER);
        lower.add(controls, BorderLayout.SOUTH);

        JPanel body = new JPanel(new BorderLayout(0, 10));
        body.setOpaque(false);
        body.add(middle, BorderLayout.NORTH);
        body.add(lower, BorderLayout.CENTER);

        add(head, BorderLayout.NORTH);
        add(body, BorderLayout.CENTER);
        render();
    }

    private int varCount() {
        return programs.get(prog).vars().size();
    }

    private static JComponent left(JComponent c) {
        c.setAlignmentX(LEFT_ALIGNMENT);
        return c;
    }

    private static JLabel sideTitle(String text) {
        return Theme.label(text, Theme.UI_SMALL.deriveFont(Font.BOLD), Theme.MUTED);
    }

    // ----- state -----

    private record Row(int line, Map<String, String> values, Set<String> changed, String out) {}

    private static final class State {
        final Map<String, String> values = new LinkedHashMap<>();
        final Set<String> alive = new HashSet<>();
        final List<Row> rows = new ArrayList<>();
        final StringBuilder out = new StringBuilder();
        Step last;
    }

    /** Replays the first n steps of a program. */
    private static State replay(Program p, int n) {
        State s = new State();
        for (int i = 0; i < n; i++) {
            Step st = p.steps().get(i);
            st.drop().forEach(s.alive::remove);
            st.set().forEach((k, v) -> { s.values.put(k, v); s.alive.add(k); });
            if (st.out() != null) s.out.append(st.out()).append('\n');
            Map<String, String> snapshot = new LinkedHashMap<>();
            for (Var v : p.vars()) if (s.alive.contains(v.name())) snapshot.put(v.name(), s.values.get(v.name()));
            s.rows.add(new Row(st.line(), snapshot, st.set().keySet(), st.out() == null ? "" : st.out()));
            s.last = st;
        }
        return s;
    }

    private void render() {
        if (programs.isEmpty()) return;
        Program p = programs.get(prog);
        state = replay(p, pos);

        for (int i = 0; i < tabs.size(); i++) {
            JButton b = tabs.get(i);
            b.setForeground(i == prog ? Theme.ACCENT : Theme.TEXT);
            b.setText(i == prog ? "<html><u>" + programs.get(i).title() + "</u></html>" : programs.get(i).title());
        }
        codeView.repaint();
        codeView.revalidate();
        memoryView.repaint();
        output.setText(state.out.toString());
        note.setText("<html>" + (state.last == null ? "Press <b>Next</b> to run the first line." : state.last.note()) + "</html>");

        List<String> cols = new ArrayList<>(List.of("Line"));
        p.vars().forEach(v -> cols.add(v.name()));
        cols.add("Output");
        Object[][] data = new Object[state.rows.size()][cols.size()];
        for (int r = 0; r < state.rows.size(); r++) {
            Row row = state.rows.get(r);
            data[r][0] = row.line();
            for (int c = 0; c < p.vars().size(); c++) data[r][c + 1] = row.values().getOrDefault(p.vars().get(c).name(), "");
            data[r][cols.size() - 1] = row.out();
        }
        tableModel.setDataVector(data, cols.toArray());
        table.getColumnModel().getColumn(0).setMaxWidth(60);

        back.setEnabled(pos > 0);
        next.setEnabled(pos < p.steps().size());
        count.setText("Step " + pos + " of " + p.steps().size());
    }

    // ----- views -----

    /** The program's code with line numbers; the line that just ran is highlighted. */
    private final class CodeView extends JComponent {
        private static final Pattern TOKENS = Pattern.compile(
                "(\"(?:[^\"\\\\]|\\\\.)*\")|(//.*$)|\\b(int|double|static|private|public|void|return|new|class)\\b|\\b(\\d+(?:\\.\\d+)?)\\b|\\b([A-Z]\\w*)\\b");

        CodeView() {
            setFont(Theme.MONO.deriveFont(13.5f));
        }

        @Override
        public Dimension getPreferredSize() {
            FontMetrics fm = getFontMetrics(getFont());
            int lines = programs.isEmpty() ? 1 : programs.get(prog).code().size();
            int longest = programs.isEmpty() ? 10 : programs.get(prog).code().stream().mapToInt(String::length).max().orElse(10);
            return new Dimension(56 + fm.charWidth('m') * longest + 20, 16 + lines * (fm.getHeight() + 4));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(Theme.CODE_BG);
            g2.fillRect(0, 0, getWidth(), getHeight());
            if (programs.isEmpty()) return;
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int lh = fm.getHeight() + 4;
            List<String> code = programs.get(prog).code();
            int current = state != null && state.last != null ? state.last.line() : -1;
            for (int i = 0; i < code.size(); i++) {
                int top = 8 + i * lh;
                if (i + 1 == current) {
                    g2.setColor(LINE_HL);
                    g2.fillRect(0, top, getWidth(), lh);
                    g2.setColor(Theme.ACCENT);
                    g2.fillRect(0, top, 4, lh);
                }
                int y = top + (lh + fm.getAscent() - fm.getDescent()) / 2;
                g2.setColor(Theme.CODE_CM);
                String n = String.valueOf(i + 1);
                g2.drawString(n, 36 - fm.stringWidth(n), y);
                drawColored(g2, code.get(i), 56, y, fm);
            }
            g2.dispose();
        }

        private void drawColored(Graphics2D g2, String line, int x, int y, FontMetrics fm) {
            Matcher m = TOKENS.matcher(line);
            int last = 0;
            while (m.find()) {
                g2.setColor(Theme.CODE_TEXT);
                String plain = line.substring(last, m.start());
                g2.drawString(plain, x, y);
                x += fm.stringWidth(plain);
                g2.setColor(m.group(1) != null ? Theme.CODE_STR : m.group(2) != null ? Theme.CODE_CM
                        : m.group(3) != null ? Theme.CODE_KW : m.group(4) != null ? Theme.CODE_NUM : Theme.CODE_TY);
                g2.drawString(m.group(), x, y);
                x += fm.stringWidth(m.group());
                last = m.end();
            }
            g2.setColor(Theme.CODE_TEXT);
            g2.drawString(line.substring(last), x, y);
        }
    }

    /** The variables that exist right now, as labelled boxes, grouped by the method they belong to. */
    private final class MemoryView extends JComponent {
        MemoryView() {
            setPreferredSize(new Dimension(300, 200));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            if (programs.isEmpty() || state == null) return;
            Program p = programs.get(prog);
            Font small = Theme.UI_SMALL;
            Font mono = Theme.MONO.deriveFont(Font.BOLD, 15f);
            // Boxes go left to right and wrap to a new row; each method's group starts with its name above it.
            int x = 0, y = 18, w = 86, h = 38, gap = 12, rowHeight = h + 64;
            String scope = null;
            boolean any = false;
            for (Var v : p.vars()) {
                if (!state.alive.contains(v.name())) continue;
                any = true;
                boolean newScope = !v.scope().equals(scope);
                if (newScope && scope != null) x += 16;
                if (x > 0 && x + w > getWidth()) {
                    x = 0;
                    y += rowHeight;
                }
                if (newScope || x == 0) {
                    scope = v.scope();
                    g2.setFont(small);
                    g2.setColor(Theme.MUTED);
                    g2.drawString(scope, x, y - 6);
                }
                boolean changed = state.last != null && state.last.set().containsKey(v.name());
                g2.setColor(changed ? CHANGED : Color.WHITE);
                g2.fillRoundRect(x, y, w, h, 10, 10);
                g2.setColor(Theme.ACCENT);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(x + 1, y + 1, w - 2, h - 2, 10, 10);
                g2.setFont(mono);
                g2.setColor(Theme.TEXT);
                String val = state.values.get(v.name());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(val, x + (w - fm.stringWidth(val)) / 2, y + (h + fm.getAscent() - fm.getDescent()) / 2);
                g2.setFont(Theme.MONO.deriveFont(13f));
                fm = g2.getFontMetrics();
                g2.drawString(v.name(), x + (w - fm.stringWidth(v.name())) / 2, y + h + 16);
                g2.setFont(small);
                g2.setColor(Theme.MUTED);
                fm = g2.getFontMetrics();
                g2.drawString(v.type(), x + (w - fm.stringWidth(v.type())) / 2, y + h + 32);
                x += w + gap;
            }
            if (!any) {
                g2.setFont(small);
                g2.setColor(Theme.MUTED);
                g2.drawString("(empty: no variables yet)", 0, 12);
            }
            g2.dispose();
        }
    }

    // ----- tracer.txt -----

    /**
     * Format:
     *   program Title
     *   var name type scope
     *   code text of one line (everything after "code ", spaces kept)
     *   step line | a=5; b=8 | dropped vars | printed text | note (HTML allowed)
     */
    private static List<Program> parse(String text) {
        List<Program> out = new ArrayList<>();
        String title = null;
        List<String> code = null;
        List<Var> vars = null;
        List<Step> steps = null;
        for (String raw : text.split("\n")) {
            String line = raw.stripTrailing();
            if (line.startsWith("#") || line.isBlank()) continue;
            if (line.startsWith("program ")) {
                if (title != null) out.add(new Program(title, code, vars, steps));
                title = line.substring(8).strip();
                code = new ArrayList<>();
                vars = new ArrayList<>();
                steps = new ArrayList<>();
            } else if (title == null) {
                continue;
            } else if (line.equals("code") || line.startsWith("code ")) {
                code.add(line.length() > 5 ? line.substring(5) : "");
            } else if (line.startsWith("var ")) {
                String[] f = line.substring(4).strip().split("\\s+");
                vars.add(new Var(f[0], f[1], f.length > 2 ? f[2] : "main"));
            } else if (line.startsWith("step ")) {
                String[] f = line.substring(5).split("\\|", -1);
                Map<String, String> set = new LinkedHashMap<>();
                for (String pair : f[1].split(";")) {
                    if (pair.isBlank()) continue;
                    String[] kv = pair.split("=", 2);
                    set.put(kv[0].strip(), kv[1].strip());
                }
                List<String> drop = new ArrayList<>();
                for (String d : f[2].split(",")) if (!d.isBlank()) drop.add(d.strip());
                String printed = f[3].strip().isEmpty() ? null : f[3].strip();
                steps.add(new Step(Integer.parseInt(f[0].strip()), set, drop, printed, f[4].strip()));
            }
        }
        if (title != null) out.add(new Program(title, code, vars, steps));
        return out;
    }
}
