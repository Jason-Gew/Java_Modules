package gew.qrcode.reader;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamException;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import gew.qrcode.model.QRCodeReaderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * One Time Camera/Video QR Code Reader/Scanner. Support Both QR Code and Bar Code, Output as UTF-8 String.
 * @author Jason/GeW
 * @since 2019-03-24
 */
public class CameraQRCodeReader implements Callable<String> {


    private Webcam webcam;
    private Dimension resolution;
    private QRCodeReader qrCodeReader;

    private long scanPeriod = 150;      // QR Code Scan Frequency
    private long timeout = 40000;       // Timeout for Keep Scanning
    private final TimeUnit timeUnit = TimeUnit.MILLISECONDS;

    private Map<DecodeHintType, Object> hints;

    private static final Logger log = LoggerFactory.getLogger(CameraQRCodeReader.class);


    public CameraQRCodeReader() {
        // Use System Default Camera
        qrCodeReader = new QRCodeReaderImpl();
    }

    public CameraQRCodeReader(Webcam webcam) {
        this.webcam = webcam;
        qrCodeReader = new QRCodeReaderImpl();
    }

    public CameraQRCodeReader(Webcam webcam, Dimension resolution) {
        this.webcam = webcam;
        this.resolution = resolution;
        qrCodeReader = new QRCodeReaderImpl();
    }

    public long getScanPeriod() {
        return scanPeriod;
    }

    public void setScanPeriod(long scanPeriod) {
        if (scanPeriod < 20) {
            log.warn("Scan Period [{} ms] is Too Fast, System Will Use Default: {} ms", scanPeriod, this.scanPeriod);
        } else if (scanPeriod > 1000) {
            log.warn("Scan Period [{} ms] is Too Slow, System Will Use Default: {} ms", scanPeriod, this.scanPeriod);
        } else {
            this.scanPeriod = scanPeriod;
        }
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        if (timeout < 10000) {
            log.warn("Timeout [{} ms] is Too Short, System Will Use Default: {} ms", timeout, this.timeout);
        }
        this.timeout = timeout;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public Map<DecodeHintType, Object> getHints() {
        return hints;
    }

    public void setHints(Map<DecodeHintType, Object> hints) {
        this.hints = hints;
    }


    private void initialize() throws Exception {
        try {
            if (webcam == null) {
                log.debug("No Web-Camera Assigned, System Will Try to Allocate the Default");
                List<Webcam> cameras = Webcam.getWebcams(10000);
                if (cameras.isEmpty()) {
                    log.error("No Web-Camera Found");
                    throw new WebcamException("No Web-Camera Found");
                } else {
                    webcam = cameras.get(0);
                }
            }
            if (resolution != null) {
                webcam.setViewSize(resolution);
                log.info("Custom Camera Resolution [{} * {}] Has Been Set!", resolution.width, resolution.height);
            } else {
                webcam.setViewSize(WebcamResolution.QVGA.getSize());
            }
            log.info("System Will Use Web-Camera: {} | Resolution: [{} * {}]",
                    webcam.getName(), webcam.getViewSize().width, webcam.getViewSize().height);
            boolean status = webcam.open();
            log.info("Open Web-Camera: {}", status ? "Success" : "Failed");

        } catch (TimeoutException e) {
            log.error("Get Web-Camera Timeout: {}", e.getMessage());
            throw e;

        } catch (WebcamException we) {
            if (we.getMessage().toLowerCase().contains("cannot execute task")) {
                log.error("Web-Camera [{}] May Be Occupied By Other Program", webcam.getName());
                throw new IllegalStateException("Web-Camera May Be Occupied By Other Program", we.getCause());
            } else {
                log.error("Web-Camera Error: {}", we.getMessage());
                throw we;
            }
        }
    }


    @Override
    public String call() throws Exception {
        initialize();
        long start = System.currentTimeMillis();
        while (!Thread.currentThread().isInterrupted()) {
            if ((start + timeout) > System.currentTimeMillis()) {
                try {
                    TimeUnit.MILLISECONDS.sleep(scanPeriod);
                } catch (InterruptedException e) {
                    log.warn("Camera Reading Process Got Interrupted");
                    throw e;
                }
                BufferedImage image;
                if (webcam.isOpen()) {
                    if ((image = webcam.getImage()) == null) {
                        continue;
                    }

                    try {
                        if (hints != null && !hints.isEmpty()) {
                            Result result = qrCodeReader.readFromBufferedImage(image, hints);
                            if (result != null) {
                                String data = result.getText();
                                log.debug("Detected Text: {}", data);
                                webcam.close();
                                return data;
                            }
                        } else {
                            String result = qrCodeReader.read(image);
                            log.debug("Detected Text: {}", result);
                            webcam.close();
                            return result;
                        }

                    } catch (QRCodeReaderException e) {
                        // QR code or Bar code Not Found in Image!
                    }
                } else {
                    boolean reopen = webcam.open(true);
                    log.debug("System Try Re-opening the Web Camera: {}", reopen ? "Success" : "Failed");
                }
            } else {
                webcam.close();
                throw new TimeoutException(timeout + " ms Timeout, No QR Code or Bar Code Detected");
            }
        }
        throw new InterruptedException("Camera Reading Process Got Interrupted");
    }
}
