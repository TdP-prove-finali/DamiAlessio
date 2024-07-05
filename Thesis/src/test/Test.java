package test;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.math.*;

public class Test {

    static class Point {
        int x, y;
        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    static class DistanceSeedPair implements Comparable<DistanceSeedPair> {
        double distance;
        Point seed;
        DistanceSeedPair(double distance, Point seed) {
            this.distance = distance;
            this.seed = seed;
        }
        public int compareTo(DistanceSeedPair other) {
            return Double.compare(this.distance, other.distance);
        }
    }

    public static void main(String[] args) throws IOException {
        int width = 800, height = 800;
        int k = 5;  // Number of closest seeds to consider
        double cutoffBright = 0.1; // Brightness cutoff threshold
        double cutoffDark = 0.9;   // Darkness cutoff threshold
        boolean invertVoronoi = true; // Set to true to invert Voronoi values

        // Step 1: Generate random seed points
        Random rand = new Random();
        int numSeeds = rand.nextInt(1) + 30; // Random number of seeds between 5 and 25
        System.out.print(numSeeds + "\n");
        List<Point> seeds = new ArrayList<>();    

        for (int i = 0; i < numSeeds; i++) {
            int x = rand.nextInt(width);
            int y = rand.nextInt(height);
            seeds.add(new Point(x, y));
        }

        // Step 2: Create arrays to store the distance values for each pixel
        double[][] distanceValues = new double[width][height];
        double[][] blendedDistanceValues = new double[width][height];
        //int[][] closestSeedIndex = new int[width][height];

        // Step 3: Calculate distances and initialize blended distances
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                PriorityQueue<DistanceSeedPair> pq = new PriorityQueue<>();
                for (Point seed : seeds) {
                    double dist = Math.hypot(i - seed.x, j - seed.y);
                    pq.add(new DistanceSeedPair(dist, seed));
                }

                // Find the closest seed and assign its index
                DistanceSeedPair closestPair = pq.peek();
                //closestSeedIndex[i][j] = seeds.indexOf(closestPair.seed);
                distanceValues[i][j] = closestPair.distance;

                // Calculate blended distances for border pixels
                double weightedSum = 0;
                double weightSum = 0;
                for (int count = 0; count < k && !pq.isEmpty(); count++) {
                    DistanceSeedPair pair = pq.poll();
                    double weight = 1.0 / (pair.distance + 1e-10); // Inverse distance weighting
                    weightedSum += pair.distance * weight;
                    weightSum += weight;
                }
                blendedDistanceValues[i][j] = weightedSum / weightSum;
            }
        }

