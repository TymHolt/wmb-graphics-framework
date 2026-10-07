package org.wmbgf.graphics.g2d;

import org.lwjgl.opengl.GL30;
import org.wmbgf.graphics.GLUtils;
import org.wmbgf.graphics.IWmbAllocatedMesh;
import org.wmbgf.graphics.WmbAllocatedMeshGuard;
import org.wmbgf.graphics.WmbPrimitiveType;
import org.wmbgf.utils.FloatArrayBuilder;
import org.wmbgf.utils.IntArrayBuilder;

import java.util.Objects;

/**
 * A utility for building 2D mesh data and allocating to the GPU.
 */
public final class WmbMeshBuilder2D {

    private final WmbPrimitiveType primitiveType;
    private final FloatArrayBuilder vertexValues = new FloatArrayBuilder();
    private final IntArrayBuilder indexValues = new IntArrayBuilder();

    /**
     * Create a builder for 2D meshes.
     *
     * @param primitiveType The type of primitive the mesh is made of.
     */
    public WmbMeshBuilder2D(WmbPrimitiveType primitiveType) {
        Objects.requireNonNull(primitiveType, "primitiveType");
        this.primitiveType = primitiveType;
    }

    /**
     * Add a vertex to the mesh.
     *
     * @param x The vertices x coordinate.
     * @param y The vertices y coordinate.
     */
    public void addVertex(float x, float y) {
        this.vertexValues.append(x);
        this.vertexValues.append(y);
        this.indexValues.append(this.indexValues.getSize());
    }

    /**
     * Verify if the added data can be used correctly.
     *
     * @return If the data can be used correctly.
     */
    public boolean verify() {
        try {
            verifyExcept();
            return true;
        } catch (IllegalStateException exception) {
            return false;
        }
    }

    /**
     * Verify if the added data can be used correctly and throws an exception if that is not the case.
     *
     * @throws IllegalStateException If the data cannot be verified.
     */
    public void verifyExcept() {
        final int indexCount = this.indexValues.getSize();

        if(indexCount == 0)
            throw new IllegalStateException("No indices added");

        if(indexCount % this.primitiveType.vertexCount != 0)
            throw new IllegalStateException(
                String.format("Index count (%d) does not match primitive type vertex count (%d)",
                    indexCount, this.primitiveType.vertexCount));

        final int valuesPerVertex = 2;
        final int vertexValueCount = this.vertexValues.getSize();

        if (vertexValueCount % valuesPerVertex != 0)
            throw new IllegalStateException(
                String.format("Vertex values (%d) do not match vertex length (%d)",
                    vertexValueCount, valuesPerVertex));

        final int vertexCount = vertexValueCount / valuesPerVertex;
        final IntArrayBuilder.BuilderIterator indexIterator = this.indexValues.createIterator();
        do {
            final int index = indexIterator.getCurrent();
            if (index < 0 || index >= vertexCount)
                throw new IllegalStateException(
                    String.format("Index (%d) out of range, vertex count is %d",
                        index, vertexCount));
        } while (indexIterator.next());
    }

    /**
     * Clears the added data.
     */
    public void clear() {
        this.vertexValues.clear();
        this.indexValues.clear();
    }

    /**
     * Allocates the added data on the GPU and returns a mesh instance that can be used for rendering. The data is
     * verified using verifyExcept(), so an IllegalStateException may be thrown when the data is wrong.
     *
     * @return The ready-to-render mesh.
     * @throws IllegalStateException If the data cannot be verified.
     */
    public IWmbAllocatedMesh allocate() {
        return allocate(true);
    }

    /**
     * Allocates the added data on the GPU and returns a mesh instance that can be used for rendering.
     *
     * @param verifyData If the data should be verified before allocation using verifyEcept().
     * @return The ready-to-render mesh.
     * @throws IllegalStateException If the data fails verification.
     */
    public IWmbAllocatedMesh allocate(boolean verifyData) {
        if (verifyData)
            verifyExcept();

        final int vaoId = GL30.glGenVertexArrays();
        try {
            GL30.glBindVertexArray(vaoId);

            final int vboId = GLUtils.createVbo(0, 2, this.vertexValues.toArray());
            try {
                final int eboId = GLUtils.createEbo(this.indexValues.toArray());
                try {
                    return new WmbAllocatedMeshGuard(new WmbAllocatedMesh2D(vaoId, new int[] {vboId, eboId},
                        this.indexValues.getSize()));
                } catch (Exception exception) {
                    GL30.glDeleteBuffers(eboId);
                    throw exception;
                }
            } catch(Exception exception) {
                GL30.glDeleteBuffers(vboId);
                throw exception;
            }
        } catch (Exception exception) {
            GL30.glDeleteVertexArrays(vaoId);
            throw exception;
        } finally {
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
            GL30.glBindVertexArray(0);
        }
    }

    private static final class WmbAllocatedMesh2D implements IWmbAllocatedMesh {

        private final int vaoId;
        private final int[] bufferIds;
        private final int vertexCount;

        private WmbAllocatedMesh2D(int vaoId, int[] bufferIds, int vertexCount) {
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
}
