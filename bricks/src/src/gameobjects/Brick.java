package src.gameobjects;

import src.brick_strategies.CollisionStrategy;
import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;

/**
 * Represents a brick game object.
 */
public class Brick extends GameObject {

    private final CollisionStrategy collisionStrategy;
    private final Counter counter;

    /**
     * Constructor. Constructs a new GameObject instance.
     * @param topLeftCorner the position of the object, in window coordinates (pixels). Note that (0,0) is the
     *                      top-left corner of the window.
     * @param dimensions the width and height of the object in window coordinates.
     * @param renderable the renderable representing the object. If null, the object is invisible.
     * @param collisionStrategy tells what to do in case of collision with the object.
     * @param counter the global counter of the instances of the Brick class (brick game objects) in the game.
     */
    public Brick(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                 CollisionStrategy collisionStrategy, Counter counter) {
        super(topLeftCorner, dimensions, renderable);
        this.collisionStrategy = collisionStrategy;
        this.counter = counter;
        counter.increment();
    }

    /**
     * To be called on collision with the object. Calls the correspondent method of the object that tells
     * what to do in case of collision with this object.
     * @param other the other GameObject instance participating in collision.
     * @param collision the object that represents the collision.
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        collisionStrategy.onCollision(this, other, counter);
    }
}
