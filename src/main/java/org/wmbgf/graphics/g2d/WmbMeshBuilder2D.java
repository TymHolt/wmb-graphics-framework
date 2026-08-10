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
        Objects.requireNonNull(primitiveType);
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
     * Verify if the added data can correctly be used.
     *
     * @return If the data can be used correctly.
     */
    public boolean verify() {
        return !this.indexValues.isEmpty() && this.indexValues.size() % this.primitiveType.vertexCount == 0;
    }

    /**
     * Clears the added data.
     */
    public void clear() {
        this.vertexValues.clear();
        this.indexValues.clear();
    }

    /**
     * Allocates the added data on the GPU and returns a mesh instance that can be used for rendering.
     *
     * @return The ready-to-render mesh.
     */
    public WmbAllocatedMesh2D allocate() {
        final int vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);

        final int vboId = createVbo(0, this.vertexValues);
        final int eboId = createEbo(this.indexValues);
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);

        GL30.glBindVertexArray(vaoId);
        return new WmbAllocatedMesh2D(vaoId, vboId, this.indexValues.size());
    }

    private static int createVbo(int attributeIndex, List<Float> valueList) {
        final float[] valueArray = new float[valueList.size()];
        int index = 0;
        for (float value : valueList)
            valueArray[index++] = value;

        final int vboId = GL30.glGenBuffers();
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vboId);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, valueArray, GL30.GL_STATIC_DRAW);
        GL30.glVertexAttribPointer(attributeIndex, 2, GL30.GL_FLOAT, false, 0, 0);
        GL30.glEnableVertexAttribArray(attributeIndex);
        return vboId;
    }

    private static int createEbo(List<Integer> valueList) {
        final int[] valueArray = new int[valueList.size()];
        int index = 0;
        for (int value : valueList)
            valueArray[index++] = value;

        final int eboId = GL30.glGenBuffers();
        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, eboId);
        GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, valueArray, GL30.GL_STATIC_DRAW);
        return eboId;
    }
}
