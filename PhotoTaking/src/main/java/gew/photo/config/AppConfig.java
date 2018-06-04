package gew.photo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static String resolution;
    private static String imageFormat;
    private static String storagePath;
    private static Integer capturePeriod;
    private static String serverUrl;
    private static String location;

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

    public static String getResolution() {
        return resolution;
    }

    public static void setResolution(String resolution) {
        AppConfig.resolution = resolution;
    }

    public static String getImageFormat() {
        return imageFormat;
    }

    public static void setImageFormat(String imageFormat) {
        AppConfig.imageFormat = imageFormat;
    }

    public static String getStoragePath() {
        return storagePath;
    }

    public static void setStoragePath(String storagePath) {
        AppConfig.storagePath = storagePath;
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

    public static void loadConfig() {
        Properties prop = new Properties();
        try (InputStream inputStream = new FileInputStream(CONFIG_PATH)) {
            prop.load(inputStream);
            cameraName = prop.getProperty("webcamera.name");
            resolution = prop.getProperty("webcamera.resolution", "1920*1080");
            imageFormat = prop.getProperty("webcamera.imageFormat", "png");
            storagePath = prop.getProperty("webcamera.storagePath", DEFAULT_PICTURE_PATH);
            Path path = Paths.get(storagePath);
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }

            capturePeriod = Integer.parseInt(prop.getProperty("webcamera.capturePeriod", "120"));
            if (capturePeriod < 120) {
                System.err.println("-> Invalid Camera Capture Period, System Set to 120 Seconds");
                capturePeriod = 120;
            }
            serverUrl = prop.getProperty("server.url");
            location = prop.getProperty("app.location");
        } catch (FileNotFoundException e) {
            LOGGER.error("Config File Not Found!");
            System.exit(1);
        } catch (IOException e) {
            LOGGER.error("Load Config File Failed: " + e.getMessage());
            System.exit(1);
        }
    }


}
