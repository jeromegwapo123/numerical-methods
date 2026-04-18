import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class Bisection extends JFrame {

    private JTextField functionField, x0Field, x1Field, errorField;
    private JTable table;
    private DefaultTableModel model;
    private JLabel resultLabel;

    // Bisection theme color: matches the light blue button in MainUI
    private static final Color THEME = new Color(100, 181, 246);   // richer readable blue
    private static final Color THEME_DARK = new Color(30, 136, 229); // darker for border

    public Bisection() {

        setTitle("Bisection Method");
        setSize(750, 620);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ===== HEADER =====
        JLabel header = new JLabel("BISECTION METHOD", SwingConstants.LEFT);
        header.setFont(new Font("Arial Black", Font.BOLD, 26));
        header.setForeground(THEME_DARK);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));
        add(header, BorderLayout.NORTH);

        // ===== LEFT PANEL (inputs) =====
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(null);
        leftPanel.setPreferredSize(new Dimension(260, 0));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createLineBorder(THEME_DARK, 3));

        addInputRow(leftPanel, "f(X)", 20,  functionField = new JTextField("x^3 - x - 2"));
        addInputRow(leftPanel, "X0",   80,  x0Field       = new JTextField());
        addInputRow(leftPanel, "X1",   140, x1Field       = new JTextField());
        addInputRow(leftPanel, "EA",   200, errorField    = new JTextField("0.01"));

        JButton computeBtn = new JButton("Compute");
        styleRoundedButton(computeBtn, new Color(80, 200, 100));
        computeBtn.setBounds(20, 270, 210, 45);
        leftPanel.add(computeBtn);

        JButton backBtn = new JButton("BACK");
        styleRoundedButton(backBtn, new Color(230, 60, 60));
        backBtn.setBounds(20, 330, 210, 45);
        leftPanel.add(backBtn);

        // ===== RIGHT PANEL (table) =====
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JLabel iterTitle = new JLabel("Iterations", SwingConstants.CENTER);
        iterTitle.setFont(new Font("Times New Roman", Font.BOLD, 20));
        iterTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        rightPanel.add(iterTitle, BorderLayout.NORTH);

        String[] columns = {"Iter", "X0", "X1", "X2", "f(X2)", "EA"};
        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.getTableHeader().setReorderingAllowed(false);
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        // Theme-colored header
        table.getTableHeader().setBackground(THEME);
        table.getTableHeader().setForeground(Color.WHITE);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++)
            table.getColumnModel().getColumn(i).setCellRenderer(center);

        // Alternating row colors using blue tint
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                                                           boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!sel) setBackground(row % 2 == 0 ? Color.WHITE : new Color(227, 242, 253));
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
        backBtn.addActionListener(e -> {
            dispose();
            MainUI.main(null);
        });
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

    private void styleRoundedButton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
    }

    private double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    private void compute() {
        model.setRowCount(0);
        resultLabel.setText("Root: ");

        try {
            String expr = functionField.getText().replaceAll("\\s+", "");

            if (!expr.matches("[0-9x+\\-*/^().a-zA-Z]+")) {
                JOptionPane.showMessageDialog(this, "Invalid characters in function!");
                return;
            }

            String temp = expr;
            for (String f : new String[]{"sin", "cos", "tan", "log", "sqrt", "exp"}) {
                temp = temp.replace(f, "");
            }
            temp = temp.replaceAll("[x0-9+\\-*/^().]", "");
            if (!temp.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Invalid or unsupported function detected!");
                return;
            }

            double x0  = Double.parseDouble(x0Field.getText().trim());
            double x1  = Double.parseDouble(x1Field.getText().trim());
            double tol = Double.parseDouble(errorField.getText().trim());

            if (MathParser.f(expr, x0) * MathParser.f(expr, x1) >= 0) {
                JOptionPane.showMessageDialog(this,
                        "Invalid interval: f(x0) and f(x1) must have opposite signs.");
                return;
            }

            double x2 = 0, x2Old = 0, Ea = 100;
            String EaText;

            for (int i = 1; i <= 50; i++) {
                x2 = round2((x0 + x1) / 2);
                double fx2 = round2(MathParser.f(expr, x2));

                EaText = (i == 1) ? "N/A" : String.format("%.2f", Ea = round2(Math.abs(x2 - x2Old)));

                model.addRow(new Object[]{
                        i,
                        String.format("%.2f", x0),
                        String.format("%.2f", x1),
                        String.format("%.2f", x2),
                        String.format("%.2f", fx2),
                        EaText
                });

                if (i > 1 && Ea <= tol) break;

                if (MathParser.f(expr, x0) * fx2 < 0) x1 = x2;
                else x0 = x2;

                x2Old = x2;
            }

            resultLabel.setText("Estimated Root ≈ " + String.format("%.2f", x2));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: Invalid input.");
        }
    }
}