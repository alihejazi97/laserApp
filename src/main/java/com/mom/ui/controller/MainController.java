
package com.mom.ui.controller;

import com.mom.BoardConnection.Arduino;
import com.mom.cam.CameraControl;
import com.mom.imgprocess.DetectRedDot;
import com.mom.imgprocess.Target;
import com.mom.persistence.GsonPersistence;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable, ControllerInterface {

    @FXML
    HBox activeTargetHbox;

    @FXML
    private Button shooting0Button, shooting1Button, shooting2Button;

    @FXML
    private MenuItem targetMenuItem, advanceSettingMenuItem, preferencesMenuItem, gunMenuItem,aboutUsMenuItem;

    private FXMLLoader fxmlLoader;

    private List<Stage> stages;

    private List<CheckBox> boxes;

    public static List<Target> targets;

    public Arduino arduino;

    public CameraControl cameraControl;

    public void setActiveTargetHbox(boolean reset) {
        if (reset){
            for (int i = 0; i < boxes.size(); i++) {
                activeTargetHbox.getChildren().clear();
            }
            boxes.clear();
        }
        targets = GsonPersistence.load2();
        for (int i = 0; i < Target.GUN_NUMBER; i++) {
            CheckBox checkBox = new CheckBox(Integer.toString(i));
            checkBox.setStyle("-fx-padding: 8 8 8 8");
            boxes.add(checkBox);
            activeTargetHbox.getChildren().add(checkBox);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        targets = GsonPersistence.load2();
        arduino = Arduino.getInstance();
        stages = new ArrayList<>();
        boxes = new ArrayList<>();
        arduino.startShooting();
        System.out.println(Target.TARGET_NUMBER);
        cameraControl = CameraControl.getInstance();
        setActiveTargetHbox(false);

        aboutUsMenuItem.setOnAction(actionEvent -> {
            Stage stage = new Stage();
            Label textArea = new Label();
            AnchorPane pane = new AnchorPane();
            Scene scene = new Scene(pane,530,300);
            pane.getChildren().add(textArea);
            textArea.setAlignment(Pos.CENTER);
            textArea.setTextAlignment(TextAlignment.RIGHT);
            String s = "\n" +
                    "\n" +
                    "این شبیه ساز به سفارش معاونت أموزش نیروی هوافضا طراحی و ساخته شده است." +
                    "\n" +
                    "\n" +
                    "سید علی فاضل     ۰۹۱۳۲۶۰۱۷۵۶" +
                    "\n" +
                    "\n" +
                    "۱/۱/۱۳۹۸" +
                    "\n" +
                    "\n" +
                    "نسخه ی ۱.۲";
            textArea.setText(s);
            pane.setStyle("-fx-font-family: \"B Lotus\";\n" +
                    "    -fx-font-size: 20;");
            stage.setScene(scene);
            stage.show();

        });

        targetMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            private SettingTargetController controller;

            @Override
            public void handle(ActionEvent actionEvent) {
                closeTargetWindows();
                Pair<Stage, ControllerInterface> pair = loadLayoutController("settingTarget.fxml");
                controller = ((SettingTargetController) pair.getValue());
                controller.setDetectRedDot(new DetectRedDot());
                pair.getKey().initModality(Modality.APPLICATION_MODAL);
                pair.getKey().show();
                controller.afterShow();
            }
        });

        preferencesMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            preferenceControl controller;
            Stage stage;

            @Override
            public void handle(ActionEvent actionEvent) {
                closeTargetWindows();
                Pair<Stage, ControllerInterface> pair = loadLayoutController("preference.fxml");
                controller = ((preferenceControl) pair.getValue());
                stage = pair.getKey();
                stages.add(stage);
                stage.setOnCloseRequest(windowEvent -> setActiveTargetHbox(true));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.show();
                controller.setTargets(targets);
            }
        });

        shooting0Button.setOnMouseClicked(new EventHandler<MouseEvent>() {
            TargetController controller;

            @Override
            public void handle(MouseEvent mouseEvent) {
                closeTargetWindows();
                initializeDetection();
                Stage stage;
                for (int i = 0; i < Target.TARGET_NUMBER; i++) {
                    if (!targets.get(i).active)
                        continue;
                    if (!(targets.get(i).valid)) {
                        System.out.println("target " + targets.get(i).getName() + " is invalid");
                        continue;
                    }
                    if (cameraControl.getCamera(targets.get(i).webCamName) == null) {
                        System.out.println("target " + targets.get(i).getName() + " camera is not connected.");
                        continue;
                    }

                    Pair<Stage, ControllerInterface> pair = loadLayoutController("target.fxml");
                    controller = ((TargetController) pair.getValue());
                    stage = pair.getKey();
                    DetectRedDot detectRedDot = new DetectRedDot();
                    detectRedDot.setIndex(i);
                    controller.setDetectRedDot(detectRedDot);
                    stages.add(stage);
                    stage.show();
                    arduino.setActive(true);
                }
            }
        });

        gunMenuItem.setOnAction(new EventHandler<>() {
            private SettingGunController controller;
            Stage stage;

            @Override
            public void handle(ActionEvent actionEvent) {
                closeTargetWindows();
                Pair<Stage, ControllerInterface> pair = loadLayoutController("settingGun.fxml");
                controller = ((SettingGunController) pair.getValue());
                stage = pair.getKey();
                stage.initModality(Modality.APPLICATION_MODAL);
                pair.getKey().show();
                controller.afterShow();
            }
        });
    }


    private void closeTargetWindows() {
        for (Stage stage :
                stages) {
            stage.close();
        }
    }

    private void initializeDetection() {
        targets = GsonPersistence.load2();
        for (int i = 0; i < targets.size(); i++) {
            targets.get(i).active = false;
        }
        for (int i = 0; i < boxes.size(); i++) {
            if (boxes.get(i).isSelected()) {
                for (int j = 0; j < targets.size(); j++) {
                    if (targets.get(j).gunId == i)
                        targets.get(j).active = true;
                }
            }
        }
        GsonPersistence.persist2(targets);
    }

    private Pair<Stage, ControllerInterface> loadLayoutController(String resourceName) {
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxml/" + resourceName));
        AnchorPane pane = null;
        try {
            pane = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ControllerInterface controller = fxmlLoader.getController();
        Scene scene = new Scene(pane);
        Stage stage = new Stage();
        stage.setOnCloseRequest(windowEvent -> controller.shutdown());
        stage.setScene(scene);
        return new Pair<>(stage, controller);
    }

    public void shutdown() {
    }
}
