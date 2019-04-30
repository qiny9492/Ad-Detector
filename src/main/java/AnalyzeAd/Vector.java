package AnalyzeAd;

public class Vector {
    
    private double posX;
    private double posY;
    private int match;
    
    public Vector(double x, double y,int match) {
        this.posX = x;
        this.posY = y;
        this.match = match;

    }
    
    public double getX() {
        return this.posX;
    }
    
    public double getY() {
        return this.posY;
    }
    
    public int getMatch() {
        return this.match;
    }
}
