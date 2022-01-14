import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
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
    int[] x; // x coordinates of sneggy
    int[] y; // y coordinates of sneggy
    double sneggySpeed; //How fast game is running
    float scoreMultiplierFloat; //score bonus
    int score;
    float sneggyBodyParts;
    float numbersLeft; // units left before entering next level
    float displayLevel; // level user is on including part level 1.3 ect
    float level; // only whole level user is on level 1, 2, 3 ect
    char direction; // (U)p (D)own (L)eft (R)ight
    // keypad diagonals
    //  UL (7)   (9) UR
    //  DL (1)   (3) DR
    boolean running; // is the game running
    boolean gameStarted; // has the first level started - first number one is hit
    Timer timer;
    Random random;
    boolean step = false; // alternating pattern used for diagonals

    int difficulty; // used for bonus - based on number of negatives on board vs amount normal for level
    boolean waitingForNextLevel; // is the white level number on board - are we waiting to hit it
    int highScore; // highest score on game instance
    float scoreLevel; // level at which got the highest score
    int displayPerUnit; // shown number received for hitting positive units
    double displayDanger; // how much of the board is full

    ArrayList<SneggyBoard> squares = new ArrayList<>(); // list holding board dimensions and what squares are where
    GamePanel() {
        random = new Random(); //setup random numbers
        //full board size
        this.setPreferredSize(new Dimension(SCREEN_WIDTH + SIDE_FOR_SCORE, SCREEN_HEIGHT + BOTTOM_PANEL));
        this.setFocusable(true);
        this.setBackground(Color.black);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {
        squares.clear(); //clear past games locations
        for (int j = 0; j < 24; j++) {  //Set up board values as Good Or Safe spaces with 0 values
            for (int i = 0; i < 48; i++) {
                squares.add(new SneggyBoard("E", 0, new int[]{i, j})); //E for empty
            }
        }
        difficulty = 0;
        direction = 'D';
        sneggyBodyParts = 11;
        sneggySpeed = 125;
        score = 0;
        running = true;
        scoreMultiplierFloat = 100.0f;
        x = new int[GAME_UNITS];
        y = new int[GAME_UNITS];
        level = 1;
        gameStarted = false;
        numbersLeft = level;
        changeAtXY(getRandomFilteredXY("E"), "G", (int) numbersLeft); //change first square to be white level number
        waitingForNextLevel = true;
        timer = new Timer((int) sneggySpeed, this);
        timer.start();
    }

    public int[] getRandomFilteredXY(String filter) {  //get Random XY of specific type
        int[] xy;
        // create new list with only FILTER types
        java.util.List<SneggyBoard> result = squares.stream().filter(item -> Objects.equals(item.type, filter)).toList();
        int randomLocation = random.nextInt(result.size()); //Find item on shorter list
        xy = new int[2];
        xy[0] = result.get(randomLocation).getXy()[0]; // find x value
        xy[1] = result.get(randomLocation).getXy()[1]; // Find y value
        return xy;
    }

    public void changeAtXY(int[] xy, String newType, int valueChange) {
        int oldValue = getValueAtXY(xy[0], xy[1]); // find current value
        squares.set((xy[1] * 48) + xy[0], //index instead of x y
                new SneggyBoard(newType, oldValue + valueChange, new int[]{xy[0], xy[1]})); //replace with new object
        displayDanger = ((TOTAL_SQUARES - countType("E")) / TOTAL_SQUARES) * 4d * 100d; // update how many squares on board
    }

    public int countType(String countType) { //count number of a type
        //  "E" Empty (0) // "G" Good (positive 1-99)
        // "HR" Hollow Red (300, 600, 900 ect) // "SR" Solid Red (negative 1-99)
        // "HY" Hollow Yellow (101-199) // "SY" Solid Yellow (-100, -200, -300 ect)

        //count number of certain type
        java.util.List<SneggyBoard> result = squares.stream() .filter(item -> Objects.equals(item.type, countType)).toList();
        return result.size(); //get number based on size of list
    }

    public int getValueAtXY(int x, int y) { //get value on x y
        //filters for x and y - 1 item list - gets value of that item
        return squares.stream().filter(item -> Arrays.equals(item.getXy(), new int[]{x, y})).toList().get(0).value;
    }

    public int countNumberAtMax(String type, int changeValue) { //count number of a type
        int squareValue;
        int numberAtMax = 0;
        for (SneggyBoard square : squares) {
            //account for different value system
            squareValue = (type.equals("HY")) ? square.value - 100 : square.value / changeValue;
            if (squareValue == 99) // equals max amount
                numberAtMax++; //increase count by one
        }
        return numberAtMax;
    }

    public void changeSquare(String typeToChange, int baseValue, int changeValue, int numToChange, boolean isNew, boolean add) {
        int squareValue;
        int[] tempValue;
        for (int i = 0; i < numToChange; i++) {
            if (isNew) {  // should it be added to a new square
                changeAtXY(getRandomFilteredXY("E"), typeToChange, baseValue); // add to empty square
            } else {
                if (add) {  // add to existing one
                    if (countType(typeToChange) == countNumberAtMax(typeToChange, changeValue)) { //if only numbers left are 99
                        changeAtXY(getRandomFilteredXY("E"), typeToChange, baseValue); // add to empty square
                    } else {
                        do {
                            tempValue = getRandomFilteredXY(typeToChange); // get xy
                            squareValue = (typeToChange.equals("HY")) ? getValueAtXY(tempValue[0], tempValue[1]) - 100 :
                                    getValueAtXY(tempValue[0], tempValue[1]) / changeValue; //find value of square
                        } while (squareValue >= 99); // find one that is not 99
                        changeAtXY(tempValue, typeToChange, changeValue); // add to it
                    }
                } else {  // remove from existing
                    tempValue = getRandomFilteredXY(typeToChange); // get random xy
                    if (getValueAtXY(tempValue[0], tempValue[1]) == baseValue) {
                        changeAtXY(tempValue, "E", -1 * baseValue); // if value == 1 remove square
                    } else {
                        changeAtXY(tempValue, typeToChange, -1 * changeValue); // if higher reduce
                    }
                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawNumbers(g); //colored squares and symbols
        drawSneggy(g); //snake
        if (level == 100){
            gameOver(g, "Sneggy Won!", Color.red.brighter()); //display win message
        } else if (!running || //program has stopped
                sneggyBodyParts <= 1 || // snake reaches only head
                score < 0 || //score drops below zero
                (countType("E") == SQUARES_DOWN * SQUARES_ACROSS) // or board fills up completely
        ) {
            running = false; // stop program
            gameOver(g, "Sneggy Died!", Color.red.brighter()); //display game message
        }
        drawGrid(g);
        drawHighScore(g);
        drawBottomPanel(g);
    }

    //set numbers
    public void drawNumbers(Graphics g) {
        for (int i = 0; i < SQUARES_ACROSS; i++) {
            for (int j = 0; j < SQUARES_DOWN; j++) {

                int squareValue = getValueAtXY(i, j);
                //next level white square with white number inside of level 1-99
                g.setFont(new Font("Terminal", Font.PLAIN, 16));
                if ((((squareValue == level) && (gameStarted) && level != 1) || ((squareValue == level) && (level == 1) && (!gameStarted)))) {
                    g.setColor(Color.white);
                    //DRAW command draws an unfilled shape
                    g.drawRect(i * UNIT_SIZE + 2, j * UNIT_SIZE + 2, UNIT_SIZE - 4, UNIT_SIZE - 4);
                    if (squareValue > 9) {
                        g.drawString(String.valueOf(squareValue), (i * UNIT_SIZE) + 3, (j * UNIT_SIZE) + UNIT_SIZE - 7);
                    } else {
                        g.drawString(String.valueOf(squareValue), (i * UNIT_SIZE) + 8, (j * UNIT_SIZE) + UNIT_SIZE - 7);
                    }

                }

                // empty yellow square
                else if (squareValue > 100 && squareValue < 200) { //hollow yellow 101-199
                    g.setColor(Color.yellow);
                    if (squareValue == 101) {
                        g.drawRect(i * UNIT_SIZE + 2, j * UNIT_SIZE + 2, UNIT_SIZE - 4, UNIT_SIZE - 4);
                    } else {
                        g.drawOval(i * UNIT_SIZE + 2, j * UNIT_SIZE + 2, UNIT_SIZE - 2, UNIT_SIZE - 2);
                        if (squareValue - 100 > 9) {
                            g.drawString(String.valueOf(squareValue - 100), (i * UNIT_SIZE) + 4, (j * UNIT_SIZE) + UNIT_SIZE - 7);
                        } else {
                            g.drawString(String.valueOf(squareValue - 100), (i * UNIT_SIZE) + 9, (j * UNIT_SIZE) + UNIT_SIZE - 7);
                        }
                    }
                }

                // yellow crossbones
                else if (((int) sneggyBodyParts - (squareValue / -100) <= 1) && (squareValue <= -100)) { //will kill user
                    g.setFont(new Font("Terminal", Font.PLAIN, UNIT_SIZE));
                    g.setColor(Color.yellow); // String.valueOf(sneggySphere(i,j) / -100)
                    g.drawString("\u2620", (i * UNIT_SIZE), (j * UNIT_SIZE) + UNIT_SIZE); // skull
                    g.setFont(new Font("Terminal", Font.PLAIN, 16));

                }

                // sold yellow square - no number
                else if (squareValue == -100) { //solid yellow
                    g.setColor(Color.yellow);
                    g.fillRect(i * UNIT_SIZE, j * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                }
                // solid yellow circle with number
                else if (squareValue < -100) { // more than 1 yellow
                    g.setColor(Color.yellow.brighter());
                    g.fillOval(i * UNIT_SIZE, j * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                    g.setColor(Color.black);
                    if ((squareValue / 100) < -9) {
                        g.drawString(String.valueOf(squareValue / -100), (i * UNIT_SIZE) + 3, (j * UNIT_SIZE) + UNIT_SIZE - 7);
                    } else {
                        g.drawString(String.valueOf(squareValue / -100), (i * UNIT_SIZE) + 8, (j * UNIT_SIZE) + UNIT_SIZE - 7);
                    }
                }

                // empty red square
                else if (squareValue == 300) { // one hollow red
                    g.setColor(Color.red);
                    g.drawRect(i * UNIT_SIZE + 2, j * UNIT_SIZE + 2, UNIT_SIZE - 4, UNIT_SIZE - 4);
                }

                // red circle with number not filled in
                else if (squareValue > 300) { // more than one hollow red
                    g.setFont(new Font("Terminal", Font.PLAIN, 16));
                    g.setColor(Color.red.brighter().brighter());
                    g.drawOval(i * UNIT_SIZE + 2, j * UNIT_SIZE + 2, UNIT_SIZE - 2, UNIT_SIZE - 2);
                    if ((squareValue / 300) > 9) {
                        g.drawString(String.valueOf(squareValue / 300), (i * UNIT_SIZE) + 4, (j * UNIT_SIZE) + UNIT_SIZE - 7);
                    } else {
                        g.drawString(String.valueOf(squareValue / 300), (i * UNIT_SIZE) + 9, (j * UNIT_SIZE) + UNIT_SIZE - 7);
                    }
                }

                // red square with crossbones
                else if ((squareValue <= -1) && (score + (displayPerUnit * level * squareValue) < 0)) {
                    g.setFont(new Font("Terminal", Font.PLAIN, UNIT_SIZE));
                    g.setColor(Color.red); // String.valueOf(sneggySphere(i,j) * -1)
                    g.drawString("\u2620", (i * UNIT_SIZE), (j * UNIT_SIZE) + UNIT_SIZE - 4); //skull
                    g.setFont(new Font("Terminal", Font.PLAIN, 16));
                }

                // filled in red circle with number
                else if (squareValue < -1) {  // solid red

                    g.setColor(Color.red.brighter().brighter());
                    g.fillOval(i * UNIT_SIZE, j * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                    g.setColor(Color.black);
                    if (squareValue < -9) {
                        g.drawString(String.valueOf(squareValue * -1), (i * UNIT_SIZE) + 3, (j * UNIT_SIZE) + UNIT_SIZE - 7);
                    } else {
                        g.drawString(String.valueOf(squareValue * -1), (i * UNIT_SIZE) + 8, (j * UNIT_SIZE) + UNIT_SIZE - 7);
                    }

                }

                //solid red square no number
                else if (squareValue < 0) {  // solid red one
                    g.setColor(new Color(247, 33, 25));
                    g.fillRect(i * UNIT_SIZE, j * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                }

                //green smiley face circle
                else if (squareValue == 1) { //positive one1
                    g.setColor(Color.green.brighter());
                    g.setFont(new Font("Terminal", Font.PLAIN, UNIT_SIZE)); //
                    g.drawString("\u263A", (i * UNIT_SIZE), (j * UNIT_SIZE) + UNIT_SIZE - 3);
                    g.setFont(new Font("Terminal", Font.PLAIN, 16));

                }

                // not filled in green number in a circle
                else if (squareValue > 0) { //more than one green
                    g.setColor(Color.green);
                    g.drawOval(i * UNIT_SIZE + 2, j * UNIT_SIZE + 2, UNIT_SIZE - 2, UNIT_SIZE - 2);
                    if (squareValue > 9) {
                        g.drawString(String.valueOf(squareValue), (i * UNIT_SIZE) + 4, (j * UNIT_SIZE) + UNIT_SIZE - 7);
                    } else {
                        g.drawString(String.valueOf(squareValue), (i * UNIT_SIZE) + 9, (j * UNIT_SIZE) + UNIT_SIZE - 7);
                    }
                }
            }
        }

        if (level == 1) {
            difficulty = 100;
        } else {
            if (waitingForNextLevel) {
                difficulty = (int) (((countType("HR") + countType("SR") +
                        countType("HY") + countType("SY")) / (((level - 1) * 4))) * 100);
            } else {
                difficulty = (int) (((countType("HY") + countType("SY") +
                        countType("HR") + countType("SR")) / (level * 4)) * 100);
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

    public void gameOver(Graphics g, String message, Color color) {
        //show GAME OVER
        g.setColor(Color.black);
        g.fillRect(15 * UNIT_SIZE, 10 * UNIT_SIZE, 18 * UNIT_SIZE, 3 * UNIT_SIZE);
        g.setColor(color);
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
        DecimalFormat decimal = new DecimalFormat("#,###.#");
        g.setFont(new Font("Terminal", Font.PLAIN, 20));

        // Game Name
        g.drawString("            Introducing \"Sneggy\" In:", 0, SCREEN_HEIGHT + (BOTTOM_PANEL / 2) - 15);
        g.setFont(new Font("Terminal", Font.PLAIN, (int) (UNIT_SIZE * 1.5)));
        g.drawString("       StrataSnake \uD83D\uDC0D", 0, SCREEN_HEIGHT + (BOTTOM_PANEL / 2) + 25);

        displayLevel = level + ((level - numbersLeft) / level);
        int displaySpeed = (int) ((125 / sneggySpeed) * 100);
        displayPerUnit = (int) ((difficulty > 100) ? (int) (scoreMultiplierFloat * (difficulty / 100f)) * (float) (125 / sneggySpeed) : (int) scoreMultiplierFloat * (float) (125 / sneggySpeed));

        //sneggySpeed / Per Unit /  Score
        g.setFont(new Font("Terminal", Font.PLAIN, 20));
        g.setColor(Color.green);
        g.drawString("Level: " + decimal.format(displayLevel) +
                "   Danger: " + decimal.format(displayDanger) + "%" +
                "   Speed: " + withCommas.format(displaySpeed) + "%" +
                "   Length: " + decimal.format(sneggyBodyParts - 1), (SCREEN_WIDTH / 3) + 40, SCREEN_HEIGHT
                + (BOTTOM_PANEL / 2) - 10);

        g.drawString("   Per Green Unit: " + withCommas.format(displayPerUnit) +
                        "   Per Red Unit: " + withCommas.format(displayPerUnit * level * -1),
                (SCREEN_WIDTH / 3) + 60, SCREEN_HEIGHT
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

    public void newNumbers(int numberHit) {

        if ((numberHit == level && level > 1) || (level == 1 && !gameStarted)) { //level hit
            waitingForNextLevel = false;
            for (int i = 0; i < level; i++) { // one extra of each per level
                changeSquare("HY", 101, 1, 1, true, true);
                changeSquare("SY", -100, -100, 1, true, true);
                changeSquare("HR", 300, 300, 1, true, true);
                changeSquare("SR", -1, -1, 1, true, true);
            }
        }

        if (numberHit > 100 && numberHit < 200) { // hollow yellow is hit HY 101-199
            sneggyBodyParts += ((numberHit - 100f) / 10f);
            if ((countType("HY") == 0) || (numberHit - 100 == 99)) {
                changeSquare("SY", -100, -100, numberHit - 100, false, false);
            }else{
                changeSquare("HY", 101, 1, (numberHit - 100) + 1, false, true);
                changeSquare("SY", -100, -100, 1, false, true);
            }
            return;
        }

        if (numberHit / -100 > 0) { //solid yellow is hit -100 multiples
            sneggyBodyParts -= (numberHit / -100f); // loss of body parts based on speed.
            if ((countType("SY") == 0) || (numberHit / -100 == 99)) {
                changeSquare("HY", 101, 1, numberHit / -100, false, false);
            } else {
                changeSquare("HY", 101, 1, 1, false, true);
                changeSquare("SY", -100, -100, (numberHit / -100) + 1, false, true);
            }
            return;
        }

        if (numberHit >= 300) { // hollow red is hit 300 multiples
            if ((countType("HR") == 0) || (numberHit / 300 == 99)) {
                changeSquare("SR", -1, -1, numberHit / 300, false, false);
            } else {
                changeSquare("HR", 300, 300, (numberHit / 300) + 1, false, true);
                changeSquare("SR", -1, -1, 1, false, true);
            }
            return;
        }

        if (numberHit < 0) {  //solid red is hit -1 to -99
            if ((countType("SR") == 0) || (numberHit * -1 == 99)) {
                changeSquare("HR", 300, 300, numberHit * -1, false, false);
            } else {
                changeSquare("HR", 300, 300, 1, false, true);
                changeSquare("SR", -1, -1, (numberHit * -1) + 1, false, true);
            }
        }

        if (numberHit == 1) {
            if (level == 1 && !gameStarted) {
                changeSquare("G", 1, 1, 1, true, true);
                gameStarted = true;
                waitingForNextLevel = false;
                numbersLeft++;
            }

            numbersLeft--;
            // all greens removed on current level
            if (numbersLeft == 0) {
                level++;
                numbersLeft = level;
                changeAtXY(getRandomFilteredXY("E"), "G", (int) numbersLeft);
                waitingForNextLevel = true;
            }
        }

        if (numberHit > 1) {

            int tempNumberToDisplay = numberHit;
            int numberToSplit = tempNumberToDisplay / 2;
            for (int i = 0; i < 2; i++) {
                changeSquare("G", numberToSplit, numberToSplit, 1, true, true);
                tempNumberToDisplay = tempNumberToDisplay - numberToSplit;
            }
            changeSquare("G", tempNumberToDisplay, tempNumberToDisplay, 1, true, true);
        }

        if (numberHit < 0) {
            score += (displayPerUnit * level * -1);
        } else if ((numberHit != level) || (level == 1 && gameStarted)) {
            score += displayPerUnit;
        }
    }

    public void newSpeed() {
        if (running) {
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

        if (getValueAtXY(x[0], y[0]) != 0) {
            int numbersHit = getValueAtXY(x[0], y[0]);
            changeAtXY(new int[]{x[0], y[0]}, "E", -1 * numbersHit);
            newNumbers(numbersHit);
            slowerSpeed();
        }
    }

    public void slowerSpeed() {
        if (sneggySpeed < 250) {
            sneggySpeed *= 1.1;
            newSpeed();
        }
    }

    public void fasterSpeed() {
        if (sneggySpeed > 25) {
            sneggySpeed /= 1.1;
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
                        sneggySpeed = 125;
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



