package rb.snake.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import rb.snake.App;
import rb.snake.model.Game;

import java.beans.PropertyChangeEvent;
import java.net.URL;
import java.util.ResourceBundle;

public class PrePreGameController implements Initializable {


    @FXML
    private HBox gameHeader;

    @FXML
    private VBox P1Header;

    @FXML
    private Text TextP1Name;

    @FXML
    private VBox P2Header;

    @FXML
    private Text TextP2Name;

    @FXML
    private HBox P1Cannibal;

    @FXML
    void handleOnKeyPressed(KeyEvent event) {
        Game.initBoard();
        App.setStage(App.GAME_SCENE);

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Game.PCS.addPropertyChangeListener(this::updateScene);
        TextP2Name.setText(Game.getP2Name());
        TextP1Name.setText(Game.getP1Name());
    }

    public void updateScene(PropertyChangeEvent e) {
        System.out.println("Updated pre-pre");

        TextP2Name.setText(Game.getP2Name());
        TextP1Name.setText(Game.getP1Name());

        if (Game.isMultiPlayer()) {
            P1Cannibal.setVisible(true);
            P2Header.setPrefWidth(400);
            P2Header.setVisible(true);
            P2Header.setDisable(false);
            for (Node n : P2Header.getChildren()) {
                n.setVisible(true);
            }
        }else{
            P1Cannibal.setVisible(false);
            P2Header.setPrefWidth(0);
            P2Header.setVisible(false);
            P2Header.setDisable(true);
            for(Node n : P2Header.getChildren()){
                n.setVisible(false);
            }
        }
    }
}
