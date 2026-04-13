import javax.swing.*;
import java.awt.*;

public class Bisection extends JFrame {

    private JTextField functionField, x0Field, x1Field, errorField;
    private JTextArea outputArea;

    public Bisection() {

        setTitle("Bisection Method");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ================= INPUT PANEL =================
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

        // ================= OUTPUT =================
        outputArea = new JTextArea();
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        // ================= ACTIONS =================
        computeBtn.addActionListener(e -> compute());

        backBtn.addActionListener(e -> {
            dispose();
            MainUI.main(null);
        });
    }

    // ================= COMPUTE =================
    private void compute() {

        outputArea.setText("");

        try {
            String expr = functionField.getText();
            double x0 = Double.parseDouble(x0Field.getText());
            double x1 = Double.parseDouble(x1Field.getText());
            double tol = Double.parseDouble(errorField.getText());

            // ✔ FIX: initialize x2 properly
            double x2 = 0;
            double x2Old = 0;
            double Ea = 100;
            String EaText;

            if (MathParser.f(expr, x0) * MathParser.f(expr, x1) >= 0) {
                outputArea.setText("Invalid interval: f(x0) and f(x1) must have opposite signs.");
                return;
            }

            outputArea.append("Iter   x0     x1     x2     f(x2)   Ea\n");
            outputArea.append("------------------------------------------------------\n");

            for (int i = 1; i <= 50; i++) {

                x2 = (x0 + x1) / 2;
                double fx2 = MathParser.f(expr, x2);

                // ROUNDING (2 DECIMALS)
                x0 = Math.round(x0 * 100.0) / 100.0;
                x1 = Math.round(x1 * 100.0) / 100.0;
                x2 = Math.round(x2 * 100.0) / 100.0;
                fx2 = Math.round(fx2 * 100.0) / 100.0;

                // ERROR COMPUTATION
                if (i == 1) {
                    EaText = "N/A";
                } else {
                    Ea = Math.abs((x2 - x2Old) / x2);
                    Ea = Math.round(Ea * 100.0) / 100.0;
                    EaText = String.format("%.2f", Ea);
                }

                outputArea.append(
                        String.format("%-6d %-6.2f %-6.2f %-6.2f %-8.2f %-6s\n",
                                i, x0, x1, x2, fx2, EaText)
                );

                // STOP CONDITION
                if (i > 1 && Ea <= tol) break;

                // UPDATE INTERVAL
                if (MathParser.f(expr, x0) * fx2 < 0) {
                    x1 = x2;
                } else {
                    x0 = x2;
                }

                x2Old = x2;
            }

            outputArea.append("\nRoot ≈ " + String.format("%.2f", x2));

        } catch (Exception e) {
            outputArea.setText("Error: Invalid input.");
        }
    }
}