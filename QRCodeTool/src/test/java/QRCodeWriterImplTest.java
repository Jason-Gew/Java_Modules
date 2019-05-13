
import gew.qrcode.writer.QRCodeWriter;
import gew.qrcode.writer.QRCodeWriterImpl;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Dimension;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * @author Jason/GeW
 */
public class QRCodeWriterImplTest {

    private QRCodeWriter qrCodeWriter;
    private static final String PIC_FOLDER = "pictures/";
    private static final String TEST_CONTENT = "===========================\n" +
                                               "  This is a QR Code Test!\n" +
                                               "   这是一个二维码生成测试\n" +
                                               "===========================";

    private static final Logger log = LoggerFactory.getLogger(QRCodeWriterImplTest.class);

    @Before
    public void setUp() throws Exception {
        Dimension dimension = new Dimension(200, 200);
        qrCodeWriter = new QRCodeWriterImpl(dimension);
        log.info("-> Start QRCodeWriterImpl with Dimension [{} * {}]", dimension.width, dimension.height);
        if (!Files.exists(Paths.get(PIC_FOLDER))) {
            Files.createDirectory(Paths.get(PIC_FOLDER));
            log.info("-> Test Pictures Will Be Stored in: {}", PIC_FOLDER);
        }
    }

    @Test
    public void toPath() {
        long timestamp = System.currentTimeMillis() / 1000;
        try {
            boolean result = qrCodeWriter.toPath(TEST_CONTENT,
                    Paths.get(PIC_FOLDER + timestamp + "." + qrCodeWriter.getDefaultImageFormat().toString()));
            assertTrue(result);
        } catch (Exception e) {
            log.error("Test Failed: {}", e.getMessage());
        }
    }
}