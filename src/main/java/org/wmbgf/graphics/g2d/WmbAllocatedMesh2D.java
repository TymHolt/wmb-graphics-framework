package org.wmbgf.graphics.g2d;

import org.lwjgl.opengl.GL30;
import org.wmbgf.graphics.IWmbAllocatedMesh;

public final class WmbAllocatedMesh2D implements IWmbAllocatedMesh {

    private final int vaoId;
    private final int[] bufferIds;
    private final int vertexCount;

    WmbAllocatedMesh2D(int vaoId, int[] bufferIds, int vertexCount) {
        this.vaoId = vaoId;
        this.bufferIds = bufferIds;
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
    public void dispose() {
        GL30.glDeleteVertexArrays(this.vaoId);
        GL30.glDeleteBuffers(this.bufferIds);
    }
}
