package src.brick_strategies;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;
import src.gameobjects.MockPaddle;
import java.util.Random;

/**
 * Concrete class extending abstract RemoveBrickStrategyDecorator. Introduces an extra paddle to the game
 * window which remains until colliding NUM_COLLISIONS_FOR_MOCK_PADDLE_DISAPPEARANCE with other game objects.
 */
public class AddPaddleStrategy extends RemoveBrickStrategyDecorator {

    private static final int NUM_COLLISIONS_FOR_MOCK_PADDLE_DISAPPEARANCE = 3;
    private static final int MOCK_PADDLE_WIDTH = 100;
    private static final int MOCK_PADDLE_HEIGHT = 15;
    private static final int MIN_DISTANCE_FROM_SCREEN_EDGE = 21;
    private static final String MOCK_PADDLE_TEXTURE_PATH = "assets/paddle.png";
    private static final String MOCK_PADDLE_TAG = "paddle";

    private static final Random random = new Random();
    private final CollisionStrategy toBeDecorated;
    private final ImageReader imageReader;
    private final UserInputListener inputListener;
    private final Vector2 windowDimensions;

    /**
     * Constructor.
     * @param toBeDecorated the CollisionStrategy object to be decorated.
     * @param imageReader an ImageReader instance for reading images from files for rendering of objects.
     * @param inputListener reads the user input.
     * @param windowDimensions the width and height of the game window.
     */
    public AddPaddleStrategy(CollisionStrategy toBeDecorated, ImageReader imageReader,
                             UserInputListener inputListener, Vector2 windowDimensions) {
        super(toBeDecorated);
        this.toBeDecorated = toBeDecorated;
        this.imageReader = imageReader;
        this.inputListener = inputListener;
        this.windowDimensions = windowDimensions;
    }

    /**
     * To be called on brick collision. Adds the mock paddle to the game on collision and delegates to the
     * held CollisionStrategy object.
     * @param thisObj the brick game object.
     * @param otherObj the other GameObject instance participating in collision.
     * @param counter the global counter of the brick game objects.
     */
    @Override
    public void onCollision(GameObject thisObj, GameObject otherObj, Counter counter) {
        if (!MockPaddle.isInstantiated) {
            createMockPaddle();
        }
        toBeDecorated.onCollision(thisObj, otherObj, counter);
    }

    private Vector2 getTopLeftCorner() {
        int mockPaddleX = random.nextInt((int) windowDimensions.x());
        if (mockPaddleX < MIN_DISTANCE_FROM_SCREEN_EDGE) {
            mockPaddleX = MIN_DISTANCE_FROM_SCREEN_EDGE;
        } else if (mockPaddleX > windowDimensions.x() - MIN_DISTANCE_FROM_SCREEN_EDGE) {
            mockPaddleX = (int) (windowDimensions.x() - MIN_DISTANCE_FROM_SCREEN_EDGE);
        }
        return new Vector2(mockPaddleX, windowDimensions.y() / 2);
    }

    private void createMockPaddle() {
        GameObjectCollection gameObjectCollection = toBeDecorated.getGameObjectCollection();
        Renderable mockPaddleImage = imageReader.readImage(MOCK_PADDLE_TEXTURE_PATH, true);
        Vector2 topLeftCorner = getTopLeftCorner();
        Vector2 dimensions = new Vector2(MOCK_PADDLE_WIDTH, MOCK_PADDLE_HEIGHT);
        GameObject mockPaddle = new MockPaddle(topLeftCorner, dimensions, mockPaddleImage, inputListener,
                windowDimensions, gameObjectCollection, MIN_DISTANCE_FROM_SCREEN_EDGE,
                NUM_COLLISIONS_FOR_MOCK_PADDLE_DISAPPEARANCE);
        mockPaddle.setTag(MOCK_PADDLE_TAG);
        gameObjectCollection.addGameObject(mockPaddle);
    }
}
