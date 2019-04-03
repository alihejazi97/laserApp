package com.mom.ui.controller;

import com.mom.cam.CameraControl;
import com.mom.imgprocess.DetectRedDot;
import com.mom.cam.WebcamInterface;
import com.mom.imgprocess.Target;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class TargetController implements Initializable, ControllerInterface {

    CameraControl cameraControl;

    @FXML
    Button resetButton;

    @FXML
    Pane pane;

    @FXML
    VBox vBox;
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
        targetText.setText(target.toString());
        scoreText.textProperty().bind(detectRedDot.scoreProperty());
        remainBulletText.textProperty().bind(detectRedDot.remainBulletProperty());
        Color color = Color.hsb(360.0 / Target.TARGET_NUMBER * (detectRedDot.getIndex() + 1), 1.0,1.0);
        BackgroundFill background_fill = new BackgroundFill(color,
                CornerRadii.EMPTY, Insets.EMPTY);
        Background background = new Background(background_fill);
        vBox.setBackground(background);
        resetButton.setOnMouseClicked(mouseEvent -> {
            detectRedDot.clear();
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cameraControl = CameraControl.getInstance();
        imgVTarget.fitWidthProperty().bind(pane.widthProperty());
        imgVTarget.fitHeightProperty().bind(pane.heightProperty());
    }

    public void shutdown(){
        if (webcamInterface != null){
            webcamInterface.removeListener(detectRedDot);
        }

    }
}
