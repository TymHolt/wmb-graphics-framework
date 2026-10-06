package org.wmbgf.graphics;

import org.lwjgl.opengl.GL30;

import java.util.Objects;

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

    /**
     * Creates an OpenGL {@code Vertex Buffer Object} with the given data and enables it as the given attribute index.
     * This method requires a bound {@code Vertex Array Objects}. The data is still bound after this method call.
     *
     * @param attributeIndex  The index of the attribute pointer to enable the VBO on, must be >= 0.
     * @param valuesPerVertex The amount of values per vertex, must be 1, 2, 3 or 4.
     * @param values          The array of values to allocate in the VBO. Needs to match {@code valuesPerVertex}. Must
     *                        not be {@code null}.
     * @return                The OpenGL ID of the VBO.
     */
    public static int createVbo(int attributeIndex, int valuesPerVertex, float[] values) {
        if (attributeIndex < 0)
            throw new IllegalArgumentException("Attribute index must be >= 0, is " + attributeIndex);

        if (valuesPerVertex < 1 || valuesPerVertex > 4)
            throw new IllegalArgumentException("Values per vertex must be 1, 2, 3 or 4, is " + valuesPerVertex);

        Objects.requireNonNull(values);
        if (values.length % valuesPerVertex != 0)
            throw new IllegalArgumentException(String.format("Values amount (%d) does not match values per vertex (%d)",
                    values.length, valuesPerVertex));

        final int vboId = GL30.glGenBuffers();
        try {
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vboId);
            GL30.glBufferData(GL30.GL_ARRAY_BUFFER, values, GL30.GL_STATIC_DRAW);
            GL30.glVertexAttribPointer(attributeIndex, valuesPerVertex, GL30.GL_FLOAT, false, 0, 0);
            GL30.glEnableVertexAttribArray(attributeIndex);
            return vboId;
        } catch(Exception exception) {
            GL30.glDeleteBuffers(vboId);
            throw exception;
        }
    }

    /**
     * Creates and binds a new OpenGL {@code Element Buffer Object}. This method requires a bound
     * {@code Vertex Array Objects}.
     *
     * @param values The index values to allocate, must not be {@code null}.
     * @return       The OpenGL ID of the EBO.
     */
    public static int createEbo(int[] values) {
        Objects.requireNonNull(values);

        if (values.length == 0)
            throw new IllegalArgumentException("No index values given, length is 0");

        final int eboId = GL30.glGenBuffers();
        try {
            GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, eboId);
            GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, values, GL30.GL_STATIC_DRAW);
            return eboId;
        } catch(Exception exception) {
            GL30.glDeleteBuffers(eboId);
            throw exception;
        }
    }
}