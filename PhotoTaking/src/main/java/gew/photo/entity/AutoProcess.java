package gew.photo.entity;

import gew.photo.camera.Camera;
import gew.photo.post.ImagePost;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Auto Process For Scheduler Service
 */
public class AutoProcess implements Runnable {

    private Camera camera;
    private String localPath;
    private boolean removeFileAfterFinished;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
    private static final Logger LOGGER = LoggerFactory.getLogger(AutoProcess.class);

    public AutoProcess(Camera camera, String localPath) {
        if (camera == null) {
            throw new IllegalArgumentException("-> Camera Has Not Been Initialized!");
        } else if (localPath == null || localPath.isEmpty()) {
            throw new IllegalArgumentException("-> Invalid Local Image Store Path!");
        } else {
            this.camera = camera;
            this.localPath = localPath;
        }
    }

    public boolean isRemoveFileAfterFinished() {
        return removeFileAfterFinished;
    }

    public void setRemoveFileAfterFinished(boolean removeFileAfterFinished) {
        this.removeFileAfterFinished = removeFileAfterFinished;
    }

    private String timestamp() {
        return LocalDateTime.now().format(FORMATTER);
    }

    @Override
    public void run() {
        try {
            if (!camera.isOpen()) {
                camera.open();
            }
            String image = camera.take(localPath + timestamp());
            if (image != null) {

                /* Further Process Code */
                //TODO
                if (removeFileAfterFinished) {
                    try {
                        Path path = Paths.get(image);
                        Files.deleteIfExists(path);
                    } catch (IOException e) {
                        LOGGER.error("-> Delete Redundant File Exception: " + e.getMessage());
                    }
                }
            } else {
                LOGGER.error("-> Unable to Locate Image File");
            }
        } catch (Exception err) {
            LOGGER.error("-> Auto Process Exception: " + err.getMessage());
        } finally {
            camera.close();
        }
    }
}
