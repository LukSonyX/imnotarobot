package com.example.imnotarobot;

import java.awt.image.BufferedImage;
import java.util.Random;

public class DiamondSquareGenerator {
    public static float[][] data;
    public int width;
    public int height;

    public DiamondSquareGenerator(long mseed, int n) {
        int DATA_SIZE = (1 << n) + 1;
        width = DATA_SIZE;
        height = DATA_SIZE;
        final float SEED = 1000.0f;
        data = new float[DATA_SIZE][DATA_SIZE];
        data[0][0] = data[0][DATA_SIZE-1] = data[DATA_SIZE-1][0] =
                data[DATA_SIZE-1][DATA_SIZE-1] = SEED;

        float valmin = Float.MAX_VALUE;
        float valmax = Float.MIN_VALUE;

        float h = 500.0f;
        Random r = new Random(mseed);
        for (int sideLength = DATA_SIZE - 1; sideLength >= 2; sideLength /= 2, h /= 2.0f) {
            int halfSide = sideLength / 2;

            for (int x = 0; x < DATA_SIZE - 1; x += sideLength) {
                for (int y = 0; y < DATA_SIZE - 1; y += sideLength) {
                    float avg = data[x][y] +
                            data[x + sideLength][y] +
                            data[x][y + sideLength] +
                            data[x + sideLength][y + sideLength];
                    avg /= 4.0f;
                    data[x + halfSide][y + halfSide] =
                            avg + (r.nextFloat() * 2 * h) - h;

                    valmax = Math.max(valmax, data[x + halfSide][y + halfSide]);
                    valmin = Math.min(valmin, data[x + halfSide][y + halfSide]);
                }
            }

            for (int x = 0; x < DATA_SIZE - 1; x += halfSide) {
                for (int y = (x + halfSide) % sideLength; y < DATA_SIZE - 1; y += sideLength) {
                    float avg =
                            data[(x - halfSide + DATA_SIZE - 1) % (DATA_SIZE - 1)][y] +
                                    data[(x + halfSide) % (DATA_SIZE - 1)][y] +
                                    data[x][(y + halfSide) % (DATA_SIZE - 1)] +
                                    data[x][(y - halfSide + DATA_SIZE - 1) % (DATA_SIZE - 1)];
                    avg /= 4.0f;
                    avg = avg + (r.nextFloat() * 2 * h) - h;
                    data[x][y] = avg;

                    valmax = Math.max(valmax, avg);
                    valmin = Math.min(valmin, avg);

                    if (x == 0) data[DATA_SIZE - 1][y] = avg;
                    if (y == 0) data[x][DATA_SIZE - 1] = avg;
                }
            }
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                data[i][j] = (data[i][j] - valmin) / (valmax - valmin);
            }
        }
    }

    public static BufferedImage generateDiamondSquare(int someNumber) {
        new DiamondSquareGenerator(12345L + someNumber, 9);

        int w = data.length;
        int h = data[0].length;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int v = Math.max(0, Math.min(255, (int)(data[x][y] * 255)));
                int rgb = (v << 16) | (v << 8) | v;
                img.setRGB(x, y, rgb);
            }
        }

        return img;
    }
}
