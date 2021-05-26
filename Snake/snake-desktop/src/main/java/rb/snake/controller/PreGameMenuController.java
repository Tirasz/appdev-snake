package rb.snake.controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import rb.snake.App;
import rb.snake.model.Game;

import java.beans.PropertyChangeEvent;
import java.net.URL;
import java.util.ResourceBundle;

public class PreGameMenuController implements Initializable {

    @FXML
    private TextField TextFieldP1;
    @FXML
    private ColorPicker ColorPickerP1;
    @FXML
    private VBox P2VBox;
    @FXML
    private TextField TextFieldP2;
    @FXML
    private ColorPicker ColorPickerP2;


    @FXML
    void ChangeColorP1(ActionEvent event) {
        Game.setP1Color(ColorPickerP1.getValue());
    }

    @FXML
    void ChangeColorP2(ActionEvent event) {
        Game.setP2Color(ColorPickerP2.getValue());
    }

    @FXML
    void switchToGame(ActionEvent event) {
        try{
            Game.setP1Name(TextFieldP1.getText());
            if(Game.isMultiPlayer()){
                Game.setP2Name(TextFieldP2.getText());
            }
        }catch (IllegalArgumentException e){
            App.showError("Input not valid!", e.getMessage());
            return;
        }
        App.setStage(App.PRE_PRE_GAME);
    }

    @FXML
    void switchToMain(ActionEvent event) {
        App.setStage(App.MAIN_MENU);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Game.PCS.addPropertyChangeListener("isMultiplayer",this::updateScene);
        ColorPickerP1.setValue(Game.getP1Color());
        ColorPickerP2.setValue(Game.getP2Color());
        TextFieldP1.setText(Game.getP1Name());
        TextFieldP2.setText(Game.getP2Name());
    }


    public void updateScene(PropertyChangeEvent e) {
        //When the multiplayer flag changes, this updates the scene.
        System.out.println("Updated pre");
        if ((Boolean) e.getNewValue()) {
            P2VBox.setPrefWidth(400);
            P2VBox.setVisible(true);
            P2VBox.setDisable(false);
            for (Node n : P2VBox.getChildren()) {
                n.setVisible(true);
            }
        }else{
            P2VBox.setPrefWidth(0);
            P2VBox.setVisible(false);
            P2VBox.setDisable(true);
            for(Node n : P2VBox.getChildren()){
                n.setVisible(false);
            }
        }
    }



}


