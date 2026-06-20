import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class GraphVisualizationPage extends JPanel {

    private final Navigator  navigator;
    private final Graph      graph;
    private final Runnable   onGraphChanged;
    private final GraphPanel graphPanel;

    // Edge-add dropdowns — repopulated whenever a city is added
    private JComboBox<String> fromCombo;
    private JComboBox<String> toCombo;
    private JTextField        distField;

    // City-add field
    private JTextField cityNameField;

    // City list display
    private JTextArea  cityListArea;

    private JLabel statusLabel;

    public GraphVisualizationPage(Navigator navigator, Graph graph, Runnable onGraphChanged) {
        this.navigator      = navigator;
        this.graph          = graph;
        this.onGraphChanged = onGraphChanged;
        this.graphPanel     = new GraphPanel();
        this.graphPanel.setGraph(graph);

        setBackground(AppColors.BG_DARKEST);
        setLayout(new BorderLayout());
        add(buildHeader(),  BorderLayout.NORTH);
        add(buildSplit(),   BorderLayout.CENTER);
    }

    /** Called after the graph changes externally so this panel stays in sync. */
    public void refresh() {
        graphPanel.setGraph(graph);
        refreshDropdowns();
        refreshCityList();
    }

    // ── Header ────────────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel h = gradientHeader();
        h.setLayout(new BorderLayout());
        h.setBorder(new EmptyBorder(0, 28, 0, 28));

        JLabel title    = headerTitle("Graph Visualization");
        JLabel subtitle = headerSubtitle("City network — add cities and routes, then find the shortest path");

        JPanel stack = vStack(title, subtitle);
        h.add(stack, BorderLayout.CENTER);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 18));
        right.setOpaque(false);
        RoundedButton sampleBtn = RoundedButton.secondary("Load Sample Cities");
        RoundedButton backBtn   = RoundedButton.secondary("← Dashboard");
        sampleBtn.addActionListener(e -> loadSample());
        backBtn  .addActionListener(e -> navigator.navigate("DASHBOARD"));
        right.add(sampleBtn);
        right.add(backBtn);
        h.add(right, BorderLayout.EAST);
        return h;
    }

    // ── Split: canvas left, controls right ───────────────────────────

    private JSplitPane buildSplit() {
        JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            wrapCanvas(), buildControls());
        sp.setResizeWeight(0.68);
        sp.setDividerLocation(600);
        sp.setDividerSize(4);
        sp.setBorder(null);
        sp.setBackground(AppColors.BG_DARKEST);
        return sp;
    }

    private JPanel wrapCanvas() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(AppColors.CANVAS_BG);
        p.setBorder(BorderFactory.createLineBorder(AppColors.BG_MEDIUM, 1));
        p.add(graphPanel, BorderLayout.CENTER);
        return p;
    }

    // ── Right control panel ───────────────────────────────────────────

    private JScrollPane buildControls() {
        JPanel panel = new JPanel();
        panel.setBackground(AppColors.BG_DARKEST);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(16, 12, 16, 12));

        panel.add(buildAddCityCard());
        panel.add(Box.createVerticalStrut(12));
        panel.add(buildAddRouteCard());
        panel.add(Box.createVerticalStrut(12));
        panel.add(buildCityListCard());
        panel.add(Box.createVerticalStrut(12));
        panel.add(buildActionsCard());
        panel.add(Box.createVerticalGlue());

        JScrollPane sp = new JScrollPane(panel);
        sp.setBorder(null);
        sp.setBackground(AppColors.BG_DARKEST);
        sp.getViewport().setBackground(AppColors.BG_DARKEST);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setPreferredSize(new Dimension(280, 0));
        return sp;
    }

    // ── Card: Add City ────────────────────────────────────────────────

    private JPanel buildAddCityCard() {
        JPanel card = darkCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        card.add(sectionLabel("Add City"));
        card.add(Box.createVerticalStrut(10));
        card.add(fieldLabel("City Name"));
        card.add(Box.createVerticalStrut(6));

        cityNameField = inputField("e.g. Hyderabad");
        card.add(cityNameField);
        card.add(Box.createVerticalStrut(10));

        RoundedButton btn = RoundedButton.primary("Add City");
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.addActionListener(e -> addCity());
        card.add(btn);

        return card;
    }

    // ── Card: Add Route ───────────────────────────────────────────────

    private JPanel buildAddRouteCard() {
        JPanel card = darkCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        card.add(sectionLabel("Add Route (km)"));
        card.add(Box.createVerticalStrut(10));

        card.add(fieldLabel("From City"));
        card.add(Box.createVerticalStrut(5));
        fromCombo = styledCombo();
        card.add(fromCombo);
        card.add(Box.createVerticalStrut(8));

        card.add(fieldLabel("To City"));
        card.add(Box.createVerticalStrut(5));
        toCombo = styledCombo();
        card.add(toCombo);
        card.add(Box.createVerticalStrut(8));

        card.add(fieldLabel("Distance (km)"));
        card.add(Box.createVerticalStrut(5));
        distField = inputField("e.g. 275");
        card.add(distField);
        card.add(Box.createVerticalStrut(10));

        RoundedButton btn = RoundedButton.amber("Add Route");
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.addActionListener(e -> addRoute());
        card.add(btn);

        return card;
    }

    // ── Card: City List ───────────────────────────────────────────────

    private JPanel buildCityListCard() {
        JPanel card = darkCard();
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(new EmptyBorder(14, 14, 14, 14));
        card.setPreferredSize(new Dimension(0, 140));

        card.add(sectionLabel("Cities in Graph"), BorderLayout.NORTH);

        cityListArea = new JTextArea();
        cityListArea.setEditable(false);
        cityListArea.setBackground(AppColors.BG_DARKEST);
        cityListArea.setForeground(AppColors.TEXT_SECONDARY);
        cityListArea.setFont(AppColors.FONT_MONO);
        cityListArea.setBorder(null);

        JScrollPane sp = new JScrollPane(cityListArea);
        sp.setBorder(null);
        sp.setBackground(AppColors.BG_DARKEST);
        sp.getViewport().setBackground(AppColors.BG_DARKEST);
        card.add(sp, BorderLayout.CENTER);

        refreshCityList();
        return card;
    }

    // ── Card: Quick actions + status ──────────────────────────────────

    private JPanel buildActionsCard() {
        JPanel card = darkCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(14, 14, 14, 14));

        card.add(sectionLabel("Quick Actions"));
        card.add(Box.createVerticalStrut(10));

        JPanel row = new JPanel(new GridLayout(1, 2, 8, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundedButton sampleBtn = RoundedButton.secondary("Sample Graph");
        RoundedButton clearBtn  = RoundedButton.danger("Clear All");
        sampleBtn.addActionListener(e -> loadSample());
        clearBtn .addActionListener(e -> clearAll());
        row.add(sampleBtn);
        row.add(clearBtn);
        card.add(row);

        card.add(Box.createVerticalStrut(10));
        statusLabel = new JLabel(" ");
        statusLabel.setFont(AppColors.FONT_SMALL);
        statusLabel.setForeground(AppColors.TEXT_MUTED);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(statusLabel);

        return card;
    }

    // ── Logic ─────────────────────────────────────────────────────────

    private void addCity() {
        String name = cityNameField.getText().trim();
        if (name.isEmpty()) { setStatus("Please enter a city name.", true); return; }
        try {
            graph.addNode(name);
            graphPanel.setGraph(graph);
            refreshDropdowns();
            refreshCityList();
            onGraphChanged.run();
            setStatus("Added city: " + name, false);
            cityNameField.setText("");
        } catch (GraphException ex) {
            setStatus(ex.getMessage(), true);
        }
    }

    private void addRoute() {
        if (fromCombo.getItemCount() < 2) {
            setStatus("Add at least 2 cities before adding a route.", true); return;
        }
        String from = (String) fromCombo.getSelectedItem();
        String to   = (String) toCombo.getSelectedItem();
        if (from == null || to == null || from.equals(to)) {
            setStatus("Select two different cities.", true); return;
        }
        String distText = distField.getText().trim();
        if (distText.isEmpty()) { setStatus("Enter the distance in km.", true); return; }
        try {
            int dist = Integer.parseInt(distText);
            graph.addEdge(from, to, dist);
            graphPanel.setGraph(graph);
            onGraphChanged.run();
            setStatus("Added route: " + from + " ↔ " + to + " (" + dist + " km)", false);
            distField.setText("");
        } catch (NumberFormatException ex) {
            setStatus("Distance must be a whole number.", true);
        } catch (GraphException ex) {
            setStatus(ex.getMessage(), true);
        }
    }

    private void loadSample() {
        try {
            graph.clear();
            // 9 Indian cities
            for (String city : new String[]{
                    "Hyderabad","Vijayawada","Visakhapatnam",
                    "Chennai","Bangalore","Mumbai","Delhi","Kolkata","Pune"}) {
                graph.addNode(city);
            }
            // Realistic road distances in km
            graph.addEdge("Hyderabad",    "Vijayawada",    275);
            graph.addEdge("Hyderabad",    "Bangalore",     570);
            graph.addEdge("Hyderabad",    "Chennai",       625);
            graph.addEdge("Hyderabad",    "Mumbai",        710);
            graph.addEdge("Vijayawada",   "Visakhapatnam", 350);
            graph.addEdge("Vijayawada",   "Chennai",       430);
            graph.addEdge("Visakhapatnam","Kolkata",       900);
            graph.addEdge("Chennai",      "Bangalore",     345);
            graph.addEdge("Bangalore",    "Mumbai",        980);
            graph.addEdge("Bangalore",    "Pune",          840);
            graph.addEdge("Mumbai",       "Pune",          150);
            graph.addEdge("Mumbai",       "Delhi",        1400);
            graph.addEdge("Pune",         "Delhi",        1490);
            graph.addEdge("Delhi",        "Kolkata",      1470);

            graphPanel.setGraph(graph);
            refreshDropdowns();
            refreshCityList();
            onGraphChanged.run();
            setStatus("Sample graph loaded: 9 cities, 14 routes.", false);
        } catch (GraphException ex) {
            setStatus("Error loading sample: " + ex.getMessage(), true);
        }
    }

    private void clearAll() {
        int choice = JOptionPane.showConfirmDialog(this,
            "Clear all cities and routes?", "Confirm Clear",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) return;
        graph.clear();
        graphPanel.setGraph(graph);
        refreshDropdowns();
        refreshCityList();
        onGraphChanged.run();
        setStatus("Graph cleared.", false);
    }

    // ── Refresh helpers ───────────────────────────────────────────────

    private void refreshDropdowns() {
        List<String> names = graph.getNodeNames();
        DefaultComboBoxModel<String> m1 = new DefaultComboBoxModel<>(names.toArray(new String[0]));
        DefaultComboBoxModel<String> m2 = new DefaultComboBoxModel<>(names.toArray(new String[0]));
        fromCombo.setModel(m1);
        toCombo  .setModel(m2);
        if (names.size() > 1) toCombo.setSelectedIndex(1);
    }

    private void refreshCityList() {
        List<String> names = graph.getNodeNames();
        if (names.isEmpty()) { cityListArea.setText("(no cities yet)"); return; }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < names.size(); i++)
            sb.append(String.format("%2d. %s%n", i, names.get(i)));
        cityListArea.setText(sb.toString());
        cityListArea.setCaretPosition(0);
    }

    private void setStatus(String msg, boolean error) {
        statusLabel.setText(msg);
        statusLabel.setForeground(error ? AppColors.RED : AppColors.GREEN);
    }

    // ── UI factory helpers ────────────────────────────────────────────

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

    private JTextField inputField(String placeholder) {
        JTextField f = new JTextField(placeholder);
        f.setBackground(AppColors.BG_PANEL);
        f.setForeground(AppColors.TEXT_PRIMARY);
        f.setCaretColor(AppColors.TEAL);
        f.setFont(AppColors.FONT_BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.BG_MEDIUM, 1),
            new EmptyBorder(7, 10, 7, 10)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        return f;
    }

    private JLabel sectionLabel(String text) {
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

    private JPanel gradientHeader() {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, AppColors.BG_DARK, getWidth(), 0, AppColors.BG_DARKEST));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(AppColors.BG_MEDIUM);
                g2.fillRect(0, getHeight() - 1, getWidth(), 1);
            }
        };
    }

    private JLabel headerTitle(String text) {
        JLabel l = new JLabel(text); l.setFont(AppColors.FONT_TITLE); l.setForeground(AppColors.TEXT_PRIMARY); return l;
    }
    private JLabel headerSubtitle(String text) {
        JLabel l = new JLabel(text); l.setFont(AppColors.FONT_SMALL); l.setForeground(AppColors.TEXT_MUTED); return l;
    }

    private JPanel vStack(JLabel... labels) {
        JPanel p = new JPanel(); p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(labels[0]);
        for (int i = 1; i < labels.length; i++) { p.add(Box.createVerticalStrut(4)); p.add(labels[i]); }
        return p;
    }
}