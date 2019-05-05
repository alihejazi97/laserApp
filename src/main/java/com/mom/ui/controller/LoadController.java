package com.mom.ui.controller;

import com.mom.BoardConnection.Arduino;
import com.mom.cam.CameraControl;
import com.mom.persistence.GsonPersistence;
import com.mom.ui.App;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class LoadController implements Initializable,ControllerInterface {

    CameraControl cameraControl;

    Arduino arduino;

    @FXML
    private Text loadingText;

    @Override
    public void shutdown() {

    }

    public void setApp(App app) {
        this.app = app;
    }

    App app;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Task task = new Task<Void>() {
            @Override public Void call() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(() -> loadingText.setText("وصل شدن به دوربین ها"));
                cameraControl = cameraControl.getInstance();
                MainController.targets = GsonPersistence.load();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(() -> loadingText.setText("وصل شدن به اسلحه ها"));
                arduino = Arduino.getInstance();
                arduino.startShooting();
                Platform.runLater(() -> app.startMainApplication());
                return null;
            }
        };
        new Thread(task).start();
    }
}
