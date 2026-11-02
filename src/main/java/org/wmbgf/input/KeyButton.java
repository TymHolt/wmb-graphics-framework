package org.wmbgf.input;

import org.lwjgl.glfw.GLFW;
/**
 * Representation of buttons on the keyboard with GLFW handling. Oriented around a {@code QWERTZ} layout.
 */
public enum KeyButton {

    UNKNOWN(-1),
    A(GLFW.GLFW_KEY_A),
    B(GLFW.GLFW_KEY_B),
    C(GLFW.GLFW_KEY_C),
    D(GLFW.GLFW_KEY_D),
    E(GLFW.GLFW_KEY_E),
    F(GLFW.GLFW_KEY_F),
    G(GLFW.GLFW_KEY_G),
    H(GLFW.GLFW_KEY_H),
    I(GLFW.GLFW_KEY_I),
    J(GLFW.GLFW_KEY_J),
    K(GLFW.GLFW_KEY_K),
    L(GLFW.GLFW_KEY_L),
    M(GLFW.GLFW_KEY_M),
    N(GLFW.GLFW_KEY_N),
    O(GLFW.GLFW_KEY_O),
    P(GLFW.GLFW_KEY_P),
    Q(GLFW.GLFW_KEY_Q),
    R(GLFW.GLFW_KEY_R),
    S(GLFW.GLFW_KEY_S),
    T(GLFW.GLFW_KEY_T),
    U(GLFW.GLFW_KEY_U),
    V(GLFW.GLFW_KEY_V),
    W(GLFW.GLFW_KEY_W),
    X(GLFW.GLFW_KEY_X),
    Y(GLFW.GLFW_KEY_Z), // Switch Y and Z
    Z(GLFW.GLFW_KEY_Y), // We use QWERTZ here
    KEY_0(GLFW.GLFW_KEY_0),
    KEY_1(GLFW.GLFW_KEY_1),
    KEY_2(GLFW.GLFW_KEY_2),
    KEY_3(GLFW.GLFW_KEY_3),
    KEY_4(GLFW.GLFW_KEY_4),
    KEY_5(GLFW.GLFW_KEY_5),
    KEY_6(GLFW.GLFW_KEY_6),
    KEY_7(GLFW.GLFW_KEY_7),
    KEY_8(GLFW.GLFW_KEY_8),
    KEY_9(GLFW.GLFW_KEY_9),
    UP(GLFW.GLFW_KEY_UP),
    DOWN(GLFW.GLFW_KEY_DOWN),
    LEFT(GLFW.GLFW_KEY_LEFT),
    RIGHT(GLFW.GLFW_KEY_RIGHT),
    BACKSPACE(GLFW.GLFW_KEY_BACKSPACE),
    DELETE(GLFW.GLFW_KEY_DELETE),
    ENTER(GLFW.GLFW_KEY_ENTER),
    TAB(GLFW.GLFW_KEY_TAB);

    /**
     * Find the corresponding {@link KeyButton} by the given GLFW ID. If no instance for that ID exists,
     * {@link #UNKNOWN} is returned.
     *
     * @param glfwId The id to get the handle for.
     * @return       The handle, never {@code null}.
     */
    public static KeyButton getByGlfwId(int glfwId) {
        for (KeyButton keyButton : KeyButton.values())
            if (keyButton.glfwId == glfwId)
                return keyButton;

        return KeyButton.UNKNOWN;
    }

    private final int glfwId;

    KeyButton(int glfwId) {
        this.glfwId = glfwId;
    }

    /**
     * @return The corresponding GLFW ID.
     */
    public int getGlfwId() {
        return this.glfwId;
    }
}