package rb.snake.controller;
import javafx.animation.AnimationTimer;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;
import rb.snake.App;
import rb.snake.dao.RecordDAO;
import rb.snake.model.Game;
import rb.snake.model.Snake;


import java.beans.PropertyChangeEvent;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

import static rb.snake.model.Game.Tile.*;

public class GameController implements Initializable {

    @FXML
    private Text TextP1Name;

    @FXML
    private Text TextP1Score;

    @FXML
    private VBox P2Header;

    @FXML
    private Text TextP2Name;

    @FXML
    private Text TextP2Score;

    @FXML
    private Canvas gameCanvas;

    @FXML
    private Button MMButton;

    @FXML
    private Button LBButton;

    @FXML
    private ProgressBar P1Ability1Cooldown;

    @FXML
    private ProgressBar P1Ability2Cooldown;

    @FXML
    private ProgressBar P2Ability1Cooldown;

    @FXML
    private ProgressBar P2Ability2Cooldown;

    @FXML
    private AnchorPane P1Ab2Anchor;

    @FXML
    void switchToLB(ActionEvent event) {
        //TODO
        gc.save();
        gc.setFill(DEFAULT_COLOR);
        gc.fillRect(0,0,Game.CANVAS_WIDTH, Game.CANVAS_HEIGHT);
        gc.restore();
        pauseGame();
        App.setStage(App.LEADERBOARDS);
    }

    @FXML
    void switchToMM(ActionEvent event) {
        App.setStage(App.MAIN_MENU);
        gc.save();
        gc.setFill(DEFAULT_COLOR);
        gc.fillRect(0,0,Game.CANVAS_WIDTH, Game.CANVAS_HEIGHT);
        gc.restore();
        pauseGame();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Game.PCS.addPropertyChangeListener(this::updateScene);
        TextP1Score.setText("0");
        TextP2Score.setText("0");
        gc = gameCanvas.getGraphicsContext2D();
        gc.setFont(Font.font(30));

        pauseGame();
    }

    public void updateScene(PropertyChangeEvent e) {
        System.out.println("Updated Game");
        TextP2Name.setText(Game.getP2Name());
        TextP1Name.setText(Game.getP1Name());


        if (Game.isMultiPlayer()) {
            P2Header.setPrefWidth(400);
            P2Header.setVisible(true);
            P2Header.setDisable(false);
            for (Node n : P2Header.getChildren()) {
                n.setVisible(true);
            }
            P1Ab2Anchor.setVisible(true);
        }else{
            P2Header.setPrefWidth(0);
            P2Header.setVisible(false);
            P2Header.setDisable(true);
            for(Node n : P2Header.getChildren()){
                n.setVisible(false);
            }
            P1Ab2Anchor.setVisible(false);
        }
    }

    public static Game.Tile[][] board;
    public static Game.Food[] food;
    public static Snake player1;
    public static Snake player2;
    private GraphicsContext gc;
    private gameState GS;

    private enum gameState{
        STARTED,
        PAUSED
    }

