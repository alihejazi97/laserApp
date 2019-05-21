package com.mom.cam;

import com.mom.cam.util.DirectShowCamera;
import com.mom.cam.util.FrameListener;
import com.mom.cam.util.Utils;
import de.humatic.dsj.DSCapture;
import de.humatic.dsj.DSFilterInfo;
import de.humatic.dsj.DSFiltergraph;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.Mat;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.beans.PropertyChangeEvent;

public class DirectShowWebCam implements WebcamInterface ,java.beans.PropertyChangeListener  {

    SimpleObjectProperty<Mat> shareMat;

    ImageView imageView;

    DSFilterInfo filterInfo;

    DSCapture capture;

    boolean show;

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Mat value = shareMat.getValue();
        if (value != null && !value.empty())
            value.release();
        System.out.println("good stuff happening here");
        BufferedImage image = capture.getImage();
        shareMat.setValue(Utils.BufferedImage2mat(image));
        System.out.println("PARTY");
        show(capture.getImage());
    }

    private DirectShowWebCam(){}

    public DirectShowWebCam(DSFilterInfo filterInfo) {
        this.filterInfo = filterInfo;
        shareMat = new SimpleObjectProperty<>();
    }

    @Override
    public void addListener(ChangeListener listener) {
        shareMat.addListener(listener);
    }

    @Override
    public void removeListener(ChangeListener listener) {
        shareMat.removeListener(listener);
    }

    private void show(BufferedImage frame) {
        if (show) {
            Image imageToShow = Utils.bufferedImage2Image(frame);
            updateImageView(imageView, imageToShow);
        }
    }

    private void updateImageView(ImageView view, Image image) {
        Utils.onFXThread(view.imageProperty(), image);
    }

    @Override
    public void startCamera() {
        capture = new DSCapture(DSFiltergraph.FRAME_CALLBACK, filterInfo, true, null, this);
        System.out.println("PA");
        capture.play();
        System.out.println("TY");
    }

    @Override
    public void stopCamera() {
        capture.stop();
    }

    @Override
    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    public void setShow(boolean show) {
        this.show = show;
    }
}
