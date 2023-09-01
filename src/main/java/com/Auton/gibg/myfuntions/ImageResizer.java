package com.Auton.gibg.myfuntions;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageResizer {
    private byte[] resizedImageBytes;

    public ImageResizer(byte[] originalImageBytes) throws IOException {
        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(originalImageBytes));

        // Calculate new dimensions while maintaining aspect ratio
        int maxWidth = 1024;
        int maxHeight = 1024;
        int newWidth = originalImage.getWidth();
        int newHeight = originalImage.getHeight();

        if (originalImage.getWidth() > maxWidth) {
            newWidth = maxWidth;
            newHeight = (newWidth * originalImage.getHeight()) / originalImage.getWidth();
        }
        if (newHeight > maxHeight) {
            newHeight = maxHeight;
            newWidth = (newHeight * originalImage.getWidth()) / originalImage.getHeight();
        }

        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = resizedImage.createGraphics();
        graphics.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        graphics.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "jpg", baos);
        resizedImageBytes = baos.toByteArray();
    }

    public byte[] getResizedImageBytes() {
        return resizedImageBytes;
    }
}
