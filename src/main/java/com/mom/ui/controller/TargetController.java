package com.mom.ui.controller;

import com.mom.cam.CameraControl;
import com.mom.imgprocess.DetectRedDot;
import com.mom.cam.WebcamInterface;
import com.mom.imgprocess.Target;
import javafx.beans.property.SimpleObjectProperty;
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
        afterShow();
    }

    WebcamInterface webcamInterface;

    public void afterShow(){
        Target target = MainController.targets.get(detectRedDot.getIndex());
        webcamInterface = cameraControl.getCamera(target.getWebCamName());
        if (webcamInterface != null)
        {
            webcamInterface.startCamera();
            webcamInterface.addListener(detectRedDot);
            scoreText.textProperty().bind(detectRedDot.scoreProperty());
        }
        gunText.setText(Integer.toString(target.gunId));
        numBulletText.setText(Integer.toString(target.bulletNum));
        targetText.setText(target.name);
        scoreText.textProperty().bind(detectRedDot.scoreProperty());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cameraControl = CameraControl.getInstance();
        imgVTarget.fitWidthProperty().bind(rootPane.widthProperty());
        imgVTarget.fitHeightProperty().bind(rootPane.heightProperty());
    }

    public void shutdown(){
        if (webcamInterface != null){
            webcamInterface.removeListener(detectRedDot);
        }
    }
}
