package src.gameobjects;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;

/**
 * Represents one of the types of objects that can be set loose when a brick is hit. Represents an additional
 * paddle.
 */
public class MockPaddle extends Paddle {

    /**
     * A constant value that is true iff there is a MockPaddle instance in the game. Is false at the start of
     * the game.
     */
    public static boolean isInstantiated = false;

    private final GameObjectCollection gameObjectCollection;
    private final int numCollisionsToDisappear;
    private final Counter collisionCounter;

    /**
     * Constructor. Constructs a new GameObject instance.
     * @param topLeftCorner the position of the object, in window coordinates (pixels). Note that (0,0) is
     *                      the top-left corner of the window.
     * @param dimensions the width and height of the object in window coordinates.
     * @param renderable the renderable representing the object. If null, the object is invisible.
     * @param inputListener reads the user input.
     * @param windowDimensions the width and height of the game window.
     * @param gameObjectCollection the global game object collection managed by the game manager.
     * @param minDistanceFromEdge the border for paddle movement. The minimal distance from the edge of the
     *                            game window for the object to maintain.
     * @param numCollisionsToDisappear the number of collisions until the object disappears from the game.
     */
    public MockPaddle(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                      UserInputListener inputListener, Vector2 windowDimensions,
                      GameObjectCollection gameObjectCollection, int minDistanceFromEdge,
                      int numCollisionsToDisappear) {
        super(topLeftCorner, dimensions, renderable, inputListener, windowDimensions, minDistanceFromEdge);
        isInstantiated = true;
        this.gameObjectCollection = gameObjectCollection;
        this.numCollisionsToDisappear = numCollisionsToDisappear;
        this.collisionCounter = new Counter(0);
    }

    /**
     * To be called on collision with the object.
     * @param other the other GameObject instance participating in collision.
     * @param collision the object that represents the collision.
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        collisionCounter.increment();
        if (collisionCounter.value() == numCollisionsToDisappear) {
            gameObjectCollection.removeGameObject(this);
            isInstantiated = false;
        }
    }
}
