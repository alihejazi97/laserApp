package com.mom;
import com.mom.ui.App;
import com.mom.ui.FxMediaExample1;
import javafx.application.Application;

public class Main {

    private static Application app;

    static {
        nu.pattern.OpenCV.loadShared();
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args)
    {
        app = new App();
        Application.launch(app.getClass(),args);
    }
}
