package com.mom.ui;
import com.mom.cam.CameraControl;
import com.mom.ui.controller.LoadController;
import com.mom.ui.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.List;

public class App extends Application {

    private FXMLLoader fxmlLoader;
    private Scene scene;
    private MainController mainController;
    Stage stage;
    public static boolean config;
    public void start(Stage stage) throws Exception {
        Parameters parameters = getParameters();
        List<String> list = parameters.getUnnamed();
        for (String s:
             list) {
            if (s.equals("config"))
                config = true;
        }
        this.stage = stage;
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxml/load.fxml"));
        StackPane pane = fxmlLoader.load();
        LoadController loadController= fxmlLoader.getController();
        scene = new Scene(pane);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show();
        loadController.setApp(this);
    }

    public void startMainApplication(){
        stage.close();
        stage = new Stage();
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxml/main.fxml"));
        AnchorPane pane = null;
        try {
            pane = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mainController = fxmlLoader.getController();
        scene = new Scene(pane);
        stage.setOnCloseRequest(windowEvent -> mainController.shutdown());
        stage.setScene(scene);
        stage.initStyle(StageStyle.DECORATED);
        stage.getIcons().add(new Image(getClass().getResource("/img/program icon.png").toExternalForm()));
        stage.show();
        mainController.afterShow();
    }
    @Override
    public void stop() throws Exception {
        CameraControl cameraControl = CameraControl.getInstance();
        cameraControl.stopCameras();
        super.stop();
    }
}
