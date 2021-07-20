package start;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j

public class App extends Application {

    public static Stage ps;
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("prop.fxml"));
        Parent parent = loader.load();
        primaryStage.setScene(new Scene(parent));
        ps = primaryStage;
        primaryStage.show();
    }
}
