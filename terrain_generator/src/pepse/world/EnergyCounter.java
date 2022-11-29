package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Counter;
import danogl.util.Vector2;

/**
 * Represents a counter for the avatar's energy in the simulation.
 */
public class EnergyCounter extends GameObject {

    // Size parameters:
    private static final int COUNTER_WIDTH = 50;
    private static final int COUNTER_HEIGHT = 30;

    private static final String MESSAGE = "Energy: %d";  // output parameter.

    private final Counter energy;
    private final TextRenderable textRenderable;

    /*
     * Constructor. Constructs an energy counter (GameObject instance).
     */
    private EnergyCounter(GameObjectCollection gameObjects, Counter energy, int layer) {
        super(Vector2.ZERO, new Vector2(COUNTER_WIDTH, COUNTER_HEIGHT), null);
        this.energy = energy;
        this.textRenderable = new TextRenderable(Integer.toString(this.energy.value()));
        GameObject text = new GameObject(Vector2.ZERO, new Vector2(COUNTER_WIDTH, COUNTER_HEIGHT),
                textRenderable);
        text.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(text, layer);
    }

    /**
     * This function creates a rectangular object in the top-left corner of the screen that shows how many
     * energy points the avatar has currently.
     * @param gameObjects the global game object collection managed by the simulation manager.
     * @param energy the global counter of the avatar's energy.
     * @param layer the layer for the energy counter.
     * @return a new game object representing the avatar's energy counter.
     */
    public static GameObject create(GameObjectCollection gameObjects, Counter energy, int layer) {
        EnergyCounter energyCounter = new EnergyCounter(gameObjects, energy, layer);
        gameObjects.addGameObject(energyCounter, layer);
        return energyCounter;
    }

    /**
     * Code in this function is run every frame update. Updates currently outputted message.
     * @param deltaTime the time between updates. For internal use by game engine.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        textRenderable.setString(String.format(MESSAGE, energy.value()));
    }
}

