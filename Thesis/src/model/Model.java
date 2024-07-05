package model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import dao.VoronoiDAO;
import noise.PerlinImage;
import voronoi.DistanceCalculator;
import voronoi.Point;
import voronoi.Voronoi;
import voronoi.VoronoiImage;

public class Model {
	
    int width = 1000;
	int height = 1000;
    Random random = new Random();
    int numSeeds = 30;
    int k = 5;
    double amplitude = 1.0;
    double frequency = 10.0;
    int octaves = 8;
    double persistence = 0.5;
    double tValue = 0.5;
    boolean invertVoronoi = true;

    Voronoi voronoi ;
    List<Point> seeds ;
    double[][] blendedDistanceValues;
    PerlinImage perlinNoiseGenerator;
    
    BufferedImage voronoiImage;
    BufferedImage perlinGrayscale;
    BufferedImage perlinColor;
    BufferedImage interpolatedGrayscale;
    BufferedImage interpolatedColor;
    
    
    public Model() {
    	
    }
    
    public void generateVoronoi() {
        DistanceCalculator distanceCalculator = new DistanceCalculator(voronoi, k);
        blendedDistanceValues = distanceCalculator.calculateBlendedDistances();
        blendedDistanceValues = distanceCalculator.normalizeDistances(blendedDistanceValues);
        VoronoiImage voronoiImageGenerator = new VoronoiImage(voronoi);
        voronoiImage = voronoiImageGenerator.generateVoronoiImage(blendedDistanceValues, true);
    }
    
    public void generatePerlin() {
        perlinNoiseGenerator = new PerlinImage(width, height, amplitude, frequency, octaves, persistence);
        perlinGrayscale = perlinNoiseGenerator.generatePerlinNoiseImage(false);
        perlinColor = perlinNoiseGenerator.generatePerlinNoiseImage(true);
    }
    
    public void generateInterpolated() {
    	interpolatedGrayscale = perlinNoiseGenerator.generateInterpolated(perlinGrayscale, blendedDistanceValues, invertVoronoi, false, tValue);
    	interpolatedColor = perlinNoiseGenerator.generateInterpolated(perlinGrayscale, blendedDistanceValues, invertVoronoi, true, tValue);
    }
    
    public void getDatabase() { //get random database with numSeeds number of seeds
    	VoronoiDAO voronoiDAO = new VoronoiDAO();
    	List<Voronoi> voronoiList = voronoiDAO.getDiagramsByNumSeeds(numSeeds);
    	voronoi = voronoiList.get(random.nextInt(10));
    }
    
    public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Random getRandom() {
		return random;
	}

	public void setRandom(Random random) {
		this.random = random;
	}

	public int getNumSeeds() {
		return numSeeds;
	}

	public void setNumSeeds(int numSeeds) {
		this.numSeeds = numSeeds;
	}

	public double getAmplitude() {
		return amplitude;
	}

	public void setAmplitude(double amplitude) {
		this.amplitude = amplitude;
	}

	public double getFrequency() {
		return frequency;
	}

	public void setFrequency(double frequency) {
		this.frequency = frequency;
	}

	public int getOctaves() {
		return octaves;
	}

	public void setOctaves(int octaves) {
		this.octaves = octaves;
	}

	public double getPersistence() {
		return persistence;
	}

	public void setPersistence(double persistence) {
		this.persistence = persistence;
	}

	public boolean isInvertVoronoi() {
		return invertVoronoi;
	}

	public void setInvertVoronoi(boolean invertVoronoi) {
		this.invertVoronoi = invertVoronoi;
	}

	public Voronoi getVoronoi() {
		return voronoi;
	}

	public void setVoronoi(Voronoi voronoi) {
		this.voronoi = voronoi;
	}

	public List<Point> getSeeds() {
		return seeds;
	}

	public void setSeeds(List<Point> seeds) {
		this.seeds = seeds;
	}

	public BufferedImage getVoronoiImage() {
		return voronoiImage;
	}

	public void setVoronoiImage(BufferedImage voronoiImage) {
		this.voronoiImage = voronoiImage;
	}

	public BufferedImage getPerlinGrayscale() {
		return perlinGrayscale;
	}

	public void setPerlinGrayscale(BufferedImage perlinGrayscale) {
		this.perlinGrayscale = perlinGrayscale;
	}

	public BufferedImage getPerlinColor() {
		return perlinColor;
	}

	public void setPerlinColor(BufferedImage perlinColor) {
		this.perlinColor = perlinColor;
	}

	public BufferedImage getInterpolatedGrayscale() {
		return interpolatedGrayscale;
	}

	public void setInterpolatedGrayscale(BufferedImage interpolatedGrayscale) {
		this.interpolatedGrayscale = interpolatedGrayscale;
	}

	public BufferedImage getInterpolatedColor() {
		return interpolatedColor;
	}

	public void setInterpolatedColor(BufferedImage interpolatedColor) {
		this.interpolatedColor = interpolatedColor;
	}

	public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}
	
	

	public double gettValue() {
		return tValue;
	}

	public void settValue(double tValue) {
		this.tValue = tValue;
	}

	public double[][] getBlendedDistanceValues() {
		return blendedDistanceValues;
	}

	public void setBlendedDistanceValues(double[][] blendedDistanceValues) {
		this.blendedDistanceValues = blendedDistanceValues;
	}
	
	public void generateRandomVoronoi() {
		voronoi = new Voronoi(width, height, numSeeds);
	}
	
	public void reset() {
	    this.width = 1000;
	    this.height = 1000;
	    this.random = new Random();
	    this.numSeeds = 30;
	    this.k = 5;
	    this.amplitude = 1.0;
	    this.frequency = 10.0;
	    this.octaves = 8;
	    this.persistence = 0.5;
	    this.tValue = 0.5;
	    this.invertVoronoi = true;

	    this.voronoi = null ;
	    this.seeds = null;
	    this.blendedDistanceValues= new double[width][height];
	    this.perlinNoiseGenerator = null;
	    
	    this.voronoiImage = null;
	    this.perlinGrayscale = null;
	    this.perlinColor = null;
	    this.interpolatedGrayscale = null;
	    this.interpolatedColor = null;
	}

	public void setPerlinParameters(double amp, double freq, int oct, double pers) {
		// TODO Auto-generated method stub
		this.amplitude = amp;
		this.frequency = freq;
		this.octaves = oct;
		this.persistence = pers;
	}

	public void addSeed(int x, int y) {
		// TODO Auto-generated method stub
		seeds.add(new Point(x,y));
	}

	public void saveImages() {
        saveImage(voronoiImage, "voronoi_image.png");
        saveImage(perlinGrayscale, "Perlin_grayscale_image.png");
        saveImage(perlinColor, "Perlin_color_image.png");
        saveImage(interpolatedGrayscale, "interpolated_grayscale_image.png");
        saveImage(interpolatedColor, "interpolated_color_image.png");

    }

    private void saveImage(BufferedImage image, String fileName) {
        if (image != null) {
            try {
                File outputfile = new File(fileName);
                ImageIO.write(image, "png", outputfile);
                System.out.println("Image saved: " + fileName);
            } catch (IOException e) {
                System.err.println("Error saving image: " + fileName);
                e.printStackTrace();
            }
        } else {
            System.out.println("No image to save: " + fileName);
        }
    }

}