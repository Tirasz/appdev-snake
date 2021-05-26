package rb.snake;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;


import java.io.*;

/**
 * JavaFX App
 */
public class App extends Application {


    private static Stage stage;
    public static final Scene MAIN_MENU = loadFXML("/fxml/main.fxml");
    public static final Scene OPTIONS_MENU = loadFXML("/fxml/options.fxml");
    public static final Scene PRE_GAME_MENU = loadFXML("/fxml/pregame.fxml");
    public static final Scene PRE_PRE_GAME = loadFXML("/fxml/pre_pregame.fxml");
    public static final Scene GAME_SCENE = loadFXML("/fxml/game.fxml");
    public static final Scene LEADERBOARDS = loadFXML("/fxml/leaderboards.fxml");

    @Override
    public void start(Stage stage) throws IOException {
        try {
            App.stage = stage;
            App.stage.setResizable(false);
            App.stage.getIcons().add(new Image("/assets/icon.png"));
            App.stage.setTitle("Best Snake game in 2021");
            setStage(App.MAIN_MENU);
            stage.show();
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public static void setStage(Scene s){
        s.getRoot().requestFocus();
        App.stage.setScene(s);
    }

    private static Scene loadFXML(String fxml){
        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml));

        Parent p = null;
        try {
            p = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene s = new Scene(p);
        return s;
    }

    public static void showError(String header, String msg){
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText(header);
        errorAlert.setContentText(msg);
        errorAlert.showAndWait();

    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop(){

    }
}