package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import java.awt.*;

/**
 * Represents a sky in the simulation.
 */
public class Sky {

    private static final String SKY_TAG = "sky";
    private static final Color BASIC_SKY_COLOR = Color.decode("#80C6E5");

    /**
     * Constructor. Constructs a sky (GameObject instance).
     * @param gameObjects the global game object collection managed by the simulation manager.
     * @param windowDimensions the width, height of the simulation window in pixels.
     * @param skyLayer the layer for the sky.
     * @return a new game object representing the sky.
     */
    public static GameObject create(GameObjectCollection gameObjects, Vector2 windowDimensions,
                                    int skyLayer) {
        GameObject sky = new GameObject(Vector2.ZERO, windowDimensions, new RectangleRenderable(
                BASIC_SKY_COLOR));
        sky.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(sky, skyLayer);
        sky.setTag(SKY_TAG);
        return sky;
    }
}
