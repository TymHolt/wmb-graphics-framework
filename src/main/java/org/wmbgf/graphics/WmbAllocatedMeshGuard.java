package org.wmbgf.graphics;

import java.util.Objects;

/**
 * This class contains an IWmbAllocatedMesh instance and propagates all function calls to the contained instance. The
 * instance is set to null when disposed, that way all following calls will produce a NullPointerException as that
 * resource is not available anymore.
 */
public final class WmbAllocatedMeshGuard implements IWmbAllocatedMesh {

    private IWmbAllocatedMesh mesh;

    /**
     * Contain the given instance to guard against use-after-free.
     *
     * @param mesh The mesh to guard. May not be null.
     */
    public WmbAllocatedMeshGuard(IWmbAllocatedMesh mesh) {
        Objects.requireNonNull(mesh, "mesh");
        this.mesh = mesh;
    }

    @Override
    public int getId() {
        return this.mesh.getId();
    }

    @Override
    public int getVertexCount() {
        return this.mesh.getVertexCount();
    }

    @Override
    public void dispose() {
        this.mesh.dispose();
        this.mesh = null;
    }
}
