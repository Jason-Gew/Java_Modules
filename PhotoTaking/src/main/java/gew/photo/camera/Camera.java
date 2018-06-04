package gew.photo.camera;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import com.github.sarxos.webcam.util.ImageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

public class Camera implements ICamera
{
    private Webcam webcam;
    private Dimension defaultDimension;
    private String defaultPhotoFormat;

    private static Camera instance;
    private static final Logger logger = LoggerFactory.getLogger(Camera.class);

    private Camera() { }

    public static Camera getInstance()
    {
        if (instance == null) {
            synchronized (Camera.class) {
                if (instance == null) {
                    instance = new Camera();
                }
            }
        }
        return instance;
    }

    public static List<Webcam> detectWebCams() {
        List<Webcam> webcams = null;
        try {
            webcams = Webcam.getWebcams();
        } catch (Exception err) {
            logger.error("-> Get System Web Camera(s) Failed: " + err.getMessage());
        }
        return webcams;
    }

    @Override
    public void initialize(final String webCamName) throws Exception {
        if(webcam == null) {
            webcam = Webcam.getWebcamByName(webCamName);
            System.out.println("-> Web-Camera [ " + webcam.getName() + " ] initializing...");
            defaultDimension = new Dimension(640, 480);
            defaultPhotoFormat = ImageUtils.FORMAT_PNG;
        } else if (webcam.isOpen()) {
            webcam.close();
            System.out.println("-> Turning off current web-camera... ");
            webcam = null;
            initialize(webCamName);
        }
    }

    @Override
    public boolean open() {
        return webcam != null && webcam.open();
    }

    @Override
    public boolean close() {
        return webcam != null && webcam.close();
    }

    @Override
    public boolean isOpen() {
        return webcam.isOpen();
    }

    public void setDefaultImageFormat(ImageFormat format) {
        defaultPhotoFormat = format.value();
    }

    public String getDefaultPhotoFormat() {
        return defaultPhotoFormat;
    }

    public void setCustomerResolutions() {
        Dimension[] customerResolutions = new Dimension[] {
                new Dimension(960, 720),    // 960 * 720
                WebcamResolution.XGA.getSize(),         // 1024 * 768
                WebcamResolution.HD.getSize(),          // 1280 * 720
                WebcamResolution.HDP.getSize(),         // 1600 * 900
                WebcamResolution.FHD.getSize()          // 1920 * 1080
        };
        setCustomerResolutions(customerResolutions);
    }

    public void setCustomerResolutions(Dimension[] dimensions) {
        if(webcam != null && dimensions.length > 0) {
            webcam.setCustomViewSizes(dimensions);
        }
    }

    public void setImageSize(Dimension dimension) {
        webcam.setViewSize(dimension);
        this.defaultDimension = dimension;
    }

    @Override
    public synchronized BufferedImage take() {
        if(!webcam.isOpen()) {
            boolean status = open();
            logger.debug("-> Turn on web camera: " + status);
        }
        BufferedImage image = webcam.getImage();
        logger.info("-> Image Has Been Captured, Resolution: " + image.getWidth() + "x" + image.getHeight());
        return image;
    }

    @Override
    public String take(final String path) {
        String source = null;
        File file = new File(path + "." + defaultPhotoFormat.toLowerCase());
        try{
            BufferedImage image = take();
            ImageIO.write(image, defaultPhotoFormat, file);
            source = file.getAbsolutePath();
        } catch (IOException err) {
            logger.error("-> Save Image to {} Failed: {}", file.getName(), err.getMessage());
        }
        return source;
    }

    public synchronized ByteBuffer takeBytes() {
        if(!webcam.isOpen()) {
            boolean status = open();
            logger.debug("-> Turn on web camera: " + status);
        }
        return webcam.getImageBytes();
    }


}
