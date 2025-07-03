package com.codebyriley.Core.Scene;


public class SceneEasings {
    // Linear (for reference)
    public static float linear(float t) {
        return t;
    }

    // Sine
    public static float easeInSine(float t) {
        return (float)(1 - Math.cos((t * Math.PI) / 2));
    }
    public static float easeOutSine(float t) {
        return (float)(Math.sin((t * Math.PI) / 2));
    }
    public static float easeInOutSine(float t) {
        return (float)(-(Math.cos(Math.PI * t) - 1) / 2);
    }

    // Exponential
    public static float easeInExpo(float t) {
        return t == 0 ? 0 : (float)Math.pow(2, 10 * (t - 1));
    }
    public static float easeOutExpo(float t) {
        return t == 1 ? 1 : (float)(1 - Math.pow(2, -10 * t));
    }
    public static float easeInOutExpo(float t) {
        if (t == 0) return 0;
        if (t == 1) return 1;
        if (t < 0.5) return (float)(Math.pow(2, 20 * t - 10) / 2);
        return (float)((2 - Math.pow(2, -20 * t + 10)) / 2);
    }

    // Quad
    public static float easeInQuad(float t) {
        return t * t;
    }
    public static float easeOutQuad(float t) {
        return t * (2 - t);
    }
    public static float easeInOutQuad(float t) {
        return t < 0.5 ? 2 * t * t : (float)(-1 + (4 - 2 * t) * t);
    }

    // You can add more from https://easings.net/ as needed!
}