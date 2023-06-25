package View;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;


public class HelpController implements Initializable {
    @FXML
    Label textInformation;
    @FXML
    Button closeButton;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        textInformation.setWrapText(true);
        textInformation.setText("" +
                        "How to play: \n" +
                        "Up = 8/UP       Up & Left = 7 \n" +
                        "Down = 2/DOWN     Up & Right = 9 \n" +
                        "Left = 4/LEFT     Down & Left = 1 \n" +
                        "Right = 6/RIGHT    Down & Right = 3 \n" +
                        "You can also use your mouse \n" +
                        "Goal: \n" +
                        "Help the astronaut reach to mars and escape from the earth. \n" +
                        "Avoid the asteroid and find the correct path. \n" +
                "If you're having difficulties, try the solve button.");
    }

    public void closeWindow(ActionEvent event) {
        Stage s = (Stage) closeButton.getScene().getWindow();
        s.close();
    }
}
