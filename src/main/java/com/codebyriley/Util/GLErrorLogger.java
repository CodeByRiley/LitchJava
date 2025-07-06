package com.codebyriley.Util;

import static org.lwjgl.opengl.GL33.*;

/**
 * Specialized OpenGL error logging utility that integrates with the main Logger system.
 * Provides convenient methods for checking and logging OpenGL errors.
 */
public class GLErrorLogger {
    
    private static final Logger logger = Logger.getInstance();
    
    /**
     * Check for OpenGL errors and log them if found
     * @return true if an error was found and logged, false otherwise
     */
    public static boolean checkGLError() {
        return checkGLError("");
    }
    
    /**
     * Check for OpenGL errors and log them if found
     * @param context Additional context information for the error
     * @return true if an error was found and logged, false otherwise
     */
    public static boolean checkGLError(String context) {
        int error = glGetError();
        if (error != GL_NO_ERROR) {
            String errorMessage = getGLErrorString(error);
            String fullMessage = context.isEmpty() ? 
                "OpenGL Error: " + errorMessage + " (0x" + Integer.toHexString(error).toUpperCase() + ")" :
                "OpenGL Error in " + context + ": " + errorMessage + " (0x" + Integer.toHexString(error).toUpperCase() + ")";
            
            // Get stack trace for better debugging
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            StringBuilder stackInfo = new StringBuilder();
            stackInfo.append(fullMessage).append("\nStack trace:\n");
            
            // Skip first 2 frames (getStackTrace and checkGLError)
            for (int i = 2; i < Math.min(stackTrace.length, 8); i++) {
                StackTraceElement element = stackTrace[i];
                stackInfo.append("  at ").append(element.getClassName())
                        .append(".").append(element.getMethodName())
                        .append("(").append(element.getFileName())
                        .append(":").append(element.getLineNumber()).append(")\n");
            }
            
            logger.error(stackInfo.toString());
            return true;
        }
        return false;
    }
    
    /**
     * Check for OpenGL errors and throw a RuntimeException if found
     * @param context Additional context information for the error
     * @throws RuntimeException if an OpenGL error is detected
     */
    public static void checkGLErrorAndThrow(String context) {
        if (checkGLError(context)) {
            throw new RuntimeException("OpenGL error detected in: " + context);
        }
    }
    
    /**
     * Check for OpenGL errors and throw a RuntimeException if found
     * @throws RuntimeException if an OpenGL error is detected
     */
    public static void checkGLErrorAndThrow() {
        checkGLErrorAndThrow("");
    }
    
    /**
     * Log all pending OpenGL errors (useful for debugging)
     * @return number of errors found and logged
     */
    public static int logAllGLErrors() {
        return logAllGLErrors("");
    }
    
    /**
     * Log all pending OpenGL errors (useful for debugging)
     * @param context Additional context information for the errors
     * @return number of errors found and logged
     */
    public static int logAllGLErrors(String context) {
        int errorCount = 0;
        int error;
        while ((error = glGetError()) != GL_NO_ERROR) {
            String errorMessage = getGLErrorString(error);
            String fullMessage = context.isEmpty() ? 
                "OpenGL Error: " + errorMessage + " (0x" + Integer.toHexString(error).toUpperCase() + ")" :
                "OpenGL Error in " + context + ": " + errorMessage + " (0x" + Integer.toHexString(error).toUpperCase() + ")";
            
            // Get stack trace for better debugging
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            StringBuilder stackInfo = new StringBuilder();
            stackInfo.append(fullMessage).append("\nStack trace:\n");
            
            // Skip first 2 frames (getStackTrace and logAllGLErrors)
            for (int i = 2; i < Math.min(stackTrace.length, 8); i++) {
                StackTraceElement element = stackTrace[i];
                stackInfo.append("  at ").append(element.getClassName())
                        .append(".").append(element.getMethodName())
                        .append("(").append(element.getFileName())
                        .append(":").append(element.getLineNumber()).append(")\n");
            }
            
            logger.error(stackInfo.toString());
            errorCount++;
        }
        return errorCount;
    }
    
    /**
     * Convert OpenGL error code to human-readable string
     * @param error OpenGL error code
     * @return Human-readable error description
     */
    private static String getGLErrorString(int error) {
        switch (error) {
            case GL_NO_ERROR: return "GL_NO_ERROR";
            case GL_INVALID_ENUM: return "GL_INVALID_ENUM";
            case GL_INVALID_VALUE: return "GL_INVALID_VALUE";
            case GL_INVALID_OPERATION: return "GL_INVALID_OPERATION";
            case GL_STACK_OVERFLOW: return "GL_STACK_OVERFLOW";
            case GL_STACK_UNDERFLOW: return "GL_STACK_UNDERFLOW";
            case GL_OUT_OF_MEMORY: return "GL_OUT_OF_MEMORY";
            case GL_INVALID_FRAMEBUFFER_OPERATION: return "GL_INVALID_FRAMEBUFFER_OPERATION";
            // GL_CONTEXT_LOST is not available in all OpenGL versions
            // case GL_CONTEXT_LOST: return "GL_CONTEXT_LOST";
            default: return "Unknown OpenGL Error";
        }
    }
    
