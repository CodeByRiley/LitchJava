package com.codebyriley.Core.Scene.Entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.codebyriley.Core.Scene.Entities.Components.ComponentBase;
import com.codebyriley.Util.Math.Transform;

/**
 * Base class for all entities, similar to SceneBase for scenes.
 * Can be used directly for serialization/deserialization.
 */
public class EntityBase {
    public int mId = 0;
    public String mName = "EntityBase";
    public boolean mIsActive = true;
    public boolean mIsVisible = true;
    public Transform mTransform = new Transform();

    public transient EntityBase mParent;
    public ArrayList<EntityBase> mChildren = new ArrayList<>();

    private ArrayList<ComponentBase> mComponents = new ArrayList<>();
    private Map<String, ComponentBase> componentMap = new HashMap<>();

    // Default constructor for Gson
    public EntityBase() {
        mId = System.identityHashCode(this);
        mName = getClass().getSimpleName();
        mIsActive = true;
        mIsVisible = true;
    }

    // Constructor with isActive and isVisible
    public EntityBase(boolean isActive, boolean isVisible) {
        mId = System.identityHashCode(this);
        mName = getClass().getSimpleName();
        mIsActive = isActive;
        mIsVisible = isVisible;
    }

    // --- Child management ---
    public ArrayList<EntityBase> GetChildren() {
        return mChildren;
    }
    public void AddChild(EntityBase child) {
        mChildren.add(child);
    }
    public void RemoveChild(EntityBase child) {
        mChildren.remove(child);
    }
    public void RemoveAllChildren() {
        mChildren.clear();
    }
    public EntityBase GetChild(int inId) {
        for (EntityBase child : mChildren) {
            if (child.mId == inId) {
                return child;
            }
        }
        return null;
    }
    public EntityBase GetChild(String name) {
        for (EntityBase child : mChildren) {
            if (child.mName.equals(name)) {
                return child;
            }
        }
        return null;
    }

    // --- Component management ---
    public void AddComponent(ComponentBase component) {
        if (componentMap.containsKey(component.mName)) {
            return; // Component already added
        }
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
        return componentMap.get(name);
    }
    public ComponentBase GetComponent(int id) {
        for (ComponentBase comp : mComponents) {
            if (comp.mId == id) {
                return comp;
            }
        }
        return null;
    }
    public <T extends ComponentBase> T GetComponent(Class<T> componentClass) {
        for (ComponentBase comp : mComponents) {
            if (componentClass.isInstance(comp)) {
                return componentClass.cast(comp);
            }
        }
        return null;
    }
    public ArrayList<ComponentBase> GetComponents() {
        return mComponents;
    }

    // --- Parent management ---
    public void SetParent(EntityBase parent) {
        mParent = parent;
    }
    public void RemoveParent() {
        mParent = null;
    }

    // --- Utility ---
    public void SetActive(boolean isActive) {
        mIsActive = isActive;
    }
    public void SetVisible(boolean isVisible) {
        mIsVisible = isVisible;
    }
    public void Destroy() {
        RemoveAllComponents();
        RemoveParent();
    }

    // --- No-op update methods for compatibility ---
    public void Update(float dT) {}
    public void FixedUpdate(float fixedDeltaTime) {}
    //public void Draw(BatchedRenderer renderer) {}
}
