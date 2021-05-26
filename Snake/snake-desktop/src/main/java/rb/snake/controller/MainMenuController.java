package rb.snake.controller;

import javafx.application.Platform;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import rb.snake.App;
import rb.snake.model.Game;


public class MainMenuController {

        @FXML
        private Button goToSP;

        @FXML
        private Button goToMP;

        @FXML
        private Button goToSettings;

        @FXML
        private Button goToLB;

        @FXML
        private Button exit;

        @FXML
        void exit(ActionEvent event) {
                Platform.exit();
        }

        @FXML
        void switchToLB(ActionEvent event) {
                App.setStage(App.LEADERBOARDS);
        }

        @FXML
        void switchToMP(ActionEvent event) {
                App.setStage(App.PRE_GAME_MENU);
                Game.setMultiplayer(true);
        }

        @FXML
        void switchToOp(ActionEvent event) {
                App.setStage(App.OPTIONS_MENU);
        }

        @FXML
        void switchToSP(ActionEvent event) {
                App.setStage(App.PRE_GAME_MENU);
                Game.setMultiplayer(false);
        }



}
