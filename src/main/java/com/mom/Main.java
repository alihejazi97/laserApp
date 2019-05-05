package com.mom;
import com.mom.ui.App;
import javafx.application.Application;

public class Main {

    private static Application app;

    static {
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args)
    {
        app = new App();
        Application.launch(app.getClass(),args);
    }
}
