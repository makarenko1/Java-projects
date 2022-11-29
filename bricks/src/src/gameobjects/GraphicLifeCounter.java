package src.gameobjects;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;

/**
 * Represents a graphic object in the game window showing as many widgets as lives left.
 */
public class GraphicLifeCounter extends GameObject {

    private static final int LIFE_WIDGET_BUFFER = 5;

    private final Counter livesCounter;
    private final Renderable renderable;
    private final GameObjectCollection gameObjectCollection;
    private final int numOfLives;
    private GameObject[] lifeWidgets;

    /**
     * Constructor. Constructs a new GameObject instance.
     * @param widgetTopLeftCorner the top left corner of left most life widgets. Other widgets will be
     *                            displayed to its right, aligned in height.
     * @param widgetDimensions the width and height of each widget in window coordinates.
     * @param livesCounter the global counter of lives left in the game.
     * @param widgetRenderable the renderable representing the widget.
     * @param gameObjectCollection the global game object collection managed by the game manager.
     * @param numOfLives the global setting of number of lives a player will have in a game.
     */
    public GraphicLifeCounter(Vector2 widgetTopLeftCorner, Vector2 widgetDimensions, Counter livesCounter,
                              Renderable widgetRenderable, GameObjectCollection gameObjectCollection,
                              int numOfLives) {
        super(widgetTopLeftCorner, widgetDimensions, null);
        this.livesCounter = livesCounter;
        this.renderable = widgetRenderable;
        this.gameObjectCollection = gameObjectCollection;
        this.numOfLives = numOfLives;
        createLifeWidgets(widgetTopLeftCorner, widgetDimensions);
    }

    /**
     * Updates the counter in the game. Displays exactly the same number of widgets as lives left.
     * @param deltaTime the time passed from the previous update.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (int i = livesCounter.value(); i < numOfLives ; i++) {
            gameObjectCollection.removeGameObject(lifeWidgets[i], Layer.BACKGROUND);
        }
    }

    private float createOneLifeWidget(Vector2 topLeftCorner, Vector2 dimensions, float lifeWidgetX, int i) {
        lifeWidgets[i] = new GameObject(new Vector2(lifeWidgetX, topLeftCorner.y()), new Vector2(
                dimensions.x(), dimensions.y()), renderable);
        gameObjectCollection.addGameObject(lifeWidgets[i], Layer.BACKGROUND);
        return lifeWidgetX + (dimensions.x() + LIFE_WIDGET_BUFFER);
    }

    private void createLifeWidgets(Vector2 topLeftCorner, Vector2 dimensions) {
        this.lifeWidgets = new GameObject[numOfLives];
        float lifeWidgetX = topLeftCorner.x() + LIFE_WIDGET_BUFFER;
        for (int i = 0; i < numOfLives ; i++) {
            lifeWidgetX = createOneLifeWidget(topLeftCorner, dimensions, lifeWidgetX, i);
        }
    }
}
