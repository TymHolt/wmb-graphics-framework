package org.wmbgf.graphics.g2d;

import org.lwjgl.opengl.GL30;
import org.wmbgf.graphics.WmbPrimitiveType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class WmbMeshBuilder2D {

    private final WmbPrimitiveType primitiveType;
    private final List<Float> vertexValues = new ArrayList<>();
    private final List<Integer> indexValues = new ArrayList<>();

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
        this.vertexValues.add(x);
        this.vertexValues.add(y);
        this.indexValues.add(this.indexValues.size());
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
        final int indexCount = this.indexValues.size();

        if(indexCount == 0)
            throw new IllegalStateException("No indices added");

        if(indexCount % this.primitiveType.vertexCount != 0)
            throw new IllegalStateException(
                String.format("Index count (%d) does not match primitive type vertex count (%d)",
                    indexCount, this.primitiveType.vertexCount));

        final int valuesPerVertex = 2;
        final int vertexValueCount = this.vertexValues.size();

        if (vertexValueCount % valuesPerVertex != 0)
            throw new IllegalStateException(
                String.format("Vertex values (%d) do not match vertex length (%d)",
                    vertexValueCount, valuesPerVertex));

        final int vertexCount = vertexValueCount / valuesPerVertex;
        for (int index : this.indexValues)
            if (index < 0 || index >= vertexCount)
                throw new IllegalStateException(
                    String.format("Index (%d) out of range, vertex count is %d",
                        index, vertexCount));
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
    public WmbAllocatedMesh2D allocate() {
        verifyExcept();

        final int vaoId = GL30.glGenVertexArrays();
        try {
            GL30.glBindVertexArray(vaoId);

            final int vboId = createVbo(0, this.vertexValues);
            try {
                final int eboId = createEbo(this.indexValues);
                try {
                    return new WmbAllocatedMesh2D(vaoId, new int[] {vboId, eboId}, this.indexValues.size());
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

    private static int createVbo(int attributeIndex, List<Float> valueList) {
        final float[] valueArray = new float[valueList.size()];
        int index = 0;
        for (float value : valueList)
            valueArray[index++] = value;

        final int vboId = GL30.glGenBuffers();
        try {
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vboId);
            GL30.glBufferData(GL30.GL_ARRAY_BUFFER, valueArray, GL30.GL_STATIC_DRAW);
            GL30.glVertexAttribPointer(attributeIndex, 2, GL30.GL_FLOAT, false, 0, 0);
            GL30.glEnableVertexAttribArray(attributeIndex);
            return vboId;
        } catch(Exception exception) {
            GL30.glDeleteBuffers(vboId);
            throw exception;
        }
    }

    private static int createEbo(List<Integer> valueList) {
        final int[] valueArray = new int[valueList.size()];
        int index = 0;
        for (int value : valueList)
            valueArray[index++] = value;

        final int eboId = GL30.glGenBuffers();
        try {
            GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, eboId);
            GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, valueArray, GL30.GL_STATIC_DRAW);
        } catch(Exception exception) {
            GL30.glDeleteBuffers(eboId);
            throw exception;
        }
        return eboId;
    }
}
