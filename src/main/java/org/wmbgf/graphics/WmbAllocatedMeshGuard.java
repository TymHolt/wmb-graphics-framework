package org.wmbgf.graphics;

import java.util.Objects;

/**
 * This class contains an {@link IWmbAllocatedMesh} instance and propagates all function calls to the containe
 * instance. The instance is set to {@code null} when disposed, that way all following calls will produce a
 * {@link NullPointerException} as that resource is not available anymore.
 */
public final class WmbAllocatedMeshGuard implements IWmbAllocatedMesh {

    private IWmbAllocatedMesh mesh;

    /**
     * Initialize the guard for the given instance.
     *
     * @param mesh The {@link IWmbAllocatedMesh} instance to guard, must not be {@code null}.
     */
    public WmbAllocatedMeshGuard(IWmbAllocatedMesh mesh) {
        Objects.requireNonNull(mesh);
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
