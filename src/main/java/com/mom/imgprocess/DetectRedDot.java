package com.mom.imgprocess;

import com.mom.ui.controller.MainController;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.apache.commons.lang3.StringUtils;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import java.util.ArrayList;
import java.util.List;

public class DetectRedDot implements ChangeListener<Mat> {
    static {
        nu.pattern.OpenCV.loadShared();
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
    }

    public static List<ColorBound> colorBounds;
    public List<Mat> masks;
    public List<Circle> circles;
    public Target target;
    public ArrayList<Point> shotPoint;
    private boolean applyColorFilter = true;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    private int index;

    public void setTest(boolean test) {
        this.test = test;
    }

    private boolean test = false;

    private final int showCircleR = 50;


    public DetectRedDot() {
        show = new ShowImageView();
        showTest = new ShowImageView();
        masks = new ArrayList<>();
        circles = new ArrayList<>();
        target = new Target();
        shotPoint = new ArrayList<>();
        score = new SimpleObjectProperty<>();
    }
//
//[ WARN:1] videoio(MSMF): OnReadSample() is called with error status: -1072873821
//            [ WARN:1] videoio(MSMF): async ReadSample() call is failed with error status: -1072873821
    private boolean findDot(Mat mat, Point dest) {
        Core.MinMaxLocResult result = Core.minMaxLoc(mat);
        if (Math.abs(result.maxVal) < 1)
            return false;
        dest.x = result.maxLoc.x;
        dest.y = result.maxLoc.y;
        return true;
    }

    public String getScore() {
        return score.get();
    }

    public SimpleObjectProperty<String> scoreProperty() {
        return score;
    }

    public void setScore(String score) {
        this.score.set(score);
    }

    private SimpleObjectProperty<String> score;

