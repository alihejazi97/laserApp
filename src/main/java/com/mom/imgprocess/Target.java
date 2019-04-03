package com.mom.imgprocess;

import org.apache.commons.lang3.StringUtils;
import org.opencv.core.Point;

public class Target {

    public int id;

    public static int TARGET_NUMBER;

    public static int GUN_NUMBER;

    public String name;

    public String webCamName;

    public static String camDescriptor;

    public int gunId,bulletNum;

    public boolean valid, gunSignal,active;

    public void setGunSignal(boolean gunSignal) {
        this.gunSignal = gunSignal;
    }

    public Target clone(){
        Target target = new Target();
        target.valid = this.valid;
        target.point0 = this.point0.clone();
        target.point1 = this.point1.clone();
        target.point2 = this.point2.clone();
        target.point3 = this.point3.clone();
        target.id = this.id;
        return target;
    }

    @Override
    public String toString() {
        if (StringUtils.isNumeric(name))
            return Integer.toString(Integer.parseInt(name) + 1);
        return name;
    }

    public Point point0;
    public Point point1;
    public Point point2;
    public Point point3;

    public Target() {
        point0 = new Point();
        point1 = new Point();
        point2 = new Point();
        point3 = new Point();
        valid = false;
        active = false;
        gunId = 0;
        bulletNum = 13;
    }

    public String getWebCamName() {
        return webCamName;
    }

    public String getName() {
        return name;
    }
}
