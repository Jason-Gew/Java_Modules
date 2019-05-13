package gew.qrcode;


import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import gew.qrcode.reader.CameraQRCodeReader;
import gew.qrcode.util.VideoStreamReader;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.core.scheme.VCard;
import net.glxn.qrgen.javase.QRCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * @author Jason/GeW
 * @since 2019-03-24
 */
public class Main {

    private static ExecutorService executorService;
    static final String PIC_FOLDER = "pictures/";

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        log.info("QR Code Tool Start...\n");

//        readQRCode(Paths.get(PIC_FOLDER + "WeChat-Login.JPG"));
//        createQRCode(Paths.get(PIC_FOLDER + "URL-QRCode.jpg"), "https://www.google.com");
//        readQRCode(Paths.get(PIC_FOLDER + "URL-QRCode.jpg"));

//        createVCard(Paths.get(PIC_FOLDER + "John-VCard.png"));
//        readQRCode(Paths.get(PIC_FOLDER + "John-VCard.png"));

//        cameraScan();
        cameraReader();
    }

    private static void createQRCode(final Path path, final String content) throws IOException {
        QRCode qrCode = QRCode.from(content);
        qrCode.withCharset(StandardCharsets.UTF_8.name());
        qrCode.withErrorCorrection(ErrorCorrectionLevel.L);
        String suffix = path.toString().substring(path.toString().lastIndexOf(".") + 1);
        switch (suffix.toUpperCase()) {
            case "PNG":
                qrCode.to(ImageType.PNG);
                break;
            case "JPG":
            case "JPEG":
                qrCode.to(ImageType.JPG);
                break;
            case "GIF":
                qrCode.to(ImageType.GIF);
                break;
            case "BMP":
                qrCode.to(ImageType.BMP);
                break;
            default:
                throw new IllegalArgumentException("Invalid Image Format: " + suffix);
        }

        ByteArrayOutputStream byteArrayOutputStream = qrCode.stream();
        Path result = Files.write(path, byteArrayOutputStream.toByteArray());

        log.info("Test QRCode has been written to: " +  result);
    }

    private static void createVCard (final Path path) throws IOException {
        VCard johnDoe = new VCard("John Doe")
                .setEmail("john.doe@example.org")
                .setAddress("5001 John Doe Street Apt 123, Austin, TX 12345")
                .setTitle("Dr.")
                .setCompany("John Doe Inc.")
                .setPhoneNumber("12345678")
                .setWebsite("www.example.org");
        QRCode qrCode = QRCode.from(johnDoe);
        qrCode.to(ImageType.JPG);
        qrCode.withCharset(StandardCharsets.UTF_8.name());
        qrCode.withErrorCorrection(ErrorCorrectionLevel.L);
        ByteArrayOutputStream byteArrayOutputStream = qrCode.stream();
        Files.write(path, byteArrayOutputStream.toByteArray());

        log.info("Test VCard QRCode has been written to: " + path.getFileName());
    }

    private static void readQRCode(final Path path) throws IOException {
        try {
            BufferedImage bufferedImage = ImageIO.read(Files.newInputStream(path));
            LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            Result result = new MultiFormatReader().decode(bitmap);
            String content = result.getText();
            log.info("Read QR Code [{}] Result: {}", path.getFileName(), content);

        } catch (NotFoundException e) {
            log.error("QRCode Not Found From File: " + path.getFileName());

        }
    }

    private static void cameraScan() {
        executorService = Executors.newSingleThreadExecutor();
        VideoStreamReader scanner = new VideoStreamReader();
        scanner.setKeepRunning(true);
        executorService.submit(scanner);
        executorService.shutdown();
    }

    private static void cameraReader() {
        CameraQRCodeReader cameraQRCodeReader = new CameraQRCodeReader();
        FutureTask<String> scanTask = new FutureTask<>(cameraQRCodeReader);
        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(scanTask);
        while (!Thread.currentThread().isInterrupted()) {
            if (scanTask.isDone()) {
                try {
                    String result = scanTask.get();
                    log.info("Get Camera QR Code Read Result: {}", result);
                } catch (Exception err) {
                    log.error("Camera Read Thread Error: {}", err.getMessage());
                }
                break;
            }
        }
        executorService.shutdown();
    }
}
