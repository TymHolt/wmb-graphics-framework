package org.wmbgf.graphics;

/**
 * A handle for a shader program allocated on the GPU.
 */
public interface IWmbAllocatedShader {
    int getId();
    int getUniformLocation(String name);
    void dispose();
}
