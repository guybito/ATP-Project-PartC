package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

import java.io.File;

public interface IModel {
    Maze generateMaze(int rows, int cols);

    Solution generateSolution();

    int getCurrRow();

    int getCurrColumn();

    boolean isGameOver();

    void move(KeyCode code);

    void move(MouseEvent mouseEvent, double cellWidth, double cellHeight);

    boolean saveMaze(File file);

    Maze loadMaze(File file);
    public MyModel.Direction getLastDirection();
}
