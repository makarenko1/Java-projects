/**
 * Represents a game board renderer (the module that shows the game board to the user). The renderer can
 * (only) show the game board.
 */
public interface Renderer {
    /**
     * Represents the supplied board (in some way).
     * @param board the board to represent.
     */
    void renderBoard(Board board);
}
