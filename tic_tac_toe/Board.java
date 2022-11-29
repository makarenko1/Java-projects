/**
 * Represents a Tic-Tac-Toe board. The board:
 * 1) Can be displayed;
 * 2) Knows when the game is over and how;
 * 3) Provides with information about the markings in the various coordinates.
 */
public class Board {

    /**
     * The size of the board (number of rows and columns in it).
     */
    public static final int SIZE = 6;
    /**
     * The number of consecutive cells on the board to be filled with the same mark to win.
     */
    public static final int WIN_STREAK = 4;

    private final Mark[][] board;

    /**
     * Constructor. Initializes the board to be empty.
     */
    public Board() {
        board = new Mark[SIZE][SIZE];
        for (int row = 0 ; row < SIZE ; row++) {
            for (int col = 0 ; col < SIZE ; col++) {
                board[row][col] = Mark.BLANK;
            }
        }
    }

    /**
     * Puts the given mark in the cell at the given coordinates (row, column) if they are legal (the cell at
     * these coordinates is within the board and is empty).
     * @param mark the mark to put.
     * @param row the row to put the mark at.
     * @param col the column to put the mark at.
     * @return true if the mark was put, false otherwise (if the coordinates were illegal).
     */
    public boolean putMark(Mark mark, int row, int col) {
        if (!checkCellWithinBoard(row, col) || board[row][col] != Mark.BLANK) {
            return false;
        }
        board[row][col] = mark;
        return true;
    }

    /**
     * Returns the mark at the given coordinates (row, column) if they are legal.
     * @param row the row to return the mark from.
     * @param col the column to return the mark from.
     * @return the mark at the given coordinates if they are legal, blank (empty cell) otherwise.
     */
    public Mark getMark(int row, int col) {
        if (!checkCellWithinBoard(row, col)) {
            return Mark.BLANK;
        }
        return board[row][col];
    }

    /**
     * Determines the winning player. That is, determines the player that performed a winning streak if
     * there is such streak.
     * @return the sign of the winning player or blank if there is a draw or if the game hasn't ended.
     */
    public Mark getWinner() {
        for (int row = 0 ; row < SIZE ; row++) {
            for (int col = 0 ; col < SIZE ; col++) {
                Mark winner = findWinningStreak(row, col);
                if (winner != Mark.BLANK) {
                    return winner;
                }
            }
        }
        return Mark.BLANK;
    }

    /**
     * Checks if the game has ended. That is, checks if there is a player who performed a winning streak or
     * there are no empty cells left on the board.
     * @return true if the game has ended, false otherwise.
     */
    public boolean gameEnded() {
        return getWinner() != Mark.BLANK || checkAllCellsTaken();
    }

    private static boolean checkCellWithinBoard(int row, int col) {
        return (row >= 0 && col >= 0 && row < SIZE && col < SIZE);
    }

    private boolean checkAllCellsTaken() {
        for (Mark[] row : board) {
            for (Mark mark : row) {
                if (mark == Mark.BLANK) {
                    return false;
                }
            }
        }
        return true;
    }

    private int countMarkInDirection(int row, int col, int rowDelta, int colDelta, Mark mark) {
        int count = 0;
        while (checkCellWithinBoard(row, col) && board[row][col] == mark) {
            count++;
            if (rowDelta == 0 && colDelta == 0) {
                return count;
            }
            row += rowDelta;
            col += colDelta;
        }
        return count;
    }

    private Mark findWinningStreak(int row, int col) {
        for (int rowDelta = -1 ; rowDelta <= 1 ; rowDelta++) {
            for (int colDelta = -1 ; colDelta <= 1; colDelta++) {
                if (countMarkInDirection(row, col, rowDelta, colDelta, Mark.X) >= WIN_STREAK) {
                    return Mark.X;
                } else if (countMarkInDirection(row, col, rowDelta, colDelta, Mark.O) >= WIN_STREAK) {
                    return Mark.O;
                }
            }
        }
        return Mark.BLANK;
    }
}
