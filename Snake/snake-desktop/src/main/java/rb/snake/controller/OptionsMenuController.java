package rb.snake.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import rb.snake.App;
import rb.snake.model.Game;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class OptionsMenuController implements Initializable {


    @FXML
    private ToggleButton ls_small;

    @FXML
    private ToggleGroup level_size;

    @FXML
    private ToggleButton ls_medium;

    @FXML
    private ToggleButton ls_large;

    @FXML
    private ToggleButton ls_custom;

    @FXML
    private ToggleButton df_easy;

    @FXML
    private ToggleGroup difficulty;

    @FXML
    private ToggleButton df_medium;

    @FXML
    private ToggleButton df_hard;

    @FXML
    private ToggleButton w_on;

    @FXML
    private ToggleGroup level_walls;

    @FXML
    private ToggleButton w_off;

    @FXML
    private ColorPicker foodColorPicker;

    @FXML
    private Button backButton;


    @FXML
    void changeFoodColor(ActionEvent event) {
        Game.setFoodColor(foodColorPicker.getValue());
    }

    @FXML
    void setDifficulty(ActionEvent event) {
        String id = ((Node) event.getSource()).getId();
        switch (id) {
            case "df_easy":
                Game.setGameDifficulty(2);
                break;
            case "df_medium":
                Game.setGameDifficulty(3);
                break;
            case "df_hard":
                Game.setGameDifficulty(5);
                break;
        }
        setChecked();
    }

    @FXML
    void setLevelSize(ActionEvent event) {
        String id = ((Node) event.getSource()).getId();
        switch (id) {
            case "ls_small":
                Game.setPredefinedSize(1);
                break;
            case "ls_medium":
                Game.setPredefinedSize(2);
                break;
            case "ls_large":
                Game.setPredefinedSize(3);
                break;
            case "ls_custom":
                setCustomBoardSize();
                break;
        }
        setChecked();
    }

    private void setCustomBoardSize() {
        //Setting up dialog window
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Set custom board size!");
        ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));
        TextField columns = new TextField();
        columns.setPromptText("Columns");
        TextField rows = new TextField();
        rows.setPromptText("Rows");
        gridPane.add(columns, 0, 0);
        gridPane.add(new Label("By:"), 1, 0);
        gridPane.add(rows, 2, 0);
        dialog.getDialogPane().setContent(gridPane);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(columns.getText(), rows.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        //Runs if the user doesnt cancel
        //Tries to convert input to integer, and tries to set settings accordingly.
        //On invalid input (smaller than minGameSize, or not integer values), shows an alert and asks for the input again.
        result.ifPresent(pair -> {
            try {
                Game.setBoardColumns(Integer.parseInt(pair.getKey()));
                Game.setBoardRows(Integer.parseInt(pair.getValue()));
                Game.setPredefinedSize(0);
                ls_custom.setSelected(true);
                setChecked();
            } catch (NumberFormatException e) {
                App.showError("Input not valid!", "Only enter whole numbers!");
                setCustomBoardSize();
            } catch (IllegalArgumentException e) {
                App.showError("Input not valid!", e.getMessage());
                setCustomBoardSize();
            }
        });
        //Runs if the user cancels: Selects and sets the level size to the default (medium)
        if (result.isEmpty()) {
            ls_medium.setSelected(true);
            Game.setPredefinedSize(2);
            setChecked();
        }

    }

    private void setChecked() {
        ToggleButton dif = (ToggleButton) difficulty.getSelectedToggle();
        ToggleButton ls = (ToggleButton) level_size.getSelectedToggle();
        ToggleButton lw = (ToggleButton) level_walls.getSelectedToggle();
        try {
            if(dif == null || ls == null ||lw == null){
                throw new NullPointerException("sad");
            }
        }catch (NullPointerException e){
            preSelectToggles();
        }
    }

    @FXML
    void setWalls(ActionEvent event) {
        String id = ((Node) event.getSource()).getId();
        Game.setBoardHasWalls(id.equals("w_on"));
        setChecked();
    }

    @FXML
    void switchToMain(ActionEvent event) {
        App.setStage(App.MAIN_MENU);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        preSelectToggles();
    }

    private void preSelectToggles(){
        //preselect settings
        w_on.setSelected(Game.isBoardHasWalls());
        w_off.setSelected(!Game.isBoardHasWalls());
        switch (Game.getGameDifficulty()) {
            case 2:
                df_easy.setSelected(true);
                break;
            case 3:
                df_medium.setSelected(true);
                break;
            case 5:
                df_hard.setSelected(true);
                break;
        }
        switch (Game.getPredefinedSize()) {
            case 1:
                ls_small.setSelected(true);
                break;
            case 2:
                ls_medium.setSelected(true);
                break;
            case 3:
                ls_large.setSelected(true);
                break;
            default:
                ls_custom.setSelected(true);
                break;
        }
        foodColorPicker.setValue(Game.getFoodColor());
    }

}
