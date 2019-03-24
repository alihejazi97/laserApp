package com.mom.ui.controller;

import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingController implements Initializable , ControllerInterface {
//
//    private ImageView imgVoriginal;
//    private ColorBound colorBound;
//    private ObservableList<ColorBound> bounds;
//
//    public void setDetectRedDot(DetectRedDot detectRedDot) {
//        this.detectRedDot = detectRedDot;
//        webCam.addListener(detectRedDot);
//        detectRedDot.showTest.setImageView(colorFilteredImgV);
//        detectRedDot.showTest.setShow(true);
//        bounds = FXCollections.observableList(detectRedDot.colorBounds);
//        filtersChoiceBox.setItems(bounds);
//        filtersChoiceBox.getSelectionModel().selectedIndexProperty().addListener((observableValue, oldVal, newVal) -> {
//            colorBound = bounds.get(newVal.intValue());
//            detectRedDot.setColorBoundTest(colorBound);
//            detectRedDot.setTest(true);
//            minHueSlider.setValue(colorBound.getLower().val[0]);
//            minSatSlider.setValue(colorBound.getLower().val[1]);
//            minValSlider.setValue(colorBound.getLower().val[2]);
//            maxHueSlider.setValue(colorBound.getUpper().val[0]);
//            maxSatSlider.setValue(colorBound.getUpper().val[1]);
//            maxValSlider.setValue(colorBound.getUpper().val[2]);
//        });
//}
//
//    private DetectRedDot detectRedDot;
//
//    public void setImgVoriginal(ImageView imgVoriginal) {
//        this.imgVoriginal = imgVoriginal;
//    }
//
//    @FXML
//    private Slider minHueSlider;
//
//    @FXML
//    private Button saveButton;
//
//    @FXML
//    private Slider minSatSlider;
//
//    @FXML
//    private Slider minValSlider;
//
//    @FXML
//    private Slider maxHueSlider;
//
//    @FXML
//    private Slider maxSatSlider;
//
//    @FXML
//    private Slider maxValSlider;
//
//    @FXML
//    private ImageView rawImgV;
//
//    @FXML
//    private ImageView colorFilteredImgV;
//
//    @FXML
//    private Button addButton;
//    @FXML
//    private Button deleteButton;
//    @FXML
//    private TextField nameTextField;
//    @FXML
//    private ChoiceBox filtersChoiceBox;
//
//    public void setWebCam(WebcamInterface webCam) {
//        this.webCam = webCam;
//        initializeCamera();
//    }
//
//    public void initializeCamera(){
//        webCam.setImageView(rawImgV);
//        webCam.setShow(true);
//    }
//
//    private WebcamInterface webCam;
//
//    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        DetectRedDot.colorBounds = GsonPersistence.load();
//        saveButton.setOnMouseClicked(mouseEvent -> GsonPersistence.persist(detectRedDot.colorBounds));
//        addButton.setOnMouseClicked(mouseEvent -> {
//            ColorBound bound = new ColorBound(nameTextField.getText());
//            bounds.add(bound);
//        });
//        deleteButton.setOnMouseClicked(mouseEvent -> {
//            if (bounds.size() < 1) {
//                ColorBound colorBoundTemp = colorBound;
//                filtersChoiceBox.getSelectionModel().selectPrevious();
//                Platform.runLater(() -> bounds.remove(colorBoundTemp));
//            } else {

//            }
//        });
//        minHueSlider.valueProperty().addListener((observableValue, number, t1) -> {
//            if (checkColorBound())
//                colorBound.getLower().val[0] = minHueSlider.getValue();
//            });
//        maxHueSlider.valueProperty().addListener((observableValue, oldNumber, newNumber) -> {
//            if (checkColorBound())
//                colorBound.getUpper().val[0] = maxHueSlider.getValue();
//            });
//        minSatSlider.valueProperty().addListener((observableValue, oldNumber, newNumber) -> {
//            if (checkColorBound())
//                colorBound.getLower().val[1] = minSatSlider.getValue();
//        });
//        maxSatSlider.valueProperty().addListener((observableValue, oldNumber, newNumber) -> {
//            if (checkColorBound())
//                colorBound.getUpper().val[1] = maxSatSlider.getValue();
//        });
//        minValSlider.valueProperty().addListener((observableValue, oldNumber, newNumber) -> {
//            if (checkColorBound())
//                colorBound.getLower().val[2] = minValSlider.getValue();
//            });
//        maxValSlider.valueProperty().addListener((observableValue, oldNumber, newNumber) -> {
//            if (checkColorBound())
//                colorBound.getUpper().val[2] = maxValSlider.getValue();
//        });
    }
//    private boolean checkColorBound(){
//        if (colorBound == null){
//            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//            alert.setTitle("filter selection");
//            alert.setHeaderText("Results:");
//            alert.setContentText("pls select a color filter!");
//            alert.showAndWait();
//            return false;
//        }
//        return true;
//    }
    public void shutdown(){
//        detectRedDot.setTest(false);
//        detectRedDot.showTest.setShow(false);
//        webCam.setImageView(imgVoriginal);
//        detectRedDot.colorBounds = bounds.stream().collect(Collectors.toList());
    }
}
