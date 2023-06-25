package ViewModel;

import Client.Client;
import Client.IClientStrategy;
import Model.IModel;
import Model.MyModel;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import IO.MyDecompressorInputStream;
import Server.Server;
import algorithms.mazeGenerators.AMazeGenerator;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.search.AState;
import algorithms.search.Solution;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer {
    private IModel model;
    private boolean gameOver;
    private int currRow;
    private int currColumn;
    private MyModel.Direction lastDirection;


    public MyModel.Direction getLastDirection() {
        return lastDirection;
    }

    public MyViewModel() {
        this.model = new MyModel();
        gameOver = model.isGameOver();
        ((Observable)model).addObserver(this);
    }

    public Maze generateMaze(int rows, int cols) {
        gameOver = false;
        return model.generateMaze(rows, cols);
    }

    public Solution generateSolution() {
        return model.generateSolution();
    }

    public int getCurrRow() {
        return currRow;
    }

    public int getCurrColumn() {
        return currColumn;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    @Override
    public void update(Observable o, Object arg) {
        if(o == model)
        {
            currRow = model.getCurrRow();
            currColumn = model.getCurrColumn();
            gameOver = model.isGameOver();
            lastDirection = model.getLastDirection();
            setChanged();
        }
        notifyObservers();
    }

    public void move(KeyCode code) {
        model.move(code);
    }

    public void move(MouseEvent mouseEvent, double cellWidth, double cellHeight) {
        model.move(mouseEvent, cellWidth, cellHeight);
    }

    public boolean saveMaze(File file) {
        return model.saveMaze(file);
    }

    public Maze loadMaze(File file) {
        return model.loadMaze(file);
    }
}
