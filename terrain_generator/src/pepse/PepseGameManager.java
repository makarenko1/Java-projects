package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.util.Vector2;
import pepse.world.*;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.LeafBlock;
import pepse.world.trees.Tree;
import pepse.world.trees.Trunk;
import java.awt.*;
import java.util.Random;

/**
 * The main class of the simulator.
 */
public class PepseGameManager extends GameManager {

    private static final int SEED = 32;  // defines randomness for the whole simulation.

    // Number of pixels to add related to the left corner of the window when the world is initialized (first)
    // and number of pixels to add from each side of the window when the world is expanded (second):
    private static final int WORLD_BORDER_OFFSET = 400;
    private static final int WORLD_BORDER_DELTA = 200;

    // Layers for all the objects:
    private static final int SKY_LAYER = Layer.BACKGROUND;
    private static final int TERRAIN_LAYER = Layer.STATIC_OBJECTS;
    private static final int NIGHT_LAYER = Layer.FOREGROUND;
    private static final int SUN_LAYER = Layer.BACKGROUND + 1;
    private static final int SUN_HALO_LAYER = Layer.BACKGROUND + 2;
    private static final int TRUNK_LAYER = Layer.STATIC_OBJECTS + 1;
    private static final int LEAF_LAYER = Layer.STATIC_OBJECTS + 2;
    private static final int AVATAR_LAYER = Layer.DEFAULT;
    private static final int ENERGY_COUNTER_LAYER = Layer.UI;

    // Parameters for the astronomic objects:
    private static final int DAY_NIGHT_CYCLE_LENGTH = 30;
    private static final Color SUN_HALO_COLOR = new Color(255, 255, 0, 20);

    // Coefficients for calculating the minimum and maximum starting coordinate of the avatar:
    private static final float MIN_AVATAR_POSITION_COEFFICIENT = 0.25f;
    private static final float MAX_AVATAR_POSITION_COEFFICIENT = 0.75f;

    // Coefficient for positioning the camera at the avatar (s.t. the avatar would be in the center of the
    // screen).
    private static final float CENTRAL_CAMERA_COEFFICIENT = 0.5f;

    private GameObjectCollection gameObjects;
    private Vector2 windowDimensions;
    private float startWorld;
    private float endWorld;
    private Avatar avatar;
    private Terrain terrain;
    private Tree tree;

