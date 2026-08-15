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
     * Append the given text to the fragment source code.
     *
     * @param source The text to append.
     */
    public void appendFragmentShader(String source) {
        Objects.requireNonNull(source);
        this.fsSource.append(source);
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

            return new WmbAllocatedShaderGuard(null); // TODO Return actual result with guard
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
}
