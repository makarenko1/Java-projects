/**
 * The Factory class for the Player module.
 */
public class PlayerFactory {

    private static final String HUMAN_PLAYER = "human";
    private static final String WHATEVER_PLAYER = "whatever";
    private static final String CLEVER_PLAYER = "clever";
    private static final String SNARTYPAMTS_PLAYER = "snartypamts";

    /**
     * Creates a player in the game according to the given type.
     * @param playerType the type of the player.
     * @return an object of the class of the given type that implements the interface Player.
     */
    Player buildPlayer(String playerType) {
        switch (playerType) {
            case HUMAN_PLAYER:
                return new HumanPlayer();
            case WHATEVER_PLAYER:
                return new WhateverPlayer();
            case CLEVER_PLAYER:
                return new CleverPlayer();
            case SNARTYPAMTS_PLAYER:
                return new SnartypamtsPlayer();
            default:
                return null;
        }
    }
}