        // Step 4: Normalize the blended distance values to [0, 1]
        double maxDistance = Double.MIN_VALUE;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (blendedDistanceValues[i][j] > maxDistance) {
                    maxDistance = blendedDistanceValues[i][j];
                }
            }
        }
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                blendedDistanceValues[i][j] /= maxDistance;
            }
        }

        // Step 5: Apply cutoff functions to blended distance values
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                blendedDistanceValues[i][j] = applyCutoff(blendedDistanceValues[i][j], cutoffBright, cutoffDark);
            }
        }

        // Step 6: Generate the Voronoi images based on blended distance values
        BufferedImage voronoiImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                double value = blendedDistanceValues[i][j];
                if (invertVoronoi) {
                    value = 1.0 - value; // Invert the value if the option is set
                }
                int colorValue = (int) (value * 255);
                Color color = new Color(colorValue, colorValue, colorValue);
                voronoiImage.setRGB(i, j, color.getRGB());
            }
        }

     // Draw seeds in a distinguishable color (white) on the images
        for (Point seed : seeds) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    int nx = seed.x + dx;
                    int ny = seed.y + dy;
                    if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                        voronoiImage.setRGB(nx, ny, Color.RED.getRGB());
                    }
                }
            }
        }


        // Display the Voronoi images
        displayImage(voronoiImage, "Voronoi Diagram with Blended Distances");

        // Save the Voronoi images
        ImageIO.write(voronoiImage, "png", new File("voronoi_blended.png"));

        // Step 8: Generate Perlin Noise Image
        BufferedImage perlinNoiseImage = generatePerlinNoiseImage(width, height, false);
        displayImage(perlinNoiseImage, "Perlin Noise Grayscale");
        ImageIO.write(perlinNoiseImage, "png", new File("perlin_noise_grayscale.png"));

        // Step 9: Generate Colored Perlin Noise Image
        BufferedImage perlinNoiseColorImage = generatePerlinNoiseImage(width, height, true);
        displayImage(perlinNoiseColorImage, "Perlin Noise Color");
        ImageIO.write(perlinNoiseColorImage, "png", new File("perlin_noise_color.png"));

        // Step 10: Interpolate Perlin Noise with Voronoi Values (Grayscale)
        BufferedImage interpolatedGrayscaleImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                double perlinValue = (new Color(perlinNoiseImage.getRGB(i, j)).getRed()) / 255.0;
                double voronoiValue = blendedDistanceValues[i][j];
                if (invertVoronoi) {
                    voronoiValue = 1.0 - voronoiValue; // Invert the value if the option is set
                }
                double interpolatedValue = lerp(0.5, perlinValue, voronoiValue); // Correct interpolation order
                interpolatedValue = enhanceContrast(interpolatedValue); // Enhance contrast
                int colorValue = (int) (interpolatedValue * 255);
                Color color = new Color(colorValue, colorValue, colorValue);
                interpolatedGrayscaleImage.setRGB(i, j, color.getRGB());
            }
        }

        // Display and save the interpolated grayscale image
        displayImage(interpolatedGrayscaleImage, "Interpolated Grayscale Image");
        ImageIO.write(interpolatedGrayscaleImage, "png", new File("interpolated_grayscale_image.png"));

        // Step 11: Interpolate Perlin Noise with Voronoi Values (Color)
        BufferedImage interpolatedColorImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                double perlinValue = (new Color(perlinNoiseImage.getRGB(i, j)).getRed()) / 255.0;
                double voronoiValue = blendedDistanceValues[i][j];
                if (invertVoronoi) {
                    voronoiValue = 1.0 - voronoiValue; // Invert the value if the option is set
                }
                double interpolatedValue = lerp(0.5, perlinValue, voronoiValue); // Correct interpolation order
                interpolatedValue = enhanceContrast(interpolatedValue); // Enhance contrast
                Color color = mapGrayScaleToColor(interpolatedValue);
                interpolatedColorImage.setRGB(i, j, color.getRGB());
            }
        }

        // Display and save the interpolated color image
        displayImage(interpolatedColorImage, "Interpolated Color Image");
        ImageIO.write(interpolatedColorImage, "png", new File("interpolated_color_image.png"));

        System.out.println("Images saved as voronoi_blended.png, perlin_noise_grayscale.png, perlin_noise_color.png, interpolated_grayscale_image.png, and interpolated_color_image.png");
    }

    private static double applyCutoff(double value, double brightCutoff, double darkCutoff) {
        if (value < brightCutoff) {
            return 0; // Bright cutoff
        } else         if (value > darkCutoff) {
            return 1; // Dark cutoff
        }
        // Linearly interpolate between cutoffs
        return (value - brightCutoff) / (darkCutoff - brightCutoff);
    }

    private static void displayImage(BufferedImage image, String title) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(image.getWidth(), image.getHeight());
        JLabel label = new JLabel(new ImageIcon(image));
        frame.add(label);
        frame.pack();
        frame.setVisible(true);
    }

    private static BufferedImage generatePerlinNoiseImage(int width, int height, boolean colored) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        // Define Perlin noise parameters
        double amplitude = 1; //default 1
        double frequency = 10.0; // Adjust this to control the frequency of the noise //default 10
        int octaves = 8; // Number of layers of noise //default 4
        double persistence = 0.5; // Controls the decrease in amplitude for each octave //default 0.5

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                double x = (double) i / width;
                double y = (double) j / height;
                double noiseValue = 0.0;
                double amp = amplitude;
                double freq = frequency;
                
                // Generate noise for each octave
                for (int o = 0; o < octaves; o++) {
                    noiseValue += ImprovedNoise2D.noise(x * freq, y * freq) * amp;
                    amp *= persistence;
                    freq *= 2;
                }
                
                if (colored) {
                    // Map the noise value to colors
                    Color color = mapGrayScaleToColor((noiseValue + 1) / 2);
                    image.setRGB(i, j, color.getRGB());
                } else {
                    // Normalize the noise value to the range [0, 255]
                    int colorValue = (int) ((noiseValue + 1) * 127.5);
                    colorValue = Math.min(255, Math.max(0, colorValue)); // Clamp value to [0, 255]
                    Color color = new Color(colorValue, colorValue, colorValue);
                    image.setRGB(i, j, color.getRGB());
                }
            }
        }
        return image;
    }

    private static double enhanceContrast(double value) {
        // Apply a simple contrast enhancement function
        return Math.pow(value, 1.5); // Example: Gamma correction with gamma = 1.5
    }

    private static Color mapGrayScaleToColor(double value) {
        if (value < 0.20) {
            return new Color(0, 0, 139); // Deep Blue for deep water
        } else if (value < 0.40) {
            return new Color(173, 216, 230); // Light Blue for shallow water
        } else if (value < 0.60) {
            return new Color(34, 139, 34); // Green for plains
        } else if (value < 0.75) {
            return new Color(139, 69, 19); // Brown for hills
        } else {
            return new Color(255, 255, 255); // White for mountains
        }
    }

    static double lerp(double t, double a, double b) {
        return a + t * (b - a);
    }

    static final class ImprovedNoise2D {
        static final int p[] = new int[512], permutation[] = { 151,160,137,91,90,15,
        131,13,201,95,96,53,194,233,7,225,140,36,103,30,69,142,8,99,37,240,21,10,23,
        190, 6,148,247,120,234,75,0,26,197,62,94,252,219,203,117,35,11,32,57,177,33,
        88,237,149,56,87,174,20,125,136,171,168, 68,175,74,165,71,134,139,48,27,166,
        77,146,158,231,83,111,229,122,60,211,133,230,220,105,92,41,55,46,245,40,244,
        102,143,54, 65,25,63,161, 1,216,80,73,209,76,132,187,208, 89,18,169,200,196,
        135,130,116,188,159,86,164,100,109,198,173,186, 3,64,52,217,226,250,124,123,
        5,202,38,147,118,126,255,82,85,212,207,206,59,227,47,16,58,17,182,189,28,42,
        223,183,170,213,119,248,152, 2,44,154,163, 70,221,153,101,155,167, 43,172,9,
        129,22,39,253, 19,98,108,110,79,113,224,232,178,185, 112,104,218,246,97,228,
        251,34,242,193,238,210,144,12,191,179,162,241, 81,51,145,235,249,14,239,107,
        49,192,214, 31,181,199,106,157,184, 84,204,176,115,121,50,45,127, 4,150,254,
        138,236,205,93,222,114,67,29,24,72,243,141,128,195,78,66,215,61,156,180
        };

        static {
            for (int i = 0; i < 256; i++) {
                p[256 + i] = p[i] = permutation[i];
            }
        }

        static double fade(double t) {
            return t * t * t * (t * (t * 6 - 15) + 10);
        }

        static double lerp(double t, double a, double b) {
            return a + t * (b - a);
        }

        static double grad(int hash, double x, double y) {
            int h = hash & 15;        // CONVERT LO 4 BITS OF HASH CODE INTO 12 GRADIENT DIRECTIONS.
            double u = h < 8 ? x : y; // GRADIENT VECTORS FROM PERMUTATION TABLE.
            double v = h < 4 ? y : h == 12 || h == 14 ? x : 0; // u=x, v=y, OR u=y, v=x DEPENDING ON HASH VALUE.
            return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
        }

        public static double noise(double x, double y) {
            int X = (int) Math.floor(x) & 255; // FIND UNIT CUBE THAT
            int Y = (int) Math.floor(y) & 255; // CONTAINS POINT.
            x -= Math.floor(x); // FIND RELATIVE X,Y,Z
            y -= Math.floor(y); // OF POINT IN CUBE.
            double u = fade(x); // COMPUTE FADE CURVES
            double v = fade(y); // FOR EACH OF X,Y,Z.
            int A = p[X] + Y, AA = p[A], AB = p[A + 1], // HASH COORDINATES OF
                    B = p[X + 1] + Y, BA = p[B], BB = p[B + 1]; // THE 8 CUBE CORNERS,

            return lerp(v, lerp(u, grad(p[AA], x, y), // AND ADD
                    grad(p[BA], x - 1, y)), // BLENDED
                    lerp(u, grad(p[AB], x, y - 1), // RESULTS
                            grad(p[BB], x - 1, y - 1))); // FROM 8 CORNERS.
        }
    }
}


