import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * RoundedButton.java
 *
 * A JButton subclass that draws itself as a pill-shaped rounded
 * rectangle. Standard Swing buttons look flat and OS-default;
 * this one gives a consistent, polished appearance across all
 * platforms because it paints its own background instead of
 * delegating to the look-and-feel.
 *
 * Hover effect: the background lightens/darkens slightly when the
 * mouse enters, giving the feel of a responsive interactive element
 * without needing any animation library.
 */
public class RoundedButton extends JButton {

    private final Color normalColor;
    private final Color hoverColor;
    private final Color pressColor;
    private final int   arcRadius;

    // Most buttons in this app use one of two presets: primary (teal)
    // or secondary (slate). The static factory methods below make
    // creating them concise at call sites.
    public static RoundedButton primary(String text) {
        return new RoundedButton(text, AppColors.TEAL, AppColors.TEAL_DARK,
                new Color(0x0A, 0x70, 0x65), 14);
    }

    public static RoundedButton secondary(String text) {
        return new RoundedButton(text, AppColors.BG_PANEL, AppColors.BG_MEDIUM,
                AppColors.BG_DARK, 14);
    }

    public static RoundedButton danger(String text) {
        return new RoundedButton(text, AppColors.RED, new Color(0xCC, 0x22, 0x22),
                new Color(0xAA, 0x11, 0x11), 14);
    }

    public static RoundedButton amber(String text) {
        return new RoundedButton(text, AppColors.AMBER, AppColors.AMBER_DARK,
                new Color(0xB4, 0x5A, 0x04), 14);
    }

    /** Full constructor -- use the static factories above for the common cases. */
    public RoundedButton(String text, Color normal, Color hover, Color press, int arc) {
        super(text);
        this.normalColor = normal;
        this.hoverColor  = hover;
        this.pressColor  = press;
        this.arcRadius   = arc;

        // Turn off the default Swing painting so our paintComponent
        // takes full control of the button's appearance.
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);

        setForeground(AppColors.TEXT_PRIMARY);
        setFont(AppColors.FONT_BUTTON);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBackground(normalColor);

        // Swap background color on mouse enter/exit/press to simulate
        // the hover animation. repaint() triggers paintComponent.
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { setBackground(hoverColor);  repaint(); }
            @Override public void mouseExited (MouseEvent e) { setBackground(normalColor); repaint(); }
            @Override public void mousePressed(MouseEvent e) { setBackground(pressColor);  repaint(); }
            @Override public void mouseReleased(MouseEvent e){ setBackground(hoverColor);  repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fill the rounded rectangle FIRST, then let super draw the
        // text on top. super.paintComponent draws the label at the
        // correct position, so we don't need to measure text ourselves.
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arcRadius, arcRadius);
        g2.dispose();

        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        // No border -- the rounded fill is the visual boundary.
    }
}