package View;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AboutController implements Initializable {
    @FXML
    Label textInformation;
    @FXML
    Button closeButton;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        textInformation.setWrapText(true);
        textInformation.setText(" This is a maze game based on Interstellar movie! \n" +
                        "The maze is built using a Prim based algorithm. \n" +
                        "It is solved using one of three search algorithms: \n" +
                        "BFS, DFS and Best FS. \n" +
                "This game is brought to you by Guy Biton and Itamar Barami.");
    }

    public void closeWindow(ActionEvent event) {
        Stage s = (Stage) closeButton.getScene().getWindow();
        s.close();
    }
}