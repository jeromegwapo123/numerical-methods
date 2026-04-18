import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class Newton extends JFrame {

    private JTextField functionField, x0Field, errorField;
    private JTable table;
    private DefaultTableModel model;
    private JLabel resultLabel;

    // Newton theme: peach/orange — matches MainUI button Color(255, 228, 181)
    private static final Color THEME      = new Color(255, 167,  38);  // vivid orange
    private static final Color THEME_DARK = new Color(230, 111,   0);  // dark orange for border/title
    private static final Color THEME_TINT = new Color(255, 248, 225);  // light peach for alternating rows

    public Newton() {

        setTitle("Newton's Method");
        setSize(800, 620);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ===== HEADER =====
        JLabel header = new JLabel("NEWTON'S METHOD", SwingConstants.LEFT);
        header.setFont(new Font("Arial Black", Font.BOLD, 26));
        header.setForeground(THEME_DARK);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));
        add(header, BorderLayout.NORTH);

        // ===== LEFT PANEL =====
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(null);
        leftPanel.setPreferredSize(new Dimension(260, 0));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createLineBorder(THEME_DARK, 3));

        addInputRow(leftPanel, "f(X)", 20,  functionField = new JTextField("x^3 - x - 2"));
        addInputRow(leftPanel, "X0",   80,  x0Field       = new JTextField());
        addInputRow(leftPanel, "EA",   140, errorField    = new JTextField("0.01"));

        JButton computeBtn = new JButton("Compute");
        styleBtn(computeBtn, new Color(80, 200, 100));
        computeBtn.setBounds(20, 220, 210, 45);
        leftPanel.add(computeBtn);

        JButton backBtn = new JButton("BACK");
        styleBtn(backBtn, new Color(230, 60, 60));
        backBtn.setBounds(20, 280, 210, 45);
        leftPanel.add(backBtn);

        // ===== RIGHT PANEL =====
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JLabel iterTitle = new JLabel("Iterations", SwingConstants.CENTER);
        iterTitle.setFont(new Font("Times New Roman", Font.BOLD, 20));
        iterTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        rightPanel.add(iterTitle, BorderLayout.NORTH);

        String[] columns = {"i", "X0", "f(X0)", "f'(X0)", "X1", "EA"};
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

        resultLabel = new JLabel("Root: ", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 16));
        resultLabel.setForeground(THEME_DARK);
        resultLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        rightPanel.add(resultLabel, BorderLayout.SOUTH);

        // ===== SPLIT =====
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        split.setDividerLocation(265);
        split.setEnabled(false);
        split.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        add(split, BorderLayout.CENTER);

        computeBtn.addActionListener(e -> compute());
        backBtn.addActionListener(e -> { dispose(); MainUI.main(null); });
    }

    private void addInputRow(JPanel panel, String label, int y, JTextField field) {
        JLabel lbl = new JLabel(label);
        lbl.setBounds(20, y, 60, 40);
        lbl.setFont(new Font("Arial", Font.BOLD, 13));
        lbl.setForeground(THEME_DARK);
        panel.add(lbl);

        field.setBounds(80, y, 155, 40);
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

    private double derivative(String expr, double x) {
        double h = 1e-5;
        return (MathParser.f(expr, x + h) - MathParser.f(expr, x - h)) / (2 * h);
    }

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
        resultLabel.setText("Root: ");
        try {
            String expr = validateExpr();
            if (expr == null) return;

            double x0  = Double.parseDouble(x0Field.getText().trim());
            double tol = Double.parseDouble(errorField.getText().trim());

            double x1 = 0, Ea = 100;

            for (int i = 1; i <= 100; i++) {
                double fx0  = round2(MathParser.f(expr, x0));
                double fpx0 = round2(derivative(expr, x0));

                if (Math.abs(fpx0) < 1e-10) {
                    JOptionPane.showMessageDialog(this, "Derivative is zero at x = " + x0); return;
                }

                x1 = round2(x0 - MathParser.f(expr, x0) / derivative(expr, x0));

                String EaText;
                if (i == 1) {
                    EaText = "N/A";
                } else {
                    Ea = round2(Math.abs(x1 - x0));
                    EaText = String.format("%.2f", Ea);
                }

                model.addRow(new Object[]{
                        i,
                        String.format("%.2f", x0),
                        String.format("%.2f", fx0),
                        String.format("%.2f", fpx0),
                        String.format("%.2f", x1),
                        EaText
                });

                if (i > 1 && Ea <= tol) break;
                x0 = x1;
            }

            resultLabel.setText("Estimated Root ≈ " + String.format("%.2f", x1));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: Invalid input.");
        }
    }
}