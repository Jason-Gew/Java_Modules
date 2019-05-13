package gew.qrcode.writer;


import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import gew.qrcode.model.ImageFormat;
import gew.qrcode.model.QRCodeWriterException;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Map;

/**
 * @author Jason/GeW
 * @since 2019-03-24
 */
public interface QRCodeWriter {

    Charset getDefaultCharset();

    void setDefaultCharset(Charset charset);

    ImageFormat getDefaultImageFormat();

    void setDefaultImageFormat(ImageFormat defaultImageFormat);

    ErrorCorrectionLevel getErrorCorrectionLevel();

    void setErrorCorrectionLevel(ErrorCorrectionLevel errorCorrectionLevel);

    void addColor(MatrixToImageConfig colorConfig);

    BufferedImage toBufferedImage(final String text, Map<EncodeHintType, Object> hints) throws QRCodeWriterException;

    BufferedImage toBufferedImage(final String text) throws QRCodeWriterException;

    ByteArrayOutputStream toByteArrayOutputStream(final String text, Map<EncodeHintType, Object> hints)
            throws QRCodeWriterException, IOException;

    ByteArrayOutputStream toByteArrayOutputStream(final String text) throws IOException, QRCodeWriterException;

    boolean toPath(final String text, Map<EncodeHintType, Object> hints, final Path path)
            throws IOException, QRCodeWriterException;

    boolean toPath(final String text, final Path path) throws IOException, QRCodeWriterException;
}
