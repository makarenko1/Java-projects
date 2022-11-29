/**
 * Represents a Tic-Tac-Toe player. The player can (only) make a move in the game.
 */
public interface Player {
    /**
     * Makes one move in the game (puts the given mark on the given board at some cell).
     * @param board the game board.
     * @param mark the mark to put on the board.
     */
    void playTurn(Board board, Mark mark);
}