import javax.swing.*;
import java.awt.*;

public class RegulaFalsi extends JFrame {

    private JTextField functionField, x0Field, x1Field, errorField;
    private JTextArea outputArea;

    public RegulaFalsi() {

        setTitle("Regula Falsi Method");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5));

        inputPanel.add(new JLabel("f(x):"));
        functionField = new JTextField("sin(x) - 0.5");
        inputPanel.add(functionField);

        inputPanel.add(new JLabel("x0:"));
        x0Field = new JTextField("0");
        inputPanel.add(x0Field);

        inputPanel.add(new JLabel("x1:"));
        x1Field = new JTextField("2");
        inputPanel.add(x1Field);

        inputPanel.add(new JLabel("Ea tolerance (%):"));
        errorField = new JTextField("0.01");
        inputPanel.add(errorField);

        JButton computeBtn = new JButton("Compute");
        JButton backBtn = new JButton("Back");

        inputPanel.add(computeBtn);
        inputPanel.add(backBtn);

        add(inputPanel, BorderLayout.NORTH);

        outputArea = new JTextArea();
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        computeBtn.addActionListener(e -> compute());

        backBtn.addActionListener(e -> {
            dispose();
            MainUI.main(null);
        });
    }

    private void compute() {

        outputArea.setText("");

        try {
            String expr = functionField.getText();
            double a = Double.parseDouble(x0Field.getText());
            double b = Double.parseDouble(x1Field.getText());
            double error = Double.parseDouble(errorField.getText());

            int itr = 50;

            double fa = MathParser.f(expr, a);
            double fb = MathParser.f(expr, b);

            double r = 0, r0 = 0, fr;
            double Ea = 100;

            outputArea.append("Iter   x0     x1     x2     f(x2)     Ea\n");
            outputArea.append("------------------------------------------------\n");

            for (int i = 1; i <= itr; i++) {

                r = (a * fb - b * fa) / (fb - fa);
                fr = MathParser.f(expr, r);

                if (i == 1) {
                    Ea = 100;
                } else {
                    Ea = Math.abs((r - r0) / r) * 100;
                }

                outputArea.append(String.format(
                        "%-6d %-6.2f %-6.2f %-6.2f %-8.4f %-6s\n",
                        i, a, b, r, fr,
                        (i == 1 ? "N/A" : String.format("%.2f", Ea))
                ));

                if (i > 1 && Ea <= error) break;

                if (fa * fr < 0) {
                    b = r;
                    fb = fr;
                } else {
                    a = r;
                    fa = fr;
                }

                r0 = r;
            }

            outputArea.append("\nRoot ≈ " + String.format("%.2f", r));

        } catch (Exception e) {
            outputArea.setText("Error: Invalid input.");
        }
    }
}