package com.mom.imgprocess;

import javafx.scene.paint.Color;
import org.opencv.core.Scalar;

public class ColorBound {
    public ColorBound(String name){
        this.name = name;
        lower = new Scalar(0,0,0);
        upper = new Scalar(0,0,0);
    }
    public Scalar getLower() {
        return lower;
    }

    public Scalar getUpper() {
        return upper;
    }

    private int id;

    private Scalar lower,upper;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public void setLower(double [] lower) {
        this.lower.set(lower);
    }

    public void setUpper(double [] upper) {
        this.upper.set(upper);
    }

    public void setLower(Scalar lower) {
        this.lower = lower;
    }

    public void setUpper(Scalar upper) {
        this.upper = upper;
    }

    public void setLower(Color color){
        fxColor2CVColor(color,lower);
    }

    public void setUpper(Color color){
        fxColor2CVColor(color,upper);
    }

    private void fxColor2CVColor(Color color, Scalar scalar){
        double [] hsv = new double[3];
        hsv[0] = color.getHue() * 180.0;
        hsv[1] = color.getSaturation() * 255.0;
        hsv[2] = color.getBrightness() * 255.0;
        scalar.set(hsv);
    }

    @Override
    public String toString() {
        return name;
    }
}
