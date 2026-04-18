import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class SimpsonRule extends JFrame {

    // Simpson theme: powder blue — matches MainUI button Color(176, 224, 230)
    private static final Color THEME      = new Color( 41, 182, 246);  // vivid sky blue
    private static final Color THEME_DARK = new Color(  2, 119, 189);  // deep ocean blue for border/title
    private static final Color THEME_TINT = new Color(225, 245, 254);  // lightest blue for alternating rows

    private JTextField functionField, aField, bField, nField;
    private JTable table;
    private DefaultTableModel model;
    private JLabel resultLabel;

    public SimpsonRule() {
        setTitle("Simpson's Rule");
        setSize(850, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ===== HEADER =====
        JLabel header = new JLabel("SIMPSON'S RULE", SwingConstants.LEFT);
        header.setFont(new Font("Arial Black", Font.BOLD, 26));
        header.setForeground(THEME_DARK);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));
        add(header, BorderLayout.NORTH);

        // ===== LEFT PANEL =====
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(null);
        leftPanel.setPreferredSize(new Dimension(270, 0));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createLineBorder(THEME_DARK, 3));

        // Rule selector (1/3 vs 3/8)
        JLabel ruleLabel = new JLabel("Rule:");
        ruleLabel.setBounds(20, 15, 50, 30);
        ruleLabel.setFont(new Font("Arial", Font.BOLD, 13));
        ruleLabel.setForeground(THEME_DARK);
        leftPanel.add(ruleLabel);

        JRadioButton oneThird = new JRadioButton("1/3 Rule", true);
        JRadioButton threeEighths = new JRadioButton("3/8 Rule");
        oneThird.setBounds(70, 15, 90, 30);
        threeEighths.setBounds(160, 15, 90, 30);
        oneThird.setBackground(Color.WHITE);
        threeEighths.setBackground(Color.WHITE);
        oneThird.setFont(new Font("Arial", Font.PLAIN, 12));
        threeEighths.setFont(new Font("Arial", Font.PLAIN, 12));
        oneThird.setForeground(THEME_DARK);
        threeEighths.setForeground(THEME_DARK);
        ButtonGroup ruleGroup = new ButtonGroup();
        ruleGroup.add(oneThird);
        ruleGroup.add(threeEighths);
        leftPanel.add(oneThird);
        leftPanel.add(threeEighths);

        addInputRow(leftPanel, "f(x):", 55,  functionField = new JTextField("x^3 + 2*x"));
        addInputRow(leftPanel, "a:",    115, aField        = new JTextField("0"));
        addInputRow(leftPanel, "b:",    175, bField        = new JTextField("1"));
        addInputRow(leftPanel, "n (intervals):", 235, nField = new JTextField("4"));

        // n hint label
        JLabel hint = new JLabel("* n must be even (1/3) or mult. of 3 (3/8)");
        hint.setBounds(15, 280, 240, 20);
        hint.setFont(new Font("Arial", Font.ITALIC, 10));
        hint.setForeground(Color.GRAY);
        leftPanel.add(hint);

        JButton computeBtn = new JButton("Compute");
        styleBtn(computeBtn, new Color(80, 200, 100));
        computeBtn.setBounds(20, 315, 210, 45);
        leftPanel.add(computeBtn);

        JButton backBtn = new JButton("BACK");
        styleBtn(backBtn, new Color(230, 60, 60));
        backBtn.setBounds(20, 375, 210, 45);
        leftPanel.add(backBtn);

        // ===== RIGHT PANEL =====
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JLabel iterTitle = new JLabel("Computation Table", SwingConstants.CENTER);
        iterTitle.setFont(new Font("Times New Roman", Font.ITALIC, 20));
        iterTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        rightPanel.add(iterTitle, BorderLayout.NORTH);

        String[] columns = {"i", "xi", "f(xi)", "Multiplier", "f(xi) × m"};
        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.getTableHeader().setReorderingAllowed(false);
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(THEME);
        table.getTableHeader().setForeground(Color.WHITE);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val,
                                                           boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!sel) setBackground(row % 2 == 0 ? Color.WHITE : THEME_TINT);
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(THEME, 2));
        rightPanel.add(scroll, BorderLayout.CENTER);

        resultLabel = new JLabel("Result: ", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 16));
        resultLabel.setForeground(THEME_DARK);
        resultLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        rightPanel.add(resultLabel, BorderLayout.SOUTH);

        // ===== SPLIT =====
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        split.setDividerLocation(275);
        split.setEnabled(false);
        split.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        add(split, BorderLayout.CENTER);

        computeBtn.addActionListener(e -> compute(oneThird.isSelected()));
        backBtn.addActionListener(e -> { dispose(); MainUI.main(null); });
    }

    private void addInputRow(JPanel panel, String label, int y, JTextField field) {
        JLabel lbl = new JLabel(label);
        lbl.setBounds(15, y, 110, 40);
        lbl.setFont(new Font("Arial", Font.BOLD, 13));
        lbl.setForeground(THEME_DARK);
        panel.add(lbl);

        field.setBounds(125, y + 5, 125, 30);
        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createLineBorder(THEME));
        panel.add(field);
    }

    private void styleBtn(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
    }

    private double round2(double v) { return Math.round(v * 100.0) / 100.0; }

    private String validateExpr() {
        String expr = functionField.getText().replaceAll("\\s+", "");
        if (!expr.matches("[0-9x+\\-*/^().a-zA-Z]+")) {
            JOptionPane.showMessageDialog(this, "Invalid characters in function!"); return null;
        }
        String temp = expr;
        for (String f : new String[]{"sin","cos","tan","log","sqrt","exp"}) temp = temp.replace(f, "");
        temp = temp.replaceAll("[x0-9+\\-*/^().]", "");
        if (!temp.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Invalid or unsupported function!"); return null;
        }
        return expr;
    }

    private void compute(boolean useOneThird) {
        model.setRowCount(0);
        resultLabel.setText("Result: ");

        try {
            String expr = validateExpr();
            if (expr == null) return;

            double a = Double.parseDouble(aField.getText().trim());
            double b = Double.parseDouble(bField.getText().trim());
            int    n = Integer.parseInt(nField.getText().trim());

            if (useOneThird) {
                // 1/3 Rule: n must be even
                if (n % 2 != 0) {
                    JOptionPane.showMessageDialog(this, "For Simpson's 1/3 Rule, n must be even.");
                    return;
                }
                computeOneThird(expr, a, b, n);
            } else {
                // 3/8 Rule: n must be multiple of 3
                if (n % 3 != 0) {
                    JOptionPane.showMessageDialog(this, "For Simpson's 3/8 Rule, n must be a multiple of 3.");
                    return;
                }
                computeThreeEighths(expr, a, b, n);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: Invalid input.");
        }
    }

    private void computeOneThird(String expr, double a, double b, int n) {
        double h = (b - a) / n;
        double sum = 0;

        for (int i = 0; i <= n; i++) {
            double xi  = round2(a + i * h);
            double fxi = round2(MathParser.f(expr, xi));
            int    mult;

            if (i == 0 || i == n) mult = 1;
            else if (i % 2 == 0)  mult = 2;
            else                   mult = 4;

            double weighted = round2(fxi * mult);
            sum += weighted;

            model.addRow(new Object[]{
                    i,
                    String.format("%.2f", xi),
                    String.format("%.2f", fxi),
                    mult,
                    String.format("%.2f", weighted)
            });
        }

        double result = round2((h / 3.0) * sum);
        resultLabel.setText("∫f(x)dx ≈ " + String.format("%.2f", result)
                + "   [h = " + String.format("%.2f", h) + ",  Σ = " + String.format("%.2f", sum) + "]");
    }

    private void computeThreeEighths(String expr, double a, double b, int n) {
        double h = (b - a) / n;
        double sum = 0;

        for (int i = 0; i <= n; i++) {
            double xi  = round2(a + i * h);
            double fxi = round2(MathParser.f(expr, xi));
            int    mult;

            if (i == 0 || i == n) mult = 1;
            else if (i % 3 == 0)  mult = 2;
            else                   mult = 3;

            double weighted = round2(fxi * mult);
            sum += weighted;

            model.addRow(new Object[]{
                    i,
                    String.format("%.2f", xi),
                    String.format("%.2f", fxi),
                    mult,
                    String.format("%.2f", weighted)
            });
        }

        double result = round2((3.0 * h / 8.0) * sum);
        resultLabel.setText("∫f(x)dx ≈ " + String.format("%.2f", result)
                + "   [h = " + String.format("%.2f", h) + ",  Σ = " + String.format("%.2f", sum) + "]");
    }
}