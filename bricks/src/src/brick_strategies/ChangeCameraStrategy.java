package src.brick_strategies;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.util.Counter;
import danogl.util.Vector2;
import src.BrickerGameManager;
import src.gameobjects.Ball;
import src.gameobjects.BallCollisionCountdownAgent;

/**
 * Concrete class extending abstract RemoveBrickStrategyDecorator. Changes camera focus from ground to ball
 * until ball collides NUM_BALL_COLLISIONS_TO_TURN_OFF times.
 */
public class ChangeCameraStrategy extends RemoveBrickStrategyDecorator {

    private static final String MAIN_BALL_TAG = "main ball";
    private static final int NUM_BALL_COLLISIONS_TO_TURN_OFF = 4;
    private static final float FOCUS_FACTOR = 1.2f;

    private final CollisionStrategy toBeDecorated;
    private final WindowController windowController;
    private final BrickerGameManager gameManager;
    private BallCollisionCountdownAgent ballCollisionCountdownAgent;

    /**
     * Constructor.
     * @param toBeDecorated the CollisionStrategy object to be decorated.
     * @param windowController controls visual rendering of the game window and object renderables.
     * @param gameManager a GameManager instance.
     */
    public ChangeCameraStrategy(CollisionStrategy toBeDecorated, WindowController windowController,
                                BrickerGameManager gameManager) {
        super(toBeDecorated);
        this.toBeDecorated = toBeDecorated;
        this.windowController = windowController;
        this.gameManager = gameManager;
        this.ballCollisionCountdownAgent = null;
    }

    /**
     * To be called on brick collision. Changes camera position on collision and delegates to the held
     * CollisionStrategy object.
     * @param thisObj the brick game object.
     * @param otherObj the other GameObject instance participating in collision.
     * @param counter the global counter of the brick game objects.
     */
    @Override
    public void onCollision(GameObject thisObj, GameObject otherObj, Counter counter) {
        if (gameManager.getCamera() == null) {
            setCameraAndStartCountdown();
        }
        toBeDecorated.onCollision(thisObj, otherObj, counter);
    }

    /**
     * Returns the camera to the normal ground position.
     */
    public void turnOffCameraChange() {
        gameManager.setCamera(null);
        getGameObjectCollection().removeGameObject(ballCollisionCountdownAgent);
        this.ballCollisionCountdownAgent = null;
    }

    private void setCameraAndStartCountdown() {
        GameObjectCollection gameObjectCollection = toBeDecorated.getGameObjectCollection();
        for (GameObject object: gameObjectCollection) {
            if (object.getTag().equals(MAIN_BALL_TAG)) {
                gameManager.setCamera(new Camera(object, Vector2.ZERO,
                        windowController.getWindowDimensions().mult(FOCUS_FACTOR),
                        windowController.getWindowDimensions()));
                this.ballCollisionCountdownAgent = new BallCollisionCountdownAgent((Ball) object,
                        this, NUM_BALL_COLLISIONS_TO_TURN_OFF);
                gameObjectCollection.addGameObject(ballCollisionCountdownAgent);
            }
        }
    }
}
