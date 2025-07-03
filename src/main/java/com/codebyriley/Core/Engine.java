package com.codebyriley.Core;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static com.codebyriley.Core.Input.InputKeyboard.*;
import com.codebyriley.Core.Rendering.WindowBase;
import com.codebyriley.Core.Rendering.Primatives.Renderer;
import com.codebyriley.Core.Rendering.Text.FontLoader;
import com.codebyriley.Core.Rendering.Text.TextRenderer;
import com.codebyriley.Core.Scene.SceneBase;
import com.codebyriley.Core.Scene.SceneManager;
import com.codebyriley.Core.Scene.SceneTransitions;
import com.codebyriley.Core.Scene.BatchDemoScene;
import com.codebyriley.Core.Scene.NonBatchDemoScene;
import com.codebyriley.Core.Scene.TextureDemoScene;

public class Engine {

    private TextRenderer textRenderer;
    private FontLoader font;
    private FontLoader TypeLightSansFont;

    private Renderer renderer;
    private WindowBase window;
    public static boolean ShouldClose = false;

    private int lastFbWidth = -1;
    private int lastFbHeight = -1;  

    private int frames = 0;
    private double fps = 0.0f;
    private double lastFpsTime = 0.0f;

    private SceneBase baseScene;

    public static float deltaTime = 0.0f;
    public float lastFrame = 0.0f;

    private static final int FB_RECREATE_THRESHOLD = 10; // pixels

    public Engine() {

    }

    public void Init() {
        // Create Window
        window = new WindowBase("Litch", 1280, 720);

        // Set window close callback to ESCAPE
        glfwSetKeyCallback(WindowBase.windowHandle, (window, key, scancode, action, mods) -> {
            if(key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE) {
                GLFW.glfwSetWindowShouldClose(window, true);
            }
        });

        
        GLFW.glfwSetWindowSizeCallback(WindowBase.windowHandle, (window, WIDTH, HEIGHT) -> { 
            WindowBase.windowWidth = WIDTH;
            WindowBase.windowHeight = HEIGHT;
            textRenderer.setWindowSize(WIDTH, HEIGHT);
        });

        // Allows user to close window by clicking the X
        glfwSetWindowCloseCallback(WindowBase.windowHandle, (window) -> {
            Engine.ShouldClose = true;
        });
        GL.createCapabilities();

        // Init Fonts
        font = new FontLoader("fonts/DejaVuSerif.ttf", 48);
        
        // Create Renderers
        renderer = new Renderer();
        textRenderer = new TextRenderer(font, WindowBase.windowWidth, WindowBase.windowHeight);
        

        // Init Scenes
        baseScene = new SceneBase() {
            @Override
            public void Update(float dT) { /* ... */ }
            @Override
            public void FixedUpdate(float fixedDeltaTime) { /* ... */ }
            @Override
            public void Draw(Renderer renderer, TextRenderer textRenderer) { /* ... */ }
        };

        // Create demo scene to showcase batching
        BatchDemoScene batchDemoScene = new BatchDemoScene(font);

        // Init Scene Manager
        SceneManager.Init(renderer);
        SceneManager.SetScene(batchDemoScene);

        // Clear Window
        glClearColor(0.0f,0.0f,0.0f,0.0f);

        

    }

    public void Run() {
        Init();

        System.out.println("Video Card Vendor: " + glGetString(GL_VENDOR));
        System.out.println("Video Card: " + glGetString(GL_RENDERER));
        System.out.println("OpenGL Version: " + glGetString(GL_VERSION));
        System.out.println("Video Card Memory: " + glGetInteger(NVXGPUMemoryInfo.GL_GPU_MEMORY_INFO_CURRENT_AVAILABLE_VIDMEM_NVX));

        Update();
        Shutdown();
    }

    public void Update() {
        lastFpsTime = glfwGetTime();
        while(!ShouldClose) {
            float currentFrame = (float)glfwGetTime();
            deltaTime = (currentFrame - lastFrame) * 1000;
            lastFrame = currentFrame;

            frames++;
            double now = glfwGetTime();
            if (now - lastFpsTime >= 1.0) {
                fps = frames / (now - lastFpsTime);
                frames = 0;
                lastFpsTime = now;
            }
    
            SceneManager.Update(deltaTime);
            Draw();
            PollEvents();
        }
    }

    public void Draw() {
        // 1. Only recreate framebuffer if window size actually changed
        if (Math.abs(WindowBase.windowWidth - lastFbWidth) > FB_RECREATE_THRESHOLD || 
            Math.abs(WindowBase.windowHeight - lastFbHeight) > FB_RECREATE_THRESHOLD) {
            renderer.createFramebuffer(WindowBase.windowWidth, WindowBase.windowHeight);
            lastFbWidth = WindowBase.windowWidth;
            lastFbHeight = WindowBase.windowHeight;
        }

        // 2. Render to framebuffer
        renderer.bindFramebuffer();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Draw current scene (which uses batching internally)
        SceneManager.Draw(renderer, textRenderer);
        

        renderer.unbindFramebuffer();

        // 3. Draw the framebuffer texture to the screen
        renderer.DrawFramebufferToScreen();

        // 4. Draw FPS text (update only once per second)
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_DEPTH_TEST);
        textRenderer.drawText(String.valueOf((int)fps), 25, 75, 0.75f, 1.0f, 1.0f, 1.0f);
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);

        // 5. Draw overlays (e.g., fade) on top of the framebuffer
        float alpha = SceneManager.getCurrentFade();
        if (alpha > 0.0f) {
            renderer.DrawFullscreenQuad(0.25f, 0.25f, 0.25f, alpha);
        }

        // 6. Swap buffers
        glfwSwapBuffers(WindowBase.windowHandle);
    }

    public void PollEvents() {
        glfwPollEvents();
        if(IsKeyPressed(GLFW.GLFW_KEY_ESCAPE)) {
            ShouldClose = true;
        }
        if(IsKeyJustPressed(GLFW.GLFW_KEY_B)) {
            // Switch to batch demo scene
            BatchDemoScene batchDemoScene = new BatchDemoScene(font);
            SceneManager.ChangeScene(batchDemoScene, SceneTransitions.EASE_IN_OUT, 0.25f);
        }
        if(IsKeyJustPressed(GLFW.GLFW_KEY_N)) {
            // Switch to non-batch scene
            NonBatchDemoScene nonBatchScene = new NonBatchDemoScene();
            SceneManager.ChangeScene(nonBatchScene, SceneTransitions.EASE_IN_OUT, 0.25f);
        }
        if(IsKeyJustPressed(GLFW.GLFW_KEY_T)) {
            // Switch to texture demo scene
            TextureDemoScene textureDemoScene = new TextureDemoScene(font);
            SceneManager.ChangeScene(textureDemoScene, SceneTransitions.EASE_IN_OUT, 0.25f);
        }
    }



    public void Shutdown() {
        System.out.println("Shutting down...");
        window.Destroy();
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
    
}
