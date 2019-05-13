package gew.qrcode.writer;

import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import gew.qrcode.model.ImageFormat;
import gew.qrcode.model.QRCodeWriterException;
import gew.qrcode.util.ImageUtil;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * QRCode Writer Implementation Wrapper Based on glxn.qrgen (on top of Google zxing)
 * @author Jason/GeW
 * @since 2019-03-24
 */
public class QRCodeWriterQRGenImpl implements QRCodeWriter {

    private Dimension defaultDimension;
    private ImageFormat defaultImageFormat;
    private MatrixToImageConfig colorConfig;
    private Charset defaultCharset = StandardCharsets.UTF_8;
    private ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevel.M;

    private static final Logger log = LoggerFactory.getLogger(QRCodeWriter.class);


    public QRCodeWriterQRGenImpl() {
        defaultDimension = new Dimension(150, 150);
        defaultImageFormat = ImageFormat.PNG;
    }

    public QRCodeWriterQRGenImpl(Dimension defaultDimension) {
        this.defaultDimension = defaultDimension;
        defaultImageFormat = ImageFormat.PNG;
    }

    public QRCodeWriterQRGenImpl(Dimension defaultDimension, ImageFormat defaultImageFormat) {
        this.defaultDimension = defaultDimension;
        this.defaultImageFormat = defaultImageFormat;
    }

    private ImageType transferFormat(ImageFormat format) {
        switch (format) {
            case JPG:
                return ImageType.JPG;
            case GIF:
                return ImageType.GIF;
            case BMP:
                return ImageType.BMP;
            case PNG:
            default:
                return ImageType.PNG;
        }
    }

    private QRCode generate(String text, Map<EncodeHintType, Object> hints) {
        QRCode qrCode = QRCode.from(text)
                .withCharset(defaultCharset.name())
                .withSize(defaultDimension.width, defaultDimension.height);
        if (hints != null && !hints.isEmpty()) {
            hints.forEach(qrCode::withHint);
        }
        if (colorConfig != null) {
            qrCode.withColor(colorConfig.getPixelOnColor(), colorConfig.getPixelOffColor());
        }
        if (defaultImageFormat != ImageFormat.PNG) {
            qrCode.to(transferFormat(defaultImageFormat));
        }
        return qrCode;
    }


    @Override
    public ImageFormat getDefaultImageFormat() {
        return defaultImageFormat;
    }

    @Override
    public void setDefaultImageFormat(ImageFormat defaultImageFormat) {
        if (defaultImageFormat != null) {
            this.defaultImageFormat = defaultImageFormat;
        }
    }

    @Override
    public Charset getDefaultCharset() {
        return defaultCharset;
    }

    @Override
    public void setDefaultCharset(Charset charset) {
        if (charset != null) {
            this.defaultCharset = charset;
        }
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
        this.colorConfig = colorConfig;
    }

    @Override
    public BufferedImage toBufferedImage(String text, Map<EncodeHintType, Object> hints) throws QRCodeWriterException {
        try {
            return ImageIO.read(generate(text, hints).file());
        } catch (IOException e) {
            log.error("Generate QR Code for [{}] to BufferedImage Failed: {}", text, e.getMessage());
            throw new QRCodeWriterException("Generate QR Code Failed in Reading Temp File", e.getCause());
        }
    }

    @Override
    public BufferedImage toBufferedImage(String text) throws QRCodeWriterException {
        return toBufferedImage(text, null);
    }

    @Override
    public ByteArrayOutputStream toByteArrayOutputStream(String text, Map<EncodeHintType, Object> hints) {
        return generate(text, hints).stream();
    }

    @Override
    public ByteArrayOutputStream toByteArrayOutputStream(String text) {
        return toByteArrayOutputStream(text, null);
    }

    @Override
    public boolean toPath(String text, Map<EncodeHintType, Object> hints, Path path) throws IOException {
        boolean suffix = ImageUtil.suffixCheck(path, defaultImageFormat);
        log.debug("Image Suffix and Format Match {}", suffix ? "Success" : "Failed");
        ByteArrayOutputStream byteArrayOutputStream = toByteArrayOutputStream(text, hints);
        Files.write(path, byteArrayOutputStream.toByteArray());
        return true;
    }

    @Override
    public boolean toPath(String text, Path path) throws IOException {
        return toPath(text, null, path);
    }
}
