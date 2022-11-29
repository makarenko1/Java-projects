package src.gameobjects;

import danogl.gui.Sound;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Represents one of the types of objects that can be set loose when a brick is hit. Represents an additional
 * ball (gray).
 */
public class Puck extends Ball {

    /**
     * Constructor. Constructs a new GameObject instance.
     * @param topLeftCorner the position of the object, in window coordinates (pixels). Note that (0,0) is
     *                      the top-left corner of the window.
     * @param dimensions the width and height of the object in window coordinates.
     * @param renderable the renderable representing the object. If null, the object is invisible.
     * @param collisionSound the sound to be played on collision with the object.
     */
    public Puck(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable, Sound collisionSound) {
        super(topLeftCorner, dimensions, renderable, collisionSound);
    }
}
