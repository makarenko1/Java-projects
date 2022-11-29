package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import java.awt.*;

/**
 * Represents a sun in the simulation.
 */
public class Sun {

    // Basic parameters:
    private static final String SUN_TAG = "sun";
    private static final Color SUN_COLOR = Color.YELLOW;
    private static final float SUN_SIZE_FACTOR = 0.15f;

    // Route angles
    private static final float START_ANGLE = 0f;
    private static final float END_ANGLE = 360f;
    private static final double ANGLE_OFFSET = 90;

    // Route coefficients:
    private static final float ELLIPSE_FACTOR = 0.4f;
    private static final float WINDOW_DIMENSIONS_X_FACTOR = 0.5f;
    private static final float WINDOW_DIMENSIONS_Y_FACTOR = 0.6f;

    /**
     * This function creates a yellow circle that moves in the sky in an elliptical path (in camera
     * coordinates).
     * @param gameObjects the global game object collection managed by the simulation manager.
     * @param layer the layer for the sun.
     * @param windowDimensions the dimensions of the width, height of the simulation window in pixels.
     * @param cycleLength the amount of seconds it should take the created game object to complete a full
     *                    cycle.
     * @return a new game object representing the sun transitions.
     */
    public static GameObject create(GameObjectCollection gameObjects, int layer, Vector2 windowDimensions,
                                    float cycleLength) {
        float diameter = SUN_SIZE_FACTOR * windowDimensions.x();
        Vector2 dimensions = new Vector2(diameter, diameter);
        Renderable renderable = new OvalRenderable(SUN_COLOR);
        GameObject sun = new GameObject(Vector2.ZERO, dimensions, renderable);
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sun.setTag(SUN_TAG);
        gameObjects.addGameObject(sun, layer);
        new Transition<>(sun, angle -> sun.setCenter(calcSunPosition(windowDimensions, angle)),
                START_ANGLE, END_ANGLE, Transition.LINEAR_INTERPOLATOR_FLOAT, cycleLength,
                Transition.TransitionType.TRANSITION_LOOP, null);
        return sun;
    }

    /*
     * Calculates and returns the needed sun center position relative to the window dimensions and the
     * given angle.
     */
    private static Vector2 calcSunPosition(Vector2 windowDimensions, float angleInSky) {
        double angle = -Math.toRadians(angleInSky + ANGLE_OFFSET);
        float x = (float) (Math.cos(angle) * ELLIPSE_FACTOR * windowDimensions.x() +
                WINDOW_DIMENSIONS_X_FACTOR * windowDimensions.x());
        float y = (float) (Math.sin(angle) * ELLIPSE_FACTOR * windowDimensions.y() +
                WINDOW_DIMENSIONS_Y_FACTOR * windowDimensions.y());
        return new Vector2(x, y);
    }
}
