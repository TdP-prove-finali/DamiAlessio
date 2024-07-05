package test;

import model.Model;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class TestModel {
	public static void main(String[] args) {
		Model model = new Model();
		model.generateRandomVoronoi();
		model.generateVoronoi();
		model.generatePerlin();
		model.generateInterpolated();
		
        displayImage(model.getVoronoiImage(), "Voronoi Image");
        displayImage(model.getPerlinGrayscale(), "Voronoi Image");
        displayImage(model.getPerlinColor(), "Voronoi Image");
        displayImage(model.getInterpolatedGrayscale(), "Voronoi Image");
        displayImage(model.getInterpolatedColor(), "Voronoi Image");

        
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
