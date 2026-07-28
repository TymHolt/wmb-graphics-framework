package org.wmbgf;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import java.awt.*;

public final class WmbUIContext {

    private static long windowId;

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

            while (!GLFW.glfwWindowShouldClose(WmbUIContext.windowId)) {
                applicationHandler.onUpdate();
                GLFW.glfwSwapBuffers(WmbUIContext.windowId);
                GLFW.glfwPollEvents();
            }

            applicationHandler.onDestroy();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        // Destroy
        Callbacks.glfwFreeCallbacks(windowId);
        GLFW.glfwDestroyWindow(WmbUIContext.windowId);
        GLFW.glfwTerminate();
        GLFW.glfwSetErrorCallback(null).free();
    }
}
