package src.brick_strategies;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;
import src.gameobjects.StatusDefiner;
import java.util.Random;

/**
 * Concrete class extending abstract RemoveBrickStrategyDecorator. Introduces to the game window instead of
 * the brick once removed a buff that can either widen the paddle (either main or mock) or narrow it.
 */
public class ChangePaddleWidthStrategy extends RemoveBrickStrategyDecorator {

    private static final String PADDLE_TAG = "paddle";
    private static final int STATUS_WIDTH = 40;
    private static final int STATUS_HEIGHT = 20;
    private static final int STATUS_VELOCITY = 130;
    private static final String STATUS_WIDEN_TEXTURE = "assets/buffWiden.png";
    private static final String STATUS_NARROW_TEXTURE = "assets/buffNarrow.png";
    private static final float WIDEN_COEFFICIENT = 2.0f;
    private static final float NARROW_COEFFICIENT = 0.5f;
    private static final int MAX_PADDLE_WIDTH = 620;
    private static final int MIN_PADDLE_WIDTH = 20;

    private static final Random random = new Random();
    private final CollisionStrategy toBeDecorated;
    private final ImageReader imageReader;

    /**
     * Constructor.
     * @param toBeDecorated the CollisionStrategy object to be decorated.
     */
    public ChangePaddleWidthStrategy(CollisionStrategy toBeDecorated, ImageReader imageReader) {
        super(toBeDecorated);
        this.toBeDecorated = toBeDecorated;
        this.imageReader = imageReader;
    }

    /**
     * To be called on brick collision. Creates a status object (that either narrows the paddle or widens it)
     * and delegates to the held CollisionStrategy object.
     * @param thisObj the brick game object.
     * @param otherObj the other GameObject instance participating in collision.
     * @param counter the global counter of the brick game objects.
     */
    @Override
    public void onCollision(GameObject thisObj, GameObject otherObj, Counter counter) {
        createStatus(thisObj);
        toBeDecorated.onCollision(thisObj, otherObj, counter);
    }

    private void createStatus(GameObject thisObj) {
        GameObjectCollection gameObjectCollection = toBeDecorated.getGameObjectCollection();
        Renderable buffImage = imageReader.readImage(STATUS_NARROW_TEXTURE, true);
        float changeWidthCoefficient = NARROW_COEFFICIENT;
        if (random.nextBoolean()) {
            buffImage = imageReader.readImage(STATUS_WIDEN_TEXTURE, true);
            changeWidthCoefficient = WIDEN_COEFFICIENT;
        }
        GameObject status = new StatusDefiner(new Vector2(STATUS_WIDTH, STATUS_HEIGHT), buffImage,
                gameObjectCollection, PADDLE_TAG, changeWidthCoefficient, MAX_PADDLE_WIDTH, MIN_PADDLE_WIDTH);
        status.setCenter(thisObj.getCenter());
        status.setVelocity(Vector2.DOWN.mult(STATUS_VELOCITY));
        gameObjectCollection.addGameObject(status);
    }
}
