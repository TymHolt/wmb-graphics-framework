package org.wmbgf;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.wmbgf.graphics.g2d.ISize2D;

import java.awt.*;

/**
 * The main handling class of the WmbUI Graphics Framework.
 */
public final class WmbUIContext {

    private static long windowId;
    private static class FramebufferSize implements ISize2D {

        int width;
        int height;

        @Override
        public int getWidth() {
            return this.width;
        }

        @Override
        public int getHeight() {
            return this.height;
        }
    };
    private final static FramebufferSize framebufferSize = new FramebufferSize();

    /**
     * Initializes all APIs and runs the given application. This method is blocking and will not return until the
     * application exits. The window will have a generic title and size.
     *
     * @param applicationHandler The handler to run, must not be {@code null}.
     */
    public static void runFramework(IApplicationHandler applicationHandler) {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!GLFW.glfwInit())
            throw new IllegalStateException("GLFW could not be initialized");

        // Create window
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 4);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        WmbUIContext.windowId = GLFW.glfwCreateWindow(screenSize.width / 2, screenSize.height / 2, "WmbGF", 0, 0);
        GLFW.glfwSetWindowPos(WmbUIContext.windowId, screenSize.width / 4, screenSize.height / 4);

        // Create context
        GLFW.glfwMakeContextCurrent(WmbUIContext.windowId);
        GLFW.glfwSwapInterval(1); // V-Sync
        GLFW.glfwShowWindow(WmbUIContext.windowId);
        GL.createCapabilities();

        // Run application exception safe
        try {
            applicationHandler.onInit();

            double lastUpdateTime = GLFW.glfwGetTime();
            while (!GLFW.glfwWindowShouldClose(WmbUIContext.windowId)) {
                // Update delta time
                final double currentUpdateTime = GLFW.glfwGetTime();
                final float deltaTime = (float) (currentUpdateTime - lastUpdateTime);
                lastUpdateTime = currentUpdateTime;

                // Poll UI events
                GLFW.glfwPollEvents();

                // Update window size
                final int[] widthPointer = new int[1];
                final int[] heightPointer = new int[1];
                GLFW.glfwGetFramebufferSize(windowId, widthPointer, heightPointer);
                WmbUIContext.framebufferSize.width = widthPointer[0];
                WmbUIContext.framebufferSize.height = heightPointer[0];

                // Update application and render
                applicationHandler.onUpdate(deltaTime);
                GLFW.glfwSwapBuffers(WmbUIContext.windowId);
            }

            applicationHandler.onDestroy();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        // Destroy
        Callbacks.glfwFreeCallbacks(WmbUIContext.windowId);
        GLFW.glfwDestroyWindow(WmbUIContext.windowId);
        GLFW.glfwTerminate();
        GLFW.glfwSetErrorCallback(null).free();
    }

    /**
     * Returns the current framebuffer size of the main window. With no application running this method produces
     * undefined results, but never {@code null}.
     *
     * @return The main window framebuffer size.
     */
    public static ISize2D getFramebufferSize() {
        return WmbUIContext.framebufferSize;
    }

    /**
     * Update the title of the main window. With no application running, this method produces undefined behavior und may
     * produce an exception in underlying API calls.
     *
     * @param title The new title, may be null.
     */
    public static void setTitle(String title) {
        if (title == null)
            title = "null";
        GLFW.glfwSetWindowTitle(WmbUIContext.windowId, title);
    }
}
