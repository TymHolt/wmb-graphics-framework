package org.wmbgf.graphics.g2d;

import org.lwjgl.opengl.GL30;
import org.wmbgf.graphics.IWmbAllocatedMesh;

public final class WmbAllocatedMesh2D implements IWmbAllocatedMesh {

    private final int vaoId;
    private final int vboId;
    private final int vertexCount;

    WmbAllocatedMesh2D(int vaoId, int vboId, int vertexCount) {
        this.vaoId = vaoId;
        this.vboId = vboId;
        this.vertexCount = vertexCount;
    }

    @Override
    public int getId() {
        return this.vaoId;
    }

    @Override
    public int getVertexCount() {
        return this.vertexCount;
    }

    @Override
    public void delete() {
        GL30.glDeleteVertexArrays(this.vaoId);
        GL30.glDeleteBuffers(this.vboId);
    }
}
