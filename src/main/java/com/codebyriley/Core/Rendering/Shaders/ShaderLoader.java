package com.codebyriley.Core.Rendering.Shaders;

import static org.lwjgl.opengl.GL33.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import com.codebyriley.Util.Log;

public class ShaderLoader {
    
    /**
     * Read shader source code from a resource file
     * @param resourcePath Path to the shader resource file
     * @return Shader source code as a string
     */
    public static String readShaderFromResource(String resourcePath) {
        InputStream in = ShaderLoader.class.getResourceAsStream(resourcePath);
        if (in == null) {
            throw new RuntimeException("Shader resource not found: " + resourcePath);
        }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read shader resource: " + resourcePath, e);
        }
        Log.debug("Loaded shader from: " + resourcePath);
        return sb.toString();
    }
    
    /**
     * Compile a shader from source code
     * @param shaderType The type of shader (GL_VERTEX_SHADER or GL_FRAGMENT_SHADER)
     * @param source The shader source code
     * @return The compiled shader ID
     */
    public static int compileShader(int shaderType, String source) {
        int shader = glCreateShader(shaderType);
        glShaderSource(shader, source);
        glCompileShader(shader);
        
        // Check for compilation errors
        int[] success = new int[1];
        glGetShaderiv(shader, GL_COMPILE_STATUS, success);
        if (success[0] == GL_FALSE) {
            String infoLog = glGetShaderInfoLog(shader);
            glDeleteShader(shader);
            throw new RuntimeException("Shader compilation failed: " + infoLog);
        }
        
        return shader;
    }
    
    /**
     * Create a shader program from vertex and fragment shader sources
     * @param vertexSource Vertex shader source code
     * @param fragmentSource Fragment shader source code
     * @return The linked shader program ID
     */
    public static int createShaderProgram(String vertexSource, String fragmentSource) {
        // Compile vertex shader
        int vertexShader = compileShader(GL_VERTEX_SHADER, vertexSource);
        
        // Compile fragment shader
        int fragmentShader = compileShader(GL_FRAGMENT_SHADER, fragmentSource);
        
        // Create and link program
        int program = glCreateProgram();
        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);
        glLinkProgram(program);
        
        // Check for linking errors
        int[] success = new int[1];
        glGetProgramiv(program, GL_LINK_STATUS, success);
        if (success[0] == GL_FALSE) {
            String infoLog = glGetProgramInfoLog(program);
            glDeleteProgram(program);
            glDeleteShader(vertexShader);
            glDeleteShader(fragmentShader);
            throw new RuntimeException("Shader program linking failed: " + infoLog);
        }
        
        // Clean up shaders (they're now attached to the program)
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
        
        Log.info("Successfully created shader program");
        return program;
    }
    
    /**
     * Load and compile a complete shader program from resource files
     * @param vertexShaderPath Path to the vertex shader resource file
     * @param fragmentShaderPath Path to the fragment shader resource file
     * @return The compiled and linked shader program ID
     */
    public static int loadShaderProgram(String vertexShaderPath, String fragmentShaderPath) {
        String vertexSource = readShaderFromResource(vertexShaderPath);
        String fragmentSource = readShaderFromResource(fragmentShaderPath);
        return createShaderProgram(vertexSource, fragmentSource);
    }
    
    /**
     * Load and compile a complete shader program from resource files (with shaders/ prefix)
     * @param vertexShaderName Name of the vertex shader file (e.g., "BaseVertexShader.vert.glsl")
     * @param fragmentShaderName Name of the fragment shader file (e.g., "BaseFragmentShader.frag.glsl")
     * @return The compiled and linked shader program ID
     */
    public static int loadShaderProgramFromShaders(String vertexShaderName, String fragmentShaderName) {
        return loadShaderProgram("/shaders/" + vertexShaderName, "/shaders/" + fragmentShaderName);
    }
    
    /**
     * Delete a shader program and free OpenGL resources
     * @param program The shader program ID to delete
     */
    public static void deleteShaderProgram(int program) {
        if (program != 0) {
            Log.infoEveryNFrames("[ShaderLoader] deleteShaderProgram: programId=" + program, 120);
            glDeleteProgram(program);
        }
    }
}
