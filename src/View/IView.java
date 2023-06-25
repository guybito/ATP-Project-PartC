package View;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.search.Solution;
import javafx.event.ActionEvent;

public interface IView {
    void generateMaze(ActionEvent event);

    void generateMazeSolution();

    void displayMaze(Maze maze);
    void displayMazeSolution(Solution solution);

    void newMaze(ActionEvent event);

    void saveMaze(ActionEvent event);

    void loadMaze(ActionEvent event);
}
