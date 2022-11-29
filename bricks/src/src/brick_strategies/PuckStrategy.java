package src.brick_strategies;

import danogl.GameObject;
import danogl.gui.ImageReader;
import danogl.gui.Sound;
import danogl.gui.SoundReader;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;
import src.gameobjects.Puck;
import java.util.Random;

/**
 * Concrete class extending abstract RemoveBrickStrategyDecorator. Introduces several pucks to the game
 * window instead of the brick once removed.
 */
public class PuckStrategy extends RemoveBrickStrategyDecorator {

    private static final int NUMBER_OF_PUCKS = 3;
    private static final String PUCK_TEXTURE_PATH = "assets/mockBall.png";
    private static final String PUCK_SOUND_PATH = "assets/blop_cut_silenced.wav";
    private static final int PUCK_VELOCITY = 150;

    private static final Random random = new Random();
    private final CollisionStrategy toBeDecorated;
    private final ImageReader imageReader;
    private final SoundReader soundReader;

    /**
     * Constructor.
     * @param toBeDecorated the CollisionStrategy object to be decorated.
     * @param imageReader an ImageReader instance for reading images from files for rendering of objects.
     * @param soundReader a SoundReader instance for reading soundclips from files for rendering event sounds.
     */
    public PuckStrategy(CollisionStrategy toBeDecorated, ImageReader imageReader, SoundReader soundReader) {
        super(toBeDecorated);
        this.toBeDecorated = toBeDecorated;
        this.imageReader = imageReader;
        this.soundReader = soundReader;
    }

    /**
     * To be called on brick collision. Adds pucks to the game on collision and delegates to the held
     * CollisionStrategy object.
     * @param thisObj the brick game object.
     * @param otherObj the other GameObject instance participating in collision.
     * @param counter the global counter of the brick game objects.
     */
    @Override
    public void onCollision(GameObject thisObj, GameObject otherObj, Counter counter) {
        createPucks(thisObj);
        toBeDecorated.onCollision(thisObj, otherObj, counter);
    }

    private void setPuckVelocity(GameObject puck) {
        float puckVelX = PUCK_VELOCITY;
        if (random.nextBoolean()) {
            puckVelX *= -1;
        }
        puck.setVelocity(new Vector2(puckVelX, (float) PUCK_VELOCITY));
    }

    private float createOnePuck(GameObject thisObj, Renderable puckImage, Sound collisionSound, float puckX) {
        float puckDiameter = Math.max(thisObj.getDimensions().x(),
                thisObj.getDimensions().y()) / NUMBER_OF_PUCKS;
        Vector2 topLeftCorner = new Vector2(puckX, thisObj.getTopLeftCorner().y());
        Vector2 dimensions = new Vector2(puckDiameter, puckDiameter);
        GameObject puck = new Puck(topLeftCorner, dimensions, puckImage, collisionSound);
        setPuckVelocity(puck);
        toBeDecorated.getGameObjectCollection().addGameObject(puck);
        return puckX + puckDiameter;
    }

    private void createPucks(GameObject thisObj) {
        Sound collisionSound = soundReader.readSound(PUCK_SOUND_PATH);
        Renderable puckImage = imageReader.readImage(PUCK_TEXTURE_PATH, true);
        float puckX = thisObj.getTopLeftCorner().x();
        for (int i = 0 ; i < NUMBER_OF_PUCKS ; i++) {
            puckX = createOnePuck(thisObj, puckImage, collisionSound, puckX);
        }
    }
}
