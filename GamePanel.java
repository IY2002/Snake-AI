import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 10;
    final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    static final int DELAY = 10;
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 10;
    int applesEaten = 0;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;


    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        //this.addKeyListener(new MyKeyAdapter());
        startGame();

    }

    public void startGame() {
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            /*
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }
             */
            g.setColor(Color.RED);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.GREEN);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    //.setColor(new Color(45, 180, 0));
                    g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            g.setColor(Color.WHITE);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
        } else {
            gameOver(g);
        }
    }

    public void newApple() {
        appleX = random.nextInt((SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    public void move() {
        direction = SnakeAI.aiDirection(x[0], appleX, y[0], appleY, direction);
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }

    }

    public void checkApple() {
        if (appleX == x[0] && appleY == y[0]) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions() {
        //if head hit snake body
        for (int i = bodyParts; i > 0; i--) {
            if (x[i] == x[0] && y[i] == y[0]) {
                running = false;
            }
        }

        //check if head touches border
        if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < 0 || y[0] >= SCREEN_HEIGHT) running = false;

        if (!running) timer.stop();
    }

    public void gameOver(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
        g.setColor(Color.RED);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);
        System.out.println(direction);
        //System.out.println(appleX);
        //System.out.println(appleY);
        //System.out.println(x[0]);
        //System.out.println(y[0]);
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

    /*
        public class MyKeyAdapter extends KeyAdapter{
            @Override
            public void keyPressed(KeyEvent e){
                switch(e.getKeyCode()){
                    case KeyEvent.VK_LEFT:
                        if(direction != 'R') direction = 'L';
                        break;
                    case KeyEvent.VK_RIGHT:
                        if(direction != 'L') direction = 'R';
                        break;
                    case KeyEvent.VK_UP:
                        if(direction != 'D') direction = 'U';
                        break;
                    case KeyEvent.VK_DOWN:
                        if(direction != 'U') direction = 'D';
                        break;

                }
            }
        }
     */
    public class SnakeAI {

        /*
        Make snake be located at the top row.
        Move forward till the x position is the same as the apple.
        Go down until you hit apple then turn towards wall and climb to top row again.
         */
        public static char aiDirection(int x, int appleX, int y, int appleY, char currDirection) {
            if (x < appleX && y == 0 && currDirection != 'L') {
                return 'R';
            }
            if (x == appleX && y == 0 && currDirection != 'U') {
                return 'D';
            }
            if(x == appleX && y < appleY && currDirection != 'U') return 'D';
            if (y > appleY) {
                if (y != 0) {
                    if (x == 0 && currDirection != 'D') {
                        return 'U';
                    } else if (y == appleY && x == appleX && currDirection != 'L'){
                        return 'R';
                    } else return 'L';
                }
                if (y == 0 && currDirection != 'L') return 'R';
            }
            if(x > appleX){
                if(y == appleY && currDirection != 'R') return 'L';
                else return 'D';
            }
            if(y < appleY && currDirection != 'D'){
                return 'U';
            }
            if(x == 0) return  'U';
            if(y > appleY && appleX == x && currDirection == 'L') return 'U';
            return 'D';
        }
    }
}