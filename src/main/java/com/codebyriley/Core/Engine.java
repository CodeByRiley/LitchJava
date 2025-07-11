package com.codebyriley.Core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static com.codebyriley.Core.Input.InputKeyboard.*;
import static com.codebyriley.Core.Input.InputMouse.*;
import static com.codebyriley.Util.AppdataPath.getAppdataPath;
import com.codebyriley.Core.Rendering.WindowBase;
import com.codebyriley.Core.Input.InputKeyboard;
import com.codebyriley.Core.Input.InputMouse;
import com.codebyriley.Core.Rendering.BatchedRenderer;
import com.codebyriley.Core.Rendering.UIRenderer;
import com.codebyriley.Core.Rendering.UI.UIManager;
import com.codebyriley.Core.Rendering.UI.Text.FontLoader;
import com.codebyriley.Core.Rendering.UI.Text.TextRenderer;
import com.codebyriley.Core.Scene.BasicScene;
import com.codebyriley.Core.Scene.SceneBase;
import com.codebyriley.Core.Scene.SceneManager;
import com.codebyriley.Core.Scene.SceneSerialisation;
import com.codebyriley.Core.Scene.SceneTransitions;
import com.codebyriley.Core.Scene.Entities.Entity;
import com.codebyriley.Util.Log;
import com.codebyriley.Core.Scene.SceneSerialisation.SceneWithUI;
import com.codebyriley.Core.Scene.SceneSerialisation.UIElementData;
import com.codebyriley.Core.Rendering.UI.UIElement;
import com.codebyriley.Core.Rendering.UI.UILayoutLoader;
import com.codebyriley.Core.Rendering.UI.UIActionHandler;
import com.codebyriley.Core.Rendering.UI.EngineUIActionHandler;
import com.codebyriley.Core.Scene.Entities.EntityBase;

public class Engine {

    private TextRenderer textRenderer;
    private FontLoader font;
    private FontLoader TypeLightSansFont;
    private UIActionHandler uiActionHandler;

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

    private int bgColorIndex = 0;
    private final float[][] bgColors = {
        {1.0f, 0.0f, 0.0f}, // Red
        {0.0f, 1.0f, 0.0f}, // Green
        {0.0f, 0.0f, 1.0f}  // Blue
    };

    //public BatchDemoScene batchDemoScene;
    //public RenderingDemoScene renderingDemoScene;
    //public UIDemoScene uiDemoScene;
    //public TextureDemoScene textureDemoScene;
    //public NonBatchDemoScene nonBatchDemoScene;

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
        uiActionHandler = new EngineUIActionHandler(this);
        
        // Validate renderers
        Log.info("Renderers initialized successfully");
        Log.checkGLErrorDetailed("Engine.Init", "renderer initialization");

        // Check saves directory and load scene
        if(!Files.exists(Paths.get(getAppdataPath() + "saves/"))) {
            Log.info("Saves directory doesn't exist, creating at: " + getAppdataPath() + "saves/");
            try {
                Files.createDirectories(Paths.get(getAppdataPath() + "saves/"));
            } catch (IOException e) {
                Log.error("Failed to create saves directory: " + e.getMessage());
            }
            baseScene = new BasicScene("TestScene");
        } else {
            Log.info("Saves directory already exists: " + getAppdataPath() + "saves/");
            Log.info("Attempting to load scene with UI");
            
            // Try to load the scene with UI first
            SceneWithUI loaded = null;
            try {
                loaded = SceneSerialisation.LoadSceneWithUI("TestScene");
            } catch (IOException e) {
                Log.error("Failed to load scene with UI: " + e.getMessage());
            }
            
            if (loaded != null && loaded.entities != null && !loaded.entities.isEmpty()) {
                // Create a new BasicScene and add the loaded entities
                baseScene = new BasicScene("TestScene");
                for (EntityBase entity : loaded.entities) {
                    // Convert EntityBase to Entity if needed
                    if (entity instanceof Entity) {
                        baseScene.AddEntity((Entity) entity);
                    } else {
                        // Create a new Entity with the same properties
                        Entity newEntity = new Entity(entity.mIsActive, entity.mIsVisible);
                        newEntity.mId = entity.mId;
                        newEntity.mName = entity.mName;
                        newEntity.mTransform = entity.mTransform;
                        newEntity.mChildren = entity.mChildren;
                        // Copy components
                        for (var comp : entity.GetComponents()) {
                            newEntity.AddComponent(comp);
                        }
                        baseScene.AddEntity(newEntity);
                    }
                }
                
                // Restore UI to UIManager
                if (loaded.ui != null) {
                    for (UIElementData elem : loaded.ui) {
                        UIElement uiElem = UILayoutLoader.createElement(elem, textRenderer, uiActionHandler);
                        if (uiElem != null) {
                            uiManager.addElement(uiElem);
                        }
                    }
                }
            } else {
                // Fallback to legacy scene loading
                try {
                    baseScene = SceneSerialisation.LoadScene("TestScene", BasicScene.class);
                } catch (IOException e) {
                    Log.error("Failed to load legacy scene: " + e.getMessage());
                    baseScene = new BasicScene("TestScene");
                }
            }
        }

        // Create demo scene to showcase batching
        //batchDemoScene = new BatchDemoScene(font);
        //renderingDemoScene = new RenderingDemoScene(sceneRenderer, uiRenderer, textRenderer);
        //textureDemoScene = new TextureDemoScene();
        //nonBatchDemoScene = new NonBatchDemoScene();
        //uiDemoScene = new UIDemoScene();
    