    private AnimationTimer gameAnimation = new AnimationTimer() {
        int frameCount = 1;
        @Override
        public void handle(long l) {
            //Function gets called every frame

            for (Game.Food f : food) {
                Color c = Game.getFoodColor();
                displayTile(f, f.isSpecial?c.invert():c,Game.FOOD);
            }
            //Display snakes
            displaySnake(player1);
            if(Game.isMultiPlayer()){
                displaySnake(player2);
            }
            //display (and update) header
            displayHeader();

            //Update (move) snakes according to their speed
            try {

                if(frameCount % (60/player1.currentSpeed) == 0){
                    displayTile(player1.tail, DEFAULT_COLOR);
                    handlePlayerInput(P1InputQueue);
                    player1.update();
                }

                if(Game.isMultiPlayer() && (frameCount % (60/player2.currentSpeed) == 0)){
                    displayTile(player2.tail, DEFAULT_COLOR);
                    handlePlayerInput(P2InputQueue);
                    player2.update();
                }


                //Check if any of the snakes collided with food
                for (int i = 0; i < food.length; i++) {
                    if (player1.head.x == food[i].x && player1.head.y == food[i].y) {
                        eat_food(i, player1);

                    }
                    if (Game.isMultiPlayer() && player2.head.x == food[i].x && player2.head.y == food[i].y) {
                        eat_food(i, player2);
                    }
                }

                //Check if the snakes collided
                player1.checkCollision(player1);
                if(Game.isMultiPlayer()){
                    player2.checkCollision(player2);
                    player1.checkCollision(player2);
                    player2.checkCollision(player1);
                    //SnakeDeadException gets thrown if the 'checking' snake's head collides with the other player
                    //If the two snake's head collide, the game is over
                }

            }
            catch (Snake.SnakeDeadException e){
                gameOver(e.deadSnake);
            }
            catch (Snake.SnakeEatenException e){
                displayBoard();
            }

            frameCount = (frameCount%60) + 1;

        }


    };

    private void displayTile(Game.Tile t, Color c){
        double xOnCanvas = t.x*tileSize.doubleValue() + offsetX.doubleValue();
        double yOnCanvas = t.y*tileSize.doubleValue() + offsetY.doubleValue();
        gc.setFill(Game.Tile.BORDER_COLOR);
        gc.fillRect(xOnCanvas ,yOnCanvas,tileSize.doubleValue(),tileSize.doubleValue());
        gc.setFill(c);
        gc.fillRect(xOnCanvas+1,yOnCanvas+1,tileSize.doubleValue()-2,tileSize.doubleValue()-2);
    }

    private void displayTile(Game.Tile t, Color c, Image overlay){
        displayTile(t, c);
        double xOnCanvas = t.x*tileSize.doubleValue() + offsetX.doubleValue();
        double yOnCanvas = t.y*tileSize.doubleValue() + offsetY.doubleValue();
        gc.drawImage(overlay, xOnCanvas, yOnCanvas, tileSize.doubleValue(), tileSize.doubleValue());
    }

    private void displayTile(Game.Tile t, Color c, Image overlay, double overlayAngle){
        displayTile(t, c);
        double xOnCanvas = t.x*tileSize.doubleValue() + offsetX.doubleValue();
        double yOnCanvas = t.y*tileSize.doubleValue() + offsetY.doubleValue();
        drawRotatedImage(gc, overlay, overlayAngle, xOnCanvas, yOnCanvas);
    }

    private void rotate(GraphicsContext gc, double angle, double px, double py) {
        Rotate r = new Rotate(angle, px, py);
        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
    }

    private void drawRotatedImage(GraphicsContext gc, Image image, double angle, double tlpx, double tlpy) {
        gc.save(); // saves the current state on stack, including the current transform
        rotate(gc, angle, tlpx + tileSize.doubleValue() / 2, tlpy + tileSize.doubleValue() / 2);
        gc.drawImage(image, tlpx, tlpy, tileSize.doubleValue(), tileSize.doubleValue());
        gc.restore(); // back to original state (before rotation)
    }

    private void displaySnake(Snake snk){
        Color snkc = snk.snakeColor;
        if(snk.cannibalActive > 0){
            snkc = snkc.invert();
            if(snk.cannibalActive <= 10 && snk.cannibalActive % 2 == 0){
                snkc = snk.snakeColor;
            }
        }else if(snk.hungerActive > 0){
            snkc = snkc.desaturate().desaturate().desaturate().darker().darker();
            if(snk.hungerActive <= 10 && snk.hungerActive% 2 == 0){
                snkc = snk.snakeColor;
            }
        }
        double angle = 0;
        switch(snk.currentDirection){
            case RIGHT:
                angle = 90;
                break;
            case LEFT:
                angle = -90;
                break;
            case DOWN:
                angle = 180;
                break;
        }

        Snake.SnakePiece current = snk.head;
        while (current != null) {
            Color c = snkc;
            if(current == snk.head){
                c = snkc.darker().darker();
                displayTile(current, c, Game.SNAKE_HEAD, angle);
            }else{
                displayTile(current, c, Game.SNAKE_BODY);
            }
            current = current.child;
        }

    }

