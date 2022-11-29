package src.gameobjects;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Counter;
import danogl.util.Vector2;
import java.awt.*;

/**
 * Represents a graphic object in the game window showing a numeric count of lives left.
 */
public class NumericLifeCounter extends GameObject {

    private final Counter livesCounter;
    private final TextRenderable textRenderable;

    /**
     * Constructor. Constructs a new GameObject instance.
     * @param livesCounter the global counter of lives left in the game.
     * @param topLeftCorner the top left corner of the renderable.
     * @param dimensions the dimensions of the renderable.
     * @param gameObjectCollection the global game object collection managed by the game manager.
     */
    public NumericLifeCounter(Counter livesCounter, Vector2 topLeftCorner, Vector2 dimensions,
                              GameObjectCollection gameObjectCollection) {
        super(topLeftCorner, dimensions, null);
        this.livesCounter = livesCounter;
        this.textRenderable = new TextRenderable(Integer.toString(livesCounter.value()));
        textRenderable.setColor(Color.ORANGE);
        gameObjectCollection.addGameObject(new GameObject(topLeftCorner, dimensions, textRenderable),
                Layer.BACKGROUND);
    }

    /**
     * Updates the counter in the game. Displays exactly the number of lives left.
     * @param deltaTime the time passed from the previous update.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        textRenderable.setString(Integer.toString(livesCounter.value()));
    }
}

