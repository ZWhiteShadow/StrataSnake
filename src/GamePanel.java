import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    static final int SCREEN_WIDTH = 1200;
    static final int SIDE_FOR_SCORE = 150;
    static final int BOTTOM_PANEL = 100;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    static final int SQUARES_ACROSS = GAME_UNITS / SCREEN_HEIGHT;
    static final int SQUARES_DOWN = GAME_UNITS / SCREEN_WIDTH;
    static final double TOTAL_SQUARES = SQUARES_ACROSS * SQUARES_DOWN;
    int[] x;
    int[] y;
    int[][] sneggySphere;
    double sneggySpeed; //How fast game is running
    float scoreMultiplierFloat;
    int score;
    float sneggyBodyParts;
    float numbersLeft;
    float displayLevel;
    float level;
    char direction;
    boolean running;
    boolean gameStarted;
    Timer timer;
    Random random;
    boolean step = false;
    int tempAcross;
    int tempDown;

    int solidYellowSquaresCount;
    int hollowYellowSquaresCount;
    int solidRedSquaresCount;
    int hollowRedSquaresCount;

    int difficulty;
    boolean waitingForNextLevel;
    int highScore;
    float scoreLevel;
    int displayPerUnit;
    double displayDanger;
    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH + SIDE_FOR_SCORE, SCREEN_HEIGHT + BOTTOM_PANEL));
        this.setFocusable(true);
        this.setBackground(Color.black);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {
        solidYellowSquaresCount = 0;
        hollowYellowSquaresCount= 0;
        solidRedSquaresCount = 0;
        hollowRedSquaresCount= 0;
        waitingForNextLevel = true;
        difficulty = 0;
        direction = 'D';
        sneggyBodyParts = 11;
        sneggySpeed = 125;
        score = 0;
        running = true;
        scoreMultiplierFloat = 100.0f;
        x = new int[GAME_UNITS];
        y = new int[GAME_UNITS];
        sneggySphere = new int[SQUARES_ACROSS][SQUARES_DOWN];
        Arrays.stream(sneggySphere).forEach(a -> Arrays.fill(a, 0));
        level = 1;
        gameStarted = false;
        numbersLeft = level;
        newLevel(numbersLeft);
        timer = new Timer((int) sneggySpeed, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawNumbers(g);
        drawSneggy(g);
        if (!running || sneggyBodyParts <= 1 || score < 0) {
            running = false;
            gameOver(g, "Sneggy Died!");
        }
        drawGrid(g);
        drawHighScore(g);
        drawBottomPanel(g);
    }

    //set numbers
    public void drawNumbers(Graphics g) {

        g.setFont(new Font("Terminal", Font.PLAIN, 16));

        for (int i = 0; i < SQUARES_ACROSS; i++) {
            for (int j = 0; j < SQUARES_DOWN; j++) {

                //next level white square with white number inside of level
                g.setFont(new Font("Terminal", Font.PLAIN, 16));
                if ((((sneggySphere[i][j] == level) && (gameStarted) && level != 1) || ((sneggySphere[i][j] == level) && (level == 1) && (!gameStarted)))) {
                    g.setColor(Color.white);
                    g.drawRect(i * UNIT_SIZE + 2, j * UNIT_SIZE + 2, UNIT_SIZE - 4, UNIT_SIZE - 4);
                    if (sneggySphere[i][j] > 9) {
                        g.drawString(String.valueOf(sneggySphere[i][j]), (i * UNIT_SIZE) + 3, (j * UNIT_SIZE) + UNIT_SIZE - 7);
                    } else {
                        g.drawString(String.valueOf(sneggySphere[i][j]), (i * UNIT_SIZE) + 8, (j * UNIT_SIZE) + UNIT_SIZE - 7);
                    }

                }

                // empty yellow square
                else if (sneggySphere[i][j] > 100 && sneggySphere[i][j] < 300) { //hollow yellow
                    g.setColor(Color.yellow);
                    if (sneggySphere[i][j] == 101) {
                        g.drawRect(i * UNIT_SIZE + 2, j * UNIT_SIZE + 2, UNIT_SIZE - 4, UNIT_SIZE - 4);
                    } else {
                        g.drawOval(i * UNIT_SIZE + 2, j * UNIT_SIZE + 2, UNIT_SIZE - 2, UNIT_SIZE - 2);
                        if (sneggySphere[i][j] - 100 > 9) {
                            g.drawString(String.valueOf(sneggySphere[i][j] - 100), (i * UNIT_SIZE) + 4, (j * UNIT_SIZE) + UNIT_SIZE - 7);
                        } else {
                            g.drawString(String.valueOf(sneggySphere[i][j] - 100), (i * UNIT_SIZE) + 9, (j * UNIT_SIZE) + UNIT_SIZE - 7);
                        }
                    }
                }

                // yellow crossbones
                else if (( (int) sneggyBodyParts - (sneggySphere[i][j] / -100) <= 1) && (sneggySphere[i][j] <= -100)) { //will kill user
                    g.setFont(new Font("Terminal", Font.PLAIN, UNIT_SIZE));
                    g.setColor(Color.yellow); // String.valueOf(sneggySphere[i][j] / -100)
                    g.drawString("\u2620", (i * UNIT_SIZE), (j * UNIT_SIZE) + UNIT_SIZE); // skull
                    g.setFont(new Font("Terminal", Font.PLAIN, 16));

                }

                // sold yellow square - no number
                else if (sneggySphere[i][j] == -100) { //solid yellow
                    g.setColor(Color.yellow);
                    g.fillRect(i * UNIT_SIZE, j * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                }
                    // solid yellow circle with number
                else if (sneggySphere[i][j] < -100) { // more than 1 yellow
                    g.setColor(Color.yellow.brighter());
                    g.fillOval(i * UNIT_SIZE, j * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                    g.setColor(Color.black);
                    if ((sneggySphere[i][j] / 100) < -9) {
                        g.drawString(String.valueOf(sneggySphere[i][j] / -100), (i * UNIT_SIZE) + 3, (j * UNIT_SIZE) + UNIT_SIZE - 7);
                    } else {
                        g.drawString(String.valueOf(sneggySphere[i][j] / -100), (i * UNIT_SIZE) + 8, (j * UNIT_SIZE) + UNIT_SIZE - 7);
                    }
                }

                // empty red square
                else if (sneggySphere[i][j] == 300) { // one hollow red
                    g.setColor(Color.red);
                    g.drawRect(i * UNIT_SIZE + 2, j * UNIT_SIZE + 2, UNIT_SIZE - 4, UNIT_SIZE - 4);
                }

                // red circle with number not filled in
                else if (sneggySphere[i][j] > 300) { // more than one hollow red
                    g.setFont(new Font("Terminal", Font.PLAIN, 16));
                    g.setColor(Color.red.brighter().brighter());
                    g.drawOval(i * UNIT_SIZE + 2, j * UNIT_SIZE + 2, UNIT_SIZE - 2, UNIT_SIZE - 2);
                    if ((sneggySphere[i][j] / 300) > 9) {
                        g.drawString(String.valueOf(sneggySphere[i][j] / 300), (i * UNIT_SIZE) + 4, (j * UNIT_SIZE) + UNIT_SIZE - 7);
                    } else {
                        g.drawString(String.valueOf(sneggySphere[i][j] / 300), (i * UNIT_SIZE) + 9, (j * UNIT_SIZE) + UNIT_SIZE - 7);
                    }
                }

                // red square with crossbones
                else if ((sneggySphere[i][j] <= -1 && sneggySphere[i][j] >= -99) && (score + (displayPerUnit * level * sneggySphere[i][j]) < 0)) {
                    g.setFont(new Font("Terminal", Font.PLAIN, UNIT_SIZE));
                    g.setColor(Color.red); // String.valueOf(sneggySphere[i][j] * -1)
                    g.drawString("\u2620", (i * UNIT_SIZE), (j * UNIT_SIZE) + UNIT_SIZE - 4); //skull
                    g.setFont(new Font("Terminal", Font.PLAIN, 16));
                }

                // filled in red circle with number
                else if (sneggySphere[i][j] < -1 && sneggySphere[i][j] >= -99) {  // solid red

                    g.setColor(Color.red.brighter().brighter());
                    g.fillOval(i * UNIT_SIZE, j * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                    g.setColor(Color.black);
                    if (sneggySphere[i][j] < -9) {
                        g.drawString(String.valueOf(sneggySphere[i][j] * -1), (i * UNIT_SIZE) + 3, (j * UNIT_SIZE) + UNIT_SIZE - 7);
                    } else {
                        g.drawString(String.valueOf(sneggySphere[i][j] * -1), (i * UNIT_SIZE) + 8, (j * UNIT_SIZE) + UNIT_SIZE - 7);
                    }

                }
                //solid red square no number
                else if (sneggySphere[i][j] < 0) {  // solid red one
                    g.setColor(new Color(247, 33, 25));
                    g.fillRect(i * UNIT_SIZE, j * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                }

                //green smiley face circle
                else if (sneggySphere[i][j] == 1) { //positive one1
                    g.setColor(Color.green.brighter());
                    g.setFont(new Font("Terminal", Font.PLAIN, UNIT_SIZE)); //
                    g.drawString("\u263A", (i * UNIT_SIZE), (j * UNIT_SIZE) + UNIT_SIZE - 3);
                    g.setFont(new Font("Terminal", Font.PLAIN, 16));

                }

                // not filled in green number in a circle
                else if (sneggySphere[i][j] > 0) { //more than one green
                    g.setColor(Color.green);
                    g.drawOval(i * UNIT_SIZE + 2, j * UNIT_SIZE + 2, UNIT_SIZE - 2, UNIT_SIZE - 2);
                    if (sneggySphere[i][j] > 9) {
                        g.drawString(String.valueOf(sneggySphere[i][j]), (i * UNIT_SIZE) + 4, (j * UNIT_SIZE) + UNIT_SIZE - 7);
                    } else {
                        g.drawString(String.valueOf(sneggySphere[i][j]), (i * UNIT_SIZE) + 9, (j * UNIT_SIZE) + UNIT_SIZE - 7);
                    }
                }
            }
        }

        if (level == 1) {
            difficulty = 100;
        } else {
            if (waitingForNextLevel) {
                difficulty = (int) (((hollowRedSquaresCount + solidRedSquaresCount +
                                      hollowYellowSquaresCount + solidYellowSquaresCount) / (((level - 1) * 4))) * 100);
            } else {
                difficulty = (int) (((hollowYellowSquaresCount + solidYellowSquaresCount +
                                      hollowRedSquaresCount   + solidRedSquaresCount ) / (level * 4)) * 100);
            }
        }
    }

    // set snake named SNEGGY
    public void drawSneggy(Graphics g) {
        if (sneggyBodyParts > 1) {
            int colorShift = 255 / (int) sneggyBodyParts;
            for (int i = 0; i < sneggyBodyParts; i++) {
                g.setColor(new Color(255 - (colorShift * i), 0, 255 - (colorShift * i)));
                g.fillRect(x[i] * UNIT_SIZE, y[i] * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
            }
        }
    }

    public void gameOver(Graphics g, String message) {
        //show GAME OVER
        g.setColor(Color.black);
        g.fillRect(15 * UNIT_SIZE, 10 * UNIT_SIZE, 18 * UNIT_SIZE, 3 * UNIT_SIZE);
        g.setColor(Color.red.brighter());
        g.setFont(new Font("Terminal", Font.PLAIN, UNIT_SIZE * 3));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString(message, (SCREEN_WIDTH - metrics.stringWidth(message)) / 2,
                SCREEN_HEIGHT / 2);
    }

    public void drawGrid(Graphics g) {

        g.setColor(new Color(0, 50, 0));
        //draw Vertical Lines on board
        for (int i = 0; i <= SQUARES_ACROSS; i++) {
            g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
        }
        //Draw Horizontal
        for (int i = 0; i <= SQUARES_DOWN; i++) {
            g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
        }
        //Draw Middle circles
        for (int i = 0; i <= SQUARES_ACROSS; i++) {
            for (int j = 0; j <= SQUARES_DOWN; j++) {
                if (((j + i) / 2) % 2 == 0) {
                    g.fillOval(i * UNIT_SIZE - 3, j * UNIT_SIZE - 3, 6, 6);
                } else {
                    g.fillOval(i * UNIT_SIZE - 5, j * UNIT_SIZE - 5, 10, 10);
                }
            }
        }
    }

    //set side for high score table
    public void drawHighScore(Graphics g) {
        g.setColor(Color.lightGray);
        g.fillRect(SCREEN_WIDTH, 0, SCREEN_WIDTH + SIDE_FOR_SCORE, SCREEN_HEIGHT);
        g.setColor(Color.black);
        g.setFont(new Font("Terminal", Font.PLAIN, UNIT_SIZE * 3 / 4));
        g.drawString("High Scores:", SCREEN_WIDTH + (SIDE_FOR_SCORE / 8), UNIT_SIZE);
    }

    //fill in bottom
    public void drawBottomPanel(Graphics g) {

        //draw panel
        g.setColor(Color.darkGray);
        g.fillRect(0, SCREEN_HEIGHT, SCREEN_WIDTH + SIDE_FOR_SCORE, BOTTOM_PANEL);

        // formatting
        g.setColor(Color.magenta);
        DecimalFormat withCommas = new DecimalFormat("#,###");
        DecimalFormat decimal = new DecimalFormat("#,###.##");
        g.setFont(new Font("Terminal", Font.PLAIN, 24));

        // Game Name
        g.drawString("            Introducing \"Sneggy\" In:", 0, SCREEN_HEIGHT + (BOTTOM_PANEL / 2) - 15);
        g.setFont(new Font("Terminal", Font.PLAIN, (int) (UNIT_SIZE * 1.5)));
        g.drawString("       StrataSnake \uD83D\uDC0D", 0, SCREEN_HEIGHT + (BOTTOM_PANEL / 2) + 25);

        displayLevel = level + ((level - numbersLeft) / level);
        int displaySpeed = (int) ((125 / sneggySpeed) * 100);
        displayPerUnit = (int) ((difficulty > 100) ? (int) (scoreMultiplierFloat * (difficulty / 100f)) * (float) (125 / sneggySpeed) : (int) scoreMultiplierFloat * (float) (125 / sneggySpeed));

        //sneggySpeed / Per Unit /  Score
        g.setFont(new Font("Terminal", Font.PLAIN, 24));
        g.setColor(Color.green);
        g.drawString("Level: " + decimal.format(displayLevel) +
                "   Danger: " + decimal.format(displayDanger) + "%" +
                "   Speed: " + withCommas.format(displaySpeed) + "%" +
                "   Length: " + decimal.format(sneggyBodyParts - 1), (SCREEN_WIDTH / 3) - 20, SCREEN_HEIGHT
                + (BOTTOM_PANEL / 2) - 10);

        g.drawString("   Per Green Unit: " + withCommas.format(displayPerUnit) +
                        "   Per Red Unit: " + withCommas.format(displayPerUnit * level * -1),
                SCREEN_WIDTH / 3 + 10, SCREEN_HEIGHT
                        + (BOTTOM_PANEL / 2) + 20);

        g.setColor(Color.magenta);
        g.drawString("Score: " + withCommas.format(score), SCREEN_WIDTH - 200, SCREEN_HEIGHT
                + (BOTTOM_PANEL / 2) - 10);

        if (score > highScore) {
            highScore = score;
            scoreLevel = displayLevel;
        }
        g.setFont(new Font("Terminal", Font.PLAIN, 18));
        g.drawString("High Score: " + withCommas.format(highScore) + " - Level: " + decimal.format(scoreLevel), SCREEN_WIDTH - 200, SCREEN_HEIGHT
                + (BOTTOM_PANEL / 2) + 20);
        g.setFont(new Font("Terminal", Font.PLAIN, 30));
        g.setColor(Color.green);

    }

    public void newLevel(float numberToDisplay) {
        do {
            tempAcross = random.nextInt(SQUARES_ACROSS);
            tempDown = random.nextInt(SQUARES_DOWN);
        } while (sneggySphere[tempAcross][tempDown] < 0 || sneggySphere[tempAcross][tempDown] >= 99);
        sneggySphere[tempAcross][tempDown] += numberToDisplay;
        waitingForNextLevel = true;
    }

    public void hollowYellow(int numToChange, boolean isNew, int add) { // 101 102 203 ect1
        int tempCount = 0;
        for (int i = 0; i < numToChange; i++) {
            do {
                tempAcross = random.nextInt(SQUARES_ACROSS);
                tempDown = random.nextInt(SQUARES_DOWN);
                tempCount++;
                if ((sneggySphere[tempAcross][tempDown] >= 100 && sneggySphere[tempAcross][tempDown] < 200) && // allow to be added on existing square
                        (x[0] != tempAcross && y[0] != tempDown) ){ // don't allow square just hit
                    break;
                }
            }
            while (!isNew || sneggySphere[tempAcross][tempDown] != 0);
            if (sneggySphere[tempAcross][tempDown] == 0 || (sneggySphere[tempAcross][tempDown] == 101 && add == -1)) {
                sneggySphere[tempAcross][tempDown] += 101 * add;
                hollowYellowSquaresCount += add;
            } else if (sneggySphere[tempAcross][tempDown] < 200) {
                sneggySphere[tempAcross][tempDown] += add;
            }
        }
    }

    public void solidYellow(int numToChange, boolean isNew, int add) { // -100, -200, -300 ect
        int tempCount = 0 ;
        for (int i = 0; i < numToChange; i++) {
            do {
                tempAcross = random.nextInt(SQUARES_ACROSS);
                tempDown = random.nextInt(SQUARES_DOWN);
                tempCount++;
                if ((sneggySphere[tempAcross][tempDown] / -100 > 0) && // allow to be added on existing square
                        (x[0] != tempAcross && y[0] != tempDown)){ // don't allow square just hit
                    break;
                }
            } while (!isNew || sneggySphere[tempAcross][tempDown] != 0);
            if (sneggySphere[tempAcross][tempDown] == 0) {
                solidYellowSquaresCount ++;
            }
            sneggySphere[tempAcross][tempDown] -= 100 * add;
            if (sneggySphere[tempAcross][tempDown] == 0) {
                solidYellowSquaresCount--;
            }
        }
    }

    public void hollowRed(int numToChange, boolean isNew, int add) { // 300 600 900 ect
        int tempCount = 0;
        for (int i = 0; i < numToChange; i++) {
            do {
                tempAcross = random.nextInt(SQUARES_ACROSS);
                tempDown = random.nextInt(SQUARES_DOWN);
                tempCount++;
                if (sneggySphere[tempAcross][tempDown] >= 300 &&  // allow to be added on existing square
                        (x[0] != tempAcross && y[0] != tempDown)) { // don't allow square just hit
                    break;
                }
            } while (!isNew || sneggySphere[tempAcross][tempDown] != 0);
            if (sneggySphere[tempAcross][tempDown] == 0) {
                hollowRedSquaresCount ++;
            }
            sneggySphere[tempAcross][tempDown] += 300 * add;
            if (sneggySphere[tempAcross][tempDown] == 0) {
                hollowRedSquaresCount--;
            }
        }
    }

    public void solidRed(int numToChange, boolean isNew, int add) { // - 1 -2 -3 ect
        int tempCount = 0;
        for (int i = 0; i < numToChange; i++) {
            do {
                tempAcross = random.nextInt(SQUARES_ACROSS);
                tempDown = random.nextInt(SQUARES_DOWN);
                tempCount++;
                if ( (sneggySphere[tempAcross][tempDown] > -100 && sneggySphere[tempAcross][tempDown] < 0) &&  // allow to be added on existing square
                        (x[0] != tempAcross && y[0] != tempDown)) { //don't allow square just hit
                    break;
                }
            } while (!isNew || sneggySphere[tempAcross][tempDown] != 0);
            if (sneggySphere[tempAcross][tempDown] == 0) {
                solidRedSquaresCount ++;
            }
            sneggySphere[tempAcross][tempDown] -= add;
            if (sneggySphere[tempAcross][tempDown] == 0) {
                solidRedSquaresCount--;
            }
        }
    }

    public void goodSquare(int numToChange) {
        do {
            tempAcross = random.nextInt(SQUARES_ACROSS);
            tempDown = random.nextInt(SQUARES_DOWN);
        } while ((sneggySphere[tempAcross][tempDown] < 0 || sneggySphere[tempAcross][tempDown] > 99) && // zero or existing
                (x[0] != tempAcross && y[0] != tempDown) ); //don't allow square on
        sneggySphere[tempAcross][tempDown] += numToChange;
    }

    public void updateDanger(){
        displayDanger = ((((double) hollowRedSquaresCount + (double) solidRedSquaresCount + (double) hollowYellowSquaresCount +  (double)  solidYellowSquaresCount) / TOTAL_SQUARES) * 100d);
    }

    public void newNumbers(int numberHit) {

        if ((numberHit == level && level > 1) || (level == 1 && !gameStarted)) { //level hit
            waitingForNextLevel = false;
            for (int i = 0; i < level; i++) { // one extra of each per level
                hollowYellow(1, true, 1);
                solidYellow(1, true, 1);
                hollowRed(1, true, 1);
                solidRed(1, true, 1);
                updateDanger();
            }
        }

        if ((numberHit > 100) && (numberHit < 200)) { // hollow yellow is hit
            hollowYellowSquaresCount--;
            sneggyBodyParts += ((numberHit-100f ) / 10f);
            if (hollowYellowSquaresCount == 0) {
                solidYellow(numberHit - 100, false, -1);
            } else {
                hollowYellow((numberHit - 100) + 1, false, 1);
                solidYellow(1, false, 1);
            }
            updateDanger();
            return;
        }

        if (numberHit / -100 > 0) { //solid yellow is hit
            solidYellowSquaresCount--;
            sneggyBodyParts -= (numberHit / -100f); //  1 less body parts per unit
            if (solidYellowSquaresCount == 0) {
                hollowYellow(numberHit / -100, false, -1);
            } else {
                hollowYellow(1, false, 1);
                solidYellow((numberHit / -100) + 1, false, 1);
            }
            updateDanger();
            return;
        }

        if (numberHit >= 300) { // hollow red is hit
            hollowRedSquaresCount--;
            score += (displayPerUnit * level / 10);
            if (hollowRedSquaresCount == 0) {
                solidRed(numberHit / 300, false, -1);
            } else {
                hollowRed((numberHit / 300) + 1, false, 1);
                solidRed(1, false, 1);
            }
            updateDanger();
            return;
        }

        if (numberHit < 0) {  //solid red is hit
            solidRedSquaresCount--;
            if (solidRedSquaresCount == 0) {
                hollowRed(numberHit * -1, false, -1);
            } else{
                hollowRed(1, false, 1);
                solidRed((numberHit * -1)  + 1, false, 1);
            }
            updateDanger();
        }

        if (numberHit == 1) {
            if (level == 1 && !gameStarted) {
                goodSquare(1);
                gameStarted = true;
                waitingForNextLevel = false;
                numbersLeft++;
            }

            numbersLeft--;
            // all greens removed on current level
            if (numbersLeft == 0) {
                level++;
                numbersLeft = level;
                newLevel(numbersLeft);
            }
        }

        if (numberHit > 1) {

            int tempNumberToDisplay = numberHit;
            int numberToSplit = tempNumberToDisplay / 2;
            for (int i = 0; i < 2; i++) {
                goodSquare(numberToSplit);
                tempNumberToDisplay = tempNumberToDisplay - numberToSplit;
            }
            goodSquare(tempNumberToDisplay);
        }

            if (numberHit < 0) {
                score += (displayPerUnit * level * -1);
            } else if ((numberHit != level) || (level == 1 && gameStarted)) {
                score += displayPerUnit;
            }
    }

    public void newSpeed() {
        if (running) {
            if (sneggySpeed > 250) {
                sneggySpeed = 250;
            }
            if (sneggySpeed < 25) {
                sneggySpeed = 25;
            }
            timer.stop();
            timer.setDelay((int) sneggySpeed);
            timer.start();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
        }
        repaint();


     }

    public void move() {

        for (int i = (int) sneggyBodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U' -> y[0] = y[0] - 1;
            case 'D' -> y[0] = y[0] + 1;
            case 'L' -> x[0] = x[0] - 1;
            case 'R' -> x[0] = x[0] + 1;

            case '7' -> {  // UP Left
                if (!step) {
                    y[0] = y[0] - 1;
                    step = true;
                } else {
                    newSpeed();
                    x[0] = x[0] - 1;
                    step = false;
                }
            }
            case '1' -> {  // UP Left
                if (!step) {
                    y[0] = y[0] + 1;
                    step = true;
                } else {
                    x[0] = x[0] - 1;
                    step = false;
                }
            }
            case '9' -> {  // UP Right
                if (!step) {
                    y[0] = y[0] - 1;
                    step = true;
                } else {
                    newSpeed();
                    x[0] = x[0] + 1;
                    step = false;
                }
            }
            case '3' -> {  // Down Right
                if (!step) {
                    y[0] = y[0] + 1;
                    step = true;
                } else {
                    newSpeed();
                    x[0] = x[0] + 1;
                    step = false;
                }
            }
        }

        //Loop around sides
        if (x[0] < 0) {
            x[0] += SQUARES_ACROSS;
        }
        if (x[0] >= SQUARES_ACROSS) {
            x[0] = 0;
        }
        if (y[0] < 0) {
            y[0] += SQUARES_DOWN;
        }
        if (y[0] >= SQUARES_DOWN) {
            y[0] = 0;
        }

        if (sneggySphere[x[0]][y[0]] != 0) {
            newNumbers(sneggySphere[x[0]][y[0]]);
            sneggySphere[x[0]][y[0]] = 0;
            slowerSpeed();
        }
    }

    public void slowerSpeed() {
        if (sneggySpeed < 250) {
            sneggySpeed += 5;
            newSpeed();
        }
    }

    public void fasterSpeed() {
        if (sneggySpeed > 25) {
            sneggySpeed -= 5;
            newSpeed();
        }
    }


    public class MyKeyAdapter extends KeyAdapter {
        @Override

        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_A:
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_NUMPAD4:
                    if (direction == 'R' || direction == '7' || direction == '1') {
                        slowerSpeed();
                    }
                    if (direction == 'L' || direction == '9' || direction == '3') {
                        fasterSpeed();
                    }
                    direction = 'L';
                    break;

                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:
                case KeyEvent.VK_NUMPAD6:
                    if (direction == 'R' || direction == '7' || direction == '1') {
                        fasterSpeed();
                    }
                    if (direction == 'L' || direction == '9' || direction == '3') {
                        slowerSpeed();
                    }
                    direction = 'R';
                    break;

                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:
                case KeyEvent.VK_NUMPAD8:
                    if (direction == 'U' || direction == '7' || direction == '9') {
                        fasterSpeed();
                    }
                    if (direction == 'D' || direction == '1' || direction == '3') {
                        slowerSpeed();
                    }
                    direction = 'U';
                    break;

                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_X:
                case KeyEvent.VK_NUMPAD2:
                case KeyEvent.VK_NUMPAD5:
                    if (direction == 'U' || direction == '7' || direction == '9') {
                        slowerSpeed();
                    }
                    if (direction == 'D' || direction == '1' || direction == '3') {
                        fasterSpeed();
                    }
                    direction = 'D';
                    break;

                case KeyEvent.VK_ENTER: //pause game for testing
                    if (timer.isRunning()) {
                        sneggySpeed = 120;
                        newSpeed();
                        timer.stop();
                    } else {
                        timer.start();
                    }
                    break;

                case KeyEvent.VK_SPACE: //start new game
                    timer.stop();
                    startGame();
                    break;

                //Diagonals
                case KeyEvent.VK_NUMPAD7:
                case KeyEvent.VK_Q:
                case KeyEvent.VK_HOME:
                    if (direction == '3' || direction == 'D' || direction == 'R') {
                        slowerSpeed();
                    }
                    if (direction == '7' || direction == 'U' || direction == 'L') {
                        fasterSpeed();
                    }
                    direction = '7';
                    break;

                case KeyEvent.VK_NUMPAD9:
                case KeyEvent.VK_E:
                case KeyEvent.VK_PAGE_UP:
                    if (direction == '9' || direction == 'U' || direction == 'R') {
                        fasterSpeed();
                    }
                    if (direction == '1' || direction == 'D' || direction == 'L') {
                        slowerSpeed();
                    }
                    direction = '9';
                    break;

                case KeyEvent.VK_NUMPAD1:
                case KeyEvent.VK_Z:
                case KeyEvent.VK_END:
                    if (direction == '9' || direction == 'U' || direction == 'R') {
                        slowerSpeed();
                    }
                    if (direction == '1' || direction == 'D' || direction == 'L') {
                        fasterSpeed();
                    }
                    direction = '1';
                    break;

                case KeyEvent.VK_NUMPAD3:
                case KeyEvent.VK_C:
                case KeyEvent.VK_PAGE_DOWN:
                    if (direction == '3' || direction == 'D' || direction == 'R') {
                        fasterSpeed();
                    }
                    if (direction == '7' || direction == 'U' || direction == 'L') {
                        slowerSpeed();
                    }
                    direction = '3';
                    break;
            }
        }
    }
}