    @Override
    public void changed(ObservableValue<? extends Mat> observableValue, Mat o, Mat mat) {
        if (mat.empty()) {
            //print error
            return;
        }
        if (test){
            test(mat);
            return;
        }
        if (index < MainController.targets.size())
            target = MainController.targets.get(index);
        else
            return;
        if (false) {
            debug(mat);
            return;
        }
        if (!target.active)
            return;
        Mat matCopy = mat.clone();
        matCopy = cutImage(matCopy);
        Imgproc.cvtColor(matCopy, matCopy, Imgproc.COLOR_BGR2GRAY);
        reduceNoise(matCopy);
        double ret = Imgproc.threshold(matCopy, matCopy, 245.0, 255.0, Imgproc.THRESH_TOZERO);
        List<MatOfPoint> countours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(matCopy, countours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        boolean checkContours = false;
        double max = 0;
        double threshholdContour = 10;
        for (int i = 0; i < countours.size(); i++) {
            max = Imgproc.contourArea(countours.get(i)) > max ? Imgproc.contourArea(countours.get(i)) : max;
            if (max > threshholdContour) {
                checkContours = true;
                break;
            }
        }
        Mat matShow = mat.clone();
        matShow = cutImage(matShow);
        System.out.println("checking countours : " + checkContours);
        if (checkContours) {
            Point point = new Point();
            if (findDot(matCopy, point)) {
                shotPoint.add(point.clone());
//                int points = 0;
//                if (StringUtils.isNumeric(score.getValue()))
//                    points = Integer.parseInt(score.getValue());
//                points += calculatePoint(point, new Point(matCopy.size().width, matCopy.size().height));
//                score.setValue(Integer.toString(points));
            }
        }
        drawCircles(matShow, matShow.size());
        show(matShow, show);
    }


    void drawCircles(Mat mat, Size original) {
        for (int i = 0; i < shotPoint.size() - 1; i++) {
            Point pScale = new Point();
            pScale.x = shotPoint.get(i).x / original.width * mat.size().width;
            pScale.y = shotPoint.get(i).y / original.height * mat.size().height;
            Imgproc.circle(mat, pScale, showCircleR, new Scalar(0, 255, 0), -1);
        }
        if (!shotPoint.isEmpty()) {
            Point pScale = new Point();
            pScale.x = shotPoint.get(shotPoint.size() - 1).x / original.width * mat.size().width;
            pScale.y = shotPoint.get(shotPoint.size() - 1).y / original.height * mat.size().height;
            Imgproc.circle(mat, shotPoint.get(shotPoint.size() - 1), showCircleR, new Scalar(0, 0, 255), -1);
        }
    }

    double calculatePoint(Point p, Point center) {
        double temp = Math.sqrt(Math.pow(p.x - center.x, 2) + Math.pow(p.y - center.y, 2));
        double r = center.x;
        double d = Math.sqrt(temp);
        if ((d / r) < (double) (1 / 11))
            return 10;
        else if ((d / r) < (double) (3 / 11))
            return 8;
        else if ((d / r) < (double) (5 / 11))
            return 6;
        else if ((d / r) < (double) (7 / 11))
            return 4;
        else if ((d / r) < (double) (9 / 11))
            return 2;
        else return 1;
    }


//    public void test(Mat mat) {
//        Mat matTest = mat.clone();
//        Imgproc.cvtColor(matTest, matTest, Imgproc.COLOR_BGR2HSV);
//        reduceNoise(matTest);
//        Mat mask = Mat.zeros(matTest.size(), CvType.CV_8U);
//        Core.inRange(matTest, colorBoundTest.getLower(), colorBoundTest.getUpper(), mask);
//        applyMask(matTest, mask);
//        Imgproc.cvtColor(matTest, matTest, Imgproc.COLOR_HSV2BGR);
//        show(matTest, showTest);
//    }

    public void debug(Mat mat){
        Mat matCopy = mat.clone();
        matCopy = cutImage(matCopy);
        Imgproc.cvtColor(matCopy, matCopy, Imgproc.COLOR_BGR2GRAY);
        reduceNoise(matCopy);
        double ret = Imgproc.threshold(matCopy, matCopy, 245.0, 255.0, Imgproc.THRESH_TOZERO);
        List<MatOfPoint> countours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(matCopy, countours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
//        boolean checkContours = false;
//        double max = 0;
//        double threshholdContour = 10;
//        for (int i = 0; i < countours.size(); i++) {
//            max = Imgproc.contourArea(countours.get(i)) > max ? Imgproc.contourArea(countours.get(i)) : max;
//            if (max > threshholdContour){
//                checkContours = true;
//                break;
//            }
//        }
//        Mat matShow = mat.clone();
//        System.out.println("checking countours : " + checkContours);
//        if (checkContours){
//            drawCircles(matShow,matCopy.size());
//        }
        Mat matShow = mat.clone();
        matShow = cutImage(matShow);
        System.out.println("countours number = " + countours.size());
        for (int i = 0; i < countours.size(); i++) {
            MatOfPoint countour = countours.get(i);
            System.out.println("counter " + (i) + " area = " + Imgproc.contourArea(countour));
            Imgproc.drawContours(matShow, countours, i, new Scalar((255 / (countours.size())) * (i + 1), 0, 0), 5);
        }
        show(matShow, show);
    }
    public void test(Mat mat) {
        Mat matCopy = mat.clone();
        matCopy = cutImage(matCopy);
        show(matCopy, show);
    }

    private void applyMask(Mat mat, Mat mask) {
        Imgproc.cvtColor(mask, mask, Imgproc.COLOR_GRAY2BGR);
        Core.bitwise_and(mask, mat, mat);
    }

    private void show(Mat mat, ShowImageView view) {
        view.show(mat);
    }

    public Mat reduceNoise(Mat mat) {
        Imgproc.blur(mat, mat, new Size(5, 5));
        return mat;
    }

    public Mat colorBoundsFilter(Mat mat) {
        Mat mask = Mat.zeros(mat.size(), CvType.CV_8U);
        for (int i = 0; i < colorBounds.size(); i++) {
            Mat maskTemp = new Mat(mat.size(), CvType.CV_8U);
            Core.inRange(mat, colorBounds.get(i).getLower(), colorBounds.get(i).getUpper(), maskTemp);
            masks.add(maskTemp);
            Core.bitwise_or(mask, maskTemp, mask);
        }
        return mask;
    }

    public Mat cutImage(Mat mat) {
        if (target.valid) {
            Point ps0 = target.point0.clone();
            ps0.x = ps0.x * mat.size().width;
            ps0.y = ps0.y * mat.size().height;
            Point ps1 = target.point1.clone();
            ps1.x = ps1.x * mat.size().width;
            ps1.y = ps1.y * mat.size().height;
            Point ps2 = target.point2.clone();
            ps2.x = ps2.x * mat.size().width;
            ps2.y = ps2.y * mat.size().height;
            Point ps3 = target.point3.clone();
            ps3.x = ps3.x * mat.size().width;
            ps3.y = ps3.y * mat.size().height;
            Mat pts0 = new MatOfPoint2f(ps0, ps1, ps2, ps3);
            Point p0 = new Point(0, 0);
            Point p1 = new Point(mat.size().width, 0);
            Point p2 = new Point(0, mat.size().height);
            Point p3 = new Point(mat.size().width, mat.size().height);
            Mat pts1 = new MatOfPoint2f(p0, p1, p2, p3);
            Mat m = Imgproc.getPerspectiveTransform(pts0, pts1);
            Mat dest = new Mat();
            Imgproc.warpPerspective(mat, dest, m, mat.size().clone(), Imgproc.INTER_NEAREST);
            return dest;
        } else
            return mat;
    }

    public ShowImageView show;
    public ShowImageView showTest;

    public boolean isApplyColorFilter() {
        return applyColorFilter;
    }

    public void setApplyColorFilter(boolean applyColorFilter) {
        this.applyColorFilter = applyColorFilter;
    }
}
