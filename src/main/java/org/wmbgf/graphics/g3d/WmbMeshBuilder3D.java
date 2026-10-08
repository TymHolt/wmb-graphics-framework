package org.wmbgf.graphics.g3d;

import org.lwjgl.opengl.GL30;
import org.wmbgf.graphics.GLUtils;
import org.wmbgf.graphics.IWmbAllocatedMesh;
import org.wmbgf.graphics.WmbAllocatedMeshGuard;
import org.wmbgf.graphics.WmbPrimitiveType;
import org.wmbgf.utils.FloatArrayBuilder;
import org.wmbgf.utils.IntArrayBuilder;

import java.util.Objects;

/**
 * A utility for building 3D mesh data and allocating to the GPU.
 */
public final class WmbMeshBuilder3D {

    private final WmbPrimitiveType primitiveType;
    private final FloatArrayBuilder vertexValues = new FloatArrayBuilder();
    private final FloatArrayBuilder textureValues = new FloatArrayBuilder();
    private final FloatArrayBuilder normalValues = new FloatArrayBuilder();
    private final IntArrayBuilder indexValues = new IntArrayBuilder();

    /**
     * Create a builder for 3D meshes.
     *
     * @param primitiveType The type of primitive the mesh is made of, must not be {@code null}.
     */
    public WmbMeshBuilder3D(WmbPrimitiveType primitiveType) {
        Objects.requireNonNull(primitiveType, "primitiveType");
        this.primitiveType = primitiveType;
    }

    /**
     * Add a vertex to the mesh.
     *
     * @param x The vertices x coordinate.
     * @param y The vertices y coordinate.
     * @param z The vertices z coordinate.
     * @param u The vertices texture u coordinate.
     * @param v The vertices texture v coordinate.
     * @param nx The vertices normal x coordinate.
     * @param ny The vertices normal y coordinate.
     * @param nz The vertices normal z coordinate.
     */
    public void addVertex(float x, float y, float z, float u, float v, float nx, float ny, float nz) {
        this.vertexValues.append(x);
        this.vertexValues.append(y);
        this.vertexValues.append(z);

        this.textureValues.append(u);
        this.textureValues.append(v);

        this.normalValues.append(nx);
        this.normalValues.append(ny);
        this.normalValues.append(nz);

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

        // Are there any indices?
        if(indexCount == 0)
            throw new IllegalStateException("No indices added");

        // Does the index data match required primitive size?
        if(indexCount % this.primitiveType.vertexCount != 0)
            throw new IllegalStateException(
                    String.format("Index count (%d) does not match primitive type vertex count (%d)",
                            indexCount, this.primitiveType.vertexCount));

        // Do position values match required length?
        final int vertexValueCount = this.vertexValues.getSize();
        if (vertexValueCount % 3 != 0)
            throw new IllegalStateException(
                    String.format("Vertex values (%d) do not match vertex length (%d)",
                            vertexValueCount, 3));

        // Do texture values match required length?
        final int textureValueCount = this.textureValues.getSize();
        if (textureValueCount % 2 != 0)
            throw new IllegalStateException(
                    String.format("Texture values (%d) do not match vertex length (%d)",
                            textureValueCount, 2));

        // Do normal values match required length?
        final int normalValueCount = this.normalValues.getSize();
        if (normalValueCount % 3 != 0)
            throw new IllegalStateException(
                    String.format("Normal values (%d) do not match vertex length (%d)",
                            normalValueCount, 3));

        // Do all data arrays have the same amount of vertices?
        final int vertexCount = vertexValueCount / 3;
        if (vertexCount != textureValueCount / 2 || vertexCount != normalValueCount / 3)
            throw new IllegalStateException("Vertex position data, texture data and normal data length do not match");

        // Do all indices point at existing vertices?
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
     * verified using {@link #verifyExcept()}, so an IllegalStateException may be thrown when the data is wrong.
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
     * @param verifyData If the data should be verified before allocation using {@link #verifyExcept()}.
     * @return The ready-to-render mesh.
     * @throws IllegalStateException If the data fails verification.
     */
    public IWmbAllocatedMesh allocate(boolean verifyData) {
        if (verifyData)
            verifyExcept();

        final int vaoId = GL30.glGenVertexArrays();
        try {
            GL30.glBindVertexArray(vaoId);

            final int posVboId = GLUtils.createVbo(0, 3, this.vertexValues.toArray());
            try {
                final int texVboId = GLUtils.createVbo(1, 2, this.textureValues.toArray());
                try {
                    final int normVboId = GLUtils.createVbo(2, 3, this.normalValues.toArray());
                    try {
                        final int eboId = GLUtils.createEbo(this.indexValues.toArray());
                        try {
                            return new WmbAllocatedMeshGuard(new WmbAllocatedMesh3D(vaoId, new int[] {posVboId,
                                    texVboId, normVboId, eboId}, this.indexValues.getSize()));
                        } catch (Exception exception) {
                            GL30.glDeleteBuffers(eboId);
                            throw exception;
                        }
                    } catch (Exception exception) {
                        GL30.glDeleteBuffers(normVboId);
                        throw exception;
                    }
                } catch (Exception exception) {
                    GL30.glDeleteBuffers(texVboId);
                    throw exception;
                }
            } catch(Exception exception) {
                GL30.glDeleteBuffers(posVboId);
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

    private static final class WmbAllocatedMesh3D implements IWmbAllocatedMesh {

        private final int vaoId;
        private final int[] bufferIds;
        private final int vertexCount;

        private WmbAllocatedMesh3D(int vaoId, int[] bufferIds, int vertexCount) {
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
