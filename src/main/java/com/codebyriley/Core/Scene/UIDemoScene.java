package com.codebyriley.Core.Scene;

import static org.lwjgl.opengl.GL11.*;

import com.codebyriley.Core.Input.InputMouse;
import com.codebyriley.Core.Rendering.BatchedRenderer;
import com.codebyriley.Core.Rendering.UIRenderer;
import com.codebyriley.Core.Rendering.UI.UIManager;
import com.codebyriley.Core.Rendering.UI.Button;
import com.codebyriley.Core.Rendering.UI.Label;
import com.codebyriley.Core.Rendering.UI.Panel;
import com.codebyriley.Core.Rendering.UI.UIElement;
import com.codebyriley.Core.Rendering.UI.Text.TextRenderer;
import com.codebyriley.Core.Rendering.Textures.Texture;
import com.codebyriley.Core.Rendering.Textures.TextureLoader;
import com.codebyriley.Core.Rendering.WindowBase;
import com.codebyriley.Util.Math.Vector3f;
import com.codebyriley.Util.Log;
import com.codebyriley.Core.Rendering.UI.Text.FontLoader;

public class UIDemoScene extends SceneBase {
    private UIManager uiManager;
    private TextRenderer textRenderer;
    private Texture buttonTexture;
    private Texture iconTexture;
    private float animationTime = 0.0f;
    private FontLoader fontLoader;
    
    private boolean isInitialized = false;
    private UIRenderer uiRenderer;
    
    public UIDemoScene(UIRenderer uiRenderer, TextRenderer textRenderer) {
        super();
        this.textRenderer = textRenderer;
        this.uiRenderer = uiRenderer;
        // Don't initialize OpenGL resources here - defer until after rendering
    }
    
    private void init() {
        if (isInitialized) return;
        
        // Initialize UI system with provided renderer
        uiManager = new UIManager(uiRenderer);
        
        // Load font if not provided
        if (this.textRenderer == null) {
            try {
                fontLoader = FontLoader.create("fonts/DejaVuSerif.ttf", 24, 96);
                this.textRenderer = new TextRenderer(uiRenderer, fontLoader);
            } catch (Exception e) {
                Log.error("Failed to load font for UI Demo Scene: " + e.getMessage());
                // Create a fallback text renderer with null font (will be handled gracefully)
                this.textRenderer = new TextRenderer(uiRenderer, fontLoader);
            }
        }
        
        // Load some textures for demonstration
        try {
            buttonTexture = TextureLoader.LoadTexture("textures/ui/icon_plusLarge.png");
            iconTexture = TextureLoader.LoadTexture("textures/ui/icon_crossLarge.png");
        } catch (Exception e) {
            Log.warn("Could not load UI textures: " + e.getMessage(), e);
        }
        
        // Create UI elements only if text renderer is available
        if (textRenderer != null) {
            createUI();
        }
        
        isInitialized = true;
    }
    
    private void createUI() {
        // Create a panel for the demo
        Panel mainPanel = new Panel(50, 50, 600, 400);
        uiManager.addElement(mainPanel);
        
        // Create some buttons
        Button demoButton1 = new Button(700, 200, 275, 40, "Demo Button 1", textRenderer);
        Button demoButton2 = new Button(300, 500, 275, 40, "Demo Button 2", textRenderer);
        demoButton1.setOnClick(btn -> Log.info("Demo Button 1 clicked"));
        demoButton1.onMouseClick((float)InputMouse.GetMouseX(), (float)InputMouse.GetMouseY(), InputMouse.MOUSE_BUTTON_LEFT);
        demoButton2.setOnClick(btn -> Log.info("Demo Button 2 clicked"));
        uiManager.addElement(demoButton1);
        uiManager.addElement(demoButton2);        
        // Create some labels
        //Label titleLabel = new Label(100, 50, "X", textRenderer);
        Label infoLabel = new Label(500, 220, "T", textRenderer);
        uiManager.addElement(infoLabel);
    }
    
    @Override
    public void Update(float dT) {
        // Initialize on first update if not already done
        if (!isInitialized) {
            init();
        }
        
        animationTime += dT;
        if (uiManager != null) {
            uiManager.update();
        }
    }
    
    @Override
    public void FixedUpdate(float fixedDeltaTime) {
        // Fixed update logic here
    }
    
    @Override
    public void Draw(BatchedRenderer renderer, TextRenderer textRenderer) {
        // Initialize on first draw if not already done
        if (!isInitialized) {
            init();
        }
        
        Log.infoSuppressed("UIDemoScene.Draw called", 2000);
        
        // Clear the screen with a dark background
        // renderer.begin();
        // renderer.addQuad(
        //     WindowBase.windowWidth / 2.0f, WindowBase.windowHeight / 2.0f,
        //     WindowBase.windowWidth, WindowBase.windowHeight,
        //     0.0f, 0.0f, 1.0f, 1.0f,
        //     0.1f, 0.1f, 0.15f, 1.0f,
        //     0 // White texture
        // );
        // renderer.end();

        // Draw UI elements
        if (uiManager != null) {
            Log.infoSuppressed("UIDemoScene.uiManager.Render called", 2000);
            uiManager.render();
        }
        
        // Draw some additional demo content
        //drawAdditionalDemoContent(renderer);
        
        // Draw text overlay
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_DEPTH_TEST);
        


        if (this.textRenderer != null) {
            this.textRenderer.drawText("UI Demo Scene", 175, 75, new Vector3f(1.0f, 1.0f, 1.0f), 0.75f);
            this.textRenderer.drawText("Press U to return to main menu", 175, 125, new Vector3f(1.0f, 1.0f, 1.0f), 0.5f);
        }
        
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);


        //this.render();
        //uiManager.render();
    }

    // public void render() {
    //     if (uiManager == null) return;
    //     uiRenderer.begin();
    //     for (UIElement element : uiManager.getElements()) {
    //         if (element.isVisible()) {
    //             element.render(uiRenderer);
    //         }
    //     }
    //     uiRenderer.end();
    // }

    
    // private void drawAdditionalDemoContent(BatchedRenderer renderer) {
    //     renderer.begin();
        
    //     // Draw some animated elements
    //     float pulse = (float)(Math.sin(animationTime * 2) * 0.2 + 0.8);
    //     float moveX = 50 + (float)(Math.sin(animationTime) * 80);
        
    //     // Animated rectangle
    //     renderer.addQuad(
    //         moveX, 300, 60, 30,
    //         0.0f, 0.0f, 1.0f, 1.0f,
    //         0.5f, 0.5f, 1.0f, 0.8f,
    //         0
    //     );
        
    //     // Pulsing circle (approximated with a quad)
    //     float size = 25 * pulse;
    //     renderer.addQuad(
    //         400, 300, size, size,
    //         0.0f, 0.0f, 1.0f, 1.0f,
    //         1.0f, 0.5f, 0.0f, pulse,
    //         0
    //     );
        
    //     renderer.end();
    // }
    
    public void Shutdown() {
        if (uiManager != null) {
            uiManager.clear();
        }
        
        // Clean up textures
        if (buttonTexture != null) {
            buttonTexture.delete();
            buttonTexture = null;
        }
        if (iconTexture != null) {
            iconTexture.delete();
            iconTexture = null;
        }
    }
    
    public void forceInitialize() {
        init();
    }
} 