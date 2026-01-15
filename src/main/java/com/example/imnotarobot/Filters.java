package com.example.imnotarobot;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.Arrays;

public class Filters {

    public static BufferedImage grayscale(BufferedImage src) {
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());

        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                int rgb = src.getRGB(x, y);

                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8)  & 0xFF;
                int b = (rgb)       & 0xFF;

                int gray = (int)(0.299*r + 0.587*g + 0.114*b);
                int newRGB = (gray << 16) | (gray << 8) | gray;

                out.setRGB(x, y, (rgb & 0xFF000000) | newRGB);
            }
        }
        return out;
    }

    public static BufferedImage invert(BufferedImage src) {
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());

        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                int rgb = src.getRGB(x, y);

                int r = 255 - ((rgb >> 16) & 0xFF);
                int g = 255 - ((rgb >> 8)  & 0xFF);
                int b = 255 - (rgb & 0xFF);

                int newRGB = (r << 16) | (g << 8) | b;

                out.setRGB(x, y, (rgb & 0xFF000000) | newRGB);
            }
        }
        return out;
    }

    public static BufferedImage brightness(BufferedImage src, int amount) {
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());

        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                int rgb = src.getRGB(x, y);

                int r = clamp(((rgb >> 16) & 0xFF) + amount);
                int g = clamp(((rgb >> 8)  & 0xFF) + amount);
                int b = clamp((rgb & 0xFF) + amount);

                int newRGB = (r << 16) | (g << 8) | b;

                out.setRGB(x, y, (rgb & 0xFF000000) | newRGB);
            }
        }
        return out;
    }

    public static BufferedImage contrast(BufferedImage src, float factor) {
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());

        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                int rgb = src.getRGB(x, y);

                int r = clamp((int)(((rgb >> 16) & 0xFF - 128) * factor + 128));
                int g = clamp((int)(((rgb >> 8)  & 0xFF - 128) * factor + 128));
                int b = clamp((int)(( (rgb       & 0xFF) - 128) * factor + 128));

                int newRGB = (r << 16) | (g << 8) | b;

                out.setRGB(x, y, (rgb & 0xFF000000) | newRGB);
            }
        }
        return out;
    }

    public static BufferedImage threshold(BufferedImage src, int level) {
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());

        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                int rgb = src.getRGB(x, y);

                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8)  & 0xFF;
                int b = (rgb)       & 0xFF;

                int gray = (r + g + b) / 3;
                int v = (gray < level) ? 0 : 255;

                int newRGB = (v << 16) | (v << 8) | v;

                out.setRGB(x, y, (rgb & 0xFF000000) | newRGB);
            }
        }
        return out;
    }

    public static BufferedImage boxBlur(BufferedImage src, int radius) {
        int size = radius * 2 + 1;
        float[] kernel = new float[size * size];

        Arrays.fill(kernel, 1f / kernel.length);

        ConvolveOp op = new ConvolveOp(new Kernel(size, size, kernel), ConvolveOp.EDGE_NO_OP, null);
        return op.filter(src, null);
    }

    public static BufferedImage sharpen(BufferedImage src) {
        float[] sharpenKernel = {
                0, -1,  0,
                -1,  5, -1,
                0, -1,  0
        };

        ConvolveOp op = new ConvolveOp(new Kernel(3, 3, sharpenKernel), ConvolveOp.EDGE_NO_OP, null);
        return op.filter(src, null);
    }

    public static BufferedImage edgeDetect(BufferedImage src) {
        float[] sobelX = {
                -1, 0, 1,
                -2, 0, 2,
                -1, 0, 1
        };

        float[] sobelY = {
                -1, -2, -1,
                0,  0,  0,
                1,  2,  1
        };

        ConvolveOp opX = new ConvolveOp(new Kernel(3, 3, sobelX));
        ConvolveOp opY = new ConvolveOp(new Kernel(3, 3, sobelY));

        BufferedImage gx = opX.filter(src, null);
        BufferedImage gy = opY.filter(src, null);

        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());

        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                int sx = gx.getRGB(x, y) & 0xFF;
                int sy = gy.getRGB(x, y) & 0xFF;

                int mag = clamp((int)Math.sqrt(sx * sx + sy * sy));

                int rgb = (mag << 16) | (mag << 8) | mag;
                out.setRGB(x, y, (0xFF << 24) | rgb);
            }
        }
        return out;
    }

    public static BufferedImage pixelate(BufferedImage src, int blockSize) {
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
        Graphics2D g = out.createGraphics();

        for (int y = 0; y < src.getHeight(); y += blockSize) {
            for (int x = 0; x < src.getWidth(); x += blockSize) {
                int rgb = src.getRGB(x, y);
                g.setColor(new Color(rgb));
                g.fillRect(x, y, blockSize, blockSize);
            }
        }
        g.dispose();
        return out;
    }

    private static int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }
}

