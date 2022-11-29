package src;

import danogl.gui.rendering.RectangleRenderable;
import src.brick_strategies.BrickStrategyFactory;
import src.brick_strategies.CollisionStrategy;
import src.gameobjects.*;
import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.*;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;
import java.awt.*;
import java.util.Random;

/**
 * Game manager - this class is responsible for game initialization, holding references for game objects and
 * calling update methods for every update iteration. Entry point for code should be in a main method in this
 * class.
 */
public class BrickerGameManager extends GameManager {

    private static final int FRAMERATE = 40;
    private static final int NUMBER_OF_LIVES = 4;
    private static final String WINDOW_TITLE = "Bricker";
    private static final int WINDOW_WIDTH = 700;
    private static final int WINDOW_HEIGHT = 500;
    private static final int BALL_DIAMETER = 20;
    private static final String BALL_TEXTURE_PATH = "assets/ball.png";
    private static final String BALL_SOUND_PATH = "assets/blop_cut_silenced.wav";
    private static final int BALL_VELOCITY = 150;
    private static final String MAIN_BALL_TAG = "main ball";
    private static final int PADDLE_WIDTH = 100;
    private static final int PADDLE_HEIGHT = 15;
    private static final int PADDLE_BUFFER = 30;
    private static final String PADDLE_TAG = "paddle";
    private static final int MIN_DISTANCE_FROM_SCREEN_EDGE = 21;
    private static final String PADDLE_TEXTURE_PATH = "assets/paddle.png";
    private static final String BRICK_TEXTURE_PATH = "assets/brick.png";
    private static final int NUMBER_OF_BRICK_ROWS = 5;
    private static final int NUMBER_OF_BRICKS_IN_ROW = 8;
    private static final int BRICK_HEIGHT = 17;
    private static final int BRICK_BUFFER = 5;
    private static final int BORDER_WIDTH = 20;
    private static final String BACKGROUND_TEXTURE_PATH = "assets/DARK_BG2_small.jpeg";
    private static final String WIDGET_TEXTURE_PATH = "assets/heart.png";
    private static final int WIDGET_SIZE = 20;
    private static final int WIDGET_BUFFER = 30;
    private static final String WIN_MESSAGE = "You win!";
    private static final String LOSE_SOUND_PATH = "assets/Bubble5_4.wav";
    private static final String LOSE_MESSAGE = "You lose!";
    private static final String PLAY_AGAIN_MESSAGE = " Play again?";

    private Counter livesCounter;
    private Counter brickCounter;
    private GameObject ball;
    private final Vector2 windowDimensions;
    private ImageReader imageReader; 
    private SoundReader soundReader;
    private WindowController windowController;
    private BrickStrategyFactory brickStrategyFactory;

    /**
     * Constructor.
     * @param windowTitle the title of the game window.
     * @param windowDimensions the pixel dimensions for the game window, its height and width.
     */
    public BrickerGameManager(String windowTitle, Vector2 windowDimensions) {
        super(windowTitle, windowDimensions);
        this.windowDimensions = windowDimensions;
    }

