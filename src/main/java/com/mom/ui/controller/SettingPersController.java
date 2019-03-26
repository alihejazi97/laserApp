package com.mom.ui.controller;

import com.mom.cam.CameraControl;
import com.mom.cam.WebcamInterface;
import com.mom.imgprocess.DetectRedDot;
import com.mom.imgprocess.Target;
import com.mom.persistence.GsonPersistence;
import com.mom.ui.Draggable;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SettingPersController implements Initializable, ControllerInterface {
    @FXML
    private ImageView imgVRaw, imgVSelected;

    @FXML
    private Button saveButton;

    @FXML
    private AnchorPane anchor,imgVRawAnchor,imgVSelectedAnchor;

    @FXML
    private ComboBox<String> cameraComboBox;
    @FXML
    private ComboBox<Target> targetComboBox;

    Target selectedTarget;

    public void setTargets(List<Target> Targets) {
        this.targets = Targets;
        targetComboBox.setItems(FXCollections.observableList(targets));
    }

    WebcamInterface webcam;

    private List<Target> targets;

    private DetectRedDot detectRedDot;

    CameraControl cameraControl;

    private final int CIRCLE_NUMBER = 4;

    private List<Circle> circles;
    private List<Line> lines;

    private double originX;

    private double originY;

    public void afterShow(){
        createCircles();
        if (targets.size() != 0)
            targetComboBox.getSelectionModel().select(0);
    }

    public void setDetectRedDot(DetectRedDot detectRedDot) {
        this.detectRedDot = detectRedDot;
        detectRedDot.setApplyColorFilter(false);
        detectRedDot.show.setImageView(imgVSelected);
        detectRedDot.show.setShow(true);
        detectRedDot.setTest(true);
    }

    public void putCirclesAroundImage() {
        System.out.println("putCirclesAroundImage");
        originX = imgVRaw.getLayoutX();
        originY = imgVRaw.getLayoutY();
        System.out.println("originX");
        circles.get(0).setCenterX(originX);
        circles.get(0).setCenterY(originY);
        circles.get(1).setCenterX(originX + imgVRaw.getFitWidth() * 0.1);
        circles.get(1).setCenterY(originY);
        circles.get(2).setCenterX(originX);
        circles.get(2).setCenterY(originY + imgVRaw.getFitHeight() * 0.1);
        circles.get(3).setCenterX(originX + imgVRaw.getFitWidth() * 0.1);
        circles.get(3).setCenterY(originY + imgVRaw.getFitHeight() * 0.1);
    }

    @Override
    public void shutdown() {
        if (webcam != null) {
            webcam.setShow(false);
            webcam.setImageView(null);
        }
    }

    private void updateDetectRedDotTest() {
        selectedTarget.point0.x = (circles.get(0).getCenterX() + circles.get(0).getTranslateX() - originX) / imgVRaw.getFitWidth();
        selectedTarget.point0.y = (circles.get(0).getCenterY() + circles.get(0).getTranslateY() - originY) / imgVRaw.getFitHeight();
        selectedTarget.point1.x = (circles.get(1).getCenterX() + circles.get(1).getTranslateX() - originX) / imgVRaw.getFitWidth();
        selectedTarget.point1.y = (circles.get(1).getCenterY() + circles.get(1).getTranslateY() - originY) / imgVRaw.getFitHeight();
        selectedTarget.point2.x = (circles.get(2).getCenterX() + circles.get(2).getTranslateX() - originX) / imgVRaw.getFitWidth();
        selectedTarget.point2.y = (circles.get(2).getCenterY() + circles.get(2).getTranslateY() - originY) / imgVRaw.getFitHeight();
        selectedTarget.point3.x = (circles.get(3).getCenterX() + circles.get(3).getTranslateX() - originX) / imgVRaw.getFitWidth();
        selectedTarget.point3.y = (circles.get(3).getCenterY() + circles.get(3).getTranslateY() - originY) / imgVRaw.getFitHeight();
        selectedTarget.valid = true;
    }

    private void updateInvalidTargetPoints() {
        selectedTarget.point0.x = 0;
        selectedTarget.point0.y = 0;
        selectedTarget.point1.x = 0.1;
        selectedTarget.point1.y = 0;
        selectedTarget.point2.x = 0;
        selectedTarget.point2.y =  0.1;
        selectedTarget.point3.x = 0.1;
        selectedTarget.point3.y = 0.1;
        selectedTarget.valid = true;
    }

    public void updateCamera(String camName) {
        if (webcam != null) {
            webcam.setImageView(null);
            webcam.setShow(false);
            webcam.removeListener(detectRedDot);
        }
        if (camName != null && cameraControl.getCamera(camName) != null) {
            webcam = cameraControl.getCamera(camName);
            webcam.setImageView(imgVRaw);
            webcam.setShow(true);
            webcam.addListener(detectRedDot);
        }
    }

    public void createCircles(){
        for (int i = 0; i < CIRCLE_NUMBER; i++) {
            Circle circle = new Circle();
            circle.setFill(Color.rgb(255, 255, 255, 0.5));
            circle.setRadius(8);
            circle.setStroke(Color.LIGHTBLUE);
            circle.setStrokeWidth(1);
            circle.setVisible(false);
            new Draggable.Nature(circle);
            circle.addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseEvent -> {
                if (targetComboBox.getValue() != null) {
                    updateDetectRedDotTest();
                } else {
                }
            });
            circles.add(circle);
        }
        putCirclesAroundImage();
        for (int i = 0; i < CIRCLE_NUMBER; i++) {
            imgVRawAnchor.getChildren().add(circles.get(i));
        }
        for (int i = 0; i < CIRCLE_NUMBER; i++) {
            Line line = new Line();
            line.setFill(Color.LIGHTBLUE);
            line.setStroke(Color.LIGHTBLUE);
            line.setStrokeWidth(2);
            line.setVisible(false);
            imgVRawAnchor.getChildren().add(line);
            lines.add(line);
        }

        lines.get(0).setStartX(circles.get(0).getCenterX());
        lines.get(0).setStartX(circles.get(0).getCenterY());
        lines.get(0).setEndX(circles.get(1).getCenterY());
        lines.get(0).setEndY(circles.get(1).getCenterY());
        lines.get(1).setStartX(circles.get(0).getCenterX());
        lines.get(1).setStartX(circles.get(0).getCenterY());
        lines.get(1).setEndX(circles.get(2).getCenterY());
        lines.get(1).setEndY(circles.get(2).getCenterY());
        lines.get(2).setStartX(circles.get(1).getCenterX());
        lines.get(2).setStartX(circles.get(1).getCenterY());
        lines.get(2).setEndX(circles.get(3).getCenterY());
        lines.get(2).setEndY(circles.get(3).getCenterY());
        lines.get(3).setStartX(circles.get(2).getCenterX());
        lines.get(3).setStartX(circles.get(2).getCenterY());
        lines.get(3).setEndX(circles.get(3).getCenterY());
        lines.get(3).setEndY(circles.get(3).getCenterY());
        ///////////////////////////
        lines.get(0).startXProperty().bind(circles.get(0).centerXProperty().add(circles.get(0).translateXProperty()));
        lines.get(0).startYProperty().bind(circles.get(0).centerYProperty().add(circles.get(0).translateYProperty()));
        lines.get(0).endXProperty().bind(circles.get(1).centerXProperty().add(circles.get(1).translateXProperty()));
        lines.get(0).endYProperty().bind(circles.get(1).centerYProperty().add(circles.get(1).translateYProperty()));
        lines.get(1).startXProperty().bind(circles.get(0).centerXProperty().add(circles.get(0).translateXProperty()));
        lines.get(1).startYProperty().bind(circles.get(0).centerYProperty().add(circles.get(0).translateYProperty()));
        lines.get(1).endXProperty().bind(circles.get(2).centerXProperty().add(circles.get(2).translateXProperty()));
        lines.get(1).endYProperty().bind(circles.get(2).centerYProperty().add(circles.get(2).translateYProperty()));
        lines.get(2).startXProperty().bind(circles.get(1).centerXProperty().add(circles.get(1).translateXProperty()));
        lines.get(2).startYProperty().bind(circles.get(1).centerYProperty().add(circles.get(1).translateYProperty()));
        lines.get(2).endXProperty().bind(circles.get(3).centerXProperty().add(circles.get(3).translateXProperty()));
        lines.get(2).endYProperty().bind(circles.get(3).centerYProperty().add(circles.get(3).translateYProperty()));
        lines.get(3).startXProperty().bind(circles.get(2).centerXProperty().add(circles.get(2).translateXProperty()));
        lines.get(3).startYProperty().bind(circles.get(2).centerYProperty().add(circles.get(2).translateYProperty()));
        lines.get(3).endXProperty().bind(circles.get(3).centerXProperty().add(circles.get(3).translateXProperty()));
        lines.get(3).endYProperty().bind(circles.get(3).centerYProperty().add(circles.get(3).translateYProperty()));
    }

    private void updateCircles(Target target) {
        circles.get(0).setTranslateX((target.point0.x * imgVRaw.getFitWidth()) + originX - circles.get(0).getCenterX());
        circles.get(0).setTranslateY((target.point0.y * imgVRaw.getFitHeight()) + originY - circles.get(0).getCenterY());
        circles.get(1).setTranslateX((target.point1.x * imgVRaw.getFitWidth()) + originX - circles.get(1).getCenterX());
        circles.get(1).setTranslateY((target.point1.y * imgVRaw.getFitHeight()) + originY - circles.get(1).getCenterY());
        circles.get(2).setTranslateX((target.point2.x * imgVRaw.getFitWidth()) - circles.get(2).getCenterX() + originX);
        circles.get(2).setTranslateY((target.point2.y * imgVRaw.getFitHeight()) - circles.get(2).getCenterY() + originY);
        circles.get(3).setTranslateX((target.point3.x * imgVRaw.getFitWidth()) - circles.get(3).getCenterX() + originX);
        circles.get(3).setTranslateY((target.point3.y * imgVRaw.getFitHeight()) - circles.get(3).getCenterY() + originY);
        updateDetectRedDotTest();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lines = new ArrayList<>();
        circles = new ArrayList<>();
        cameraControl = CameraControl.getInstance();
        cameraComboBox.setItems(FXCollections.observableList(cameraControl.getCameraNames()));
        targetComboBox.getSelectionModel().selectedIndexProperty().addListener((observableValue, oldVal, newVal) -> {
            selectedTarget = targets.get(newVal.intValue());
            detectRedDot.target = selectedTarget;
            if (!selectedTarget.valid) {
                updateInvalidTargetPoints();
            }
            updateCircles(selectedTarget);
            for (int i = 0; i < circles.size(); i++) {
                circles.get(i).setVisible(true);
                lines.get(i).setVisible(true);
            }
            cameraComboBox.getSelectionModel().clearSelection();
            if (selectedTarget.webCamName != null){
                for (String s:
                     cameraComboBox.getItems()) {
                    if (selectedTarget.webCamName.equals(s)) {
                        cameraComboBox.getSelectionModel().select(s);
                        break;
                    }
                }
            }
        });
        cameraComboBox.getSelectionModel().selectedIndexProperty().addListener((observableValue, oldVal, newVal) -> {
            if (selectedTarget == null) {
                //show error message
            } else {
                if (selectedTarget.webCamName != null) {
                    updateCamera(cameraComboBox.getValue());
                    if (cameraComboBox.getValue() != null)
                        selectedTarget.webCamName = cameraComboBox.getValue();
                }
            }
        });
        saveButton.setOnMouseClicked(mouseEvent -> {
            GsonPersistence.persist2(targets);
        });
    }
}