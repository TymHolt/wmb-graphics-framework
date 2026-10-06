package org.wmbgf.utils;

import java.util.Arrays;

public final class IntArrayBuilder {

    private final int initialCapacity;
    private int[] array;
    private int size = 0;

    public IntArrayBuilder(int initialCapacity) {
        if (initialCapacity < 1)
            throw new IllegalArgumentException("initialCapacity must be >= 1, is " + initialCapacity);
        this.initialCapacity = initialCapacity;
        this.array = new int[initialCapacity];
    }

    public IntArrayBuilder() {
        this(1024);
    }

    public void append(int value) {
        if (size >= this.array.length)
            this.array = Arrays.copyOf(this.array, this.array.length * 2);

        this.array[this.size++] = value;
    }

    public void clear() {
        this.array = new int[this.initialCapacity];
        this.size = 0;
    }

    public int[] toArray() {
        return Arrays.copyOf(this.array, this.size);
    }

    public int getSize() {
        return this.size;
    }

    public BuilderIterator createIterator() {
        return new BuilderIterator();
    }

    public class BuilderIterator {

        private int index = 0;

        private BuilderIterator() {

        }

        public boolean next() {
            return ++index < array.length;
        }

        public int getCurrent() {
            return array[index];
        }
    }
}
