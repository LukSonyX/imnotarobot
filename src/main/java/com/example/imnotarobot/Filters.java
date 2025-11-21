package com.example.imnotarobot;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
class YUV {
    public double y;
    public double u;
    public double v;
}
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

    protected BufferedImage invertColors(BufferedImage image) {
        BufferedImage invertedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color color = new Color(image.getRGB(x, y));
                Color invertedColor = new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
                invertedImage.setRGB(x, y, invertedColor.getRGB());
            }
        }
        return invertedImage;
    }

    protected BufferedImage pixelSort(BufferedImage image) {
        BufferedImage sortedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        List<YUV> pixels = new ArrayList<>();
        YUV pixel = new YUV();

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color color = new Color(image.getRGB(x, y));
                pixel.y =  0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue();
                pixel.u = 0.493 * (color.getBlue() - pixel.y);
                pixel.v = 0.877 * (color.getRed() - pixel.y);
                pixels.add(pixel);
            }
        }


        pixels.sort(Comparator.comparingDouble(yuv -> (yuv.y + yuv.u + yuv.v)));

        int index = 0;
        for (int x = 0; x < sortedImage.getWidth(); x++) {
            for (int y = 0; y < sortedImage.getHeight(); y++) {
                Color color = new Color(image.getRGB(x, y));
                color = Color(pixels[index].y + 1.402 (pixels[index].u -128), Y - 0.34414 (pixels[index].u - 128) - 0.71414 (pixels[index].v - 128), pixels[index].y + 1.772 (pidels[index].u-128));
                sortedImage.setRGB(x, y, color.getRGB());
                index++;
            }
        }

        return sortedImage;
    }
}
