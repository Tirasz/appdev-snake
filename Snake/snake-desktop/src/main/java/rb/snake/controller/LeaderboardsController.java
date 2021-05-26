package rb.snake.controller;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import rb.snake.App;
import rb.snake.dao.RecordDAO;
import rb.snake.model.Game;
import rb.snake.model.Record;

import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class LeaderboardsController implements Initializable {
    @FXML
    private HBox LBHeader;

    @FXML
    private ToggleButton SPButton;

    @FXML
    private TextField TextFieldSearch;

    @FXML
    private ToggleButton MPButton;

    @FXML
    private TableView<Record> RecordsTable;

    @FXML
    private ToggleGroup LBMode;

    @FXML
    private TableColumn<Record, String> P1;

    @FXML
    private TableColumn<Record, Integer> P1Score;

    @FXML
    private TableColumn<Record, String> P2;

    @FXML
    private TableColumn<Record, Integer> P2Score;

    @FXML
    private Button back;

    @FXML
    private TableColumn<Record, Void> ActionColumn;



    void SelectMP() {
        //Open MP columns, put records into table, reset search textfield
        P2.setPrefWidth(150);
        P2Score.setPrefWidth(150);
        RecordsTable.getItems().setAll(dao.getAllMP());

        TextFieldSearch.setText("");;
    }

    void SelectSP() {
        //Close MP columns, put records into table, reset search textfield
        P2.setPrefWidth(0);
        P2Score.setPrefWidth(0);
        RecordsTable.getItems().setAll(dao.getAllSP());




        TextFieldSearch.setText("");

    }

    void SelectAll(){
        //Open MP columns, put records into table, reset search textfield
        P2.setPrefWidth(150);
        P2Score.setPrefWidth(150);
        RecordsTable.getItems().setAll(dao.getAll());
        TextFieldSearch.setText("");

    }

    Label defaultPlaceHolder = new Label("No records found!");

    @FXML
    void refreshTable(ActionEvent e) {
        //onAction for buttons
        ToggleButton selected = (ToggleButton) LBMode.getSelectedToggle();

            Task<Void> task;

            try{
                if ("SPButton".equals(selected.getId())) {//SelectSP();
                    task = new Task<>() {

                        @Override
                        protected Void call() throws Exception {
                            SelectSP();
                            return null;
                        }
                    };
                } else {
                    task = new Task<>() {
                        @Override
                        protected Void call() throws Exception {
                            SelectMP();
                            return null;
                        }
                    };
                }
            }catch (NullPointerException exc){
                task = new Task<>(){

                    @Override
                    protected Void call() throws Exception {
                        SelectAll();
                        return null;
                    }
                };
            }

            Thread refreshThread = new Thread(task);

            task.setOnSucceeded(event -> {
                SPButton.setDisable(false);
                MPButton.setDisable(false);
                TextFieldSearch.setDisable(false);
                RecordsTable.setPlaceholder(defaultPlaceHolder);
            });

            RecordsTable.setPlaceholder(new ProgressIndicator(-1.0f));
            RecordsTable.getItems().clear();
            SPButton.setDisable(true);
            MPButton.setDisable(true);
            TextFieldSearch.setDisable(true);
            refreshThread.start();

    }

    @FXML
    void switchToMain(ActionEvent event) {
        App.setStage(App.MAIN_MENU);
    }

    private final RecordDAO dao = new RecordDAO();

    private void editRecord(Record r){
        boolean isMP = !(r.getP2() == null);
        Dialog<Record> dialog = new Dialog<>();
        dialog.setTitle("Edit record");
        ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
        HBox root = new HBox();
        root.setPrefWidth(500);
        root.setPrefHeight(200);
        root.setAlignment(Pos.CENTER);
        VBox vboxP1N = new VBox();
        VBox vboxP1S = new VBox();
        VBox vboxP2N = new VBox();
        VBox vboxP2S = new VBox();
        TextField P1N = new TextField(r.getP1());
        TextField P2N = new TextField(r.getP2());
        TextField P1S = new TextField(Integer.toString(r.getP1Score()));
        TextField P2S = new TextField(Integer.toString(r.getP2Score()));
        vboxP1N.getChildren().add(P1N);
        vboxP1S.getChildren().add(P1S);
        vboxP2N.getChildren().add(P2N);
        vboxP2S.getChildren().add(P2S);
        double maxW = (!isMP? ((double)500/2) : ((double)500/4));
        vboxP1N.setMaxWidth(maxW);
        vboxP1S.setMaxWidth(maxW);
        vboxP2N.setMaxWidth(maxW);
        vboxP2S.setMaxWidth(maxW);
        dialog.getDialogPane().setContent(root);
        root.getChildren().addAll(vboxP1N, vboxP1S);
        if(isMP){
            root.getChildren().addAll(vboxP2N, vboxP2S);
        }

        dialog.setResultConverter(dialogButton -> {
            //dialog window returns a record if user clicks on okay
            if(dialogButton != loginButtonType){
                System.out.println(dialogButton);
                return null;
            }

            Record nr = new Record();
            try{
                //making sure the input is valid
                Record.checkName(P1N.getText());
                nr.setP1(P1N.getText());
                nr.setP1Score(Integer.parseInt(P1S.getText()));
                if(isMP){
                    Record.checkName(P2N.getText());
                    nr.setP2(P2N.getText());
                    nr.setP2Score(Integer.parseInt(P2S.getText()));
                }
                return nr;
            }
            catch (NumberFormatException e){ //from parseint
                Record shittyErrorRecordSolutionButIHateJavaSoItsOkay = new Record();
                shittyErrorRecordSolutionButIHateJavaSoItsOkay.setP1("Invalid input!");
                shittyErrorRecordSolutionButIHateJavaSoItsOkay.setP2("Scores have to be integers!");
                return shittyErrorRecordSolutionButIHateJavaSoItsOkay;
            }
            catch (IllegalArgumentException e){ // from  checkName
                Record shittyErrorRecordSolutionButIHateJavaSoItsOkay = new Record();
                shittyErrorRecordSolutionButIHateJavaSoItsOkay.setP1("Invalid input!");
                shittyErrorRecordSolutionButIHateJavaSoItsOkay.setP2(e.getMessage());
                return shittyErrorRecordSolutionButIHateJavaSoItsOkay;
            }

        });


        Optional<Record> newRecord = dialog.showAndWait();

        newRecord.ifPresent(record -> {
            if(record.getP1().equals("Invalid input!")){
                App.showError(record.getP1(), record.getP2());
                return;
            }
            //dao.save(record);
            dao.update(r, record);
            refreshTable(null);

            //dao.update(r, record);
            //refreshTable(null);

        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        P1.setCellValueFactory(new PropertyValueFactory<>("p1"));
        P1.setReorderable(false);
        P1Score.setCellValueFactory(new PropertyValueFactory<>("p1Score"));
        P1Score.setReorderable(false);
        P2.setCellValueFactory(new PropertyValueFactory<>("p2"));
        P2.setReorderable(false);
        P2Score.setCellValueFactory(new PropertyValueFactory<>("p2Score"));
        P2Score.setReorderable(false);
        RecordsTable.setPlaceholder(defaultPlaceHolder);
        ActionColumn.setCellFactory(param -> new TableCell<>(){
            private final Button deleteBtn = new Button("");
            private final Button editBtn = new Button("");

            {
                deleteBtn.setPrefWidth(50);
                deleteBtn.getStyleClass().add("red");
                deleteBtn.getStyleClass().add("delete");
                deleteBtn.setOnAction(event -> {
                    Record r = getTableRow().getItem();

                    if(r.getP1().contains("Tirasz") || (r.getP2() != null && r.getP2().contains("Tirasz"))){
                        App.showError("Absolutely not", "No");
                        return;
                    }

                    String rc = r.getP1() + (r.getP2()==null?"":(" vs. " + r.getP2()));
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete the record for " + rc + "?");
                    confirm.showAndWait().ifPresent(buttonType -> {
                        if(buttonType.equals(ButtonType.OK)){
                            dao.delete(r);
                            refreshTable(null);
                        }
                    });

                });

                editBtn.setPrefWidth(50);
                editBtn.getStyleClass().add("yellow");
                editBtn.getStyleClass().add("edit");
                editBtn.setOnAction(event -> {
                    Record r = getTableRow().getItem();

                    if(r.getP1().contains("Tirasz") || (r.getP2() != null && r.getP2().contains("Tirasz"))){
                        App.showError("Absolutely not", "No");
                        return;
                    }

                    editRecord(r);
                });
            }

            @Override
            protected void updateItem(Void unused, boolean empty) {
                super.updateItem(unused, empty);
                if(empty){
                    setGraphic(null);
                }else{
                    HBox container = new HBox();
                    container.setPrefWidth(100);
                    container.setMaxWidth(160);
                    container.setSpacing(10);
                    container.setAlignment(Pos.CENTER);
                    container.getChildren().addAll(deleteBtn, editBtn);
                    setGraphic(container);
                }
            }
        });
        SelectAll();
        TextFieldSearch.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {

                Task<ArrayList<Record>> task = new Task<>(){
                    @Override
                    protected ArrayList<Record> call() throws Exception {
                        SPButton.setDisable(true);
                        MPButton.setDisable(true);
                        return new ArrayList<>(dao.searchByName(TextFieldSearch.getText()));
                    }
                };

                task.setOnSucceeded(event -> {
                    RecordsTable.getItems().setAll(task.getValue());
                    SPButton.setDisable(false);
                    MPButton.setDisable(false);
                });

                Thread searchThread = new Thread(task);
                searchThread.start();

            }
        });


    }


}
