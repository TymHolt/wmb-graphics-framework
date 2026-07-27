package org.wmbgf;

import java.util.Objects;

public final class WMBGF {

    private static IApplicationHandler applicationHandler;

    public static void init(IApplicationHandler applicationHandler) {
        if (WMBGF.applicationHandler != null)
            throw new IllegalStateException("WMBGF already initialized");

        Objects.requireNonNull(applicationHandler);
        WMBGF.applicationHandler = applicationHandler;
    }

    public static void destroy() {
        if (WMBGF.applicationHandler == null)
            throw new IllegalStateException("WMBGF not initialized");

        WMBGF.applicationHandler = null;
    }
}
