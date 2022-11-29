package src.gameobjects;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Represents one of the types of objects that can be set loose when a brick is hit. Represents an object
 * that can change the width of the object that it can collide with.
 */
public class StatusDefiner extends GameObject {

    private final GameObjectCollection gameObjectCollection;
    private final String collideWithTag;
    private final float changeWidthCoefficient;
    private final int maxObjectWidth;
    private final int minObjectWidth;

    /**
     * Constructor. Constructs a new GameObject instance.
     * @param dimensions the width and height of the object in window coordinates.
     * @param renderable the renderable representing the object. If null, the object is invisible.
     * @param gameObjectCollection the global game object collection managed by the game manager.
     * @param collideWithTag should collide with every object that has this tag.
     * @param changeWidthCoefficient changes the width of the object that collided with to be its
     *                                previous length multiplied
     * @param maxObjectWidth the maximal width of the object that the buff can collide with.
     * @param minObjectWidth the minimal width of the object that the buff can collide with.
     */
    public StatusDefiner(Vector2 dimensions, Renderable renderable, GameObjectCollection gameObjectCollection,
                String collideWithTag, float changeWidthCoefficient, int maxObjectWidth, int minObjectWidth) {
        super(Vector2.ZERO, dimensions, renderable);
        this.gameObjectCollection = gameObjectCollection;
        this.collideWithTag = collideWithTag;
        this.changeWidthCoefficient = changeWidthCoefficient;
        this.maxObjectWidth = maxObjectWidth;
        this.minObjectWidth = minObjectWidth;
    }

    /**
     * Checks if the object can collide with another given object.
     * @param other another object to check if the object can collide with.
     * @return true iff the tag of the other object is equal to collideWithTag.
     */
    @Override
    public boolean shouldCollideWith(GameObject other) {
        return other.getTag().equals(collideWithTag);
    }

    /**
     * To be called on collision with the object. The width of the other object is changed to be the previous
     * width multiplied by the changeWidthCoefficient if the new width is within the interval
     * [minObjectWidth, maxObjectWidth].
     * @param other the other GameObject instance participating in collision.
     * @param collision the object that represents the collision.
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        Vector2 dimensions = other.getDimensions();
        float newObjectLength = dimensions.x() * changeWidthCoefficient;
        if (newObjectLength >= minObjectWidth && newObjectLength <= maxObjectWidth) {
            other.setDimensions(new Vector2(newObjectLength, dimensions.y()));
        }
        gameObjectCollection.removeGameObject(this);
    }
}
