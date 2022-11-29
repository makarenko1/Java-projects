package pepse.world;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Represents a single block in the simulation (larger objects can be created from blocks).
 */
public class Block extends GameObject {

    /**
     * The size of a single block.
     */
    public static final int SIZE = 30;

    /**
     * Constructor. Constructs a block (GameObject instance).
     * @param topLeftCorner the position of the block, in window coordinates.
     * @param renderable the renderable representing the block.
     */
    public Block(Vector2 topLeftCorner, Renderable renderable) {
        super(topLeftCorner, Vector2.ONES.mult(SIZE), renderable);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
    }

    /**
     * Calculates the given coordinate rounded to a multiple of Block.SIZE.
     * @param coordinate the given coordinate.
     * @return the rounded coordinate.
     */
    public static int getRoundedCoordinate(float coordinate) {
        return (int) Math.floor(coordinate / (double) Block.SIZE) * Block.SIZE;
    }
}
