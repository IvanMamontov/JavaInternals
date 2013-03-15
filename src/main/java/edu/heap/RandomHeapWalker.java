package edu.heap;

/**
 * @author Ivan Mamontov
 */
public class RandomHeapWalker extends MemoryWalker {

    @Override
    public int nextAddress(int pageOffset, int pos) {
        return (pos + PRIME_INC) & ARRAY_MASK;
    }
}
