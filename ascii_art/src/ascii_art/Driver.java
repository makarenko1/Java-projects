package ascii_art;

import image.Image;
import java.util.logging.Logger;

/**
 * The Driver class.
 */
public class Driver {

    private static final int NUM_ARGS = 1;  // the number of command line arguments.

    // All the messages to the user:
    private static final String USAGE_ERROR = "USAGE: java asciiArt ";
    private static final String FILE_ERROR = "Failed to open image file ";

    /**
     * The main method. If the user didn't supply the image filename in the command line arguments, then the
     * usage error message is printed. If there is any problem with the given file, then the file error
     * message is printed. Otherwise, the run function of the Shell object is called.
     * @param args the program arguments.
     */
    public static void main(String[] args) {
        if (args.length != NUM_ARGS) {
            System.out.println(USAGE_ERROR);
            return;
        }
        Image img = Image.fromFile(args[0]);
        if (img == null) {
            Logger.getGlobal().severe(FILE_ERROR + args[0]);
            return;
        }
        new Shell(img).run();
    }
}
