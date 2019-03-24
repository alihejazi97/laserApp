package com.mom.BoardConnection;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.mom.imgprocess.DetectRedDot;
import com.mom.imgprocess.Target;

import java.util.EventListener;
import java.util.List;

public class Arduino {
    List<DetectRedDot> dots;

    public List<DetectRedDot> getDots() {
        return dots;
    }

    public void setDots(List<DetectRedDot> dots) {
        this.dots = dots;
    }

    public static void checkCom(){
        SerialPort comPort = SerialPort.getCommPort(Target.camDescriptor);
        comPort.openPort();
        comPort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_WRITTEN;
            }

            @Override
            public void serialEvent(SerialPortEvent serialPortEvent) {

            }
        });
        try {
            while (true)
            {
                while (comPort.bytesAvailable() == 0)
                    Thread.sleep(20);

                byte[] readBuffer = new byte[comPort.bytesAvailable()];
                int numRead = comPort.readBytes(readBuffer, readBuffer.length);
                System.out.println("Read " + numRead + " bytes.");
                for (byte b :
                        readBuffer) {
                    String s1 = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
                    System.out.println(s1);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        comPort.closePort();
    }

    public static void main(String[] args) {
        checkCom();
    }

}
