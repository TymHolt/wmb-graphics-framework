package org.wmbgf.graphics.g3d;

import org.lwjgl.opengl.GL30;
import org.wmbgf.graphics.*;
import org.wmbgf.graphics.g2d.ISize2D;
import org.wmbgf.utils.StreamLoader;

import java.io.IOException;

/**
 * A basic 3D renderer.
 */
public final class WmbObjectRenderer3D {

    private final ObjectShader objectShader;
    private int nextVertexCount = 0;

    /**
     * Allocates the needed resources.
     */
    public WmbObjectRenderer3D() {
        this.objectShader = new ObjectShader();
    }

    /**
     * Dispose allocated resources of the renderer.
     */
    public void dispose() {
        this.objectShader.shader.dispose();
    }

    /**
     * Make the OpenGL state ready for rendering.
     *
     * @param framebufferSize The framebuffer size that is the rendering target.
     * @param camera          The camera to render the objects from, must not be {@code null}.
     * @param light           The light to use in the rendering of the world, must not be {@code null}.
     */
    public void prepare(ISize2D framebufferSize, WmbCamera3D camera, WmbWorldLight3D light) {
        prepare(framebufferSize.getWidth(), framebufferSize.getHeight(), camera, light);
    }

    /**
     * Make the OpenGL state ready for rendering.
     *
     * @param framebufferWidth  The framebuffer width that is the rendering target.
     * @param frameBufferHeight The framebuffer height that is the rendering target.
     * @param camera            The camera to render the objects from, must not be {@code null}.
     * @param light             The light to use in the rendering of the world, must not be {@code null}.
     */
    public void prepare(int framebufferWidth, int frameBufferHeight, WmbCamera3D camera, WmbWorldLight3D light) {
        GL30.glViewport(0, 0, framebufferWidth, frameBufferHeight);

        // Bind resources
        GL30.glUseProgram(this.objectShader.shader.getId());
        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        this.objectShader.setTextureSlot(0);
        this.objectShader.setCamera(camera, (float) framebufferWidth / (float) frameBufferHeight);
        this.objectShader.setAmbienceFactor(light.ambience);
        this.objectShader.setLightDirection(light.x, light.y, light.z);

        // OpenGL settings
        GL30.glEnable(GL30.GL_DEPTH_TEST);
        GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        GL30.glEnable(GL30.GL_BLEND);
    }

    /**
     * Set the mesh to use in the render calls. The renderer needs to be prepared before calling this using
     * {@link #prepare(int, int, WmbCamera3D, WmbWorldLight3D)}.
     *
     * @param mesh The mesh to render, must not be {@code null} and has to support the 3D pipeline.
     */
    public void setObjectMesh(IWmbAllocatedMesh mesh) {
        GL30.glBindVertexArray(mesh.getId());
        this.nextVertexCount = mesh.getVertexCount();
    }

    /**
     * Set the transform to use in the render calls. The renderer needs to be prepared before calling this using
     * {@link #prepare(int, int, WmbCamera3D, WmbWorldLight3D)}.
     *
     * @param transform The transform to use, must not be {@code null}.
     */
    public void setObjectTransform(WmbObjectTransform3D transform) {
        this.objectShader.setTransform(transform);
    }

    /**
     * Render an object with the given color. The renderer needs to be prepared before calling this using
     * {@link #prepare(int, int, WmbCamera3D, WmbWorldLight3D)}.
     *
     * @param r Red component of the object color.
     * @param g Green component of the object color.
     * @param b Bue component of the object color.
     * @param a Alpha component of the object color.
     */
    public void render(float r, float g, float b, float a) {
        this.objectShader.setRenderMode(RenderMode.COLORED);
        this.objectShader.setColor(r, g, b, a);
        GLUtils.issueElementsDrawCall(this.nextVertexCount);
    }

