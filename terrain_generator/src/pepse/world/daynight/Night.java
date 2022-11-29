package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import java.awt.*;

/**
 * Represents a night in the simulation.
 */
public class Night {

    // Basic parameters:
    private static final String NIGHT_TAG = "night";
    private static final Color NIGHT_COLOR = Color.BLACK;

    // Opacity parameters:
    private static final float MIDDAY_OPACITY = 0f;
    private static final float MIDNIGHT_OPACITY = 0.5f;

    /**
     * This function creates a black rectangular game object that covers the entire game window and changes
     * its opaqueness in a cyclic manner, in order to resemble day-to-night transitions.
     * @param gameObjects the global game object collection managed by the simulation manager.
     * @param layer the layer for the night.
     * @param windowDimensions the dimensions of the width, height of the simulation window in pixels.
     * @param cycleLength the amount of seconds it should take the created game object to complete a full
     *                    cycle.
     * @return a new game object representing the day-to-night transitions.
     */
    public static GameObject create(GameObjectCollection gameObjects, int layer, Vector2 windowDimensions,
                                    float cycleLength) {
        Renderable renderable = new RectangleRenderable(NIGHT_COLOR);
        GameObject night = new GameObject(Vector2.ZERO, windowDimensions, renderable);
        night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        night.setTag(NIGHT_TAG);
        gameObjects.addGameObject(night, layer);
        new Transition<>(night, night.renderer()::setOpaqueness, MIDDAY_OPACITY, MIDNIGHT_OPACITY,
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                cycleLength / 2, // transition fully over half a day.
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null);
        return night;
    }
}
