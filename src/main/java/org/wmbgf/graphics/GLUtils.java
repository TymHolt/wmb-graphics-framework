package org.wmbgf.graphics;

import org.lwjgl.opengl.GL30;

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

    /**
     * Clear the current framebuffer to black and clear the depth component.
     */
    public static void clearFramebuffer() {
        clearFramebuffer(0.0f, 0.0f, 0.0f);
    }

    /**
     * Clear the current framebuffer to the given color and clear the depth component.
     *
     * @param red The red component of the clear color.
     * @param green The green component of the clear color.
     * @param blue The blue component of the clear color.
     */
    public static void clearFramebuffer(float red, float green, float blue) {
        clearFramebuffer(red, green, blue, true);
    }

    /**
     * Clear the current framebuffer to the given color, and optional the depth component.
     *
     * @param red The red component of the clear color.
     * @param green The green component of the clear color.
     * @param blue The blue component of the clear color.
     * @param depth If the depth component should be cleared.
     */
    public static void clearFramebuffer(float red, float green, float blue, boolean depth) {
        GL30.glClearColor(red, green, blue, 1.0f);
        GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | (depth ? GL30.GL_DEPTH_BUFFER_BIT : 0));
    }
}
