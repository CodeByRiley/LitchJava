package com.codebyriley.Util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import com.codebyriley.Util.Logger.LogLevel;

/**
 * Utility class to suppress duplicate log messages within a specified time window.
 * Useful for preventing log spam from repeated operations like rendering calls.
 */
public class LogSuppressor {
    
    private static final ConcurrentHashMap<String, Long> lastLogTimes = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Integer> suppressedCounts = new ConcurrentHashMap<>();
    
    /**
     * Suppress duplicate messages for a specified duration
     * @param message The message to log
     * @param suppressDurationMs Duration in milliseconds to suppress duplicates
     * @param logLevel The log level to use
     */
    public static void logSuppressed(String message, long suppressDurationMs, LogLevel logLevel) {
        long currentTime = System.currentTimeMillis();
        Long lastTime = lastLogTimes.get(message);
        
        if (lastTime == null || (currentTime - lastTime) >= suppressDurationMs) {
            // Log the message
            switch (logLevel) {
                case TRACE:
                    Log.trace(message);
                    break;
                case DEBUG:
                    Log.debug(message);
                    break;
                case INFO:
                    Log.info(message);
                    break;
                case WARN:
                    Log.warn(message);
                    break;
                case ERROR:
                    Log.error(message);
                    break;
                case FATAL:
                    Log.fatal(message);
                    break;
            }
            
            // Update last log time
            lastLogTimes.put(message, currentTime);
            
            // Check if we had suppressed messages and report them
            Integer suppressedCount = suppressedCounts.remove(message);
            if (suppressedCount != null && suppressedCount > 0) {
                Log.debug("Suppressed " + suppressedCount + " duplicate messages for: " + message);
            }
        } else {
            // Increment suppressed count
            suppressedCounts.compute(message, (key, count) -> count == null ? 1 : count + 1);
        }
    }
    
    /**
     * Suppress duplicate messages for 1 second
     * @param message The message to log
     * @param logLevel The log level to use
     */
    public static void logSuppressed(String message, LogLevel logLevel) {
        logSuppressed(message, 1000, logLevel);
    }
    
    /**
     * Suppress duplicate messages for 1 second at INFO level
     * @param message The message to log
     */
    public static void logSuppressed(String message) {
        logSuppressed(message, 1000, LogLevel.INFO);
    }
    
    /**
     * Suppress duplicate messages for a specified duration at INFO level
     * @param message The message to log
     * @param suppressDurationMs Duration in milliseconds to suppress duplicates
     */
    public static void logSuppressed(String message, long suppressDurationMs) {
        logSuppressed(message, suppressDurationMs, LogLevel.INFO);
    }
    
    /**
     * Convenience methods for different log levels with 1-second suppression
     */
    public static void traceSuppressed(String message) {
        logSuppressed(message, 1000, LogLevel.TRACE);
    }
    
    public static void debugSuppressed(String message) {
        logSuppressed(message, 1000, LogLevel.DEBUG);
    }
    
    public static void infoSuppressed(String message) {
        logSuppressed(message, 1000, LogLevel.INFO);
    }
    
    public static void warnSuppressed(String message) {
        logSuppressed(message, 1000, LogLevel.WARN);
    }
    
    public static void errorSuppressed(String message) {
        logSuppressed(message, 1000, LogLevel.ERROR);
    }
    
    public static void fatalSuppressed(String message) {
        logSuppressed(message, 1000, LogLevel.FATAL);
    }
    
    /**
     * Clear all suppression data (useful for testing or cleanup)
     */
    public static void clearSuppression() {
        lastLogTimes.clear();
        suppressedCounts.clear();
    }
    
    /**
     * Get the number of suppressed messages for a specific message
     * @param message The message to check
     * @return Number of suppressed messages
     */
    public static int getSuppressedCount(String message) {
        return suppressedCounts.getOrDefault(message, 0);
    }
    
    /**
     * Force log a message regardless of suppression (useful for important messages)
     * @param message The message to log
     * @param logLevel The log level to use
     */
    public static void forceLog(String message, LogLevel logLevel) {
        // Remove from suppression tracking
        lastLogTimes.remove(message);
        suppressedCounts.remove(message);
        
        // Log immediately
        switch (logLevel) {
            case TRACE:
                Log.trace(message);
                break;
            case DEBUG:
                Log.debug(message);
                break;
            case INFO:
                Log.info(message);
                break;
            case WARN:
                Log.warn(message);
                break;
            case ERROR:
                Log.error(message);
                break;
            case FATAL:
                Log.fatal(message);
                break;
        }
    }
    
