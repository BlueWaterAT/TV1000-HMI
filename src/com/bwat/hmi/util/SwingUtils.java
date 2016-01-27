package com.bwat.hmi.util;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;

/**
 * Helper functions for creating Swing components
 *
 * @author Kareem El-Faramawi
 */
public class SwingUtils {

    /**
     * Recursively gathers all children components including the root
     *
     * @param c Root component
     * @return Given component and all its children
     */
    public static ArrayList<Component> getAllComponents_r(Component c) {
        ArrayList<Component> components = new ArrayList<Component>();
        components.add(c);
        if (c instanceof Container) {
            Component[] inner = ((Container) c).getComponents();
            if (inner.length > 0) {
                for (Component comp : inner) {
                    components.addAll(getAllComponents_r(comp));
                }
            }
        }
        return components;
    }

    /**
     * Recursively sets the font size of a component and all of its children
     *
     * @param c    Root component
     * @param size Font size
     */
    public static void setFontSize_r(Component c, float size) {
        setFont_r(c, c.getFont().deriveFont(size));
    }

    /**
     * Recursively sets the Font of a component and all of its children
     *
     * @param c    Root component
     */
    public static void setFont_r(Component c, Font f) {
        for (Component comp : getAllComponents_r(c)) {
            comp.setFont(f);
        }
    }

    /**
     * Recursively sets the background color of a component and all its children
     *
     * @param c  Root component
     * @param bg Background color
     */
    public static void setBG_r(Component c, Color bg) {
        setColor_r(c, bg, null);
    }

    /**
     * Recursively sets the foreground color of a component and all its children
     *
     * @param c  Root component
     * @param fg Foreground color
     */
    public static void setFG_r(Component c, Color fg) {
        setColor_r(c, null, fg);
    }

    /**
     * Recursively sets the background and foreground colors of a component and all its children
     *
     * @param c  Root component
     * @param bg Background color
     * @param fg Foreground color
     */
    public static void setColor_r(Component c, Color bg, Color fg) {
        for (Component comp : getAllComponents_r(c)) {
            if (bg != null) {
                comp.setBackground(bg);
            }
            if (fg != null) {
                comp.setForeground(fg);
            }
        }
    }

    /**
     * Gives any component an etched border by placing it in another JPanel
     *
     * @param c          Component
     * @param title      Title to put on the border
     * @param titleColor Color of the title text
     * @return Bordered JPanel with the given component inside
     */
    public static JPanel placeInTitledEtchedJPanel(Component c, String title, Color titleColor) {
        JPanel parent = new JPanel(new BorderLayout());
        parent.add(c, BorderLayout.CENTER);
        TitledBorder tb = new TitledBorder(title);
        tb.setBorder(BorderFactory.createEtchedBorder());
        tb.setTitleColor(titleColor);
        parent.setBorder(tb);
        return parent;
    }

    /**
     * Lays out all the given components in a JPanel with the given GridLayout
     *
     * @param layout     GridLayout to use for the JPanel
     * @param components Components to be placed in the grid
     * @return JPanel with all the components placed inside
     */
    public static JPanel createGridJPanel(GridLayout layout, Component... components) {
        JPanel panel = new JPanel(layout);
        for (Component c : components) {
            panel.add(c);
        }
        return panel;
    }

    /**
     * Lays out all the given components in a JPanel using a grid of the given dimensions
     *
     * @param rows  Number of rows
     * @param cols  Number of columns
     * @param comps Components to be placed in the grid
     * @return JPanel with all the components placed inside
     */
    public static JPanel createGridJPanel(int rows, int cols, Component... comps) {
        return createGridJPanel(new GridLayout(rows, cols), comps);
    }

    /**
     * Lays out all the given components in a JPanel using a grid of the given dimensions and spacing gaps
     *
     * @param rows  Number of rows
     * @param cols  Number of columns
     * @param hgap  Horizontal gap between components
     * @param vgap  Vertical gap between components
     * @param comps Components to be placed in the grid
     * @return JPanel with all the components placed inside
     */
    public static JPanel createGridJPanel(int rows, int cols, int hgap, int vgap, Component... comps) {
        return createGridJPanel(new GridLayout(rows, cols, hgap, vgap), comps);
    }

    /**
     * Lays out all the given components vertically in a JPanel using the given spacing gap
     *
     * @param vgap       Vertical gap between components
     * @param components Components to be placed in the grid
     * @return JPanel with all the components placed inside
     */
    public static JPanel createVerticalGrid(int vgap, Component... components) {
        return createGridJPanel(new GridLayout(components.length, 1, 0, vgap), components);
    }

    /**
     * Lays out all the given components horizontally in a JPanel using the given spacing gap
     *
     * @param hgap       Horizontal gap between components
     * @param components Components to be placed in the grid
     * @return JPanel with all the components placed inside
     */
    public static JPanel createHorizontalGrid(int hgap, Component... components) {
        return createGridJPanel(new GridLayout(1, components.length, hgap, 0), components);
    }

}
