package src.gameobjects;

import danogl.GameObject;
import danogl.util.Vector2;
import src.brick_strategies.ChangeCameraStrategy;

/**
 * An object of this class is instantiated on collision of a ball with a brick with a change camera strategy.
 * It checks ball's collision counter every frame, and once it finds that the ball has collided
 * countDownValue times since instantiation, it calls the strategy object to reset the camera to normal.
 */
public class BallCollisionCountdownAgent extends GameObject {

    private final Ball ball;
    private final ChangeCameraStrategy owner;
    private final int countDownValue;
    private final int initialCollisionCount;

    /**
     * Constructor.
     * @param ball the Ball object whose collisions are to be counted.
     * @param owner the Object asking for countdown notification.
     * @param countDownValue the number of ball collisions. Notify the owner object that the ball collided
     *                       countDownValue times since instantiation.
     */
    public BallCollisionCountdownAgent(Ball ball, ChangeCameraStrategy owner, int countDownValue) {
        super(Vector2.ZERO, Vector2.ZERO, null);
        this.ball = ball;
        this.owner = owner;
        this.countDownValue = countDownValue;
        this.initialCollisionCount = ball.getCollisionCount() + 1; // count the last collision with brick.
    }

    /**
     * Updates the object in the game. If the ball game object collided countDownValue times since
     * instantiation, notifies the owner object (turns off the camera change).
     * @param deltaTime the time passed from the previous update.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (ball.getCollisionCount() - initialCollisionCount == countDownValue) {
            owner.turnOffCameraChange();
        }
    }
}
