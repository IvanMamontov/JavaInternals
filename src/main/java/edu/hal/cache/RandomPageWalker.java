package edu.hal.cache;

/**
 * @author Ivan Mamontov
 */
public class RandomPageWalker extends MemoryWalker {

    @Override
    public int nextAddress(int pageOffset, int pos) {
        return pageOffset + ((pos + PRIME_INC) & PAGE_MASK);
    }
}
