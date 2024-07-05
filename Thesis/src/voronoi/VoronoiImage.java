package voronoi;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;

public class VoronoiImage {
    private Voronoi voronoi;

    public VoronoiImage(Voronoi voronoi) {
        this.voronoi = voronoi;
    }

    public BufferedImage generateVoronoiImage(double[][] blendedDistanceValues, boolean inverted) {
    	int width = voronoi.getWidth();
    	int height = voronoi.getHeight();
    	List<Point> seeds = voronoi.getSeeds();
    	
        BufferedImage voronoiImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                double value = blendedDistanceValues[i][j];
                if (inverted) {
                    value = 1.0 - value;
                }
                int colorValue = (int) (value * 255);
                Color color = new Color(colorValue, colorValue, colorValue);
                voronoiImage.setRGB(i, j, color.getRGB());
            }
        }
        for (Point seed : seeds) {	//Colora i seeds nell'immagine
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    int nx = seed.getX() + dx;
                    int ny = seed.getY() + dy;
                    if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                        voronoiImage.setRGB(nx, ny, Color.RED.getRGB());
                    }
                }
            }
        }
        return voronoiImage;
    }

	public Voronoi getVoronoi() {
		return voronoi;
	}

	public void setVoronoi(Voronoi voronoi) {
		this.voronoi = voronoi;
	}
    
    
}
