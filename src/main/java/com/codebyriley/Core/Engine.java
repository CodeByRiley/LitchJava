package com.codebyriley.Core;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static com.codebyriley.Core.Input.InputKeyboard.*;
import com.codebyriley.Core.Rendering.WindowBase;
import com.codebyriley.Core.Rendering.BatchedRenderer;
import com.codebyriley.Core.Rendering.UIRenderer;
import com.codebyriley.Core.Rendering.UI.UIManager;
import com.codebyriley.Core.Rendering.UI.Text.FontLoader;
import com.codebyriley.Core.Rendering.UI.Text.TextRenderer;
import com.codebyriley.Core.Scene.SceneBase;
import com.codebyriley.Core.Scene.SceneManager;
import com.codebyriley.Core.Scene.SceneTransitions;
import com.codebyriley.Core.Scene.BatchDemoScene;
import com.codebyriley.Core.Scene.NonBatchDemoScene;
import com.codebyriley.Core.Scene.TextureDemoScene;
import com.codebyriley.Core.Scene.UIDemoScene;
import com.codebyriley.Core.Scene.RenderingDemoScene;
import com.codebyriley.Util.Log;

public class Engine {

    private TextRenderer textRenderer;
    private FontLoader font;
    private FontLoader TypeLightSansFont;

    private BatchedRenderer sceneRenderer;
    private UIRenderer uiRenderer;
    private UIManager uiManager;
    private WindowBase window;
    public static boolean ShouldClose = false;

    private int frames = 0;
    private double fps = 0.0f;
    private double lastFpsTime = 0.0f;

    private SceneBase baseScene;
    private SceneBase pendingScene = null;
    private SceneTransitions pendingTransition = SceneTransitions.EASE_IN_OUT;
    private float pendingDuration = 0.25f;

    public BatchDemoScene batchDemoScene;
    public RenderingDemoScene renderingDemoScene;
    public UIDemoScene uiDemoScene;
    public TextureDemoScene textureDemoScene;
    public NonBatchDemoScene nonBatchDemoScene;

    public static float deltaTime = 0.0f;
    public float lastFrame = 0.0f;

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

        // Set framebuffer size callback to handle window resizing properly
        GLFW.glfwSetFramebufferSizeCallback(WindowBase.windowHandle, (window, width, height) -> {
            // Update window dimensions
            WindowBase.windowWidth = width;
            WindowBase.windowHeight = height;
            
            // Update OpenGL viewport to match the new framebuffer size
            glViewport(0, 0, width, height);
            Log.info("Framebuffer resized to: " + width + "x" + height);
        });

        // Allows user to close window by clicking the X
        glfwSetWindowCloseCallback(WindowBase.windowHandle, (window) -> {
            Engine.ShouldClose = true;
        });
        GL.createCapabilities();

        // Initialize the viewport to match the window size
        glViewport(0, 0, WindowBase.windowWidth, WindowBase.windowHeight);
        Log.info("Initial viewport set to: " + WindowBase.windowWidth + "x" + WindowBase.windowHeight);

        // Init Fonts
        font = FontLoader.create("fonts/Tektur-Bold.ttf", 48, 96);
        
        // Create Renderers
        sceneRenderer = new BatchedRenderer();
        if (sceneRenderer == null) {
            Log.error("Failed to create BatchedRenderer");
            throw new RuntimeException("Failed to create BatchedRenderer");
        }
        
        uiRenderer = new UIRenderer();
        if (uiRenderer == null) {
            Log.error("Failed to create UIRenderer");
            throw new RuntimeException("Failed to create UIRenderer");
        }
        
        uiManager = new UIManager(uiRenderer);
        textRenderer = new TextRenderer(uiRenderer, font);
        
        // Validate renderers
        Log.info("Renderers initialized successfully");
        Log.checkGLErrorDetailed("Engine.Init", "renderer initialization");

        // Init Scenes
        baseScene = new SceneBase() {
            @Override
            public void Update(float dT) { /* ... */ }
            @Override
            public void FixedUpdate(float fixedDeltaTime) { /* ... */ }
            @Override
            public void Draw(BatchedRenderer renderer, TextRenderer textRenderer) { /* ... */ }
        };

        // Create demo scene to showcase batching
        batchDemoScene = new BatchDemoScene(font);
        renderingDemoScene = new RenderingDemoScene(sceneRenderer, uiRenderer, textRenderer);
        textureDemoScene = new TextureDemoScene();
        nonBatchDemoScene = new NonBatchDemoScene();
        uiDemoScene = new UIDemoScene(uiRenderer, textRenderer);
    

        // OpenGL debug output setup (requires OpenGL 4.3+)
        // glEnable(GL40.GL_DEBUG_OUTPUT_SYNCHRONOUS);

