package com.mom.ui.controller;

import com.mom.cam.CameraControl;
import com.mom.imgprocess.DetectRedDot;
import com.mom.cam.WebcamInterface;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;

import java.net.URL;
import java.util.ResourceBundle;

public class TargetController implements Initializable, ControllerInterface {

    CameraControl cameraControl;

    @FXML
    AnchorPane rootPane;

    @FXML
    TilePane tilePane;

    @FXML
    ImageView imgVTarget;

    @FXML
    private Label scoreText,remainBulletText,targetText,numBulletText,gunText;

    private DetectRedDot detectRedDot;

    public void setDetectRedDot(DetectRedDot detectRedDot) {
        this.detectRedDot = detectRedDot;
        detectRedDot.show.setImageView(imgVTarget);
        detectRedDot.show.setShow(true);
        WebcamInterface webcamInterface = cameraControl.getCamera(detectRedDot.target.webCamName);
        webcamInterface.startCamera();
        webcamInterface.addListener(detectRedDot);
        detectRedDot.points.addListener((observableValue, number, t1) -> scoreText.setText(t1.toString()));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cameraControl = CameraControl.getInstance();
        imgVTarget.fitWidthProperty().bind(rootPane.widthProperty());
        imgVTarget.fitHeightProperty().bind(rootPane.heightProperty());
    }

    public void shutdown(){

    }
}
