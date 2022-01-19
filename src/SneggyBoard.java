public class SneggyBoard
{
    public String type;
    public int value;
    private int[] xy;

    public SneggyBoard(String newType, int newValue, int[] newXY) {
        type = newType;
        value = newValue;
        xy = newXY;
    }

    //getters
    public String getType() {
        return type;
    }
    public int getValue() {
        return value;
    }
    public int[] getXY() {
        return xy;
    }
    public int getX() {
        return xy[0];
    }
    public int getY() {
        return xy[1];
    }

    //setters
    public void setType(String newType) {
       this.type = newType;
    }
    public void setScore(int newValue) {
        this.value = newValue;
    }
    public void setXY(int[] newXY) {
        this.xy = newXY;
    }
    public void setX(int newX) {
        this.xy[0] = newX;
    }
    public void setY(int newY) {
        this.xy[1] = newY;
    }


}