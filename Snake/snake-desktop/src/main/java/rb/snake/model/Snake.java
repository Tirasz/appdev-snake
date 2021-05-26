package rb.snake.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.beans.PropertyChangeSupport;

public class Snake {

    public static class SnakeDeadException extends Exception {
        public Snake deadSnake;

        public SnakeDeadException(String msg, Snake snk) {
            super(msg);
            deadSnake = snk;
        }
    }

    public static class SnakeEatenException extends Exception {
        public SnakeEatenException(String msg) {
            super(msg);
        }
    }

    public class SnakePiece extends Game.Tile {
        public int full;
        public SnakePiece parent;
        public SnakePiece child;

        public SnakePiece(int x, int y) {
            super(x, y);
            this.full = 0;
        }

        private void append(int x, int y) {
            SnakePiece new_piece = new SnakePiece(x, y);
            // tail should be the last node
            // The new node's parent is the last node
            new_piece.parent = tail;
            // And the new node is the child of the last node
            tail.child = new_piece;
            // The new node is also the new tail
            tail = new_piece;
        }

        private void append(SnakePiece new_piece) {
            new_piece.parent = tail;
            tail.child = new_piece;
            tail = new_piece;
        }
    }

    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT;

        public Direction opposite() {
            switch (this) {
                case UP:
                    return DOWN;
                case DOWN:
                    return UP;
                case LEFT:
                    return RIGHT;
                case RIGHT:
                    return LEFT;
                default:
                    return null;
            }
        }
    }

    public int currentSpeed;
    public Color snakeColor;
    public SnakePiece head;
    public SnakePiece tail;
    public Direction currentDirection;
    public int score;
    public int hungerCooldown;
    public boolean cannibalIsOnCooldown;
    public int hungerActive;
    public int cannibalActive;

    public Snake(int headX, int headY, Color color) {
        snakeColor = color;
        head = new SnakePiece(headX, headY);
        tail = head;
        for (int i = 1; i < Game.INITIAL_SNAKE_LENGTH; i++) {
            tail.append(headX, headY + i);
        }
        currentSpeed = Game.getGameDifficulty();
        currentDirection = Direction.UP;
        score = 0;
        hungerCooldown = Game.HUNGER_COOLDOWN;
        cannibalIsOnCooldown = true;
        hungerActive = 0;
        cannibalActive = 0;

    }

    public void update() throws SnakeDeadException{
        //  If the snake's alive, go through the array from TAIL to HEAD:
        //      --If the TAIL has a FLAG above 0, then DECREASE FLAG by 1 and CREATE NEW PIECE
        //      -- if parent has FLAG above ZERO then DECREASE parent's FLAG by 1, INCREASE self FLAG by 1
        // the head should 'MOVE' in the 'DIRECTION' the snake is facing.
        // every other piece should follow its 'parent': The coordinate pair that was before him in the array.

        SnakePiece current = tail;
        SnakePiece newNode = null;

        while (current.parent != null) {
            if (current == tail && current.full > 0) {
                System.out.println("Tail full: " + current.full);
                current.full--;
                newNode = new SnakePiece(current.x, current.y);
            }
            if (current.parent.full > 0) {
                current.parent.full--;
                current.full++;
            }
            current.x = current.parent.x;
            current.y = current.parent.y;

            current = current.parent;
        }
        //edgecase
        if(tail == head){
            if(tail.full > 0){
                tail.full--;
                tail.append(new SnakePiece(current.x, current.y));
            }
        }
        if (newNode != null) {
            tail.append(newNode);
            this.score++;
        }
        move();
        //this.score++;
        reduceCooldown();


    }

    private void move() throws SnakeDeadException {
        switch (currentDirection) {
            case UP:
                head.y--;
                break;
            case DOWN:
                head.y++;
                break;
            case RIGHT:
                head.x++;
                break;
            case LEFT:
                head.x--;
                break;
            default:
                break;
        }
        if (head.x >= Game.getBoardColumns() || head.y >= Game.getBoardRows() || head.x < 0 || head.y < 0) {
            //Snake has left the board
            if (Game.isBoardHasWalls()) {
                throw new SnakeDeadException("A snake has left the board!", this);
            } else {
                head.x = (head.x < 0) ? (Game.getBoardColumns() - 1) : (head.x % Game.getBoardColumns());
                head.y = (head.y < 0) ? (Game.getBoardRows() - 1) : (head.y % Game.getBoardRows());
            }
        }
    }

    private void accelerate() {
        this.currentSpeed++;
        if (currentSpeed > Game.MAX_SNAKE_SPEED) {
            currentSpeed = Game.MAX_SNAKE_SPEED;
        }
    }

    private void decelerate() {
        this.currentSpeed--;
        if (this.currentSpeed < Game.getGameDifficulty()) {
            currentSpeed = Game.getGameDifficulty();
        }
    }

    public void setCurrentDirection(Direction d) {

        if (d == this.currentDirection) {
            if (Game.isMultiPlayer()) {
                accelerate();
            }
            return;
        }
        if (d.opposite() == currentDirection) {
            if (Game.isMultiPlayer()) {
                decelerate();
            }
            return;
        }
        currentDirection = d;
    }

    public void checkCollision(Snake other) throws SnakeDeadException, SnakeEatenException{
        boolean ateSelf = (other.head == this.head);
        SnakePiece otherCurrent =  ateSelf? (this.head.child) : (other.head);
        while (otherCurrent != null) {

            if (this.head.x == otherCurrent.x && this.head.y == otherCurrent.y) {
                //COLLISION

                if( (ateSelf && hungerActive == 0) || (!ateSelf && cannibalActive == 0) || (otherCurrent == other.head)) {
                    //Ha magát ette meg hunger nélkül, vagy másba ütközött és nincs kannibál, vagy egymás fejébe ütköztek
                    throw new SnakeDeadException("Collision", this);
                }
                else{
                    //this.hungerCooldown = Game.HUNGER_COOLDOWN;
                    this.hungerActive = 0;
                    int eaten = 1;
                    //count the pieces eaten
                    SnakePiece temp = otherCurrent;
                    while(temp.child != null){
                        eaten++;
                        temp = temp.child;
                    }
                    //cut the other snake
                    other.tail = otherCurrent.parent;
                    other.tail.child = null;
                    otherCurrent = null;
                    //modify points
                    other.score -= eaten;
                    this.score += eaten;

                    throw new SnakeEatenException("nothing");
                }
            }

            otherCurrent = otherCurrent.child;
        }
    }

    public int getScore() {
        return score;
    }

    public void reduceCooldown(){
        this.hungerCooldown--;
        if(hungerCooldown < 0){
            hungerCooldown = 0;
        }
        this.hungerActive--;
        if(hungerActive < 0){
            hungerActive = 0;
        }
        this.cannibalActive--;
        if(cannibalActive < 0){
            cannibalActive = 0;
        }
    }

}
