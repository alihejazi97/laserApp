package com.mom.BoardConnection;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.mom.imgprocess.Target;
import com.mom.persistence.GsonPersistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Arduino {
    private List<Target> targets;

    private Map<Integer,List<Target> > activeTargets;

    private static Arduino arduino;

    private Arduino(){
        activeTargets = new HashMap<>();
    }

    public static Arduino getInstance(){
        if (arduino == null)
            arduino = new Arduino();
        return arduino;
    }

    public void startShooting(){
        targets = GsonPersistence.load2();
        activeTargets.clear();
        for (int i = 0; i < targets.size(); i++) {
            if (activeTargets.containsKey(targets.get(i).gunId)){
                activeTargets.get(targets.get(i).gunId).add(targets.get(i));
            }
            else {
                activeTargets.put(targets.get(i).gunId,new ArrayList<>());
            }
        }
        SerialPort[] commPorts = SerialPort.getCommPorts();
        SerialPort serialPort = null;
        for (SerialPort port:
             commPorts) {
            if (port.getPortDescription().equals(Target.camDescriptor))
                serialPort = port;
        }
        if (serialPort == null)
            return;
        serialPort.openPort();
        serialPort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_RECEIVED; }
            @Override
            public void serialEvent(SerialPortEvent event)
            {
                byte[] newData = event.getReceivedData();
                System.out.println("Received data of size: " + newData.length);
                for (int i = 0; i < newData.length; i++) {
                    System.out.println("newData " + i + " " + newData[i] + "received");
                    if(activeTargets.containsKey(newData[i])){
                        List<Target> targets = activeTargets.get(newData[i]);
                        for (int j = 0; j < targets.size(); j++) {
                            System.out.println("sett target " + targets.get(j).getName() + " active");
                            targets.get(j).setActive(true);
                        }
                    }
                }
            }
        });
    }
}
