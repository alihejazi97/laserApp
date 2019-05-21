package com.mom.imgprocess;

import com.mom.cam.util.Utils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.Mat;

public class ShowImageView {
    public ShowImageView(){
    }

    public ShowImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public ShowImageView(ImageView imageView, boolean show) {
        this.show = show;
        this.imageView = imageView;
    }

    public boolean isShow() {
        return show;
    }

    public ImageView getImageView() {
        return imageView;
    }

    private boolean show = false;
    private ImageView imageView;
    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public void show(Mat frame){
        if (show){
            Image imageToShow = Utils.mat2Image(frame);
            updateImageView(imageView, imageToShow);
        }
    }
    private void updateImageView(ImageView view, Image image)
    {
        Utils.onFXThread(view.imageProperty(), image);
    }
}
