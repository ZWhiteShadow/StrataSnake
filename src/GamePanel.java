import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 1200;
    static final int SIDE_FOR_SCORE = 150;
    static final int BOTTOM_PANEL = 100;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    int[] x;
    int[] y;
    int speed; //How fast game is running
    float scoreMultiplierFloat;
    int scoreMultiplierInt;
    int score;
    int snegyBodyParts;
    int appleX;
    int appleY;
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
        snegyBodyParts = 6;
        speed = 100;
        score = 0;
        running = true;
        scoreMultiplierFloat = 100.0f;
        x = new int[GAME_UNITS];
        y = new int[GAME_UNITS];
        newApple();
        timer = new Timer(speed, this);
        timer.start();
    }

    public void newSpeed() {
        if(running) {
        timer.stop();
        timer.setDelay(speed);
        timer.start();
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
        if (!running) {

            //set font size and color
            g.setColor(Color.green);
            g.setFont(new Font("TimesRoman", Font.PLAIN, UNIT_SIZE * 2));

            //show GAME OVER
            g.drawString("GAME OVER", SCREEN_WIDTH / 3, SCREEN_HEIGHT / 2);

        }
    }

    public void draw(Graphics g) {

        for (int i = 0; i <= SCREEN_WIDTH / UNIT_SIZE; i++) {
            g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
        }
        for (int i = 0; i <= SCREEN_HEIGHT / UNIT_SIZE; i++) {
            g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
        }

        //set apple
        g.setColor(Color.red);
        g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

        // set snake named SNEGY
        for (int i = 0; i < snegyBodyParts; i++) {
            if (i == 0) {
                g.setColor(Color.green);
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            } else {
                g.setColor(new Color(45, 180, 0));
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }
        }

        //fill in bottom
        g.setColor(Color.darkGray);
        g.fillRect(0, SCREEN_HEIGHT, SCREEN_WIDTH + SIDE_FOR_SCORE, BOTTOM_PANEL);
        g.setFont(new Font("TimesRoman", Font.PLAIN, UNIT_SIZE));

        //show the per apple amount and score at bottom
        scoreMultiplierInt = (int) scoreMultiplierFloat;
        g.setColor(Color.green);
        g.setFont(new Font("TimesRoman", Font.PLAIN, (int) (UNIT_SIZE * 1.5)));
        DecimalFormat scoreWithCommas = new DecimalFormat("#,###");
        g.drawString("       StrataSnake \uD83D\uDC0D", 0, SCREEN_HEIGHT + (BOTTOM_PANEL / 2) + 15);
        g.drawString("Speed: " + (100 - speed) / 5 + "   Per Apple: " + scoreWithCommas.format(scoreMultiplierInt) +
                "   Score: " + scoreWithCommas.format(score), SCREEN_WIDTH / 3, SCREEN_HEIGHT
                + +(BOTTOM_PANEL / 2) + 15);


        //set side for high score table
        g.setColor(Color.lightGray);
        g.fillRect(SCREEN_WIDTH, 0, SCREEN_WIDTH + SIDE_FOR_SCORE, SCREEN_HEIGHT);
        g.setColor(Color.black);
        g.setFont(new Font("TimesRoman", Font.PLAIN, UNIT_SIZE * 3 / 4));
        g.drawString("High Scores:", SCREEN_WIDTH + (SIDE_FOR_SCORE / 8), UNIT_SIZE);

        //Used later for numbers one red one green - to make game strategic
//        newApple();
//        g.setColor(Color.green);
//        g.drawString(String.valueOf(1), appleX + (int) (UNIT_SIZE / 5), (int) appleY + (UNIT_SIZE * 5/6 ));
//        newApple();
//        g.setColor(Color.red);
//        g.drawString(String.valueOf(1), appleX + (int) (UNIT_SIZE / 5), (int) appleY + (UNIT_SIZE * 5/6 ));

    }

    public void newApple() {
        appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    public void move() {
        for (int i = snegyBodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        switch (direction) {
            case 'U' -> y[0] = y[0] - UNIT_SIZE;
            case 'D' -> y[0] = y[0] + UNIT_SIZE;
            case 'L' -> x[0] = x[0] - UNIT_SIZE;
            case 'R' -> x[0] = x[0] + UNIT_SIZE;
        }
    }

    public void checkApple() {
        //If eats apple
        if ((x[0] == appleX) && (y[0] == appleY)) {
            snegyBodyParts++;
            scoreMultiplierFloat *= 1.035333f;
            score += scoreMultiplierInt;
            newApple();
        }
    }

    public void checkCollisions() {
        for (int i = snegyBodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
            //check if head touches left border
            if (x[0] < 0) {
                running = false;
            }
            //check if head touches right border
            if (x[0] > SCREEN_WIDTH - UNIT_SIZE) {
                running = false;
            }
            //check if head touches top border
            if (y[0] < 0) {
                running = false;
            }
            //check if head touches bottom border
            if (y[0] > SCREEN_HEIGHT - UNIT_SIZE) {
                running = false;
            }
            if (!running) {
                timer.stop();
            }
        }
    }

    public void gameOver(Graphics g) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
            if (!running) {

            }
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
                    if (timer.isRunning()) {
                        speed = 100;
                        newSpeed();
                        timer.stop();
                    } else {
                        timer.start();
                    }
                    break;
                case KeyEvent.VK_ENTER: { //start new game
                    timer.stop();
                    startGame();
                }
                break;
            }
        }
    }
}




