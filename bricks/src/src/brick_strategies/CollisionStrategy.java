package src.brick_strategies;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.util.Counter;

/**
 * Represents a collision strategy of a game object.
 */
public interface CollisionStrategy {
    /**
     * Tells what to do on collision with another game object.
     * @param thisObject the GameObject instance participating in collision that has a collision strategy.
     * @param otherObject the other GameObject instance participating in collision.
     * @param counter the global counter of the objects of type that thisObject has.
     */
    void onCollision(GameObject thisObject, GameObject otherObject, Counter counter);

    /**
     * Returns the reference to the global game object collection that is held in the object.
     * @return the global game object collection managed by the game manager.
     */
    GameObjectCollection getGameObjectCollection();
}
