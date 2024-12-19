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

    private enum GameState {
        START_MENU,
        IN_GAME,
        GAME_OVER
    }

    private final int BOARD_WIDTH = 500;
    private final int BOARD_HEIGHT = 500;
    private final int DOT_SIZE = 10;    // Size of each snake segment and the food
    private final int ALL_DOTS = (BOARD_WIDTH * BOARD_HEIGHT) / (DOT_SIZE * DOT_SIZE);
    private final int RAND_POS = 29;
    private final int DELAY = 140;

    private final int x[] = new int[ALL_DOTS];
    private final int y[] = new int[ALL_DOTS];

    private int dots;        // Current length of the snake
    private int food_x;      // X coordinate of the food
    private int food_y;      // Y coordinate of the food

    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;

    private GameState gameState = GameState.START_MENU;

    private Timer timer;

    public GameBoard() {
        initBoard();
    }

    private void initBoard() {
        setBackground(Color.black);
        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        setFocusable(true);
        addKeyListener(new TAdapter());
    }

    private void startGame() {
        dots = 3;  // Initial length of the snake

        // Initial snake position
        for (int z = 0; z < dots; z++) {
            x[z] = 50 - z * DOT_SIZE;
            y[z] = 50;
        }

        locateFood();
        leftDirection = false;
        rightDirection = true;
        upDirection = false;
        downDirection = false;

        timer = new Timer(DELAY, this);
        timer.start();
        gameState = GameState.IN_GAME;
    }

    private void endGame() {
        gameState = GameState.GAME_OVER;
        if (timer != null) {
            timer.stop();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        switch (gameState) {
            case START_MENU:
                drawStartMenu(g);
                break;
            case IN_GAME:
                doDrawing(g);
                break;
            case GAME_OVER:
                drawGameOver(g);
                break;
        }
    }

    private void doDrawing(Graphics g) {
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
    }

    private void drawStartMenu(Graphics g) {
        String title = "SNAKE GAME";
        String prompt = "Press ENTER to start";

        g.setColor(Color.white);
        Font large = new Font("Helvetica", Font.BOLD, 20);
        FontMetrics fm = getFontMetrics(large);
        g.setFont(large);

        g.drawString(title, (BOARD_WIDTH - fm.stringWidth(title)) / 2, BOARD_HEIGHT / 2 - 30);

        Font smaller = new Font("Helvetica", Font.PLAIN, 14);
        g.setFont(smaller);
        fm = getFontMetrics(smaller);
        g.drawString(prompt, (BOARD_WIDTH - fm.stringWidth(prompt)) / 2, BOARD_HEIGHT / 2);
    }

    private void drawGameOver(Graphics g) {
        String msg = "Game Over";
        String scoreMsg = "Score: " + (dots - 3);
        String restartMsg = "Press ENTER to restart";

        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(msg, (BOARD_WIDTH - metr.stringWidth(msg)) / 2, BOARD_HEIGHT / 2 - 20);
        g.drawString(scoreMsg, (BOARD_WIDTH - metr.stringWidth(scoreMsg)) / 2, BOARD_HEIGHT / 2);
        g.drawString(restartMsg, (BOARD_WIDTH - metr.stringWidth(restartMsg)) / 2, BOARD_HEIGHT / 2 + 20);
    }

    private void checkFood() {
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
                endGame();
            }
        }

        // Check walls
        if (x[0] < 0 || x[0] >= BOARD_WIDTH || y[0] < 0 || y[0] >= BOARD_HEIGHT) {
            endGame();
        }
    }

    private void locateFood() {
        int r = (int) (Math.random() * RAND_POS);
        food_x = r * DOT_SIZE;

        r = (int) (Math.random() * RAND_POS);
        food_y = r * DOT_SIZE;
    }

    @Override // the main loop
    public void actionPerformed(ActionEvent e) {
        if (gameState == GameState.IN_GAME) {
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

            if (gameState == GameState.START_MENU) {
                if (key == KeyEvent.VK_ENTER) {
                    startGame();
                }
            } else if (gameState == GameState.IN_GAME) {
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
            } else if (gameState == GameState.GAME_OVER) {
                if (key == KeyEvent.VK_ENTER) {
                    // Restart the game by calling startGame again
                    startGame();
                }
            }
        }
    }
}
