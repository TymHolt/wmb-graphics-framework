package org.wmbgf.graphics;

import java.util.Objects;

/**
 * This class contains an {@link IWmbAllocatedShader} instance and propagates all function calls to the contained
 * instance. The instance is set to {@code null} when disposed, that way all following calls will produce a
 * {@link NullPointerException} as that resource is not available anymore. This class also protects failed uniform name
 * resolves by throwing an exception.
 */
public final class WmbAllocatedShaderGuard implements IWmbAllocatedShader {

    private IWmbAllocatedShader shader;

    /**
     * Initialize the guard for the given instance.
     *
     * @param shader The {@link IWmbAllocatedShader} instance to guard, must not be {@code null}.
     */
    public WmbAllocatedShaderGuard(IWmbAllocatedShader shader) {
        Objects.requireNonNull(shader);
        this.shader = shader;
    }

    @Override
    public int getId() {
        return this.shader.getId();
    }

    @Override
    public int getUniformLocation(String name) {
        Objects.requireNonNull(name);

        final int location = this.shader.getUniformLocation(name);
        if (location < 0)
            throw new ResolveException(name);

        return location;
    }

    @Override
    public void dispose() {
        this.shader.dispose();
        this.shader = null;
    }

    public static final class ResolveException extends RuntimeException {

        private ResolveException(String name) {
            super("Could not find uniform location for name '" + name + "'");
        }
    }
}
