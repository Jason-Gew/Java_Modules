package gew.photo.camera;

import java.awt.image.BufferedImage;

public interface ICamera {
    void initialize(final String webCamName) throws Exception;

    boolean open();

    boolean close();

    boolean isOpen();

    BufferedImage take();

    String take(final String path);
}
