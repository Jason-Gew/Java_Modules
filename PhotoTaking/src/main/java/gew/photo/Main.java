package gew.photo;

import java.awt.Dimension;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.github.sarxos.webcam.Webcam;
import gew.photo.camera.Camera;
import gew.photo.config.AppConfig;
import gew.photo.entity.AutoProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main
{
    private static final String EXIT_SYMBOL = "/exit";
    private static final String TAKE_IMAGE = "/take";
    private static final String STATUS = "/status";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss-SSS");

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static void resolution() {
        System.out.println("\n======== Image Resolution ========");
        System.out.println("[1] Image Resolution 640 * 480 ");
        System.out.println("[2] Image Resolution 960 * 720 ");
        System.out.println("[3] Image Resolution 1024 * 768 ");
        System.out.println("[4] Image Resolution 1280 * 720 ");
        System.out.println("[5] Image Resolution 1600 * 900 ");
        System.out.println("[6] Image Resolution 1920 * 1080");
    }

    private static Dimension resolution(int option) {
        Dimension dimension;
        switch (option) {
            case 1:
                dimension = new Dimension(640, 480);
                break;
            case 2:
                dimension = new Dimension(960, 720);
                break;
            case 3:
                dimension = new Dimension(1024, 768);
                break;
            case 4:
                dimension = new Dimension(1280, 720);
                break;
            case 5:
                dimension = new Dimension(1600, 900);
                break;
            case 6:
                dimension = new Dimension(1920, 1080);
                break;
            default:
                dimension = new Dimension(640, 480);

        }
        return dimension;
    }

    public static void main(String[] args) {
        AppConfig.loadConfig();
        int num = 0;
        List<Webcam> cameras = Camera.detectWebCams();
        for(Webcam webcam : cameras) {
            System.out.println("Detected Web-Camera [" + num + "] " + webcam.getDevice().getName());
            num++;
        }
        Scanner scanner = new Scanner(System.in);

        System.out.print("\nPlease Select Web-Camera Number: ");
        String selection = scanner.nextLine();
        try{
            num = Integer.parseInt(selection.trim());
            if(num < 0 || num > cameras.size() - 1) {
                System.out.println("\nInvalid Web-Camera Number!!!");
                System.exit(1);
            }
        } catch (NumberFormatException err) {
            System.err.println("Invalid Input: " + err.getMessage());
            scanner.close();
            System.exit(1);
        }

        Camera camera = Camera.getInstance();
        try {
            camera.initialize(cameras.get(num).getName());
        } catch (Exception e) {
            logger.error("Initialize Web Camera Failed: " + e.getMessage());
        }

        camera.setCustomerResolutions();
        camera.setImageSize(new Dimension(960, 720));

        logger.info("-> Web-Camera Has Been Initialized!");

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
        AutoProcess autoProcess = new AutoProcess(camera, AppConfig.getStoragePath());
        executor.scheduleAtFixedRate(autoProcess, 5, AppConfig.getCapturePeriod(), TimeUnit.SECONDS);

        logger.info("-> Scheduler Thread Begins Running...");
        ExecutorService pool = Executors.newFixedThreadPool(1);
        manualProcess(scanner, camera, pool);

    }

    private static void manualProcess(Scanner scanner, Camera camera, ExecutorService pool) {
        while (true) {
            try {
                String input;
                input = scanner.nextLine();

                if (input == null || input.isEmpty()) {
                    Thread.sleep(1000);
                } else if (input.equalsIgnoreCase(EXIT_SYMBOL)) {
                    logger.warn("-> User Terminate The System...");
                    pool.shutdownNow();
                    System.exit(0);
                } else if (input.equalsIgnoreCase(TAKE_IMAGE)) {
                    AutoProcess autoProcess = new AutoProcess(camera, AppConfig.getStoragePath());
                    pool.submit(autoProcess);
                } else if (input.equalsIgnoreCase(STATUS)) {
                    if (camera.isOpen()) {
                        logger.info("Camera is Opened and Using By Different Thread");
                    } else {
                        logger.info("Camera is Closed and Ready For Taking Picture");
                    }
                }
                Thread.sleep(1000);

            } catch (NoSuchElementException e) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            } catch (Exception err) {
                logger.error("-> Exception In Manual Process: " + err.getMessage());
            }

        }
    }
}
