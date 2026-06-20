import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * SidebarNav.java
 *
 * The persistent left navigation panel that appears on every inner
 * page (Dashboard, Route Finder, Graph View, About). It holds:
 *   - A logo / project name area at the top
 *   - One nav button per inner page, each highlighting when active
 *   - A "Logout" button at the very bottom
 *
 * Active-page highlighting works by calling setActivePage(id): the
 * nav item whose id matches gets the teal-left-border treatment;
 * all others go back to their default dark background.
 */
public class SidebarNav extends JPanel {

    private final Navigator navigator;
    private static final int WIDTH = 220;

    // Keep references so setActivePage() can update their appearance
    private final NavButton[] navButtons;
    private static final String[] PAGE_IDS    = { "DASHBOARD", "ROUTE", "GRAPH", "ABOUT" };
    private static final String[] PAGE_LABELS = { "⬡  Dashboard", "⇥  Route Finder",
                                                   "◉  Graph View", "ℹ  About Project" };

    public SidebarNav(Navigator navigator) {
        this.navigator = navigator;
        setPreferredSize(new Dimension(WIDTH, 0));
        setBackground(AppColors.BG_DARK);
        setLayout(new BorderLayout());

        add(buildLogoArea(), BorderLayout.NORTH);

        navButtons = new NavButton[PAGE_IDS.length];
        JPanel navArea = new JPanel();
        navArea.setBackground(AppColors.BG_DARK);
        navArea.setLayout(new BoxLayout(navArea, BoxLayout.Y_AXIS));
        navArea.setBorder(new EmptyBorder(10, 0, 10, 0));
        for (int i = 0; i < PAGE_IDS.length; i++) {
            navButtons[i] = new NavButton(PAGE_LABELS[i], PAGE_IDS[i]);
            navArea.add(navButtons[i]);
        }
        add(navArea, BorderLayout.CENTER);
        add(buildLogoutArea(), BorderLayout.SOUTH);
    }

    /** Highlights the nav button for the given pageId and un-highlights all others. */
    public void setActivePage(String pageId) {
        for (NavButton btn : navButtons) {
            btn.setActive(btn.pageId.equals(pageId));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Subtle right border to separate sidebar from main content
        g.setColor(AppColors.BG_MEDIUM);
        g.fillRect(getWidth() - 1, 0, 1, getHeight());
    }

    // ── Logo area ─────────────────────────────────────────────────────

    private JPanel buildLogoArea() {
        JPanel area = new JPanel();
        area.setBackground(AppColors.BG_DARKEST);
        area.setLayout(new BoxLayout(area, BoxLayout.Y_AXIS));
        area.setBorder(new EmptyBorder(22, 18, 22, 18));

        // Teal accent circle with initials
        JPanel badge = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppColors.TEAL);
                g2.fillRoundRect(0, 0, 38, 38, 10, 10);
                g2.setFont(new Font("SansSerif", Font.BOLD, 18));
                g2.setColor(Color.WHITE);
                g2.drawString("DP", 6, 27);
                g2.dispose();
            }
        };
        badge.setOpaque(false);
        badge.setPreferredSize(new Dimension(38, 38));
        badge.setMaximumSize(new Dimension(38, 38));
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel appName = new JLabel("Dijkstra");
        appName.setFont(new Font("SansSerif", Font.BOLD, 17));
        appName.setForeground(AppColors.TEXT_PRIMARY);
        appName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tagLine = new JLabel("Path Finder");
        tagLine.setFont(AppColors.FONT_SMALL);
        tagLine.setForeground(AppColors.TEAL);
        tagLine.setAlignmentX(Component.LEFT_ALIGNMENT);

        area.add(badge);
        area.add(Box.createVerticalStrut(10));
        area.add(appName);
        area.add(tagLine);

        return area;
    }

    // ── Logout area ───────────────────────────────────────────────────

    private JPanel buildLogoutArea() {
        JPanel area = new JPanel(new BorderLayout());
        area.setBackground(AppColors.BG_DARK);
        area.setBorder(new EmptyBorder(10, 12, 20, 12));

        // Thin separator line above logout
        JPanel sep = new JPanel();
        sep.setBackground(AppColors.BG_MEDIUM);
        sep.setPreferredSize(new Dimension(0, 1));

        RoundedButton logoutBtn = new RoundedButton("⏻  Log Out",
            AppColors.BG_MEDIUM, AppColors.BG_PANEL, AppColors.BG_DARKEST, 10);
        logoutBtn.setForeground(AppColors.TEXT_SECONDARY);
        logoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        logoutBtn.addActionListener(e -> navigator.navigate("LOGIN"));

        area.add(sep,       BorderLayout.NORTH);
        area.add(logoutBtn, BorderLayout.CENTER);
        return area;
    }

    // ── NavButton inner class ─────────────────────────────────────────

    /**
     * A sidebar navigation item. When active, a 3px teal bar appears on
     * the left edge and the text turns bright white. When inactive, the
     * background is the sidebar color and text is muted.
     */
    private class NavButton extends JPanel {
        final String pageId;
        private boolean active = false;
        private boolean hovered = false;
        private final JLabel label;

        NavButton(String text, String pageId) {
            this.pageId = pageId;
            setLayout(new BorderLayout());
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
            setPreferredSize(new Dimension(WIDTH, 48));

            label = new JLabel(text);
            label.setFont(AppColors.FONT_BODY);
            label.setForeground(AppColors.TEXT_SECONDARY);
            label.setBorder(new EmptyBorder(0, 22, 0, 0));
            add(label, BorderLayout.CENTER);

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                @Override public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
                @Override public void mouseClicked(MouseEvent e) {
                    navigator.navigate(pageId);
                }
            });
        }

        void setActive(boolean active) {
            this.active = active;
            label.setForeground(active ? AppColors.TEXT_PRIMARY : AppColors.TEXT_SECONDARY);
            label.setFont(active
                ? new Font("SansSerif", Font.BOLD, 14)
                : AppColors.FONT_BODY);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            // Background fill
            if (active) {
                g2.setColor(new Color(0x14, 0xB8, 0xA6, 25));
            } else if (hovered) {
                g2.setColor(new Color(0xFF, 0xFF, 0xFF, 8));
            } else {
                g2.setColor(new Color(0, 0, 0, 0));
            }
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Active left bar
            if (active) {
                g2.setColor(AppColors.TEAL);
                g2.fillRect(0, 8, 3, getHeight() - 16);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }
}