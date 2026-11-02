package org.wmbgf.graphics;

import java.util.Objects;

/**
 * This class contains an {@link IWmbAllocatedTexture} instance and propagates all function calls to the contained
 * instance. The instance is set to {@code null} when disposed, that way all following calls will produce a
 * {@link NullPointerException} as that resource is not available anymore.
 */
public final class WmbAllocatedTextureGuard implements IWmbAllocatedTexture {

    private IWmbAllocatedTexture texture;

    /**
     * Initialize the guard for the given instance.
     *
     * @param texture The {@link IWmbAllocatedTexture} instance to guard, must not be {@code null}.
     */
    public WmbAllocatedTextureGuard(IWmbAllocatedTexture texture) {
        Objects.requireNonNull(texture);
        this.texture = texture;
    }

    @Override
    public int getId() {
        return this.texture.getId();
    }

    @Override
    public int getWidth() {
        return this.texture.getWidth();
    }

    @Override
    public int getHeight() {
        return this.texture.getHeight();
    }

    @Override
    public void dispose() {
        this.texture.dispose();
        this.texture = null;
    }
}