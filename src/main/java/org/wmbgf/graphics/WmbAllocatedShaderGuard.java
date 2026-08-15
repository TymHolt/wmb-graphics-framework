package org.wmbgf.graphics;

import java.util.Objects;

/**
 * This class contains an IWmbAllocatedShader instance and propagates all function calls to the contained instance. The
 * instance is set to null when disposed, that way all following calls will produce a NullPointerException as that
 * resource is not available anymore.
 */
public final class WmbAllocatedShaderGuard implements IWmbAllocatedShader {

    private IWmbAllocatedShader shader;

    /**
     * Contain the given instance to guard against use-after-free.
     *
     * @param shader The shader to guard. May not be null.
     */
    public WmbAllocatedShaderGuard(IWmbAllocatedShader shader) {
        Objects.requireNonNull(shader, "shader");
        this.shader = shader;
    }

    @Override
    public int getId() {
        return this.shader.getId();
    }

    @Override
    public void dispose() {
        this.shader.dispose();
        this.shader = null;
    }
}
