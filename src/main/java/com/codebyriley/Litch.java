package com.codebyriley;

import com.codebyriley.Core.Engine;

public class Litch {
    public static void main(String... args) {
        System.out.println("Hello, World!");
        System.setProperty("org.lwjgl.opengl.Display.allowSoftwareOpenGL", "false");
        
        new Engine().Run();
    }
}
