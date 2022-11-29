package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.Block;
import pepse.world.Terrain;
import java.awt.*;
import java.util.Objects;
import java.util.Random;

/**
 * Represents a trunk of a tree in the simulation.
 */
public class Trunk {

    /**
     * The trunk tag. Is needed for removing the trunk objects from the game object collection.
     */
    public static final String TRUNK_TAG = "trunk";

    // Parameters for possible trunk sizes in blocks:
    private static final int MIN_NUM_TRUNK_BLOCKS = 5;
    private static final int MAX_NUM_TRUNK_BLOCKS = 15;

    /**
     * This function creates a trunk at the given x value.
     * @param x the coordinate to put the trunk at (will be rounded to a multiple of the block size).
     * @param gameObjects the global game object collection managed by the simulation manager.
     * @param terrain the object that represents the ground in the simulation.
     * @param color the color of the trunk.
     * @param layer the layer for the trunk.
     * @param seed defines randomness for the whole simulation.
     */
    public static int create(int x, GameObjectCollection gameObjects, Terrain terrain, Color color,
                             int layer, int seed) {
        int y = (int) (Math.floor(terrain.groundHeightAt(x) / Block.SIZE) - 1) * Block.SIZE;
        Random random = new Random(Objects.hash(x, seed));
        int numBlocks = random.nextInt((MAX_NUM_TRUNK_BLOCKS - MIN_NUM_TRUNK_BLOCKS)) + MIN_NUM_TRUNK_BLOCKS;
        for (int i = 0; i < numBlocks; i++) {
            GameObject trunkBlock = createTrunkBlock(x, y, color);
            gameObjects.addGameObject(trunkBlock, layer);
            y -= Block.SIZE;
        }
        return y;
    }

    /*
     * Creates and returns one trunk clock.
     */
    private static GameObject createTrunkBlock(int x, float y, Color color) {
        Renderable renderable = new RectangleRenderable(ColorSupplier.approximateColor(color));
        GameObject block = new Block(new Vector2(x, y), renderable);
        block.setTag(TRUNK_TAG);
        return block;
    }
}
