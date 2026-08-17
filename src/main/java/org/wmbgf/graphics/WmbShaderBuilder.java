package org.wmbgf.graphics;

import org.lwjgl.opengl.GL30;

import java.util.Objects;

public final class WmbShaderBuilder {

    private final StringBuilder vsSource = new StringBuilder();
    private final StringBuilder fsSource = new StringBuilder();

    /**
     * Append the given text to the vertex source code.
     *
     * @param source The text to append.
     */
    public void appendVertexShader(String source) {
        Objects.requireNonNull(source);
        this.vsSource.append(source);
    }

    /**
     * Append the given text to the vertex source code and append a new-line (\n).
     *
     * @param source The text to append as a line.
     */
    public void appendVertexShaderLn(String source) {
        appendVertexShader(source + '\n');
    }

    /**
     * Append the given text to the fragment source code.
     *
     * @param source The text to append.
     */
    public void appendFragmentShader(String source) {
        Objects.requireNonNull(source);
        this.fsSource.append(source);
    }

    /**
     * Append the given text to the fragment source code and append a new-line (\n).
     *
     * @param source The text to append as a line.
     */
    public void appendFragmentShaderLn(String source) {
        appendFragmentShader(source + '\n');
    }

    /**
     * Clears the added source data.
     */
    public void clear() {
        this.vsSource.setLength(0);
        this.fsSource.setLength(0);
    }

    /**
     * Creates and allocates a shader object from the added sources on the GPU. Can throw RuntimeException,
     * CompileException or LinkException when something goes wrong.
     *
     * @return The allocated shader.
     */
    public IWmbAllocatedShader allocate() {
        try (GLShader vShader = GLShader.compile(GL30.GL_VERTEX_SHADER, this.vsSource.toString());
             GLShader fShader = GLShader.compile(GL30.GL_FRAGMENT_SHADER, this.fsSource.toString())) {

            final int programId = GL30.glCreateProgram();
            GLUtils.requireValidId(programId);

            GL30.glAttachShader(programId, vShader.id);
            GL30.glAttachShader(programId, fShader.id);
            GL30.glLinkProgram(programId);
            GL30.glDetachShader(programId, vShader.id);
            GL30.glDetachShader(programId, fShader.id);

            if (GL30.glGetProgrami(programId, GL30.GL_LINK_STATUS) == 0) {
                final String log = GL30.glGetProgramInfoLog(programId);
                GL30.glDeleteProgram(programId);
                throw new LinkException(log);
            }

            return new WmbAllocatedShaderGuard(new WmbAllocatedShader(programId));
        }
    }

    private record GLShader(int id) implements AutoCloseable {

        static GLShader compile(int type, String source) {
            final int id = GL30.glCreateShader(type);
            GLUtils.requireValidId(id);

            GL30.glShaderSource(id, source);
            GL30.glCompileShader(id);

            if (GL30.glGetShaderi(id, GL30.GL_COMPILE_STATUS) == 0) {
                GL30.glDeleteShader(id);
                throw new CompileException(type, GL30.glGetShaderInfoLog(id));
            }

            return new GLShader(id);
        }

        @Override
        public void close() {
            GL30.glDeleteShader(this.id);
        }
    }

    private static final class WmbAllocatedShader implements IWmbAllocatedShader {

        private final int id;

        private WmbAllocatedShader(int id) {
            this.id = id;
        }

        @Override
        public int getId() {
            return this.id;
        }

        @Override
        public int getUniformLocation(String name) {
            Objects.requireNonNull(name, "name");
            return GL30.glGetUniformLocation(this.id, name);
        }

        @Override
        public void dispose() {
            GL30.glDeleteProgram(this.id);
        }
    }

    /**
     * This class contains an IWmbAllocatedShader instance and propagates all function calls to the contained instance. The
     * instance is set to null when disposed, that way all following calls will produce a NullPointerException as that
     * resource is not available anymore.
     */
    public static final class WmbAllocatedShaderGuard implements IWmbAllocatedShader {

        private IWmbAllocatedShader shader;

        private WmbAllocatedShaderGuard(IWmbAllocatedShader shader) {
            this.shader = shader;
        }

        @Override
        public int getId() {
            return this.shader.getId();
        }

        @Override
        public int getUniformLocation(String name) {
            Objects.requireNonNull(name);

            final int location = this.shader.getUniformLocation(name);
            if (location < 0)
                throw new ResolveException(name);

            return location;
        }

        @Override
        public void dispose() {
            this.shader.dispose();
            this.shader = null;
        }
    }

    public static final class CompileException extends RuntimeException {

        public final String log;

        private CompileException(int type, String log) {
            super(getShaderName(type) + " shader failed to compile");
            this.log = log;
        }

        private static String getShaderName(int type) {
            return switch (type) {
                case GL30.GL_VERTEX_SHADER -> "Vertex";
                case GL30.GL_FRAGMENT_SHADER -> "Fragment";
                default -> "Unknown";
            };
        }
    }

    public static final class LinkException extends RuntimeException {

        public final String log;

        private LinkException(String log) {
            super("Program creation failed");
            this.log = log;
        }
    }

    public static final class ResolveException extends RuntimeException {

        private ResolveException(String name) {
            super("Could not find uniform location for name '" + name + "'");
        }
    }
}