    private void displayHeader(){
        TextP1Score.setText(String.valueOf(player1.getScore()));
        P1Ability1Cooldown.setProgress(((double)player1.hungerCooldown/Game.HUNGER_COOLDOWN));

        if(player1.hungerCooldown == 0){
            P1Ability1Cooldown.setOpacity(0);
        }else{
            P1Ability1Cooldown.setOpacity(0.68);
        }

        if(!player1.cannibalIsOnCooldown){
            P1Ability2Cooldown.setOpacity(0);
        }else{
            P1Ability2Cooldown.setOpacity(0.68);
        }

        if(Game.isMultiPlayer()){
            TextP2Score.setText(String.valueOf(player2.getScore()));
            P2Ability1Cooldown.setProgress(((double)player2.hungerCooldown/Game.HUNGER_COOLDOWN));

            if(player2.hungerCooldown == 0){
                P2Ability1Cooldown.setOpacity(0);
            }else{
                P2Ability1Cooldown.setOpacity(0.68);
            }

            if(!player2.cannibalIsOnCooldown){
                P2Ability2Cooldown.setOpacity(0);
            }else{
                P2Ability2Cooldown.setOpacity(0.68);
            }
        }

    }

    private void displayBoard() {
        for(int x = 0; x < Game.getBoardColumns(); x++) {
            for(int y = 0; y < Game.getBoardRows(); y++) {
                displayTile(board[x][y], DEFAULT_COLOR);
            }
        }
    }

    private void eat_food(int i, Snake player) {
        if(food[i].isSpecial){
            player.cannibalIsOnCooldown = false;
        }
        player.head.full += food[i].value;

        food[i] = new Game.Food();
    }

    private void gameOver(Snake deadSnake) {
        gameAnimation.stop();
        gc.setGlobalAlpha(0.68);
        MMButton.setDisable(false);
        MMButton.setVisible(true);
        LBButton.setVisible(true);
        gc.setFill(Game.Tile.DEFAULT_COLOR);
        gc.fillRect(0,0, Game.CANVAS_WIDTH, Game.CANVAS_HEIGHT);
        gc.setFill(Game.Tile.BORDER_COLOR);
        gc.fillText("Game over", Game.CANVAS_WIDTH/2, Game.CANVAS_HEIGHT/4);
        gc.fillText((deadSnake == player1?Game.getP1Name():Game.getP2Name()) + " died!", Game.CANVAS_WIDTH/2, Game.CANVAS_HEIGHT/4 +40);
        gc.fillText(("Your score" + (Game.isMultiPlayer()?"s have been":" has been") + " submitted."), Game.CANVAS_WIDTH/2, Game.CANVAS_HEIGHT/4 +80);
        gc.setGlobalAlpha(1);

        RecordDAO records = new RecordDAO();
        Task<Void> task = new Task<>(){
            @Override
            protected Void call() throws Exception {
                if(Game.isMultiPlayer()){
                    records.save(Game.getP1Name(), player1.getScore(), Game.getP2Name(), player2.getScore());
                }else{
                    records.save(Game.getP1Name(), player1.getScore());
                }
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            LBButton.setDisable(false);
        });

        Thread saveThread = new Thread(task);
        saveThread.start();

    }

