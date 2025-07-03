package com.codebyriley.Core.Scene;

import java.util.ArrayList;

import com.codebyriley.Core.Rendering.Primatives.Renderer;
import com.codebyriley.Core.Rendering.Text.TextRenderer;
import com.codebyriley.Core.Scene.Entities.Entity;

public abstract class SceneBase {
    public ArrayList<Entity> entities = new ArrayList<>();

    public SceneBase() {
    }

    public void AddEntity(Entity entity) {
        if(!entities.contains(entity)) {
            entities.add(entity);
        } else {
            System.out.println("Entity already added: " + entity.mName);
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
    public abstract void Draw(Renderer renderer, TextRenderer textRenderer);
}
