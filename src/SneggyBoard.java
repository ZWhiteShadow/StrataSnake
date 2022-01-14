public class SneggyBoard
{
    public String type;
    public int value;
    private int[] xy;

    public SneggyBoard(String type, int value, int[] xy) {
        this.type = type;
        this.value = value;
        this.setXy(xy);

    }

    public int[] getXy() {
        return xy;
    }

    public void setXy(int[] xy) {
        this.xy = xy;
    }
}