    /**
     * Log with custom suppression duration for different log levels
     */
    public static void traceSuppressed(String message, long suppressDurationMs) {
        logSuppressed(message, suppressDurationMs, LogLevel.TRACE);
    }
    
    public static void debugSuppressed(String message, long suppressDurationMs) {
        logSuppressed(message, suppressDurationMs, LogLevel.DEBUG);
    }
    
    public static void infoSuppressed(String message, long suppressDurationMs) {
        logSuppressed(message, suppressDurationMs, LogLevel.INFO);
    }
    
    public static void warnSuppressed(String message, long suppressDurationMs) {
        logSuppressed(message, suppressDurationMs, LogLevel.WARN);
    }
    
    public static void errorSuppressed(String message, long suppressDurationMs) {
        logSuppressed(message, suppressDurationMs, LogLevel.ERROR);
    }
    
    public static void fatalSuppressed(String message, long suppressDurationMs) {
        logSuppressed(message, suppressDurationMs, LogLevel.FATAL);
    }
    
    // Frame-based logging methods for per-frame operations
    private static final ConcurrentHashMap<String, Integer> frameCounters = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Integer> frameSuppressedCounts = new ConcurrentHashMap<>();
    
    /**
     * Log only every N frames for per-frame operations
     * @param message The message to log
     * @param logLevel The log level to use
     * @param logEveryNFrames Log the message only every N frames (e.g., 60 = once per second at 60fps)
     */
    public static void logEveryNFrames(String message, LogLevel logLevel, int logEveryNFrames) {
        Integer currentCount = frameCounters.compute(message, (key, count) -> count == null ? 1 : count + 1);
        
        if (currentCount % logEveryNFrames == 0) {
            // Log the message
            switch (logLevel) {
                case TRACE:
                    Log.trace(message + " (frame " + currentCount + ")");
                    break;
                case DEBUG:
                    Log.debug(message + " (frame " + currentCount + ")");
                    break;
                case INFO:
                    Log.info(message + " (frame " + currentCount + ")");
                    break;
                case WARN:
                    Log.warn(message + " (frame " + currentCount + ")");
                    break;
                case ERROR:
                    Log.error(message + " (frame " + currentCount + ")");
                    break;
                case FATAL:
                    Log.fatal(message + " (frame " + currentCount + ")");
                    break;
            }
            
            // Check if we had suppressed frames and report them
            Integer suppressedCount = frameSuppressedCounts.remove(message);
            if (suppressedCount != null && suppressedCount > 0) {
                Log.debug("Suppressed " + suppressedCount + " frames for: " + message);
            }
        } else {
            // Increment suppressed count
            frameSuppressedCounts.compute(message, (key, count) -> count == null ? 1 : count + 1);
        }
    }
    
    /**
     * Convenience methods for frame-based logging at different levels
     */
    public static void traceEveryNFrames(String message, int logEveryNFrames) {
        logEveryNFrames(message, LogLevel.TRACE, logEveryNFrames);
    }
    
    public static void debugEveryNFrames(String message, int logEveryNFrames) {
        logEveryNFrames(message, LogLevel.DEBUG, logEveryNFrames);
    }
    
    public static void infoEveryNFrames(String message, int logEveryNFrames) {
        logEveryNFrames(message, LogLevel.INFO, logEveryNFrames);
    }
    
    public static void warnEveryNFrames(String message, int logEveryNFrames) {
        logEveryNFrames(message, LogLevel.WARN, logEveryNFrames);
    }
    
    public static void errorEveryNFrames(String message, int logEveryNFrames) {
        logEveryNFrames(message, LogLevel.ERROR, logEveryNFrames);
    }
    
    public static void fatalEveryNFrames(String message, int logEveryNFrames) {
        logEveryNFrames(message, LogLevel.FATAL, logEveryNFrames);
    }
    
    /**
     * Clear frame-based logging counters
     */
    public static void clearFrameCounters() {
        frameCounters.clear();
        frameSuppressedCounts.clear();
    }
    
    /**
     * Get the current frame count for a specific message
     * @param message The message to check
     * @return Current frame count
     */
    public static int getFrameCount(String message) {
        return frameCounters.getOrDefault(message, 0);
    }
} 