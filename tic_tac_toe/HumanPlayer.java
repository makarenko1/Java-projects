import java.util.Scanner;

/**
 * Represents a human Tic-Tac-Toe player.
 */
public class HumanPlayer implements Player {

    private static final Scanner scanner = new Scanner(System.in);
    private static final String ASK_INPUT_MESSAGE = "Player %s, type coordinates: ";
    private static final String INVALID_INPUT_MESSAGE = "Invalid coordinates, type again: ";

    /**
     * Makes one move in the game: first asks for legal coordinates to put the mark at, then puts the mark
     * on the board at these coordinates.
     * @param board the game board.
     * @param mark the mark to put on the board.
     */
    @Override
    public void playTurn(Board board, Mark mark) {
        System.out.print(String.format(ASK_INPUT_MESSAGE, mark));
        int[] userInput = getUserInput();
        while (!board.putMark(mark, userInput[0], userInput[1])) {
            System.err.print(INVALID_INPUT_MESSAGE);
            userInput = getUserInput();
        }
    }

    private static int[] getUserInput() {
        int coordinates = scanner.nextInt();
        int row = coordinates / 10 - 1; // The coordinates start at 1 for the players.
        int col = coordinates % 10 - 1;
        return new int[] {row, col};
    }
}