        // OpenGL debug output setup (requires OpenGL 4.3+)
        // glEnable(GL40.GL_DEBUG_OUTPUT_SYNCHRONOUS);

        // Init Scene Manager
        SceneManager.Init(sceneRenderer, uiManager, textRenderer);
        SceneManager.SetScene(baseScene);
        //SceneManager.SetScene(batchDemoScene);
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
        
        // Update input systems
        InputMouse.update();
        InputKeyboard.update();
        
        // Update UI manager with mouse events
        if (uiManager != null) {
            float mouseX = (float)InputMouse.GetMouseX();
            float mouseY = (float)InputMouse.GetMouseY();
            
            uiManager.onMouseMove(mouseX, mouseY);
            
            if (InputMouse.IsButtonJustPressed(InputMouse.MOUSE_BUTTON_LEFT)) {
                uiManager.onMousePress(mouseX, mouseY, InputMouse.MOUSE_BUTTON_LEFT);
            }
            if (InputMouse.IsButtonJustReleased(InputMouse.MOUSE_BUTTON_LEFT)) {
                uiManager.onMouseRelease(mouseX, mouseY, InputMouse.MOUSE_BUTTON_LEFT);
            }
            if (InputMouse.IsButtonJustPressed(InputMouse.MOUSE_BUTTON_RIGHT)) {
                uiManager.onMousePress(mouseX, mouseY, InputMouse.MOUSE_BUTTON_RIGHT);
            }
            if (InputMouse.IsButtonJustReleased(InputMouse.MOUSE_BUTTON_RIGHT)) {
                uiManager.onMouseRelease(mouseX, mouseY, InputMouse.MOUSE_BUTTON_RIGHT);
            }
        }
        
        glfwPollEvents();
        if(IsKeyPressed(GLFW.GLFW_KEY_ESCAPE)) {
            ShouldClose = true;
        }
        if(IsKeyJustPressed(GLFW.GLFW_KEY_B)) {
            // Defer scene creation to avoid OpenGL state issues
            Log.info("Switching to Batch Demo Scene");
            //pendingScene = batchDemoScene;
            pendingTransition = SceneTransitions.EASE_IN_OUT;
            pendingDuration = 0.25f;
        }
        if(IsKeyJustPressed(GLFW.GLFW_KEY_N)) {
            // Defer scene creation to avoid OpenGL state issues
            Log.info("Switching to Non-Batch Demo Scene");
            //pendingScene = nonBatchDemoScene;
            pendingTransition = SceneTransitions.EASE_IN_OUT;
            pendingDuration = 0.25f;
        }
        if(IsKeyJustPressed(GLFW.GLFW_KEY_T)) {
            // Defer scene creation to avoid OpenGL state issues
            Log.info("Switching to Texture Demo Scene");
            //pendingScene = textureDemoScene;
            pendingTransition = SceneTransitions.EASE_IN_OUT;
            pendingDuration = 0.25f;
        }
        if(IsKeyJustPressed(GLFW.GLFW_KEY_U)) {
            // Defer scene creation to avoid OpenGL state issues
            Log.info("Switching to UI Demo Scene");
            //pendingScene = uiDemoScene;
            pendingTransition = SceneTransitions.EASE_IN_OUT;
            pendingDuration = 0.25f;
        }
        if(IsKeyJustPressed(GLFW.GLFW_KEY_R)) {
            // Defer scene creation to avoid OpenGL state issues
            Log.info("Switching to Rendering Demo Scene");
            //pendingScene = renderingDemoScene;
            pendingTransition = SceneTransitions.EASE_IN_OUT;
            pendingDuration = 0.25f;
        }
        if(IsKeyJustPressed(GLFW.GLFW_KEY_S)) {
            // Defer scene creation to avoid OpenGL state issues
            Log.info("Saving scene");
            if(SceneManager.mCurrentScene != null) {
                // Convert SceneBase entities to EntityBase for saving
                List<EntityBase> entities = new java.util.ArrayList<>();
                for (Entity entity : SceneManager.mCurrentScene.entities) {
                    entities.add(entity);
                }
                saveCurrentSceneWithUI(entities);
            } else {
                Log.error("No scene to save");
            }
        }
        if(IsKeyJustPressed(GLFW.GLFW_KEY_L)) {
            // Defer scene creation to avoid OpenGL state issues
            Log.info("Loading scene");
            try {
                SceneSerialisation.LoadScene("TestScene", BasicScene.class);
            } catch (IOException e) {
                Log.error("Failed to load scene: " + e.getMessage());
            }
        }
    }

    private void processPendingScene() {
        if (pendingScene != null) {
            // Initialize the scene before changing to it to avoid OpenGL resource creation during rendering
            
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

    public void cycleBackgroundColor() {
        bgColorIndex = (bgColorIndex + 1) % bgColors.length;
        float[] color = bgColors[bgColorIndex];
        glClearColor(color[0], color[1], color[2], 1.0f);
    }

    // Example save method (call this to save the current scene+UI)
    public void saveCurrentSceneWithUI(List<com.codebyriley.Core.Scene.Entities.EntityBase> entities) {
        List<UIElementData> uiData = SceneSerialisation.uiElementsToData(uiManager.getElements());
        SceneWithUI sceneWithUI = new SceneWithUI();
        sceneWithUI.mName = "TestScene";
        sceneWithUI.entities = entities;
        sceneWithUI.ui = uiData;
        SceneSerialisation.SaveSceneWithUI(sceneWithUI);
    }
}

