import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class DashboardPage extends JPanel {

    private final Navigator navigator;
    private final Graph     graph;

    private JLabel   nodeCountLabel;
    private JLabel   edgeCountLabel;
    private JTextArea cityListArea;

    public DashboardPage(Navigator navigator, Graph graph) {
        this.navigator = navigator;
        this.graph     = graph;
        setBackground(AppColors.BG_DARKEST);
        setLayout(new BorderLayout());
        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(),   BorderLayout.CENTER);
    }

    public void refreshStats() {
        if (nodeCountLabel != null) {
            nodeCountLabel.setText(String.valueOf(graph.numVertices));
            edgeCountLabel.setText(String.valueOf(graph.getEdgeRecords().size()));
        }
        if (cityListArea != null) {
            List<String> names = graph.getNodeNames();
            if (names.isEmpty()) {
                cityListArea.setText("(No cities loaded. Go to Graph View.)");
            } else {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < names.size(); i++)
                    sb.append(String.format("  %2d.  %s%n", i, names.get(i)));
                cityListArea.setText(sb.toString());
                cityListArea.setCaretPosition(0);
            }
        }
    }

    // ── Header ────────────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel h = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, AppColors.BG_DARK, getWidth(), 0, AppColors.BG_DARKEST));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(AppColors.BG_MEDIUM);
                g2.fillRect(0, getHeight() - 1, getWidth(), 1);
            }
        };
        h.setPreferredSize(new Dimension(0, 80));
        h.setLayout(new BorderLayout());
        h.setBorder(new EmptyBorder(0, 28, 0, 28));

        JLabel title    = new JLabel("Dashboard");
        title.setFont(AppColors.FONT_TITLE);
        title.setForeground(AppColors.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Indian city road network — Dijkstra shortest path finder");
        subtitle.setFont(AppColors.FONT_SMALL);
        subtitle.setForeground(AppColors.TEXT_MUTED);

        JPanel stack = new JPanel();
        stack.setOpaque(false);
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
        stack.add(title);
        stack.add(Box.createVerticalStrut(4));
        stack.add(subtitle);
        h.add(stack, BorderLayout.CENTER);
        return h;
    }

    // ── Body ──────────────────────────────────────────────────────────

    private JPanel buildBody() {
        JPanel body = new JPanel();
        body.setBackground(AppColors.BG_DARKEST);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(24, 28, 24, 28));

        // ── Metric cards row ──
        JPanel cardsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        cardsRow.setOpaque(false);
        cardsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        cardsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        nodeCountLabel = new JLabel(String.valueOf(graph.numVertices));
        edgeCountLabel = new JLabel(String.valueOf(graph.getEdgeRecords().size()));
        JLabel algoLabel   = new JLabel("Dijkstra");
        JLabel weightLabel = new JLabel("Km");

        cardsRow.add(metricCard("Cities",    nodeCountLabel, AppColors.TEAL));
        cardsRow.add(metricCard("Routes",    edgeCountLabel, AppColors.BLUE));
        cardsRow.add(metricCard("Algorithm", algoLabel,      AppColors.AMBER));
        cardsRow.add(metricCard("Unit",      weightLabel,    AppColors.GREEN));

        // ── Quick actions ──
        JPanel actionsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        actionsRow.setOpaque(false);
        actionsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        actionsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundedButton findBtn  = RoundedButton.primary("⇥  Find Shortest Route");
        RoundedButton graphBtn = RoundedButton.secondary("◉  Graph View");
        RoundedButton aboutBtn = RoundedButton.secondary("ℹ  About");
        findBtn .setPreferredSize(new Dimension(210, 40));
        graphBtn.setPreferredSize(new Dimension(140, 40));
        aboutBtn.setPreferredSize(new Dimension(120, 40));
        findBtn .addActionListener(e -> navigator.navigate("ROUTE"));
        graphBtn.addActionListener(e -> navigator.navigate("GRAPH"));
        aboutBtn.addActionListener(e -> navigator.navigate("ABOUT"));
        actionsRow.add(findBtn);
        actionsRow.add(graphBtn);
        actionsRow.add(aboutBtn);

        // ── City list + instructions (side by side) ──
        JPanel lowerRow = new JPanel(new GridLayout(1, 2, 16, 0));
        lowerRow.setOpaque(false);
        lowerRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        lowerRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        lowerRow.add(buildCityListCard());
        lowerRow.add(buildInfoCard());

        body.add(sectionLabel("Network Statistics"));
        body.add(Box.createVerticalStrut(12));
        body.add(cardsRow);
        body.add(Box.createVerticalStrut(24));
        body.add(sectionLabel("Quick Actions"));
        body.add(Box.createVerticalStrut(12));
        body.add(actionsRow);
        body.add(Box.createVerticalStrut(24));
        body.add(sectionLabel("Network Overview"));
        body.add(Box.createVerticalStrut(12));
        body.add(lowerRow);

        return body;
    }

    // ── City list card ────────────────────────────────────────────────

    private JPanel buildCityListCard() {
        JPanel card = roundCard();
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel heading = new JLabel("CITIES IN NETWORK");
        heading.setFont(AppColors.FONT_LABEL);
        heading.setForeground(AppColors.TEAL);
        card.add(heading, BorderLayout.NORTH);

        cityListArea = new JTextArea();
        cityListArea.setEditable(false);
        cityListArea.setBackground(AppColors.BG_MEDIUM);
        cityListArea.setForeground(AppColors.TEXT_SECONDARY);
        cityListArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        cityListArea.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane sp = new JScrollPane(cityListArea);
        sp.setBorder(BorderFactory.createLineBorder(AppColors.BG_PANEL, 1));
        sp.getViewport().setBackground(AppColors.BG_MEDIUM);
        card.add(sp, BorderLayout.CENTER);

        // Populate immediately
        refreshStats();
        return card;
    }

    // ── How-to-use card ───────────────────────────────────────────────

    private JPanel buildInfoCard() {
        JPanel card = roundCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(18, 20, 18, 20));

        JLabel heading = new JLabel("HOW TO USE");
        heading.setFont(AppColors.FONT_LABEL);
        heading.setForeground(AppColors.TEAL);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(heading);
        card.add(Box.createVerticalStrut(12));

        String[] steps = {
            "1.  Open Graph View to see the city network.",
            "2.  Add new cities with the Add City panel.",
            "3.  Connect cities by adding routes with distances.",
            "4.  Open Route Finder and pick two cities.",
            "5.  Click Find Shortest Route — Dijkstra will",
            "     calculate the minimum-distance path instantly.",
            "6.  The glowing route appears on the map canvas."
        };
        for (String s : steps) {
            JLabel l = new JLabel(s);
            l.setFont(AppColors.FONT_BODY);
            l.setForeground(AppColors.TEXT_SECONDARY);
            l.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(l);
            card.add(Box.createVerticalStrut(5));
        }
        return card;
    }

    // ── Component factories ───────────────────────────────────────────

    private JPanel metricCard(String heading, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppColors.BG_MEDIUM);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, 4, getHeight(), 4, 4);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(14, 20, 14, 14));

        JLabel hLbl = new JLabel(heading.toUpperCase());
        hLbl.setFont(AppColors.FONT_LABEL);
        hLbl.setForeground(AppColors.TEXT_MUTED);
        hLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
        valueLabel.setForeground(accent);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(hLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(valueLabel);
        return card;
    }

    private JPanel roundCard() {
        JPanel c = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppColors.BG_DARK);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
            }
        };
        c.setOpaque(false);
        return c;
    }

    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text.toUpperCase());
        l.setFont(AppColors.FONT_LABEL);
        l.setForeground(AppColors.TEXT_MUTED);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }
}