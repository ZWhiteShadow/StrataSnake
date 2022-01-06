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
    int[] x;
    int[] y;
    int[][] sneggySphere;
    double sneggySpeed; //How fast game is running
    float scoreMultiplierFloat;
    int score;
    int sneggyBodyParts;
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
    int tempRedCount;
    int tempBlueCount;
    int difficulty;
    boolean waitingForNextLevel;

    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH + SIDE_FOR_SCORE, SCREEN_HEIGHT + BOTTOM_PANEL));
        this.setFocusable(true);
        this.setBackground(Color.black);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {
        waitingForNextLevel = true;
        tempRedCount = 0;
        tempBlueCount = 0;
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
        tempRedCount = 0;
        tempBlueCount = 0;
        for (int i = 0; i < SQUARES_ACROSS; i++) {
            for (int j = 0; j < SQUARES_DOWN; j++) {
                if ((sneggySphere[i][j] / 300 == Math.round((float) sneggySphere[i][j] / 300)) ||
                        (sneggySphere[i][j] % -100 == 0) || (sneggySphere[i][j] < 0 && sneggySphere[i][j] > -99) ||
                        (sneggySphere[i][j] > 0 && sneggySphere[i][j] < 99) || sneggySphere[i][j] == 0) {
                } else {
                    System.out.println("ERROR: (X: " + i + " Y: " + j + " ) " + sneggySphere[i][j]);
                }

                if ((sneggySphere[i][j] > -100 && sneggySphere[i][j] < 100) || (sneggySphere[i][j] >= 300)) {
                    g.setFont(new Font("Terminal", Font.PLAIN, 16));
                } else {
                    g.setFont(new Font("Terminal", Font.PLAIN, 36));
                }

                if ((((sneggySphere[i][j] == level) && (gameStarted) && level != 1) || ((sneggySphere[i][j] == level) && (level == 1) && (!gameStarted)))) {
                    g.setColor(Color.white);
                    g.fillRect(i * UNIT_SIZE, j * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                    g.setColor(Color.black);
                    if (sneggySphere[i][j] > 9) {
                        g.drawString(String.valueOf(sneggySphere[i][j]), (i * UNIT_SIZE) + 3, (j * UNIT_SIZE) + UNIT_SIZE - 7);
                    } else {
                        g.drawString(String.valueOf(sneggySphere[i][j]), (i * UNIT_SIZE) + 8, (j * UNIT_SIZE) + UNIT_SIZE - 7);
                    }

                } else if (sneggySphere[i][j] <= -100) { // loss of a limited number of body parts
                    if (sneggyBodyParts - (sneggySphere[i][j] / -100) <= 1) {
                        g.setColor(new Color(255, 0, 0));
                    } else {
                        if (sneggySphere[i][j] == -100) {
                            g.setColor(Color.yellow.brighter());
                        }else {
                            g.setColor(new Color(255,100,0));
                        }
                    }
                    g.fillRect(i * UNIT_SIZE, j * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                    g.setColor(Color.black);
                    g.drawString("!", (i * UNIT_SIZE) + 7, (j * UNIT_SIZE) + UNIT_SIZE - 3);

                } else if (sneggySphere[i][j] < -1 && sneggySphere[i][j] >= -99) {
                    tempRedCount -= sneggySphere[i][j];
                    g.setColor(Color.red.brighter().brighter());
                    g.fillOval(i * UNIT_SIZE, j * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                    g.setColor(Color.black);
                    if (sneggySphere[i][j] < -9) {
                        g.drawString(String.valueOf(sneggySphere[i][j] * -1), (i * UNIT_SIZE) + 3, (j * UNIT_SIZE) + UNIT_SIZE - 7);
                    } else {
                        g.drawString(String.valueOf(sneggySphere[i][j] * -1), (i * UNIT_SIZE) + 8, (j * UNIT_SIZE) + UNIT_SIZE - 7);
                    }

                } else if (sneggySphere[i][j] < 0) {
                    g.setColor(new Color(120, 0, 0));
                    g.fillRect(i * UNIT_SIZE, j * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                    g.setColor(Color.white);
                    g.drawString(String.valueOf(sneggySphere[i][j] * -1), (i * UNIT_SIZE) + 8, (j * UNIT_SIZE) + UNIT_SIZE - 7);
                    tempRedCount++;

                } else if (sneggySphere[i][j] == 1) {
                    g.setColor(Color.green.brighter());
                    g.fillRect(i * UNIT_SIZE, j * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                    g.setColor(Color.black);
                    g.drawString(String.valueOf(sneggySphere[i][j]), (i * UNIT_SIZE) + 8, (j * UNIT_SIZE) + UNIT_SIZE - 7);
                } else if (sneggySphere[i][j] == 300) {
                    tempBlueCount++;
                    g.setColor(new Color(0, 0, 120));
                    g.fillRect(i * UNIT_SIZE, j * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);

                } else if (sneggySphere[i][j] > 300) {
                    tempBlueCount += (sneggySphere[i][j] / 300);
                    g.setColor(Color.blue.brighter());
                    g.fillOval(i * UNIT_SIZE, j * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                    g.setColor(Color.black);
                    if (sneggySphere[i][j] < -9) {
                        g.drawString(String.valueOf(sneggySphere[i][j] / 300), (i * UNIT_SIZE) + 3, (j * UNIT_SIZE) + UNIT_SIZE - 7);
                    } else {
                        g.drawString(String.valueOf(sneggySphere[i][j] / 300), (i * UNIT_SIZE) + 8, (j * UNIT_SIZE) + UNIT_SIZE - 7);
                    }

                } else if (sneggySphere[i][j] > 0) {
                    g.setColor(Color.green);
                    g.fillOval(i * UNIT_SIZE, j * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                    g.setColor(Color.black);
                    if (sneggySphere[i][j] > 9) {
                        g.drawString(String.valueOf(sneggySphere[i][j]), (i * UNIT_SIZE) + 3, (j * UNIT_SIZE) + UNIT_SIZE - 7);
                    } else {
                        g.drawString(String.valueOf(sneggySphere[i][j]), (i * UNIT_SIZE) + 8, (j * UNIT_SIZE) + UNIT_SIZE - 7);
                    }
                }
            }
        }

        for (int i = 0; i < (tempBlueCount - tempRedCount); i++) { // if more blues reds - remove blues
            do {
                tempAcross = random.nextInt(SQUARES_ACROSS);
                tempDown = random.nextInt(SQUARES_DOWN);
            } while (sneggySphere[tempAcross][tempDown] < 300);
            sneggySphere[tempAcross][tempDown] -= 300;
        }

        if (waitingForNextLevel) {
            difficulty = (int) (((tempRedCount + (level * 2)) / (level * 2)) * 100);
        } else {
            difficulty = (int) ((tempRedCount / (level * 2)) * 100);
        }
    }

    // set snake named SNEGGY
    public void drawSneggy(Graphics g) {
        if (sneggyBodyParts > 1) {
            int colorShift = 255 / sneggyBodyParts;
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
        g.setColor(Color.red);
        g.setFont(new Font("Terminal", Font.PLAIN, UNIT_SIZE * 3));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString(message, (SCREEN_WIDTH - metrics.stringWidth(message)) / 2,
                SCREEN_HEIGHT / 2);
    }

    public void drawGrid(Graphics g) {

        g.setColor(Color.green.darker().darker().darker());
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
        int displaySpeed = (int) ((120 - sneggySpeed) / 5) + 1;
        int displayPerUnit = (int) ((difficulty > 100) ? (int) (scoreMultiplierFloat * (difficulty / 100f)) * (float) (125 / sneggySpeed) : (int) scoreMultiplierFloat * (float) (125 / sneggySpeed));

        //sneggySpeed / Per Unit /  Score
        g.setFont(new Font("Terminal", Font.PLAIN, 24));
        g.setColor(Color.green);
        g.drawString("Level: " + decimal.format(displayLevel) +
                "   Difficulty: " + difficulty + "%" +
                "   Speed: " + withCommas.format(displaySpeed) +
                "   Length: " + (sneggyBodyParts - 1), (SCREEN_WIDTH / 3) + 20, SCREEN_HEIGHT
                + (BOTTOM_PANEL / 2) - 10);

        g.drawString("   Per Green Unit: " + withCommas.format(displayPerUnit) +
                        "   Per Red Unit: " + withCommas.format(displayPerUnit * level * -1),
                SCREEN_WIDTH / 3 + 50, SCREEN_HEIGHT
                        + (BOTTOM_PANEL / 2) + 20);
        g.drawString("   Score: " + withCommas.format(score), SCREEN_WIDTH - 200, SCREEN_HEIGHT
                + (BOTTOM_PANEL / 2) + 10);
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

    public void newNumbers(int numberHit) {

        if (numberHit / -100 > 0) { //  1 less body parts - less per unit - Yellow Circle /w  "!" sign
            sneggyBodyParts -= (numberHit / -100);
            return;
        }
        if (numberHit >= 300) { // blue is hit
            for (int i = 0; i < (Math.abs(numberHit) / 300); i++) {
                do {
                    tempAcross = random.nextInt(SQUARES_ACROSS);
                    tempDown = random.nextInt(SQUARES_DOWN);
                } while (sneggySphere[tempAcross][tempDown] >= 0 || sneggySphere[tempAcross][tempDown] <= -100);
                sneggySphere[tempAcross][tempDown]++;
            }
            return;
        }
        float bonus = (difficulty > 100) ? scoreMultiplierFloat * (difficulty / 100f) : scoreMultiplierFloat;
        if (numberHit < 0) {
            score += (bonus * numberHit * level);
        } else {
            score += (bonus * numberHit);
        }
        if (numberHit == level) {
            waitingForNextLevel = false;
            for (int i = 0; i < level * 2; i++) {
                for (i = 0; i < 2; i++) {
                    do {
                        tempAcross = random.nextInt(SQUARES_ACROSS);
                        tempDown = random.nextInt(SQUARES_DOWN);
                    } while (sneggySphere[tempAcross][tempDown] != 0);
                    sneggySphere[tempAcross][tempDown] -= 100;
                }
                int tempCount = 0;
                do {
                    tempAcross = random.nextInt(SQUARES_ACROSS);
                    tempDown = random.nextInt(SQUARES_DOWN);
                } while (sneggySphere[tempAcross][tempDown] < -99 || sneggySphere[tempAcross][tempDown] == 0);
                sneggySphere[tempAcross][tempDown] -= 100;
            }
            for (int i = 0; i < (level * 2); i++) {
                do {
                    tempAcross = random.nextInt(SQUARES_ACROSS);
                    tempDown = random.nextInt(SQUARES_DOWN);
                    if (sneggySphere[tempAcross][tempDown] >= 300) {
                        break;
                    }
                } while (sneggySphere[tempAcross][tempDown] != 0);
                sneggySphere[tempAcross][tempDown] += 300;
            }
        }
        if (numberHit == 1) {
            if (level == 1 && !gameStarted) {
                do {
                    tempAcross = random.nextInt(SQUARES_ACROSS);
                    tempDown = random.nextInt(SQUARES_DOWN);
                } while (sneggySphere[tempAcross][tempDown] < 0 || sneggySphere[tempAcross][tempDown] >= 99);
                sneggySphere[tempAcross][tempDown]++;
                numbersLeft++;
                gameStarted = true;
            }
            numbersLeft--;
            if (numbersLeft == 0) {
                level++;
                sneggyBodyParts += level;
                numbersLeft = level;
                newLevel(numbersLeft);
                return;
            }
        }
        if (numberHit < 0) {
            for (int i = 0; i < (Math.abs(numberHit)); i++) {
                do {
                    tempAcross = random.nextInt(SQUARES_ACROSS);
                    tempDown = random.nextInt(SQUARES_DOWN);
                } while (sneggySphere[tempAcross][tempDown] <= -99 || sneggySphere[tempAcross][tempDown] >= 0);
                sneggySphere[tempAcross][tempDown]--;
            }
        }
        if (numberHit == level) {
            for (int i = 0; i < (Math.abs(numberHit) * 2); i++) {
                do {
                    tempAcross = random.nextInt(SQUARES_ACROSS);
                    tempDown = random.nextInt(SQUARES_DOWN);
                } while (sneggySphere[tempAcross][tempDown] <= -99 || sneggySphere[tempAcross][tempDown] > 0);
                sneggySphere[tempAcross][tempDown]--;
            }
        }
        if (numberHit > 1) {
            int tempNumberToDisplay = Math.abs(numberHit);
            int numberToSplit = tempNumberToDisplay / 2;
            for (int i = 0; i < 2; i++) {
                do {
                    tempAcross = random.nextInt(SQUARES_ACROSS);
                    tempDown = random.nextInt(SQUARES_DOWN);
                } while (sneggySphere[tempAcross][tempDown] != 0);
                sneggySphere[tempAcross][tempDown] += numberToSplit;
                tempNumberToDisplay = tempNumberToDisplay - numberToSplit;
            }
            do {
                tempAcross = random.nextInt(SQUARES_ACROSS);
                tempDown = random.nextInt(SQUARES_DOWN);
            } while (sneggySphere[tempAcross][tempDown] != 0);
            sneggySphere[tempAcross][tempDown] += tempNumberToDisplay;
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

        for (int i = sneggyBodyParts; i > 0; i--) {
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
                    x[0] = x[0] - 1;
                    step = false;
                }
            }
            case '1' -> {  // UP Left
                if (!step) {
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
                    x[0] = x[0] + 1;
                    step = false;
                }
            }
            case '3' -> {  // Down Right
                if (!step) {
                    y[0] = y[0] + 1;
                    step = true;
                } else {
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
        }
        sneggySphere[x[0]][y[0]] = 0;
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override

        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_A:
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_NUMPAD4:
                    direction = 'L';
                    break;

                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:
                case KeyEvent.VK_NUMPAD6:
                    direction = 'R';
                    break;

                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:
                case KeyEvent.VK_NUMPAD8:
                    direction = 'U';
                    break;

                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_X:
                case KeyEvent.VK_NUMPAD2:
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

                //Diagonals!
                case KeyEvent.VK_NUMPAD7:
                case KeyEvent.VK_Q:
                case KeyEvent.VK_HOME:
                    direction = '7';
                    break;

                case KeyEvent.VK_NUMPAD9:
                case KeyEvent.VK_E:
                case KeyEvent.VK_PAGE_UP:
                    direction = '9';
                    break;

                case KeyEvent.VK_NUMPAD1:
                case KeyEvent.VK_Z:
                case KeyEvent.VK_END:
                    direction = '1';
                    break;

                case KeyEvent.VK_NUMPAD3:
                case KeyEvent.VK_C:
                case KeyEvent.VK_PAGE_DOWN:
                    direction = '3';
                    break;

                case KeyEvent.VK_SUBTRACT:
                case KeyEvent.VK_MINUS:
                case KeyEvent.VK_SHIFT:
                    if (sneggySpeed < 250) { // As low as 50 at base difficulty 100%
                        sneggySpeed += 5;
                        newSpeed();
                    }
                    break;

                case KeyEvent.VK_ADD:
                case KeyEvent.VK_PLUS:
                case KeyEvent.VK_CONTROL:
                    if (sneggySpeed > 25) { // AS high as 500 base at base difficulty 100%
                        sneggySpeed -= 5;
                        newSpeed();
                    }
                    break;

                case KeyEvent.VK_NUMPAD5:
                case KeyEvent.VK_S:
                    sneggySpeed = 125;
                    newSpeed();
                    break;
            }
        }
    }
}



