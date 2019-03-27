package com.mom.BoardConnection;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.mom.imgprocess.Target;
import com.mom.persistence.GsonPersistence;
import com.mom.ui.controller.MainController;
import javafx.scene.control.Alert;
import javafx.stage.Modality;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Arduino {

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    private boolean active = false;
    private Map<Integer, List<Target>> activeTargets;

    private static Arduino arduino;

    private Arduino() {
        activeTargets = new HashMap<>();
    }

    public static Arduino getInstance() {
        if (arduino == null)
            arduino = new Arduino();
        return arduino;
    }

    SerialPortDataListener listener;

    public void startShooting() {
        SerialPort[] commPorts = SerialPort.getCommPorts();
        SerialPort serialPort = null;
        for (SerialPort port :
                commPorts) {
            if (port.getPortDescription().equals(Target.camDescriptor))
                serialPort = port;
        }
        if (serialPort == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("توجه");
            alert.setHeaderText("");
            alert.setContentText("ارتباط با سلاح بر قرار نمی باشد. ارتباط را تنظیم نموده و برنامه را مجددا راه اندازی نمایید.");
            alert.initModality(Modality.WINDOW_MODAL);
            alert.showAndWait();
            return;
        }
        serialPort.openPort();
        listener = new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
            }

            @Override
            public void serialEvent(SerialPortEvent event) {
                if (active == false)
                    return;
                activeTargets.clear();
                for (int i = 0; i < MainController.targets.size(); i++) {
                    if (activeTargets.containsKey(MainController.targets.get(i).gunId)) {
                        activeTargets.get(MainController.targets.get(i).gunId).add(MainController.targets.get(i));
                    } else {
                        ArrayList<Target> list = new ArrayList<>();
                        list.add(MainController.targets.get(i));
                        activeTargets.put(MainController.targets.get(i).gunId, list);
                    }
                }
                byte[] newData = event.getReceivedData();
                System.out.println("Received data of size: " + newData.length);
                if (newData.length > 0) {
                    int count = 0;
                    for (int i = 1; i <= 128; i *= 2) {
                        if (activeTargets.containsKey(count) && (newData[newData.length - 1] & i) != 0) {
                            List<Target> targets = activeTargets.get(count);
                            for (int j = 0; j < targets.size(); j++) {
                                System.out.println("gun" + count + " shoot in target " + targets.get(j).name);
                                targets.get(j).setActive(true);
                            }
                        }
                        count++;
                    }
                }
            }
        };
        serialPort.addDataListener(listener);
    }

}
