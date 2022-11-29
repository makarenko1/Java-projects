package src.brick_strategies;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.util.Counter;

/**
 * Says what to do when a brick collided with another object (with the ball).
 */
public class RemoveBrickStrategy implements CollisionStrategy {

    private final GameObjectCollection gameObjectCollection;

    /**
     * Constructor.
     * @param gameObjectCollection the global game object collection managed by the game manager.
     */
    public RemoveBrickStrategy(GameObjectCollection gameObjectCollection) {
        this.gameObjectCollection = gameObjectCollection;
    }

    /**
     * To be called on brick collision. If the brick object that participated in the collision still exists
     * in the game, removes the brick object that was collided with from the game (deletes it from the
     * global collection of game objects) and decrements the global counter of brick game objects.
     * @param thisObj the brick game object.
     * @param otherObj the other GameObject instance participating in collision.
     * @param counter the global counter of the brick game objects.
     */
    public void onCollision(GameObject thisObj, GameObject otherObj, Counter counter) {
        if (gameObjectCollection.removeGameObject(thisObj, Layer.STATIC_OBJECTS)) {
            counter.decrement();
        }
    }

    /**
     * Returns the reference to the global game object collection which is held in the object.
     * @return the global game object collection managed by the game manager.
     */
    public GameObjectCollection getGameObjectCollection() {
        return gameObjectCollection;
    }
}
