package com.codebyriley.Core.Scene.Entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.codebyriley.Core.Scene.Entities.Components.ComponentBase;
import com.codebyriley.Util.Math.Transform;

public abstract class Entity {
    public int mId = 0;
    public String mName = "EntityBase";
    public boolean mIsActive = true;
    public boolean mIsVisible = true;

    public Transform mTransform = new Transform();

    public Entity mParent;

    public ArrayList<Entity> mChildren = new ArrayList<>();
    public ArrayList<Entity> GetChildren() {
        return mChildren;
    }
    public void AddChild(Entity child) {
        mChildren.add(child);
    }
    public void RemoveChild(Entity child) {
        mChildren.remove(child);
    }
    public void RemoveAllChildren() {
        mChildren.clear();
    }
    public Entity GetChild(int inId) {
        for(int i = 0; i < mChildren.size(); i++) {
            if(mChildren.get(i).mId == inId) {
                return mChildren.get(i);
            }
        }
        return null;
    }
    public Entity GetChild(String name) {
        for(int i = 0; i < mChildren.size(); i++) {
            if(mChildren.get(i).mName.equals(name)) {
                return mChildren.get(i);
            }
        }
        return null;
    }

    private ArrayList<ComponentBase> mComponents = new ArrayList<>();
    private Map<String, ComponentBase> componentMap = new HashMap<>();

    public Entity(boolean isActive, boolean isVisible) {
        mId = System.identityHashCode(this);
        mName = getClass().getSimpleName();
        mIsActive = isActive;
        mIsVisible = isVisible;
    }

    public void SetActive(boolean isActive) {
        mIsActive = isActive;
    }
    public void SetVisible(boolean isVisible) {
        mIsVisible = isVisible;
    }

    public void AddComponent(ComponentBase component) {
        // Check if component already exists
        if (componentMap.containsKey(component.mName)) {
            return; // Component already added
        }
        
        // Add to both list and map for O(1) lookups
        mComponents.add(component);
        componentMap.put(component.mName, component);
        component.mParent = this;
    }

    public void RemoveComponent(ComponentBase component) {
        if (componentMap.remove(component.mName) != null) {
            mComponents.remove(component);
        }
    }

    public void RemoveAllComponents() {
        mComponents.clear();
        componentMap.clear();
    }

    public ComponentBase GetComponent(String name) {
        return componentMap.get(name); // O(1) lookup
    }
    public ComponentBase GetComponent(int id) {
        for(int i = 0; i < mComponents.size(); i++) {
            if(mComponents.get(i).mId == id) {
                return mComponents.get(i);
            }
        }
        return null;
    }

    // Generic version that works with any class that extends ComponentBase
    public <T extends ComponentBase> T GetComponent(Class<T> componentClass) {
        for(int i = 0; i < mComponents.size(); i++) {
            if(componentClass.isInstance(mComponents.get(i))) {
                return componentClass.cast(mComponents.get(i));
            }
        }
        return null;
    }

    public void SetParent(Entity parent) {
        mParent = parent;
    }
    public void RemoveParent() {
        mParent = null;
    }

    public ArrayList<ComponentBase> GetComponents() {
        return mComponents;
    }

    public void Destroy() {
        RemoveAllComponents();
        RemoveParent();
    }

    public abstract void Update();
    public abstract void FixedUpdate(float fixedDeltaTime);
    //public abstract void Draw(BatchedRenderer renderer);
}
