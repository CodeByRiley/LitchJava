package com.codebyriley.Util;

/**
 * Simple logging facade that provides static methods for easier usage throughout the codebase.
 * This makes it simple to replace System.out.println calls with proper logging.
 */
public class Log {
    
    private static final Logger logger = Logger.getInstance();
    
    // Static convenience methods that delegate to the Logger instance
    
    public static void trace(String message) { logger.trace(message); }
    public static void trace(String message, Throwable throwable) { logger.trace(message, throwable); }
    
    public static void debug(String message) { logger.debug(message); }
    public static void debug(String message, Throwable throwable) { logger.debug(message, throwable); }
    
    public static void info(String message) { logger.info(message); }
    public static void info(String message, Throwable throwable) { logger.info(message, throwable); }
    
    public static void warn(String message) { logger.warn(message); }
    public static void warn(String message, Throwable throwable) { logger.warn(message, throwable); }
    
    public static void error(String message) { logger.error(message); }
    public static void error(String message, Throwable throwable) { logger.error(message, throwable); }
    
    public static void fatal(String message) { logger.fatal(message); }
    public static void fatal(String message, Throwable throwable) { logger.fatal(message, throwable); }
    
    // Configuration methods
    public static void setLogLevel(Logger.LogLevel level) { logger.setLogLevel(level); }
    public static void setConsoleOutput(boolean enable) { logger.setConsoleOutput(enable); }
    public static void setFileOutput(boolean enable) { logger.setFileOutput(enable); }
    public static void setLogFilePath(String path) { logger.setLogFilePath(path); }
    
    // OpenGL error checking convenience methods
    public static boolean checkGLError() { return GLErrorLogger.checkGLError(); }
    public static boolean checkGLError(String context) { return GLErrorLogger.checkGLError(context); }
    public static boolean checkGLErrorAuto() { return GLErrorLogger.checkGLErrorAuto(); }
    public static boolean checkGLErrorDetailed(String context, String operation) { return GLErrorLogger.checkGLErrorDetailed(context, operation); }
    public static void checkGLErrorAndThrow() { GLErrorLogger.checkGLErrorAndThrow(); }
    public static void checkGLErrorAndThrow(String context) { GLErrorLogger.checkGLErrorAndThrow(context); }
    public static int logAllGLErrors() { return GLErrorLogger.logAllGLErrors(); }
    public static int logAllGLErrors(String context) { return GLErrorLogger.logAllGLErrors(context); }
    public static void logGLInfo() { GLErrorLogger.logGLInfo(); }
    public static void logFramebufferStatus(int framebuffer, String context) { 
        GLErrorLogger.logFramebufferStatus(framebuffer, context); 
    }
    
    // Cleanup
    public static void close() { logger.close(); }
    
    // Log suppression methods
    public static void traceSuppressed(String message) { LogSuppressor.traceSuppressed(message); }
    public static void debugSuppressed(String message) { LogSuppressor.debugSuppressed(message); }
    public static void infoSuppressed(String message) { LogSuppressor.infoSuppressed(message); }
    public static void warnSuppressed(String message) { LogSuppressor.warnSuppressed(message); }
    public static void errorSuppressed(String message) { LogSuppressor.errorSuppressed(message); }
    public static void fatalSuppressed(String message) { LogSuppressor.fatalSuppressed(message); }
    
    public static void traceSuppressed(String message, long suppressDurationMs) { LogSuppressor.traceSuppressed(message, suppressDurationMs); }
    public static void debugSuppressed(String message, long suppressDurationMs) { LogSuppressor.debugSuppressed(message, suppressDurationMs); }
    public static void infoSuppressed(String message, long suppressDurationMs) { LogSuppressor.infoSuppressed(message, suppressDurationMs); }
    public static void warnSuppressed(String message, long suppressDurationMs) { LogSuppressor.warnSuppressed(message, suppressDurationMs); }
    public static void errorSuppressed(String message, long suppressDurationMs) { LogSuppressor.errorSuppressed(message, suppressDurationMs); }
    public static void fatalSuppressed(String message, long suppressDurationMs) { LogSuppressor.fatalSuppressed(message, suppressDurationMs); }
    
    public static void forceLog(String message, Logger.LogLevel logLevel) { LogSuppressor.forceLog(message, logLevel); }
    public static void clearSuppression() { LogSuppressor.clearSuppression(); }
    public static int getSuppressedCount(String message) { return LogSuppressor.getSuppressedCount(message); }
    
    // Frame-based logging methods for per-frame operations
    public static void traceEveryNFrames(String message, int logEveryNFrames) { LogSuppressor.traceEveryNFrames(message, logEveryNFrames); }
    public static void debugEveryNFrames(String message, int logEveryNFrames) { LogSuppressor.debugEveryNFrames(message, logEveryNFrames); }
    public static void infoEveryNFrames(String message, int logEveryNFrames) { LogSuppressor.infoEveryNFrames(message, logEveryNFrames); }
    public static void warnEveryNFrames(String message, int logEveryNFrames) { LogSuppressor.warnEveryNFrames(message, logEveryNFrames); }
    public static void errorEveryNFrames(String message, int logEveryNFrames) { LogSuppressor.errorEveryNFrames(message, logEveryNFrames); }
    public static void fatalEveryNFrames(String message, int logEveryNFrames) { LogSuppressor.fatalEveryNFrames(message, logEveryNFrames); }
    
    public static void clearFrameCounters() { LogSuppressor.clearFrameCounters(); }
    public static int getFrameCount(String message) { return LogSuppressor.getFrameCount(message); }
} 