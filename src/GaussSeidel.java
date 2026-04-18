import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GaussSeidel extends JFrame {

    // Gauss-Seidel theme: khaki/yellow — matches MainUI button Color(240, 230, 140)
    private static final Color THEME      = new Color(251, 192,  45);  // vivid amber/yellow
    private static final Color THEME_DARK = new Color(245, 127,  23);  // deep amber for border/title
    private static final Color THEME_TINT = new Color(255, 249, 219);  // light yellow for alternating rows

    private JSpinner sizeSpinner;
    private JPanel matrixPanel;
    private JTextField[][] matrixFields;
    private JTextField errorField;

    private JTable table;
    private DefaultTableModel model;
    private JLabel resultLabel;

    private int n = 3;

    public GaussSeidel() {
        setTitle("Gauss-Seidel Method");
        setSize(950, 680);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ===== HEADER =====
        JLabel header = new JLabel("GAUSS-SEIDEL METHOD", SwingConstants.LEFT);
        header.setFont(new Font("Arial Black", Font.BOLD, 26));
        header.setForeground(THEME_DARK);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));
        add(header, BorderLayout.NORTH);

        // ===== LEFT PANEL =====
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(310, 0));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createLineBorder(THEME_DARK, 3));

        // — Size selector —
        JPanel sizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        sizePanel.setBackground(Color.WHITE);
        JLabel sizeLabel = new JLabel("Equations (n):");
        sizeLabel.setFont(new Font("Arial", Font.BOLD, 13));
        sizeLabel.setForeground(THEME_DARK);
        sizeSpinner = new JSpinner(new SpinnerNumberModel(3, 2, 6, 1));
        sizeSpinner.setPreferredSize(new Dimension(55, 30));
        JButton buildBtn = new JButton("Build");
        styleBtn(buildBtn, THEME);
        buildBtn.setForeground(Color.BLACK);
        buildBtn.setPreferredSize(new Dimension(65, 30));
        sizePanel.add(sizeLabel);
        sizePanel.add(sizeSpinner);
        sizePanel.add(buildBtn);
        leftPanel.add(sizePanel, BorderLayout.NORTH);

        // — Matrix input area —
        matrixPanel = new JPanel();
        matrixPanel.setBackground(Color.WHITE);
        leftPanel.add(new JScrollPane(matrixPanel), BorderLayout.CENTER);

        // — Bottom controls —
        JPanel bottomPanel = new JPanel(null);
        bottomPanel.setPreferredSize(new Dimension(0, 130));
        bottomPanel.setBackground(Color.WHITE);

        JLabel eaLbl = new JLabel("EA tolerance:");
        eaLbl.setBounds(10, 10, 100, 30);
        eaLbl.setFont(new Font("Arial", Font.BOLD, 12));
        eaLbl.setForeground(THEME_DARK);
        bottomPanel.add(eaLbl);

        errorField = new JTextField("0.01");
        errorField.setBounds(115, 10, 100, 30);
        errorField.setFont(new Font("Arial", Font.PLAIN, 12));
        errorField.setBorder(BorderFactory.createLineBorder(THEME));
        bottomPanel.add(errorField);

        JButton computeBtn = new JButton("Compute");
        styleBtn(computeBtn, new Color(80, 200, 100));
        computeBtn.setBounds(10, 55, 130, 40);
        bottomPanel.add(computeBtn);

        JButton backBtn = new JButton("BACK");
        styleBtn(backBtn, new Color(230, 60, 60));
        backBtn.setBounds(155, 55, 130, 40);
        bottomPanel.add(backBtn);

        leftPanel.add(bottomPanel, BorderLayout.SOUTH);

        // ===== RIGHT PANEL =====
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JLabel iterTitle = new JLabel("Iterations", SwingConstants.CENTER);
        iterTitle.setFont(new Font("Times New Roman", 1, 20));
        iterTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        rightPanel.add(iterTitle, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"Iter"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.getTableHeader().setReorderingAllowed(false);
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(THEME);
        table.getTableHeader().setForeground(Color.BLACK);   // dark text on yellow header

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

        resultLabel = new JLabel(" ", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 14));
        resultLabel.setForeground(THEME_DARK);
        resultLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        rightPanel.add(resultLabel, BorderLayout.SOUTH);

        // ===== SPLIT =====
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        split.setDividerLocation(315);
        split.setEnabled(false);
        split.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        add(split, BorderLayout.CENTER);

        // ===== ACTIONS =====
        buildBtn.addActionListener(e -> buildMatrix());
        computeBtn.addActionListener(e -> compute());
        backBtn.addActionListener(e -> { dispose(); MainUI.main(null); });

        buildMatrix();
    }

    private void buildMatrix() {
        n = (int) sizeSpinner.getValue();
        matrixPanel.removeAll();
        matrixPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);

        matrixFields = new JTextField[n][n + 1];

        // Header row
        gbc.gridy = 0;
        for (int j = 0; j <= n; j++) {
            gbc.gridx = j + 1;
            JLabel lbl = new JLabel(j < n ? "x" + (j + 1) : "b", SwingConstants.CENTER);
            lbl.setFont(new Font("Arial", Font.BOLD, 12));
            lbl.setForeground(THEME_DARK);
            lbl.setPreferredSize(new Dimension(50, 25));
            matrixPanel.add(lbl, gbc);
        }

        // Rows
        for (int i = 0; i < n; i++) {
            gbc.gridy = i + 1;

            gbc.gridx = 0;
            JLabel rowLbl = new JLabel("Eq " + (i + 1) + ":", SwingConstants.RIGHT);
            rowLbl.setFont(new Font("Arial", Font.BOLD, 11));
            rowLbl.setForeground(THEME_DARK);
            matrixPanel.add(rowLbl, gbc);

            for (int j = 0; j < n + 1; j++) {
                gbc.gridx = j + 1;
                JTextField tf = new JTextField("0", 4);
                tf.setFont(new Font("Arial", Font.PLAIN, 12));
                tf.setHorizontalAlignment(SwingConstants.CENTER);
                tf.setBorder(BorderFactory.createLineBorder(j == n ? THEME_DARK : THEME));
                tf.setPreferredSize(new Dimension(50, 28));
                matrixFields[i][j] = tf;
                matrixPanel.add(tf, gbc);
            }
        }

        matrixPanel.revalidate();
        matrixPanel.repaint();
    }

    private void compute() {
        model.setRowCount(0);
        resultLabel.setText(" ");

        try {
            double tol = Double.parseDouble(errorField.getText().trim());

            double[][] A = new double[n][n];
            double[]   b = new double[n];

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    A[i][j] = Double.parseDouble(matrixFields[i][j].getText().trim());
                }
                b[i] = Double.parseDouble(matrixFields[i][n].getText().trim());
            }

            // Diagonal dominance check
            for (int i = 0; i < n; i++) {
                double diag = Math.abs(A[i][i]);
                double sum  = 0;
                for (int j = 0; j < n; j++) if (j != i) sum += Math.abs(A[i][j]);
                if (diag < sum) {
                    int choice = JOptionPane.showConfirmDialog(this,
                            "Matrix may not be diagonally dominant — convergence not guaranteed.\nContinue anyway?",
                            "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (choice != JOptionPane.YES_OPTION) return;
                    break;
                }
            }

            // Build columns
            List<String> cols = new ArrayList<>();
            cols.add("Iter");
            for (int i = 0; i < n; i++) cols.add("x" + (i + 1));
            for (int i = 0; i < n; i++) cols.add("EA(x" + (i + 1) + ")");
            model.setColumnIdentifiers(cols.toArray());

            double[] x  = new double[n];   // initial guess = 0
            double[] xOld;
            double[] Ea = new double[n];
            boolean converged;

            for (int iter = 1; iter <= 100; iter++) {
                xOld = x.clone();

                // Seidel: use UPDATED x immediately as each variable is computed
                for (int i = 0; i < n; i++) {
                    double sum = b[i];
                    for (int j = 0; j < n; j++) {
                        if (j != i) sum -= A[i][j] * x[j];  // x[j] already updated for j < i
                    }
                    x[i] = round2(sum / A[i][i]);
                }

                converged = true;
                String[] EaText = new String[n];
                for (int i = 0; i < n; i++) {
                    if (iter == 1) {
                        EaText[i] = "N/A";
                    } else {
                        Ea[i] = round2(Math.abs(x[i] - xOld[i]));
                        EaText[i] = String.format("%.2f", Ea[i]);
                        if (Ea[i] > tol) converged = false;
                    }
                }

                Object[] row = new Object[1 + n + n];
                row[0] = iter;
                for (int i = 0; i < n; i++) row[1 + i]     = String.format("%.2f", x[i]);
                for (int i = 0; i < n; i++) row[1 + n + i] = EaText[i];
                model.addRow(row);

                if (iter > 1 && converged) break;
            }

            StringBuilder sb = new StringBuilder("Solution: ");
            for (int i = 0; i < n; i++) {
                sb.append("x").append(i + 1).append(" = ").append(String.format("%.2f", x[i]));
                if (i < n - 1) sb.append(",  ");
            }
            resultLabel.setText(sb.toString());

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error: Please fill all matrix fields with numbers.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private double round2(double v) { return Math.round(v * 100.0) / 100.0; }

    private void styleBtn(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
    }
}