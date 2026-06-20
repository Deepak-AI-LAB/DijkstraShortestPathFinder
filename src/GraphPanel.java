import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.List;

public class GraphPanel extends JPanel {

    private Graph        graph;
    private List<Integer> highlightedPath;
    private int           highlightSource      = -1;
    private int           highlightDestination = -1;
    private int           highlightDistance    = -1;

    // Node radius scales down slightly with more cities so they don't crowd each other
    private static final int MAX_RADIUS = 28;
    private static final int MIN_RADIUS = 20;

    public GraphPanel() {
        setBackground(AppColors.CANVAS_BG);
        setPreferredSize(new Dimension(540, 540));
    }

    public void setGraph(Graph g) {
        this.graph = g;
        clearHighlight();
        repaint();
    }

    public void highlightPath(List<Integer> path, int source, int dest, int distance) {
        this.highlightedPath      = path;
        this.highlightSource      = source;
        this.highlightDestination = dest;
        this.highlightDistance    = distance;
        repaint();
    }

    public void clearHighlight() {
        highlightedPath      = null;
        highlightSource      = -1;
        highlightDestination = -1;
        highlightDistance    = -1;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,   RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (graph == null || graph.numVertices == 0) {
            drawEmptyState(g2);
            return;
        }

        Point[] pos    = computeNodePositions();
        int     radius = nodeRadius();

        drawEdges(g2, pos);
        drawGlowPath(g2, pos);
        drawNodes(g2, pos, radius);
        drawLegend(g2);
        if (highlightDistance >= 0) drawDistanceBadge(g2);
    }

    // ── Layout ───────────────────────────────────────────────────────

    private int nodeRadius() {
        if (graph == null || graph.numVertices == 0) return MAX_RADIUS;
        return Math.max(MIN_RADIUS, Math.min(MAX_RADIUS, 240 / graph.numVertices));
    }

    /**
     * Arranges cities evenly around a circle, starting at the top (−90°).
     * The ring radius is sized to leave room for name labels pushed outward
     * beyond the node circles.
     */
    private Point[] computeNodePositions() {
        int n   = graph.numVertices;
        int cx  = getWidth()  / 2;
        int cy  = getHeight() / 2;
        int r   = Math.min(cx, cy) - nodeRadius() - 52;
        if (r < 50) r = 50;

        Point[] pos = new Point[n];
        for (int i = 0; i < n; i++) {
            double angle = (2 * Math.PI * i / n) - (Math.PI / 2.0);
            pos[i] = new Point(cx + (int)(r * Math.cos(angle)),
                               cy + (int)(r * Math.sin(angle)));
        }
        return pos;
    }

    // ── Drawing passes ────────────────────────────────────────────────

