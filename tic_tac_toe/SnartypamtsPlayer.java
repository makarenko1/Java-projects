/**
 * Represents a Tic-Tac-Toe player that can beat the clever player.
 */
public class SnartypamtsPlayer implements Player {

    /**
     * Makes one move in the game. Subsequently, tries to make a move in each cell in the board.
     * @param board the game board.
     * @param mark the mark to put on the board.
     */
    @Override
    public void playTurn(Board board, Mark mark) {
        for (int row = 0 ; row < Board.SIZE ; row++) {
            for (int col = 0 ; col < Board.SIZE ; col++) {
                if (board.putMark(mark, row, col)) {
                    return;
                }
            }
        }
    }
}
