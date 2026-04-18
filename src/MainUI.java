import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.*;

public class MainUI {

    // ── Palette ──────────────────────────────────────────────
    static final Color BG        = new Color(13,  17,  23);
    static final Color GRID_LINE = new Color(255, 255, 255, 8);
    static final Color ACCENT    = new Color(88,  166, 255);
    static final Color FG_DIM    = new Color(255, 255, 255, 50);
    static final Color FG_MID    = new Color(255, 255, 255, 76);
    static final Color FG_MAIN   = new Color(240, 246, 252);
    static final Color DIVIDER   = new Color(255, 255, 255, 20);

    public static void main(String[] args) {
        JFrame frame = new JFrame("Numerical Methods");
        frame.setSize(820, 520);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setBackground(BG);

        // ── Root panel with grid background ──────────────────
        JPanel root = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                // dark bg
                g2.setColor(BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // subtle grid
                g2.setColor(GRID_LINE);
                for (int x = 0; x < getWidth(); x += 40)
                    g2.drawLine(x, 0, x, getHeight());
                for (int y = 0; y < getHeight(); y += 40)
                    g2.drawLine(0, y, getWidth(), y);
                // corner brackets
                g2.setColor(new Color(255, 255, 255, 38));
                g2.setStroke(new BasicStroke(1.5f));
                int m = 18, s = 18;
                g2.drawLine(m, m, m+s, m);      g2.drawLine(m, m, m, m+s);           // TL
                g2.drawLine(getWidth()-m-s, m, getWidth()-m, m);
                g2.drawLine(getWidth()-m, m, getWidth()-m, m+s);                      // TR
                g2.drawLine(m, getHeight()-m, m+s, getHeight()-m);
                g2.drawLine(m, getHeight()-m-s, m, getHeight()-m);                   // BL
                g2.drawLine(getWidth()-m-s, getHeight()-m, getWidth()-m, getHeight()-m);
                g2.drawLine(getWidth()-m, getHeight()-m-s, getWidth()-m, getHeight()-m); // BR
            }
        };
        root.setBackground(BG);
        frame.setContentPane(root);

        int pad = 44;

        // ── Eyebrow label ─────────────────────────────────────
        JLabel eyebrow = new JLabel("ECPE206");
        eyebrow.setFont(loadMono(10f));
        eyebrow.setForeground(ACCENT);
        eyebrow.setBounds(pad, 34, 200, 16);
        root.add(eyebrow);

        // ── Title ─────────────────────────────────────────────
        JLabel title = new JLabel("Numerical Methods");
        title.setFont(new Font("Serif", Font.PLAIN, 28));
        title.setForeground(FG_MAIN);
        title.setBounds(pad, 52, 500, 38);
        root.add(title);



        // ── Buttons ───────────────────────────────────────────
        JButton bisectionBtn   = createButton("01", "Bisection",        new Color(173, 216, 230), new Color(10,  42,  58));
        JButton regulaBtn      = createButton("02", "Regula-Falsi",     new Color(144, 238, 144), new Color(13,  46,  13));
        JButton newtonBtn      = createButton("03", "Newton",           new Color(255, 228, 181), new Color(58,  40,   0));
        JButton secantBtn      = createButton("04", "Secant",           new Color(255, 182, 193), new Color(58,   0,  16));

        JButton jacobiBtn      = createButton("05", "Gauss-Jacobi",     new Color(221, 160, 221), new Color(42,   0,  64));
        JButton seidelBtn      = createButton("06", "Gauss-Seidel",     new Color(240, 230, 140), new Color(46,  40,   0));
        JButton simpsonBtn     = createButton("07", "Simpson's Rule",   new Color(176, 224, 230), new Color(10,  34,  40));
        JButton trapezoidalBtn = createButton("08", "Trapezoidal Rule", new Color(250, 200, 150), new Color(58,  24,   0));

