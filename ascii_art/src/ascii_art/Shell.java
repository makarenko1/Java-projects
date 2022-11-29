package ascii_art;

import ascii_art.img_to_char.BrightnessImgCharMatcher;
import ascii_output.AsciiOutput;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;
import java.util.*;

/**
 * Represents an interface for creating ASCII art.
 */
public class Shell {

    // All the commands that the run method supports:
    private static final String CMD_EXIT = "exit";
    private static final String CMD_SHOW_CHARS = "chars";
    private static final String CMD_ADD = "add";
    private static final String CMD_REMOVE = "remove";
    private static final String CMD_CHANGE_RESOLUTION = "res";
    private static final String CMD_CONSOLE = "console";
    private static final String CMD_RENDER = "render";

    // All the commands' possible parameters:
    private static final String PARAM_ALL = "all";
    private static final String PARAM_SPACE = "space";
    private static final String PARAM_UP = "up";
    private static final String PARAM_DOWN = "down";

    // The start and the end of the full character range:
    private static final char START_CHAR_RANGE = ' ';  // the first legal ASCII character.
    private static final char END_CHAR_RANGE = '~';  // the last legal ASCII character.

    // Resolution parameters:
    private static final int INITIAL_CHARS_IN_ROW = 64;
    private static final int MIN_PIXELS_PER_CHAR = 2;
    private static final int CHANGE_RESOLUTION_COEFFICIENT = 2;

    // All the output parameters:
    private static final String FONT_NAME = "Courier New";
    private static final String OUTPUT_FILENAME = "out.html";
    private static final String INITIAL_CHARS_RANGE = "0-9";  // the initially available characters.

    // All the messages to the user:
    private static final String ASK_FOR_INPUT_MESSAGE = ">>> ";
    private static final String WRONG_INPUT_ERROR = "Wrong input given!";
    private static final String CHANGE_RESOLUTION_MESSAGE = "Width set to ";
    private static final String MIN_RESOLUTION_ERROR = "You're using the minimal resolution.";
    private static final String MAX_RESOLUTION_ERROR = "You're using the maximal resolution.";

    // All the user input parameters:
    private static final String USER_INPUT_REGEX = "\\s+";
    private static final int MAX_USER_INPUT_LENGTH = 2;
    private static final int PARAM_INDEX = 1;
    private static final int ONE_CHAR_RANGE_LENGTH = 1;
    private static final int MULTIPLE_CHAR_RANGE_LENGTH = 3;
    private static final int RANGE_SEPARATOR_INDEX = 1;
    private static final char RANGE_SEPARATOR = '-';


    private final Set<Character> charSet;  // all the available characters for ASCII art.
    private final int minCharsInRow;  // the minimal number of characters in row.
    private final int maxCharsInRow;  // the maximal number of characters in row.
    private int charsInRow;  // the current number of characters in row.
    private final BrightnessImgCharMatcher charMatcher;  // chooses the characters for ASCII art.
    private boolean ifConsoleOutput;  // true iff the output is to the console. Is false by default.
    private AsciiOutput output;  // outputs ASCII art (either to html file or to the console).

    /**
     * Constructor.
     * @param img the image to construct ASCII art for.
     */
    public Shell(Image img) {
        this.charSet = new HashSet<>();
        this.minCharsInRow = Math.max(1, img.getWidth()/img.getHeight());
        this.maxCharsInRow = img.getWidth() / MIN_PIXELS_PER_CHAR;
        this.charsInRow = Math.max(Math.min(INITIAL_CHARS_IN_ROW, maxCharsInRow), minCharsInRow);
        this.charMatcher = new BrightnessImgCharMatcher(img, FONT_NAME);
        this.ifConsoleOutput = false;
        this.output = new HtmlAsciiOutput(OUTPUT_FILENAME, FONT_NAME);
        addChars(INITIAL_CHARS_RANGE);
    }

