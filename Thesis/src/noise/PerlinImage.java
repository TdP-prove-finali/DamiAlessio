package noise;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class PerlinImage {
    private int width;
    private int height;
    private double amplitude;
    private double frequency;
    private int octaves;
    private double persistence;

	public PerlinImage(int width, int height, double amplitude, double frequency, int octaves, double persistence) {
        this.width = width;
        this.height = height;
        this.amplitude = amplitude;
        this.frequency = frequency;
        this.octaves = octaves;
        this.persistence = persistence;
    }

    public BufferedImage generatePerlinNoiseImage(boolean colored) {
    	
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                double x = (double) i / width;
                double y = (double) j / height;
                double noiseValue = 0.0;
                double amp = amplitude;
                double freq = frequency;
                
                // Generazione rumore
                for (int o = 0; o < octaves; o++) {
                    noiseValue += Noise.noise(x * freq, y * freq) * amp;
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
    
    /*
    public BufferedImage generatePerlinNoiseImage() {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        //double amplitude = 1.0;
        double freq = frequency;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                double x = (double) i / width;
                double y = (double) j / height;
                double noiseValue = 0.0;
                double amp = amplitude;
                double f = freq;

                for (int o = 0; o < octaves; o++) {
                    noiseValue += Noise.noise(x * f, y * f) * amp;
                    amp *= persistence;
                    f *= 2;
                }

                int colorValue = (int) ((noiseValue + 1) * 127.5);
                colorValue = Math.min(255, Math.max(0, colorValue)); // Limita valori a [0, 255]
                Color color = new Color(colorValue, colorValue, colorValue);
                image.setRGB(i, j, color.getRGB());
            }
        }
        return image;   
    }
    */
    
    public BufferedImage generateInterpolated(BufferedImage perlinNoiseImage, double[][] blendedDistanceValues, boolean invertVoronoi, boolean colored, double tValue) {
    	
        BufferedImage interpolated = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                double perlinValue = (new Color(perlinNoiseImage.getRGB(i, j)).getRed()) / 255.0;
                double voronoiValue = blendedDistanceValues[i][j];
                if (invertVoronoi) {
                    voronoiValue = 1.0 - voronoiValue; // Invert the value if the option is set
                }
                double interpolatedValue = Noise.lerp(tValue, perlinValue, voronoiValue); // Correct interpolation order
                interpolatedValue = enhanceContrast(interpolatedValue); // Enhance contrast
                int colorValue = (int) (interpolatedValue * 255);
                
                if(!colored) {
                Color color = new Color(colorValue, colorValue, colorValue);
                interpolated.setRGB(i, j, color.getRGB());
                } else {
                    Color color = mapGrayScaleToColor(interpolatedValue);
                    interpolated.setRGB(i, j, color.getRGB());
                }
            }
        }

        return interpolated;
    }
    
    private static double enhanceContrast(double value) {
        return Math.pow(value, 1.5); //aumenta contrasto
    }
    
    public  static Color mapGrayScaleToColor(double value) {
        if (value < 0.20) {
            return new Color(0, 0, 139); //	Blu scuro per oceano
        } else if (value < 0.40) {
            return new Color(173, 216, 230); // Celeste per costa
        } else if (value < 0.60) {
            return new Color(34, 139, 34); // Verde per pianura
        } else if (value < 0.75) {
            return new Color(139, 69, 19); // Marrone per colline
        } else {
            return new Color(255, 255, 255); // Bianco per montagne
        }
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

	public double getAmplitude() {
		return amplitude;
	}

	public void setAmplitude(double amplitude) {
		this.amplitude = amplitude;
	}

}