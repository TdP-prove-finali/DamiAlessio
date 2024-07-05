package dao;

import java.util.List;

import voronoi.Point;
import voronoi.Voronoi;

public class TestDAO {
    public static void main(String[] args) {
        VoronoiDAO voronoiDAO = new VoronoiDAO();
        List<Voronoi> voronoiList = voronoiDAO.getDiagramsByNumSeeds(10);

        for (Voronoi voronoi : voronoiList) {
            System.out.println("Voronoi Diagram with " + voronoi.getNumSeeds() + " seeds:");
            for (Point point : voronoi.getSeeds()) {
                System.out.println("Point: (" + point.getX() + ", " + point.getY() + ")");
            }
        }
    }
}
