import java.util.Random;

/**
 * Represents a random Tic-Tac-Toe player.
 */
public class WhateverPlayer implements Player {

    private static final Random random = new Random();

    /**
     * Makes one move in the game: puts mark with equal probability at any of the free cells on the game
     * board.
     * @param board the game board.
     * @param mark the mark to put on the board.
     */
    @Override
    public void playTurn(Board board, Mark mark) {
        int row;
        int col;
        do {
            row = random.nextInt(Board.SIZE);
            col = random.nextInt(Board.SIZE);
        } while (!board.putMark(mark, row, col));
    }
}
