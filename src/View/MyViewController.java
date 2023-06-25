package View;

import Model.MyModel;
import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

public class MyViewController implements IView, Observer {
//    @FXML
//    MenuItem saveMenuItem;
//    @FXML
//    MenuItem loadMenuItem;
    @FXML
    MazeDisplayer mazeDisplayer;
    @FXML
    TextField columnsTextField;
    @FXML
    TextField rowsTextField;
    @FXML
    ImageView imageView1, imageView2, imageView3;
    @FXML
    RadioButton radioButton1, radioButton2, radioButton3;
    private MyViewModel viewModel;
    private Maze maze;
    private MediaPlayer mediaPlayer;
    private boolean showOnce;
    public enum CurrentPlayer{Spaceship1, Spaceship2, Rocket}
    private CurrentPlayer currentPlayer;


    public MyViewController() {
        viewModel = new MyViewModel();
        viewModel.addObserver(this);
        currentPlayer = CurrentPlayer.Spaceship1;
    }

    public void initialize()
    {
        try {imageView1.setImage(new Image(new FileInputStream("resources/Images/PlayerSpaceship.png")));
            imageView2.setImage(new Image(new FileInputStream("resources/Images/PlayerRocketRight.png")));
            imageView3.setImage(new Image(new FileInputStream("resources/Images/PlayerSpaceship2Right.png")));}
        catch (FileNotFoundException e) {throw new RuntimeException(e);}
        radioButton1.setOnAction(this::setSpaceshipPlayer);
        radioButton2.setOnAction(this::setRocketPlayer);
        radioButton3.setOnAction(this::setSpaceshipPlayer2);
        radioButton1.setSelected(true);
    }

    public void showPropertiesScene(ActionEvent event) throws IOException {
        FXMLLoader propertiesLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("Properties.fxml")));
        Parent root = propertiesLoader.load();
        Stage stage = new Stage();
        stage.setTitle("Properties");
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void showHelpScene(ActionEvent event) throws IOException {
        try
        {
            FXMLLoader helpLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("Help.fxml")));
            Parent root = helpLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Help");
            Scene scene = new Scene(root);
//            scene.getStylesheets().add("box.css");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (Exception e) {
            System.out.println("Error Help.fxml not found");
        }
    }

    public void showAboutScene(ActionEvent event) {
        try
        {
            FXMLLoader helpLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("About.fxml")));
            Parent root = helpLoader.load();
            Stage stage = new Stage();
            stage.setTitle("About");
            Scene scene = new Scene(root);
