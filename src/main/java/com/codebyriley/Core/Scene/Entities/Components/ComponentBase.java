package com.codebyriley.Core.Scene.Entities.Components;

import com.codebyriley.Core.Scene.Entities.Entity;

public abstract class ComponentBase {
    public int mId = 0;

    public String mName = "ComponentBase";

    public boolean mIsActive = true;
    public boolean mIsVisible = true;

    public Entity mParent;

    public ComponentBase(String name) {
        mId = System.identityHashCode(this);
        mName = getClass().getSimpleName();
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public Entity getParent() {
        return mParent;
    }

    public void setParent(Entity parent) {
        this.mParent = parent;
    }
}