    /**
     * Calling this function should initialize the game window. It should initialize objects in the game
     * window - ball, paddle, walls, life counters, bricks. This version of the game has 5 rows, 8 columns
     * of bricks.
     * @param imageReader an ImageReader instance for reading images from files for rendering of objects.
     * @param soundReader a SoundReader instance for reading soundclips from files for rendering event sounds.
     * @param inputListener an InputListener instance for reading user input.
     * @param windowController controls visual rendering of the game window and object renderables.
     */
    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                               UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        windowController.setTargetFramerate(FRAMERATE);
        this.livesCounter = new Counter(NUMBER_OF_LIVES);
        this.brickCounter = new Counter(0);
        this.imageReader = imageReader;
        this.soundReader = soundReader;
        this.windowController = windowController;
        this.brickStrategyFactory = new BrickStrategyFactory(gameObjects(), this, imageReader,
                soundReader, inputListener, windowController, windowDimensions);
        createMainGameObjects(inputListener);
    }

    /**
     * Code in this function is run every frame update.
     * @param deltaTime the time between updates. For internal use by game engine.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        removeFallenObjects();
        checkForGameEnd();
    }

    /**
     * Entry point for game. Contains:
     * 1. An instantiation call to BrickerGameManager constructor.
     * 2. A call to run() method of instance of BrickerGameManager.
     * Should initialize game window of dimensions (x,y) = (700,500).
     * @param args the program command line arguments.
     */
    public static void main(String[] args) {
        BrickerGameManager brickerGameManager = new BrickerGameManager(WINDOW_TITLE, new Vector2(WINDOW_WIDTH,
                WINDOW_HEIGHT));
        brickerGameManager.run();
    }

    private void setBallVelocity() {
        Random random = new Random();
        float ballVelX = BALL_VELOCITY;
        float ballVelY = BALL_VELOCITY;
        if (random.nextBoolean()) {
            ballVelX *= -1;
        }
        if (random.nextBoolean()) {
            ballVelY *= -1;
        }
        ball.setVelocity(new Vector2(ballVelX, ballVelY));
    }

    private void positionBall() {
        ball.setCenter(windowDimensions.mult(0.5f));
        setBallVelocity();
    }

    private void createBall() {
        Renderable ballImage = imageReader.readImage(BALL_TEXTURE_PATH, true);
        Sound collisionSound = soundReader.readSound(BALL_SOUND_PATH);
        this.ball = new Ball(Vector2.ZERO, new Vector2(BALL_DIAMETER, BALL_DIAMETER), ballImage,
                collisionSound);
        positionBall();
        ball.setTag(MAIN_BALL_TAG);
        gameObjects().addGameObject(ball);
    }

    private void createPaddle(UserInputListener inputListener) {
        Renderable paddleImage = imageReader.readImage(PADDLE_TEXTURE_PATH, true);
        GameObject paddle = new Paddle(Vector2.ZERO, new Vector2(PADDLE_WIDTH, PADDLE_HEIGHT),
                paddleImage, inputListener, windowDimensions, MIN_DISTANCE_FROM_SCREEN_EDGE);
        paddle.setCenter(new Vector2(windowDimensions.x() / 2, windowDimensions.y() - PADDLE_BUFFER));
        paddle.setTag(PADDLE_TAG);
        gameObjects().addGameObject(paddle);
    }

    private void createOneBrick(Renderable brickImage, float brickWidth, float brickY, float brickX) {
        CollisionStrategy collisionStrategy = brickStrategyFactory.getStrategy();
        GameObject brick = new Brick(new Vector2(brickX, brickY), new Vector2(brickWidth, BRICK_HEIGHT),
                brickImage, collisionStrategy, brickCounter);
        gameObjects().addGameObject(brick, Layer.STATIC_OBJECTS);
    }

    private float createBricksInRow(Renderable brickImage, float brickWidth, float brickY) {
        float brickX = BORDER_WIDTH + 1;
        for (int j = 0 ; j < NUMBER_OF_BRICKS_IN_ROW ; j++) {
            createOneBrick(brickImage, brickWidth, brickY, brickX);
            brickX += (brickWidth + BRICK_BUFFER);
        }
        brickY += (BRICK_HEIGHT + BRICK_BUFFER);
        return brickY;
    }

    private void createBricks() {
        Renderable brickImage = imageReader.readImage(BRICK_TEXTURE_PATH, false);
        float allSpacesWidth = 2 * (BORDER_WIDTH) + BRICK_BUFFER * NUMBER_OF_BRICKS_IN_ROW;
        float allBricksWidth = windowDimensions.x() - allSpacesWidth;
        float brickWidth = allBricksWidth / NUMBER_OF_BRICKS_IN_ROW;
        float brickY = BORDER_WIDTH + 1;
        for (int i = 0 ; i < NUMBER_OF_BRICK_ROWS ; i++) {
            brickY = createBricksInRow(brickImage, brickWidth, brickY);
        }
    }

    private void createBorders() {
        RectangleRenderable rectangleRenderable = new RectangleRenderable(Color.GRAY);
        Vector2[] topLeftCorner = {
                Vector2.ZERO,
                Vector2.ZERO,
                new Vector2(windowDimensions.x() - BORDER_WIDTH, 0)};
        Vector2[] dimensions = {
                new Vector2(BORDER_WIDTH, windowDimensions.y()),
                new Vector2(windowDimensions.x(), BORDER_WIDTH),
                new Vector2(BORDER_WIDTH, windowDimensions.y())};
        for (int i = 0 ; i < 3; i++) {
            GameObject wall = new GameObject(topLeftCorner[i], dimensions[i], rectangleRenderable);
            gameObjects().addGameObject(wall);
        }
    }

    private void createBackground() {
        Renderable backgroundImage = imageReader.readImage(BACKGROUND_TEXTURE_PATH, false);
        GameObject background = new GameObject(Vector2.ZERO, windowDimensions, backgroundImage);
        background.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects().addGameObject(background, Layer.BACKGROUND);
    }

    private float createGraphicCounter() {
        Vector2 topLeftCorner = new Vector2(BORDER_WIDTH, windowDimensions.y() - WIDGET_BUFFER);
        Vector2 directions = new Vector2(WIDGET_SIZE, WIDGET_SIZE);
        Renderable widgetImage = imageReader.readImage(WIDGET_TEXTURE_PATH, true);
        GameObject graphicLifeCounter = new GraphicLifeCounter(topLeftCorner, directions, livesCounter,
                widgetImage, gameObjects(), NUMBER_OF_LIVES);
        gameObjects().addGameObject(graphicLifeCounter, Layer.BACKGROUND);
        return graphicLifeCounter.getTopLeftCorner().y();
    }

    private void createNumericCounter(float topLeftCornerY) {
        Vector2 topLeftCorner = new Vector2(BORDER_WIDTH + WIDGET_BUFFER, topLeftCornerY - WIDGET_BUFFER);
        Vector2 directions = new Vector2(WIDGET_SIZE, WIDGET_SIZE);
        GameObject numericLifeCounter = new NumericLifeCounter(livesCounter, topLeftCorner, directions,
                gameObjects());
        gameObjects().addGameObject(numericLifeCounter, Layer.BACKGROUND);
    }

    private void createLifeCounters() {
        float topLeftCornerY = createGraphicCounter();
        createNumericCounter(topLeftCornerY);
    }

    private void createMainGameObjects(UserInputListener inputListener) {
        createBall();
        createPaddle(inputListener);
        createBricks();
        createBorders();
        createBackground();
        createLifeCounters();
    }

    private void removeFallenObjects() {
        for (GameObject object : gameObjects()) {
            if (!object.getTag().equals(MAIN_BALL_TAG) && object.getCenter().y() > windowDimensions.y()) {
                gameObjects().removeGameObject(object);
            }
        }
    }

    private void playAgainOrExit(String prompt) {
        if (windowController.openYesNoDialog(prompt + PLAY_AGAIN_MESSAGE)) {
            windowController.resetGame();
        } else {
            windowController.closeWindow();
        }
    }

    private void treatGameEnd(String prompt) {
        livesCounter.decrement();
        if (livesCounter.value() > 0) {
            positionBall();
        } else {
            playAgainOrExit(prompt);
        }
    }

    private void checkForGameEnd() {
        double ballHeight = ball.getCenter().y();
        String prompt = "";
        if (brickCounter.value() == 0) {
            prompt = WIN_MESSAGE;
        } else if (ballHeight > windowDimensions.y()) {
            soundReader.readSound(LOSE_SOUND_PATH).play();
            prompt = LOSE_MESSAGE;
        }
        if (!prompt.isEmpty()) {
            treatGameEnd(prompt);
        }
    }
}
