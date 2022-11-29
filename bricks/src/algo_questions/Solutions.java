package algo_questions;

import java.util.Arrays;

/**
 * The solutions to the given algorithmic problems.
 */
public class Solutions {

    /**
     * Computes the maximal amount of tasks out of n tasks that can be completed with m time slots. A task
     * can only be completed in a time slot if the length of the time slot is greater or equal to the no.
     * of hours needed to complete the task.
     * @param tasks array of integers of length n. tasks[i] is the time in hours required to complete task i.
     * @param timeSlots array of integers of length m. timeSlots[i] is the length in hours of the slot i.
     * @return the maximal amount of tasks that can be completed.
     */
    public static int alotStudyTime(int[] tasks, int[] timeSlots) {
        int[] sortedTasks = new int[tasks.length];
        int[] sortedTimeSlots = new int[timeSlots.length];
        System.arraycopy(tasks, 0, sortedTasks, 0, tasks.length);
        System.arraycopy(timeSlots, 0, sortedTimeSlots, 0, timeSlots.length);
        Arrays.sort(sortedTasks);
        Arrays.sort(sortedTimeSlots);
        int numTasks = 0;
        int j = 0;
        for (int i = 0 ; i < sortedTimeSlots.length && j < sortedTasks.length ; i++) {
            if (sortedTasks[j] <= sortedTimeSlots[i]) {
                numTasks++;
                j++;
            }
        }
        return numTasks;
    }

    /**
     * Computes the minimal amount of leaps a frog needs to jump across n waterlily leaves, from leaf 1 to
     * leaf n. The leaves vary in size and how stable they are, so some leaves allow larger leaps than
     * others. leapNum[i] is an integer telling you how many leaves ahead you can jump from leaf i.
     * @param leapNum array of ints. leapNum[i] is how many leaves ahead you can jump from leaf i.
     * @return the minimal number of leaps to the last leaf.
     */
    public static int minLeap(int[] leapNum) {
        if (leapNum.length <= 1) {
            return 0;
        }
        int maxReach = leapNum[0];
        int steps = leapNum[0];
        int numJumps = 1;
        for (int i = 1 ; i < leapNum.length - 1 ; i++) {
            if (i + leapNum[i] > maxReach) {
                maxReach = i + leapNum[i];
            }
            steps--;
            if (steps == 0) {
                numJumps++;
                steps = maxReach - i;
            }
        }
        return numJumps;
    }

    /**
     * Computes the solution to the following problem: A boy is filling the water trough for his
     * father's cows in their village. The trough holds n liters of water. With every trip to the village
     * well, he can return using either the 2 bucket yoke, or simply with a single bucket. A bucket holds 1
     * liter. In how many ways can he fill the water trough?
     * @param n the number of litres of water in the water trough.
     * @return the number of ways to fill the water trough.
     */
    public static int bucketWalk(int n) {
        if (n == 0) {
            return 1;
        }
        int[] result = new int[n + 1];
        result[0] = 1;
        result[1] = 1;
        for (int i = 2 ; i <= n ; i++) {
            result[i] = result[i - 1] + result[i - 2];
        }
        return result[n];
    }

    /**
     * Computes the solution to the following problem: Given an integer n, return the number of structurally
     * unique BSTs (binary search trees) that have exactly n nodes of unique values from 1 to n.
     * @param n the number of nodes in the trees.
     * @return the number of structurally unique BSTs that have exactly n nodes of unique values from 1 to n.
     */
    public static int numTrees(int n) {
        int[] result = new int[n + 1];
        result[0] = 1;
        result[1] = 1;
        for (int i = 2 ; i <= n ; i++) {
            for (int j = 1 ; j <= i ; j++) {
                result[i] = result[i] + (result[j - 1] * result[i - j]);
            }
        }
        return result[n];
    }
}
