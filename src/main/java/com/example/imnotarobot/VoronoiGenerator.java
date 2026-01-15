package com.example.imnotarobot;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

public class VoronoiGenerator {
    static class Point {
        final int x, y;
        Point(int x, int y){ this.x = x; this.y = y; }
    }

    static Point[] generateSites(int N, int width, int height, long seed){
        Random r = new Random(seed);
        Point[] sites = new Point[N];
        for(int i=0;i<N;i++){
            sites[i] = new Point(r.nextInt(width), r.nextInt(height));
        }
        return sites;
    }

    static int dist2(int x1,int y1,int x2,int y2){
        int dx = x1-x2, dy = y1-y2;
        return dx*dx + dy*dy;
    }

    static BufferedImage renderVoronoi(Point[] sites, int width, int height) {
        BufferedImage colorImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int[] colors = new int[sites.length];
        Random r = new Random(0xC0FFEE);
        for(int i=0;i<sites.length;i++){
            int rr = 100 + r.nextInt(190), gg = 100 + r.nextInt(190), bb = 100 + r.nextInt(190);
            colors[i] = (rr<<16) | (gg<<8) | bb;
        }

        int[][] nearestIdx = new int[width][height];
        int maxDist2 = 0;

        for(int x=0;x<width;x++){
            for(int y=0;y<height;y++){
                int bestIdx = -1;
                int bestD2 = Integer.MAX_VALUE;
                for(int i=0;i<sites.length;i++){
                    int d2 = dist2(x,y,sites[i].x, sites[i].y);
                    if(d2 < bestD2){
                        bestD2 = d2; bestIdx = i;
                    }
                }
                nearestIdx[x][y] = bestIdx;
                if(bestD2 > maxDist2) maxDist2 = bestD2;
            }
        }

        for(int x=0;x<width;x++){
            for(int y=0;y<height;y++){
                int idx = nearestIdx[x][y];
                colorImg.setRGB(x,y, colors[idx]);
            }
        }

        return colorImg;
    }

    public static BufferedImage generateVoronoi(long seed) throws IOException {
        int width = 800, height = 600, sites = 64;
        Point[] pts = generateSites(sites, width, height, seed);
        return renderVoronoi(pts, width, height);
    }
}
