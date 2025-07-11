package com.codebyriley.Core.Scene.Entities.Components;

import com.codebyriley.Core.Scene.Entities.Entity;
import com.codebyriley.Core.Scene.Entities.EntityBase;
import com.google.gson.Gson;

public abstract class ComponentBase {
    public int mId = 0;

    public String mName = "ComponentBase";

    public boolean mIsActive = true;
    public boolean mIsVisible = true;

    public EntityBase mParent;

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

    public EntityBase getParent() {
        return mParent;
    }

    public void setParent(EntityBase parent) {
        this.mParent = parent;
    }

    public abstract void OnDeserialize(Gson gson);
}
