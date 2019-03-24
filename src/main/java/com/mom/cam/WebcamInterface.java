package com.mom.cam;

import javafx.beans.value.ChangeListener;
import javafx.scene.image.ImageView;

public interface WebcamInterface {
    void addListener(ChangeListener listener);

    void removeListener(ChangeListener listener);

    void startCamera();

    void stopCamera();

    void setImageView(ImageView imageView);

    void setShow(boolean show);
}
