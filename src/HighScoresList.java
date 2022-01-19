public class HighScoresList
{
    private String initials;
    private int score;
    private float level;

    public HighScoresList(String newInitials, int newScore, float newLevel) {
        initials = newInitials;
        score = newScore;
        level = newLevel;
    }

    //getters
    public String getInitials() {
        return initials;
    }
    public int getScore() {
        return score;
    }
    public float getLevel() {
        return level;
    }

    //setters
    public void setInitials(String newInitials) {
        this.initials = newInitials;
    }
    public void setScore(int newScore) {
        this.score = newScore;
    }
    public void setLevel(float newLevel) {
        this.level = newLevel;
    }

}