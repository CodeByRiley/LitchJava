package com.codebyriley.Core.Scene;

import com.codebyriley.Core.Rendering.BatchedRenderer;
import com.codebyriley.Core.Rendering.UI.Text.TextRenderer;

public class BasicScene extends SceneBase {
    public BasicScene() {
        super("BasicScene");
    }
    public BasicScene(String name) {
        super(name);
    }
    @Override
    public void Update(float dT) {}
    @Override
    public void FixedUpdate(float fixedDeltaTime) {}
    @Override
    public void Draw(BatchedRenderer renderer, TextRenderer textRenderer) {
    }
}