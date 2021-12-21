import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    final int[] x = new int[GAME_UNITS];
    final int[] y = new int[GAME_UNITS];
    int speed = 100; //How fast game is running
    float scoreMultiplierFloat = 100.0f;
    int scoreMultiplierInt;
    int score;
    int snegyBodyParts = 6;
    int appleX;
    int appleY;
    char direction = 'D';
    boolean running = false;
    Timer timer;
    Random random;

    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {
        newApple();
        running = true;
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
    }

    public void draw(Graphics g) {
        for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
            g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
            g.drawLine(0, i * UNIT_SIZE, SCREEN_HEIGHT, i * UNIT_SIZE);
        }

        scoreMultiplierInt = (int) scoreMultiplierFloat;
        g.setColor(Color.green);
        g.setFont(new Font("TimesRoman", Font.PLAIN, UNIT_SIZE * 2));
        g.drawString(String.format("%d", scoreMultiplierInt), UNIT_SIZE , UNIT_SIZE * 2); //show score multiplier
        DecimalFormat scoreWithCommas = new DecimalFormat("#,###");
        g.drawString(scoreWithCommas.format(score), SCREEN_WIDTH / 2 , UNIT_SIZE * 2); //show score multiplier
        g.setColor(Color.red);
        g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

        for (int i = 0; i < snegyBodyParts; i++) {
            if (i == 0) {
                g.setColor(Color.green);
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            } else {
                g.setColor(new Color(45, 180, 0));
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }
        }

//        g.setColor(Color.green);
//        newApple();
//        g.setFont(new Font("TimesRoman", Font.PLAIN, UNIT_SIZE));
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
        if( ( x[0] == appleX ) && (y[0] == appleY) ) {
            snegyBodyParts++;
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
            if (x[0] > SCREEN_WIDTH) {
                running = false;
            }
            //check if head touches top border
            if (y[0] < 0) {
                running = false;
            }
            //check if head touches bottom border
            if (y[0] > SCREEN_HEIGHT) {
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
                        scoreMultiplierFloat *= 1.13461; //score starts at 100 goes up to 1250
                        System.out.println(speed);
                    }
                    break;
                case KeyEvent.VK_SUBTRACT: //slow down
                    if (speed < 215) {
                        speed += 5;
                        newSpeed();
                        scoreMultiplierFloat /= 1.13461; //score stats at 100 goes down to 5
                        System.out.println(speed);
                    }
                    break;
            }
        }
    }
}




