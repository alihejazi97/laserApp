package com.mom.ui.controller;

import com.mom.cam.CameraControl;
import com.mom.imgprocess.DetectRedDot;
import com.mom.imgprocess.Target;
import com.mom.persistence.GsonPersistence;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
    Button targetButton,saveButton;

    FXMLLoader fxmlLoader;

    @Override
    public void shutdown() {
        targets.clear();
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
        targets = GsonPersistence.load2();
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
        gunCombobox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object o, Object t1) {
                gunId = ((int) gunCombobox.getSelectionModel().getSelectedItem());
                for (Target target:
                     targets) {
                    if (target.gunId == gunId){
                        targetCombobox.getSelectionModel().select(gunId);
                        bulletNumTextField.setText(Integer.toString(target.bulletNum));
                        return;
                    }
                }
                bulletNumTextField.setText(Integer.toString(13));

            }
        });
        targetCombobox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Target>() {
            @Override
            public void changed(ObservableValue<? extends Target> observableValue, Target target, Target t1) {
                selectedTarget = targetCombobox.getSelectionModel().getSelectedItem();
                selectedTarget.gunId = gunId;
            }
        });
        bulletNumTextField.setOnAction(mouseDragEvent -> {
            if (StringUtils.isNumeric(bulletNumTextField.getText())){
                int bulletNum = Integer.parseInt(bulletNumTextField.getText());
                if (bulletNum > 0)
                    if (selectedTarget != null)
                        selectedTarget.bulletNum = bulletNum;
            }

        });
        saveButton.setOnMouseClicked(mouseEvent -> {
            if (selectedTarget != null){
                if (StringUtils.isNumeric(bulletNumTextField.getText())){
                    int bulletNum = Integer.parseInt(bulletNumTextField.getText());
                    if (bulletNum > 0)
                        if (selectedTarget != null)
                            selectedTarget.bulletNum = bulletNum;
                }
            }
            GsonPersistence.persist2(targets);
        });
    }
    int gunId;
    Target selectedTarget;
    public void afterShow(){
        guns = new ArrayList<>();
        for (int i = 0; i < Target.GUN_NUMBER; i++) {
            guns.add(i);
        }
        gunCombobox.setItems(FXCollections.observableList(guns));
        targetCombobox.setItems(FXCollections.observableList(targets));
    }

    List<Integer> guns;

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

}