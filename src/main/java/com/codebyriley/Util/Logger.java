package com.codebyriley.Util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Comprehensive logging system for the Litch game engine.
 * Replaces System.out.println and System.err.println with proper logging levels,
 * timestamps, and file output capabilities.
 */
public class Logger {
    
    public enum LogLevel {
        TRACE(0, "TRACE"),
        DEBUG(1, "DEBUG"),
        INFO(2, "INFO"),
        WARN(3, "WARN"),
        ERROR(4, "ERROR"),
        FATAL(5, "FATAL");
        
        private final int level;
        private final String name;
        
        LogLevel(int level, String name) {
            this.level = level;
            this.name = name;
        }
        
        public int getLevel() { return level; }
        public String getName() { return name; }
    }
    
    private static Logger instance;
    private static final ReentrantLock lock = new ReentrantLock();
    
    private LogLevel currentLevel = LogLevel.INFO;
    private boolean enableConsoleOutput = true;
    private boolean enableFileOutput = false;
    private String logFilePath = "logs/litch.log";
    private PrintWriter fileWriter;
    private final SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    
    private Logger() {
        initializeFileOutput();
    }
    
    public static Logger getInstance() {
        if (instance == null) {
            lock.lock();
            try {
                if (instance == null) {
                    instance = new Logger();
                }
            } finally {
                lock.unlock();
            }
        }
        return instance;
    }
    
    private void initializeFileOutput() {
        if (enableFileOutput) {
            try {
                File logFile = new File(logFilePath);
                logFile.getParentFile().mkdirs();
                fileWriter = new PrintWriter(new FileWriter(logFile, false), true);
            } catch (IOException e) {
                System.err.println("Failed to initialize log file: " + e.getMessage());
            }
        }
    }
    
    /**
     * Set the minimum log level to display
     */
    public void setLogLevel(LogLevel level) {
        this.currentLevel = level;
    }
    
    /**
     * Enable or disable console output
     */
    public void setConsoleOutput(boolean enable) {
        this.enableConsoleOutput = enable;
    }
    
    /**
     * Enable or disable file output
     */
    public void setFileOutput(boolean enable) {
        this.enableFileOutput = enable;
        if (enable && fileWriter == null) {
            initializeFileOutput();
        } else if (!enable && fileWriter != null) {
            fileWriter.close();
            fileWriter = null;
        }
    }
    
    /**
     * Set the log file path
     */
    public void setLogFilePath(String path) {
        this.logFilePath = path;
        if (enableFileOutput && fileWriter != null) {
            fileWriter.close();
            initializeFileOutput();
        }
    }
    
    private void log(LogLevel level, String message, Throwable throwable) {
        if (level.getLevel() < currentLevel.getLevel()) {
            return;
        }
        
        String timestamp = timestampFormat.format(new Date());
        String logMessage = String.format("[%s] [%s] %s", timestamp, level.getName(), message);
        
        lock.lock();
        try {
            if (enableConsoleOutput) {
                if (level == LogLevel.ERROR || level == LogLevel.FATAL) {
                    System.err.println(logMessage);
                    if (throwable != null) {
                        throwable.printStackTrace(System.err);
                    }
                } else {
                    System.out.println(logMessage);
                    if (throwable != null) {
                        throwable.printStackTrace(System.out);
                    }
                }
            }
            
            if (enableFileOutput && fileWriter != null) {
                fileWriter.println(logMessage);
                if (throwable != null) {
                    throwable.printStackTrace(fileWriter);
                }
            }
        } finally {
            lock.unlock();
        }
    }
    
    // Convenience methods for each log level
    public void trace(String message) { log(LogLevel.TRACE, message, null); }
    public void trace(String message, Throwable throwable) { log(LogLevel.TRACE, message, throwable); }
    
    public void debug(String message) { log(LogLevel.DEBUG, message, null); }
    public void debug(String message, Throwable throwable) { log(LogLevel.DEBUG, message, throwable); }
    
    public void info(String message) { log(LogLevel.INFO, message, null); }
    public void info(String message, Throwable throwable) { log(LogLevel.INFO, message, throwable); }
    
    public void warn(String message) { log(LogLevel.WARN, message, null); }
    public void warn(String message, Throwable throwable) { log(LogLevel.WARN, message, throwable); }
    
    public void error(String message) { log(LogLevel.ERROR, message, null); }
    public void error(String message, Throwable throwable) { log(LogLevel.ERROR, message, throwable); }
    
    public void fatal(String message) { log(LogLevel.FATAL, message, null); }
    public void fatal(String message, Throwable throwable) { log(LogLevel.FATAL, message, throwable); }
    
    /**
     * Close the logger and cleanup resources
     */
    public void close() {
        lock.lock();
        try {
            if (fileWriter != null) {
                fileWriter.close();
                fileWriter = null;
            }
        } finally {
            lock.unlock();
        }
    }
} 