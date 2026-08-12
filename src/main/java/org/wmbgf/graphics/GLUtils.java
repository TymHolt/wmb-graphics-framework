package org.wmbgf.graphics;

public final class GLUtils {

    /**
     * Ensures a given OpenGL ID is valid (>= 0). If not, an IllegalStateException is thrown.
     *
     * @param id The ID to validate.
     */
    public static void requireValidId(int id) {
        if (id < 0)
            throw new IllegalStateException("Id " + id + " is invalid");
    }
}
