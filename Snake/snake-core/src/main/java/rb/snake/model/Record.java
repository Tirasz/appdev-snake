package rb.snake.model;

import javafx.beans.property.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Record {

    private StringProperty p1 = new SimpleStringProperty(this, "p1");
    private StringProperty p2 = new SimpleStringProperty(this, "p2");
    private IntegerProperty p1Score = new SimpleIntegerProperty(this, "p1Score");
    private IntegerProperty p2Score = new SimpleIntegerProperty(this, "p2Score");

    public static void checkName(String name) throws IllegalArgumentException{
        Pattern p = Pattern.compile("[^A-Za-z0-9]");
        Matcher m = p.matcher(name);
        if(m.find()){
            throw new IllegalArgumentException("Character name cannot contain special characters or spaces! ("+name+")");
        }
        if(name.length() < 3 || name.length() > 10){
            throw new IllegalArgumentException("Character name must be between 3 and 10 characters long! ("+name+")");
        }

    }

    public String getP1() {
        return p1.get();
    }

    public StringProperty p1Property() {
        return p1;
    }

    public void setP1(String p1) {
        this.p1.set(p1);
    }

    public String getP2() {
        return p2.get();
    }

    public StringProperty p2Property() {
        return p2;
    }

    public void setP2(String p2) {
        this.p2.set(p2);
    }

    public int getP1Score() {
        return p1Score.get();
    }

    public IntegerProperty p1ScoreProperty() {
        return p1Score;
    }

    public void setP1Score(int p1Score) {
        this.p1Score.set(p1Score);
    }

    public int getP2Score() {
        return p2Score.get();
    }

    public IntegerProperty p2ScoreProperty() {
        return p2Score;
    }

    public void setP2Score(int p2Score) {
        this.p2Score.set(p2Score);
    }

    @Override
    public String toString() {
        String str;
        str = "$type record{\n" +
                "p1=" + (getP1() == null ? "null" : getP1().toString()) +
                ", p1Score=" + getP1Score() + "\n";
        if(getP2() != null){
            str += "p2=" + getP2().toString() +
                    ", p2Score=" + getP2Score()+ "\n";
        }
        str +="}";
        return str.replace("$type", getP2()==null?"Singleplayer":"Multiplayer");

    }
}