//            scene.getStylesheets().add("box.css");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (Exception e) {
            System.out.println("Error About.fxml not found");
        }
    }

    @Override
    public void generateMaze(ActionEvent event)
    {
        int rows;
        try
        {
            rows = Integer.parseInt(String.valueOf(rowsTextField.getText()));
            if(rows <= 0) throw new NumberFormatException();
        }
        catch (NumberFormatException exception)
        {
            showAlert("error", "Invalid Input", "Rows value must be positive integer");
            return;
        }
        int cols;
        try
        {
            cols = Integer.parseInt(String.valueOf(rowsTextField.getText()));
            if(cols <= 0) throw new NumberFormatException();
        }
        catch (NumberFormatException exception)
        {
            showAlert("error", "Invalid Input", "Columns value must be positive integer");
            return;
        }
        showOnce = false;
        maze = viewModel.generateMaze(rows, cols);
        displayMaze(maze);
        mazeDisplayer.requestFocus();
    }

    @Override
    public void generateMazeSolution()
    {
        if(maze == null)
        {
            showAlert("error", "Solution Error", "First you will need to generate maze");
            return;
        }
        Solution solution = viewModel.generateSolution();
        displayMazeSolution(solution);
    }

    @Override
    public void displayMaze(Maze maze) {
        mazeDisplayer.setZoomFactor(1);
        mazeDisplayer.setMaze(maze);
        mazeDisplayer.hideSolution();
        mazeDisplayer.setCharacterPosition(0, 0);
        playMusic(0);
    }

    @Override
    public void displayMazeSolution(Solution solution) {
        mazeDisplayer.setSolution(solution);
        mazeDisplayer.showSolution();
        mazeDisplayer.drawMaze();
    }

    @Override
    public void newMaze(ActionEvent event)
    {
        maze = null;
        rowsTextField.setText("");
        columnsTextField.setText("");
        mazeDisplayer.clearMazeDisplayer();
    }

    @Override
    public void saveMaze(ActionEvent event)
    {
        if(maze == null)
        {
            showAlert("ERROR", "Maze Error", "There is not maze to save");
            return;
        }
        FileChooser fc = new FileChooser();
        File filePath = new File("./Mazes/");
        boolean res = true;
        if (!filePath.exists())
            res = filePath.mkdir();
        if (res) {
            fc.setTitle("Saving maze");
            fc.setInitialFileName("Maze " + maze.getRows() + "X" + maze.getColumns());
            fc.setInitialDirectory(filePath);

            FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Maze Files (*.maze)", "*.maze");
            fc.getExtensionFilters().add(extensionFilter);

            File file = fc.showSaveDialog(mazeDisplayer.getScene().getWindow());
            if (file != null)
                viewModel.saveMaze(file);
        }
    }

    @Override
    public void loadMaze(ActionEvent event)
    {
        FileChooser fc = new FileChooser();
        fc.setTitle("Loading maze");
        File filePath = new File("./Mazes/");
        boolean res = true;
        if (!filePath.exists())
            res = filePath.mkdir();
        if (res) {
            fc.setInitialDirectory(filePath);
            FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Maze Files (*.maze)", "*.maze");
            fc.getExtensionFilters().add(extensionFilter);
            File file = fc.showOpenDialog(mazeDisplayer.getScene().getWindow());
            if (file != null && file.exists() && !file.isDirectory()) {
                Maze tempMaze = viewModel.loadMaze(file);
                if (tempMaze != null) {
                    maze = tempMaze;
                    mazeDisplayer.clearMazeDisplayer();
                    rowsTextField.setText(String.valueOf(maze.getRows()));
                    columnsTextField.setText(String.valueOf(maze.getColumns()));
                    displayMaze(maze);
                }
            }
        }
    }

    private void showAlert(String alertType, String alertTitle, String alertMessage)
    {
        Alert alert = null;
        switch (alertType.toUpperCase())
        {
            case "ERROR":
            {
                alert = new Alert(Alert.AlertType.ERROR);
                break;
            }
            case "WARNING":
            {
                alert = new Alert(Alert.AlertType.WARNING);
                break;
            }
            case "INFORMATION":
            {
                alert = new Alert(Alert.AlertType.INFORMATION);
                break;
            }
            case "CONFIRMATION":
            {
                alert = new Alert(Alert.AlertType.CONFIRMATION);
                break;
            }
        }
        assert alert != null;
        alert.setTitle(alertTitle);
        alert.setContentText(alertMessage);
        alert.show();
    }

    public void keyPressed(KeyEvent keyEvent) {
        if (maze != null){
            viewModel.move(keyEvent.getCode());
            keyEvent.consume();
        }
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        mazeDisplayer.requestFocus();
        mouseEvent.consume();
    }

    public void setSpaceshipPlayer(ActionEvent event)
    {
        currentPlayer = CurrentPlayer.Spaceship1;
        mazeDisplayer.setImageFileNamePlayer("resources/Images/PlayerSpaceship.png");
        mazeDisplayer.drawMaze();
        radioButton1.setSelected(true);
        radioButton2.setSelected(false);
        radioButton3.setSelected(false);
        mazeDisplayer.requestFocus();
    }
    public void setSpaceshipPlayer2(ActionEvent event)
    {
        currentPlayer = CurrentPlayer.Spaceship2;
        mazeDisplayer.setImageFileNamePlayer("resources/Images/PlayerSpaceship2Up.png");
        mazeDisplayer.drawMaze();
        radioButton1.setSelected(false);
        radioButton2.setSelected(false);
        radioButton3.setSelected(true);
        mazeDisplayer.requestFocus();
    }
    public void setRocketPlayer(ActionEvent event)
    {
        currentPlayer = CurrentPlayer.Rocket;
        mazeDisplayer.setImageFileNamePlayer("resources/Images/PlayerRocketUp.png");
        mazeDisplayer.drawMaze();
        radioButton1.setSelected(false);
        radioButton2.setSelected(true);
        radioButton3.setSelected(false);
        mazeDisplayer.requestFocus();
    }

    public void setCharacterImageRotation(MyModel.Direction lastDirection){
        if (currentPlayer == CurrentPlayer.Spaceship1) return;
        switch (lastDirection){
            case UP:
                if ((currentPlayer == CurrentPlayer.Spaceship2)) {
                    mazeDisplayer.setImageFileNamePlayer("resources/Images/PlayerSpaceship2Up.png");
                }
                if ((currentPlayer == CurrentPlayer.Spaceship1)) {
                    mazeDisplayer.setImageFileNamePlayer("resources/Images//PlayerSpaceship.png");
                } else {
                mazeDisplayer.setImageFileNamePlayer("resources/Images/PlayerRocketUp.png");
                }
                break;
            case DOWN:
                if ((currentPlayer == CurrentPlayer.Spaceship2)) {
                    mazeDisplayer.setImageFileNamePlayer("resources/Images/PlayerSpaceship2Down.png");
                }
                if ((currentPlayer == CurrentPlayer.Spaceship1)) {
                    mazeDisplayer.setImageFileNamePlayer("resources/Images//PlayerSpaceship.png");
                } else {
                    mazeDisplayer.setImageFileNamePlayer("resources/Images/PlayerRocketDown.png");
                }
                break;
            case RIGHT:
                if ((currentPlayer == CurrentPlayer.Spaceship2)) {
                    mazeDisplayer.setImageFileNamePlayer("resources/Images/PlayerSpaceship2Right.png");
                }
                if ((currentPlayer == CurrentPlayer.Spaceship1)) {
                    mazeDisplayer.setImageFileNamePlayer("resources/Images//PlayerSpaceship.png");
                } else {
                    mazeDisplayer.setImageFileNamePlayer("resources/Images/PlayerRocketRight.png");
                }
                break;
            case LEFT:
                if ((currentPlayer == CurrentPlayer.Spaceship2)) {
                    mazeDisplayer.setImageFileNamePlayer("resources/Images/PlayerSpaceship2Left.png");
                }
                if ((currentPlayer == CurrentPlayer.Spaceship1)) {
                    mazeDisplayer.setImageFileNamePlayer("resources/Images//PlayerSpaceship.png");
                } else {
                    mazeDisplayer.setImageFileNamePlayer("resources/Images/PlayerRocketLeft.png");
                }
                break;
        }
    }


    @Override
    public void update(Observable o, Object arg) {
        if(o == viewModel)
        {
            setCharacterImageRotation(viewModel.getLastDirection());
            mazeDisplayer.setCharacterPosition(viewModel.getCurrRow(), viewModel.getCurrColumn());
            if(viewModel.isGameOver() && !showOnce) arrivedToGoalPosition();
        }
    }

    public void mouseMoved(MouseEvent mouseEvent) {
        if (maze != null){
            viewModel.move(mouseEvent, mazeDisplayer.getCellWidth(), mazeDisplayer.getCellHeight());
            mouseEvent.consume();
        }
    }

    public void arrivedToGoalPosition()
    {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        alert.getDialogPane().lookupButton(ButtonType.CLOSE).setStyle("-fx-font: 24 david;");
        alert.setContentText("The Astronaut is free!");
        alert.getDialogPane().setStyle("-fx-font: 48 david;");
        try {
            Image image = new Image(new FileInputStream("resources/images/EndGamePicture.jpg"));
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(300);
            imageView.setFitWidth(300);
            alert.setGraphic(imageView);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        playMusic(1);
        alert.show();
        alert.getDialogPane().lookupButton(ButtonType.CLOSE).addEventFilter(ActionEvent.ACTION, event -> {
            if (mediaPlayer != null) mediaPlayer.stop();
        });
        showOnce = true;
    }

    public void playMusic(int musicNumber)
    {
        String musicPath = null;
        if(mediaPlayer != null) mediaPlayer.stop();
        switch (musicNumber)
        {
            case 0: // Background Sound
            {
                musicPath = "resources/Songs/Background.mp3";
                break;
            }
            case 1: // Game Over Sound
            {
                musicPath = "resources/Songs/GameOver.mp3";
                break;
            }
            case 2: // Show Solution Sound
            {
                musicPath = "resources/Songs/ShowSolution.mp3";
                break;
            }
        }
        if (musicPath != null)
        {
            Media sound = new Media(new File(musicPath).toURI().toASCIIString());
            mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
        }

    }


    public void exitProgram(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }
}
