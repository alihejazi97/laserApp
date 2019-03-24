package com.mom.ui.controller;

import com.mom.cam.CameraControl;
import com.mom.imgprocess.DetectRedDot;
import com.mom.imgprocess.Target;
import com.mom.persistence.GsonPersistence;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Pair;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TargetConfController implements Initializable,ControllerInterface {

    @FXML
    AnchorPane root;

    @FXML
    HBox hbox;

    @FXML
    private ComboBox<Target> targetCombobox;

    @FXML
    private ComboBox gunCombobox;

    @FXML
    private ComboBox cameraComboBox;

    @FXML
    private TextField bulletNumTextField;

    @FXML
    TableView<Target> targetTableView;

    @FXML
    TableColumn<Target,Integer> gunColumn,bulletNumColumn;

    @FXML
    TableColumn<Target,Target> targetColumn;

    @FXML
    TableColumn<Target,String> cameraColumn;

    @FXML
    Button targetButton;

    FXMLLoader fxmlLoader;

    @Override
    public void shutdown() {
        targets.clear();
        targets = GsonPersistence.load2();
    }

    public void setTargets(List<Target> targets) {
        this.targets = targets;

    }

    void comboBoxConfiguration(){
        targetCombobox.setItems(FXCollections.observableList(targets));
        cameraComboBox.setItems(FXCollections.observableList(cameraControl.getCameraNames()));

    }
    void tableViewConfiguration(){
        gunColumn.setCellValueFactory(new PropertyValueFactory<>("gunId"));
        bulletNumColumn.setCellValueFactory(new PropertyValueFactory<>("bulletNum"));
        cameraColumn.setCellValueFactory(new PropertyValueFactory<>("webCamName"));
        ObservableList<Target> targetObservableList = FXCollections.observableList(targets);
        targetTableView.setItems(targetObservableList);
    }

    List<Target> targets;

    CameraControl cameraControl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cameraControl = CameraControl.getInstance();
        targetButton.setOnMouseClicked(new EventHandler<>() {
            private SettingPersController controller;

            @Override
            public void handle(MouseEvent mouseEvent) {
                Pair<Stage, ControllerInterface> pair = loadLayoutController("settingPers.fxml");
                controller = ((SettingPersController) pair.getValue());
                controller.setTargets(targets);
                controller.setDetectRedDot(new DetectRedDot());
                pair.getKey().setAlwaysOnTop(true);
                pair.getKey().show();
                controller.afterShow();
            }
        });
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

}