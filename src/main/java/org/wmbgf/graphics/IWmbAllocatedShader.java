package org.wmbgf.graphics;

public interface IWmbAllocatedShader {
    int getId();
    int getUniformLocation(String name);
    void dispose();
}
