package gew.qrcode.reader;


import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import gew.qrcode.model.QRCodeReaderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Jason/GeW
 * @since 2019-03-24
 */
public class QRCodeReaderImpl implements QRCodeReader {


    private Charset defaultCharset = StandardCharsets.UTF_8;

    private static final Logger log = LoggerFactory.getLogger(QRCodeReaderImpl.class);


    public QRCodeReaderImpl() {

    }

    public QRCodeReaderImpl(Charset defaultCharset) {
        this.defaultCharset = defaultCharset;
    }


    private Map<DecodeHintType, Object> setDefaultHints() {
        Map<DecodeHintType, Object> hints = new HashMap<>();
        if (defaultCharset != null) {
            hints.put(DecodeHintType.CHARACTER_SET, defaultCharset.name());
        }
        return hints;
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
    public Result readFromPath(Path path, Map<DecodeHintType, Object> hints) throws IOException, QRCodeReaderException {
        return readFromInputStream(Files.newInputStream(path), hints);
    }

    @Override
    public String read(Path path) throws IOException, QRCodeReaderException {
        return readFromPath(path, null).getText();
    }

    @Override
    public Result readFromInputStream(InputStream inputStream,  Map<DecodeHintType, Object> hints)
            throws IOException, QRCodeReaderException {
        BufferedImage bufferedImage = ImageIO.read(inputStream);
        return readFromBufferedImage(bufferedImage, null);
    }

    @Override
    public String read(InputStream inputStream)
            throws IOException, QRCodeReaderException {
        return readFromInputStream(inputStream, null).getText();
    }

    @Override
    public Result readFromBufferedImage(BufferedImage bufferedImage, Map<DecodeHintType, Object> hints)
            throws QRCodeReaderException {
        Result result;
        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        final Reader reader = new MultiFormatReader();
        Map<DecodeHintType, Object> defaultHints = setDefaultHints();
        try {
            if (hints != null && !hints.isEmpty()) {
                defaultHints.putAll(hints);
                result = reader.decode(bitmap, defaultHints);
            } else if (!defaultHints.isEmpty()) {
                result = reader.decode(bitmap, defaultHints);
            } else {
                result = reader.decode(bitmap);
            }
        } catch (FormatException e) {
            log.debug("Read Failed Due to Format Error: {}", e.getMessage());
            throw new QRCodeReaderException("Read Failed Due to Format Error", e.getCause());

        } catch (ChecksumException e) {
            log.debug("Read Failed Due to Checksum Error: {}", e.getMessage());
            throw new QRCodeReaderException("Read Failed Due to Checksum Error", e.getCause());

        } catch (NotFoundException e) {
            log.debug("Read Failed QR Code Not Found: {}", e.getMessage());
            throw new QRCodeReaderException("Read Failed Due to QR Code Not Found", e.getCause());
        }
        return result;
    }

    @Override
    public String read(BufferedImage bufferedImage) throws QRCodeReaderException {
        return read(bufferedImage, null);
    }

    @Override
    public String read(BufferedImage bufferedImage, Map<DecodeHintType, Object> hints) throws QRCodeReaderException {
        Result result = readFromBufferedImage(bufferedImage, hints);
        return result.getText();
    }

    @Override
    public Result readFromByteArray(byte[] image, Map<DecodeHintType, Object> hints)
            throws QRCodeReaderException, IOException {
        return readFromInputStream(new ByteArrayInputStream(image), hints);
    }

    @Override
    public String read(byte[] image) throws QRCodeReaderException, IOException {
        return readFromByteArray(image, null).getText();
    }
}
