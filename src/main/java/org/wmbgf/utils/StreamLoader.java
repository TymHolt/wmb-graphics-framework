package org.wmbgf.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * A loader utility to load streams into strings.
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
}
