package org.wmbgf.graphics;

public enum WmbPrimitiveType {
    TRIANGLE(3);

    public final int vertexCount;

    WmbPrimitiveType(int vertexCount) {
        this.vertexCount = vertexCount;
    }
}
