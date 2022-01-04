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
    int redSquares;

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
        sneggyBodyParts = 3;
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
        redSquares = 0;
        for (int i = 0; i < SQUARES_ACROSS; i++) {
            for (int j = 0; j < SQUARES_DOWN; j++) {
                if (sneggySphere[i][j] == 100 || sneggySphere[i][j] == 200 || (sneggySphere[i][j] / 300 == Math.round((float) sneggySphere[i][j] / 300)) ||
                        sneggySphere[i][j] == -100 || sneggySphere[i][j] == -200 || (sneggySphere[i][j] < 0 && sneggySphere[i][j] > -99) ||
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

                } else if (sneggySphere[i][j] == -200) {
                    g.setColor(Color.yellow.brighter());
                    g.fillRect(i * UNIT_SIZE, j * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                    g.setColor(Color.black);
                    g.drawString("-", (i * UNIT_SIZE) + 7, (j * UNIT_SIZE) + UNIT_SIZE - 3);

                } else if (sneggySphere[i][j] == -100) {
                    g.setColor(Color.yellow.brighter());
                    g.fillOval(i * UNIT_SIZE, j * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                    g.setColor(Color.black);
                    g.drawString("-", (i * UNIT_SIZE) + 7, (j * UNIT_SIZE) + UNIT_SIZE - 3);

                } else if (sneggySphere[i][j] < -1 && sneggySphere[i][j] > -100) {
                    tempRedCount -= sneggySphere[i][j];
                    g.setColor(Color.red.brighter());
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
                    tempRedCount++;

                } else if (sneggySphere[i][j] == 1) {
                    g.setColor(Color.green.brighter());
                    g.fillRect(i * UNIT_SIZE, j * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                } else if (sneggySphere[i][j] == 100) {
                    g.setColor(Color.green.darker());
                    g.fillOval(i * UNIT_SIZE, j * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                    g.setColor(Color.black);
                    g.drawString("+", (i * UNIT_SIZE) + 2, (j * UNIT_SIZE) + UNIT_SIZE);

                } else if (sneggySphere[i][j] == 200) {
                    g.setColor(Color.green.darker());
                    g.fillRect(i * UNIT_SIZE, j * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                    g.setColor(Color.black);
                    g.drawString("+", (i * UNIT_SIZE) + 2, (j * UNIT_SIZE) + UNIT_SIZE);

                } else if (sneggySphere[i][j] == 300) {
                    tempBlueCount++;
                    g.setColor(new Color(0, 0, 120));
                    g.fillRect(i * UNIT_SIZE, j * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);

                } else if (sneggySphere[i][j] > 300) {
                    tempBlueCount += (sneggySphere[i][j] /300);
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
            difficulty = (int) (((tempRedCount + (level  * 2)) / (level * 2)) * 100);
        }else{
            difficulty = (int) ((tempRedCount / (level * 2)) * 100);
        }
    }

    // set snake named SNEGGY
    public void drawSneggy(Graphics g) {
            for (int i = 0; i < sneggyBodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.GREEN.darker());
                    g.fillRect(x[i] * UNIT_SIZE, y[i] * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(Color.green);
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

        g.setColor(Color.darkGray);
        //draw Vertical Lines on board
        for (int i = 0; i <= SCREEN_WIDTH / UNIT_SIZE; i++) {
            g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
        }
        //Draw Horizontal
        for (int i = 0; i <= SCREEN_HEIGHT / UNIT_SIZE; i++) {
            g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
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
        g.setColor(Color.green);
        DecimalFormat withCommas = new DecimalFormat("#,###");
        DecimalFormat decimal = new DecimalFormat("#,###.##");
        g.setFont(new Font("Terminal", Font.PLAIN, 24));

        // Game Name
        g.drawString("            Introducing \"Sneggy\" In:", 0, SCREEN_HEIGHT + (BOTTOM_PANEL / 2) - 25);
        g.setFont(new Font("Terminal", Font.PLAIN, (int) (UNIT_SIZE * 1.5)));
        g.drawString("       StrataSnake \uD83D\uDC0D", 0, SCREEN_HEIGHT + (BOTTOM_PANEL / 2) + 15);

        displayLevel = level + ((level - numbersLeft) / level);
        int displaySpeed = (int) (125 - sneggySpeed) / 5;
        int displayPerUnit = (difficulty > 100) ? (int) (scoreMultiplierFloat * (difficulty / 100f)) : (int) scoreMultiplierFloat;
        //sneggySpeed / Per Unit /  Score
        g.setFont(new Font("Terminal", Font.PLAIN, 24));
        g.setColor(Color.green);
        g.drawString("Level: " + decimal.format(displayLevel) +
                        "   Difficulty: " + difficulty + "%" +
                "   Speed: " + displaySpeed +
                "   Length: " + sneggyBodyParts +
                "   Per Unit: " + withCommas.format(displayPerUnit) +
                "   Score: " + withCommas.format(score), SCREEN_WIDTH / 3, SCREEN_HEIGHT
                + (BOTTOM_PANEL / 2) + 5);
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

        if (numberHit == 100) { // slow down
            if (sneggySpeed > 0) {
                sneggySpeed -= 5;
                newSpeed();
                scoreMultiplierFloat *= 1.106f; //score starts at 100 goes up to 750
                return;
            }
        }
        if (numberHit == -100) { // speed up
            sneggySpeed += 5;
            newSpeed();
            scoreMultiplierFloat /= 1.106f;
            return;
        }
        if (numberHit == 200) { // 1 extra body parts - less per unit - Yellow Square /w  "-" sign
            sneggyBodyParts++;
            scoreMultiplierFloat *= 1.106f;
            return;
        }
        if (numberHit == -200) { //  1 less body parts - less per unit - Yellow Square /w  "-" sign
            scoreMultiplierFloat /= 1.106f;
            sneggyBodyParts--;
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
        float bonus = (difficulty > 100) ? scoreMultiplierFloat * (difficulty / 100f) :scoreMultiplierFloat;
        if (numberHit == 1 && gameStarted) {
            score += (bonus * sneggySphere[x[0]][y[0]]);
        }

        if (numberHit == level) {
            waitingForNextLevel = false;
            for (int i = -2; i < 3; i++) {
                do {
                    tempAcross = random.nextInt(SQUARES_ACROSS);
                    tempDown = random.nextInt(SQUARES_DOWN);
                } while (sneggySphere[tempAcross][tempDown] != 0);
                sneggySphere[tempAcross][tempDown] += (i * 100);
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
                for (int i = 0; i < SQUARES_ACROSS; i++) {
                    for (int j = 0; j < SQUARES_DOWN; j++) {
                        if (sneggySphere[i][j] == 100 ||
                                sneggySphere[i][j] == 200 ||
                                sneggySphere[i][j] == -100 ||
                                sneggySphere[i][j] == -200) {
                            sneggySphere[i][j] = 0;
                        }
                    }
                }
                level++;
                numbersLeft = level;
                newLevel(numbersLeft);
                return;
            }
        }

        if (numberHit < 0 || redSquares >= 2) {
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
                } while (sneggySphere[tempAcross][tempDown] < 0);
                sneggySphere[tempAcross][tempDown] += numberToSplit;
                tempNumberToDisplay = tempNumberToDisplay - numberToSplit;
            }
            do {
                tempAcross = random.nextInt(SQUARES_ACROSS);
                tempDown = random.nextInt(SQUARES_DOWN);
            } while (sneggySphere[tempAcross][tempDown] < 0);
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
            checkCollisions();
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
        try {
            if (sneggySphere[x[0]][y[0]] != 0) {
                newNumbers(sneggySphere[x[0]][y[0]]);
            }
            sneggySphere[x[0]][y[0]] = 0;
        } catch (Exception e) {
            timer.stop();
            running = false;
        }
    }

    public void checkCollisions() {
        for (int i = sneggyBodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
                break;
            }
        }
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override

        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_NUMPAD4:
                    if ((direction != 'R') && (direction != '9') && (direction != '3')) {
                        direction = 'L';
                    }
                    break;

                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_NUMPAD6:
                    if ((direction != 'L') && (direction != '7') && (direction != '1')) {
                        direction = 'R';
                    }
                    break;

                case KeyEvent.VK_UP:
                case KeyEvent.VK_NUMPAD8:
                    if (direction != 'D' && (direction != '1') && (direction != '3')) {
                        direction = 'U';
                    }
                    break;

                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_NUMPAD2:
                    if (direction != 'U' && (direction != '7') && (direction != '9')) {
                        direction = 'D';
                    }
                    break;

                case KeyEvent.VK_ENTER: //pause game
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

                //Diagonals!
                case KeyEvent.VK_NUMPAD7:
                case KeyEvent.VK_HOME:
                    if (direction == 'D') {
                        direction = 'L';
                        break;
                    }
                    if (direction == 'R') {
                        direction = 'U';
                        break;
                    }
                    if (direction != '3') {
                        direction = '7';
                    }
                    break;

                case KeyEvent.VK_NUMPAD9:
                case KeyEvent.VK_PAGE_UP:
                    if (direction == 'D') {
                        direction = 'R';
                        break;
                    }
                    if (direction == 'L') {
                        direction = 'U';
                        break;
                    }
                    if (direction != '1') {
                        direction = '9';
                    }
                    break;

                case KeyEvent.VK_NUMPAD1:
                case KeyEvent.VK_END:
                    if (direction == 'U') {
                        direction = 'L';
                        break;
                    }
                    if (direction == 'R') {
                        direction = 'D';
                        break;
                    }
                    if (direction != '9') {
                        direction = '1';
                    }
                    break;

                case KeyEvent.VK_NUMPAD3:
                case KeyEvent.VK_PAGE_DOWN:
                    if (direction == 'U') {
                        direction = 'R';
                        break;
                    }
                    if (direction == 'L') {
                        direction = 'D';
                        break;
                    }
                    if (direction != '7') {
                        direction = '3';
                    }
                    break;
            }
        }
    }
}



