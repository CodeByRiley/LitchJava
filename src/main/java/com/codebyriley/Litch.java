package com.codebyriley;

import com.codebyriley.Core.Engine;
import com.codebyriley.Util.Log;
import com.codebyriley.Util.Logger;

public class Litch {
    public static void main(String... args) {
        // Configure logging
        Log.setLogLevel(Logger.LogLevel.TRACE);
        Log.setFileOutput(true);
        Log.setLogFilePath("logs/litch.log");
        
        Log.info("Starting Litch game engine...");
        System.setProperty("org.lwjgl.opengl.Display.allowSoftwareOpenGL", "false");
        
        try {
            new Engine().Run();
        } catch (Exception e) {
            Log.fatal("Engine crashed", e);
            throw e;
        } finally {
            Log.info("Litch game engine shutdown complete");
            Log.close();
        }
    }
}
