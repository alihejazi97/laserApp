package com.mom.cam;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.image.Image;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.videoio.VideoCapture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.scene.image.ImageView;
import org.opencv.videoio.Videoio;


class OpenCVWebCam implements WebcamInterface {
    private ScheduledExecutorService timer;
    private VideoCapture capture;
    private boolean active = false;
    private int camID;
    private long initialDelay;
    private long FPS = -1;
    private boolean show = false;
    private ImageView imageView;
    private ObjectProperty<Mat> shareFrame;

    public void addListener(ChangeListener listener) {
        shareFrame.addListener(listener);
    }

    public void removeListener(ChangeListener listener) {
        shareFrame.removeListener(listener);
    }

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }


    public OpenCVWebCam(int camID) {
        this.camID = camID;
        initialDelay = 0;
        capture = new VideoCapture(camID);
        shareFrame = new SimpleObjectProperty<>();
    }

    String camSt;
    public OpenCVWebCam(String camSt,int FPS) {
        this.camSt = camSt;
        initialDelay = 0;
        capture = new VideoCapture(camSt);
        this.FPS = FPS;
        camID = -1;
        shareFrame = new SimpleObjectProperty<>();
    }

    public synchronized void startCamera() {
        if (!this.active) {
            if (camID >= 0){
                this.capture.open(camID);
                FPS = (long) capture.get(Videoio.CAP_PROP_FPS);
            }
            if (this.capture.isOpened()) {
                this.active = true;
                Runnable frameGrabber = () -> {
                    try {
                        if ((shareFrame.getValue() != null) && !shareFrame.getValue().empty())
                            shareFrame.getValue().release();
                        Mat frame = grabFrame();
                        if (!frame.empty()) {
                            show(frame);
                            shareFrame.setValue(frame);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                };
                this.timer = Executors.newSingleThreadScheduledExecutor();
                this.timer.scheduleAtFixedRate(frameGrabber, initialDelay, 1000 / FPS, TimeUnit.MILLISECONDS);
            }

            if (active) {
                if (camID >= 0)
                    System.out.println("camera " + camID + " started.");
                else
                    System.out.println("camera " + camSt + " started.");
            } else
                System.err.println("Impossible to open the camera connection...");

        }
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    private void show(Mat frame) {
        if (show) {
            Image imageToShow = Utils.mat2Image(frame);
            updateImageView(imageView, imageToShow);
        }
    }

    private Mat grabFrame() {
        Mat frame = new Mat();
        boolean valid = false;
        if (this.capture.isOpened()) {
            try {
                if (camID != -1)
                    valid = this.capture.read(frame);
                else{
                    valid = this.capture.grab();
                    valid = this.capture.read(frame);
                }
                if (!valid){
                    if (camID >= 0)
                        System.out.println("camera " + camID + " invalid frame.");
                    else
                        System.out.println("camera " + camSt + " invalid frame.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Exception during the image elaboration: " + e);
            }
        }
        return valid ? frame : new Mat(600,600, CvType.CV_8UC3,new Scalar(0,0,0));
    }


    /**
     * Stop the acquisition from the camera and release all the resources
     */
    private void stopAcquisition() {
        if (this.timer != null && !this.timer.isShutdown()) {
            try {
                if (camID >= 0)
                    System.out.println("shutting down camera " + camID + ".");
                else
                    System.out.println("shutting down camera " + camSt + ".");
                this.timer.shutdown();
                this.timer.awaitTermination(1000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
                e.printStackTrace();
            }
        }

        if (this.capture.isOpened()) {
            this.capture.release();
        }
    }


    private void updateImageView(ImageView view, Image image) {
        Utils.onFXThread(view.imageProperty(), image);
    }


    public synchronized void stopCamera() {
        this.stopAcquisition();
    }
}
