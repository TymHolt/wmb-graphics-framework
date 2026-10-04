package org.wmbgf.graphics.g2d;

import org.lwjgl.opengl.GL30;
import org.wmbgf.graphics.*;

/**
 * A basic renderer for 2D sprites with different rendering options.
 */
public final class WmbSpriteRenderer2D {

    private final IWmbAllocatedMesh spriteMesh;
    private final IWmbAllocatedShader spriteShader;
    private final int colorUL;
    private final int textureUL;
    private final int texturedModeUL;

    private int framebufferWidth = 0;
    private int framebufferHeight = 0;

    /**
     * Create a rendering instance for rendering sprites. Allocates the needed resources.
     */
    public WmbSpriteRenderer2D() {
        this.spriteMesh = buildSpriteMesh();
        try {
            this.spriteShader = buildSpriteShader();
            try {
                this.colorUL = this.spriteShader.getUniformLocation("uColor");
                this.textureUL = this.spriteShader.getUniformLocation("uTexture");
                this.texturedModeUL = this.spriteShader.getUniformLocation("uTexturedMode");
            } catch (Exception exception) {
                this.spriteShader.dispose();
                throw exception;
            }
        } catch (Exception exception) {
            this.spriteMesh.dispose();
            throw exception;
        }
    }

    /**
     * Dispose allocated resources of the renderer.
     */
    public void dispose() {
        this.spriteMesh.dispose();
        this.spriteShader.dispose();
    }

    /**
     * Make the OpenGL state ready for sprite rendering.
     *
     * @param framebufferSize The framebuffer size that is the rendering target.
     */
    public void prepare(ISize2D framebufferSize) {
        prepare(framebufferSize.getWidth(), framebufferSize.getHeight());
    }

    /**
     * Make the OpenGL state ready for sprite rendering.
     *
     * @param framebufferWidth The framebuffer width that is the rendering target.
     * @param frameBufferHeight The framebuffer height that is the rendering target.
     */
    public void prepare(int framebufferWidth, int frameBufferHeight) {
        this.framebufferWidth = framebufferWidth;
        this.framebufferHeight = frameBufferHeight;

        // Bind resources
        GL30.glBindVertexArray(this.spriteMesh.getId());
        GL30.glUseProgram(this.spriteShader.getId());
        GL30.glActiveTexture(GL30.GL_TEXTURE0);

        // OpenGL settings
        GL30.glDisable(GL30.GL_DEPTH_TEST);
        GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        GL30.glEnable(GL30.GL_BLEND);
    }

    /**
     * Render a sprite with the given bounds and color. The renderer needs to be prepared before calling this using
     * prepare(). Bounds orientation is from the top-left corner.
     *
     * @param x The x coordinate of the sprite bounds.
     * @param y The y coordinate of the sprite bounds.
     * @param width The width of the sprite bounds.
     * @param height The height of the sprite bounds.
     * @param r Red component of the sprite color.
     * @param g Green component of the sprite color.
     * @param b Bue component of the sprite color.
     * @param a Alpha component of the sprite color.
     */
    public void render(int x, int y, int width, int height, float r, float g, float b, float a) {
        // Correct Y to be oriented from top-left corner
        final int correctedY = this.framebufferHeight - y - height;
        GL30.glViewport(x, correctedY, width, height);
        GL30.glUniform1i(this.texturedModeUL, 0);
        GL30.glUniform4f(this.colorUL, r, g, b, a);
        GLUtils.issueElementsDrawCall(this.spriteMesh.getVertexCount());
    }

    /**
     * Render a sprite with the given bounds and texture. The renderer needs to be prepared before calling this using
     * prepare(). Bounds orientation is from the top-left corner.
     *
     * @param x The x coordinate of the sprite bounds.
     * @param y The y coordinate of the sprite bounds.
     * @param width The width of the sprite bounds.
     * @param height The height of the sprite bounds.
     * @param texture The texture to fill the sprite with.
     */
    public void render(int x, int y, int width, int height, IWmbAllocatedTexture texture) {
        // Correct Y to be oriented from top-left corner
        final int correctedY = this.framebufferHeight - y - height;
        GL30.glViewport(x, correctedY, width, height);
        GL30.glUniform1i(this.texturedModeUL, 1);
        GL30.glUniform1i(this.textureUL, 0);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, texture.getId());
        GLUtils.issueElementsDrawCall(this.spriteMesh.getVertexCount());
    }

    /**
     * Remove renderer resources from OpenGL state.
     */
    public void finish() {
        GL30.glBindVertexArray(0);
        GL30.glUseProgram(0);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, 0);
    }

    private static IWmbAllocatedMesh buildSpriteMesh() {
        final WmbMeshBuilder2D meshBuilder = new WmbMeshBuilder2D(WmbPrimitiveType.TRIANGLE);

        meshBuilder.addVertex(-1.0f, 1.0f);
        meshBuilder.addVertex(-1.0f, -1.0f);
        meshBuilder.addVertex(1.0f, -1.0f);

        meshBuilder.addVertex(1.0f, -1.0f);
        meshBuilder.addVertex(1.0f, 1.0f);
        meshBuilder.addVertex(-1.0f, 1.0f);

        return meshBuilder.allocate(false);
    }

    private static IWmbAllocatedShader buildSpriteShader() {
        final WmbShaderBuilder shaderBuilder = new WmbShaderBuilder();

        shaderBuilder.appendVertexShaderLn("#version 330 core");
        shaderBuilder.appendVertexShaderLn("layout (location = 0) in vec2 aPosition;");
        shaderBuilder.appendVertexShaderLn("out vec2 pTexturePosition;");
        shaderBuilder.appendVertexShaderLn("void main() {");
        shaderBuilder.appendVertexShaderLn("    gl_Position = vec4(aPosition, 0.0, 1.0);");
        shaderBuilder.appendVertexShaderLn("    pTexturePosition = vec2((aPosition.x + 1.0) / 2.0,");
        shaderBuilder.appendVertexShaderLn("        (aPosition.y + 1.0) / 2.0);");
        shaderBuilder.appendVertexShaderLn("}");

        shaderBuilder.appendFragmentShaderLn("#version 330 core");
        shaderBuilder.appendFragmentShaderLn("in vec2 pTexturePosition;");
        shaderBuilder.appendFragmentShaderLn("uniform vec4 uColor;");
        shaderBuilder.appendFragmentShaderLn("uniform sampler2D uTexture;");
        shaderBuilder.appendFragmentShaderLn("uniform int uTexturedMode;");
        shaderBuilder.appendFragmentShaderLn("out vec4 oFragColor;");
        shaderBuilder.appendFragmentShaderLn("void main() {");
        shaderBuilder.appendFragmentShaderLn("    if (uTexturedMode == 0)");
        shaderBuilder.appendFragmentShaderLn("        oFragColor = uColor;");
        shaderBuilder.appendFragmentShaderLn("    else");
        shaderBuilder.appendFragmentShaderLn("        oFragColor = texture(uTexture, pTexturePosition);");
        shaderBuilder.appendFragmentShaderLn("}");

        return shaderBuilder.allocate();
    }
}