    /**
     * Initializes the simulation window. Initializes the simulation objects. The objects of the simulation
     * include: sky, terrain, night, sun, sun halo, trees, avatar and its energy counter.
     * @param imageReader an ImageReader instance for reading images from files for rendering of objects.
     * @param soundReader a SoundReader instance for reading soundclips from files for rendering event sounds.
     * @param inputListener an InputListener instance for reading user input.
     * @param windowController controls visual rendering of the game window and object renderables.
     */
    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                               UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        this.gameObjects = gameObjects();
        this.windowDimensions = windowController.getWindowDimensions();
        // In relation to the left edge of the window:
        this.startWorld = -WORLD_BORDER_OFFSET;
        this.endWorld = windowDimensions.x() + WORLD_BORDER_OFFSET;
        createObjects(imageReader, inputListener);
        setLayersBehavior();
        setCamera(windowController);
    }

    /**
     * Code in this function is run every frame update. Expands the world borders if the avatar is
     * sufficiently close to them.
     * @param deltaTime the time between updates. For internal use by game engine.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        // We check if the avatar has travelled WORLD_BORDER_OFFSET / 2, and if so, then we must add
        // WORLD_BORDER_DELTA := WORLD_BORDER_OFFSET / 2 to maintain WORLD_BORDER_OFFSET from both sides of
        // the window.
        float bufferLength = (windowDimensions.x() + WORLD_BORDER_OFFSET) / 2;
        float avatarX = avatar.getCenter().x();
        if ((avatarX - startWorld) < bufferLength) {
            expandFromLeft();
        } else if ((endWorld - avatarX) < bufferLength) {
            expandFromRight();
        }
    }

    /**
     * Runs the entire simulation.
     * @param args this argument should not be used.
     */
    public static void main(String[] args) {
        new PepseGameManager().run();
    }

    /*
     * Creates the simulation objects. Saves all the necessary objects for the infinite world in separate
     * fields (terrain, tree, avatar).
     */
    private void createObjects(ImageReader imageReader, UserInputListener inputListener) {
        // Create sky:
        Sky.create(gameObjects, windowDimensions, SKY_LAYER);
        // Create terrain:
        this.terrain = new Terrain(gameObjects, TERRAIN_LAYER, windowDimensions, SEED);
        terrain.createInRange((int) startWorld, (int) endWorld);
        // Create night:
        Night.create(gameObjects, NIGHT_LAYER, windowDimensions, DAY_NIGHT_CYCLE_LENGTH);
        // Create sun and sun halo:
        GameObject sun = Sun.create(gameObjects, SUN_LAYER, windowDimensions, DAY_NIGHT_CYCLE_LENGTH);
        SunHalo.create(gameObjects, SUN_HALO_LAYER, sun, SUN_HALO_COLOR);
        // Create trees:
        this.tree = new Tree(gameObjects, terrain, TRUNK_LAYER, LEAF_LAYER, SEED);
        tree.createInRange((int) startWorld, (int) endWorld);
        // Create avatar and energy counter:
        this.avatar = Avatar.create(gameObjects, AVATAR_LAYER, getAvatarTopLeftCorner(), inputListener,
                imageReader);
        EnergyCounter.create(gameObjects, avatar.getEnergy(), ENERGY_COUNTER_LAYER);
    }

    /*
     * Calculates the top left corner coordinates of the avatar.
     */
    private Vector2 getAvatarTopLeftCorner() {
        Random random = new Random(SEED);
        int x;
        do {
            int lowerBound = (int) (MIN_AVATAR_POSITION_COEFFICIENT * windowDimensions.x());
            int upperBound = (int) (MAX_AVATAR_POSITION_COEFFICIENT * windowDimensions.x());
            x = Block.getRoundedCoordinate(random.nextInt((upperBound - lowerBound)) + lowerBound);
        } while (tree.isTreeInX(x));
        return new Vector2(x, terrain.groundHeightAt(x));
    }

    /*
     * Defines the objects of which layers (not empty) are able to collide with each other.
     */
    private void setLayersBehavior() {
        gameObjects().layers().shouldLayersCollide(TERRAIN_LAYER, LEAF_LAYER, true);
        gameObjects().layers().shouldLayersCollide(TRUNK_LAYER, AVATAR_LAYER, true);
        gameObjects().layers().shouldLayersCollide(TERRAIN_LAYER, AVATAR_LAYER, true);
    }

    /*
     * Positions the camera at the avatar (s.t. the avatar would be in the center of the screen).
     */
    private void setCamera(WindowController windowController) {
        Vector2 cameraOffset = windowController.getWindowDimensions().mult(
                CENTRAL_CAMERA_COEFFICIENT).subtract(avatar.getCenter());
        setCamera(new Camera(avatar, cameraOffset, windowController.getWindowDimensions(),
                windowController.getWindowDimensions()));
    }

    /*
     * Shifts the world borders to the left, creates the terrain and the trees to the left from the previous
     * left world border to the new one, removes all the terrain and tree blocks from the left that don't
     * fit in the new world borders.
     */
    private void expandFromLeft() {
        removeObjectsFromSide(endWorld - WORLD_BORDER_DELTA, false);
        createNewObjectsInRange(startWorld - WORLD_BORDER_DELTA + Block.SIZE, startWorld);
        startWorld -= WORLD_BORDER_DELTA;
        endWorld -= WORLD_BORDER_DELTA;
    }

    /*
     * Shifts the world borders to the right, creates the terrain and the trees to the right from the previous
     * right world border to the new one, removes all the terrain and tree blocks from the left that don't
     * fit in the new world borders.
     */
    private void expandFromRight() {
        removeObjectsFromSide(startWorld - WORLD_BORDER_DELTA, true);
        createNewObjectsInRange(endWorld + Block.SIZE, endWorld + WORLD_BORDER_DELTA);
        startWorld += WORLD_BORDER_DELTA;
        endWorld += WORLD_BORDER_DELTA;
    }

    /*
     * Removes all the terrain and tree blocks from the left that don't fit in the new world borders from a
     * particular size. If flag is true, then tries to remove from the left. Tries to remove from the right
     * otherwise.
     */
    private void removeObjectsFromSide(float coordinate, boolean flag) {
        for (GameObject object : gameObjects) {
            float objectTopLeft = object.getTopLeftCorner().x();
            if (flag && objectTopLeft < coordinate) {
                removeObject(object);  // remove from the left.
            } else if (!flag && objectTopLeft > coordinate) {
                removeObject(object);  // remove from the right.
            }
        }
    }

    /*
     * Removes the given object from the game object collection.
     */
    private void removeObject(GameObject object) {
        switch (object.getTag()) {
            case Terrain.GROUND_TAG:
                gameObjects.removeGameObject(object, TERRAIN_LAYER);
                return;
            case Trunk.TRUNK_TAG:
                gameObjects.removeGameObject(object, TRUNK_LAYER);
                return;
            case LeafBlock.LEAF_TAG:
                gameObjects.removeGameObject(object, LEAF_LAYER);
        }
    }

    /*
     * Creates the terrain and the trees in the given range.
     */
    private void createNewObjectsInRange(float startRange, float endRange) {
        terrain.createInRange((int) startRange, (int) endRange);
        tree.createInRange((int) startRange, (int) endRange);
    }
}
