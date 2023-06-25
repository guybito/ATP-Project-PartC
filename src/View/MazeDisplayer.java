package View;

import Model.MyModel;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MazeDisplayer extends Canvas {
    private double zoomFactor;
    private GraphicsContext graphicsContext2D;
    private Maze maze;
    private Solution solution;
    private int characterRow;
    private int characterCol;
    private boolean showSolution;
    private double cellHeight;
    private double cellWidth;
    StringProperty imageFileNameWall = new SimpleStringProperty();
    StringProperty imageFileNameGoal = new SimpleStringProperty();
    StringProperty imageFileNamePlayer = new SimpleStringProperty();
    StringProperty imageFileNameSolution = new SimpleStringProperty();
    StringProperty imageFileNameBackground = new SimpleStringProperty();
    StringProperty imageFileNameStart = new SimpleStringProperty();

    public double getCellHeight() {
        return cellHeight;
    }

    public double getCellWidth() {
        return cellWidth;
    }

    public MazeDisplayer() {
        this.graphicsContext2D = getGraphicsContext2D();
        this.showSolution = false;
        this.characterRow = 0;
        this.characterCol = 0;
        this.zoomFactor = 1;

        setOnScroll(event -> {
            if (event.isControlDown() && maze != null)
            {
                double currentScroll = event.getDeltaY();
                // Scroll up, increase the zoom factor
                if (currentScroll < 0)
                    zoomFactor /= 1.05;
                // Scroll down, decrease the zoom factor
                else
                    zoomFactor *= 1.05;
                drawMaze();
                event.consume();
            }
        });
    }
    public String getImageFileNameStart() {
        return imageFileNameStart.get();
    }

    public void setImageFileNameStart(String imageFileNameStart) {
        this.imageFileNameStart.set(imageFileNameStart);
    }

    public String getImageFileNameBackground() {
        return imageFileNameBackground.get();
    }

    public void setImageFileNameBackground(String imageFileNameBackground) {
        this.imageFileNameBackground.set(imageFileNameBackground);
    }

    public String getImageFileNameWall() {
        return imageFileNameWall.get();
    }

    public void setImageFileNameWall(String imageFileNameWall) {
        this.imageFileNameWall.set(imageFileNameWall);
    }

    public String getImageFileNameGoal() {
        return imageFileNameGoal.get();
    }

    public void setImageFileNameGoal(String imageFileNameGoal) {
        this.imageFileNameGoal.set(imageFileNameGoal);
    }

    public String getImageFileNamePlayer() {
        return imageFileNamePlayer.get();
    }

    public void setImageFileNamePlayer(String imageFileNamePlayer) {
        this.imageFileNamePlayer.set(imageFileNamePlayer);
    }

    public String getImageFileNameSolution() {
        return imageFileNameSolution.get();
    }

    public void setImageFileNameSolution(String imageFileNameSolution) {
        this.imageFileNameSolution.set(imageFileNameSolution);
    }

    public void setMaze(Maze maze) {
        this.maze = maze;
    }

    public void setSolution(Solution solution) {
        this.solution = solution;
    }

    public int getCharacterRow() {
        return this.characterRow;
    }

    public int getCharacterCol() {
        return this.characterCol;
    }

    public void setCharacterPosition(int row, int col) {
        this.characterRow = row;
        this.characterCol = col;
        drawMaze();
    }


    public void setZoomFactor(double zoomFactor) {
        this.zoomFactor = zoomFactor;
    }

    public void drawMaze()
    {
        if(maze == null)
        {
            Alert mazeNotFountAlert = new Alert(Alert.AlertType.ERROR);
            mazeNotFountAlert.setTitle("Maze Not Found");
            mazeNotFountAlert.setContentText("You need to define maze for the maze displayer first");
            mazeNotFountAlert.show();
            return;
        }

        graphicsContext2D = getGraphicsContext2D();
        // Clear the canvas from 0,0 to canvasWidth,canvasWidth
        graphicsContext2D.clearRect(0, 0, getWidth(), getHeight());
        graphicsContext2D.setFill(Color.BLACK);
        this.cellHeight = (getHeight() / maze.getRows()) * zoomFactor;
        this.cellWidth = (getWidth() / maze.getColumns()) * zoomFactor;
        Image wallImage = null;
        Image goalImage = null;
        Image characterImage;
        Image backgroundImage = null;
        Image startImage = null;
        try
        {
            startImage = new Image(new FileInputStream(getImageFileNameStart()));
        }
        catch (FileNotFoundException e) {
            Alert characterNotFountAlert = new Alert(Alert.AlertType.ERROR);
            characterNotFountAlert.setTitle("Image Not Found");
            characterNotFountAlert.setContentText("Start image not found");
            characterNotFountAlert.show();
        }
        try
        {
            backgroundImage = new Image(new FileInputStream(getImageFileNameBackground()));
        }
        catch (FileNotFoundException e) {
            Alert characterNotFountAlert = new Alert(Alert.AlertType.ERROR);
            characterNotFountAlert.setTitle("Image Not Found");
            characterNotFountAlert.setContentText("Background image not found");
            characterNotFountAlert.show();
        }
        if (backgroundImage != null){
            graphicsContext2D.drawImage(backgroundImage, 0, 0);
        }
        try
        {
            characterImage = new Image(new FileInputStream(getImageFileNamePlayer()));
        }
        catch (FileNotFoundException e) {
            Alert characterNotFountAlert = new Alert(Alert.AlertType.ERROR);
            characterNotFountAlert.setTitle("Image Not Found");
            characterNotFountAlert.setContentText("Character image not found");
            characterNotFountAlert.show();
            return;
        }
        try
        {
            wallImage = new Image(new FileInputStream(getImageFileNameWall()));
        }
        catch (FileNotFoundException e) {
            Alert wallNotFountAlert = new Alert(Alert.AlertType.WARNING);
            wallNotFountAlert.setTitle("Image Not Found");
            wallNotFountAlert.setContentText("Wall image not found, the walls will be drawn by colors");
            wallNotFountAlert.show();
        }
        try
        {
            goalImage = new Image(new FileInputStream(getImageFileNameGoal()));
        }
        catch (FileNotFoundException e) {
            Alert wallNotFountAlert = new Alert(Alert.AlertType.WARNING);
            wallNotFountAlert.setTitle("Image Not Found");
            wallNotFountAlert.setContentText("Goal image not found, the walls will be drawn by colors");
            wallNotFountAlert.show();
        }
        double height;
        double width;
        for(int row = 0; row < maze.getRows(); row++)
        {
            for(int col = 0; col < maze.getColumns(); col++)
            {
                height = row * cellHeight;
                width = col * cellWidth;
                if(maze.getValue(row, col) == 1)
                {
                    if (wallImage == null)
                        graphicsContext2D.fillRect(width, height, cellWidth, cellHeight);
                    else
                        graphicsContext2D.drawImage(wallImage, width, height, cellWidth, cellHeight);
                }
                if (row == maze.getRows() - 1 && col == maze.getColumns() -1)
                {
                    if (wallImage == null)
                        graphicsContext2D.fillRect(width, height, cellWidth, cellHeight);
                    else
                        graphicsContext2D.drawImage(goalImage, width, height, cellWidth, cellHeight);
                }
            }
        }
        if (startImage != null){
            graphicsContext2D.drawImage(startImage, 0, 0, cellWidth, cellHeight);
        }
        if(this.showSolution) drawSolution();
        // Draw the character
        graphicsContext2D.drawImage(characterImage, characterCol * cellWidth, characterRow * cellHeight, cellWidth, cellHeight);
    }

    private void drawSolution()
    {
        Image pathImage = null;
        try {
            pathImage = new Image(new FileInputStream(getImageFileNameSolution()));
        } catch (FileNotFoundException e) {
            Alert pathNotFountAlert = new Alert(Alert.AlertType.WARNING);
            pathNotFountAlert.setTitle("Image Not Found");
            pathNotFountAlert.setContentText("Path image not found, the path will be drawn by colors");
            pathNotFountAlert.show();
        }
        if (pathImage == null) graphicsContext2D.setFill(Color.RED);
        double height;
        double width;
        for(AState solState : solution.getSolutionPath())
        {
            Position solutionPosition = ((MazeState)solState).getPosition();
            height = solutionPosition.getRowIndex() * cellHeight;
            width = solutionPosition.getColumnIndex() * cellWidth;
            if (pathImage == null)
                graphicsContext2D.fillRect(width, height, cellWidth, cellHeight);
            else
                graphicsContext2D.drawImage(pathImage, width, height, cellWidth, cellHeight);
        }
        graphicsContext2D.setFill(Color.BLACK);
    }


    public boolean isShowSolution() { return showSolution; }
    public void showSolution() { showSolution = true; }
    public void hideSolution() { showSolution = false; }

    public void clearMazeDisplayer()
    {
        maze = null;
        this.showSolution = false;
        this.characterRow = 0;
        this.characterCol = 0;
        graphicsContext2D.clearRect(0, 0, getWidth(), getHeight());
    }

}
