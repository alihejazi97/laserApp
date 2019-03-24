package com.mom.imgprocess;


import org.opencv.core.Point;

public class Circle {
    Point center;
    int radious;

    public Circle(Point center, int radious) {
        this.center = center;
        this.radious = radious;
    }
}
