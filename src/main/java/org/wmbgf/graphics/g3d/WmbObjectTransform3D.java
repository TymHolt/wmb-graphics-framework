package org.wmbgf.graphics.g3d;

import org.joml.Matrix4f;

/**
 * This class represents a transformation of objects, or their vertices, in 3D space. It can produce corresponding
 * matrices.
 */
public final class WmbObjectTransform3D {

    /** The objects x position. */
    public float x;

    /** The objects y position. */
    public float y;

    /** The objects z position. */
    public float z;

    /** The objects pitch rotation. */
    public float pitch;

    /** The objects yaw rotation. */
    public float yaw;

    /** The objects roll rotation. */
    public float roll;

    /**
     * Initializes this transform with the given parameters.
     *
     * @param x     The objects x position.
     * @param y     The objects y position.
     * @param z     The objects z position.
     * @param pitch The objects pitch rotation.
     * @param yaw   The objects yaw rotation.
     * @param roll  The objects roll rotation.
     */
    public WmbObjectTransform3D(float x, float y, float z, float pitch, float yaw, float roll) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
    }

    /**
     * Returns a new {@link Matrix4f} instance with the translation matrix produced from this transform.
     *
     * @return The transform translation matrix.
     */
    public Matrix4f getTranslationMatrix() {
        return new Matrix4f().identity().translate(this.x, this.y, this.z);
    }

    /**
     * Returns a new {@link Matrix4f} instance with the rotation matrix produced from this transform.
     *
     * @return The transform rotation matrix.
     */
    public Matrix4f getRotationMatrix() {
        return new Matrix4f().identity()
                .rotate((float) Math.toRadians(this.yaw), 0.0f, 1.0f, 0.0f)
                .rotate((float) Math.toRadians(this.pitch), 1.0f, 0.0f, 0.0f)
                .rotate((float) Math.toRadians(this.roll), 0.0f, 0.0f, 1.0f);
    }

    /**
     * Returns a new {@link Matrix4f} instance with the full matrix produced from this transform.
     *
     * @return The transform matrix.
     */
    public Matrix4f getTransformMatrix() {
        return getTranslationMatrix().mul(getRotationMatrix());
    }
}
