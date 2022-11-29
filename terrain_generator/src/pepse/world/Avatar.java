package pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;
import pepse.world.trees.Trunk;
import java.awt.event.KeyEvent;

/**
 * Represents an avatar (a character that is controllable by the user and can move in the simulation).
 */
public class Avatar extends GameObject {

    // Size parameters:
    private static final int AVATAR_WIDTH = 40;
    private static final int AVATAR_HEIGHT = 70;

    // Velocity parameters:
    private static final int VELOCITY = -300;
    private static final int MAX_Y_VELOCITY = 400;

    // Energy parameters:
    private static final int GRAVITY_ACCELERATION = 500;
    private static final int INITIAL_ENERGY = 100;
    private static final float DELTA_ENERGY = 0.5f;

    // Paths to image files:
    private static final String AVATAR_IMAGE = "pepse/assets/avatar1.png";
    private static final String AVATAR_IMAGE_LEFT_LEG = "pepse/assets/avatar2.png";
    private static final String AVATAR_IMAGE_RIGHT_LEG = "pepse/assets/avatar3.png";

    // Animation parameters:
    private static final float TIME_BETWEEN_ANIMATION_CLIPS = 0.2f;
    private static final int FLY_INCLINE_ANGLE = -30;

    private final UserInputListener inputListener;
    private final Renderable generalRenderable;
    private final Renderable animatedRenderable;
    private float energy;
    private Counter energyToDisplay;
    private boolean isFlying;

    /*
     * Constructor. Constructs an avatar (GameObject instance).
     */
    private Avatar(Vector2 topLeftCorner, UserInputListener inputListener, ImageReader imageReader) {
        super(topLeftCorner, new Vector2(AVATAR_WIDTH, AVATAR_HEIGHT), null);
        this.inputListener = inputListener;
        this.generalRenderable = imageReader.readImage(AVATAR_IMAGE, true);
        Renderable renderableLeftLeg = imageReader.readImage(AVATAR_IMAGE_LEFT_LEG, true);
        Renderable renderableRightLeg = imageReader.readImage(AVATAR_IMAGE_RIGHT_LEG, true);
        this.animatedRenderable = new AnimationRenderable(new Renderable[] {renderableLeftLeg,
                renderableRightLeg}, TIME_BETWEEN_ANIMATION_CLIPS);
        setInitialParameters();
    }

    /**
     * This function creates an avatar that can travel the world and is followed by the camera. He can
     * stand, walk, jump and fly, and never reaches the end of the world.
     * @param gameObjects the global game object collection managed by the simulation manager.
     * @param layer the layer for the avatar.
     * @param topLeftCorner the position of the avatar, in window coordinates.
     * @param inputListener an InputListener instance for reading user input.
     * @param imageReader an ImageReader instance for reading images from files for rendering of objects.
     * @return a new game object representing the avatar.
     */
    public static Avatar create(GameObjectCollection gameObjects, int layer, Vector2 topLeftCorner,
                                UserInputListener inputListener, ImageReader imageReader) {
        topLeftCorner = new Vector2(topLeftCorner.x(), topLeftCorner.y() - AVATAR_HEIGHT);
        Avatar avatar = new Avatar(topLeftCorner, inputListener, imageReader);
        gameObjects.addGameObject(avatar, layer);
        return avatar;
    }

    /**
     * Defines if the leaf is able to collide with the given object. The avatar can collide with the ground
     * blocks and with the trunk blocks.
     * @param other another object in the simulation.
     * @return true iff the leaf is able to collide with the given object.
     */
    @Override
    public boolean shouldCollideWith(GameObject other) {
        return (other.getTag().equals(Trunk.TRUNK_TAG) || other.getTag().equals(Terrain.GROUND_TAG));
    }

