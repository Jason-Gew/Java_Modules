package gew.qrcode.util;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * GUI & Basic Function Test Only
 */
public class VideoStreamReader extends JFrame implements Runnable {


    private Webcam webcam;
    private JTextArea texture;

    private boolean keepRunning;
    private long scanPeriod = 200;


    public VideoStreamReader(WebcamResolution... resolutions) {
        super();
        setLayout(new FlowLayout());
        setTitle("QR / Bar Code Camera Scanner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        webcam = Webcam.getWebcams().get(0);
        Dimension size;

        if (resolutions.length > 0) {
            size = resolutions[0].getSize();
        } else {
            size = WebcamResolution.VGA.getSize();       // Change Camera Resolution
        }
        webcam.setViewSize(size);
        System.out.println("-> Using Web-Camera: " + webcam.getName() + " | Resolution: " + size);


        WebcamPanel panel = new WebcamPanel(webcam);
        panel.setPreferredSize(size);
        panel.setFPSDisplayed(true);
        panel.setBackground(Color.darkGray);

        texture = new JTextArea();
        texture.setEditable(false);
        texture.setPreferredSize(size);

        add(panel);
        add(texture);

        pack();
        setVisible(true);
    }


    public boolean isKeepRunning() {
        return keepRunning;
    }

    public void setKeepRunning(boolean keepRunning) {
        this.keepRunning = keepRunning;
    }

    public long getScanPeriod() {
        return scanPeriod;
    }

    public void setScanPeriod(long scanPeriod) {
        if (scanPeriod < 50) {
            throw new IllegalArgumentException("Scan Period is too fast!");
        } else if (scanPeriod > 1500) {
            throw new IllegalArgumentException("Scan Period is too slow!");
        }
        this.scanPeriod = scanPeriod;
    }

    @Override
    public void run() {

        while (!Thread.currentThread().isInterrupted()) {
            try {
                TimeUnit.MILLISECONDS.sleep(scanPeriod);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }

            Result result = null;
            BufferedImage image;

            if (webcam.isOpen()) {
                if ((image = webcam.getImage()) == null) {
                    continue;
                }

                LuminanceSource source = new BufferedImageLuminanceSource(image);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                try {
                    result = new MultiFormatReader().decode(bitmap);

                } catch (NotFoundException e) {
//                    texture.setText("\n\n\n\n\n\n\n\n\t\t             * * * * * *    No QR-Code Found    * * * * * *");
                }
            } else {
                boolean status = webcam.open();
                System.out.println("Re-Open Camera: " + (status ? "Success" : "Failed"));
            }
            if (result != null) {
                texture.setText(result.getText());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
                System.out.println(LocalDateTime.now().format(formatter)
                        + " | Scan QR Code Success! Text: " + result.getText());
                if (!keepRunning) {
                    try {
                        TimeUnit.SECONDS.sleep(3);
                        break;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        }
    }
}
