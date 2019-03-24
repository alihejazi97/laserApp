package com.mom.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mom.cam.CameraControl;
import com.mom.imgprocess.ColorBound;
import com.mom.imgprocess.Target;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class GsonPersistence {
    static CameraControl cameraControl = CameraControl.getInstance();

    public static void persist(List objects) {
        File file = new File("./setting.json");
        try{if (file.createNewFile()){
            System.out.println("lets create the file.");
        }
        else
        {
            System.out.println("file has been created.");
        }
            Gson gson = new Gson();
            FileWriter writer = new FileWriter(file);
            gson.toJson(objects,writer);
            writer.close();
        }
        catch (IOException e){}
    }
    public static List load(){
        ArrayList<ColorBound> bounds = new ArrayList<>();
        File file = new File("./setting.json");
        try{if (file.createNewFile()){
            System.out.println("fuck u we dont have this file");
        }
        else
        {
            System.out.println("file has been created.");
            Gson gson = new Gson();
            FileReader reader = new FileReader(file);
            ColorBound [] colorBounds =  gson.fromJson(reader, ColorBound[].class);
            if (colorBounds == null)
                return bounds;
            for (int i = 0; i < colorBounds.length; i++) {
                bounds.add(colorBounds[i]);
            }
            reader.close();
        }
        }
        catch (IOException e){}
        return bounds;
    }

    public static void persist2(List objects) {
        File file = new File("./settingTarget.json");
        try{if (file.createNewFile()){
            System.out.println("lets create the file.");
        }
        else
        {
            System.out.println("file has been created.");
        }
            GsonBuilder gsonBuilder  = new GsonBuilder();
            gsonBuilder.excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT);
            Gson gson = gsonBuilder.create();
            FileWriter writer = new FileWriter(file);
            gson.toJson(objects,writer);
            writer.close();
        }
        catch (IOException e){}
    }
    public static List load2(){
        ArrayList<Target> targets = new ArrayList<>();
        File file = new File("./settingTarget.json");
        try{if (file.createNewFile()){
            System.out.println("fuck u we dont have this file");
            final List<String> cameraNames = cameraControl.getCameraNames();
            Target.TARGET_NUMBER = 1;
            Target.GUN_NUMBER = 1;
            Target.camDescriptor = "";
            for (int i = 0; i < Target.TARGET_NUMBER; i++) {
                Target target = new Target();
                target.name = Integer.toString(i);
                if (!cameraNames.isEmpty())
                    target.webCamName = cameraNames.get(0);
                else target.webCamName = "";
                target.gunId = 0;
                targets.add(target);
            }
        }
        else
        {
            System.out.println("file has been created.");
            GsonBuilder gsonBuilder  = new GsonBuilder();
            gsonBuilder.excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT);
            Gson gson = gsonBuilder.create();
            FileReader reader = new FileReader(file);
            Target[] Targets =  gson.fromJson(reader, Target[].class);
            if (Targets == null || Targets.length == 0){
                final List<String> cameraNames = cameraControl.getCameraNames();
                Target.TARGET_NUMBER = 1;
                Target.GUN_NUMBER = 1;
                Target.camDescriptor = "";
                for (int i = 0; i < Target.TARGET_NUMBER; i++) {
                    Target target = new Target();
                    target.name = Integer.toString(i);
                    if (!cameraNames.isEmpty())
                        target.webCamName = cameraNames.get(0);
                    else target.webCamName = "";
                    target.gunId = 0;
                    targets.add(target);
                }
            }
            else{
                for (int i = 0; i < Targets.length; i++) {
                targets.add(Targets[i]);
                }
            }
            reader.close();
            return targets;
        }
        }
        catch (IOException e){}
        return targets;
    }
}
