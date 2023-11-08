package it.xargon.xshellmenu.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import javax.swing.border.AbstractBorder;

public class XSRoundBorder extends AbstractBorder {
	private static final long serialVersionUID = 7466139037675343091L;

	private Color color;
    private int thickness;
    private int radius;
    private Insets insets = null;
    private BasicStroke stroke = null;
    private int strokePad;
    private RenderingHints hints;

    public XSRoundBorder(Color color, int thickness, int radius) {
        this.thickness = thickness;
        this.radius = radius;
        this.color = color;

        stroke = new BasicStroke(thickness);
        strokePad = thickness / 2;

        hints = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int pad = radius + strokePad;
        insets = new Insets(pad, pad, pad, pad);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return insets;
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        return getBorderInsets(c);
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
    	if (g instanceof Graphics2D g2) {
    		RoundRectangle2D.Double border = new RoundRectangle2D.Double(
                    strokePad,
                    strokePad,
                    width - thickness,
                    height - thickness,
                    radius,
                    radius);

            g2.setRenderingHints(hints);
            g2.setColor(color);
            g2.setStroke(stroke);
            g2.draw(border);
    	}
    }
}