    private void drawEdges(Graphics2D g2, Point[] pos) {
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int[] e : graph.getEdgeRecords()) {
            Point a = pos[e[0]], b = pos[e[1]];
            g2.setColor(AppColors.EDGE_DEFAULT);
            g2.drawLine(a.x, a.y, b.x, b.y);

            // Distance label at midpoint, offset slightly off the line
            int mx = (a.x + b.x) / 2;
            int my = (a.y + b.y) / 2;
            // Perpendicular nudge so label sits beside the line, not on it
            double dx = b.x - a.x, dy = b.y - a.y;
            double len = Math.max(1, Math.hypot(dx, dy));
            int nx = (int)(-dy / len * 10);
            int ny = (int)( dx / len * 10);

            g2.setFont(new Font("SansSerif", Font.BOLD, 11));
            String lbl = e[2] + " km";
            FontMetrics fm = g2.getFontMetrics();
            int lx = mx + nx - fm.stringWidth(lbl) / 2;
            int ly = my + ny + fm.getAscent() / 2;

            // Small dark pill behind the label for readability
            g2.setColor(new Color(0x0A, 0x10, 0x1E, 200));
            g2.fillRoundRect(lx - 3, ly - fm.getAscent(), fm.stringWidth(lbl) + 6, fm.getHeight(), 4, 4);
            g2.setColor(AppColors.TEXT_SECONDARY);
            g2.drawString(lbl, lx, ly);
        }
    }

    /** Three-pass amber glow for the shortest path edges. */
    private void drawGlowPath(Graphics2D g2, Point[] pos) {
        if (highlightedPath == null || highlightedPath.size() < 2) return;
        int[][]   passes = {{16, 45}, {9, 110}, {4, 255}};
        for (int[] p : passes) {
            g2.setColor(new Color(245, 158, 11, p[1]));
            g2.setStroke(new BasicStroke(p[0], BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (int i = 0; i < highlightedPath.size() - 1; i++) {
                Point a = pos[highlightedPath.get(i)];
                Point b = pos[highlightedPath.get(i + 1)];
                g2.drawLine(a.x, a.y, b.x, b.y);
            }
        }
    }

    private void drawNodes(Graphics2D g2, Point[] pos, int r) {
        int n   = graph.numVertices;
        int cx  = getWidth()  / 2;
        int cy  = getHeight() / 2;
        int rng = Math.min(cx, cy) - r - 52;
        if (rng < 50) rng = 50;

        for (int i = 0; i < pos.length; i++) {
            Point p    = pos[i];
            Color fill = nodeColor(i);

            // Drop shadow
            g2.setColor(new Color(0, 0, 0, 90));
            g2.fillOval(p.x - r + 2, p.y - r + 3, r * 2, r * 2);

            // Circle fill + border
            Ellipse2D circle = new Ellipse2D.Double(p.x - r, p.y - r, r * 2.0, r * 2.0);
            g2.setColor(fill);
            g2.fill(circle);
            boolean special = (i == highlightSource || i == highlightDestination);
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(special ? 3f : 1.5f));
            g2.draw(circle);

            // Abbreviation inside the circle (first 3 chars, upper-case)
            String name  = graph.getNodeName(i);
            String abbr  = name.length() > 3 ? name.substring(0, 3).toUpperCase() : name.toUpperCase();
            g2.setFont(new Font("SansSerif", Font.BOLD, r > 24 ? 12 : 10));
            g2.setColor(Color.WHITE);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(abbr, p.x - fm.stringWidth(abbr) / 2, p.y + fm.getAscent() / 2 - 2);

            // Full city name pushed radially outward beyond the circle
            double angle = Math.atan2(p.y - cy, p.x - cx);
            int labelDist = rng + r + 18;
            int lx = cx + (int)(labelDist * Math.cos(angle));
            int ly = cy + (int)(labelDist * Math.sin(angle));

            g2.setFont(new Font("SansSerif", Font.BOLD, 11));
            fm = g2.getFontMetrics();
            int lw = fm.stringWidth(name);

            // Slightly transparent dark pill so label reads on any edge
            g2.setColor(new Color(0x0A, 0x10, 0x1E, 180));
            g2.fillRoundRect(lx - lw / 2 - 4, ly - fm.getAscent() - 1, lw + 8, fm.getHeight() + 2, 6, 6);

            Color nameColor = special ? fill : (highlightedPath != null && highlightedPath.contains(i)
                                                 ? AppColors.AMBER : AppColors.TEXT_PRIMARY);
            g2.setColor(nameColor);
            g2.drawString(name, lx - lw / 2, ly);
        }
    }

    private Color nodeColor(int i) {
        if (i == highlightSource)       return AppColors.GREEN;
        if (i == highlightDestination)  return AppColors.RED;
        if (highlightedPath != null && highlightedPath.contains(i)) return AppColors.AMBER;
        return AppColors.NODE_DEFAULT;
    }

    private void drawLegend(Graphics2D g2) {
        int x = 12, y = getHeight() - 106, w = 170, h = 94;
        g2.setColor(new Color(0x0F, 0x17, 0x2A, 215));
        g2.fillRoundRect(x, y, w, h, 10, 10);
        g2.setColor(AppColors.BG_PANEL);
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(x, y, w, h, 10, 10);
        g2.setFont(AppColors.FONT_LABEL);
        g2.setColor(AppColors.TEXT_MUTED);
        g2.drawString("LEGEND", x + 10, y + 18);
        drawLegendItem(g2, AppColors.GREEN,         "Start City",      x + 10, y + 38);
        drawLegendItem(g2, AppColors.RED,           "Destination City", x + 10, y + 58);
        drawLegendItem(g2, AppColors.AMBER,         "Shortest Route",   x + 10, y + 78);
    }

    private void drawLegendItem(Graphics2D g2, Color c, String label, int x, int y) {
        g2.setColor(c);
        g2.fillOval(x, y - 9, 12, 12);
        g2.setColor(AppColors.TEXT_PRIMARY);
        g2.setFont(AppColors.FONT_SMALL);
        g2.drawString(label, x + 18, y);
    }

    private void drawDistanceBadge(Graphics2D g2) {
        String text = "Shortest: " + highlightDistance + " km";
        g2.setFont(new Font("SansSerif", Font.BOLD, 13));
        FontMetrics fm = g2.getFontMetrics();
        int w = fm.stringWidth(text) + 22, h = 32;
        int x = getWidth() - w - 10, y = 10;
        g2.setColor(new Color(0x0F, 0x17, 0x2A, 215));
        g2.fillRoundRect(x, y, w, h, 10, 10);
        g2.setColor(AppColors.AMBER);
        g2.drawString(text, x + 11, y + 21);
    }

    private void drawEmptyState(Graphics2D g2) {
        g2.setColor(AppColors.TEXT_MUTED);
        g2.setFont(AppColors.FONT_BODY);
        String msg1 = "No cities in the graph yet.";
        String msg2 = "Use the controls to add cities and routes.";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(msg1, (getWidth() - fm.stringWidth(msg1)) / 2, getHeight() / 2 - 12);
        g2.drawString(msg2, (getWidth() - fm.stringWidth(msg2)) / 2, getHeight() / 2 + 12);
    }
}