    /**
     * Render an object with the given texture. The renderer needs to be prepared before calling this using
     * {@link #prepare(int, int, WmbCamera3D, WmbWorldLight3D)}.
     *
     * @param texture The texture to apply.
     */
    public void render(IWmbAllocatedTexture texture) {
        this.objectShader.setRenderMode(RenderMode.TEXTURED);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, texture.getId());
        GLUtils.issueElementsDrawCall(this.nextVertexCount);
    }

    /**
     * Render an object with the given color and texture. The renderer needs to be prepared before calling this using
     * {@link #prepare(int, int, WmbCamera3D, WmbWorldLight3D)}. The texture and color are mixed by multiplication.
     *
     * @param texture The texture to fill the sprite with.
     * @param r       Red component of the sprite color.
     * @param g       Green component of the sprite color.
     * @param b       Bue component of the sprite color.
     * @param a       Alpha component of the sprite color.
     */
    public void render(IWmbAllocatedTexture texture, float r, float g, float b, float a) {
        this.objectShader.setRenderMode(RenderMode.MIXED);
        this.objectShader.setColor(r, g, b, a);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, texture.getId());
        GLUtils.issueElementsDrawCall(this.nextVertexCount);
    }

    /**
     * Remove renderer resources from OpenGL state.
     */
    public void finish() {
        GL30.glBindVertexArray(0);
        GL30.glUseProgram(0);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, 0);
    }

    private enum RenderMode {
        COLORED(0),
        TEXTURED(1),
        MIXED(2);

        private final int id;

        RenderMode(int id) {
            this.id = id;
        }
    }

    private static class ObjectShader {

        private static final String VS_CLASS_PATH = "/shaders/object_renderer_3d_vs.glsl";
        private static final String FS_CLASS_PATH = "/shaders/object_renderer_3d_fs.glsl";

        private final IWmbAllocatedShader shader;
        private final int viewUL;
        private final int projectionUL;
        private final int rotationUL;
        private final int transformUL;
        private final int lightDirectionUL;
        private final int ambienceFactorUL;
        private final int colorUL;
        private final int textureUL;
        private final int renderModeUL;

        ObjectShader() {
            final WmbShaderBuilder shaderBuilder = new WmbShaderBuilder();

            try {
                shaderBuilder.appendVertexShader(StreamLoader.loadResourceToString(VS_CLASS_PATH));
                shaderBuilder.appendFragmentShader(StreamLoader.loadResourceToString(FS_CLASS_PATH));
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }

            this.shader = shaderBuilder.allocate();
            try {
                this.viewUL = this.shader.getUniformLocation("uView");
                this.projectionUL = this.shader.getUniformLocation("uProjection");
                this.rotationUL = this.shader.getUniformLocation("uRotation");
                this.transformUL = this.shader.getUniformLocation("uTransform");
                this.lightDirectionUL = this.shader.getUniformLocation("uLightDirection");
                this.ambienceFactorUL = this.shader.getUniformLocation("uAmbienceFactor");
                this.colorUL = this.shader.getUniformLocation("uColor");
                this.textureUL = this.shader.getUniformLocation("uTexture");
                this.renderModeUL = this.shader.getUniformLocation("uRenderMode");
            } catch (Exception exception) {
                this.shader.dispose();
                throw exception;
            }
        }

        void setCamera(WmbCamera3D camera, float aspect) {
            GLUtils.uniformMat4(this.viewUL, camera.getViewMatrix());
            GLUtils.uniformMat4(this.projectionUL, camera.getProjectionMatrix(aspect));
        }

        void setTransform(WmbObjectTransform3D transform) {
            GLUtils.uniformMat4(this.rotationUL, transform.getRotationMatrix());
            GLUtils.uniformMat4(this.transformUL, transform.getTransformMatrix());
        }

        void setLightDirection(float x, float y, float z) {
            GL30.glUniform3f(this.lightDirectionUL, x, y, z);
        }

        void setAmbienceFactor(float value) {
            GL30.glUniform1f(this.ambienceFactorUL, value);
        }

        void setColor(float red, float green, float blue, float alpha) {
            GL30.glUniform4f(this.colorUL, red, green, blue, alpha);
        }

        void setTextureSlot(int slot) {
            GL30.glUniform1i(this.textureUL, slot);
        }

        void setRenderMode(RenderMode mode) {
            GL30.glUniform1i(this.renderModeUL, mode.id);
        }
    }
}
