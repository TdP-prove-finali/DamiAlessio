package test;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import noise.PerlinImage;
import voronoi.DistanceCalculator;
import voronoi.Point;
import voronoi.Voronoi;
import voronoi.VoronoiImage;

public class TestClasses {
    public static void main(String[] args) {
        int width = 1000;
        int height = 1000;
        Random random = new Random();
        int numSeeds = random.nextInt(50)+1;
        double amplitude = 1.0;
        double frequency = 10.0;
        int octaves = 8;
        double persistence = 0.5;
        boolean invertVoronoi = true;

        Voronoi voronoi = new Voronoi(width, height, numSeeds);
        List<Point> seeds = voronoi.getSeeds();

        DistanceCalculator distanceCalculator = new DistanceCalculator(voronoi, 3);
        double[][] blendedDistanceValues = distanceCalculator.calculateBlendedDistances();
        blendedDistanceValues = distanceCalculator.normalizeDistances(blendedDistanceValues);
        //distanceCalculator.calculateBlendedDistances();

        VoronoiImage voronoiImageGenerator = new VoronoiImage(voronoi);
        BufferedImage voronoiImage = voronoiImageGenerator.generateVoronoiImage(blendedDistanceValues, true);

        PerlinImage perlinNoiseGenerator = new PerlinImage(width, height, amplitude, frequency, octaves, persistence);
        BufferedImage perlinNoiseImage = perlinNoiseGenerator.generatePerlinNoiseImage(false);
        
        BufferedImage interpolatedImage = perlinNoiseGenerator.generateInterpolated(perlinNoiseImage, blendedDistanceValues, invertVoronoi, true, 0.5);

        // Display images
        displayImage(voronoiImage, "Voronoi Image");
        displayImage(perlinNoiseImage, "Perlin Noise Image");
        displayImage(interpolatedImage, "Interpolated Noise Image");
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
}


