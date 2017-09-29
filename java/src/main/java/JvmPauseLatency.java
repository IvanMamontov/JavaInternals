import java.util.Arrays;
import java.util.Date;
import java.util.Random;

public class JvmPauseLatency {

    private static final int WARMUP = 30;
    private static final int EXTRA = 5;
    private static final long PAUSE = 5 * 1000000000L; // in nanos

    private final Random rand = new Random();
    private int count;
    private double calculation;
    private final long[] results = new long[WARMUP + EXTRA];
    private long interval = PAUSE; // in nanos

    private long busyPause(long pauseInNanos) {
        long until = System.nanoTime() + pauseInNanos;
        while(System.nanoTime() < until) {
        }
        return until;
    }

    public void run() {

        long testDuration = ((WARMUP * 1) + (EXTRA * PAUSE)) / 1000000000L;
        System.out.println(new Date() +" => Please wait " + testDuration + " seconds for the results...");

        while(count < results.length) {

            double x = busyPause(interval);

            long latency = System.nanoTime();

            calculation += x / (rand.nextInt(5) + 1);
            calculation -= calculation / (rand.nextInt(5) + 1);
            calculation -= x / (rand.nextInt(6) + 1);
            calculation += calculation / (rand.nextInt(6) + 1);

            latency = System.nanoTime() - latency;

            results[count++] = latency;
            if (count >= WARMUP) {
                interval = PAUSE;
            }
            else {
                interval =  PAUSE;
//                interval =  rand.nextBoolean() ? PAUSE : 0;
            }
        }

        // now print the last (EXTRA * 2) results so you can compare before and after the pause change (from 1 to PAUSE)
        System.out.println(new Date() + " => Calculation: " + calculation);
        System.out.println("Results:");
        long[] array = Arrays.copyOfRange(results, results.length - EXTRA * 2, results.length);
        for(long t: array) System.out.println(t);
    }

    public static void main(String[] args) {
        new JvmPauseLatency().run();
    }
}