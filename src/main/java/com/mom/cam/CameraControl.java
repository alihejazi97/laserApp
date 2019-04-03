package com.mom.cam;


import com.github.sarxos.webcam.Webcam;
import com.google.gson.*;
import com.mom.imgprocess.Target;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/*
    Every Class that uses
* */
public class CameraControl {

    private static CameraControl cameraControl;

    HashMap<String, WebcamInterface> webcams;
    List<String> cameraNames;

    private CameraControl() {
        webcams = new HashMap<>();
        cameraNames = new ArrayList<>();
        getOpenCvWebCams();
    }

    private void getOpenCvWebCams() {
        List<Webcam> webCamList = Webcam.getWebcams();
        int size = webCamList.size();
        for (int i = 0; i < webCamList.size(); i++) {
            WebcamInterface webCam = new OpenCVWebCam(i);
            webCam.startCamera();
            webcams.put(webCamList.get(i).getName(), webCam);
            cameraNames.add(webCamList.get(i).getName());
        }
        loadIPcamera();
        for (int i = size; i < webCamList.size(); i++) {
            WebcamInterface webCam = new OpenCVWebCam(cameraNames.get(i));
            webCam.startCamera();
            webcams.put(cameraNames.get(i), webCam);
        }
    }

    public void loadIPcamera() {
        File file = new File("./ip.txt");
        try {
            if (file.createNewFile()) {
                System.out.println("file has been created.");
            }
            System.out.println("file has been loaded.");
            FileReader reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String st;
            while ((st = bufferedReader.readLine()) != null)
                cameraNames.add(st);
            reader.close();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public static CameraControl getInstance() {
        if (cameraControl == null)
            cameraControl = new CameraControl();
        return cameraControl;
    }

    public WebcamInterface getCamera(String camName) {
        if (webcams.containsKey(camName))
            return webcams.get(camName);
        return null;
    }

    public void refreshCameras() {
        webcams.forEach((s, webcamInterface) -> webcamInterface.stopCamera());
        webcams.clear();
        getOpenCvWebCams();
    }

    public List<String> getCameraNames() {
        return cameraNames;
    }

    public void stopCameras() {
        webcams.keySet().forEach(s -> webcams.get(s).stopCamera());
    }
}
