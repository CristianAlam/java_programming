package app;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;

/** Two buckets you control with buttons; each click shows the matching Java command. */
final class BucketSimulator extends JPanel {
    private final int[] cap = {5, 3};
    private final int[] amt = {0, 0};
    private final String[] names = {"big", "small"};
    private final JTextPane log = new JTextPane();
    private final BucketsView view = new BucketsView();

    BucketSimulator() {
        super(new BorderLayout(0, 14));
        setBackground(Theme.SURFACE);
        setBorder(new EmptyBorder(22, 28, 22, 28));

        JPanel head = new JPanel();
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        head.setOpaque(false);
        head.add(Theme.label("Try it: Bucket simulator", Theme.TITLE, Theme.TEXT));
        head.add(Box.createVerticalStrut(6));
        head.add(Theme.label("Click the buttons to control two buckets. Each click shows the Java command that does the same thing.",
                Theme.UI, Theme.MUTED));
        head.add(Box.createVerticalStrut(4));
        head.add(Theme.label("Challenge: can you get exactly 4 liters into big? (Homework exercise 8)", Theme.UI_BOLD, Theme.TIP));

        JPanel buttons = new JPanel(new GridLayout(2, 4, 8, 8));
        buttons.setOpaque(false);
        buttons.add(btn("big.fill()", () -> amt[0] = cap[0], "big.fill();"));
        buttons.add(btn("small.fill()", () -> amt[1] = cap[1], "small.fill();"));
        buttons.add(btn("big.pourInto(small)", () -> pour(0, 1), null));
        buttons.add(btn("small.pourInto(big)", () -> pour(1, 0), null));
        buttons.add(btn("big.empty()", () -> amt[0] = 0, "big.empty();"));
        buttons.add(btn("small.empty()", () -> amt[1] = 0, "small.empty();"));
        JButton reset = Theme.button("Reset", false);
        reset.addActionListener(e -> reset());
        buttons.add(reset);

        log.setEditable(false);
        log.setFont(Theme.MONO);
        log.setBackground(Theme.CODE_BG);
        log.setBorder(new EmptyBorder(10, 12, 10, 12));
        log.setTransferHandler(null);
        log.setHighlighter(null);
        JScrollPane logScroll = new JScrollPane(log);
        logScroll.setBorder(BorderFactory.createEmptyBorder());
        logScroll.setPreferredSize(new Dimension(100, 170));

        JPanel center = new JPanel(new BorderLayout(0, 14));
        center.setOpaque(false);
        center.add(view, BorderLayout.CENTER);
        center.add(buttons, BorderLayout.SOUTH);

        add(head, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(logScroll, BorderLayout.SOUTH);
        reset();
    }

    private JButton btn(String label, Runnable action, String code) {
        JButton b = Theme.button(label, false);
        b.setFont(Theme.MONO.deriveFont(Font.BOLD, 13f));
        b.addActionListener(e -> {
            action.run();
            if (code != null) logLine(code, Theme.CODE_TEXT);
            if (amt[0] == 4) logLine("// big has exactly 4 liters! Well done.", Theme.CODE_STR);
            view.repaint();
        });
        return b;
    }

    private void pour(int from, int to) {
        int moved = Math.min(amt[from], cap[to] - amt[to]);
        amt[from] -= moved;
        amt[to] += moved;
        logLine(names[from] + ".pourInto(" + names[to] + ");   // moved " + moved + (moved == 1 ? " liter" : " liters"),
                Theme.CODE_TEXT);
    }

    private void reset() {
        amt[0] = 0;
        amt[1] = 0;
        log.setText("");
        logLine("Bucket big = new Bucket(5);", Theme.CODE_TY);
        logLine("Bucket small = new Bucket(3);", Theme.CODE_TY);
        view.repaint();
    }

    private void logLine(String text, Color color) {
        StyledDocument doc = log.getStyledDocument();
        SimpleAttributeSet s = new SimpleAttributeSet();
        StyleConstants.setForeground(s, color);
        try {
            if (doc.getLength() > 0) doc.insertString(doc.getLength(), "\n", s);
            int comment = text.indexOf("//");
            if (comment > 0) {
                doc.insertString(doc.getLength(), text.substring(0, comment), s);
                SimpleAttributeSet c = new SimpleAttributeSet();
                StyleConstants.setForeground(c, Theme.CODE_CM);
                doc.insertString(doc.getLength(), text.substring(comment), c);
            } else {
                doc.insertString(doc.getLength(), text, s);
            }
        } catch (Exception ignored) {
        }
        log.setCaretPosition(doc.getLength());
    }

    /** Draws the two buckets with their water level. */
    private final class BucketsView extends JComponent {
        BucketsView() {
            setPreferredSize(new Dimension(400, 230));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int unit = 30;                  // pixels per liter
            int w = 110, gap = 90;
            int baseY = getHeight() - 46;
            int x0 = (getWidth() - (2 * w + gap)) / 2;
            for (int i = 0; i < 2; i++) {
                int x = x0 + i * (w + gap);
                int h = cap[i] * unit;
                int top = baseY - h;
                g2.setColor(Theme.BG);
                g2.fillRoundRect(x, top, w, h, 16, 16);
                int water = amt[i] * unit;
                g2.setColor(Theme.WATER);
                g2.setClip(new java.awt.geom.RoundRectangle2D.Float(x, top, w, h, 16, 16));
                g2.fillRect(x, baseY - water, w, water);
                g2.setClip(null);
                g2.setColor(new Color(127, 127, 127, 80));
                for (int l = 1; l < cap[i]; l++) g2.drawLine(x + 6, baseY - l * unit, x + w - 6, baseY - l * unit);
                g2.setColor(Theme.MUTED);
                g2.setStroke(new BasicStroke(3f));
                g2.drawLine(x, top, x, baseY - 8);
                g2.drawLine(x + w, top, x + w, baseY - 8);
                g2.drawArc(x, baseY - 16, 16, 16, 180, 90);
                g2.drawArc(x + w - 16, baseY - 16, 16, 16, 270, 90);
                g2.drawLine(x + 8, baseY, x + w - 8, baseY);
                g2.setStroke(new BasicStroke(1f));

                g2.setFont(Theme.MONO.deriveFont(Font.BOLD, 15f));
                g2.setColor(Theme.TEXT);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(names[i], x + (w - fm.stringWidth(names[i])) / 2, baseY + 22);
                g2.setFont(Theme.UI_SMALL);
                g2.setColor(Theme.MUTED);
                String t = amt[i] + " / " + cap[i] + " liters";
                fm = g2.getFontMetrics();
                g2.drawString(t, x + (w - fm.stringWidth(t)) / 2, baseY + 40);
            }
            g2.dispose();
        }
    }
}
