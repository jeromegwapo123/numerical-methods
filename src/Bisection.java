import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class Bisection extends JFrame {

    private JTextField functionField, x0Field, x1Field, errorField;

    private JTable table;
    private DefaultTableModel model;

    private JLabel resultLabel;

    public Bisection() {

        setTitle("Bisection Method");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5));

        inputPanel.add(new JLabel("f(x):"));
        functionField = new JTextField("x^3 - x - 2");
        inputPanel.add(functionField);

        inputPanel.add(new JLabel("x0:"));
        x0Field = new JTextField();
        inputPanel.add(x0Field);

        inputPanel.add(new JLabel("x1:"));
        x1Field = new JTextField();
        inputPanel.add(x1Field);

        inputPanel.add(new JLabel("Ea tolerance:"));
        errorField = new JTextField("0.01");
        inputPanel.add(errorField);

        JButton computeBtn = new JButton("Compute");
        JButton backBtn = new JButton("Back");

        inputPanel.add(computeBtn);
        inputPanel.add(backBtn);

        add(inputPanel, BorderLayout.NORTH);

        String[] columns = {"Iter", "x0", "x1", "x2", "f(x2)", "Ea"};

        // ✅ ONLY CHANGE HERE
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        resultLabel = new JLabel("Root: ");
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 16));
        resultLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        resultLabel.setForeground(new Color(0, 100, 0));

        outputPanel.add(resultLabel, BorderLayout.SOUTH);

        add(outputPanel, BorderLayout.CENTER);

        computeBtn.addActionListener(e -> compute());

        backBtn.addActionListener(e -> {
            dispose();
            MainUI.main(null);
        });
    }

    private double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    private void compute() {

        model.setRowCount(0);
        resultLabel.setText("Root: ");

        try {
            String expr = functionField.getText();

            expr = expr.replaceAll("\\s+", "");

            if (!expr.matches("[0-9x+\\-*/^().a-zA-Z]+")) {
                JOptionPane.showMessageDialog(this, "Invalid characters in function!");
                return;
            }

            String temp = expr;

            String[] validFunctions = {"sin", "cos", "tan", "log", "sqrt"};

            for (String f : validFunctions) {
                temp = temp.replace(f, "");
            }

            temp = temp.replaceAll("[x0-9+\\-*/^().]", "");

            if (!temp.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Invalid or unsupported function detected!");
                return;
            }

            double x0 = Double.parseDouble(x0Field.getText());
            double x1 = Double.parseDouble(x1Field.getText());
            double tol = Double.parseDouble(errorField.getText());

            double x2 = 0, x2Old = 0;
            double Ea = 100;
            String EaText;

            if (MathParser.f(expr, x0) * MathParser.f(expr, x1) >= 0) {
                JOptionPane.showMessageDialog(this,
                        "Invalid interval: f(x0) and f(x1) must have opposite signs.");
                return;
            }

            for (int i = 1; i <= 50; i++) {

                x2 = (x0 + x1) / 2;
                x2 = round2(x2);

                double fx2 = round2(MathParser.f(expr, x2));

                if (i == 1) {
                    EaText = "N/A";
                } else {
                    Ea = Math.abs(x2 - x2Old);
                    Ea = round2(Ea);
                    EaText = String.format("%.2f", Ea);
                }

                model.addRow(new Object[]{
                        i,
                        String.format("%.2f", x0),
                        String.format("%.2f", x1),
                        String.format("%.2f", x2),
                        String.format("%.2f", fx2),
                        EaText
                });

                if (i > 1 && Ea <= tol) break;

                if (MathParser.f(expr, x0) * fx2 < 0) {
                    x1 = x2;
                } else {
                    x0 = x2;
                }

                x2Old = x2;
            }

            resultLabel.setText("Estimated Root ≈ " + String.format("%.2f", x2));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: Invalid input.");
        }
    }
}