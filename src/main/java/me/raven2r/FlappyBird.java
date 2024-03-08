package me.raven2r;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    private static Random random = new Random();

    int boardWidth = 360;
    int boardHeight = 640;

    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image botPipeImg;

    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64; // scaled by 1/6
    int pipeHeight = 512;

    int birdX = boardWidth / 8;
    int birdY = boardHeight / 2;
    int birdWidth = 34;
    int birdHeight = 24;




    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }


    // game logic
    Bird bird;
    ArrayList<Pipe> pipes = new ArrayList<>();

    int velocityX = -4;
    int velocityY = 0;
    int gravity = 1;

    Timer gameLoop;
    Timer placePipesTimer;
    double score;
    boolean gameIsOver = false;


    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        backgroundImg = new ImageIcon(getClass().getResource("/flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("/flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("/toppipe.png")).getImage();
        botPipeImg = new ImageIcon(getClass().getResource("/bottompipe.png")).getImage();

        bird = new Bird(birdImg);

        placePipesTimer = new Timer(1500, e -> {
            placePipes();
        });

        gameLoop = new Timer(1000/60, this);
        placePipesTimer.start();
        gameLoop.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
        move();

        if(gameIsOver) {
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }


    public void draw(Graphics g) {
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        for(int i = 0; i < pipes.size(); i++) {
            var pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 32));

        if(gameIsOver)
            g.drawString("Game over: " + (int) score, 10, 35);
        else
            g.drawString(String.valueOf((int)score), 10, 35);

    }

    public void move() {
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        for(int i = 0; i < pipes.size(); i++) {
            var pipe = pipes.get(i);
            pipe.x += velocityX;

            if(!pipe.passed && bird.x > pipe.x + pipe.width) {
                pipe.passed = true;
                score += 0.5;
            }

            if(collision(bird, pipe))
                gameIsOver = true;
        }

        if(bird.y > boardHeight)
            gameIsOver = true;
    }

    public void placePipes() {
        int randomPipeY = (int) (pipeY - pipeHeight/4 - (Math.random() * (pipeHeight/2)));
        int openingSpace = boardHeight / 4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe botPipe = new Pipe(botPipeImg);
        botPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(botPipe);
    }

    public boolean collision(Bird bird, Pipe pipe) {
        return bird.x < pipe.x + pipe.width &&
                bird.x + bird.width > pipe.x &&
                bird.y < pipe.y + pipe.height &&
                bird.y + bird.height > pipe.y;
    }
    
    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_SPACE:
                velocityY = -9;

                if(gameIsOver) {
                    bird.y = birdY;
                    velocityY = 0;
                    pipes.clear();
                    score = 0;
                    gameIsOver = false;
                    gameLoop.start();
                    placePipesTimer.start();
                }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
