package edu.hal.cache;

/**
 * @author Ivan Mamontov
 */
public class LinearMemoryWalker extends MemoryWalker {

    @Override
    public int nextAddress(int pageOffset, int pos) {
        return (pos + 1) & MemoryWalker.ARRAY_MASK;
    }
}
