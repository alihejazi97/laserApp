package com.mom.ui.controller;

import com.mom.cam.CameraControl;
import com.mom.imgprocess.DetectRedDot;
import com.mom.cam.WebcamInterface;
import com.mom.imgprocess.Target;
import com.mom.persistence.GsonPersistence;
import com.mom.ui.App;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
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
        if (webcamInterface != null) {
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
        if (App.config){
            resetButton.setText("ذخیره تنظیمات");
            resetButton.setOnMouseClicked(mouseEvent -> GsonPersistence.persist(MainController.targets));
        }
        else
            resetButton.setOnMouseClicked(mouseEvent -> detectRedDot.clear());

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cameraControl = CameraControl.getInstance();
        imgVTarget.fitWidthProperty().bind(pane.widthProperty());
        imgVTarget.fitHeightProperty().bind(pane.heightProperty());
        if (App.config){
            drawConfigSlider();
        }
    }

    private void drawConfigSlider(){
        Slider sliderLV = new Slider(0.0,255.0,DetectRedDot.LASER_THRESHOLD);
        Slider sliderLA = new Slider(0.0,2000.0,DetectRedDot.LASER_AREA_THRESHOLD);
        Label SliderLVlabel = new Label("میزان شدت نور لیزر : " + String.format("%.2f",DetectRedDot.LASER_THRESHOLD));
        Label SliderLAlabel = new Label("مساحت لیزر : " + String.format("%.0f",DetectRedDot.LASER_AREA_THRESHOLD));
        sliderLV.valueProperty().addListener(observable ->{
            DetectRedDot.LASER_THRESHOLD = sliderLV.getValue();
            SliderLVlabel.setText("میزان شدت نور لیزر : " + String.format("%.2f",DetectRedDot.LASER_THRESHOLD));
        });
        sliderLA.valueProperty().addListener(observable -> {
            DetectRedDot.LASER_AREA_THRESHOLD = sliderLA.getValue();
            SliderLAlabel.setText("مساحت لیزر : " + String.format("%.0f",DetectRedDot.LASER_AREA_THRESHOLD));
            });

        vBox.getChildren().add(SliderLAlabel);
        vBox.getChildren().add(sliderLA);
        vBox.getChildren().add(SliderLVlabel);
        vBox.getChildren().add(sliderLV);
    }
    public void shutdown(){
        if (webcamInterface != null) {
            webcamInterface.removeListener(detectRedDot);
        }
    }
}
