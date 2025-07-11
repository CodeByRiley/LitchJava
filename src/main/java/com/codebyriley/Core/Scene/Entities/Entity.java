package com.codebyriley.Core.Scene.Entities;

import com.codebyriley.Core.Rendering.BatchedRenderer;
import com.codebyriley.Core.Rendering.UI.Text.TextRenderer;

public class Entity extends EntityBase {
    public Entity(boolean isActive, boolean isVisible) {
        super(isActive, isVisible);
    }
    @Override
    public void Update(float dT) {}
    @Override
    public void FixedUpdate(float fixedDeltaTime) {}
}
