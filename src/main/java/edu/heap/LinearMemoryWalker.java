package edu.heap;

/**
 * @author Ivan Mamontov
 */
public class LinearMemoryWalker extends MemoryWalker {

    @Override
    public int nextAddress(int pageOffset, int pos) {
        return (pos + 1) & ARRAY_MASK;
    }
}
