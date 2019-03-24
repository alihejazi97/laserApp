package com.mom.cam;


import com.github.sarxos.webcam.Webcam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/*
    Every Class that uses
* */
public class CameraControl{

    private static CameraControl cameraControl;

    HashMap<String,WebcamInterface> webcams;
    List<String> cameraNames;

    private CameraControl(){
        webcams = new HashMap<>();
        cameraNames = new ArrayList<>();
        getOpenCvWebCams();
    }
    private void getOpenCvWebCams(){
        List<Webcam> webCamList = Webcam.getWebcams();
        for (int i = 0; i < webCamList.size(); i++) {
            webcams.put(webCamList.get(i).getName(),new OpenCVWebCam(i));
            cameraNames.add(webCamList.get(i).getName());
        }
    }

    public static CameraControl getInstance(){
        if (cameraControl == null)
            cameraControl = new CameraControl();
        return cameraControl;
    }

    public WebcamInterface getCamera(String camName){
        if (webcams.containsKey(camName))
            return webcams.get(camName);
        return null;
    }

    public void refreshCameras(){
        webcams.forEach((s, webcamInterface) -> webcamInterface.stopCamera());
        webcams.clear();
        getOpenCvWebCams();
    }

    public List<String> getCameraNames(){
        return cameraNames;
    }

}
