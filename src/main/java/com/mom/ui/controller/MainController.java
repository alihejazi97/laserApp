
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
import javafx.stage.WindowEvent;
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
    private MenuItem targetMenuItem,advanceSettingMenuItem,preferencesMenuItem;

    private FXMLLoader fxmlLoader;

    private List<DetectRedDot> detectRedDots;

    private List<Stage> stages;

    public List<Target> targets;

    public Arduino arduino;

    public CameraControl cameraControl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        arduino = Arduino.getInstance();
        targets = new ArrayList<>();
        detectRedDots = new ArrayList<>();
        stages = new ArrayList<>();
        targets = GsonPersistence.load2();
        System.out.println(Target.TARGET_NUMBER);
        cameraControl = CameraControl.getInstance();
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
                stage.initModality(Modality.WINDOW_MODAL);
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
                    if (!(targets.get(i).valid)){
                        //show error
                        continue;
                    }
                    if (cameraControl.getCamera(targets.get(i).webCamName) == null){
                        //show error
                        continue;
                    }
                    Pair<Stage, ControllerInterface> pair = loadLayoutController("target.fxml");
                    controller = ((TargetController) pair.getValue());
                    stage = pair.getKey();
                    DetectRedDot detectRedDot = new DetectRedDot();
                    detectRedDot.target = targets.get(i);
                    controller.setDetectRedDot(detectRedDot);                                                      
                    stages.add(stage);
                    stage.show();
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
                stage.initModality(Modality.WINDOW_MODAL);
                controller.setTargets(targets);
                pair.getKey().show();
                controller.afterShow();
            }
        });
    }


    private void closeTargetWindows(){
        for (Stage stage:
             stages) {
            stage.close();
        }
    }

    private void initializeDetection() {
        targets = GsonPersistence.load2();
        for (int i = 0; i < Target.TARGET_NUMBER; i++) {
            DetectRedDot detectRedDot = new DetectRedDot();
            detectRedDot.target = targets.get(i);
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
