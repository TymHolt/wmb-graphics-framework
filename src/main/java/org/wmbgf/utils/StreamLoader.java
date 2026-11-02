package org.wmbgf.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * A loader utility to load streams and resources.
 */
public final class StreamLoader {

    /**
     * Load the given {@link InputStream} as a {@link String}.
     *
     * @param stream       The stream to read into a string.
     * @return             The string containing the {@code UTF-8} data of the stream.
     * @throws IOException If any IO-Errors occur.
     */
    public static String loadStreamToString(InputStream stream) throws IOException {
        return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
    }


    /**
     * Load the resource identified by the given path as an {@link InputStream}.
     *
     * @param  path        The path to the resource in the class-path.
     * @return             The stream of the resource. Never {@code null}.
     * @throws IOException If any IO-Errors occur or if the resource does not exist.
     */
    public static InputStream loadResourceAsStream(String path) throws IOException {
        final InputStream stream = StreamLoader.class.getResourceAsStream(path);

        if (stream == null)
            throw new IOException("Resource " + path + " no found");

        return stream;
    }

    /**
     * Load the resource identified by the given path as a {@link String}.
     *
     * @param  path        The path to the resource in the class-path.
     * @return             The loaded text resource. Never {@code null}.
     * @throws IOException If any IO-Errors occur or if the resource does not exist.
     */
    public static String loadResourceToString(String path) throws IOException {
        return loadStreamToString(loadResourceAsStream(path));
    }

    /**
     * Load the resource identified by the given path as a {@link BufferedImage}.
     *
     * @param  path        The path to the resource in the class-path.
     * @return             The loaded image resource. Never {@code null}.
     * @throws IOException If any IO-Errors occur or if the resource does not exist.
     */
    public static BufferedImage loadResourceToImage(String path) throws IOException {
        return ImageIO.read(loadResourceAsStream(path));
    }
}
