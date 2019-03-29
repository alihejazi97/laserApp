package com.mom.persistence;

import com.google.gson.*;
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
                System.out.println("creating file");
            }
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            JsonElement element = gson.toJsonTree(objects);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("GUN_NUMBER",Target.GUN_NUMBER);
            jsonObject.addProperty("camDescriptor",Target.camDescriptor);
            jsonObject.addProperty("TARGET_NUMBER",Target.TARGET_NUMBER);
            jsonObject.add("targets",element);
            FileWriter writer = new FileWriter(file);
            gson.toJson(jsonObject,writer);
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
            Gson gson = gsonBuilder.create();
            FileReader reader = new FileReader(file);
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = ((JsonObject) jsonParser.parse(reader));
            JsonArray jsonArray = jsonObject.getAsJsonArray("targets");
            Target[] Targets = gson.fromJson(jsonArray,Target[].class);
            JsonElement element = jsonObject.get("GUN_NUMBER");
            Target.GUN_NUMBER = gson.fromJson(element,Integer.class);
            element = jsonObject.get("camDescriptor");
            Target.camDescriptor = gson.fromJson(element,String.class);
            element = jsonObject.get("TARGET_NUMBER");
            Target.TARGET_NUMBER = gson.fromJson(element,Integer.class);
            for (int i = 0; i < Targets.length; i++) {
                targets.add(Targets[i]);
            }
            reader.close();
            return targets;
        } catch (IOException e) {
        }
        return targets;
    }
}
