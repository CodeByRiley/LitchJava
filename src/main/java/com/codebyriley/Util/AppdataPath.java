package com.codebyriley.Util;

public class AppdataPath {
    public static String getAppdataPath() {
        return System.getenv("APPDATA") + "/.litch/";
    }
}
