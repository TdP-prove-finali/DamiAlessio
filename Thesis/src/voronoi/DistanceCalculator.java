package voronoi;

import java.util.List;
import java.util.PriorityQueue;

public class DistanceCalculator {
	private Voronoi voronoi;
    private int k;	//quanti valora da fare media
    double cutoffBright = 0.9; // Brightness cutoff threshold
    double cutoffDark = 0.1;   // Darkness cutoff threshold

	public DistanceCalculator(Voronoi voronoi, int k) {
		this.voronoi = voronoi;
        this.k = k;
    }

    public double[][] calculateBlendedDistances() {
    	int width = voronoi.getWidth();
    	int height = voronoi.getHeight();
    	List<Point> seeds = voronoi.getSeeds();
    	
        double[][] blendedDistanceValues = new double[width][height]; //distanza media pesata
        //int[][] closestSeedIndex = new int[width][height];	//indice seed più vicino per pixel [i][j]
        double[][] distanceValues = new double[width][height];	

        for (int i = 0; i < width; i++) { //ciclo per tutti i pixel dell'immagine
            for (int j = 0; j < height; j++) {
                PriorityQueue<DistanceSeedPair> pq = new PriorityQueue<>();
                for (Point seed : seeds) {
                    double dist = Math.hypot(i - seed.getX(), j - seed.getY()); //distanza euclidea
                    pq.add(new DistanceSeedPair(dist, seed));
                }

                DistanceSeedPair closestPair = pq.peek(); //prendo primo elemento, ovvero nodo più vicino
                //closestSeedIndex[i][j] = seeds.indexOf(closestPair.getSeed());
                distanceValues[i][j] = closestPair.getDistance();

                double weightedSum = 0; //componente somma pesata
                double weightSum = 0;	//peso distanza attuale
                for (int count = 0; count < k && !pq.isEmpty(); count++) {
                    DistanceSeedPair pair = pq.poll();	//prendo (e rimuovo da priority queue) nodo più vicino
                    double weight = 1.0 / (pair.getDistance() + 1e-10); //evito divisione 0
                    weightedSum += pair.getDistance() * weight;	//aggiungo distanza pesata a somma pesata
                    weightSum += weight; //aggiungo peso attuale a totale
                }
                blendedDistanceValues[i][j] = weightedSum / weightSum; //divido somma pesata totale per peso totale
            }
        }        
        return blendedDistanceValues;
    }

    public double[][] normalizeDistances(double[][] blendedDistanceValues) { //normalizza ciascuna distanza in [0,1]
    	int width = voronoi.getWidth();
    	int height = voronoi.getHeight();
        double maxDistance = Double.MIN_VALUE;
        
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (blendedDistanceValues[i][j] > maxDistance) {
                    maxDistance = blendedDistanceValues[i][j]; //distanza massima nel diagramma
                }
            }
        }
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                blendedDistanceValues[i][j] /= maxDistance; //ogni distanza divisa per max distanza
            }
        }
        return blendedDistanceValues;
    }
    
    public static double applyCutOff(double value, double brightCutoff, double darkCutoff) {
        if (value < brightCutoff) {
            return 0; // Bright cutoff
        } else         if (value > darkCutoff) {
            return 1; // Dark cutoff
        }
        // Linearly interpolate between cutoffs
        return (value - brightCutoff) / (darkCutoff - brightCutoff);
    }
    
    public double[][] cutOffBlended(double[][] blendedDistanceValues){
    	int width = voronoi.getWidth();
    	int height = voronoi.getHeight();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                blendedDistanceValues[i][j] = applyCutOff(blendedDistanceValues[i][j], cutoffBright, cutoffDark);
            }
        }
        return blendedDistanceValues;
    }

	public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}

	public Voronoi getVoronoi() {
		return voronoi;
	}

	public void setVoronoi(Voronoi voronoi) {
		this.voronoi = voronoi;
	}
	
	
}
