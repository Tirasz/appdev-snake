package rb.snake.model;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import rb.snake.controller.GameController;

import javax.imageio.ImageIO;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;
import java.util.prefs.Preferences;
import java.util.regex.*;


public class Game {
    //Stores/loads data about the game using the java.util.Preferences, that handles saving and loading from a config file that is platform independent
    public static final double CANVAS_WIDTH = 800;
    public static final double CANVAS_HEIGHT = 500;
    public static final int MIN_BOARD_SIZE = 5; // 5 by 5
    public static final int MAX_BOARD_COLUMNS = 105;
    public static final int MAX_BOARD_ROWS = 65;
    public static final int INITIAL_SNAKE_LENGTH = 3; //3
    public static final int SPECIAL_FOOD_CHANCE = 3; // out of ten
    public static final int MAX_SNAKE_SPEED = 50;
    public static final int HUNGER_COOLDOWN = 250; //500
    public static final int HUNGER_DURATION = 30;
    public static final int CANNIBAL_DURATION = 15;
    public static final PropertyChangeSupport PCS = new PropertyChangeSupport(Game.class);
    public static final Image SNAKE_HEAD = new Image("/assets/snake_head.png");
    public static final Image SNAKE_BODY = new Image("/assets/snake_body.png");
    public static final Image FOOD = new Image("assets/food.png");
    private static final Preferences settings = Preferences.userRoot().node(Game.class.getName());
    private static IntegerProperty BoardColumns = new SimpleIntegerProperty(Game.class,"BOARD_COLUMNS" );
    private static IntegerProperty BoardRows = new SimpleIntegerProperty(Game.class,"BOARD_ROWS" );
    private static boolean currentIsMultiplayer;



    static{
        //Load board dimensions from preferences file.
        setBoardRows(getBoardRows());
        setBoardColumns(getBoardColumns());


    }


    public static class Tile{

        public static final Color BORDER_COLOR = Color.web("#395306");
        public static final Color DEFAULT_COLOR = Color.web("#a9ff00");
        public static NumberBinding tileSize = Bindings.min( Bindings.divide(Game.CANVAS_WIDTH, Game.BoardColumns), Bindings.divide(Game.CANVAS_HEIGHT, Game.BoardRows));
        public static NumberBinding offsetX = Bindings.divide( Bindings.subtract( Game.CANVAS_WIDTH, Bindings.multiply( Game.BoardColumns, tileSize)),2);
        public static NumberBinding offsetY = Bindings.divide( Bindings.subtract( Game.CANVAS_HEIGHT, Bindings.multiply( Game.BoardRows, tileSize)),2);

        public int x, y;

        public Tile(){
            x = -1;
            y = -1;
        }

        public Tile(int x, int y){
            this.x = x;
            this.y = y;
        }

    }

    public static class Food extends Tile{
        public boolean isSpecial;
        public int value;
        private static final Random r = new Random();

        public Food(){
            this.x = r.nextInt(Game.getBoardColumns());
            this.y = r.nextInt(Game.getBoardRows());
            isSpecial = (r.nextInt(10) < Game.SPECIAL_FOOD_CHANCE);
            this.value = (isSpecial)? (r.nextInt(3)+2):1;
        }
    }

    public static void initBoard(){

        GameController.board = new Tile[getBoardColumns()][getBoardRows()];
        for (int x = 0; x < Game.getBoardColumns(); x++ ){
            for(int y = 0; y < Game.getBoardRows(); y++ ){
                GameController.board[x][y] = new Tile(x,y);
            }
        }
        //Init player(s) and food(s)
        GameController.food = new Food[(Game.isMultiPlayer())?2:1];
        int baseX = getBoardColumns() / 2;
        int baseY = getBoardRows() / 2;
        int playerOffset = (Game.isMultiPlayer())? (getBoardColumns()/4):(0);
        GameController.player1 = new Snake(baseX - playerOffset, baseY, Game.getP1Color());
        GameController.food[0] = new Food();
        if(Game.isMultiPlayer()){
            GameController.player2 = new Snake(baseX + playerOffset, baseY, Game.getP2Color());
            GameController.food[1] = new Food();
        }

    }

    // GETTERS AND SETTERS

    public static String getP1Name() {
        return settings.get("p1Name", "");
    }

    public static void setP1Name(String name) throws IllegalArgumentException{
        Record.checkName(name);
        settings.put("p1Name", name);
        System.out.println("Set name for P1: " +settings.get("p1Name", ""));
        String old = "";
        PCS.firePropertyChange("P1Name",old, name);
    }

