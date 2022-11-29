package ascii_art.img_to_char;

import image.Image;
import java.awt.*;
import java.util.HashMap;

/**
 * Matches ASCII characters to images according to brightness.
 */
public class BrightnessImgCharMatcher {

    private static final int CHAR_RESOLUTION = 16;
    private static final int MAX_RGB_VALUE = 255;
    private static final double RED_COEFFICIENT = 0.2126;
    private static final double GREEN_COEFFICIENT = 0.7152;
    private static final double BLUE_COEFFICIENT = 0.0722;

    private final Image image;  // the image to make ASCII art for.
    private final String font;  // the font for the characters in the ASCII art.
    private final HashMap<Image, Double> cache;  // stores the calculated brightnesses of the image objects.

    /**
     * Constructor.
     * @param image the image to match the characters to.
     * @param font the font name of the characters.
     */
    public BrightnessImgCharMatcher(Image image, String font) {
        this.image = image;
        this.font = font;
        this.cache = new HashMap<>();
    }

    /**
     * Chooses characters for the given image according to brightness.
     * @param numCharsInRow the number of characters in each row of the obtained character array that
     *                      represents the given image. The bigger it is, the higher the resolution is.
     * @param charSet the list of the possible characters in the obtained character array that represents
     *                the given image.
     * @return the converted to ASCII image (character array that represents the given image)
     */
    public char[][] chooseChars(int numCharsInRow, Character[] charSet) {
        Character[] newCharSet = new Character[charSet.length];  // copy the input so that not to change it.
        System.arraycopy(charSet, 0, newCharSet, 0, charSet.length);
        double[] charBrightness = getCharBrightness(newCharSet);
        sortCharsByBrightness(newCharSet, charBrightness);
        linearStretching(charBrightness);
        return convertImageToAscii(charBrightness, numCharsInRow, newCharSet);
    }

    /*
     * Calculates the brightness of one character. The brightness of a character is the number of white
     * pixels divided by the total number of pixels in the character image.
     */
    private double getOneCharBrightness(Character character) {
        boolean[][] charImage = CharRenderer.getImg(character, CHAR_RESOLUTION, font);
        int numPixels = 0;
        int numWhitePixels = 0;
        for (boolean[] row : charImage) {
            for (boolean pixel : row) {
                if (pixel) {
                    numWhitePixels++;
                }
                numPixels++;
            }
        }
        return ((double) numWhitePixels) / numPixels;
    }

    /*
     * Returns an array of all the brightnesses of all the characters in the given array. The brightness of
     * a character at the index i in the given array is at the index i in the returned array.
     */
    private double[] getCharBrightness(Character[] charSet) {
        double[] charBrightness = new double[charSet.length];
        for (int i = 0 ; i < charSet.length ; i++) {
            charBrightness[i] = getOneCharBrightness(charSet[i]);
        }
        return charBrightness;
    }

    /*
     * Swaps the values in the given array of brightnesses at the indexes i and i + 1.
     */
    private static void swapBrightness(double[] charBrightness, int i) {
        double tempBrightness = charBrightness[i];
        charBrightness[i] = charBrightness[i + 1];
        charBrightness[i + 1] = tempBrightness;
    }

    /*
     * Swaps the values in the given array of characters at the indexes i and i + 1.
     */
    private static void swapChars(Character[] charSet, int i) {
        Character tempChar = charSet[i];
        charSet[i] = charSet[i + 1];
        charSet[i + 1] = tempChar;
    }

    /*
     * Sorts the array of characters (charSet) by their brightnesses (charBrightness) with the bubble sort
     * algorithm. When we perform a swap in the array of characters, we also perform a corresponding swap
     * in the array of brightnesses, so that a character and its brightness would be at the same index.
     */
    private static void sortCharsByBrightness(Character[] charSet, double[] charBrightness) {
        for (int i = 0 ; i < charSet.length - 1 ; i++) {
            for (int j = 0 ; j < charSet.length - i - 1 ; j++) {
                if (charBrightness[j] > charBrightness[j + 1]) {
                    swapBrightness(charBrightness, j);
                    swapChars(charSet, j);
                }
            }
        }
    }

    /*
     * Performs linear stretching on the array of brightnesses by the given formula. If linear stretching
     * can't be performed (the minimal brightness in the array equals the maximal), we make all the
     * brightnesses equal to zero.
     */
    private static void linearStretching(double[] charBrightness) {
        double minBrightness = charBrightness[0];
        double maxBrightness = charBrightness[charBrightness.length - 1];
        for (int i = 0; i < charBrightness.length ; i++) {
            if (minBrightness == maxBrightness) {
                charBrightness[i] = 0;
            }
            charBrightness[i] = (charBrightness[i] - minBrightness) / (maxBrightness - minBrightness);
        }
    }

    /*
     * Calculates the grey value of the colored pixel by the given formula.
     */
    private double getGreyPixel(Color colorPixel) {
        return (colorPixel.getRed() * RED_COEFFICIENT + colorPixel.getGreen() * GREEN_COEFFICIENT +
                colorPixel.getBlue() * BLUE_COEFFICIENT);
    }

    /*
     * Gets the brightness of the given image. If the cache contains the given image, returns its brightness.
     * Otherwise, calculates its brightness by summing up the brightnesses of all the pixels in it and
     * dividing by the total number of pixels in the image, and then puts the image with its brightness
     * into the cache.
     */
    private double getImageBrightness(Image image) {
        if (cache.containsKey(image)) {
            return cache.get(image);
        }
        int numPixels = 0;
        double sumBrightness = 0;
        for (Color colorPixel : image.pixels()) {
            sumBrightness += (getGreyPixel(colorPixel) / MAX_RGB_VALUE);
            numPixels++;
        }
        double imageBrightness = sumBrightness / numPixels;
        cache.put(image, imageBrightness);
        return imageBrightness;
    }

    /*
     * Chooses from the given array of characters the closest character by its brightness to the given
     * sub-image brightness.
     */
    private static char getCharForSubImage(Character[] charSet, double[] charBrightness,
                                    double subImageBrightness) {
        char bestChar = charSet[0];
        double bestDifference = Math.abs(subImageBrightness - charBrightness[0]);
        for (int i = 0; i < charBrightness.length ; i++) {
            if (Math.abs(subImageBrightness - charBrightness[i]) < bestDifference) {
                bestChar = charSet[i];
                bestDifference = Math.abs(subImageBrightness - charBrightness[i]);
            }
        }
        return bestChar;
    }

    /*
     * Converts the image stored in the object to an array of characters by dividing the image into
     * sub-images and for each sub-image choosing the closest character by its brightness to the brightness
     * the sub-image.
     */
    private char[][] convertImageToAscii(double[] charBrightness, int numCharsInRow, Character[] charSet) {
        int pixels = image.getWidth() / numCharsInRow;
        char[][] asciiArt = new char[image.getHeight() / pixels][image.getWidth() / pixels];
        int row = 0;
        int col = 0;
        for (Image subImage : image.squareSubImagesOfSize(pixels)) {
            double subImageBrightness = getImageBrightness(subImage);  // get the sub-image brightness.
            // Choose the best character for the sub-image:
            asciiArt[row][col] = getCharForSubImage(charSet, charBrightness, subImageBrightness);
            if (++col == asciiArt[row].length) {  // increment the column index.
                row++;  // go to the start of the next row if the column index is out of borders.
                col = 0;
            }
        }
        return asciiArt;
    }
}
