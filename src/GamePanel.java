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
    int speed; //How fast game is running
    float scoreMultiplierFloat;
    int scoreMultiplierInt;
    int score;
    int sneggyBodyParts;
    float numbersLeft;
    float displayLevel;
    float level;
    char direction;
    boolean running;
    Timer timer;
    Random random;

    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH + SIDE_FOR_SCORE, SCREEN_HEIGHT + BOTTOM_PANEL));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {
        direction = 'D';
        sneggyBodyParts = 2;
        speed = 100;
        score = 0;
        running = true;
        scoreMultiplierFloat = 100.0f;
        x = new int[GAME_UNITS];
        y = new int[GAME_UNITS];
        sneggySphere = new int[SQUARES_ACROSS][SQUARES_DOWN];
        Arrays.stream(sneggySphere).forEach(a -> Arrays.fill(a, 0));
        level = 1;
        numbersLeft = level;
        newLevel(numbersLeft);
        timer = new Timer(speed, this);
        timer.start();
    }

    public void newSpeed() {
        if (running) {
            timer.stop();
            timer.setDelay(speed);
            timer.start();
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawNumbers(g);
        if (!running || score <= -2000) {
            gameOver(g, "Sneggy Died!");
        }
        drawSneggy(g);
        drawGrid(g);
        drawHighScore(g);
        drawBottomPanel(g);

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

    //set numbers
    public void drawNumbers(Graphics g) {

        for (int i = 0; i < SQUARES_ACROSS; i++) {
            g.setFont(new Font("Terminal", Font.PLAIN, 16));
            for (int j = 0; j < SQUARES_DOWN; j++) {
                if (sneggySphere[i][j] > 0) {
                    g.setColor(Color.green);
                    if (sneggySphere[i][j] > 9) {
                        g.drawString(String.valueOf(sneggySphere[i][j]), (i * UNIT_SIZE) + 3, (j * UNIT_SIZE) + UNIT_SIZE - 7);
                    } else {
                        g.drawString(String.valueOf(sneggySphere[i][j]), (i * UNIT_SIZE) + 8, (j * UNIT_SIZE) + UNIT_SIZE - 7);
                    }
                }
                if (sneggySphere[i][j] < 0) {
                    g.setColor(Color.red);
                    if (sneggySphere[i][j] < -9) {
                        g.drawString(String.valueOf(sneggySphere[i][j] * -1), (i * UNIT_SIZE) + 3, (j * UNIT_SIZE) + UNIT_SIZE - 7);
                    } else {
                        g.drawString(String.valueOf(sneggySphere[i][j] * -1), (i * UNIT_SIZE) + 8, (j * UNIT_SIZE) + UNIT_SIZE - 7);
                    }
                }
            }
        }
    }

    // set snake named SNEGGY
    public void drawSneggy(Graphics g) {
        if (score > -2000) {
            for (int i = 0; i < sneggyBodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                    g.fillRect(x[i] * UNIT_SIZE, y[i] * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(45, 180, 0));
                    g.fillRect(x[i] * UNIT_SIZE, y[i] * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                }
            }
        }
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
        g.setFont(new Font("Terminal", Font.PLAIN, 20));

        // Game Name
        g.drawString("                Introducing Sneggy In:", 0, SCREEN_HEIGHT + (BOTTOM_PANEL / 2) - 25);
        g.setFont(new Font("Terminal", Font.PLAIN, (int) (UNIT_SIZE * 1.5)));
        g.drawString("       StrataSnake \uD83D\uDC0D", 0, SCREEN_HEIGHT + (BOTTOM_PANEL / 2) + 15);

        scoreMultiplierInt = (int) scoreMultiplierFloat;
        displayLevel = level + ((level - numbersLeft) / level);
        //Speed / Per Unit /  Score
        g.setColor(Color.green);
        g.drawString("Level: " + decimal.format(displayLevel) + "    " +
                "Speed: " + (100 - speed) / 5 +
                "   Per Unit: " + withCommas.format(scoreMultiplierInt) +
                "   Score: " + withCommas.format(score), SCREEN_WIDTH / 3, SCREEN_HEIGHT
                + (BOTTOM_PANEL / 2) + 15);
    }

    //set side for high score table
    public void drawHighScore(Graphics g) {
        g.setColor(Color.lightGray);
        g.fillRect(SCREEN_WIDTH, 0, SCREEN_WIDTH + SIDE_FOR_SCORE, SCREEN_HEIGHT);
        g.setColor(Color.black);
        g.setFont(new Font("Terminal", Font.PLAIN, UNIT_SIZE * 3 / 4));
        g.drawString("High Scores:", SCREEN_WIDTH + (SIDE_FOR_SCORE / 8), UNIT_SIZE);
    }

    public void newLevel(float numberToDisplay) {
        int tempAcross;
        int tempDown;
        int tempNumberToDisplay = (int) numberToDisplay;
        for (int i = 0; i < numberToDisplay; i++) {
            do {
                tempAcross = random.nextInt(SQUARES_ACROSS);
                tempDown = random.nextInt(SQUARES_DOWN);
            } while (sneggySphere[tempAcross][tempDown] < 0);
            while (tempNumberToDisplay > 0) {
                int newNumber = (random.nextInt(tempNumberToDisplay)) + 1;
                if ((newNumber == tempNumberToDisplay) && (newNumber > 1)) {
                    newNumber -= 1;
                }
                do {
                    tempAcross = random.nextInt(SQUARES_ACROSS);
                    tempDown = random.nextInt(SQUARES_DOWN);
                } while (sneggySphere[tempAcross][tempDown] < 0);
                sneggySphere[tempAcross][tempDown] += newNumber;
                tempNumberToDisplay = tempNumberToDisplay - newNumber;
            }
            for (i = 0; i < (level * sneggyBodyParts); i++) {
                do {
                    tempAcross = random.nextInt(SQUARES_ACROSS);
                    tempDown = random.nextInt(SQUARES_DOWN);
                } while (sneggySphere[tempAcross][tempDown] > 0);
                sneggySphere[tempAcross][tempDown] -= 1;
            }
        }
    }

    public void newNumbers(int numberHit) {
        int tempAcross;
        int tempDown;
        if (numberHit == 1) {
            numbersLeft -= 1;
            if (numbersLeft == 0 ) {
                level += 1;
                numbersLeft = level;
                Arrays.stream(sneggySphere).forEach(a -> Arrays.fill(a, 0));
                newLevel(numbersLeft);
            }
        } else if (numberHit != -1) {
            int tempNumberToDisplay = Math.abs(numberHit);
            while (tempNumberToDisplay > 0) {
                int newNumber = (random.nextInt(tempNumberToDisplay)) + 1;
                if ((Math.abs(numberHit) == tempNumberToDisplay) && (Math.abs(numberHit) > 1)) {
                    newNumber -= 1;
                }
                do {
                    tempAcross = random.nextInt(SQUARES_ACROSS);
                    tempDown = random.nextInt(SQUARES_DOWN);
                } while ((numberHit > 0 && sneggySphere[tempAcross][tempDown] < 0) ||
                        (numberHit < 0 && sneggySphere[tempAcross][tempDown] > 0));
                if (numberHit > 0) {
                    sneggySphere[tempAcross][tempDown] += newNumber;
                } else {
                    sneggySphere[tempAcross][tempDown] -= newNumber;
                }
                tempNumberToDisplay = tempNumberToDisplay - newNumber;
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

    public void move() {
        sneggyBodyParts = 2 + (score / 1000);
        for (int i = sneggyBodyParts - 1; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        switch (direction) {
            case 'U' -> y[0] = y[0] - 1;
            case 'D' -> y[0] = y[0] + 1;
            case 'L' -> x[0] = x[0] - 1;
            case 'R' -> x[0] = x[0] + 1;
        }
        try {
            if (sneggySphere[x[0]][y[0]] != 0) {

                if (sneggySphere[x[0]][y[0]] < 0) {  // hit a negative
                    scoreMultiplierFloat = (scoreMultiplierFloat * (1 + (score / 100000.0f)));
                    score += (scoreMultiplierInt * sneggySphere[x[0]][y[0]]);
                }

                if (sneggySphere[x[0]][y[0]] > 0) {
                    scoreMultiplierFloat = (scoreMultiplierFloat * (1 + (score / 100000.0f)));
                    score += (scoreMultiplierInt * sneggySphere[x[0]][y[0]]);
                }

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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkCollisions();
        }
        repaint();
    }


    public class MyKeyAdapter extends KeyAdapter {
        @Override

        public void keyPressed(KeyEvent e) {

            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;

                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;

                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;

                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
                case KeyEvent.VK_ADD: //speed up
                    if (speed > 0) {
                        speed -= 5;
                        newSpeed();
                        scoreMultiplierFloat *= 1.106f; //score starts at 100 goes up to 750
                    }
                    break;
                case KeyEvent.VK_SUBTRACT: //slow down
                    if (speed < 200) {
                        speed += 5;
                        newSpeed();
                        scoreMultiplierFloat /= 1.106f; //score stats at 100 goes down to 13
                    }
                    break;
                case KeyEvent.VK_SPACE: //pause game
                    timer.stop();
                    startGame();
                    break;
                case KeyEvent.VK_ENTER: { //start new game
                    if (timer.isRunning()) {
                        speed = 100;
                        newSpeed();
                        timer.stop();
                    } else {
                        timer.start();
                    }
                }
                break;
            }
        }
    }
}




