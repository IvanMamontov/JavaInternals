package edu.heap;

/**
 * @author Ivan Mamontov
 */
public class RandomHeapWalker extends MemoryWalker {

    @Override
    public int nextAddress(int pageOffset, int pos) {
        return (pos + PRIME_INC) & ARRAY_MASK;
    }

    public static void main(String[] args) {
        RandomHeapWalker randomHeapWalker = new RandomHeapWalker();
        for (int i = 0; i < 1; i++) {
            randomHeapWalker.doWalk();
        }
    }
}
