package org.wmbgf.input;

import org.lwjgl.glfw.GLFW;

/**
 * Representation button actions with GLFW handling.
 */
public enum ClickAction {

    UNKNOWN(-1),
    PRESS(GLFW.GLFW_PRESS),
    RELEASE(GLFW.GLFW_RELEASE),
    REPEAT(GLFW.GLFW_REPEAT);

    /**
     * Find the corresponding {@link ClickAction} by the given GLFW ID. If no instance for that ID exists,
     * {@link #UNKNOWN} is returned.
     *
     * @param glfwId The id to get the handle for.
     * @return       The handle, never {@code null}.
     */
    public static ClickAction getByGlfwId(int glfwId) {
        for (ClickAction clickAction : ClickAction.values())
            if (clickAction.glfwId == glfwId)
                return clickAction;

        return ClickAction.UNKNOWN;
    }

    private final int glfwId;

    ClickAction(int glfwId) {
        this.glfwId = glfwId;
    }

    /**
     * @return If this instance is either {@link #PRESS} or {@link #REPEAT}.
     */
    public boolean isPressOrRepeat() {
        return this == ClickAction.PRESS || this == ClickAction.REPEAT;
    }

    /**
     * @return The corresponding GLFW ID.
     */
    public int getGlfwId() {
        return this.glfwId;
    }
}