package app;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/** Colors, fonts and small UI helpers shared by the whole app. */
final class Theme {
    static final Color BG = new Color(0xf6f7fb);
    static final Color SURFACE = Color.WHITE;
    static final Color TEXT = new Color(0x1d2233);
    static final Color MUTED = new Color(0x5d6478);
    static final Color BORDER = new Color(0xe2e5ee);
    static final Color ACCENT = new Color(0x3b6ef5);
    static final Color ACCENT_SOFT = new Color(0xe8eefe);
    static final Color TIP = new Color(0x0f9d7a);
    static final Color WARN = new Color(0xc2410c);
    static final Color WATER = new Color(0x3b9cf5);

    static final Color CODE_BG = new Color(0x1e2230);
    static final Color CODE_GUTTER = new Color(0x171a25);
    static final Color CODE_TEXT = new Color(0xe6e8f0);
    static final Color CODE_KW = new Color(0xc792ea);
    static final Color CODE_STR = new Color(0xc3e88d);
    static final Color CODE_CM = new Color(0x7f8aa8);
    static final Color CODE_TY = new Color(0x82aaff);
    static final Color CODE_NUM = new Color(0xf78c6c);
    static final Color CODE_ERR = new Color(0xff8a80);

    static final Font UI = pick(Font.PLAIN, 14, "Segoe UI", "Helvetica Neue", "Dialog");
    static final Font UI_BOLD = UI.deriveFont(Font.BOLD);
    static final Font UI_SMALL = UI.deriveFont(12.5f);
    static final Font TITLE = UI.deriveFont(Font.BOLD, 20f);
    static final Font MONO = pick(Font.PLAIN, 14, "Consolas", "Menlo", "Monospaced");

    private Theme() {}

    private static Font pick(int style, int size, String... names) {
        java.util.Set<String> available = java.util.Set.of(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        for (String n : names) {
            if (available.contains(n) || n.equals("Dialog") || n.equals("Monospaced")) {
                return new Font(n, style, size);
            }
        }
        return new Font(Font.DIALOG, style, size);
    }

    static void installDefaults() {
        for (String key : new String[]{"Label.font", "Button.font", "CheckBox.font", "List.font",
                "TextField.font", "TabbedPane.font", "ToolTip.font", "OptionPane.messageFont"}) {
            UIManager.put(key, UI);
        }
        UIManager.put("TabbedPane.font", UI_BOLD);
        UIManager.put("TextArea.font", UI);
    }

    /** Reads a text resource bundled inside the jar. */
    static String resource(String path) {
        try (InputStream in = Theme.class.getResourceAsStream("/" + path)) {
            if (in == null) return "";
            return new String(in.readAllBytes(), StandardCharsets.UTF_8).replace("\r\n", "\n");
        } catch (IOException e) {
            return "";
        }
    }

    private static StyleSheet pageStyles;

    /** The tutorial styles. Added to each page's own document so they don't leak into labels. */
    private static StyleSheet pageStyles() {
        if (pageStyles == null) {
            pageStyles = new StyleSheet();
            pageStyles.addRule(resource("tutorial/style.css"));
        }
        return pageStyles;
    }

    /** A read-only HTML page that cannot be selected or copied. */
    static JEditorPane htmlView(String bodyHtml) {
        JEditorPane pane = new JEditorPane();
        pane.setEditorKit(new HTMLEditorKit());
        ((HTMLDocument) pane.getDocument()).getStyleSheet().addStyleSheet(pageStyles());
        pane.setEditable(false);
        pane.setText("<html><body>" + bodyHtml + "</body></html>");
        pane.setHighlighter(null);
        pane.setTransferHandler(null);
        pane.setDragEnabled(false);
        pane.setBackground(SURFACE);
        pane.setBorder(new EmptyBorder(22, 28, 28, 28));
        pane.setCaretPosition(0);
        return pane;
    }

    static JScrollPane scroll(Component c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setBorder(BorderFactory.createLineBorder(BORDER));
        sp.getVerticalScrollBar().setUnitIncrement(18);
        sp.getHorizontalScrollBar().setUnitIncrement(18);
        return sp;
    }

    static JLabel label(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }

    /** Flat rounded button: primary (filled accent) or secondary (outlined). */
    static JButton button(String text, boolean primary) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean hover = getModel().isRollover();
                boolean down = getModel().isPressed();
                Shape r = new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 10, 10);
                if (primary) {
                    g2.setColor(down ? ACCENT.darker() : hover ? ACCENT.brighter() : ACCENT);
                    g2.fill(r);
                } else {
                    g2.setColor(hover || down ? ACCENT_SOFT : SURFACE);
                    g2.fill(r);
                    g2.setColor(hover ? ACCENT : BORDER);
                    g2.draw(r);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(UI_BOLD);
        b.setForeground(primary ? Color.WHITE : TEXT);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(false);
        b.setRolloverEnabled(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(7, 16, 7, 16));
        return b;
    }

    /** A small "play" triangle for the Run button (the font may not have the symbol). */
    static Icon playIcon() {
        return new Icon() {
            public int getIconWidth() { return 10; }
            public int getIconHeight() { return 12; }
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(c.isEnabled() ? c.getForeground() : MUTED);
                g2.fillPolygon(new int[]{x, x + 10, x}, new int[]{y, y + 6, y + 12}, 3);
                g2.dispose();
            }
        };
    }

    /** Navigation list with rounded highlight for the selected row. */
    static <T> JList<T> navList(T[] items) {
        JList<T> list = new JList<>(items);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setBackground(BG);
        list.setFixedCellHeight(38);
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object value, int index,
                                                          boolean selected, boolean focus) {
                JLabel c = (JLabel) super.getListCellRendererComponent(l, value, index, false, false);
                String text = value.toString();
                boolean sub = text.startsWith("  ");
                c.setText(text.strip());
                c.setFont(selected ? UI_BOLD : UI);
                c.setForeground(selected ? TEXT : MUTED);
                c.setBorder(new EmptyBorder(0, sub ? 30 : 14, 0, 10));
                c.setOpaque(false);
                JPanel wrap = new JPanel(new BorderLayout()) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        if (selected) {
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g2.setColor(ACCENT_SOFT);
                            g2.fillRoundRect(6, 3, getWidth() - 12, getHeight() - 6, 10, 10);
                            g2.setColor(ACCENT);
                            g2.fillRoundRect(6, 9, 4, getHeight() - 18, 4, 4);
                            g2.dispose();
                        }
                    }
                };
                wrap.setBackground(BG);
                wrap.add(c);
                return wrap;
            }
        });
        return list;
    }

    static Image appIcon() {
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(ACCENT);
        g.fillRoundRect(0, 0, 64, 64, 18, 18);
        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 40));
        FontMetrics fm = g.getFontMetrics();
        g.drawString("1", (64 - fm.stringWidth("1")) / 2, 47);
        g.dispose();
        return img;
    }
}
