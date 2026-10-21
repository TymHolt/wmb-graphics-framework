package org.wmbgf.utils;

import java.util.Arrays;

/**
 * A utility to dynamically build an array of integers.
 */
public final class IntArrayBuilder {

    private final int initialCapacity;
    private int[] array;
    private int size = 0;

    /**
     * Initialize the builder with the given initial allocated capacity.
     *
     * @param initialCapacity The amount of values that can be added without reallocation.
     */
    public IntArrayBuilder(int initialCapacity) {
        if (initialCapacity < 1)
            throw new IllegalArgumentException("initialCapacity must be >= 1, is " + initialCapacity);
        this.initialCapacity = initialCapacity;
        this.array = new int[initialCapacity];
    }

    /**
     * Initialize the builder with an initial allocated capacity of {@code 1024}.
     */
    public IntArrayBuilder() {
        this(1024);
    }

    /**
     * Appends the given value to the array.
     *
     * @param value The value to add.
     */
    public void append(int value) {
        if (size >= this.array.length)
            this.array = Arrays.copyOf(this.array, this.array.length * 2);

        this.array[this.size++] = value;
    }

    /**
     * Clears all added data.
     */
    public void clear() {
        this.array = new int[this.initialCapacity];
        this.size = 0;
    }

    /**
     * Allocates and returns the final array with the exact capacity of the added date.
     *
     * @return The array containing the added data.
     */
    public int[] toArray() {
        return Arrays.copyOf(this.array, this.size);
    }

    /**
     * Returns the size of the array that is being built. This builder may already have more data allocated.
     *
     * @return The size of the added data.
     */
    public int getSize() {
        return this.size;
    }

    /**
     * Returns the value at the given index.
     *
     * @param index The index to access. Must be in bounds.
     * @return      The value at the given index.
     */
    public int getValue(int index) {
        if (index < 0 || index >= this.size)
            throw new IndexOutOfBoundsException(String.format("Index %d out of bounds (%d)", index, this.size));
        return this.array[index];
    }

    /**
     * @return A new iterator for this array data.
     */
    public BuilderIterator createIterator() {
        return new BuilderIterator();
    }

    /**
     * An iterator for the data that is being built.
     */
    public class BuilderIterator {

        private int index = 0;

        private BuilderIterator() {

        }

        /**
         * Go forward in the array data until the end.
         *
         * @return {@code true} if the iterator points at valid data.
         */
        public boolean next() {
            return ++index < size;
        }

        /**
         * @return The current value of the iterator.
         */
        public int getCurrent() {
            return array[index];
        }
    }
}