        // Init Scene Manager
        SceneManager.Init(sceneRenderer, uiManager);
        SceneManager.SetScene(batchDemoScene);
        glfwSwapInterval(1);
        // Clear Window
        glClearColor(0.0f,0.0f,0.0f,0.0f);

        

    }

    public void Run() {
        Init();

        Log.logGLInfo();
        Log.logAllGLErrors();
        
        // Try to get GPU memory info (NVIDIA extension)
        try {
            Log.info("Video Card Memory: " + glGetInteger(org.lwjgl.opengl.NVXGPUMemoryInfo.GL_GPU_MEMORY_INFO_CURRENT_AVAILABLE_VIDMEM_NVX));
        } catch (Exception e) {
            Log.warn("Could not get GPU memory info (NVIDIA extension not available)");
        }
        
        Log.info("Video Card Vendor: " + glGetString(GL_VENDOR));
        Log.info("Video Card: " + glGetString(GL_RENDERER));
        Log.info("OpenGL Version: " + glGetString(GL_VERSION));
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
            
            // Process any pending scene changes after rendering is complete
            processPendingScene();
        }
    }

    public void Draw() {
        // Clear the screen
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        Log.checkGLErrorDetailed("Engine.Draw", "glClear");

        // Draw current scene (which uses batching internally)
        SceneManager.Draw(sceneRenderer, textRenderer);
        Log.checkGLErrorDetailed("Engine.Draw", "SceneManager.Draw");
        
        // 3. Draw FPS text
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_DEPTH_TEST);
        textRenderer.drawText(String.valueOf((int)fps), 25, 75, new com.codebyriley.Util.Math.Vector3f(1.0f, 1.0f, 1.0f), 1.0f);
        Log.checkGLErrorDetailed("Engine.Draw", "textRenderer.drawText");
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);

        uiManager.render();
        
        Log.checkGLErrorDetailed("Engine.Draw", "uiManager.render");

        // 4. Draw overlays (e.g., fade) on top
        float alpha = SceneManager.getCurrentFade();
        if (alpha > 0.0f) {
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glDisable(GL_DEPTH_TEST);

            sceneRenderer.begin();
            sceneRenderer.addQuad(
                WindowBase.windowWidth / 2.0f, WindowBase.windowHeight / 2.0f,
                WindowBase.windowWidth, WindowBase.windowHeight,
                0.0f, 0.0f, 1.0f, 1.0f,
                0.25f, 0.25f, 0.25f, alpha,
                0 // White texture
            );
            sceneRenderer.end();

            glEnable(GL_DEPTH_TEST);
            glDisable(GL_BLEND);

            Log.checkGLErrorDetailed("Engine.Draw", "sceneRenderer fade overlay");
        }

        // 5. Swap buffers
        glfwSwapBuffers(WindowBase.windowHandle);
        Log.checkGLErrorDetailed("Engine.Draw", "glfwSwapBuffers");
    }

    public void PollEvents() {
        Log.checkGLErrorAuto();
        
        glfwPollEvents();
        if(IsKeyPressed(GLFW.GLFW_KEY_ESCAPE)) {
            ShouldClose = true;
        }
        if(IsKeyJustPressed(GLFW.GLFW_KEY_B)) {
            // Defer scene creation to avoid OpenGL state issues
            Log.info("Switching to Batch Demo Scene");
            pendingScene = batchDemoScene;
            pendingTransition = SceneTransitions.EASE_IN_OUT;
            pendingDuration = 0.25f;
        }
        if(IsKeyJustPressed(GLFW.GLFW_KEY_N)) {
            // Defer scene creation to avoid OpenGL state issues
            Log.info("Switching to Non-Batch Demo Scene");
            pendingScene = nonBatchDemoScene;
            pendingTransition = SceneTransitions.EASE_IN_OUT;
            pendingDuration = 0.25f;
        }
        if(IsKeyJustPressed(GLFW.GLFW_KEY_T)) {
            // Defer scene creation to avoid OpenGL state issues
            Log.info("Switching to Texture Demo Scene");
            pendingScene = textureDemoScene;
            pendingTransition = SceneTransitions.EASE_IN_OUT;
            pendingDuration = 0.25f;
        }
        if(IsKeyJustPressed(GLFW.GLFW_KEY_U)) {
            // Defer scene creation to avoid OpenGL state issues
            Log.info("Switching to UI Demo Scene");
            pendingScene = uiDemoScene;
            pendingTransition = SceneTransitions.EASE_IN_OUT;
            pendingDuration = 0.25f;
        }
        if(IsKeyJustPressed(GLFW.GLFW_KEY_R)) {
            // Defer scene creation to avoid OpenGL state issues
            Log.info("Switching to Rendering Demo Scene");
            pendingScene = renderingDemoScene;
            pendingTransition = SceneTransitions.EASE_IN_OUT;
            pendingDuration = 0.25f;
        }
    }

    private void processPendingScene() {
        if (pendingScene != null) {
            // Initialize the scene before changing to it to avoid OpenGL resource creation during rendering
            if (pendingScene instanceof RenderingDemoScene) {
                ((RenderingDemoScene) pendingScene).forceInitialize();
            } else if (pendingScene instanceof UIDemoScene) {
                ((UIDemoScene) pendingScene).forceInitialize();
            } else if (pendingScene instanceof TextureDemoScene) {
                ((TextureDemoScene) pendingScene).forceInitialize();
            } else if (pendingScene instanceof BatchDemoScene) {
                ((BatchDemoScene) pendingScene).forceInitialize();
            }
            
            SceneManager.ChangeScene(pendingScene, pendingTransition, pendingDuration);
            pendingScene = null;
        }
    }

    public void Shutdown() {
        // Clean up renderers
        if (sceneRenderer != null) {
            sceneRenderer.destroy();
        }
        if (uiRenderer != null) {
            uiRenderer.destroy();
        }
        if (uiManager != null) {
            uiManager.clear();
        }
        
        // Clean up window
        if (window != null) {
        window.Destroy();
        }
    }
}

