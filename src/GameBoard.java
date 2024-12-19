import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GameBoard extends JPanel implements ActionListener {

    private final int BOARD_WIDTH = 300;
    private final int BOARD_HEIGHT = 300;
    private final int DOT_SIZE = 10;    // Size of each snake segment and the food
    private final int ALL_DOTS = (BOARD_WIDTH * BOARD_HEIGHT) / (DOT_SIZE * DOT_SIZE);
    private final int RAND_POS = 29;    // For randomizing food position (given DOT_SIZE=10 and 300/10=30 cells)
    private final int DELAY = 140;      // The speed of the game (ms between ticks)

    private final int x[] = new int[ALL_DOTS];
    private final int y[] = new int[ALL_DOTS];

    private int dots;        // Current length of the snake
    private int food_x;      // X coordinate of the food
    private int food_y;      // Y coordinate of the food

    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;
    private boolean inGame = true;

    private Timer timer;

    public GameBoard() {
        initBoard();
    }

    private void initBoard() {
        setBackground(Color.black);
        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        setFocusable(true);
        addKeyListener(new TAdapter());
        startGame();
    }

    private void startGame() {
        dots = 3;  // Initial length of the snake

        // Initial snake position
        for (int z = 0; z < dots; z++) {
            x[z] = 50 - z * DOT_SIZE;
            y[z] = 50;
        }

        locateFood(); // Place the first food
        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    private void doDrawing(Graphics g) {
        if (inGame) {
            // Draw the food
            g.setColor(Color.red);
            g.fillRect(food_x, food_y, DOT_SIZE, DOT_SIZE);

            // Draw the snake
            for (int z = 0; z < dots; z++) {
                if (z == 0) {
                    // Head of the snake
                    g.setColor(Color.green);
                    g.fillRect(x[z], y[z], DOT_SIZE, DOT_SIZE);
                } else {
                    // Body of the snake
                    g.setColor(Color.yellow);
                    g.fillRect(x[z], y[z], DOT_SIZE, DOT_SIZE);
                }
            }

            Toolkit.getDefaultToolkit().sync();
        } else {
            gameOver(g);
        }
    }

    private void gameOver(Graphics g) {
        String msg = "Game Over";
        String scoreMsg = "Score: " + (dots - 3);
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(msg, (BOARD_WIDTH - metr.stringWidth(msg)) / 2, BOARD_HEIGHT / 2);
        g.drawString(scoreMsg, (BOARD_WIDTH - metr.stringWidth(scoreMsg)) / 2, (BOARD_HEIGHT / 2) + 20);
    }

    private void checkFood() {
        // If snake head is on the food
        if ((x[0] == food_x) && (y[0] == food_y)) {
            dots++;
            locateFood();
        }
    }

    private void move() {
        // Move segments from the end towards the front
        for (int z = dots - 1; z > 0; z--) {
            x[z] = x[z - 1];
            y[z] = y[z - 1];
        }

        // Change the head position according to direction
        if (leftDirection) {
            x[0] -= DOT_SIZE;
        }

        if (rightDirection) {
            x[0] += DOT_SIZE;
        }

        if (upDirection) {
            y[0] -= DOT_SIZE;
        }

        if (downDirection) {
            y[0] += DOT_SIZE;
        }
    }

    private void checkCollision() {
        // Check if the snake hits its own body
        for (int z = dots - 1; z > 0; z--) {
            if ((z > 4) && (x[0] == x[z]) && (y[0] == y[z])) {
                inGame = false;
            }
        }

        // Check if the snake hits the left border
        if (x[0] < 0) {
            inGame = false;
        }

        // Check if the snake hits the right border
        if (x[0] >= BOARD_WIDTH) {
            inGame = false;
        }

        // Check if the snake hits the top border
        if (y[0] < 0) {
            inGame = false;
        }

        // Check if the snake hits the bottom border
        if (y[0] >= BOARD_HEIGHT) {
            inGame = false;
        }

        if (!inGame) {
            timer.stop();
        }
    }

    private void locateFood() {
        int r = (int) (Math.random() * RAND_POS);
        food_x = r * DOT_SIZE;

        r = (int) (Math.random() * RAND_POS);
        food_y = r * DOT_SIZE;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (inGame) {
            checkFood();
            move();
            checkCollision();
        }

        repaint();
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_UP) && (!downDirection)) {
                upDirection = true;
                rightDirection = false;
                leftDirection = false;
            }

            if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
                downDirection = true;
                rightDirection = false;
                leftDirection = false;
            }
        }
    }
}
