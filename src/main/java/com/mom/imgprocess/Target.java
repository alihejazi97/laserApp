package com.mom.imgprocess;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import org.opencv.core.Point;

public class Target {

    public static class EddittingCell extends TableCell<Target,String>{
        private TextField textField;

        public EddittingCell() {
        }

        @Override
        public void startEdit() {
            if (!isEmpty()) {
                super.startEdit();
                createTextField();
                setText(null);
                setGraphic(textField);
                textField.selectAll();
            }
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();

            setText((String) getItem());
            setGraphic(null);
        }

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getString());
                    }
                    setText(null);
                    setGraphic(textField);
                } else {
                    setText(getString());
                    setGraphic(null);
                }
            }
        }

        private void createTextField() {
            textField = new TextField(getString());
            textField.setMinWidth(this.getWidth() - this.getGraphicTextGap()* 2);
            textField.focusedProperty().addListener(new ChangeListener<Boolean>(){
                @Override
                public void changed(ObservableValue<? extends Boolean> arg0,
                                    Boolean arg1, Boolean arg2) {
                    if (!arg2) {
                        commitEdit(textField.getText());
                    }
                }
            });
        }

        private String getString() {
            return getItem() == null ? "" : getItem().toString();
        }
    }

    public int id;

    public static int TARGET_NUMBER;

    public static int GUN_NUMBER;

    public String name;

    public String webCamName;

    public static String camDescriptor;

    public int gunId,bulletNum;

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean valid;

    public Target clone(){
        Target target = new Target();
        target.valid = this.valid;
        target.point0 = this.point0.clone();
        target.point1 = this.point1.clone();
        target.point2 = this.point2.clone();
        target.point3 = this.point3.clone();
        target.id = this.id;
        return target;
    }

    @Override
    public String toString() {
        return name;
    }

    public Point point0;
    public Point point1;
    public Point point2;
    public Point point3;

    public Target() {
        point0 = new Point();
        point1 = new Point();
        point2 = new Point();
        point3 = new Point();
        valid = false;
        gunId = 0;
        webCamName = "empty";
        camDescriptor = "empty";
    }

    public String getWebCamName() {
        return webCamName;
    }

    public void setWebCamName(String webCamName) {
        this.webCamName = webCamName;
    }

    public int getGunId() {
        return gunId;
    }

    public void setGunId(int gunId) {
        this.gunId = gunId;
    }

    public int getBulletNum() {
        return bulletNum;
    }

    public void setBulletNum(int bulletNum) {
        this.bulletNum = bulletNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Point getPoint0() {
        return point0;
    }

    public void setPoint0(Point point0) {
        this.point0 = point0;
    }

    public Point getPoint1() {
        return point1;
    }

    public void setPoint1(Point point1) {
        this.point1 = point1;
    }

    public Point getPoint2() {
        return point2;
    }

    public void setPoint2(Point point2) {
        this.point2 = point2;
    }

    public Point getPoint3() {
        return point3;
    }

    public void setPoint3(Point point3) {
        this.point3 = point3;
    }
}
