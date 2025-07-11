package com.codebyriley.Core.Scene;

import java.util.ArrayList;

import com.codebyriley.Core.Rendering.BatchedRenderer;
import com.codebyriley.Core.Rendering.UI.Text.TextRenderer;
import com.codebyriley.Core.Scene.Entities.Entity;
import com.codebyriley.Util.Log;

public abstract class SceneBase {
    public String mName = "Scene";
    public ArrayList<Entity> entities = new ArrayList<>();

    public SceneBase(String name) {
        mName = name;
    }

    public void AddEntity(Entity entity) {
        if(!entities.contains(entity)) {
            entities.add(entity);
        } else {
            Log.warn("Entity already added: " + entity.mName);
        }
    }

    public Entity GetEntity(int id) {
        for(int i = 0; i < entities.size(); i++) {
            if(entities.get(i).mId == id) {
                return entities.get(i);
            }
        }
        return null;
    }

    public Entity GetEntity(String name) {
        for(int i = 0; i < entities.size(); i++) {
            if(entities.get(i).mName.equals(name)) {
                return entities.get(i);
            }
        }
        return null;
    }

    public ArrayList<Entity> GetEntities() {
        return entities;
    }

    public void RemoveEntity(Entity entity) {
        entities.remove(entity);
    }

    public void RemoveEntity(int id) {
        for(int i = 0; i < entities.size(); i++) {
            if(entities.get(i).mId == id) {
                entities.remove(i);
            }
        }
    }

    public void RemoveEntity(String name) {
        for(int i = 0; i < entities.size(); i++) {
            if(entities.get(i).mName.equals(name)) {
                entities.remove(i);
            }
        }
    }

    public void RemoveAllEntities() {
        entities.clear();
    }

    public abstract void Update(float dT);
    public abstract void FixedUpdate(float fixedDeltaTime);
    public abstract void Draw(BatchedRenderer renderer, TextRenderer textRenderer);
}
