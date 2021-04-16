package com.loohp.limbo.utils;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class ImageUtils {

    public static String imgToBase64String(final RenderedImage img, String formatName) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(img, formatName, out);
        return Base64.getEncoder().encodeToString(out.toByteArray());
    }

}
