package src.brick_strategies;

import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.util.Vector2;
import src.BrickerGameManager;
import java.util.Random;

/**
 * The Factory class for the brick strategies.
 */
public class BrickStrategyFactory {

    private static final int NUM_OF_STRATEGIES = 6;
    private static final int PUCK_STRATEGY = 0;
    private static final int ADD_PADDLE_STRATEGY = 1;
    private static final int CHANGE_CAMERA_STRATEGY = 2;
    private static final int CHANGE_PADDLE_WIDTH_STRATEGY = 3;
    private static final int DOUBLE_STRATEGY = 4;
    private static final int REMOVE_BRICK_STRATEGY = 5;

    private static final Random random = new Random();
    private final GameObjectCollection gameObjectCollection;
    private final BrickerGameManager gameManager;
    private final ImageReader imageReader;
    private final SoundReader soundReader;
    private final UserInputListener inputListener;
    private final WindowController windowController;
    private final Vector2 windowDimensions;

    /**
     * Constructor.
     * @param gameObjectCollection the global game object collection managed by the game manager.
     * @param gameManager a GameManager instance.
     * @param imageReader an ImageReader instance for reading images from files for rendering of objects.
     * @param soundReader a SoundReader instance for reading soundclips from files for rendering event sounds.
     * @param inputListener an InputListener instance for reading user input.
     * @param windowController controls visual rendering of the game window and object renderables.
     * @param windowDimensions the pixel dimensions for the game window, its height and width.
     */
    public BrickStrategyFactory(GameObjectCollection gameObjectCollection, BrickerGameManager gameManager,
                                ImageReader imageReader, SoundReader soundReader,
                                UserInputListener inputListener, WindowController windowController,
                                Vector2 windowDimensions) {
        this.gameObjectCollection = gameObjectCollection;
        this.gameManager = gameManager;
        this.imageReader = imageReader;
        this.soundReader = soundReader;
        this.inputListener = inputListener;
        this.windowController = windowController;
        this.windowDimensions = windowDimensions;
    }

    /**
     * Randomly selects between 5 strategies and returns one CollisionStrategy object which is a
     * RemoveBrickStrategy decorated by one of the decorator strategies, or decorated by two randomly
     * selected strategies, or decorated by one of the decorator strategies and a pair of additional two
     * decorator strategies.
     * @return an instance of the chosen brick strategy.
     */
    public CollisionStrategy getStrategy() {
        int index = random.nextInt(NUM_OF_STRATEGIES);
        if (index == DOUBLE_STRATEGY) {
            return getDoubleStrategy(NUM_OF_STRATEGIES - 1);
        }
        return getSingleStrategy(index, new RemoveBrickStrategy(gameObjectCollection));
    }

    private CollisionStrategy getDoubleStrategy(int firstIndexBound) {
        int firstIndex = random.nextInt(firstIndexBound);
        int secondIndex = random.nextInt(NUM_OF_STRATEGIES - 2);
        CollisionStrategy firstStrategy;
        if (firstIndex == DOUBLE_STRATEGY) {
            firstStrategy = getDoubleStrategy(NUM_OF_STRATEGIES - 2);
        } else {
            firstStrategy = getSingleStrategy(firstIndex, new RemoveBrickStrategy(gameObjectCollection));
        }
        return getSingleStrategy(secondIndex, firstStrategy);
    }

    private CollisionStrategy getSingleStrategy(int index, CollisionStrategy toBeDecorated) {
        switch (index) {
            case PUCK_STRATEGY:
                return new PuckStrategy(toBeDecorated, imageReader, soundReader);
            case ADD_PADDLE_STRATEGY:
                return new AddPaddleStrategy(toBeDecorated, imageReader, inputListener,
                        windowDimensions);
            case CHANGE_CAMERA_STRATEGY:
                return new ChangeCameraStrategy(toBeDecorated, windowController, gameManager);
            case CHANGE_PADDLE_WIDTH_STRATEGY:
                return new ChangePaddleWidthStrategy(toBeDecorated, imageReader);
            case REMOVE_BRICK_STRATEGY:
                return new RemoveBrickStrategy(gameObjectCollection);
            default:
                return null;
        }
    }
}
