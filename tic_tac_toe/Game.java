/**
 * Represents a single Tic-Tac-Toe game.
 */
public class Game {

    private final Player playerX;
    private final Player playerO;
    private final Renderer renderer;

    /**
     * Constructor.
     * @param playerX the first player (that has the mark X in the game).
     * @param playerO the second player (that has the mark O).
     * @param renderer represents the game board.
     */
    public Game(Player playerX, Player playerO, Renderer renderer) {
        this.playerX = playerX;
        this.playerO = playerO;
        this.renderer = renderer;
    }

    /**
     * Runs a single Tic-Tac-Toe game.
     * @return the mark of the winning player, or blank in case of a draw.
     */
    public Mark run() {
        Board board = new Board();
        Player[] players = {playerX, playerO};
        Mark[] marks = {Mark.X, Mark.O};
        int counter = 0;
        while (!board.gameEnded()) {
            renderer.renderBoard(board);
            players[counter % 2].playTurn(board, marks[counter % 2]);
            counter++;
        }
        renderer.renderBoard(board);
        return board.getWinner();
    }
}
