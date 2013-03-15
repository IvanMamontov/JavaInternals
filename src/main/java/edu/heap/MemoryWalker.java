package edu.heap;

/**
 * @author Ivan Mamontov
 */
public abstract class MemoryWalker implements Walker {

    public static final int LONG_SIZE = 8;
    public static final int PAGE_SIZE = 2 * 1024 * 1024;
    public static final int ONE_GIG = 1024 * 1024 * 1024;
    public static final long TWO_GIG = 2L * ONE_GIG;

    public static final int ARRAY_SIZE = (int) (TWO_GIG / LONG_SIZE);
    public static final int WORDS_PER_PAGE = PAGE_SIZE / LONG_SIZE;

    public static final int ARRAY_MASK = ARRAY_SIZE - 1;
    public static final int PAGE_MASK = WORDS_PER_PAGE - 1;

    public static final int PRIME_INC = 514229;

    private long[] memory;

    MemoryWalker() {
        memory = new long[MemoryWalker.ARRAY_SIZE];
        for (int i = 0; i < MemoryWalker.ARRAY_SIZE; i++) {
            memory[i] = 777;
        }
    }

    public long doWalk() {
        int pos = -1;
        long result = 0;
        for (int pageOffset = 0; pageOffset < ARRAY_SIZE; pageOffset += WORDS_PER_PAGE) {
            for (int wordOffset = pageOffset, limit = pageOffset + WORDS_PER_PAGE;
                 wordOffset < limit;
                 wordOffset++) {
                pos = nextAddress(pageOffset, pos);
                result += memory[pos];
            }
        }
        if (208574349312L != result) {
            throw new IllegalStateException();
        }
        return result;
    }
}
