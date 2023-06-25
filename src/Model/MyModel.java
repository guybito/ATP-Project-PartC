package Model;

import Client.Client;
import Client.IClientStrategy;
import IO.MyCompressorOutputStream;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import IO.MyDecompressorInputStream;
import Server.Server;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.Observable;

public class MyModel extends Observable implements IModel{
    private int currRow;
    private int currColumn;
    private int goalRow;
    private int goalColumn;
    private boolean gameOver;
    private Maze maze;
    private Solution mazeSolution;
    private static Server mazeGeneratingServer;
    private static Server solveSearchProblemServer;
    private double prevMouseX;
    private double prevMouseY;
    public enum Direction{UP, DOWN, RIGHT, LEFT}
    private Direction lastDirection;
    private static final Logger logger = LogManager.getLogger();



    public Direction getLastDirection() {
        return lastDirection;
    }

    public MyModel() {
        mazeGeneratingServer = new Server(5400, 0, new ServerStrategyGenerateMaze());
        logger.info(mazeGeneratingServer.toString() + " Starting server at port = "  + 5400 );
        mazeGeneratingServer.start();
        solveSearchProblemServer = new Server(5401, 0, new ServerStrategySolveSearchProblem());
        logger.info(solveSearchProblemServer.toString() + " Starting server at port = "  + 5401 );
        solveSearchProblemServer.start();
        lastDirection = Direction.UP;
    }

    @Override
    public Maze generateMaze(int rows, int cols) {
        try {
//            mazeGeneratingServer.start();

            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        toServer.writeObject(new int[]{ rows, cols }); //send mazedimensions to server
                        toServer.flush();
                        byte[] compressedMaze = (byte[]) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                        InputStream inputStream = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[rows * cols + 12]; //allocating byte[] for the decompressed maze
                        inputStream.read(decompressedMaze); //Fill decompressedMaze with bytes
//                        mazeGeneratingServer.stop();
                        toServer.close();
                        fromServer.close();
                        maze = new Maze(decompressedMaze);
                        gameOver = false;
                        currRow = maze.getStartPosition().getRowIndex();
                        currColumn = maze.getStartPosition().getColumnIndex();
                        goalRow = maze.getGoalPosition().getRowIndex();
                        goalColumn = maze.getGoalPosition().getColumnIndex();
                        logger.info(mazeGeneratingServer.toString() + " generated maze [" + rows + "X" + cols + "] ");
                    } catch (Exception e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Generating Maze Error");
                        alert.setContentText("There was an error while generating maze: " + e.getMessage());
                        logger.info(mazeGeneratingServer.toString() + " Error to generate maze [" + rows + "X" + cols + "]: " + e.getMessage());
                        alert.show();
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Generating Maze Error");
            alert.setContentText("There was an error while generating maze: " + e.getMessage());
            logger.info(mazeGeneratingServer.toString() + " Error to generate maze [" + rows + "X" + cols + "]: " + e.getMessage());
            alert.show();
        }
        return maze;
    }

