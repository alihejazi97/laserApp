
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
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
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
    private Button shooting0Button, shooting1Button, shooting2Button;

    @FXML
    private MenuItem targetMenuItem, advanceSettingMenuItem, preferencesMenuItem, gunMenuItem;

    private FXMLLoader fxmlLoader;

    private List<DetectRedDot> detectRedDots;

    private List<Stage> stages;

    public static List<Target> targets;

    public Arduino arduino;

    public CameraControl cameraControl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        targets = GsonPersistence.load2();
        arduino = Arduino.getInstance();
        detectRedDots = new ArrayList<>();
        stages = new ArrayList<>();
        arduino.startShooting();
        System.out.println(Target.TARGET_NUMBER);
        cameraControl = CameraControl.getInstance();

        gunMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            private SettingPersController controller;

            @Override
            public void handle(ActionEvent actionEvent) {
                closeTargetWindows();
                Pair<Stage, ControllerInterface> pair = loadLayoutController("settingPers.fxml");
                controller = ((SettingPersController) pair.getValue());
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
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.show();
                controller.setTargets(targets);
            }
        });

        shooting0Button.setOnMouseClicked(new EventHandler<MouseEvent>() {
            TargetController controller;

            @Override
            public void handle(MouseEvent mouseEvent) {
                initializeDetection();
                Stage stage;
                for (int i = 0; i < Target.TARGET_NUMBER; i++) {
                    if (!(targets.get(i).valid)) {
                        //show error
                        continue;
                    }
                    if (cameraControl.getCamera(targets.get(i).webCamName) == null) {
                        //show error
                        continue;
                    }

                    Pair<Stage, ControllerInterface> pair = loadLayoutController("target.fxml");
                    controller = ((TargetController) pair.getValue());
                    stage = pair.getKey();
                    controller.setDetectRedDot(detectRedDots.get(i));
                    stages.add(stage);
                    stage.show();
                    arduino.setActive(true);
                }
            }
        });

        targetMenuItem.setOnAction(new EventHandler<>() {
            private TargetConfController controller;
            Stage stage;

            @Override
            public void handle(ActionEvent actionEvent) {
                closeTargetWindows();
                Pair<Stage, ControllerInterface> pair = loadLayoutController("targetConf.fxml");
                controller = ((TargetConfController) pair.getValue());
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
        for (int i = 0; i < Target.TARGET_NUMBER; i++) {
            DetectRedDot detectRedDot = new DetectRedDot();
            detectRedDot.setIndex(i);
            detectRedDots.add(detectRedDot);
        }
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
