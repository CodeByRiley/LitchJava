package com.codebyriley.Core.Scene;

import com.codebyriley.Core.Rendering.BatchedRenderer;
import com.codebyriley.Core.Rendering.UIRenderer;
import com.codebyriley.Core.Rendering.Primitives.PrimitiveRenderer;
import com.codebyriley.Core.Rendering.UI.UIManager;
import com.codebyriley.Core.Rendering.UI.Button;
import com.codebyriley.Core.Rendering.UI.Label;
import com.codebyriley.Core.Rendering.UI.Panel;
import com.codebyriley.Core.Rendering.UI.Text.TextRenderer;
import com.codebyriley.Core.Rendering.UI.Text.FontLoader;
import com.codebyriley.Core.Rendering.Textures.TextureUtils;
import com.codebyriley.Util.Math.Vector2f;
import com.codebyriley.Util.Math.Vector3f;
import com.codebyriley.Util.Log;

/**
 * Demo scene showcasing all rendering capabilities:
 * - Primitive rendering (rectangles, circles, lines, etc.)
 * - UI rendering (buttons, labels, panels)
 * - Text rendering
 * - Batched rendering
 */
public class RenderingDemoScene extends SceneBase {
    private BatchedRenderer sceneRenderer;
    private UIManager uiManager;
    private PrimitiveRenderer primitiveRenderer;
    private TextRenderer textRenderer;
    private FontLoader fontLoader;
    
    private int whiteTextureId;
    private int checkerboardTextureId;
    private int gradientTextureId;
    
    private float rotation = 0.0f;
    private float time = 0.0f;
    private int clickCount = 0;
    
    private boolean isInitialized = false;
    private UIRenderer uiRenderer;
    
    public RenderingDemoScene(BatchedRenderer sceneRenderer, UIRenderer uiRenderer, TextRenderer textRenderer) {
        super();
        this.sceneRenderer = sceneRenderer;
        this.textRenderer = textRenderer;
        this.uiRenderer = uiRenderer;
        // Don't initialize OpenGL resources here - defer until after rendering
    }
    
    private void init() {
        if (isInitialized) return;
        
        Log.info("Initializing Rendering Demo Scene");
        
        // Use provided renderers instead of creating new ones
        uiManager = new UIManager(uiRenderer);
        
        // Create textures
        whiteTextureId = TextureUtils.createWhiteTexture();
        checkerboardTextureId = TextureUtils.createCheckerboardTexture(64, 8);
        gradientTextureId = TextureUtils.createGradientTexture(128, 128, 255, 0, 0, 0, 0, 255, true);
        
        // Create primitive renderer
        primitiveRenderer = new PrimitiveRenderer(sceneRenderer, whiteTextureId);
        
        // Load font if not provided
        if (this.textRenderer == null) {
            try {
                fontLoader = FontLoader.create("fonts/Tektur-Regular.ttf", 24, 96);
                this.textRenderer = new TextRenderer(uiRenderer, fontLoader);
            } catch (Exception e) {
                Log.error("Failed to load font: " + e.getMessage());
            }
        }
        
        // Create UI elements
        createUI();
        
        isInitialized = true;
        Log.info("Rendering Demo Scene initialized successfully");
    }
    
    private void createUI() {
        // Create main panel
        Panel mainPanel = new Panel(10, 10, 300, 400);
        mainPanel.setLayoutType(Panel.LayoutType.VERTICAL);
        mainPanel.setPadding(10);
        mainPanel.setSpacing(5);
        mainPanel.setId("mainPanel");
        
        // Create title label
        Label titleLabel = new Label(0, 0, "Rendering Demo", textRenderer);
        titleLabel.setTextColor(new Vector3f(1.0f, 1.0f, 0.0f));
        titleLabel.setTextScale(1.5f);
        titleLabel.setId("titleLabel");
        
        // Create info label
        Label infoLabel = new Label(0, 0, "Click the button below!", textRenderer);
        infoLabel.setTextColor(new Vector3f(0.8f, 0.8f, 0.8f));
        infoLabel.setId("infoLabel");
        
        // Create click counter label
        Label counterLabel = new Label(0, 0, "Clicks: 0", textRenderer);
        counterLabel.setTextColor(new Vector3f(0.0f, 1.0f, 0.0f));
        counterLabel.setId("counterLabel");
        
        // Create button
        Button demoButton = new Button(0, 0, 200, 40, "Click Me!", textRenderer);
        demoButton.setOnClick(button -> {
            clickCount++;
            counterLabel.setText("Clicks: " + clickCount);
            Log.info("Button clicked! Total clicks: " + clickCount);
        });
        demoButton.setId("demoButton");
        
        // Create status label
        Label statusLabel = new Label(0, 0, "Status: Ready", textRenderer);
        statusLabel.setTextColor(new Vector3f(0.0f, 0.8f, 1.0f));
        statusLabel.setId("statusLabel");
        
        // Add elements to panel
        mainPanel.addChild(titleLabel);
        mainPanel.addChild(infoLabel);
        mainPanel.addChild(counterLabel);
        mainPanel.addChild(demoButton);
        mainPanel.addChild(statusLabel);
        
        // Add panel to UI manager
        uiManager.addElement(mainPanel);
        
        // Create a second panel for additional controls
        Panel controlPanel = new Panel(320, 10, 250, 200);
        controlPanel.setLayoutType(Panel.LayoutType.VERTICAL);
        controlPanel.setPadding(10);
        controlPanel.setSpacing(5);
        controlPanel.setId("controlPanel");
        
        Label controlTitle = new Label(0, 0, "Controls", textRenderer);
        controlTitle.setTextColor(new Vector3f(1.0f, 0.5f, 0.0f));
        controlTitle.setId("controlTitle");
        
        Label instructionLabel = new Label(0, 0, "Move mouse to see hover effects", textRenderer);
        instructionLabel.setTextColor(new Vector3f(0.7f, 0.7f, 0.7f));
        instructionLabel.setId("instructionLabel");
        
        controlPanel.addChild(controlTitle);
        controlPanel.addChild(instructionLabel);
        
        uiManager.addElement(controlPanel);
    }
    
