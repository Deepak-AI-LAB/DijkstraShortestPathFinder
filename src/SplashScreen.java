import javax.swing.*;
import java.awt.*;

/**
 * SplashScreen.java
 *
 * The first thing the user sees. A progress bar animated with
 * javax.swing.Timer advances every 25 ms, filling a teal bar from
 * left to right over roughly 2.5 seconds. Once it reaches 100%,
 * a short 400 ms pause gives the user a moment to read the title,
 * then navigator.navigate("LOGIN") is called automatically.
 *
 * No user interaction needed -- the splash auto-advances.
 */
public class SplashScreen extends JPanel {

    private final Navigator navigator;
    private int  progress  = 0;   // 0..100, drives the loading bar width
    private Timer loadTimer;

    public SplashScreen(Navigator navigator) {
        this.navigator = navigator;
        setBackground(AppColors.BG_DARKEST);
        setLayout(null);  // absolute positioning -- splash layout is bespoke
        startLoading();
    }

    private void startLoading() {
        // Fires every 25 ms, increments progress by 1 each time,
        // so 100 ticks = 2.5 seconds total loading time.
        loadTimer = new Timer(25, e -> {
            progress++;
            repaint();
            if (progress >= 100) {
                loadTimer.stop();
                // Short pause at 100% so the user sees the bar complete
                Timer pause = new Timer(400, ev -> navigator.navigate("LOGIN"));
                pause.setRepeats(false);
                pause.start();
            }
        });
        loadTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;

        // ── Background gradient (top-dark to slightly lighter at bottom) ──
        GradientPaint bgGrad = new GradientPaint(
            0, 0,           AppColors.BG_DARKEST,
            0, getHeight(), new Color(0x1E, 0x29, 0x3B)
        );
        g2.setPaint(bgGrad);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // ── Teal accent line across the top ──
        g2.setColor(AppColors.TEAL);
        g2.setStroke(new BasicStroke(3f));
        g2.drawLine(0, 3, getWidth(), 3);

        // ── App icon ring ──
        int ringR = 48;
        g2.setColor(new Color(0x14, 0xB8, 0xA6, 40));
        g2.fillOval(cx - ringR, cy - ringR - 80, ringR * 2, ringR * 2);
        g2.setColor(AppColors.TEAL);
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawOval(cx - ringR, cy - ringR - 80, ringR * 2, ringR * 2);

        // Draw a minimal "graph" symbol inside the ring: 3 dots + lines
        int[][] pts = {{cx, cy-128},{cx-22, cy-80},{cx+22, cy-80}};
        g2.setColor(new Color(0x14, 0xB8, 0xA6, 180));
        g2.setStroke(new BasicStroke(2f));
        for (int i = 0; i < pts.length; i++) {
            for (int j = i + 1; j < pts.length; j++) {
                g2.drawLine(pts[i][0], pts[i][1], pts[j][0], pts[j][1]);
            }
        }
        g2.setColor(AppColors.AMBER);
        for (int[] pt : pts) g2.fillOval(pt[0]-5, pt[1]-5, 10, 10);

        // ── Title ──
        g2.setFont(new Font("SansSerif", Font.BOLD, 34));
        g2.setColor(AppColors.TEXT_PRIMARY);
        String title = "DIJKSTRA PATH FINDER";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(title, cx - fm.stringWidth(title) / 2, cy - 10);

        // ── Subtitle ──
        g2.setFont(AppColors.FONT_BODY);
        g2.setColor(AppColors.TEXT_SECONDARY);
        String sub = "Shortest Path Algorithm  •  Java Swing  •  4th Year Project";
        fm = g2.getFontMetrics();
        g2.drawString(sub, cx - fm.stringWidth(sub) / 2, cy + 22);

        // ── Loading bar background ──
        int barW = 320, barH = 6;
        int barX = cx - barW / 2, barY = cy + 58;
        g2.setColor(AppColors.BG_PANEL);
        g2.fillRoundRect(barX, barY, barW, barH, barH, barH);

        // ── Loading bar fill (teal, grows with progress) ──
        int fillW = (int)(barW * (progress / 100.0));
        GradientPaint barGrad = new GradientPaint(
            barX, 0, AppColors.TEAL, barX + barW, 0, AppColors.AMBER
        );
        g2.setPaint(barGrad);
        g2.fillRoundRect(barX, barY, Math.max(fillW, barH), barH, barH, barH);

        // ── Percentage label ──
        g2.setFont(AppColors.FONT_SMALL);
        g2.setColor(AppColors.TEXT_MUTED);
        String pct = "Loading... " + progress + "%";
        fm = g2.getFontMetrics();
        g2.drawString(pct, cx - fm.stringWidth(pct) / 2, barY + 24);

        // ── Version line at the very bottom ──
        g2.setFont(AppColors.FONT_SMALL);
        g2.setColor(AppColors.TEXT_MUTED);
        String ver = "v2.0  •  Powered by Dijkstra's Algorithm";
        fm = g2.getFontMetrics();
        g2.drawString(ver, cx - fm.stringWidth(ver) / 2, getHeight() - 20);
    }
}