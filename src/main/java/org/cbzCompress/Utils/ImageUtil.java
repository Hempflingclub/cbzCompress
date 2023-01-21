package org.cbzCompress.Utils;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

abstract class ImageUtil { //package-private
    protected static void convertAndSaveImage(String imagePath, int quality) {
        try {
            File imageFile = new File(imagePath);
            BufferedImage img = ImageIO.read(imageFile);
            ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
            ImageOutputStream ios = ImageIO.createImageOutputStream(imageFile);
            writer.setOutput(ios);
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality((float) quality / 100);
            writer.write(null, new IIOImage(img, null, null), param);
            ios.close();
            writer.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
