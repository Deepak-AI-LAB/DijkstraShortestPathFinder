import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class RouteFinderPage extends JPanel {

    private final Navigator  navigator;
    private final Graph      graph;
    private final GraphPanel graphPanel;

    private JComboBox<String> sourceCombo;
    private JComboBox<String> destCombo;
    private JLabel            resultHeading;
    private JLabel            distanceLabel;
    private JLabel            distUnitLabel;
    private JLabel            pathLabel;
    private JTextArea         logArea;

    public RouteFinderPage(Navigator navigator, Graph graph) {
        this.navigator  = navigator;
        this.graph      = graph;
        this.graphPanel = new GraphPanel();
        this.graphPanel.setGraph(graph);

        setBackground(AppColors.BG_DARKEST);
        setLayout(new BorderLayout());
        add(buildHeader(),  BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
    }

    /** Syncs comboboxes and the graph canvas after the graph changes. */
    public void onGraphUpdated() {
        graphPanel.setGraph(graph);
        graphPanel.clearHighlight();
        refreshCombos();
        appendLog("Graph updated — " + graph.numVertices + " cities, " +
                  graph.getEdgeRecords().size() + " routes.");
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

        JLabel title    = new JLabel("Route Finder");
        title.setFont(AppColors.FONT_TITLE);
        title.setForeground(AppColors.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Find the shortest driving route between two cities using Dijkstra's Algorithm");
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

    // ── Split layout ──────────────────────────────────────────────────

    private JSplitPane buildContent() {
        JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            wrapGraph(), buildControlPanel());
        sp.setResizeWeight(0.62);
        sp.setDividerLocation(530);
        sp.setDividerSize(4);
        sp.setBorder(null);
        sp.setBackground(AppColors.BG_DARKEST);
        return sp;
    }

    private JPanel wrapGraph() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(AppColors.CANVAS_BG);
        p.setBorder(BorderFactory.createLineBorder(AppColors.BG_MEDIUM, 1));
        p.add(graphPanel, BorderLayout.CENTER);
        return p;
    }

    // ── Right control panel ───────────────────────────────────────────

    private JPanel buildControlPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(AppColors.BG_DARKEST);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 14, 14, 14));

        panel.add(buildInputCard());
        panel.add(Box.createVerticalStrut(14));
        panel.add(buildResultCard());
        panel.add(Box.createVerticalStrut(14));
        panel.add(buildLogCard());
        panel.add(Box.createVerticalGlue());
        panel.add(Box.createVerticalStrut(10));

        RoundedButton backBtn = RoundedButton.secondary("← Back to Dashboard");
        backBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        backBtn.addActionListener(e -> navigator.navigate("DASHBOARD"));
        panel.add(backBtn);

        return panel;
    }

    // ── City selection card ───────────────────────────────────────────

    private JPanel buildInputCard() {
        JPanel card = darkCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(18, 18, 18, 18));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 230));

        card.add(tealLabel("Select Cities"));
        card.add(Box.createVerticalStrut(14));

        card.add(fieldLabel("From City"));
        card.add(Box.createVerticalStrut(6));
        sourceCombo = styledCombo();
        card.add(sourceCombo);
        card.add(Box.createVerticalStrut(12));

        card.add(fieldLabel("To City"));
        card.add(Box.createVerticalStrut(6));
        destCombo = styledCombo();
        card.add(destCombo);
        card.add(Box.createVerticalStrut(16));

        JPanel btnRow = new JPanel(new GridLayout(1, 2, 8, 0));
        btnRow.setOpaque(false);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundedButton findBtn  = RoundedButton.primary("Find Shortest Route");
        RoundedButton clearBtn = RoundedButton.secondary("Clear");
        findBtn .addActionListener(e -> runDijkstra());
        clearBtn.addActionListener(e -> clearResult());
        btnRow.add(findBtn);
        btnRow.add(clearBtn);
        card.add(btnRow);

        refreshCombos();
        return card;
    }

    // ── Result card ───────────────────────────────────────────────────

    private JPanel buildResultCard() {
        JPanel card = darkCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(18, 18, 18, 18));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 165));

        resultHeading = tealLabel("Result");
        card.add(resultHeading);
        card.add(Box.createVerticalStrut(12));

        // Large amber distance number + "km" unit beside it
        JPanel distRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        distRow.setOpaque(false);
        distRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        distanceLabel = new JLabel("—");
        distanceLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        distanceLabel.setForeground(AppColors.AMBER);

        distUnitLabel = new JLabel("");
        distUnitLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        distUnitLabel.setForeground(AppColors.TEXT_MUTED);

        distRow.add(distanceLabel);
        distRow.add(distUnitLabel);
        card.add(distRow);

        JLabel caption = new JLabel("Total Distance");
        caption.setFont(AppColors.FONT_SMALL);
        caption.setForeground(AppColors.TEXT_MUTED);
        caption.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(caption);
        card.add(Box.createVerticalStrut(10));

        pathLabel = new JLabel("No route calculated yet.");
        pathLabel.setFont(AppColors.FONT_SMALL);
        pathLabel.setForeground(AppColors.TEXT_SECONDARY);
        pathLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(pathLabel);

        return card;
    }

    // ── Activity log card ─────────────────────────────────────────────

    private JPanel buildLogCard() {
        JPanel card = darkCard();
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(new EmptyBorder(14, 14, 10, 14));

        card.add(tealLabel("Activity Log"), BorderLayout.NORTH);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setBackground(AppColors.BG_DARKEST);
        logArea.setForeground(AppColors.TEXT_SECONDARY);
        logArea.setFont(AppColors.FONT_MONO);
        logArea.setBorder(null);
        logArea.setText("> Route Finder ready. Select source and destination cities.\n");

        JScrollPane sp = new JScrollPane(logArea);
        sp.setBorder(null);
        sp.setBackground(AppColors.BG_DARKEST);
        sp.getViewport().setBackground(AppColors.BG_DARKEST);
        card.add(sp, BorderLayout.CENTER);

        return card;
    }

    // ── Dijkstra call ─────────────────────────────────────────────────

    private void runDijkstra() {
        if (graph == null || graph.numVertices < 2) {
            appendLog("ERROR: Need at least 2 cities. Go to Graph View to build the network.");
            return;
        }
        String fromCity = (String) sourceCombo.getSelectedItem();
        String toCity   = (String) destCombo.getSelectedItem();
        if (fromCity == null || toCity == null) {
            appendLog("ERROR: Please select both source and destination cities.");
            return;
        }
        if (fromCity.equals(toCity)) {
            appendLog("ERROR: Source and destination must be different cities.");
            return;
        }
        try {
            int src  = graph.getNodeIndex(fromCity);
            int dest = graph.getNodeIndex(toCity);

            int[] previous = new int[graph.numVertices];
            Arrays.fill(previous, -1);
            int[] distances = DijkstraSolver.findShortestDistances(graph, src, previous);

            if (distances[dest] == Integer.MAX_VALUE) {
                distanceLabel.setText("∞");
                distUnitLabel.setText("");
                distanceLabel.setForeground(AppColors.RED);
                pathLabel.setText("No route exists between these cities.");
                graphPanel.clearHighlight();
                appendLog("No route: " + fromCity + " → " + toCity);
                return;
            }

            List<Integer> path = DijkstraSolver.getPath(previous, dest);
            int totalKm = distances[dest];

            distanceLabel.setText(String.valueOf(totalKm));
            distUnitLabel.setText(" km");
            distanceLabel.setForeground(AppColors.AMBER);
            pathLabel.setText("<html>Route: " + formatPath(path) + "</html>");

            graphPanel.highlightPath(path, src, dest, totalKm);
            appendLog("Shortest route  " + fromCity + " → " + toCity
                      + "  =  " + totalKm + " km");
            appendLog("Path: " + formatPath(path));

        } catch (GraphException ex) {
            appendLog("ERROR: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Route Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearResult() {
        distanceLabel.setText("—");
        distUnitLabel.setText("");
        distanceLabel.setForeground(AppColors.AMBER);
        pathLabel.setText("No route calculated yet.");
        graphPanel.clearHighlight();
        appendLog("Cleared.");
    }

    // ── Helpers ───────────────────────────────────────────────────────

    void refreshCombos() {
        List<String> names = graph.getNodeNames();
        String[] arr = names.toArray(new String[0]);
        String prevSrc  = (String) sourceCombo.getSelectedItem();
        String prevDest = (String) destCombo.getSelectedItem();

        sourceCombo.setModel(new DefaultComboBoxModel<>(arr));
        destCombo  .setModel(new DefaultComboBoxModel<>(arr));

        if (prevSrc  != null && names.contains(prevSrc))  sourceCombo.setSelectedItem(prevSrc);
        if (prevDest != null && names.contains(prevDest)) destCombo  .setSelectedItem(prevDest);
        else if (arr.length > 1) destCombo.setSelectedIndex(1);
    }

    private void appendLog(String msg) {
        logArea.append("> " + msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    /** Formats a path of city indices as "Hyderabad → Vijayawada → Chennai". */
    private String formatPath(List<Integer> path) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            if (i > 0) sb.append(" → ");
            sb.append(graph.getNodeName(path.get(i)));
        }
        return sb.toString();
    }

    // ── UI helpers ────────────────────────────────────────────────────

    private JPanel darkCard() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppColors.BG_DARK);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        return card;
    }

    private JComboBox<String> styledCombo() {
        JComboBox<String> cb = new JComboBox<>();
        cb.setBackground(AppColors.BG_PANEL);
        cb.setForeground(AppColors.TEXT_PRIMARY);
        cb.setFont(AppColors.FONT_BODY);
        cb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        cb.setAlignmentX(Component.LEFT_ALIGNMENT);
        return cb;
    }

    private JLabel tealLabel(String text) {
        JLabel l = new JLabel(text.toUpperCase());
        l.setFont(AppColors.FONT_LABEL);
        l.setForeground(AppColors.TEAL);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(AppColors.FONT_LABEL);
        l.setForeground(AppColors.TEXT_SECONDARY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }
}