    private void pauseGame(){
        gameAnimation.stop();
        gc.setGlobalAlpha(0.68);
        gc.setFill(Game.Tile.DEFAULT_COLOR);
        gc.fillRect(0,0, Game.CANVAS_WIDTH, Game.CANVAS_HEIGHT);
        gc.setFill(Game.Tile.BORDER_COLOR);
        gc.setStroke(Game.Tile.BORDER_COLOR);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText("Press any key to start!",Game.CANVAS_WIDTH/2, Game.CANVAS_HEIGHT/2);
        GS = gameState.PAUSED;
        MMButton.setDisable(true);
        MMButton.setVisible(false);
        LBButton.setVisible(false);
        LBButton.setDisable(true);
        gc.setGlobalAlpha(1);
    }

    private static class InputQueue<T> extends LinkedList<T> {
        @Override
        public boolean add(T t) {
            if(this.size() >= 4){
                this.clear();
            }
            return super.add(t);
        }
    }

    private final InputQueue<KeyCode> P1InputQueue = new InputQueue<>();
    private final InputQueue<KeyCode> P2InputQueue = new InputQueue<>();

    private void handlePlayerInput(InputQueue<KeyCode> playerInputQueue){
        KeyCode code = playerInputQueue.poll(); //returns null on empty queue
        if(code == null){
            return;
        }
        switch(code){
            case W:
                player1.setCurrentDirection(Snake.Direction.UP);
                break;
            case S:
                player1.setCurrentDirection(Snake.Direction.DOWN);
                break;
            case A:
                player1.setCurrentDirection(Snake.Direction.LEFT);
                break;
            case D:
                player1.setCurrentDirection(Snake.Direction.RIGHT);
                break;
            case F:
                if(player1.hungerCooldown == 0){
                    player1.hungerActive = Game.HUNGER_DURATION;
                    player1.hungerCooldown = Game.HUNGER_COOLDOWN;
                }
                break;
            case R:
                if(!player1.cannibalIsOnCooldown && Game.isMultiPlayer()){
                    player1.cannibalActive = Game.CANNIBAL_DURATION;
                    player1.cannibalIsOnCooldown = true;
                }
                break;
            case UP:
                player2.setCurrentDirection(Snake.Direction.UP);
                break;
            case DOWN:
                player2.setCurrentDirection(Snake.Direction.DOWN);
                break;
            case LEFT:
                player2.setCurrentDirection(Snake.Direction.LEFT);
                break;
            case RIGHT:
                player2.setCurrentDirection(Snake.Direction.RIGHT);
                break;
            case NUMPAD1:
                if(player2.hungerCooldown == 0){
                    player2.hungerActive = Game.HUNGER_DURATION;
                    player2.hungerCooldown = Game.HUNGER_COOLDOWN;
                }
                break;
            case NUMPAD2:
                if(!player2.cannibalIsOnCooldown && Game.isMultiPlayer()){
                    player2.cannibalActive = Game.CANNIBAL_DURATION;
                    player2.cannibalIsOnCooldown = true;
                }
                break;
        }

    }

    public void handleOnKeyPressed(KeyEvent keyEvent) {
        if(GS == gameState.PAUSED){
            displayBoard();
            gameAnimation.start();
            GS = gameState.STARTED;
            return;
        }

        if(GS != gameState.STARTED){
            return;
        }

        try {
            switch (keyEvent.getCode()) {
                case P:
                    pauseGame();
                    break;
                case T:
                    Game.setBoardRows(Game.getBoardRows()+1);
                    Game.initBoard();
                    displayBoard();
                    break;
                case Z:
                    Game.setBoardColumns(Game.getBoardColumns()+1);
                    Game.initBoard();
                    displayBoard();
                    break;
                case UP:
                case DOWN:
                case LEFT:
                case RIGHT:
                case NUMPAD1:
                case NUMPAD2:
                    P2InputQueue.add(keyEvent.getCode());
                    break;
                case W:
                case A:
                case S:
                case D:
                case F:
                case R:
                    P1InputQueue.add(keyEvent.getCode());
                    break;
                default:
                    System.out.println(keyEvent.getCode());
            }
        }catch (NullPointerException ignored ){}
    }




}
