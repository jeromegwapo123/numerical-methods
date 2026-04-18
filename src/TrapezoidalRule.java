import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class TrapezoidalRule extends JFrame {

    // Trapezoidal theme: warm apricot — matches MainUI button Color(250, 200, 150)
    private static final Color THEME      = new Color(255, 152,  0);   // vivid amber-orange
    private static final Color THEME_DARK = new Color(188,  81,   0);  // deep burnt orange for border/title
    private static final Color THEME_TINT = new Color(255, 243, 224);  // light apricot for alternating rows

    private JTextField functionField, aField, bField, nField;
    private JTable table;
    private DefaultTableModel model;
    private JLabel resultLabel;

    public TrapezoidalRule() {
        setTitle("Trapezoidal Rule");
        setSize(850, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ===== HEADER =====
        JLabel header = new JLabel("TRAPEZOIDAL RULE", SwingConstants.LEFT);
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

        addInputRow(leftPanel, "f(x):",         20,  functionField = new JTextField("x^3 + 2*x"));
        addInputRow(leftPanel, "a:",             80,  aField        = new JTextField("0"));
        addInputRow(leftPanel, "b:",             140, bField        = new JTextField("1"));
        addInputRow(leftPanel, "n (intervals):", 200, nField        = new JTextField("4"));

        // Formula display label
        JLabel formulaLbl = new JLabel("<html><center>h/2 · [f(x₀) + 2f(x₁) +...+ 2f(xₙ₋₁) + f(xₙ)]</center></html>");
        formulaLbl.setBounds(10, 255, 245, 40);
        formulaLbl.setFont(new Font("Arial", Font.ITALIC, 11));
        formulaLbl.setForeground(Color.GRAY);
        formulaLbl.setHorizontalAlignment(SwingConstants.CENTER);
        leftPanel.add(formulaLbl);

        JButton computeBtn = new JButton("Compute");
        styleBtn(computeBtn, new Color(80, 200, 100));
        computeBtn.setBounds(20, 310, 210, 45);
        leftPanel.add(computeBtn);

        JButton backBtn = new JButton("BACK");
        styleBtn(backBtn, new Color(230, 60, 60));
        backBtn.setBounds(20, 370, 210, 45);
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

        computeBtn.addActionListener(e -> compute());
        backBtn.addActionListener(e -> { dispose(); MainUI.main(null); });
    }

    private void addInputRow(JPanel panel, String label, int y, JTextField field) {
        JLabel lbl = new JLabel(label);
        lbl.setBounds(15, y, 115, 40);
        lbl.setFont(new Font("Arial", Font.BOLD, 13));
        lbl.setForeground(THEME_DARK);
        panel.add(lbl);

        field.setBounds(130, y + 5, 120, 30);
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

    private void compute() {
        model.setRowCount(0);
        resultLabel.setText("Result: ");

        try {
            String expr = validateExpr();
            if (expr == null) return;

            double a = Double.parseDouble(aField.getText().trim());
            double b = Double.parseDouble(bField.getText().trim());
            int    n = Integer.parseInt(nField.getText().trim());

            if (n < 1) {
                JOptionPane.showMessageDialog(this, "n must be at least 1.");
                return;
            }
            if (a >= b) {
                JOptionPane.showMessageDialog(this, "a must be less than b.");
                return;
            }

            double h   = (b - a) / n;
            double sum = 0;

            for (int i = 0; i <= n; i++) {
                double xi  = round2(a + i * h);
                double fxi = round2(MathParser.f(expr, xi));
                int    mult = (i == 0 || i == n) ? 1 : 2;

                double weighted = round2(fxi * mult);
                sum = round2(sum + weighted);

                model.addRow(new Object[]{
                        i,
                        String.format("%.2f", xi),
                        String.format("%.2f", fxi),
                        mult,
                        String.format("%.2f", weighted)
                });
            }

            double result = round2((h / 2.0) * sum);
            resultLabel.setText("∫f(x)dx ≈ " + String.format("%.2f", result)
                    + "   [h = " + String.format("%.2f", h) + ",  Σ = " + String.format("%.2f", sum) + "]");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: Invalid input.");
        }
    }
}