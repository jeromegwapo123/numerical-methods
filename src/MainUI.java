import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MainUI {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Numerical Methods");
        frame.setSize(800, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // ================= BACKGROUND IMAGE =================
        Image img = new ImageIcon(
                new File("out/source/coco.jpg").getAbsolutePath()
        ).getImage();

        JPanel background = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        };

        background.setLayout(null);
        frame.setContentPane(background);

        // ================= TITLE =================
        JLabel title = new JLabel("Numerical Methods", SwingConstants.CENTER);
        title.setBounds(200, 30, 400, 40);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        background.add(title);

        // ================= BUTTONS =================
        JButton bisectionBtn = createButton("Bisection", new Color(173, 216, 230));
        JButton regulaBtn = createButton("Regula-Falsi", new Color(144, 238, 144));
        JButton newtonBtn = createButton("Newton", new Color(255, 228, 181));
        JButton secantBtn = createButton("Secant", new Color(255, 182, 193));

        JButton jacobiBtn = createButton("Gauss-Jacobi", new Color(221, 160, 221));
        JButton seidelBtn = createButton("Gauss-Seidel", new Color(240, 230, 140));
        JButton simpsonBtn = createButton("Simpson's Rule", new Color(176, 224, 230));
        JButton trapezoidalBtn = createButton("Trapezoidal Rule", new Color(250, 200, 150));

        bisectionBtn.setBounds(100, 120, 130, 80);
        regulaBtn.setBounds(250, 120, 130, 80);
        newtonBtn.setBounds(400, 120, 130, 80);
        secantBtn.setBounds(550, 120, 130, 80);

        jacobiBtn.setBounds(100, 230, 130, 80);
        seidelBtn.setBounds(250, 230, 130, 80);
        simpsonBtn.setBounds(400, 230, 130, 80);
        trapezoidalBtn.setBounds(550, 230, 130, 80);

        background.add(bisectionBtn);
        background.add(regulaBtn);
        background.add(newtonBtn);
        background.add(secantBtn);
        background.add(jacobiBtn);
        background.add(seidelBtn);
        background.add(simpsonBtn);
        background.add(trapezoidalBtn);

        // ================= ACTIONS =================
        bisectionBtn.addActionListener(e -> {
            frame.dispose();
            new Bisection().setVisible(true);
        });

        regulaBtn.addActionListener(e -> {
            frame.dispose();
            new RegulaFalsi().setVisible(true);
        });


        newtonBtn.addActionListener(e -> open(frame, "Newton Method"));
        secantBtn.addActionListener(e -> open(frame, "Secant Method"));
        jacobiBtn.addActionListener(e -> open(frame, "Gauss-Jacobi Method"));
        seidelBtn.addActionListener(e -> open(frame, "Gauss-Seidel Method"));
        simpsonBtn.addActionListener(e -> open(frame, "Simpson's Rule"));
        trapezoidalBtn.addActionListener(e -> open(frame, "Trapezoidal Rule"));

        frame.setVisible(true);
    }

    // ================= BUTTON STYLE =================
    public static JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        return btn;
    }

    // ================= OPEN WINDOW =================
    public static void open(JFrame currentFrame, String titleText) {

        currentFrame.dispose();

        JFrame newFrame = new JFrame(titleText);
        newFrame.setSize(400, 300);
        newFrame.setLayout(new BorderLayout());
        newFrame.setLocationRelativeTo(null);

        JLabel label = new JLabel(titleText, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));

        JButton backBtn = new JButton("Back");

        backBtn.addActionListener(e -> {
            newFrame.dispose();
            MainUI.main(null);
        });

        newFrame.add(label, BorderLayout.CENTER);
        newFrame.add(backBtn, BorderLayout.SOUTH);

        newFrame.setVisible(true);
    }
}