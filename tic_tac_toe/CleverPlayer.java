import java.util.Random;

/**
 * Represents a clever Tic-Tac-Toe player (that can beat the random player).
 */
public class CleverPlayer implements Player {

    private static final Random random = new Random();

    /**
     * Makes one move in the game. The move is semi-random: a random row is chosen and then the player
     * tries to make a move in each column at that row in order.
     * @param board the game board.
     * @param mark the mark to put on the board.
     */
    @Override
    public void playTurn(Board board, Mark mark) {
        while(true) {
            int randomRow = random.nextInt(Board.SIZE);
            for (int col = 0 ; col < Board.SIZE ; col++) {
                if (board.putMark(mark, randomRow, col)) {
                    return;
                }
            }
        }
    }
}