        int bx = pad, by1 = 136, by2 = 278, bw = 164, bh = 82, bgap = 12;
        bisectionBtn.setBounds(bx + 0*(bw+bgap), by1, bw, bh);
        regulaBtn   .setBounds(bx + 1*(bw+bgap), by1, bw, bh);
        newtonBtn   .setBounds(bx + 2*(bw+bgap), by1, bw, bh);
        secantBtn   .setBounds(bx + 3*(bw+bgap), by1, bw, bh);

        jacobiBtn      .setBounds(bx + 0*(bw+bgap), by2, bw, bh);
        seidelBtn      .setBounds(bx + 1*(bw+bgap), by2, bw, bh);
        simpsonBtn     .setBounds(bx + 2*(bw+bgap), by2, bw, bh);
        trapezoidalBtn .setBounds(bx + 3*(bw+bgap), by2, bw, bh);

        for (JButton btn : new JButton[]{
                bisectionBtn, regulaBtn, newtonBtn, secantBtn,
                jacobiBtn, seidelBtn, simpsonBtn, trapezoidalBtn})
            root.add(btn);

        // ── Actions ───────────────────────────────────────────
        bisectionBtn  .addActionListener(e -> { frame.dispose(); new Bisection().setVisible(true); });
        regulaBtn     .addActionListener(e -> { frame.dispose(); new RegulaFalsi().setVisible(true); });
        newtonBtn     .addActionListener(e -> { frame.dispose(); new Newton().setVisible(true); });
        secantBtn     .addActionListener(e -> { frame.dispose(); new Secant().setVisible(true); });
        jacobiBtn     .addActionListener(e -> { frame.dispose(); new GaussJacobi().setVisible(true); });
        seidelBtn     .addActionListener(e -> { frame.dispose(); new GaussSeidel().setVisible(true); });
        simpsonBtn    .addActionListener(e -> { frame.dispose(); new SimpsonRule().setVisible(true); });
        trapezoidalBtn.addActionListener(e -> { frame.dispose(); new TrapezoidalRule().setVisible(true); });

        frame.setVisible(true);
    }

    // ── Button factory ────────────────────────────────────────
    static JButton createButton(String num, String label, Color bg, Color fg) {
        JButton btn = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                // number
                g2.setFont(loadMono(9f));
                g2.setColor(new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 130));
                g2.drawString(num, 12, 22);
                // label (wraps if needed)
                g2.setFont(loadMono(11.5f).deriveFont(Font.BOLD));
                g2.setColor(fg);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), 12, 46);
                // arrow
                g2.setFont(loadMono(11f));
                g2.setColor(new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 90));
                g2.drawString("→", getWidth()-22, getHeight()-12);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(164, 82); }
        };
        btn.setText(label);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(btn.getBackground().brighter());
                btn.repaint();
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(bg);
                btn.repaint();
            }
        });
        return btn;
    }

    static JLabel makeSectionLabel(String text, int x, int y) {
        JLabel l = new JLabel(text.toUpperCase());
        l.setFont(loadMono(9f));
        l.setForeground(new Color(255, 255, 255, 76));
        l.setBounds(x, y, 500, 14);
        return l;
    }

    static JPanel makeDivider(String comment, int x, int y, int w) {
        JPanel d = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(DIVIDER);
                g2.drawLine(0, 8, getWidth(), 8);
                g2.setFont(loadMono(9f));
                FontMetrics fm = g2.getFontMetrics();
                int tw = fm.stringWidth(comment) + 8;
                g2.setColor(BG);
                g2.fillRect(0, 0, tw, 18);
                g2.setColor(new Color(255, 255, 255, 50));
                g2.drawString(comment, 0, 14);
            }
        };
        d.setOpaque(false);
        d.setBounds(x, y, w, 18);
        return d;
    }

    static Font loadMono(float size) {
        return new Font("Monospaced", Font.PLAIN, (int) size);
    }
}