package gew.qrcode.util;

import gew.qrcode.model.ImageFormat;

import java.nio.file.Path;

public class ImageUtil {

    private ImageUtil () {

    }

    public static boolean suffixCheck(final Path path, final ImageFormat format) {
        String suffix = path.toString().substring(path.toString().lastIndexOf(".") + 1);
        return format.toString().equalsIgnoreCase(suffix);
    }
}
