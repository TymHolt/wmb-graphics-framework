package org.wmbgf.graphics.g3d;

import org.wmbgf.graphics.IWmbAllocatedMesh;
import org.wmbgf.graphics.WmbPrimitiveType;
import org.wmbgf.utils.FloatArrayBuilder;
import org.wmbgf.utils.StreamLoader;
import org.wmbol.obj.IWmbObjLoadConsumer;
import org.wmbol.obj.WmbObjLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility to load and allocate {@code .obj} source as a {@link IWmbAllocatedMesh} for 3D purposes.
 */
public final class WmbObjMeshLoader {

    private final List<String> warnings = new ArrayList<>();

    /**
     * Fetch warnings that occurred during the {@code .obj} parsing.
     *
     * @return The list containing all warnings.
     */
    public List<String> getWarnings() {
        return this.warnings;
    }

    /**
     * Loads and allocates the given {@code .obj} source. Parsing or allocation errors may produce exceptions.
     *
     * @param stream       The stream to load as {@code .obj}.
     * @return             The allocated mesh.
     * @throws IOException When an IO-Error occurs while loading the stream.
     */
    public IWmbAllocatedMesh load(InputStream stream) throws IOException {
        return load(StreamLoader.loadStreamToString(stream));
    }

    /**
     * Loads and allocates the given {@code .obj} source. Parsing or allocation errors may produce exceptions.
     *
     * @param objSource The source of the {@code .obj} model.
     * @return          The allocated mesh.
     */
    public IWmbAllocatedMesh load(String objSource) {
        final WmbMeshBuilder3D meshBuilder = new WmbMeshBuilder3D(WmbPrimitiveType.TRIANGLE);
        final FloatArrayBuilder geometryData = new FloatArrayBuilder();
        final FloatArrayBuilder textureData = new FloatArrayBuilder();
        final FloatArrayBuilder normalData = new FloatArrayBuilder();
        final WmbObjLoader objLoader = new WmbObjLoader(new IWmbObjLoadConsumer() {

            @Override
            public void consumeError(String message) {
                throw new RuntimeException(message);
            }

            @Override
            public void consumeWarning(String message) {
                warnings.add(message);
            }

            @Override
            public void consumeV(float x, float y, float z, float w) {
                geometryData.append(x);
                geometryData.append(y);
                geometryData.append(z);
            }

            @Override
            public void consumeVT(float u, float v, float w) {
                textureData.append(u);
                textureData.append(v);
            }

            @Override
            public void consumeVN(float x, float y, float z) {
                normalData.append(x);
                normalData.append(y);
                normalData.append(z);
            }

            @Override
            public void consumeF(int aV, int aVT, int aVN, int bV, int bVT, int bVN, int cV, int cVT, int cVN) {
                consumeVertex(aV, aVT, aVN);
                consumeVertex(bV, bVT, bVN);
                consumeVertex(cV, cVT, cVN);
            }

            private void consumeVertex(int vIndex, int vtIndex, int vnIndex) {
                float x = 0.0f, y = 0.0f, z = 0.0f, u = 0.0f, v = 0.0f, nx = 0.0f, ny = 0.0f, nz = 0.0f;

                vIndex *= 3;
                x = geometryData.getValue(vIndex);
                y = geometryData.getValue(vIndex + 1);
                z = geometryData.getValue(vIndex + 2);

                if (vtIndex >= 0) {
                    vtIndex *= 2;
                    u = textureData.getValue(vtIndex);
                    v = textureData.getValue(vtIndex + 1);
                }

                if (vnIndex >= 0) {
                    vnIndex *= 3;
                    nx = normalData.getValue(vnIndex);
                    ny = normalData.getValue(vnIndex + 1);
                    nz = normalData.getValue(vnIndex + 2);
                }

                meshBuilder.addVertex(x, y, z, u, v, nx, ny, nz);
            }
        });
        objLoader.load(objSource);
        return meshBuilder.allocate();
    }
}
