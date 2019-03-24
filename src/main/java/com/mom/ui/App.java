package com.mom.ui;
import com.mom.ui.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class App extends Application {

    private FXMLLoader fxmlLoader;
    private AnchorPane pane;
    private Scene scene;
    private MainController mainController;

    public void start(Stage stage) throws Exception {
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxml/main.fxml"));
        pane = fxmlLoader.load();
        mainController = fxmlLoader.getController();
        scene = new Scene(pane,pane.getWidth(),pane.getHeight());
        stage.setOnCloseRequest(windowEvent -> mainController.shutdown());
        stage.setScene(scene);
        stage.show();
    }
}
