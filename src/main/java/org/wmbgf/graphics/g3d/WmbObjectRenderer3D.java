package org.wmbgf.graphics.g3d;

import org.lwjgl.opengl.GL30;
import org.wmbgf.graphics.*;
import org.wmbgf.graphics.g2d.ISize2D;

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
     */
    public void prepare(ISize2D framebufferSize, WmbCamera3D camera) {
        prepare(framebufferSize.getWidth(), framebufferSize.getHeight(), camera);
    }

    /**
     * Make the OpenGL state ready for rendering.
     *
     * @param framebufferWidth  The framebuffer width that is the rendering target.
     * @param frameBufferHeight The framebuffer height that is the rendering target.
     * @param camera            The camera to render the objects from, must not be {@code null}.
     */
    public void prepare(int framebufferWidth, int frameBufferHeight, WmbCamera3D camera) {
        GL30.glViewport(0, 0, framebufferWidth, frameBufferHeight);

        // Bind resources
        GL30.glUseProgram(this.objectShader.shader.getId());
        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        this.objectShader.setTextureSlot(0);
        this.objectShader.setCamera(camera, (float) framebufferWidth / (float) frameBufferHeight);

        // OpenGL settings
        GL30.glEnable(GL30.GL_DEPTH_TEST);
        GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        GL30.glEnable(GL30.GL_BLEND);
    }

    /**
     * Set the mesh to use in the render calls. The renderer needs to be prepared before calling this using
     * {@link #prepare(int, int, WmbCamera3D)}.
     *
     * @param mesh The mesh to render, must not be {@code null} and has to support the 3D pipeline.
     */
    public void setObjectMesh(IWmbAllocatedMesh mesh) {
        GL30.glBindVertexArray(mesh.getId());
        this.nextVertexCount = mesh.getVertexCount();
    }

    /**
     * Set the transform to use in the render calls. The renderer needs to be prepared before calling this using
     * {@link #prepare(int, int, WmbCamera3D)}.
     *
     * @param transform The transform to use, must not be {@code null}.
     */
    public void setObjectTransform(WmbObjectTransform3D transform) {
        this.objectShader.setTransform(transform);
    }

    /**
     * Render an object with the given color. The renderer needs to be prepared before calling this using
     * {@link #prepare(int, int, WmbCamera3D)}.
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
     * {@link #prepare(int, int, WmbCamera3D)}.
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
     * {@link #prepare(int, int, WmbCamera3D)}. The texture and color are mixed by multiplication.
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

        private final IWmbAllocatedShader shader;
        private final int viewUL;
        private final int projectionUL;
        private final int transformUL;
        private final int colorUL;
        private final int textureUL;
        private final int renderModeUL;

        ObjectShader() {
            final WmbShaderBuilder shaderBuilder = new WmbShaderBuilder();

            shaderBuilder.appendVertexShaderLn("#version 330 core");
            shaderBuilder.appendVertexShaderLn("layout (location = 0) in vec3 aPosition;");
            shaderBuilder.appendVertexShaderLn("layout (location = 1) in vec2 aTextureUV;");
            shaderBuilder.appendVertexShaderLn("layout (location = 2) in vec3 aNormal;");
            shaderBuilder.appendVertexShaderLn("uniform mat4 uView;");
            shaderBuilder.appendVertexShaderLn("uniform mat4 uProjection;");
            shaderBuilder.appendVertexShaderLn("uniform mat4 uTransform;");
            shaderBuilder.appendVertexShaderLn("out vec2 pTextureUV;");
            shaderBuilder.appendVertexShaderLn("out vec3 pNormal;");
            shaderBuilder.appendVertexShaderLn("void main() {");
            shaderBuilder.appendVertexShaderLn("    mat4 cameraMatrix = uProjection * uView;");
            shaderBuilder.appendVertexShaderLn("    gl_Position = cameraMatrix * uTransform * vec4(aPosition, 1.0);");
            shaderBuilder.appendVertexShaderLn("    pTextureUV = aTextureUV;");
            shaderBuilder.appendVertexShaderLn("    pNormal = aNormal;");
            shaderBuilder.appendVertexShaderLn("}");

            shaderBuilder.appendFragmentShaderLn("#version 330 core");
            shaderBuilder.appendFragmentShaderLn("in vec2 pTextureUV;");
            shaderBuilder.appendFragmentShaderLn("in vec3 pNormal;");
            shaderBuilder.appendFragmentShaderLn("uniform vec4 uColor;");
            shaderBuilder.appendFragmentShaderLn("uniform sampler2D uTexture;");
            shaderBuilder.appendFragmentShaderLn("uniform int uRenderMode;");
            shaderBuilder.appendFragmentShaderLn("out vec4 oFragColor;");
            shaderBuilder.appendFragmentShaderLn("void main() {");
            shaderBuilder.appendFragmentShaderLn("    switch (uRenderMode) {");
            shaderBuilder.appendFragmentShaderLn("        case " + RenderMode.COLORED.id + ":");
            shaderBuilder.appendFragmentShaderLn("            oFragColor = uColor;");
            shaderBuilder.appendFragmentShaderLn("            break;");
            shaderBuilder.appendFragmentShaderLn("        case " + RenderMode.TEXTURED.id + ":");
            shaderBuilder.appendFragmentShaderLn("            oFragColor = texture(uTexture, pTextureUV);");
            shaderBuilder.appendFragmentShaderLn("            break;");
            shaderBuilder.appendFragmentShaderLn("        case " + RenderMode.MIXED.id + ":");
            shaderBuilder.appendFragmentShaderLn("            vec4 texColor = texture(uTexture, pTextureUV);");
            shaderBuilder.appendFragmentShaderLn("            oFragColor = texColor * uColor;");
            shaderBuilder.appendFragmentShaderLn("            break;");
            shaderBuilder.appendFragmentShaderLn("        default:");
            // TODO Prevent GLSL compiler from optimizing normals out before they are actually used here
            shaderBuilder.appendFragmentShaderLn("            //oFragColor = vec4(0.0, 0.0, 0.0, 1.0);");
            shaderBuilder.appendFragmentShaderLn("            oFragColor = vec4(pNormal, 1.0);");
            shaderBuilder.appendFragmentShaderLn("            break;");
            shaderBuilder.appendFragmentShaderLn("    }");
            shaderBuilder.appendFragmentShaderLn("}");

            this.shader = shaderBuilder.allocate();

            try {
                this.viewUL = this.shader.getUniformLocation("uView");
                this.projectionUL = this.shader.getUniformLocation("uProjection");
                this.transformUL = this.shader.getUniformLocation("uTransform");
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
            GLUtils.uniformMat4(this.transformUL, transform.getTransformMatrix());
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
