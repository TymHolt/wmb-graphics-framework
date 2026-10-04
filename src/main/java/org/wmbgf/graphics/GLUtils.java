package org.wmbgf.graphics;

import org.lwjgl.opengl.GL30;

/**
 * A collection of utilities and tools for OpenGL interactions.
 */
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

    private static int drawCallCount = 0;

    /**
     * Issue a draw call with the current OpenGL state. Uses {@code GL_TRIANGLES} as mode and {@code GL_UNSIGNED_INT} as
     * type. The given vertex {@code count} is given directly to OpenGL. This also registers a drawcall in the counter.
     *
     * @param count The amount of vertices to render.
     */
    public static void issueElementsDrawCall(int count) {
        GL30.glDrawElements(GL30.GL_TRIANGLES, count, GL30.GL_UNSIGNED_INT, 0);
        GLUtils.drawCallCount++;
    }

    /**
     * Fetches the amount of counted drawcalls since the last reset.
     *
     * @return The amount of drawcalls.
     */
    public static int getDrawCallCount() {
        return GLUtils.drawCallCount;
    }

    /**
     * Resets the drawcall count.
     */
    public static void resetDrawCallCount() {
        GLUtils.drawCallCount = 0;
    }
}
