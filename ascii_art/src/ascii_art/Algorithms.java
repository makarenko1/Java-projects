package ascii_art;

import java.util.HashSet;
import java.util.Set;

/**
 * Contains solutions to algorithmic problems.
 */
public class Algorithms {

    private static final int A_CODE = 97;  // the code of 'a' in the ASCII table.
    private static final String[] MORSE_ALPHABET = new String[] {".-", "-...", "-.-.", "-..", ".", "..-.",
            "--.", "....", "..", ".---", "-.-", ".-..", "--", "-.", "---", ".--.", "--.-", ".-.", "...", "-",
            "..-", "...-", ".--", "-..-", "-.--", "--.."};

    /**
     * Finds the repeated number in the given array.
     * @param numList the array of length n + 1 such that every number in it is in the range [1, n], and
     *                such that there is exactly one repeated number in the array.
     * @return the repeated number.
     */
    public static int findDuplicate(int[] numList) {
        int tortoise = numList[0];
        int hare = numList[0];
        // The first phase of the algorithm. Finds the intersection point of hare and tortoise.
        do {
            tortoise = numList[tortoise];
            hare = numList[numList[hare]];
        } while (tortoise != hare);
        // The second phase of the algorithm. Finds the repeated number (the entrance to the cycle).
        tortoise = numList[0];
        while (tortoise != hare) {
            tortoise = numList[tortoise];
            hare = numList[hare];
        }
        return hare;
    }

    /**
     * Finds the number of unique morse codes in the given array of words (that is, if two words are
     * translated to morse in the same way, their code will be calculated once).
     * @param words the array of words (all words are legal and in lowercase).
     * @return the number of unique morse codes.
     */
    public static int uniqueMorseRepresentations(String[] words) {
        Set<String> translatedWords = new HashSet<>();
        for (String word : words) {
            translatedWords.add(translateToMorse(word));
        }
        return translatedWords.size();
    }

    /*
     * Translates the given word to morse code. Translates each letter of the word to morse code and
     * returns a string that contains the translated letters in the same order they were in the given word.
     */
    private static String translateToMorse(String word) {
        StringBuilder translatedWord = new StringBuilder();
        for (char c : word.toCharArray()) {
            // The index of the Morse code of c in the MORSE_ALPHABET is the number of letters between c
            // and 'a' in the ASCII table (is equal to the number of letters between them in the alphabet).
            translatedWord.append(MORSE_ALPHABET[c - A_CODE]);
        }
        return translatedWord.toString();
    }
}
