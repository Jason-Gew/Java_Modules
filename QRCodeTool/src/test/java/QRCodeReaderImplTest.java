import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import gew.qrcode.reader.QRCodeReader;
import gew.qrcode.reader.QRCodeReaderImpl;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class QRCodeReaderImplTest {

    private QRCodeReader qrCodeReader;
    private static final String PIC_FOLDER = "pictures/";
    private static final String TEST_PIC_NAME = "test5.png";

    private static final Logger log = LoggerFactory.getLogger(QRCodeReaderImplTest.class);

    @Before
    public void setUp() throws Exception {
        qrCodeReader = new QRCodeReaderImpl();
        if (!Files.exists(Paths.get(PIC_FOLDER + TEST_PIC_NAME))) {
            throw new IllegalStateException("-> Unable to find test QR Code Picture: " + TEST_PIC_NAME);
        } else {
            log.info("-> Start QRCodeReaderImpl Test");
        }
    }

    @Test
    public void read() {
        try {
            String result = qrCodeReader.read(Paths.get(PIC_FOLDER + TEST_PIC_NAME));
            System.out.println(result);
            assertNotNull(result);
        } catch (Exception e) {
            log.error("Read QR Code Test Failed: {}", e.getMessage());
        }
    }

    @Test
    public void readFrom() {
    }

    @Test
    public void readFromByteArray() {
    }
}