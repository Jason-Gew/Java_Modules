package gew.photo.config;

import gew.photo.camera.ImageFormat;
import gew.photo.camera.ImageNaming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Dimension;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class AppConfig {

    private static String cameraName;
    private static Dimension resolution;
    private static ImageFormat imageFormat;
    private static String storagePath;
    private static Boolean enableScheduler;
    private static Integer capturePeriod;
    private static String serverUrl;
    private static String location;
    private static String imageNaming;

    private static final String DEFAULT_PICTURE_PATH = "pictures/";
    private static final String CONFIG_PATH = "config/config.properties";

    private static final Logger LOGGER = LoggerFactory.getLogger(AppConfig.class);

    public AppConfig() {
    }

    public static String getCameraName() {
        return cameraName;
    }

    public static void setCameraName(String cameraName) {
        AppConfig.cameraName = cameraName;
    }

    public static Dimension getResolution() {
        return resolution;
    }

    public static void setResolution(Dimension resolution) {
        AppConfig.resolution = resolution;
    }

    public static ImageFormat getImageFormat() {
        return imageFormat;
    }

    public static void setImageFormat(ImageFormat imageFormat) {
        AppConfig.imageFormat = imageFormat;
    }

    public static String getStoragePath() {
        return storagePath;
    }

    public static void setStoragePath(String storagePath) {
        AppConfig.storagePath = storagePath;
    }

    public static Boolean getEnableScheduler() {
        return enableScheduler;
    }

    public static void setEnableScheduler(Boolean enableScheduler) {
        AppConfig.enableScheduler = enableScheduler;
    }

    public static Integer getCapturePeriod() {
        return capturePeriod;
    }

    public static void setCapturePeriod(Integer capturePeriod) {
        AppConfig.capturePeriod = capturePeriod;
    }

    public static String getServerUrl() {
        return serverUrl;
    }

    public static void setServerUrl(String serverUrl) {
        AppConfig.serverUrl = serverUrl;
    }

    public static String getLocation() {
        return location;
    }

    public static void setLocation(String location) {
        AppConfig.location = location;
    }

    public static String getImageNaming() {
        return imageNaming;
    }

    public static void setImageNaming(String imageNaming) {
        AppConfig.imageNaming = imageNaming;
    }

    private static ImageFormat parseImageFormat(final String format) {
        switch (format.toLowerCase()) {
            case "jpg":
                return ImageFormat.JPG;
            case "png":
                return ImageFormat.PNG;
            case "gif":
                return ImageFormat.GIF;
            case "BMP":
                return ImageFormat.BMP;
            default:
                return ImageFormat.JPG;
        }
    }

    private static Dimension parseResolution(String resolution) {
        Dimension dimension;
        if (resolution == null || resolution.isEmpty()) {
            LOGGER.warn("-> Invalid Image Resolution, System Will Set to Default [960*720]...");
            resolution = "960*720";
        }
        switch (resolution) {

            case "1920*1080":
                dimension = new Dimension(1920, 1080);
                break;
            case "1600*900":
                dimension = new Dimension(1600, 900);
                break;
            case "1280*720":
                dimension = new Dimension(1280, 720);
                break;
            case "1024*768":
                dimension = new Dimension(1024, 768);
                break;
            case "960*720":
                dimension = new Dimension(960, 720);
                break;
            case "640*480":

            default:
                dimension = new Dimension(640, 480);
        }
        return dimension;
    }

    public static void loadConfig() {
        Properties prop = new Properties();
        try (InputStream inputStream = new FileInputStream(CONFIG_PATH)) {
            prop.load(inputStream);
            cameraName = prop.getProperty("camera.name");
            resolution = parseResolution(prop.getProperty("camera.resolution"));

            imageFormat = parseImageFormat(prop.getProperty("camera.imageFormat", "png"));

            storagePath = prop.getProperty("camera.storagePath", DEFAULT_PICTURE_PATH);
            Path path = Paths.get(storagePath);
            if (!Files.exists(path)) {
                LOGGER.info("-> Directory: [{}] Does Not Exist, System Will Auto Create....", storagePath);
                Files.createDirectory(path);
            }

            enableScheduler = Boolean.parseBoolean(prop.getProperty("camera.enableScheduler", "false"));

            capturePeriod = Integer.parseInt(prop.getProperty("camera.capturePeriod", "60"));
            if (capturePeriod < 20) {
                LOGGER.warn("-> Invalid Camera Capture Period, System Set to 120 Seconds");
                capturePeriod = 20;
            }

            location = prop.getProperty("app.location");
            imageNaming = prop.getProperty("app.picNaming", "timestamp").toLowerCase();

        } catch (FileNotFoundException e) {
            LOGGER.error("Config File Not Found!");
            System.exit(1);
        } catch (IOException e) {
            LOGGER.error("Load Config File Failed: " + e.getMessage());
            System.exit(1);
        }
    }


}