    @Override
    public Solution generateSolution()
    {
//        solveSearchProblemServer.start();
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        toServer.writeObject(maze); //send maze to server
                        toServer.flush();
                        mazeSolution = (Solution) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
//                        solveSearchProblemServer.stop();
                        toServer.close();
                        fromServer.close();
                        logger.info(mazeGeneratingServer.toString() + "generated solution");
                    } catch (Exception e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Generating Solution Error");
                        alert.setContentText("There was an error while generating solution: " + e.getMessage());
                        logger.info(mazeGeneratingServer.toString() + "There was an error while generating solution: " + e.getMessage());

                        alert.show();
                    }
                    finally {

                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Generating Solution Error");
            alert.setContentText("There was an error while generating solution: " + e.getMessage());
            logger.info(mazeGeneratingServer.toString() + "There was an error while generating solution: " + e.getMessage());
            alert.show();
        }
        return mazeSolution;
    }
    @Override
    public int getCurrRow() {
        return currRow;
    }
    @Override
    public int getCurrColumn() {
        return currColumn;
    }

    @Override
    public boolean isGameOver() {
        return gameOver;
    }

    @Override
    public void move(KeyCode code)
    {
        switch (code){
            case UP: case NUMPAD8: // UP
                if (maze.getValue(currRow - 1, currColumn) == 0){
                    currRow --;
                    lastDirection = Direction.UP;
                    setChanged();
                }
                break;
            case DOWN: case NUMPAD2: // DOWN
                if (maze.getValue(currRow + 1, currColumn) == 0) {
                    currRow++;
                    lastDirection = Direction.DOWN;
                    setChanged();
                }
                break;
            case RIGHT: case NUMPAD6: // RIGHT
                if (maze.getValue(currRow, currColumn + 1) == 0) {
                    currColumn++;
                    lastDirection = Direction.RIGHT;
                    setChanged();
                }
                break;
            case LEFT: case NUMPAD4: // LEFT
                if (maze.getValue(currRow, currColumn - 1) == 0) {
                    currColumn--;
                    lastDirection = Direction.LEFT;
                    setChanged();
                }
                break;
            case NUMPAD9: // UP RIGHT
                if (maze.getValue(currRow - 1, currColumn + 1) == 0 &&
                        (maze.getValue(currRow, currColumn + 1) == 0 ||
                                maze.getValue(currRow - 1, currColumn) == 0)){
                    currRow--;
                    currColumn++;
                    lastDirection = Direction.UP;
                    setChanged();
                }
                break;
            case NUMPAD3: // DOWN RIGHT
                if (maze.getValue(currRow + 1, currColumn + 1) == 0 &&
                        (maze.getValue(currRow, currColumn + 1) == 0 ||
                                maze.getValue(currRow + 1, currColumn) == 0)){
                    currRow++;
                    currColumn++;
                    lastDirection = Direction.DOWN;
                    setChanged();
                }
                break;
            case NUMPAD1: // DOWN LEFT
                if (maze.getValue(currRow + 1, currColumn - 1) == 0 &&
                        (maze.getValue(currRow, currColumn - 1) == 0 ||
                                maze.getValue(currRow + 1, currColumn) == 0)){
                    currRow ++;
                    currColumn --;
                    lastDirection = Direction.DOWN;
                    setChanged();
                }
                break;
            case NUMPAD7: // UP LEFT
                if (maze.getValue(currRow - 1, currColumn - 1) == 0 &&
                        (maze.getValue(currRow, currColumn - 1) == 0 ||
                                maze.getValue(currRow - 1, currColumn) == 0)){
                    currRow --;
                    currColumn --;
                    lastDirection = Direction.UP;
                    setChanged();
                }
                break;
        }
        if (this.currRow == maze.getGoalPosition().getRowIndex() && this.currColumn == maze.getGoalPosition().getColumnIndex())
            gameOver = true;
        notifyObservers();
    }

    @Override
    public void move(MouseEvent mouseEvent, double cellWidth, double cellHeight)
    {
        double mouseX = mouseEvent.getX();
        double mouseY = mouseEvent.getY();

        // Compare current mouse coordinates with previous coordinates
        double deltaX = mouseX - prevMouseX;
        double deltaY = mouseY - prevMouseY;

        if (Math.abs(deltaX) < cellWidth && Math.abs(deltaY) < cellHeight) {
            return;
        }

        // Analyze differences to determine direction
        if (deltaX > 0 && Math.abs(deltaX) > Math.abs(deltaY)) {
            // Mouse moved to the right
            if (maze.getValue(currRow, currColumn + 1) == 0) {
                currColumn++;
                lastDirection = Direction.RIGHT;
                setChanged();
            }
        } else if (deltaX < 0 && Math.abs(deltaX) > Math.abs(deltaY)) {
            // Mouse moved to the left
            if (maze.getValue(currRow, currColumn - 1) == 0) {
                currColumn--;
                lastDirection = Direction.LEFT;
                setChanged();
            }
        } else if (deltaY > 0 && Math.abs(deltaY) > Math.abs(deltaX)) {
            // Mouse moved downwards
            if (maze.getValue(currRow + 1, currColumn) == 0) {
                currRow++;
                lastDirection = Direction.DOWN;
                setChanged();
            }
        } else if (deltaY < 0 && Math.abs(deltaY) > Math.abs(deltaX)) {
            // Mouse moved upwards
            if (maze.getValue(currRow - 1, currColumn) == 0){
                currRow --;
                lastDirection = Direction.UP;
                setChanged();
            }
        }
        if (this.currRow == maze.getGoalPosition().getRowIndex() && this.currColumn == maze.getGoalPosition().getColumnIndex())
            gameOver = true;
        notifyObservers();

        // Update previous mouse coordinates
        prevMouseX = mouseX;
        prevMouseY = mouseY;
    }
    @Override
    public boolean saveMaze(File file)
    {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(Files.newOutputStream(file.toPath()));
            objectOutputStream.writeObject(maze);
            objectOutputStream.flush();
            objectOutputStream.close();
            logger.info("Maze has been save too: " + file.getAbsolutePath());
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    @Override
    public Maze loadMaze(File file)
    {
        byte[] savedMazeBytes;
        try {
            ObjectInputStream inputStream = new ObjectInputStream(Files.newInputStream(file.toPath()));
            maze = (Maze) inputStream.readObject();
            gameOver = false;
            currRow = maze.getStartPosition().getRowIndex();
            currColumn = maze.getStartPosition().getColumnIndex();
            goalRow = maze.getGoalPosition().getRowIndex();
            goalColumn = maze.getGoalPosition().getColumnIndex();
            logger.info("Maze has been loaded from: " + file.getAbsolutePath());
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
        return maze;
    }
}
