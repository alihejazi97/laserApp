
package com.mom.ui.controller;

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
    private MenuItem targetMenuItem,advanceSettingMenuItem,preferencesMenuItem;

    private FXMLLoader fxmlLoader;

    private List<DetectRedDot> detectRedDots;

    private List<Stage> stages;

    public List<Target> targets;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        targets = new ArrayList<>();
        detectRedDots = new ArrayList<>();
        stages = new ArrayList<>();
        targets = GsonPersistence.load2();
        System.out.println(Target.TARGET_NUMBER);
        DetectRedDot.colorBounds = GsonPersistence.load();

        preferencesMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            preferenceControl controller;
            Stage stage;
            @Override
            public void handle(ActionEvent actionEvent) {
                Pair<Stage, ControllerInterface> pair = loadLayoutController("preference.fxml");
                controller = ((preferenceControl) pair.getValue());
                stage = pair.getKey();
                stages.add(stage);
                stage.setAlwaysOnTop(true);
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



//        inRangeMenuItem.setOnAction(new EventHandler<>() {
//            private SettingController controller;
//            Stage stage;
//
//            @Override
//            public void handle(ActionEvent actionEvent) {
//                Pair<Stage, ControllerInterface> pair = loadLayoutController("ToBeDeleted.fxml");
//                controller = ((SettingController) pair.getValue());
//                stage = pair.getKey();
//                controller.setWebCam(webCam);
//                controller.setImgVoriginal(imgVOriginal);
//                controller.setDetectRedDot(new DetectRedDot());
//                stage.show();
//            }
//        });

//
//        targetMenuItem.setOnAction(new EventHandler<>() {
//            private SettingPersController controller;
//            Stage stage;
//
//            @Override
//            public void handle(ActionEvent actionEvent) {
//                Pair<Stage, ControllerInterface> pair = loadLayoutController("settingPers.fxml");
//                controller = ((SettingPersController) pair.getValue());
//                stage = pair.getKey();
//                controller.setTargets(targets);
//                controller.setDetectRedDot(new DetectRedDot());
//                pair.getKey().show();
//            }
//        });

        targetMenuItem.setOnAction(new EventHandler<>() {
            private TargetConfController controller;
            Stage stage;

            @Override
            public void handle(ActionEvent actionEvent) {
                Pair<Stage, ControllerInterface> pair = loadLayoutController("targetConf.fxml");
                controller = ((TargetConfController) pair.getValue());
                stage = pair.getKey();
                stage.setAlwaysOnTop(true);
                pair.getKey().show();
                controller.setTargets(targets);
            }
        });
    }

    private void initializeDetection() {
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
        Scene scene = new Scene(pane, pane.getWidth(), pane.getHeight());
        Stage stage = new Stage();
        stage.setOnCloseRequest(windowEvent -> controller.shutdown());
        stage.setScene(scene);
        return new Pair<>(stage, controller);
    }

    public void shutdown() {
    }
}
