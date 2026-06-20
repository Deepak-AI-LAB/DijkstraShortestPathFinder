import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * LoginPage.java
 *
 * A centered login card on a dark gradient background.
 * The credential check here is intentionally simple (admin/admin)
 * since this is a graph-algorithm project, not an auth system.
 * The purpose of the login screen is to demonstrate multi-page
 * navigation and UI polish, not real security.
 *
 * Accepted credentials:  username = admin   password = admin
 * (Hint is shown below the input fields so the evaluator can log in
 * immediately during a demo without fumbling.)
 */
public class LoginPage extends JPanel {

    private final Navigator   navigator;
    private       JTextField  usernameField;
    private       JPasswordField passwordField;
    private       JLabel      errorLabel;

    public LoginPage(Navigator navigator) {
        this.navigator = navigator;
        setLayout(new GridBagLayout()); // centers the card both vertically and horizontally
        setBackground(AppColors.BG_DARKEST);
        add(buildCard());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Subtle diagonal gradient behind the card to add depth
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(new GradientPaint(
            0, 0, AppColors.BG_DARKEST,
            getWidth(), getHeight(), new Color(0x1E, 0x29, 0x3B)
        ));
        g2.fillRect(0, 0, getWidth(), getHeight());
        // Teal accent lines
        g2.setColor(AppColors.TEAL);
        g2.setStroke(new BasicStroke(3f));
        g2.drawLine(0, 3, getWidth(), 3);
        g2.drawLine(0, getHeight() - 3, getWidth(), getHeight() - 3);
    }

    /** Builds the white-bordered login card placed in the center. */
    private JPanel buildCard() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppColors.BG_DARK);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(AppColors.BG_PANEL);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(380, 460));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(36, 36, 36, 36));

        // ── Icon placeholder (a teal circle with a "D" glyph) ──
        JLabel iconLabel = new JLabel("D") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppColors.TEAL);
                g2.fillOval(0, 0, 60, 60);
                g2.setFont(new Font("SansSerif", Font.BOLD, 28));
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("D", (60 - fm.stringWidth("D")) / 2, 42);
                g2.dispose();
            }
        };
        iconLabel.setPreferredSize(new Dimension(60, 60));
        iconLabel.setMaximumSize(new Dimension(60, 60));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = styledLabel("Welcome Back", AppColors.FONT_TITLE, AppColors.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = styledLabel("Sign in to Dijkstra Path Finder", AppColors.FONT_SMALL, AppColors.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Username field ──
        JLabel userLbl = styledLabel("Username", AppColors.FONT_LABEL, AppColors.TEXT_SECONDARY);
        userLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernameField = styledTextField("admin");

        // ── Password field ──
        JLabel passLbl = styledLabel("Password", AppColors.FONT_LABEL, AppColors.TEXT_SECONDARY);
        passLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField = new JPasswordField();
        passwordField.setText("admin");
        styleInputField(passwordField);
        // Allow pressing Enter in password field to trigger login
        passwordField.addActionListener(e -> attemptLogin());

        // ── Error message (hidden until needed) ──
        errorLabel = styledLabel("", AppColors.FONT_SMALL, AppColors.RED);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Login button ──
        RoundedButton loginBtn = RoundedButton.primary("Sign In  →");
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.addActionListener(e -> attemptLogin());

        // ── Hint ──
        JLabel hintLabel = styledLabel("Demo credentials:  admin / admin", AppColors.FONT_SMALL, AppColors.TEXT_MUTED);
        hintLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Assemble card in vertical order
        card.add(iconLabel);
        card.add(Box.createVerticalStrut(16));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(subtitleLabel);
        card.add(Box.createVerticalStrut(28));
        card.add(userLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(14));
        card.add(passLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(10));
        card.add(errorLabel);
        card.add(Box.createVerticalStrut(20));
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(16));
        card.add(hintLabel);

        return card;
    }

    private void attemptLogin() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword()).trim();

        if (user.equals("admin") && pass.equals("admin")) {
            navigator.navigate("MAIN");
        } else if (user.isEmpty() || pass.isEmpty()) {
            errorLabel.setText("Please enter both username and password.");
        } else {
            errorLabel.setText("Incorrect credentials. Try admin / admin.");
            passwordField.setText("");
        }
    }

    // ── Factory helpers ───────────────────────────────────────────────

    private JLabel styledLabel(String text, Font font, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        lbl.setForeground(color);
        return lbl;
    }

    private JTextField styledTextField(String placeholder) {
        JTextField f = new JTextField(placeholder);
        styleInputField(f);
        return f;
    }

    private void styleInputField(JTextField f) {
        f.setBackground(AppColors.BG_PANEL);
        f.setForeground(AppColors.TEXT_PRIMARY);
        f.setCaretColor(AppColors.TEAL);
        f.setFont(AppColors.FONT_BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.BG_MEDIUM, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
    }
}