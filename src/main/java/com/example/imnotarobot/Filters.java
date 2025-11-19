package com.example.imnotarobot;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Filters {

    // Grayscale filter
    protected BufferedImage grayscaleFilter(BufferedImage image) {
        BufferedImage grayscaleImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color color = new Color(image.getRGB(x, y));
                int grayValue = (int) (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue());
                Color grayColor = new Color(grayValue, grayValue, grayValue);

                grayscaleImage.setRGB(x, y, grayColor.getRGB());
            }
        }
        return grayscaleImage;
    }
}
