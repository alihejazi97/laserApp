package com.mom.imgprocess;

import com.mom.ui.App;
import com.mom.ui.controller.MainController;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DetectRedDot implements ChangeListener<Mat> {
    static {
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
    }

    public List<Mat> masks;
    public List<Circle> circles;
    public Target target;
    public ArrayList<Pair<Point,Integer> > shotPoint;

    public ShowImageView show;
    public ShowImageView showTest;

    public static double LASER_THRESHOLD;

    public static double LASER_AREA_THRESHOLD;

    private int index;

    private boolean test = false;

    private final double showCircleR = 0.02;

    private StringProperty remainBullet;

    private Mat targetImage;

    private StringProperty score;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
        remainBullet.setValue(Integer.toString(MainController.targets.get(index).bulletNum));
    }

    public void setTest(boolean test) {
        this.test = test;
    }

    public DetectRedDot() {
        show = new ShowImageView();
        showTest = new ShowImageView();
        masks = new ArrayList<>();
        circles = new ArrayList<>();
        target = new Target();
        shotPoint = new ArrayList<>();
        score = new SimpleStringProperty("0");
        remainBullet = new SimpleStringProperty("0");
        String out = Paths.get(".").toAbsolutePath().normalize().toString();
        out = out.replace("/","\\");
        out = out + "\\target.png";
        targetImage = Imgcodecs.imread(out);
    }

    private boolean findDot(Mat mat, Point dest) {
        Core.MinMaxLocResult result = Core.minMaxLoc(mat);
        if (Math.abs(result.maxVal) < 1){
            return false;
        }
        dest.x = result.maxLoc.x;
        dest.y = result.maxLoc.y;
        return true;
    }

    public StringProperty scoreProperty() {
        return score;
    }


    public StringProperty remainBulletProperty() {
        return remainBullet;
    }


    boolean firstTime = true;

    private void configLightValue(Mat mat){
        for (int i = 0; i <= 245 ; i++) {
            Mat matCopy = mat.clone();
            matCopy = cutImage(matCopy);
            if (matCopy.channels() != 1)
                Imgproc.cvtColor(matCopy, matCopy, Imgproc.COLOR_BGR2GRAY);
            reduceNoise(matCopy);
            Imgproc.threshold(matCopy, matCopy, i, 255.0, Imgproc.THRESH_TOZERO);
            List<MatOfPoint> countours = new ArrayList<>();
            Mat hierarchy = new Mat();
            Imgproc.findContours(matCopy, countours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
            boolean checkContours = false;
            double max = 0;
            for (int j = 0; j < countours.size(); j++) {
                System.out.println("a = " + Imgproc.contourArea(countours.get(j)));
                max = Imgproc.contourArea(countours.get(j)) > max ? Imgproc.contourArea(countours.get(j)) : max;
                if (max > LASER_AREA_THRESHOLD) {
                    checkContours = true;
                    break;
                }
            }
            matCopy.release();
            if (!checkContours){
                LASER_THRESHOLD = i + 10;
                System.out.println("LASER_THRESHOLD = " + LASER_THRESHOLD);
                return;
            }
        }
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("توجه");
            alert.setHeaderText("");
            alert.setContentText("نور محیط زیاد است.");
            alert.initModality(Modality.WINDOW_MODAL);
            alert.showAndWait();
        });

    }

    @Override
    public void changed(ObservableValue<? extends Mat> observableValue, Mat o, Mat mat) {
        if (test){
            test(mat);
            return;
        }
        if (index < MainController.targets.size())
            target = MainController.targets.get(index);
        else
            return;
        if (App.config) {
            debug(mat);
            return;
        }
        if (firstTime) {
            configLightValue(mat);
            firstTime = false;
            return;
        }
        if (!target.gunSignal)
            return;
        if (StringUtils.isNumeric(remainBulletProperty().getValue())) {
            int remainBullet = Integer.parseInt(remainBulletProperty().getValue()) - 1;
            if (remainBullet >= 0)
                Platform.runLater(() -> remainBulletProperty().setValue(Integer.toString(remainBullet)));
            else
                target.active = false;
        }
        Mat matCopy = mat.clone();
        matCopy = cutImage(matCopy);
        if (matCopy.channels() != 1)
            Imgproc.cvtColor(matCopy, matCopy, Imgproc.COLOR_BGR2GRAY);
        reduceNoise(matCopy);
        Imgproc.threshold(matCopy, matCopy, LASER_THRESHOLD, 255.0, Imgproc.THRESH_TOZERO);
        List<MatOfPoint> countours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(matCopy, countours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        boolean checkContours = false;
        double max = 0;
        for (int i = 0; i < countours.size(); i++) {
            System.out.println("a = " + Imgproc.contourArea(countours.get(i)));
            max = Imgproc.contourArea(countours.get(i)) > max ? Imgproc.contourArea(countours.get(i)) : max;
            if (max > LASER_AREA_THRESHOLD) {
                checkContours = true;
                break;
            }
        }
        System.out.println("max " + max);
        Mat matShow = targetImage.clone();
        if (checkContours) {
            Point point = new Point();
            if (findDot(matCopy, point)) {
                shotPoint.add(new Pair<>(point.clone(),15 - Integer.parseInt(remainBullet.getValue())));
                int points = 0;
                if (StringUtils.isNumeric(score.getValue()))
                    points = Integer.parseInt(score.getValue());
                points += calculatePoint(point, new Point(matCopy.size().width / 2, matCopy.size().height / 2));
                final int finalPoints = points;
                Platform.runLater(() -> score.setValue(Integer.toString(finalPoints)));
            }
        }
        drawCircles(matShow, matCopy.size());
        show(matShow, show);
        target.gunSignal = false;
        matCopy.release();
        matShow.release();
    }


    void drawCircles(Mat mat, Size original) {
        for (int i = 0 ; i < shotPoint.size() - 1; i++) {
            Point pScale = new Point();
            pScale.x = shotPoint.get(i).getKey().x / original.width * mat.size().width;
            pScale.y = shotPoint.get(i).getKey().y / original.height * mat.size().height;
            Imgproc.circle(mat, pScale, (int) (showCircleR * mat.size().width), new Scalar(0, 255 / shotPoint.size() * (i + 1), 0), -1);
            Point pScaleText = new Point();
            pScaleText.x = (shotPoint.get(i).getKey().x - (int) (showCircleR * mat.size().width)) / original.width * mat.size().width;
            pScaleText.y = (shotPoint.get(i).getKey().y + (int) (showCircleR * mat.size().width)) / original.height * mat.size().height;
            Imgproc.putText(mat,Integer.toString(shotPoint.get(i).getValue()),pScaleText,0,0.5,new Scalar(255,255,255));

        }
        if (!shotPoint.isEmpty()) {
            Point pScale = new Point();
            pScale.x = shotPoint.get(shotPoint.size() - 1).getKey().x / original.width * mat.size().width;
            pScale.y = shotPoint.get(shotPoint.size() - 1).getKey().y / original.height * mat.size().height;
            Imgproc.circle(mat, pScale, (int) (showCircleR * mat.size().width), new Scalar(0, 0, 255), -1);
            Point pScaleText = new Point();
            pScaleText.x = (shotPoint.get(shotPoint.size() - 1).getKey().x - (int) (showCircleR * mat.size().width)) / original.width * mat.size().width;
            pScaleText.y = (shotPoint.get(shotPoint.size() - 1).getKey().y + (int) (showCircleR * mat.size().width)) / original.height * mat.size().height;
            Imgproc.putText(mat,Integer.toString(shotPoint.get(shotPoint.size() - 1).getValue()),pScaleText,0,0.5,new Scalar(255,255,255));
        }
    }

    public void clear(){
        shotPoint.clear();
        Platform.runLater(() -> {
            remainBulletProperty().setValue(Integer.toString(target.bulletNum));
            scoreProperty().setValue("0");
        });
        show(targetImage.clone(),show);
        target.active = true;
    }

    double calculatePoint(Point p, Point center) {
        double temp = Math.sqrt(Math.pow(p.x - center.x, 2) + Math.pow(p.y - center.y, 2));
        double r = center.x;
        double d = temp;
        if ((d / r) <  (1.0 / 9.0))
            return 10;
        else if ((d / r) <  (3.0 / 9.0))
            return 8;
        else if ((d / r) < (5.0 / 9.0))
            return 6;
        else if ((d / r) <  (7.0 / 9.0))
            return 4;
        else if ((d / r) <  (9.0 / 9.0))
            return 2;
        else return 1;
    }



    public void debug(Mat mat){
        Mat matCopy = mat.clone();
        matCopy = cutImage(matCopy);
        if (matCopy.channels() != 1)
        Imgproc.cvtColor(matCopy, matCopy, Imgproc.COLOR_BGR2GRAY);
        reduceNoise(matCopy);
        Imgproc.threshold(matCopy, matCopy, LASER_THRESHOLD, 255.0, Imgproc.THRESH_TOZERO);
        List<MatOfPoint> countours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(matCopy, countours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        Mat matShow = mat.clone();
        matShow = cutImage(matShow);
        System.out.println("countours number = " + countours.size());
        for (int i = 0; i < countours.size(); i++) {
            MatOfPoint countour = countours.get(i);
            System.out.println("counter " + (i) + " area = " + Imgproc.contourArea(countour));
            if (Imgproc.contourArea(countour) > LASER_AREA_THRESHOLD)
                Imgproc.drawContours(matShow, countours, i, new Scalar((255 / (countours.size())) * (i + 1), 0, 0), 5);
        }
        System.out.println("laser light = " + LASER_THRESHOLD);
        System.out.println("laser are = " + LASER_AREA_THRESHOLD);
        matCopy.release();
        show(matShow, show);
        matShow.release();
    }
    public void test(Mat mat) {
        Mat matCopy = mat.clone();
        matCopy = cutImage(matCopy);
        show(matCopy, show);
        matCopy.release();
    }

    private void show(Mat mat, ShowImageView view) {
        view.show(mat);
    }

    public Mat reduceNoise(Mat mat) {
        Imgproc.blur(mat, mat, new Size(5, 5));
        return mat;
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
}
