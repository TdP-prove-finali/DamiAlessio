package main;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import model.Model;
import voronoi.Point;

import java.awt.image.BufferedImage;
import java.util.List;

public class FXMLController {

    @FXML
    private TextField width;
    @FXML
    private TextField height;
    @FXML
    private TextField seedsNumber;
    @FXML
    private Button populateVoronoi;
    @FXML
    private TextField databaseNumber;
    @FXML
    private Button getDatabase;
    @FXML
    private TextField xCoordinate;
    @FXML
    private TextField yCoordinate;
    @FXML
    private Button addSeed;
    @FXML
    private TextField amplitude;
    @FXML
    private TextField frequency;
    @FXML
    private TextField octave;
    @FXML
    private TextField persistence;
    @FXML
    private TextField seedsAverage;
    @FXML
    private Slider interpolationValue;
    @FXML
    private ComboBox<String> selectedImage;
    @FXML
    private Button generateVoronoi;
    @FXML
    private Button generatePerlin;
    @FXML
    private Button interpolate;
    @FXML
    private Button saveImages;
    @FXML
    private Button reset;
    @FXML
    private ImageView imageView;

    
    private Model model;
    private ObservableList<String> imageOptions; // Declare imageOptions as an instance variable


    @FXML
    public void initialize() {
        model = new Model(); // Initialize the model

        // Initialize the ComboBox with an empty list
        imageOptions = FXCollections.observableArrayList();
        selectedImage.setItems(imageOptions);

        // Add a listener to handle selection changes
        selectedImage.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            handleImageSelection(newValue);
        });
    }

    @FXML
    private void handlePopulateVoronoi() {
    	if (width.getText() != "") {
    		int w = Integer.parseInt(width.getText());
    		model.setWidth(w);
    	}
    	if (height.getText() != "") {
    		int h = Integer.parseInt(height.getText());
    		model.setHeight(h);
    	}
    	if (seedsNumber.getText() != "") {
    		int n = Integer.parseInt(seedsNumber.getText());
    		model.setNumSeeds(n);
    	}
    	if 	(seedsAverage.getText()!= "") {
    		int seeds = Integer.parseInt(seedsAverage.getText());
    		model.setK(seeds);
    	}
        model.generateRandomVoronoi();
        model.generateVoronoi();

        // Display the Voronoi diagram
        displayImage(model.getVoronoiImage());
        addImageOption("Voronoi Image");

    }

    @FXML
    private void handleGenerateVoronoi() {
    	if 	(model.getVoronoi()!=null) {
        	model.generateVoronoi();
        	if 	(seedsAverage.getText()!= "") {
        		int seeds = Integer.parseInt(seedsAverage.getText());
        		model.setK(seeds);
        	}
        	// Display the Voronoi diagram
        	displayImage(model.getVoronoiImage());
            addImageOption("Voronoi Image");

    	}
    }
    
    @FXML
    private void handleGetDatabase() {
        if 	(databaseNumber.getText() != "") {
    		int numSeeds = Integer.parseInt(databaseNumber.getText());
        	model.setNumSeeds(numSeeds);
        	if 	(seedsAverage.getText()!= "") {
        		int seeds = Integer.parseInt(seedsAverage.getText());
        		model.setK(seeds);
        	}
            model.getDatabase();
            model.generateVoronoi();
            
            // Display the fetched Voronoi diagram
            displayImage(model.getVoronoiImage());
            addImageOption("Voronoi Image");

        }
    }

    @FXML
    private void handleAddSeed() {
    	if 	(xCoordinate.getText()!= "" && xCoordinate.getText() != "" && model.getVoronoi()!=null) {
        	int x = Integer.parseInt(xCoordinate.getText());
        	int y = Integer.parseInt(yCoordinate.getText());
            model.getVoronoi().getSeeds().add(new Point(x,y));
            model.generateVoronoi();

            // Display the Voronoi diagram
            displayImage(model.getVoronoiImage());
            addImageOption("Voronoi Image");

    	}

        // Clear the input fields after adding the seed
        xCoordinate.clear();
        yCoordinate.clear();
    }

    @FXML
    private void handleGeneratePerlin() {
    	if	(amplitude.getText()!= "") {
        	double amp = Double.parseDouble(amplitude.getText());
        	model.setAmplitude(amp);
    	}
    	if	(frequency.getText()!= "") {
        	double freq = Double.parseDouble(frequency.getText());
        	model.setFrequency(freq);
    	}
    	if 	(octave.getText()!="") {
        	int oct = Integer.parseInt(octave.getText());
        	model.setOctaves(oct);
    	}
    	if 	(persistence.getText()!="") {
        	double pers = Double.parseDouble(persistence.getText());
        	model.setPersistence(pers);
    	}
    	if 	(seedsAverage.getText()!= "") {
    		int seeds = Integer.parseInt(seedsAverage.getText());
    		model.setK(seeds);
    	}
        model.generatePerlin();

        // Display the Perlin noise image
        displayImage(model.getPerlinGrayscale());
        
        addImageOption("Perlin Noise Grayscale Image");
        addImageOption("Perlin Noise Color Image");
    }

    @FXML
    private void handleInterpolate() {
        double interpolationWeight = interpolationValue.getValue();
        model.settValue(interpolationWeight);
        model.generateInterpolated();
    	if 	(seedsAverage.getText()!= "") {
    		int seeds = Integer.parseInt(seedsAverage.getText());
    		model.setK(seeds);
    	}

        // Display the interpolated image
        displayImage(model.getInterpolatedGrayscale());

        addImageOption("Interpolated Grayscale Image");
        addImageOption("Interpolated Color Image");
    }

    @FXML
    private void handleSaveImages() {
        model.saveImages();
    }

    @FXML
    private void handleReset() {
        model.reset();
        // Clear all elements of the UI
        width.clear();
        height.clear();
        seedsNumber.clear();
        databaseNumber.clear();
        xCoordinate.clear();
        yCoordinate.clear();
        amplitude.clear();
        frequency.clear();
        octave.clear();
        persistence.clear();
        seedsAverage.clear();
        interpolationValue.setValue(0);
        selectedImage.getSelectionModel().clearSelection();
        imageView.setImage(null);

        // Clear the ComboBox
        imageOptions.clear();
    }
    
    private void handleImageSelection(String newValue) {
        BufferedImage imageToDisplay = null;

        switch (newValue) {
            case "Voronoi Image":
                imageToDisplay = model.getVoronoiImage();
                break;
            case "Perlin Noise Grayscale Image":
                imageToDisplay = model.getPerlinGrayscale();
                break;
            case "Perlin Noise Color Image":
                imageToDisplay = model.getPerlinColor();
                break;
            case "Interpolated Grayscale Image":
                imageToDisplay = model.getInterpolatedGrayscale();
                break;
            case "Interpolated Color Image":
                imageToDisplay = model.getInterpolatedColor();
                break;
        }

        if (imageToDisplay != null) {
            displayImage(imageToDisplay);
        }
    }
    
    private void displayImage(BufferedImage bufferedImage) {
        Image image = SwingFXUtils.toFXImage(bufferedImage, null);
        imageView.setImage(image);
    }
    
    private void addImageOption(String option) {
        if (!imageOptions.contains(option)) {
            imageOptions.add(option);
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