    /**
     * Translates the commands obtained from the user to the program functions. Continues to ask for the user
     * input until the user enters the 'exit' command.
     */
    public void run() {
        Scanner scanner = new Scanner(System.in);
        String cmd;
        String[] words;
        do {
            System.out.print(ASK_FOR_INPUT_MESSAGE);
            cmd = scanner.nextLine().trim();  // get the user input.
            words = cmd.split(USER_INPUT_REGEX);
            if (checkWrongInputLength(words)) {  // if the input length is incorrect, ask for input again.
                continue;
            }
            callFunctionByInput(words[0], getParam(words));  // perform function by command.
        } while (!words[0].equals(CMD_EXIT) || words.length != 1);  // continue until the user inputs 'exit'.
    }

    /*
     * Returns the command parameter if exists, an empty string otherwise.
     */
    private static String getParam(String[] words) {
        String param = "";
        if (words.length > 1) {
            param = words[PARAM_INDEX];
        }
        return param;
    }

    /*
     * Checks if for the commands that don't have a parameter ('exit', 'chars', 'console', 'render') the user
     * entered a parameter. Returns true iff for these commands user entered a parameter.
     */
    private static boolean checkParameterlessWrongParameter(String command, String param) {
        return ((command.equals(CMD_EXIT) || command.equals(CMD_SHOW_CHARS) || command.equals(CMD_CONSOLE) ||
                command.equals(CMD_RENDER)) && param.length() != 0);
    }

    /*
     * Checks if the user input is of incorrect length (it has an incorrect number of words). Returns true
     * iff the input length is bigger than the constant value MAX_USER_INPUT_LENGTH or the parameterless
     * commands have parameters.
     */
    private static boolean checkWrongInputLength(String[] words) {
        if (words.length > MAX_USER_INPUT_LENGTH || checkParameterlessWrongParameter(words[0],
                getParam(words))) {
            System.out.println(WRONG_INPUT_ERROR);
            return true;
        }
        return false;
    }

    /*
     * Calls a function for the requested command with a parameter given (possibly empty).
     */
    private void callFunctionByInput(String command, String param) {
        switch (command) {
            case CMD_SHOW_CHARS:
                showChars();
                break;
            case CMD_ADD:
                addChars(param);
                break;
            case CMD_REMOVE:
                removeChars(param);
                break;
            case CMD_CHANGE_RESOLUTION:
                resChange(param);
                break;
            case CMD_CONSOLE:
                changeOutputToConsole();
                break;
            case CMD_RENDER:
                render();
                break;
            default:
                // Do not call a function if the command is empty, or it is an 'exit' command:
                if (command.equals("") || command.equals(CMD_EXIT)) {
                    return;
                }
                // In any other case, the command is wrong, and an error message needs to be printed:
                System.out.println(WRONG_INPUT_ERROR);
        }
    }

    /*
     * Method for the 'chars' command. Prints all the characters that are available for ASCII art (that are
     * in the charSet).
     */
    private void showChars() {
        charSet.stream().sorted().forEach(c-> System.out.print(c + " "));
        System.out.println();
    }

    /*
     * Checks if the range borders are correct. Returns true iff the range borders are within the full
     * range borders. Notes: e.g. there are characters that are in the extended ASCII table that are not
     * supposed to be added to the charSet.
     */
    private static boolean checkRangeBorders(char start, char end) {
        return (start >= START_CHAR_RANGE && end <= END_CHAR_RANGE);
    }

    /*
     * Checks if the user entered a correct range for the add command. Returns true iff the range is of the
     * length ONE_CHAR_RANGE_LENGTH or MULTIPLE_CHAR_RANGE_LENGTH length, its borders are within the
     * borders of the full character range, and the separator (if needed) is correct.
     */
    private static boolean checkRangeCorrectness(String param) {
        switch (param.length()) {
            case ONE_CHAR_RANGE_LENGTH:  // if the range consists of one character.
                return checkRangeBorders(param.charAt(0), param.charAt(ONE_CHAR_RANGE_LENGTH - 1));
            case MULTIPLE_CHAR_RANGE_LENGTH:
                // The characters of the start and the end of the range, and the separator character:
                char start = (char) Math.min(param.charAt(0), param.charAt(MULTIPLE_CHAR_RANGE_LENGTH - 1));
                char end = (char) Math.max(param.charAt(0), param.charAt(MULTIPLE_CHAR_RANGE_LENGTH - 1));
                char separator = param.charAt(RANGE_SEPARATOR_INDEX);
                return (checkRangeBorders(start, end) && separator == RANGE_SEPARATOR);
            default:
                return false;
        }
    }

