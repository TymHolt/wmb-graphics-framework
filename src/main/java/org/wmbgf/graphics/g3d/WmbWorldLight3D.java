package org.wmbgf.graphics.g3d;

/**
 * A class representing a world light ("Sun") for 3D rendering.
 */
public final class WmbWorldLight3D {

    /** The x component of the light direction vector. May not be normalized.*/
    public float x;

    /** The y component of the light direction vector. May not be normalized.*/
    public float y;

    /** The z component of the light direction vector. May not be normalized.*/
    public float z;

    /** The ambient light visible in the whole scene independent of direct light exposure. */
    public float ambience;

    /**
     * Initialize the light with the given settings.
     *
     * @param x        The x component of the light direction vector. May not be normalized.
     * @param y        The y component of the light direction vector. May not be normalized.
     * @param z        The z component of the light direction vector. May not be normalized.
     * @param ambience The ambient light visible in the whole scene independent of direct light exposure.
     */
    public WmbWorldLight3D(float x, float y, float z, float ambience) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.ambience = ambience;
    }
}
