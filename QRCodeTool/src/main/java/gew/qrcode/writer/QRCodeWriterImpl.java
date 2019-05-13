package gew.qrcode.writer;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import gew.qrcode.model.ImageFormat;
import gew.qrcode.model.QRCodeWriterException;
import gew.qrcode.util.ImageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;


/**
 * QRCode Writer Implementation Wrapper Based on Google zxing.
 * @author Jason/GeW
 * @since 2019-03-24
 */
public class QRCodeWriterImpl implements QRCodeWriter {

    private Dimension defaultDimension;
    private ImageFormat defaultImageFormat;
    private MatrixToImageConfig colorConfig;
    private Charset defaultCharset = StandardCharsets.UTF_8;
    private ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevel.L;

    private static final Logger log = LoggerFactory.getLogger(QRCodeWriter.class);


    public QRCodeWriterImpl() {
        defaultDimension = new Dimension(150, 150);
        defaultImageFormat = ImageFormat.PNG;
    }

    public QRCodeWriterImpl(Dimension defaultDimension) {
        this.defaultDimension = defaultDimension;
        defaultImageFormat = ImageFormat.PNG;
    }

    public QRCodeWriterImpl(Dimension defaultDimension, ImageFormat defaultImageFormat) {
        this.defaultDimension = defaultDimension;
        this.defaultImageFormat = defaultImageFormat;
    }

    private BitMatrix generate(String text, Map<EncodeHintType, Object> hints) throws WriterException {
        Writer writer = new MultiFormatWriter();
        if (hints == null || hints.isEmpty()) {
            hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, defaultCharset.name());
            hints.put(EncodeHintType.ERROR_CORRECTION, errorCorrectionLevel);
        }
        return writer.encode(text, BarcodeFormat.QR_CODE,
                defaultDimension.width, defaultDimension.height, hints);
    }

    @Override
    public Charset getDefaultCharset() {
        return this.defaultCharset;
    }

    @Override
    public void setDefaultCharset(Charset charset) {
        this.defaultCharset = charset;
    }

    @Override
    public ImageFormat getDefaultImageFormat() {
        return this.defaultImageFormat;
    }

    @Override
    public void setDefaultImageFormat(ImageFormat defaultImageFormat) {
        this.defaultImageFormat = defaultImageFormat;
    }

    @Override
    public ErrorCorrectionLevel getErrorCorrectionLevel() {
        return errorCorrectionLevel;
    }

    @Override
    public void setErrorCorrectionLevel(ErrorCorrectionLevel errorCorrectionLevel) {
        this.errorCorrectionLevel = errorCorrectionLevel;
    }

    @Override
    public void addColor(MatrixToImageConfig colorConfig) {
        if (colorConfig != null) {
            this.colorConfig = colorConfig;
        }
    }

    @Override
    public BufferedImage toBufferedImage(String text, Map<EncodeHintType, Object> hints) throws QRCodeWriterException {
        BufferedImage image;
        BitMatrix bitMatrix;
        try {
            bitMatrix = generate(text, hints);
        } catch (WriterException e) {
            log.error("Generate QR Code for [{}] Failed: {}", text, e.getMessage());
            throw new QRCodeWriterException(e.getMessage(), e.getCause());
        }
        if (colorConfig != null) {
            image = MatrixToImageWriter.toBufferedImage(bitMatrix, colorConfig);
        } else {
            image = MatrixToImageWriter.toBufferedImage(bitMatrix);
        }
        return image;
    }

    @Override
    public BufferedImage toBufferedImage(String text) throws QRCodeWriterException {
        return toBufferedImage(text, null);
    }

    @Override
    public ByteArrayOutputStream toByteArrayOutputStream(String text, Map<EncodeHintType, Object> hints)
            throws QRCodeWriterException, IOException {
        BufferedImage image = toBufferedImage(text, hints);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        boolean status = ImageIO.write(image, defaultImageFormat.toString(), byteArrayOutputStream);
        log.debug("QR Code Write to ByteArrayOutputStream: {}", status ? "Success" : "Failed");
        return byteArrayOutputStream;
    }

    @Override
    public ByteArrayOutputStream toByteArrayOutputStream(String text) throws IOException, QRCodeWriterException {
        return toByteArrayOutputStream(text, null);
    }

    @Override
    public boolean toPath(String text, Map<EncodeHintType, Object> hints, Path path)
            throws IOException, QRCodeWriterException {
        BufferedImage image;
        boolean suffix = ImageUtil.suffixCheck(path, defaultImageFormat);
        log.debug("Image Suffix and Format Match {}", suffix ? "Success" : "Failed");
        image = toBufferedImage(text, hints);
        return ImageIO.write(image, defaultImageFormat.toString(), path.toFile());
    }

    @Override
    public boolean toPath(String text, Path path) throws IOException, QRCodeWriterException {
        return toPath(text, null, path);
    }
}
