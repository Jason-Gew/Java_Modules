package gew.qrcode.reader;


import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import gew.qrcode.model.QRCodeReaderException;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Map;

/**
 * @author Jason/GeW
 * @since 2019-03-24
 */
public interface QRCodeReader {

    Charset getDefaultCharset();

    void setDefaultCharset(Charset charset);

    Result readFromPath(final Path path, Map<DecodeHintType, Object> hints)
            throws IOException, QRCodeReaderException;

    String read(final Path path) throws IOException, QRCodeReaderException;

    Result readFromInputStream(InputStream inputStream, Map<DecodeHintType, Object> hints)
            throws IOException, QRCodeReaderException;

    String read(InputStream inputStream) throws IOException, QRCodeReaderException;


    Result readFromBufferedImage(final BufferedImage bufferedImage, final Map<DecodeHintType, Object> hints)
            throws QRCodeReaderException;

    String read(final BufferedImage bufferedImage) throws QRCodeReaderException;

    String read(final BufferedImage bufferedImage, final Map<DecodeHintType, Object> hints)
            throws QRCodeReaderException;

    Result readFromByteArray(final byte[] image, final Map<DecodeHintType, Object> hints)
            throws IOException, QRCodeReaderException;

    String read(final byte[] image) throws IOException, QRCodeReaderException;
}
