import java.awt.*;

/**
 * AppColors.java
 *
 * Single source of truth for every color and font in the application.
 * Every page and component imports from here -- nothing hardcodes
 * color values inline. If you ever want to change the accent color
 * or tweak the background, you change it once here and every screen
 * updates automatically.
 *
 * Design language: "Navigation Dashboard"
 * - Deep navy/slate backgrounds (like a GPS app at night)
 * - Teal primary accent (modern, tech-forward)
 * - Amber for the active shortest route (matches how mapping apps
 *   draw highlighted routes)
 * - Green = start node, Red = destination (universal map convention)
 */
public class AppColors {

    // ── Background layers (darkest to lightest) ──────────────────────
    public static final Color BG_DARKEST = new Color(0x0F, 0x17, 0x2A); // #0F172A – window bg
    public static final Color BG_DARK    = new Color(0x1E, 0x29, 0x3B); // #1E293B – sidebar / cards
    public static final Color BG_MEDIUM  = new Color(0x27, 0x35, 0x49); // #273549 – card interior
    public static final Color BG_PANEL   = new Color(0x33, 0x41, 0x55); // #334155 – input fields

    // ── Accent colours ───────────────────────────────────────────────
    public static final Color TEAL       = new Color(0x14, 0xB8, 0xA6); // primary accent
    public static final Color TEAL_DARK  = new Color(0x0D, 0x94, 0x88); // teal on hover/press
    public static final Color AMBER      = new Color(0xF5, 0x9E, 0x0B); // route highlight / warning
    public static final Color AMBER_DARK = new Color(0xD9, 0x77, 0x06); // amber hover
    public static final Color GREEN      = new Color(0x22, 0xC5, 0x5E); // start / success
    public static final Color RED        = new Color(0xEF, 0x44, 0x44); // destination / error
    public static final Color BLUE       = new Color(0x60, 0xA5, 0xFA); // info / stat accent

    // ── Text colours ─────────────────────────────────────────────────
    public static final Color TEXT_PRIMARY   = new Color(0xF1, 0xF5, 0xF9); // near-white – main text
    public static final Color TEXT_SECONDARY = new Color(0x94, 0xA3, 0xB8); // slate-400  – labels
    public static final Color TEXT_MUTED     = new Color(0x64, 0x74, 0x8B); // slate-500  – hints

    // ── Graph canvas colours ─────────────────────────────────────────
    public static final Color CANVAS_BG      = new Color(0x0A, 0x10, 0x1E); // near-black canvas
    public static final Color EDGE_DEFAULT   = new Color(0x33, 0x41, 0x55); // dim edges
    public static final Color NODE_DEFAULT   = TEAL;                         // unselected nodes

    // ── Typography ───────────────────────────────────────────────────
    // SansSerif resolves to the best available sans on every OS
    // (Helvetica/Arial on Mac/Win, DejaVu Sans on Linux)
    public static final Font FONT_DISPLAY = new Font("SansSerif", Font.BOLD,  28);
    public static final Font FONT_TITLE   = new Font("SansSerif", Font.BOLD,  22);
    public static final Font FONT_HEADER  = new Font("SansSerif", Font.BOLD,  17);
    public static final Font FONT_BODY    = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONT_BUTTON  = new Font("SansSerif", Font.BOLD,  13);
    public static final Font FONT_LABEL   = new Font("SansSerif", Font.BOLD,  12);
    public static final Font FONT_SMALL   = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font FONT_MONO    = new Font("Monospaced", Font.PLAIN, 13);
}