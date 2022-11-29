/**
 * Represents a Tic-Tac-Toe tournament that consists of multiple rounds between two players.
 */
public class Tournament {

    private static final int ARG_NUM = 4;
    private static final int ARG_NUM_ROUNDS = 0;
    private static final int ARG_RENDER_TYPE = 1;
    private static final int ARG_PLAYER1_TYPE = 2;
    private static final int ARG_PLAYER2_TYPE = 3;
    private static final int RESULTS_NUM = 3;
    private static final int PLAYER1_RESULT = 0;
    private static final int PLAYER2_RESULT = 1;
    private static final int DRAW_RESULT = 2;
    private static final String USAGE_MESSAGE = "Usage: java Tournament [round count] [render target: " +
            "console/none] [player1: human/clever/whatever/snartypamts] [player2: " +
            "human/clever/whatever/snartypamts]";
    private static final String RESULTS_MESSAGE = "=== player 1: %d | player 2: %d | Draws: %d ===\r";

    private final int rounds;
    private final Renderer renderer;
    private final Player player1;
    private final Player player2;

    /**
     * Constructor.
     * @param rounds the number of game rounds between the players until the tournament stops.
     * @param renderer the object that renders the game board.
     * @param players the array of two players.
     */
    public Tournament(int rounds, Renderer renderer, Player[] players) {
        this.rounds = rounds;
        this.renderer = renderer;
        this.player1 = players[0];
        this.player2 = players[1];
    }

    /**
     * Plays one tournament between the two players and prints the results in the end. Each round the players
     * switch marks.
     */
    public void playTournament() {
        Player[][] players = {{player1, player2}, {player2, player1}};
        Mark[][] marks = {{Mark.X, Mark.O}, {Mark.O, Mark.X}};
        int[] results = new int[RESULTS_NUM];
        for (int i = 0 ; i < rounds ; i++) {
            Game game = new Game(players[i % 2][0], players[i % 2][1], renderer);
            updateResults(game.run(), marks[i % 2], results);
        }
        printResults(results);
    }

    /**
     * The main function of the program.
     */
    public static void main(String[] args) {
        if (args.length != ARG_NUM) {  // Check if the arguments were provided.
            System.err.println(USAGE_MESSAGE);
            return;
        }
        int rounds = Integer.parseInt(args[ARG_NUM_ROUNDS]);
        Tournament tournament = createTournament(rounds, args[ARG_RENDER_TYPE], args[ARG_PLAYER1_TYPE],
                args[ARG_PLAYER2_TYPE]);  // Try to create a Tournament object.
        if (tournament == null) {
            return;
        }
        tournament.playTournament();  // Start playing the tournament.
    }

    private static boolean checkArgumentsValidity(int rounds, Renderer renderer, Player player1,
                                                  Player player2) {
        if (rounds <= 0 || renderer == null || player1 == null || player2 == null) {
            System.err.println(USAGE_MESSAGE);
            return false;
        }
        return true;
    }

    private static Tournament createTournament(int rounds, String renderType, String player1Type,
                                         String player2Type) {
        RendererFactory rendererFactor = new RendererFactory();
        PlayerFactory playerFactory = new PlayerFactory();
        Renderer renderer = rendererFactor.buildRenderer(renderType);
        Player player1 = playerFactory.buildPlayer(player1Type);
        Player player2 = playerFactory.buildPlayer(player2Type);
        if (!checkArgumentsValidity(rounds, renderer, player1, player2)) {
            return null;
        }
        return new Tournament(rounds, renderer, new Player[] {player1, player2});
    }

    private static void updateResults(Mark result, Mark[] playerMarks, int[] results) {
        if (result == playerMarks[0]) {
            results[PLAYER1_RESULT]++;
        } else if (result == playerMarks[1]) {
            results[PLAYER2_RESULT]++;
        } else {
            results[DRAW_RESULT]++;
        }
    }

    private static void printResults(int[] results) {
        String stringToPrint = String.format(RESULTS_MESSAGE, results[PLAYER1_RESULT],
                results[PLAYER2_RESULT], results[DRAW_RESULT]);
        System.out.print(stringToPrint);
    }
}
