package View;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        System.setProperty("log4j.configurationFile", "resources/log4j2.xml");
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("MyView.fxml")));
        Scene scene = new Scene(root);
        stage.setTitle("Our Space Maze");
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
        stage.show();
    }
}
