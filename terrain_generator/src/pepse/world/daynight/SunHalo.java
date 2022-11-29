package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import java.awt.*;

/**
 * Represents a sun halo in the simulation.
 */
public class SunHalo {

    private static final String SUN_HALO_TAG = "sun halo";
    private static final float SUN_HALO_SIZE_FACTOR = 2.5f;

    /**
     * This function creates a halo around a given object that represents the sun. The halo will be tied to
     * the given sun, and will always move with it.
     * @param gameObjects the global game object collection managed by the simulation manager.
     * @param layer the layer for the halo.
     * @param sun the game object representing the sun (it will be followed by the created game object).
     * @param color the color of the halo.
     * @return a new game object representing the sun's halo.
     */
    public static GameObject create(GameObjectCollection gameObjects, int layer, GameObject sun,
                                    Color color) {
        Renderable renderable = new OvalRenderable(color);
        GameObject sunHalo = new GameObject(sun.getTopLeftCorner(),
                sun.getDimensions().mult(SUN_HALO_SIZE_FACTOR), renderable);
        sunHalo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sunHalo.setTag(SUN_HALO_TAG);
        sunHalo.addComponent((x) -> sunHalo.setCenter(sun.getCenter()));
        gameObjects.addGameObject(sunHalo, layer);
        return sunHalo;
    }
}
