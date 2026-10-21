package org.wmbgf.graphics.g3d;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * This class represents a 3D projection camera, with position, rotation and other modifiers. It can produce
 * corresponding projection and transformation matrices.
 */
public final class WmbCamera3D {

    /** The cameras x position. */
    public float x;

    /** The cameras y position. */
    public float y;

    /** The cameras z position. */
    public float z;

    /** The cameras pitch rotation. */
    public float pitch;

    /** The cameras yaw rotation. */
    public float yaw;

    /** The cameras field-of-view. */
    public float fov;

    /** The cameras near clip plane. */
    public float near;

    /** The cameras far clip plane. */
    public float far;

    /**
     * Create the camera with the given initial values.
     *
     * @param x     The cameras x position.
     * @param y     The cameras y position.
     * @param z     The cameras z position.
     * @param pitch The cameras pitch rotation.
     * @param yaw   The cameras yaw rotation.
     * @param fov   The cameras field-of-view.
     * @param near  The cameras near clip plane.
     * @param far   The cameras far clip plane.
     */
    public WmbCamera3D(float x, float y, float z, float pitch, float yaw, float fov, float near, float far) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
        this.fov = fov;
        this.near = near;
        this.far = far;
    }

    /**
     * Same as {@link #move(Vector3f)}, just with a {@link Vector4f} whose w component is ignored. This method can be
     * useful for easier calculations without needing to produce a {@link Vector3f}.
     *
     * @param direction The direction to move the camera in, must not be {@code null}.
     */
    public void move(Vector4f direction) {
        x += direction.x;
        y += direction.y;
        z += direction.z;
    }

    /**
     * Moves the camera along the given vector.
     *
     * @param direction The direction to move the camera in, must not be {@code null}.
     */
    public void move(Vector3f direction) {
        x += direction.x;
        y += direction.y;
        z += direction.z;
    }

    /**
     * Produces a new {@link Matrix4f} instance based on the cameras rotation settings. This matrix can be applied to
     * vertices to rotate them from the cameras point-of-view.
     *
     * @return The rotation matrix from the cameras point-of-view.
     */
    public Matrix4f getRotationMatrix() {
        return new Matrix4f().identity()
                .rotate((float) Math.toRadians(this.pitch), 1.0f, 0.0f, 0.0f)
                .rotate((float) Math.toRadians(this.yaw), 0.0f, 1.0f, 0.0f);
    }

    /**
     * Produces a new {@link Matrix4f} instance based on the cameras rotation settings. This matrix can be applied to
     * vertices to rotate them from the worlds point-of-view. This method just uses the {@code yaw} rotation.
     *
     * @return The rotation matrix from the worlds point-of-view.
     */
    public Matrix4f getLookYawRotationMatrix() {
        return new Matrix4f().identity()
                .rotate((float) Math.toRadians(-this.yaw), 0.0f, 1.0f, 0.0f);
    }

    /**
     * Produces a new {@link Matrix4f} instance based on the cameras rotation settings. This matrix can be applied to
     * vertices to rotate them from the worlds point-of-view.
     *
     * @return The rotation matrix from the worlds point-of-view.
     */
    public Matrix4f getLookRotationMatrix() {
        return new Matrix4f().identity()
                .rotate((float) Math.toRadians(-this.yaw), 0.0f, 1.0f, 0.0f)
                .rotate((float) Math.toRadians(-this.pitch), 1.0f, 0.0f, 0.0f);
    }

    /**
     * Produces a new normalized {@link Vector3f} pointing in the direction the camera is looking.
     *
     * @return The cameras look direction.
     */
    public Vector3f getLookVector() {
        final Matrix4f rotationMatrix = getLookRotationMatrix();
        final Vector4f forward = rotationMatrix.transform(new Vector4f(0.0f, 0.0f, -1.0f, 1.0f));
        return forward.xyz(new Vector3f());
    }

    /**
     * Produces a new {@link Matrix4f} instance with the cameras settings. This matrix can be used to transform vertices
     * from the cameras point-of-view. The projection is not part of this matrix.
     *
     * @return The view matrix of this camera.
     */
    public Matrix4f getViewMatrix() {
        return getRotationMatrix().mul(new Matrix4f().identity().translate(-this.x, -this.y, -this.z));
    }

    /**
     * Produces a new {@link Matrix4f} instance with this cameras projection settings. This matrix can be used to
     * project vertices.
     *
     * @param aspect The aspect ratio of the projected screen.
     * @return The projection matrix of this camera.
     */
    public Matrix4f getProjectionMatrix(float aspect) {
        return new Matrix4f().identity().perspective(fov, aspect, near, far);
    }

    /**
     * Produces a new {@link Matrix4f} instance with this cameras projection settings. This matrix is the inverse
     * projection matrix and can be used to resolve points from the projected screen space.
     *
     * @param aspect The aspect ratio of the projected screen.
     * @return The inverse projection matrix of this camera.
     */
    public Matrix4f getProjectionMatrixInverse(float aspect) {
        return getProjectionMatrix(aspect).invert();
    }
}
