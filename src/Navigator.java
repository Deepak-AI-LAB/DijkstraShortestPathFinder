/**
 * Navigator.java
 *
 * A tiny interface that gives every page a way to request a screen
 * change without knowing anything about how screens are managed
 * (CardLayout, a JTabbedPane, or something else entirely). Pages only
 * call navigate("PAGE_ID") and MainApp -- which implements this
 * interface -- handles the actual switch.
 *
 * Page IDs used in this project:
 *   "SPLASH"  – animated loading screen (auto-advances to LOGIN)
 *   "LOGIN"   – login form
 *   "MAIN"    – the main shell (sidebar + inner pages)
 *   "DASHBOARD"    – home cards
 *   "ROUTE"        – route finder (Dijkstra input + result)
 *   "GRAPH"        – graph builder / visualization
 *   "ABOUT"        – about the project
 */
public interface Navigator {
    void navigate(String pageId);
}