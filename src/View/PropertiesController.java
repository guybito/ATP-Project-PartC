package View;

import Server.Configurations;
import com.sun.javafx.scene.control.Properties;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;

public class PropertiesController {
    @FXML
    Spinner<Integer> threadSpinner;
    @FXML
    Button setEmptyMazeGeneratorButton;
    @FXML
    Button setSimpleMazeGeneratorButton;
    @FXML
    Button setMyMazeGeneratorButton;
    @FXML
    Button setBreadthSearchingAlgorithmButton;
    @FXML
    Button setDFSSearchingAlgorithmButton;
    @FXML
    Button setBestSearchingAlgorithmButton;
    private Button lastPressedGeneratorButton;
    private Button lastPressedAlgorithmButton;


    public void initialize() {
        // Set the range and step size of the spinner
        threadSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, Configurations.getThreadPoolSize()));

        // Listen for value changes
        threadSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            Configurations.setThreadPoolSize(newValue);
        });

        switch (Configurations.getMazeGeneratingAlgorithm()){
            case "EmptyMazeGenerator": setEmptyMazeGenerator(); break;
            case "SimpleMazeGenerator": setSimpleMazeGenerator(); break;
            case "MyMazeGenerator": setMyMazeGenerator(); break;
        }
        switch (Configurations.getMazeSearchingAlgorithm()){
            case "BreadthFirstSearch": setBreadthSearchingAlgorithm(); break;
            case "DepthFirstSearch": setDFSSearchingAlgorithm(); break;
            case "BestFirstSearch": setBestSearchingAlgorithm(); break;
        }
    }

    public void setEmptyMazeGenerator(){
        Configurations.setMazeGeneratingAlgorithm("EmptyMazeGenerator");
        markGeneratorButtons(setEmptyMazeGeneratorButton);
    }
    public void setSimpleMazeGenerator(){
        Configurations.setMazeGeneratingAlgorithm("SimpleMazeGenerator");
        markGeneratorButtons(setSimpleMazeGeneratorButton);
    }
    public void setMyMazeGenerator(){
        Configurations.setMazeGeneratingAlgorithm("MyMazeGenerator");
        markGeneratorButtons(setMyMazeGeneratorButton);
    }
    public void setBreadthSearchingAlgorithm(){
        Configurations.setMazeSearchingAlgorithm("BreadthFirstSearch");
        markAlgorithmButtons(setBreadthSearchingAlgorithmButton);
    }
    public void setDFSSearchingAlgorithm(){
        Configurations.setMazeSearchingAlgorithm("DepthFirstSearch");
        markAlgorithmButtons(setDFSSearchingAlgorithmButton);
    }
    public void setBestSearchingAlgorithm(){
        Configurations.setMazeSearchingAlgorithm("BestFirstSearch");
        markAlgorithmButtons(setBestSearchingAlgorithmButton);
    }
    public void markGeneratorButtons(Button button){
        if(lastPressedGeneratorButton != null){
            lastPressedGeneratorButton.setStyle("");
        }
        lastPressedGeneratorButton = button;
        lastPressedGeneratorButton.setStyle("-fx-background-color: yellow");
    }
    public void markAlgorithmButtons(Button button){
        if(lastPressedAlgorithmButton != null){
            lastPressedAlgorithmButton.setStyle("");
        }
        lastPressedAlgorithmButton = button;
        lastPressedAlgorithmButton.setStyle("-fx-background-color: yellow");
    }
}
