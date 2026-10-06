package org.wmbgf.graphics;

/**
 * A handle for a texture allocated on the GPU.
 */
public interface IWmbAllocatedTexture {
    int getId();
    int getWidth();
    int getHeight();
    void dispose();
}
