package edu;

/**
 * @author Ivan Mamontov
 */
public class SafePoint {
    public static void main(String[] args) throws Exception {
        Thread worker = new Thread() {
            @Override public void run() {
                double d = 0;

                for (int j = 1; j < 2000000000; j++)
                    d += Math.log(Math.E * j);

                System.out.println(d);
            }

        };

        Thread reporter = new Thread() {
            @Override public void run() {
                try {
                    while (true) {
                        Thread.sleep(1000);

                        System.out.println("Running: " + System.currentTimeMillis());
                    }
                }
                catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        };

        reporter.start();
        worker.start();

        worker.join();
        reporter.interrupt();
    }
}
