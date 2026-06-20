import javax.swing.*;
import java.awt.*;

public class MainApp extends JFrame implements Navigator {

    private final CardLayout outerLayout = new CardLayout();
    private final JPanel     outerPanel  = new JPanel(outerLayout);
    private final CardLayout innerLayout = new CardLayout();
    private final JPanel     innerPanel  = new JPanel(innerLayout);

    private final Graph graph;

    private DashboardPage          dashboardPage;
    private RouteFinderPage        routeFinderPage;
    private GraphVisualizationPage graphVisPage;
    private SidebarNav             sidebarNav;

    private static final java.util.Set<String> INNER_PAGES =
        new java.util.HashSet<>(java.util.Arrays.asList(
            "DASHBOARD", "ROUTE", "GRAPH", "ABOUT"));

    public MainApp() {
        super("Dijkstra Path Finder — Indian City Road Network");
        graph = buildSampleGraph();
        setupWindow();
        buildAllPages();
        outerLayout.show(outerPanel, "SPLASH");
        setVisible(true);
    }

    private void setupWindow() {
        setSize(1150, 740);
        setMinimumSize(new Dimension(960, 640));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(outerPanel);
        outerPanel.setBackground(AppColors.BG_DARKEST);
        setIconImage(buildAppIcon());
    }

    private java.awt.image.BufferedImage buildAppIcon() {
        java.awt.image.BufferedImage img =
            new java.awt.image.BufferedImage(32, 32, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(AppColors.BG_DARKEST);
        g2.fillRect(0, 0, 32, 32);
        g2.setColor(AppColors.TEAL);
        g2.fillRoundRect(2, 2, 28, 28, 8, 8);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 18));
        g2.drawString("D", 8, 23);
        g2.dispose();
        return img;
    }

    private void buildAllPages() {
        outerPanel.add(new SplashScreen(this), "SPLASH");
        outerPanel.add(new LoginPage(this),    "LOGIN");

        dashboardPage   = new DashboardPage(this, graph);
        routeFinderPage = new RouteFinderPage(this, graph);
        graphVisPage    = new GraphVisualizationPage(this, graph, this::onGraphChanged);
        AboutPage aboutPage = new AboutPage(this);

        innerPanel.add(dashboardPage,   "DASHBOARD");
        innerPanel.add(routeFinderPage, "ROUTE");
        innerPanel.add(graphVisPage,    "GRAPH");
        innerPanel.add(aboutPage,       "ABOUT");
        innerPanel.setBackground(AppColors.BG_DARKEST);

        sidebarNav = new SidebarNav(this);
        JPanel shell = new JPanel(new BorderLayout());
        shell.setBackground(AppColors.BG_DARKEST);
        shell.add(sidebarNav, BorderLayout.WEST);
        shell.add(innerPanel, BorderLayout.CENTER);
        outerPanel.add(shell, "MAIN");
    }

    @Override
    public void navigate(String pageId) {
        if (INNER_PAGES.contains(pageId)) {
            outerLayout.show(outerPanel, "MAIN");
            innerLayout.show(innerPanel, pageId);
            sidebarNav.setActivePage(pageId);
        } else {
            outerLayout.show(outerPanel, pageId);
        }
    }

    private void onGraphChanged() {
        dashboardPage.refreshStats();
        routeFinderPage.onGraphUpdated();
        graphVisPage.refresh();
    }

    // 9 Indian cities with realistic inter-city road distances in km
    private Graph buildSampleGraph() {
        Graph g = new Graph();
        try {
            // Cities
            g.addNode("Hyderabad");
            g.addNode("Vijayawada");
            g.addNode("Visakhapatnam");
            g.addNode("Chennai");
            g.addNode("Bangalore");
            g.addNode("Mumbai");
            g.addNode("Delhi");
            g.addNode("Kolkata");
            g.addNode("Pune");

            // Routes (approximate road distances in km)
            g.addEdge("Hyderabad",    "Vijayawada",     275);
            g.addEdge("Hyderabad",    "Bangalore",      570);
            g.addEdge("Hyderabad",    "Chennai",        625);
            g.addEdge("Hyderabad",    "Mumbai",         710);
            g.addEdge("Vijayawada",   "Visakhapatnam",  350);
            g.addEdge("Vijayawada",   "Chennai",        430);
            g.addEdge("Visakhapatnam","Kolkata",        900);
            g.addEdge("Chennai",      "Bangalore",      345);
            g.addEdge("Bangalore",    "Mumbai",         980);
            g.addEdge("Bangalore",    "Pune",           840);
            g.addEdge("Mumbai",       "Pune",           150);
            g.addEdge("Mumbai",       "Delhi",         1400);
            g.addEdge("Pune",         "Delhi",         1490);
            g.addEdge("Delhi",        "Kolkata",       1470);
        } catch (GraphException ex) {
            throw new RuntimeException("Failed to build city graph: " + ex.getMessage(), ex);
        }
        return g;
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(MainApp::new);
    }
}