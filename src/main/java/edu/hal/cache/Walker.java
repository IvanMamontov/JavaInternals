package edu.hal.cache;

/**
 * @author Ivan Mamontov
 */
public interface Walker {

    int nextAddress(int pageOffset, int pos);
}
