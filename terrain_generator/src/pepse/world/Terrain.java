package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.PerlinNoise;
import java.awt.*;

/**
 * The class that is responsible for the creation and management of terrain.
 */
public class Terrain {

    /**
     * The ground tag. Is needed for removing the ground objects from the game object collection.
     */
    public static final String GROUND_TAG = "ground";

    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);

    // Height and depth parameters of the first and last ground blocks:
    private static final float HEIGHT_FACTOR = 2 / (float) 3;
    private static final int TERRAIN_DEPTH = 20;

    private final GameObjectCollection gameObjects;
    private final int groundLayer;
    private final float groundHeightAtX0;
    private final PerlinNoise perlinNoise;  // creates random y by x coordinate.

    /**
     * Constructor.
     * @param gameObjects the global game object collection managed by the simulation manager.
     * @param groundLayer the layer for all the blocks of the ground.
     * @param windowDimensions the dimensions of the width, height of the simulation window in pixels.
     * @param seed defines randomness for the whole simulation.
     */
    public Terrain(GameObjectCollection gameObjects, int groundLayer, Vector2 windowDimensions, int seed) {
        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        this.groundHeightAtX0 = HEIGHT_FACTOR * windowDimensions.y();
        this.perlinNoise = new PerlinNoise(seed);
    }

    /**
     * Calculates and returns the ground height of a given location.
     * @param x a number.
     * @return the ground height at the given location.
     */
    public float groundHeightAt(float x) {
        if (x == 0) {
            return groundHeightAtX0;
        }
        return (float) (groundHeightAtX0 + groundHeightAtX0 * perlinNoise.noise(x / (float) Block.SIZE));

    }

    /**
     * Creates terrain in a given range of x-values.
     * @param minX the lower bound of the given range (will be rounded to a multiple of Block.SIZE).
     * @param maxX the upper bound of the given range (will be rounded to a multiple of Block.SIZE).
     */
    public void createInRange(int minX, int maxX) {
        int firstX = Block.getRoundedCoordinate(minX);
        for (int x = firstX ; x <= maxX ; x += Block.SIZE) {
            int y = Block.getRoundedCoordinate(groundHeightAt(x));
            for (int i = 0 ; i < TERRAIN_DEPTH ; i++) {
                createGroundBlock(x, y);
                y += Block.SIZE;
            }
        }
    }

    /**
     * Checks if the passed block is in the first two layers of the ground.
     * @param block some instance of the Block class.
     * @return true if the block is in the first two layers of the ground, false otherwise.
     */
    public boolean isBlockInFirstTwoLayers(GameObject block) {
        return block.getTopLeftCorner().y() - 2 * Block.SIZE < groundHeightAt(block.getTopLeftCorner().x());
    }

    /*
     * Creates one ground block.
     */
    private void createGroundBlock(int x, int y) {
        Renderable renderable = new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));
        GameObject block = new Block(new Vector2(x, y), renderable);
        block.setTag(GROUND_TAG);
        gameObjects.addGameObject(block, groundLayer);
    }
}