    /*
     * Parses the range for the given parameter that is not 'all' or 'space'. If the range is incorrect,
     * returns null. Returns an array that contains the edge characters of the range otherwise.
     */
    private static char[] parseCustomCharRange(String param) {
        if (!checkRangeCorrectness(param)) {
            System.out.println(WRONG_INPUT_ERROR);
            return null;
        }
        if (param.length() == ONE_CHAR_RANGE_LENGTH) {
            return new char[] {param.charAt(0), param.charAt(ONE_CHAR_RANGE_LENGTH - 1)};
        }
        return new char[] {param.charAt(0), param.charAt(MULTIPLE_CHAR_RANGE_LENGTH - 1)};
    }

    /*
     * Parses the range that user entered according to the given parameter. If the range is incorrect,
     * returns null. Returns an array that contains the edge characters of the range otherwise.
     */
    private static char[] parseCharRange(String param) {
        switch (param) {
            case PARAM_ALL:  // the range is [' ', '~'].
                return new char[] {START_CHAR_RANGE, END_CHAR_RANGE};
            case PARAM_SPACE:  // the range is [' ', ' '].
                return new char[] {START_CHAR_RANGE, START_CHAR_RANGE};
            default:
                return parseCustomCharRange(param);
        }
    }

    /*
     * Method for the 'add' command. Adds a single character that is given in the string s or all the
     * characters in the multiple-character-range that is given in the string s to the available characters
     * for ASCII art.
     */
    private void addChars(String s) {
        char[] range = parseCharRange(s);
        if(range != null) {
            for (char c = (char) Math.min(range[0], range[1]) ; c <= Math.max(range[0], range[1]) ; c++) {
                charSet.add(c);
            }
        }
    }

    /*
     * Method for the 'remove' command. Removes a single character that is given in the string s or all the
     * characters in the multiple-character-range that is given in the string s from the available characters
     * for ASCII art.
     */
    private void removeChars(String s) {
        char[] range = parseCharRange(s);
        if(range != null) {
            for (char c = (char) Math.min(range[0], range[1]) ; c <= Math.max(range[0], range[1]) ; c++) {
                charSet.remove(c);
            }
        }
    }

    /*
     * If the changed resolution is bigger than the maximal resolution, make it maximal and print an error
     * message. If the changed resolution is smaller than the minimal resolution, make it minimal and print
     * an error message. If the changed resolution is in borders, output the current resolution.
     */
    private void makeResolutionInBorders() {
        if (charsInRow < minCharsInRow) {
            System.out.println(MIN_RESOLUTION_ERROR);
            charsInRow = minCharsInRow;
        } else if (charsInRow > maxCharsInRow) {
            System.out.println(MAX_RESOLUTION_ERROR);
            charsInRow = maxCharsInRow;
        } else {
            System.out.println(CHANGE_RESOLUTION_MESSAGE + charsInRow);
        }
    }

    /*
     * Method for the 'res' command. Changes the ASCII art resolution.
     */
    private void resChange(String s) {
        switch (s) {
            case PARAM_UP:  // increase the resolution.
                charsInRow *= CHANGE_RESOLUTION_COEFFICIENT;
                break;
            case PARAM_DOWN:  // decrease the resolution.
                charsInRow /= CHANGE_RESOLUTION_COEFFICIENT;
                break;
            default:
                System.out.println(WRONG_INPUT_ERROR);
                return;
        }
        makeResolutionInBorders();
    }

    /*
     * Method for the 'console' command. Outputs ASCII art to console from now on.
     */
    private void changeOutputToConsole() {
        if (ifConsoleOutput) {
            return;
        }
        this.ifConsoleOutput = true;
        this.output = new ConsoleAsciiOutput();
    }

    /*
     * Method for the 'render' command. Outputs ASCII art either to html file or to console (if the user
     * entered the 'console' command previously).
     */
    private void render() {
        if (charSet.size() == 0) {  // if there are no available characters for ASCII art, do nothing.
            return;
        }
        char[][] chosenChars = charMatcher.chooseChars(charsInRow, charSet.toArray(new Character[0]));
        output.output(chosenChars);
    }
}