    /**
     * Defines what the avatar does if at the moment it collides with the given object. Sets the velocity of
     * the avatar to zero.
     * @param other another object in the simulation (with which the avatar is able to collide).
     * @param collision the object that represents the collision.
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        setVelocity(Vector2.ZERO);
    }

    /**
     * Code in this function is run every frame update. Updates the avatar's energy, checks the user input,
     * sets the animation accordingly, if the velocity by the y-axis is too big, make the velocity in borders.
     * @param deltaTime the time between updates. For internal use by game engine.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        updateEnergy();
        checkInput();
        setAnimation();
        makeVelocityInBorders();
    }

    /**
     * Getter.
     * @return the avatar's energy counter.
     */
    public Counter getEnergy() {
        return energyToDisplay;
    }

    /*
     * Sets the initial parameters of the avatar: its size, its physics, its acceleration by the y-axis, and
     * its energy.
     */
    private void setInitialParameters() {
        renderer().setRenderable(generalRenderable);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        transform().setAccelerationY(GRAVITY_ACCELERATION);
        this.energy = INITIAL_ENERGY;
        this.energyToDisplay = new Counter(INITIAL_ENERGY);
    }

    /*
     * Updates the avatar's energy. If the avatar flies, we decrement the energy. And if it is on the
     * ground or on a tree, the energy is incremented. We then make the changed energy in borders and update
     * the energy to display if there was any change.
     */
    private void updateEnergy() {
        if (isFlying) {
            energy = Math.max(0, energy - DELTA_ENERGY);
        } else if (getVelocity().y() == 0) {
            energy = Math.min(energy + DELTA_ENERGY, INITIAL_ENERGY);
        }
        int deltaEnergyToDisplay = (int) Math.floor(energy - energyToDisplay.value());
        energyToDisplay.increaseBy(deltaEnergyToDisplay);
    }

    /*
     * Checks the user input and reacts accordingly.
     */
    private void checkInput() {
        checkFly();
        checkJump();
        checkWalk();
    }

    /*
     * Checks if the avatar is flying. That is, checks if the energy is bigger than 0, and the user pressed
     * both the space and the shift keys. Sets the boolean flag accordingly.
     */
    private void checkFly() {
        if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) && inputListener.isKeyPressed(KeyEvent.VK_SHIFT) &&
                energy > 0) {
            isFlying = true;
            transform().setVelocityY(VELOCITY);
        }
        else {
            isFlying = false;
        }
    }

    /*
     * Checks if the avatar is jumping. That is, checks if its velocity by the y-axis is zero (it is on some
     * surface), and the user pressed the space key.
     */
    private void checkJump() {
        if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) && getVelocity().y() == 0) {
            transform().setVelocityY(VELOCITY);
        }
    }

    /*
     * Checks if the avatar is walking. That is, checks if the user pressed just one of the left and right
     * arrow keys. Sets the current renderable accordingly. Sets the angle of the renderable according to
     * the current direction on the x-axis if the avatar is flying.
     */
    private void checkWalk() {
        float xVel = 0;
        if (inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
            xVel += VELOCITY;
            renderer().setIsFlippedHorizontally(true);
        } if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
            xVel -= VELOCITY;
            renderer().setIsFlippedHorizontally(false);
        }
        setAngle();
        transform().setVelocityX(xVel);
    }

    /*
     * Sets the angle of the renderable if the avatar is currently flying and going to some direction by
     * the x-axis.
     */
    private void setAngle() {
        if (isFlying && getVelocity().x() != 0) {
            renderer().setRenderableAngle(FLY_INCLINE_ANGLE);
        } else {
            renderer().setRenderableAngle(0);
        }
    }

    /*
     * Sets the avatar's renderable according to its current direction. The general renderable is used when
     * the avatar is jumping or standing straight (iff its velocity by the x-axis is 0). The animated
     * renderable is used in any other case.
     */
    private void setAnimation() {
        if (getVelocity().x() == 0) {
            renderer().setRenderable(generalRenderable);
        } else {
            renderer().setRenderable(animatedRenderable);
        }
    }

    /*
     * Makes the velocity by the y-axis in bounds if the avatar is not going up.
     */
    private void makeVelocityInBorders() {
        if (!isFlying) {
            float x = getVelocity().x();
            float y = Math.min(MAX_Y_VELOCITY, getVelocity().y());
            setVelocity(new Vector2(x, y));
        }
    }
}
