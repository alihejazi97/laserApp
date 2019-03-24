package com.mom.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mom.cam.CameraControl;
import com.mom.imgprocess.Target;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class GsonPersistence {

    private static final int GUN_NUMBER_DEFAULT = 1;
    private static final int TARGET_NUMBER_DEFAULT = 1;

    static CameraControl cameraControl = CameraControl.getInstance();

    public static void persist2(List objects) {
        File file = new File("./settingTarget.json");
        try {
            if (file.createNewFile()) {
                System.out.println("creating the file.");
            }
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT);
            Gson gson = gsonBuilder.create();
            FileWriter writer = new FileWriter(file);
            gson.toJson(objects, writer);
            writer.close();
        } catch (IOException e) {
        }
    }

    public static void makeDefaultTargets(List<Target> targets) {
        final List<String> cameraNames = cameraControl.getCameraNames();
        Target.TARGET_NUMBER = TARGET_NUMBER_DEFAULT;
        Target.GUN_NUMBER = GUN_NUMBER_DEFAULT;
        Target.camDescriptor = new String();
        for (int i = 0; i < Target.TARGET_NUMBER; i++) {
            Target target = new Target();
            target.name = Integer.toString(i);
            if (!cameraNames.isEmpty())
                target.webCamName = cameraNames.get(0);
            else
                target.webCamName = new String();
            target.gunId = 0;
            targets.add(target);
        }
    }

    public static List load2() {
        ArrayList<Target> targets = new ArrayList<>();
        File file = new File("./settingTarget.json");
        try {
            if (file.createNewFile()) {
                System.out.println("file has been created.");
                makeDefaultTargets(targets);
                persist2(targets);
                targets.clear();
            }
            System.out.println("file has been loaded.");
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT);
            Gson gson = gsonBuilder.create();
            FileReader reader = new FileReader(file);
            Target[] Targets = gson.fromJson(reader, Target[].class);
            if (Targets == null || Targets.length == 0) {
                makeDefaultTargets(targets);
            } else {
                for (int i = 0; i < Targets.length; i++) {
                    targets.add(Targets[i]);
                }
            }
            reader.close();
            return targets;
        } catch (IOException e) {
        }
        return targets;
    }
}
