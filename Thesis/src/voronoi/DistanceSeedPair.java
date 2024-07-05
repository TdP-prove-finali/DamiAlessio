package voronoi;

public class DistanceSeedPair implements Comparable<DistanceSeedPair> {
    private double distance;
	private Point seed;
	
    public DistanceSeedPair(double distance, Point seed) {
        this.setDistance(distance);
        this.seed = seed; 
    }
    public int compareTo(DistanceSeedPair other) {
        return Double.compare(this.getDistance(), other.getDistance());
    }
	public Point getSeed() {
		return seed;
	}
	public void setSeed(Point seed) {
		this.seed = seed;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
}