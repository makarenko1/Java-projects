package src.gameobjects;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.Sound;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;

/**
 * Represents a ball, which is the main game object. It's positioned in game window as part of game
 * initialization and given initial velocity. On collision, it's velocity is updated to be reflected about
 * the normal vector of the surface it collides with.
 */
public class Ball extends GameObject {

    private final Sound collisionSound;
    private final Counter collisionCounter;

    /**
     * Constructor. Constructs a new GameObject instance.
     * @param topLeftCorner the position of the object, in window coordinates (pixels). Note that (0,0) is the
     *                      top-left corner of the window.
     * @param dimensions the width and height of the object in window coordinates.
     * @param renderable the renderable representing the object. If null, the object is invisible.
     * @param collisionSound the sound to be played on collision with the object.
     */
    public Ball(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable, Sound collisionSound) {
        super(topLeftCorner, dimensions, renderable);
        this.collisionSound = collisionSound;
        this.collisionCounter = new Counter(0);
    }

    /**
     * To be called on collision with the object. Object velocity is reflected about the normal vector of
     * the surface it collides with.
     * @param other the other GameObject instance participating in collision.
     * @param collision the object that represents the collision.
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        setVelocity(getVelocity().flipped(collision.getNormal()));
        collisionSound.play();
        collisionCounter.increment();
    }

    /**
     * Ball object maintains a counter which keeps count of collisions from start of game. This getter method
     * allows access to the collision count in case any behavior should need to be based on the number of
     * ball collisions.
     * @return the number of times ball collided with an object since start of game.
     */
    public int getCollisionCount() {
        return collisionCounter.value();
    }
}