    @Override
    public void Update(float dT) {        
        time += dT;
        rotation += dT * 0.5f; // Rotate at 0.5 radians per second
        
        // Update UI
        if (uiManager != null) {
            uiManager.update();
            
            // Update status label
            Label statusLabel = (Label) uiManager.getElement("statusLabel");
            if (statusLabel != null) {
                statusLabel.setText("Status: Running - Time: " + String.format("%.1f", time));
            }
        }
    }
    
    @Override
    public void FixedUpdate(float fixedDeltaTime) {
        // Physics updates would go here
    }
    
    @Override
    public void Draw(BatchedRenderer renderer, TextRenderer textRenderer) {
        // Render scene elements (primitives, game objects, etc.)
        sceneRenderer.begin();
        
        // Draw rotating rectangle
        sceneRenderer.addQuad(
            400, 300, 100, 100,
            0.0f, 0.0f, 1.0f, 1.0f,
            1.0f, 0.5f, 0.0f, 1.0f,
            whiteTextureId
        );
        
        // Draw textured rectangle
        sceneRenderer.addQuad(
            550, 300, 100, 100,
            0.0f, 0.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            checkerboardTextureId
        );
        
        // Draw gradient rectangle
        sceneRenderer.addQuad(
            700, 300, 100, 100,
            0.0f, 0.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            gradientTextureId
        );
        
        sceneRenderer.end();
        
        // Render primitives
        renderPrimitives();
        
        // Render UI
        if (uiManager != null) {
            uiManager.render();
        }
        
        // Draw text overlay
        if (this.textRenderer != null) {
            this.textRenderer.drawText("Rendering Demo Scene", 175, 75, new Vector3f(1.0f, 1.0f, 1.0f), 0.75f);
            this.textRenderer.drawText("Press R to return to main menu", 175, 125, new Vector3f(1.0f, 1.0f, 1.0f), 0.5f);
        }
    }
    
    private void renderPrimitives() {
        if (primitiveRenderer == null) return;
        
        // Draw some primitive shapes
        primitiveRenderer.drawRect(100, 500, 80, 60, new Vector3f(1.0f, 0.0f, 0.0f), 0.8f);
        primitiveRenderer.drawRect(200, 500, 80, 60, new Vector3f(0.0f, 1.0f, 0.0f), 0.8f, rotation);
        primitiveRenderer.drawRect(300, 500, 80, 60, new Vector3f(0.0f, 0.0f, 1.0f), 0.8f);
        
        // Draw some lines
        primitiveRenderer.drawLine(100, 600, 200, 650, new Vector3f(1.0f, 1.0f, 0.0f), 0.9f, 2.0f);
        primitiveRenderer.drawLine(200, 600, 300, 650, new Vector3f(1.0f, 0.0f, 1.0f), 0.9f, 2.0f);
        
        // Draw some circles
        primitiveRenderer.drawCircle(400, 550, 30, new Vector3f(0.0f, 1.0f, 1.0f), 0.7f);
        primitiveRenderer.drawCircle(500, 550, 25, new Vector3f(1.0f, 0.5f, 0.0f), 0.8f);
    }
    
    public void handleInput(float mouseX, float mouseY, boolean mousePressed, int mouseButton) {
        if (uiManager != null) {
            if (mousePressed) {
                uiManager.onMousePress(mouseX, mouseY, mouseButton);
            } else {
                uiManager.onMouseRelease(mouseX, mouseY, mouseButton);
            }
            uiManager.onMouseMove(mouseX, mouseY);
        }
    }
    
    public void handleKeyInput(int key, int action, int mods) {
        if (uiManager != null) {
            if (action == 1) { // GLFW_PRESS
                uiManager.onKeyPress(key, mods);
            } else if (action == 0) { // GLFW_RELEASE
                uiManager.onKeyRelease(key, mods);
            }
        }
    }
    
    public void cleanup() {
        if (uiManager != null) {
            uiManager.clear();
        }
        
        // Clean up textures
        if (whiteTextureId != 0) {
            TextureUtils.deleteTexture(whiteTextureId);
            whiteTextureId = 0;
        }
        if (checkerboardTextureId != 0) {
            TextureUtils.deleteTexture(checkerboardTextureId);
            checkerboardTextureId = 0;
        }
        if (gradientTextureId != 0) {
            TextureUtils.deleteTexture(gradientTextureId);
            gradientTextureId = 0;
        }
    }
    
    public void forceInitialize() {
        init();
    }
} 