package com.mom.cam;

import com.mom.BoardConnection.Arduino;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.image.Image;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.scene.image.ImageView;
import org.opencv.videoio.Videoio;


class OpenCVWebCam implements WebcamInterface{
    private ScheduledExecutorService timer;
    private VideoCapture capture = new VideoCapture();
    private boolean active = false;
    private int camID = 0;
    private long initialDelay = 0;
    private long FPS = -1;
    private boolean show = false;
    private ImageView imageView;
    private ObjectProperty<Mat> shareFrame;
    boolean filmMode = true;
    boolean ardiunoMode = false;

    public void addListener(ChangeListener listener){
        shareFrame.addListener(listener);
    }

    public void removeListener(ChangeListener listener){shareFrame.removeListener(listener);}

    static {
        nu.pattern.OpenCV.loadShared();
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }


    public OpenCVWebCam(int camID) {
        this.camID = camID;
        initialDelay = 0;
        shareFrame = new SimpleObjectProperty<>();
    }

    private String camSt;

    public OpenCVWebCam(String camSt) {
        camID = -1;
        this.camSt = camSt;
        initialDelay = 0;
        shareFrame = new SimpleObjectProperty<>();
    }

    public OpenCVWebCam(int camID, long initialDelay) {
        this.camID = camID;
        this.initialDelay = initialDelay;
        FPS = (long) capture.get(Videoio.CV_CAP_PROP_FPS);
        shareFrame = new SimpleObjectProperty<>();
    }

    public OpenCVWebCam(int camID, long initialDelay, long FPS) {
        this.camID = camID;
        this.initialDelay = initialDelay;
        this.FPS = FPS;
        shareFrame = new SimpleObjectProperty<>();
    }

    private OpenCVWebCam() {
    }

    public synchronized void startCamera(){
        if (!this.active)
        {
            if (camID == -1)
                this.capture.open(camSt);
            else
                this.capture.open(camID);
            FPS = (long) capture.get(Videoio.CV_CAP_PROP_FPS);
            if (this.capture.isOpened()) {
                this.active = true;
                Runnable frameGrabber = () -> {
                    try {
                        Mat frame = grabFrame();
                        show(frame);
                        shareFrame.setValue(frame);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                };
                this.timer = Executors.newSingleThreadScheduledExecutor();
                this.timer.scheduleAtFixedRate(frameGrabber, initialDelay, 1000 / FPS, TimeUnit.MILLISECONDS);
            }
            {
                if(active)
                    System.out.println("camera is gunSignal.");
                else
                    System.err.println("Impossible to open the camera connection...");
            }
        }
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    private void show(Mat frame){
        if (show){
            Image imageToShow = Utils.mat2Image(frame);
            updateImageView(imageView, imageToShow);
        }
    }

    private Mat grabFrame()
    {
        Mat frame = new Mat();
        boolean shit = false;
        if (this.capture.isOpened())
        {
            try
            {
                shit = this.capture.read(frame);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                System.err.println("Exception during the image elaboration: " + e);
            }
        }
        return shit ? frame : new Mat();
    }


    /**
     * Stop the acquisition from the camera and release all the resources
     */
    private void stopAcquisition()
    {
        if (this.timer !=null && !this.timer.isShutdown())
        {
            try
            {
                System.out.println("shutting down");
                // stop the timer
                this.timer.shutdown();
                this.timer.awaitTermination(FPS, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException e)
            {
                System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
                e.printStackTrace();
            }
        }

        if (this.capture.isOpened())
        {
            this.capture.release();
        }
    }


    private void updateImageView(ImageView view, Image image)
    {
        Utils.onFXThread(view.imageProperty(), image);
    }


    public synchronized void stopCamera()
    {
        this.stopAcquisition();
    }
}
