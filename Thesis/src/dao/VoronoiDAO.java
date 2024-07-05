package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import voronoi.Point;
import voronoi.Voronoi;

public class VoronoiDAO {
    private Connection connection;

    public VoronoiDAO() {
        this.connection = DBConnect.getConnection();
    }

    public List<Voronoi> getDiagramsByNumSeeds(int numSeeds) {
        List<Voronoi> voronoiList = new ArrayList<>();
        String sql = "SELECT d.id, d.num_seeds, s.x, s.y " +
                     "FROM Diagrams d " +
                     "JOIN Seeds s ON d.id = s.diagram_id " +
                     "WHERE d.num_seeds = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, numSeeds);
            ResultSet resultSet = statement.executeQuery();

            // Use a map to collect points for each Voronoi diagram by ID
            Map<Integer, List<Point>> voronoiPointsMap = new HashMap<>();

            while (resultSet.next()) {
                int diagramId = resultSet.getInt("id");
                int x = resultSet.getInt("x");
                int y = resultSet.getInt("y");
                Point point = new Point(x, y);

                voronoiPointsMap.computeIfAbsent(diagramId, k -> new ArrayList<>()).add(point);
            }

            // Create Voronoi objects from the collected points
            for (Map.Entry<Integer, List<Point>> entry : voronoiPointsMap.entrySet()) {
                Voronoi voronoi = new Voronoi(1000, 1000, numSeeds, entry.getValue());
                voronoiList.add(voronoi);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return voronoiList;
    }
}