    public static String getP2Name() {
        return settings.get("p2Name", "");
    }

    public static void setP2Name(String name) throws IllegalArgumentException{
        Record.checkName(name);
        settings.put("p2Name", name);
        System.out.println("Set name for P2: " +settings.get("p2Name", ""));
        String old = "";
        PCS.firePropertyChange("P1Name",old, name);
    }

    public static int getBoardColumns() {
        return settings.getInt("boardColumns", 10);
    }

    public static void setBoardColumns(int columns) throws IllegalArgumentException {
        if(columns < MIN_BOARD_SIZE){
            throw new IllegalArgumentException("The board has to be at least "+ MIN_BOARD_SIZE +" by "+ MIN_BOARD_SIZE);
        }
        if(columns > MAX_BOARD_COLUMNS){
            throw new IllegalArgumentException("The maximum board size is "+ MAX_BOARD_COLUMNS +" by "+ MAX_BOARD_ROWS);
        }
        settings.putInt("boardColumns", columns);
        BoardColumns.set(columns);
        System.out.println("Set boardColumns: " + columns);
    }

    public static int getBoardRows() {
        return settings.getInt("boardRows", 10);
    }

    public static void setBoardRows(int rows) throws IllegalArgumentException{
        if(rows < MIN_BOARD_SIZE){
            throw new IllegalArgumentException("The board has to be at least "+ MIN_BOARD_SIZE +" by "+ MIN_BOARD_SIZE);
        }
        if(rows > MAX_BOARD_ROWS){
            throw new IllegalArgumentException("The maximum board size is "+ MAX_BOARD_COLUMNS +" by "+ MAX_BOARD_ROWS);
        }
        BoardRows.set(rows);
        settings.putInt("boardRows", rows);
        System.out.println("Set boardRows: " + rows);
    }

    public static int getPredefinedSize() {
        return settings.getInt("predefinedSize", 2);
    }

    public static void setPredefinedSize(int size) {
       settings.putInt("predefinedSize", size);
       if(size != 0){
            switch(size){
                case 1:
                    setBoardRows(10);
                    setBoardColumns(16);
                    break;
                case 2:
                    setBoardRows(20);
                    setBoardColumns(32);
                    break;
                case 3:
                    setBoardRows(33);
                    setBoardColumns(53);
            }

       }
    }

    public static int getGameDifficulty() {
        return settings.getInt("gameDifficulty", 2);
    }

    public static void setGameDifficulty(int difficulty) {
        settings.putInt("gameDifficulty", difficulty);
    }

    public static boolean isBoardHasWalls() {
        return settings.getBoolean("boardHasWalls", false);
    }

    public static void setBoardHasWalls(boolean hasWalls) {
        settings.putBoolean("boardHasWalls", hasWalls);

    }

    public static Color getFoodColor() {
       return Color.web(settings.get("foodColor", "#00ff09"));
    }

    public static void setFoodColor(Color color) {
        settings.put("foodColor",toHexString(color));
    }

    public static Color getP1Color() {
        return Color.web(settings.get("p1Color", "#e8741c"));
    }

    public static void setP1Color(Color color) {
        settings.put("p1Color",toHexString(color));
    }

    public static Color getP2Color() {
        return Color.web(settings.get("p2Color", "#11baed"));
    }

    public static void setP2Color(Color color) {
        settings.put("p2Color",toHexString(color));
    }

    public static void setMultiplayer(boolean new_val) {
        System.out.println("Setting " + currentIsMultiplayer + " to: "+ new_val);
        boolean old_val = currentIsMultiplayer;
        PCS.firePropertyChange("isMultiplayer", old_val, new_val);
        currentIsMultiplayer = new_val;
    }

    public static boolean isMultiPlayer(){
        return currentIsMultiplayer;
    }

    private static String format(double val) {
        String in = Integer.toHexString((int) Math.round(val * 255));
        return in.length() == 1 ? "0" + in : in;
    }

    private static String toHexString(Color value) {
        return "#" + (format(value.getRed()) + format(value.getGreen()) + format(value.getBlue()) + format(value.getOpacity()))
                .toUpperCase();
    }



    public static IntegerProperty boardColumnsProperty() {
        return BoardColumns;
    }

    public static IntegerProperty boardRowsProperty() {
        return BoardRows;
    }
}
