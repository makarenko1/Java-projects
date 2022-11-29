package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Block;
import pepse.world.Terrain;
import java.util.Objects;
import java.util.Random;

/**
 * Represents a leaf in the simulation. Each leaf rotates on the wind when on the tree, falls from the tree,
 * disappears and appears on the tree again at the same place as it was.
 */
public class LeafBlock extends GameObject {

    /**
     * The leaf tag. Is needed for removing the leaf objects from the game object collection.
     */
    public static final String LEAF_TAG = "leaf";

    // Parameters for size:
    private static final int LEAF_SIZE = Block.SIZE;
    private static final float CHANGE_WIDTH_FACTOR = 1.2f;  // for changing size while rotating on the tree.

    // Parameters for time:
    private static final int MAX_LIFE_TIME = 800;  // max time until falls from the tree.
    private static final int MAX_DEATH_TIME = 50;  // max time until appears on the tree again.
    private static final int MAX_START_BEHAVIOR_TIME = 3;  // max time until starts rotating on the tree.
    private static final int FADEOUT_TIME = 20;  // max time until disappears on the ground.
    private static final float ON_TREE_CYCLE_TIME = 1f;  // cycle time of rotation on the tree.
    private static final float FALL_SWING_CYCLE_TIME = 3;  // cycle time of swing by x while falling.

    // Parameters for fall velocity:
    private static final float FALL_VELOCITY_X = 10;
    private static final int FALL_VELOCITY_Y = 50;

    // Other parameters:
    private static final float SWING_ANGLE = 5f;  // defines angle for the rotation on the tree.
    private static final float INITIAL_OPAQUENESS = 1;  // opacity until falls from the tree.

    private final Vector2 onTreePosition;
    private final Terrain terrain;
    private final float lifeTime;
    private final float deathTime;
    private final float startBehaviorTime;
    private boolean isOffTree;
    private Transition<Float> angleTransition;
    private Transition<Float> widthTransition;
    private Transition<Float> horizontalTransition;

    /**
     * Constructor. Constructs a leaf (GameObject instance).
     * @param topLeftCorner the position of the leaf, in window coordinates.
     * @param renderable the renderable representing the leaf.
     * @param terrain the object that represents the ground in the simulation.
     * @param seed defines randomness for the whole simulation.
     */
    public LeafBlock(Vector2 topLeftCorner, Renderable renderable, Terrain terrain, int seed) {
        super(topLeftCorner, Vector2.ONES.mult(LEAF_SIZE), renderable);
        this.onTreePosition = topLeftCorner;
        this.terrain = terrain;
        Random random = new Random(Objects.hash(topLeftCorner, seed));
        this.lifeTime = random.nextFloat() * MAX_LIFE_TIME;
        this.deathTime = random.nextFloat() * MAX_DEATH_TIME;
        this.startBehaviorTime = random.nextFloat() * MAX_START_BEHAVIOR_TIME;
        setTag(LEAF_TAG);
        createOnTreeBehavior();
    }

    /**
     * Defines if the leaf is able to collide with the given object. The leaf can collide with the blocks of
     * the first two layers of the ground, if the leaf is not on the tree and if its velocity isn't equal
     * to zero.
     * @param other another object in the simulation.
     * @return true iff the leaf is able to collide with the given object.
     */
    @Override
    public boolean shouldCollideWith(GameObject other) {
        return isOffTree && !this.getVelocity().isZero() && other.getTag().equals(Terrain.GROUND_TAG) &&
                 terrain.isBlockInFirstTwoLayers(other);
    }

    /**
     * Defines what the leaf does if at the moment it collides with the given object. Turns off all the
     * special moves of the leaves (swing, size change).
     * @param other another object in the simulation (with which the leaf is able to collide).
     * @param collision the object that represents the collision.
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        removeComponent(widthTransition);
        removeComponent(angleTransition);
        removeComponent(horizontalTransition);
    }

    /**
     * Defines what the leaf does while still colliding with the given object. Sets the velocity of the
     * leaf to be zero so that it would stay in place during the collision.
     * @param other another object in the simulation (with which the leaf is able to collide).
     * @param collision the object that represents the collision.
     */
    @Override
    public void onCollisionStay(GameObject other, Collision collision) {
        super.onCollisionStay(other, collision);
        setVelocity(Vector2.ZERO);
    }

    /*
     * Defines the delay before the initial behavior of the leaf (the behavior while it is on the tree).
     * Defines the initial behavior of the leaf and its duration.
     */
    private void createOnTreeBehavior() {
        isOffTree = false;
        new ScheduledTask(this, startBehaviorTime, false,
                this::createOnTreeBehaviorTransitions);
        new ScheduledTask(this, lifeTime, false, this::createOffTreeBehavior);
    }

    /*
     * Defines the initial behavior of the leaf. The leaf rotates around the x-axis and changes its width
     * back and forth to imitate wind blowing.
     */
    private void createOnTreeBehaviorTransitions() {
        angleTransition = new Transition<>(this, renderer()::setRenderableAngle,
                -SWING_ANGLE, SWING_ANGLE, Transition.LINEAR_INTERPOLATOR_FLOAT, ON_TREE_CYCLE_TIME,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
        widthTransition = new Transition<>(this, width -> this.setDimensions(
                new Vector2(width, Block.SIZE)), (float) Block.SIZE, Block.SIZE * CHANGE_WIDTH_FACTOR,
                Transition.LINEAR_INTERPOLATOR_FLOAT, ON_TREE_CYCLE_TIME,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
    }

    /*
     * Defines the behavior of the leaf while it is no longer on the tree. The leaf starts to fall down and
     * fade out, and it swings back and forth on the x-axis to imitate wind blowing. After some defined
     * time the leaf returns on the tree where it was.
     */
    private void createOffTreeBehavior() {
        isOffTree = true;
        renderer().fadeOut(FADEOUT_TIME, () -> new ScheduledTask(this, deathTime,
                false, this::resetLeaf));
        transform().setVelocityY(FALL_VELOCITY_Y);
        horizontalTransition = new Transition<>(this, transform()::setVelocityX,
                -FALL_VELOCITY_X, FALL_VELOCITY_X, Transition.LINEAR_INTERPOLATOR_FLOAT, FALL_SWING_CYCLE_TIME,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
    }

    /*
     * Returns the leaf back on the tree where it was, again defines its initial behavior.
     */
    private void resetLeaf() {
        setTopLeftCorner(onTreePosition);
        renderer().setOpaqueness(INITIAL_OPAQUENESS);
        createOnTreeBehavior();
    }
}
