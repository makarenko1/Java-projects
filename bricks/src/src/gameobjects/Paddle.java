package src.gameobjects;

import danogl.GameObject;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import java.awt.event.KeyEvent;

/**
 * Represents a paddle, which is one of the main game objects. Repels the ball against the bricks.
 */
public class Paddle extends GameObject {

    private static final float PADDLE_VELOCITY = 300;

    private final UserInputListener inputListener;
    private final Vector2 windowDimensions;
    private final int minDistanceFromEdge;

    /**
     * Constructor. Constructs a new GameObject instance.
     * @param topLeftCorner the position of the object, in window coordinates (pixels). Note that (0,0) is the
     *                      top-left corner of the window.
     * @param dimensions the width and height of the object in window coordinates.
     * @param renderable the renderable representing the object. If null, the object is invisible.
     * @param inputListener reads the user input.
     * @param windowDimensions the width and height of the game window.
     * @param minDistanceFromEdge the border for paddle movement. The minimal distance from the edge of the
     *                            game window for the object to maintain.
     */
    public Paddle(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                  UserInputListener inputListener, Vector2 windowDimensions, int minDistanceFromEdge) {
        super(topLeftCorner, dimensions, renderable);
        this.inputListener = inputListener;
        this.windowDimensions = windowDimensions;
        this.minDistanceFromEdge = minDistanceFromEdge;
    }

    /**
     * Updates the object in the game. Sets its velocity according to the user input and makes sure that the
     * object is within the border for its movement.
     * @param deltaTime the time passed from the previous update.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        updateVelocity();
        updatePositionWithinBorders();
    }

    private void updateVelocity() {
        Vector2 movementDir = Vector2.ZERO;
        if (inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
            movementDir = movementDir.add(Vector2.LEFT);
        }
        if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
            movementDir = movementDir.add(Vector2.RIGHT);
        }
        setVelocity(movementDir.mult(PADDLE_VELOCITY));
    }

    private void updatePositionWithinBorders() {
        float topCornerX = getTopLeftCorner().x();
        float leftBorderX = minDistanceFromEdge;
        float rightBorderX = windowDimensions.x() - minDistanceFromEdge - getDimensions().x();
        if (topCornerX < leftBorderX) {
            setTopLeftCorner(new Vector2(leftBorderX, getTopLeftCorner().y()));
        } else if (topCornerX > rightBorderX) {
            setTopLeftCorner(new Vector2(rightBorderX, getTopLeftCorner().y()));
        }
    }
}
