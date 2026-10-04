package org.wmbgf.graphics;

import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * A helper class for allocating textures to the GPU from a {@link BufferedImage}.
 */
public final class WmbTextureBuilder {

    /**
     * A wrapper for OpenGl texture filter, handling how the texture is interpolated when resized.
     */
    public enum Filter {
        /** Always take the nearest pixel color, wrapper for {@code GL_NEAREST}. */
        NEAREST(GL30.GL_NEAREST),

        /** Interpolate between pixels, wrapper for {@code GL_LINEAR}. */
        LINEAR(GL30.GL_LINEAR);

        final int id;

        Filter(int id) {
            this.id = id;
        }
    }

    private BufferedImage image;
    private Filter filter = Filter.NEAREST;

    /**
     * Initialize the builder with a default texture and {@link Filter#NEAREST} as default filter.
     */
    public WmbTextureBuilder() {
        this.image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB);
        this.image.setRGB(0, 0, Color.BLACK.getRGB());
        this.image.setRGB(1, 1, Color.BLACK.getRGB());
        this.image.setRGB(0, 1, Color.PINK.getRGB());
        this.image.setRGB(1, 0, Color.PINK.getRGB());
    }

    /**
     * Set the image to use in the texture.
     *
     * @param image The image instance, must not be {@code null}.
     */
    public void setImage(BufferedImage image) {
        Objects.requireNonNull(image);
        this.image = image;
    }

    /**
     * Set the filter to use for the texture.
     *
     * @param filter The filter wrapper, must not be {@code null}.
     */
    public void setFilter(Filter filter) {
        Objects.requireNonNull(filter);
        this.filter = filter;
    }

    /**
     * Allocates the image instance with the given options as a texture on the GPU.
     *
     * @return The wrapper of the allocated texture.
     */
    public IWmbAllocatedTexture allocate() {
        final int width = this.image.getWidth();
        final int height = this.image.getHeight();
        final ByteBuffer pixelDataBuffer = MemoryUtil.memAlloc(width * height * 4);

        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                final int pixelRgba = this.image.getRGB(x, y);
                pixelDataBuffer.put((byte) ((pixelRgba >> 16) & 0xFF));
                pixelDataBuffer.put((byte) ((pixelRgba >> 8) & 0xFF));
                pixelDataBuffer.put((byte) (pixelRgba & 0xFF));
                pixelDataBuffer.put((byte) ((pixelRgba >> 24) & 0xFF));
            }
        }

        pixelDataBuffer.flip();

        final int textureId = GL30.glGenTextures();
        GLUtils.requireValidId(textureId);

        GL30.glBindTexture(GL30.GL_TEXTURE_2D, textureId);
        GL30.glPixelStorei(GL30.GL_UNPACK_ALIGNMENT, 1);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, filter.id);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, filter.id);
        GL30.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_RGBA, width, height, 0, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE,
                pixelDataBuffer);

        return new WmbAllocatedTexture(textureId, width, height);
    }

    private static final class WmbAllocatedTexture implements IWmbAllocatedTexture {

        private final int id, width, height;

        private WmbAllocatedTexture(int id, int width, int height) {
            this.id = id;
            this.width = width;
            this.height = height;
        }

        @Override
        public int getId() {
            return this.id;
        }

        @Override
        public int getWidth() {
            return this.width;
        }

        @Override
        public int getHeight() {
            return this.height;
        }

        @Override
        public void dispose() {
            GL30.glDeleteTextures(this.id);
        }
    }
}