    /**
     * Log OpenGL version and vendor information
     */
    public static void logGLInfo() {
        String vendor = glGetString(GL_VENDOR);
        String renderer = glGetString(GL_RENDERER);
        String version = glGetString(GL_VERSION);
        String glslVersion = glGetString(GL_SHADING_LANGUAGE_VERSION);
        
        logger.info("OpenGL Vendor: " + vendor);
        logger.info("OpenGL Renderer: " + renderer);
        logger.info("OpenGL Version: " + version);
        logger.info("GLSL Version: " + glslVersion);
    }
    
    /**
     * Log framebuffer status for debugging
     * @param framebuffer Framebuffer ID to check
     * @param context Context information for the log message
     */
    public static void logFramebufferStatus(int framebuffer, String context) {
        int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
        String statusString = getFramebufferStatusString(status);
        
        if (status == GL_FRAMEBUFFER_COMPLETE) {
            logger.debug("Framebuffer " + framebuffer + " (" + context + ") is complete");
        } else {
            logger.error("Framebuffer " + framebuffer + " (" + context + ") is incomplete: " + statusString);
        }
    }
    
    /**
     * Convert framebuffer status code to human-readable string
     * @param status Framebuffer status code
     * @return Human-readable status description
     */
    private static String getFramebufferStatusString(int status) {
        switch (status) {
            case GL_FRAMEBUFFER_COMPLETE: return "GL_FRAMEBUFFER_COMPLETE";
            case GL_FRAMEBUFFER_UNDEFINED: return "GL_FRAMEBUFFER_UNDEFINED";
            case GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT: return "GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT";
            case GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT: return "GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT";
            case GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER: return "GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER";
            case GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER: return "GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER";
            case GL_FRAMEBUFFER_UNSUPPORTED: return "GL_FRAMEBUFFER_UNSUPPORTED";
            case GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE: return "GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE";
            case GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS: return "GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS";
            default: return "Unknown Framebuffer Status";
        }
    }

    /**
     * Enhanced error checking with detailed context and OpenGL state information
     * @param context Context information for the error
     * @param operation Description of what operation was being performed
     * @return true if an error was found and logged, false otherwise
     */
    public static boolean checkGLErrorDetailed(String context, String operation) {
        int error = glGetError();
        if (error != GL_NO_ERROR) {
            String errorMessage = getGLErrorString(error);
            StringBuilder detailedMessage = new StringBuilder();
            detailedMessage.append("OpenGL Error in ").append(context).append(": ")
                        .append(errorMessage).append(" (0x").append(Integer.toHexString(error).toUpperCase()).append(")\n");
            detailedMessage.append("Operation: ").append(operation).append("\n");
            
            // Get stack trace
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            detailedMessage.append("Stack trace:\n");
            for (int i = 2; i < Math.min(stackTrace.length, 10); i++) {
                StackTraceElement element = stackTrace[i];
                detailedMessage.append("  at ").append(element.getClassName())
                            .append(".").append(element.getMethodName())
                            .append("(").append(element.getFileName())
                            .append(":").append(element.getLineNumber()).append(")\n");
            }
            
            // Add OpenGL state information
            detailedMessage.append("OpenGL State:\n");
            try {
                detailedMessage.append("  Current Program: ").append(glGetInteger(GL_CURRENT_PROGRAM)).append("\n");
                detailedMessage.append("  Active Texture: ").append(glGetInteger(GL_ACTIVE_TEXTURE)).append("\n");
                detailedMessage.append("  Bound VAO: ").append(glGetInteger(GL_VERTEX_ARRAY_BINDING)).append("\n");
                detailedMessage.append("  Bound VBO: ").append(glGetInteger(GL_ARRAY_BUFFER_BINDING)).append("\n");
                detailedMessage.append("  Blend Enabled: ").append(glIsEnabled(GL_BLEND)).append("\n");
                detailedMessage.append("  Depth Test Enabled: ").append(glIsEnabled(GL_DEPTH_TEST)).append("\n");
            } catch (Exception e) {
                detailedMessage.append("  Could not retrieve OpenGL state: ").append(e.getMessage()).append("\n");
            }
            
            logger.error(detailedMessage.toString());
            return true;
        }
        return false;
    }

    /**
     * Check for OpenGL errors with automatic context detection
     * @return true if an error was found and logged, false otherwise
     */
    public static boolean checkGLErrorAuto() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length > 2) {
            StackTraceElement caller = stackTrace[2];
            String context = caller.getClassName() + "." + caller.getMethodName();
            String operation = "Line " + caller.getLineNumber() + " in " + caller.getFileName();
            return checkGLErrorDetailed(context, operation);
        }
        return checkGLError("");
    }
} 