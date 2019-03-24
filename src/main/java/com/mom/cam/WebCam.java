package com.mom.cam;

import com.github.sarxos.webcam.Webcam;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import org.opencv.core.Mat;

import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.concurrent.atomic.AtomicReference;

class WebCam extends Observable {
    public Webcam webCam;

    private ObjectProperty<Mat> shareFrame;


    public WebCam(Webcam webcam) {
        if (webcam != null)
            this.webCam = webcam;
    }

    private boolean stopCamera = false;
    private ObjectProperty<Image> imageProperty = new SimpleObjectProperty<Image>();

    public void startWebCamStream(ImageView view,BufferedImage grabbedImage) {

        stopCamera = false;

        Task<Void> task = new Task<Void>() {

            @Override
            protected Void call() {
                final AtomicReference<WritableImage> ref = new AtomicReference<>();
                BufferedImage img = null;



                while (!stopCamera) {
                    try {
                        if ((img = webCam.getImage()) != null) {
                            grabbedImage.flush();
                            grabbedImage.setData(img.getData());
                            ref.set(SwingFXUtils.toFXImage(img, ref.get()));
                            img.flush();
                            Platform.runLater(() -> imageProperty.set(ref.get()));
                        }
                    } catch (Exception e) {

                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("WebCam Warning");
                        alert.setHeaderText("WebCam " + webCam.getName() + " is unavailable.");
                        alert.setContentText("Check for if your WebCam is connected.");

                        alert.showAndWait();
                        e.printStackTrace();
                    }
                }
                return null;
            }
        };

        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
        view.imageProperty().bind(imageProperty);
    }

}
