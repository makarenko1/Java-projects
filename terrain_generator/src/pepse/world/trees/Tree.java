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
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;

/**
 * The class that is responsible for the creation and management of trees. Each tree consists of a trunk (an
 * instance of the Trunk class) and a crown of leaves (instances of the Leaf class that are arranged
 * randomly).
 */
public class Tree {

    // Probabilities:
    private static final float PROBABILITY_TO_PLANT_TREE = 0.85f;
    private static final float PROBABILITY_TO_PLANT_LEAF = 0.3f;

    // Colors:
    private static final Color TRUNK_BASE_COLOR = new Color(100, 50, 20);
    private static final Color LEAF_BASE_COLOR = new Color(50, 200, 30);

    // Crown size:
    private static final int MIN_NUM_LEAVES = 2;
    private static final int MAX_NUM_LEAVES = 5;

    private final GameObjectCollection gameObjects;
    private final Terrain terrain;
    private final int trunkLayer;
    private final int leafLayer;
    private final int seed;
    // Contains all the x such that there exists a tree with a trunk at that x from the first tree creation:
    private final HashSet<Integer> allXWithTree;
    // Indicates the first creation of the trees in the world. Is needed to place the avatar correctly in the
    // PepseGameManager class:
    private boolean isFirstCreation;

    /**
     * Constructor.
     * @param gameObjects the global game object collection managed by the simulation manager.
     * @param terrain the object that represents the ground in the simulation.
     * @param trunkLayer the layer for the trunks of the trees.
     * @param leafLayer the layer for all the leaves of the trees.
     * @param seed defines randomness for the whole simulation.
     */
    public Tree(GameObjectCollection gameObjects, Terrain terrain, int trunkLayer, int leafLayer, int seed) {
        this.gameObjects = gameObjects;
        this.terrain = terrain;
        this.trunkLayer = trunkLayer;
        this.leafLayer = leafLayer;
        this.seed = seed;
        this.allXWithTree = new HashSet<>();
        this.isFirstCreation = true;
    }

    /**
     * Randomly creates trees in a given range of x-values. For each column we check if to plant there a tree
     * or not by "tossing a biased coin".
     * @param minX the lower bound of the given range (will be rounded to a multiple of the block size).
     * @param maxX the upper bound of the given range (will be rounded to a multiple of the block size).
     */
    public void createInRange(int minX, int maxX) {
        int firstX = Block.getRoundedCoordinate(minX);
        for (int x = firstX ; x <= maxX ; x += Block.SIZE) {
            Random random = new Random(Objects.hash(x, seed));
            if (random.nextFloat() > PROBABILITY_TO_PLANT_TREE) {
                x = createTree(x);
            }
        }
        this.isFirstCreation = false;
    }

    /**
     * Checks if there exists a tree with a trunk at the given x.
     * @param x the coordinate on the x-axis.
     * @return true if such tree exists, false otherwise.
     */
    public boolean isTreeInX(int x) {
        return allXWithTree.contains(x);
    }

    /**
     * Creates a tree that consists of a trunk and leaves, such that the trunk is at the given coordinate.
     * @param x the coordinate on the x-axis.
     * @return returns the minimum possible coordinate on the x-axis of the next tree. It is the x of the top
     * left corner of the next block because we don't want that two neighboring trees merged into one another.
     */
    private int createTree(int x) {
        int y = Trunk.create(x, gameObjects, terrain, TRUNK_BASE_COLOR, trunkLayer, seed);
        createLeaves(x, y);
        if (isFirstCreation) {
            allXWithTree.add(Block.getRoundedCoordinate(x));
        }
        x += Block.SIZE;
        return x;
    }

    /*
     * Creates all the tree leaves. Initially a potential crown of leaves is a square with a randomly set
     * number of leaves from the center of the crown (the top point of the trunk) to each border. For each
     * row, column we check if there can be a leaf by "tossing a biased coin".
     */
    private void createLeaves(int x, int y) {
        Random random = new Random(Objects.hash(x, y, seed));
        int numLeavesPerSide = random.nextInt((MAX_NUM_LEAVES - MIN_NUM_LEAVES)) + MIN_NUM_LEAVES;
        int numLeavesPerEdge = 2 * numLeavesPerSide + 1;
        x = x - numLeavesPerSide * Block.SIZE;
        y = y - numLeavesPerSide * Block.SIZE;
        for (int i = 0; i < numLeavesPerEdge; i++) {
            createLeavesInRow(x, y, numLeavesPerEdge, i);
        }
    }

    /*
     * Creates all the leaves in one row.
     */
    private void createLeavesInRow(int x, int y, int numLeavesPerSide, int i) {
        Random random;
        for (int j = 0; j < numLeavesPerSide; j++) {
            int currentX = x + j * Block.SIZE;
            int currentY = y + i * Block.SIZE;
            random = new Random(Objects.hash(currentX, currentY, seed));
            if (random.nextFloat() > PROBABILITY_TO_PLANT_LEAF && terrain.groundHeightAt(currentX) >
                    currentY + Block.SIZE) {
                createLeaf(currentX, currentY);
            }
        }
    }

    /*
     * Creates one leaf on the tree at the given x, y (top left corner coordinates).
     */
    private void createLeaf(int x, int y) {
        Vector2 topLeftCorner = new Vector2(x, y);
        Renderable renderable = new RectangleRenderable(ColorSupplier.approximateColor(
                LEAF_BASE_COLOR));
        GameObject leaf = new LeafBlock(topLeftCorner, renderable, terrain, seed);
        gameObjects.addGameObject(leaf, leafLayer);
    }
}
