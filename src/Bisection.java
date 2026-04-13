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

        // INPUT PANEL
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

        inputPanel.add(new JLabel("Ea tolerance (decimal):"));
        errorField = new JTextField("0.01");
        inputPanel.add(errorField);

        JButton computeBtn = new JButton("Compute");
        JButton backBtn = new JButton("Back");

        inputPanel.add(computeBtn);
        inputPanel.add(backBtn);

        add(inputPanel, BorderLayout.NORTH);

        // OUTPUT AREA
        outputArea = new JTextArea();
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        outputArea.setEditable(false);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        computeBtn.addActionListener(e -> compute());

        backBtn.addActionListener(e -> {
            dispose();
            MainUI.main(null);
        });
    }

    // ================= FUNCTION =================
    private double f(String expr, double x) {

        expr = expr.replaceAll("\\s+", "");

        expr = expr.replaceAll("(\\d)(x)", "$1*x");

        expr = expr.replace("x^3", "(" + x + "*" + x + "*" + x + ")");
        expr = expr.replace("x^2", "(" + x + "*" + x + ")");

        expr = expr.replaceAll("(?<![0-9])x(?![0-9])", "(" + x + ")");

        try {
            return eval(expr);
        } catch (Exception e) {
            return 0;
        }
    }

    // ================= EVALUATOR =================
    private double eval(String expr) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < expr.length()) ? expr.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                return parseExpression();
            }

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int start = this.pos;

                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(expr.substring(start, this.pos));
                }

                return x;
            }
        }.parse();
    }

    // ================= COMPUTE =================
    private void compute() {

        outputArea.setText("");

        try {
            String expr = functionField.getText();
            double x0 = Double.parseDouble(x0Field.getText());
            double x1 = Double.parseDouble(x1Field.getText());
            double tol = Double.parseDouble(errorField.getText());

            if (tol > 1) tol = tol / 100;

            int maxIter = 50;

            if (f(expr, x0) * f(expr, x1) >= 0) {
                outputArea.setText("Invalid interval: f(x0) and f(x1) must have opposite signs.");
                return;
            }

            outputArea.append(String.format("%-8s %-8s %-8s %-8s %-10s %-8s\n",
                    "Iter", "x0", "x1", "x2", "f(x2)", "Ea"));
            outputArea.append("------------------------------------------------------\n");

            double x2 = 0, x2Old = 0;
            double EaValue = 0;
            String Ea;

            for (int i = 1; i <= maxIter; i++) {

                x2 = (x0 + x1) / 2;
                double fx2 = f(expr, x2);

                // ROUND EVERYTHING TO 2 DECIMALS
                x0 = Math.round(x0 * 100.0) / 100.0;
                x1 = Math.round(x1 * 100.0) / 100.0;
                x2 = Math.round(x2 * 100.0) / 100.0;
                fx2 = Math.round(fx2 * 100.0) / 100.0;

                if (i == 1) {
                    Ea = "N/A";
                } else {
                    EaValue = Math.abs((x2 - x2Old) / x2);
                    EaValue = Math.round(EaValue * 100.0) / 100.0;
                    Ea = String.valueOf(EaValue);
                }

                outputArea.append(String.format("%-8d %-8.2f %-8.2f %-8.2f %-10.2f %-8s\n",
                        i, x0, x1, x2, fx2, Ea));

                if (i != 1 && EaValue <= tol) break;

                if (f(expr, x0) * fx2 < 0) {
                    x1 = x2;
                } else {
                    x0 = x2;
                }

                x2Old = x2;
            }

            outputArea.append("\nApprox Root = " + String.format("%.2f", x2));

        } catch (Exception e) {
            outputArea.setText("Error: Invalid input.");
        }
    }
}