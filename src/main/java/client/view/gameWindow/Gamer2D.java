package client.view.gameWindow;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class Gamer2D {

    private int gamerId;
//    private boolean active;
//    private int points;
    private double x;
    private double y;
    private double diameter = 30;
    private Ellipse2D graphicRepresentation;
    private Color color;

//    public void Gamer2D(int gamerId, int x, int y, Color color) {
    public Gamer2D(int gamerId) {
        this.gamerId = gamerId;
        this.generateGraphicRepresentation();
        //this.color = color;
    }

    public void generateGraphicRepresentation() {
        this.graphicRepresentation = new Ellipse2D.Double(this.x, this.y, this.diameter, this.diameter);
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
        this.generateGraphicRepresentation();
    }

    public Ellipse2D getGraphicRepresentation() {
        return graphicRepresentation;
    }

    public int getGamerId() {
        return gamerId;
    }

    public void setGamerId(int gamerId) {
        this.gamerId = gamerId;
    }
}
