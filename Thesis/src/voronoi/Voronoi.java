package voronoi;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Voronoi {

	private int width;	//larghezza immagine
    private int height;	//altezza immagine
    private int numSeeds;	//numero seeds immagine


	private List<Point> seeds;	//lista seeds
    
    public Voronoi(int width, int height, int numSeeds) {
        this.width = width;
        this.height = height;
        this.numSeeds = numSeeds;
        this.seeds = new ArrayList<>();
        generateSeeds();
    }
    
    public Voronoi(int width, int height, int numSeeds, List<Point> list) {
        this.width = width;
        this.height = height;
        this.numSeeds = numSeeds;
        this.seeds = new ArrayList<>();
        this.seeds = list;
    }
    private void generateSeeds() {
        Random rand = new Random();
        for (int i = 0; i < numSeeds; i++) {
            int x = rand.nextInt(width);
            int y = rand.nextInt(height);
            seeds.add(new Point(x, y));
        }
    }
    
    private void generateSeeds(int numSeeds) {
        Random rand = new Random();
        for (int i = 0; i < numSeeds; i++) {
            int x = rand.nextInt(width);
            int y = rand.nextInt(height);
            seeds.add(new Point(x, y));
        }
    }
    
    public List<Point> getSeeds() {
        return seeds;
    }

    public int getNumSeeds() {
        return numSeeds;
    }

    public void setNumSeeds(int numSeeds) {
        this.numSeeds = numSeeds;
        generateSeeds();
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

	public void setSeeds(List<Point> seeds) {
		this.seeds = seeds;
	}

    
}



