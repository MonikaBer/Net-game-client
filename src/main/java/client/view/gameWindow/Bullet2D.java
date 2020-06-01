package client.view.gameWindow;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class Bullet2D {

    private double x;
    private double y;
    private double diameter = 10;
    private Ellipse2D graphicRepresentation;
    private Color color;

    public Bullet2D(double x, double y) {
        this.x = x;
        this.y = y;
        this.color = new Color(0,0,0);
        this.generateGraphicRepresentation();
    }

    public void generateGraphicRepresentation() {
        this.graphicRepresentation = new Ellipse2D.Double(this.x, this.y, this.diameter, this.diameter);
    }

    public Ellipse2D getGraphicRepresentation() {
        return graphicRepresentation;
    }

    public Color getColor() {
        return color;
    }
}
