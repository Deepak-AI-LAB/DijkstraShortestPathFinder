import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * AboutPage.java
 *
 * Project information: algorithm overview, team details, and tech stack.
 * Cards are purely presentational -- no algorithm calls happen here.
 */
public class AboutPage extends JPanel {

    private final Navigator navigator;

    public AboutPage(Navigator navigator) {
        this.navigator = navigator;
        setBackground(AppColors.BG_DARKEST);
        setLayout(new BorderLayout());
        add(buildHeader(), BorderLayout.NORTH);
        add(new JScrollPane(buildBody()) {{
            setBorder(null);
            getViewport().setBackground(AppColors.BG_DARKEST);
            setBackground(AppColors.BG_DARKEST);
        }}, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, AppColors.BG_DARK, getWidth(), 0, AppColors.BG_DARKEST));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(AppColors.BG_MEDIUM);
                g2.fillRect(0, getHeight() - 1, getWidth(), 1);
            }
        };
        header.setPreferredSize(new Dimension(0, 80));
        header.setLayout(new BorderLayout());
        header.setBorder(new EmptyBorder(0, 28, 0, 28));

        JLabel title = new JLabel("About This Project");
        title.setFont(AppColors.FONT_TITLE);
        title.setForeground(AppColors.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Dijkstra's Shortest Path Algorithm — 4th Year Java Mini Project");
        subtitle.setFont(AppColors.FONT_SMALL);
        subtitle.setForeground(AppColors.TEXT_MUTED);

        JPanel stack = new JPanel();
        stack.setOpaque(false);
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
        stack.add(title);
        stack.add(Box.createVerticalStrut(4));
        stack.add(subtitle);
        header.add(stack, BorderLayout.CENTER);

        RoundedButton backBtn = RoundedButton.secondary("← Dashboard");
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 22));
        right.setOpaque(false);
        right.add(backBtn);
        backBtn.addActionListener(e -> navigator.navigate("DASHBOARD"));
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JPanel buildBody() {
        JPanel body = new JPanel();
        body.setBackground(AppColors.BG_DARKEST);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(28, 28, 28, 28));

        body.add(infoCard("About the Algorithm",
            AppColors.TEAL,
            new String[]{
                "Dijkstra's Algorithm, published in 1959 by Edsger W. Dijkstra, finds the",
                "shortest path from a single source node to all other nodes in a weighted graph",
                "with non-negative edge weights. It is a greedy algorithm that always expands",
                "the closest unvisited node first, using a min-heap (PriorityQueue) to track",
                "the current best-known distances. Time complexity: O((V + E) log V)."
            }
        ));
        body.add(Box.createVerticalStrut(16));

        body.add(infoCard("Project Architecture",
            AppColors.BLUE,
            new String[]{
                "Edge.java          — Data class for a single graph connection (destination + weight).",
                "Graph.java         — Adjacency-list graph with input validation and addEdge().",
                "DijkstraSolver.java— Pure algorithm class: no UI, no Scanner, fully reusable.",
                "GraphException.java— Custom checked exception for invalid graph data.",
                "GraphPanel.java    — Swing canvas: draws nodes, edges, and glowing path.",
                "SidebarNav.java    — Persistent navigation panel with active-page highlighting.",
                "MainApp.java       — Root JFrame, CardLayout orchestrator, shared graph state."
            }
        ));
        body.add(Box.createVerticalStrut(16));

        body.add(infoCard("Technology Stack",
            AppColors.AMBER,
            new String[]{
                "Language:    Java 8+  (no external libraries required)",
                "GUI Toolkit: Java Swing (javax.swing, java.awt)",
                "Layout:      CardLayout (page switching) + BorderLayout + GridBagLayout",
                "Algorithm:   Dijkstra with java.util.PriorityQueue (min-heap)",
                "Build:       javac / any Java IDE (IntelliJ, Eclipse, NetBeans, VS Code)"
            }
        ));
        body.add(Box.createVerticalStrut(16));

        body.add(infoCard("Key Design Decisions",
            AppColors.GREEN,
            new String[]{
                "• DijkstraSolver is completely decoupled from the UI — tested independently.",
                "• A custom checked GraphException gives clear, domain-specific error messages.",
                "• The 3-pass glow in GraphPanel creates the route highlight with no libraries.",
                "• RoundedButton overrides paintComponent() for cross-platform rounded corners.",
                "• Navigator interface lets any page trigger navigation without knowing MainApp.",
                "• One shared Graph object is passed by reference — changes propagate instantly."
            }
        ));
        body.add(Box.createVerticalStrut(16));

        body.add(infoCard("How to Run",
            AppColors.TEAL,
            new String[]{
                "1. Compile:  javac -d bin src/*.java",
                "2. GUI app:  java -cp bin MainApp",
                "3. Console:  java -cp bin Main",
                "",
                "Login credentials for the GUI: admin / admin"
            }
        ));

        return body;
    }

    private JPanel infoCard(String title, Color accent, String[] lines) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppColors.BG_DARK);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, 4, getHeight(), 4, 4);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(18, 22, 18, 22));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel headingLbl = new JLabel(title);
        headingLbl.setFont(AppColors.FONT_HEADER);
        headingLbl.setForeground(accent);
        headingLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(headingLbl);
        card.add(Box.createVerticalStrut(12));

        for (String line : lines) {
            JLabel lbl = new JLabel(line.isEmpty() ? " " : line);
            lbl.setFont(AppColors.FONT_BODY);
            lbl.setForeground(line.isEmpty() ? AppColors.TEXT_MUTED : AppColors.TEXT_SECONDARY);
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(lbl);
            card.add(Box.createVerticalStrut(4));
        }
        return card;
    }
}