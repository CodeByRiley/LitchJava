package com.codebyriley.Core.Scene;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.codebyriley.Util.Log;
import com.codebyriley.Core.Scene.Entities.EntityBase;
import com.codebyriley.Core.Scene.Entities.Components.ComponentBase;
import static com.codebyriley.Util.AppdataPath.getAppdataPath;
import static com.codebyriley.Core.Scene.RuntimeTypeAdapterFactory.createComponentAdapter;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class SceneSerialisation {
    // Register all concrete ComponentBase subclasses here
    private static final RuntimeTypeAdapterFactory<ComponentBase> componentAdapter = createComponentAdapter();

    private static final Gson gson = new GsonBuilder()
        .registerTypeAdapterFactory(componentAdapter)
        .setExclusionStrategies(new com.google.gson.ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(com.google.gson.FieldAttributes f) {
                // Skip transient fields and mParent field to avoid circular references
                return f.hasModifier(java.lang.reflect.Modifier.TRANSIENT) || 
                       f.getName().equals("mParent");
            }
            
            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        })
        .setPrettyPrinting()
        .create();
    private static String scenePath = getAppdataPath() + "saves/";

    // --- UI Layout Data Classes ---
    public static class UIElementData {
        public String type;
        public float x, y, width, height;
        public String label;
        public String text;
        public String onClickMessage;
        public String actionType; // New field for different action types
        public String actionParameter; // New field for action parameters
        public String customProperty;
        public List<UIElementData> children; // For nested UI
        // Add more fields as needed
    }
    public static class UILayout {
        public List<UIElementData> ui;
    }

    // --- Scene Wrapper for Entities and UI ---
    public static class SceneWithUI {
        public String mName;
        public List<EntityBase> entities;
        public List<UIElementData> ui;
    }

    // --- Utility: Convert UIElement <-> UIElementData ---
    public static List<UIElementData> uiElementsToData(List<com.codebyriley.Core.Rendering.UI.UIElement> elements) {
        List<UIElementData> dataList = new java.util.ArrayList<>();
        for (com.codebyriley.Core.Rendering.UI.UIElement elem : elements) {
            UIElementData data = new UIElementData();
            data.type = elem.getClass().getSimpleName();
            data.x = elem.getX();
            data.y = elem.getY();
            data.width = elem.getWidth();
            data.height = elem.getHeight();
            if (elem instanceof com.codebyriley.Core.Rendering.UI.Button) {
                data.label = ((com.codebyriley.Core.Rendering.UI.Button) elem).getText();
                data.onClickMessage = ((com.codebyriley.Core.Rendering.UI.Button) elem).getOnClickMessage();
                data.actionType = ((com.codebyriley.Core.Rendering.UI.Button) elem).getActionType();
                data.actionParameter = ((com.codebyriley.Core.Rendering.UI.Button) elem).getActionParameter();
            }
            if (elem instanceof com.codebyriley.Core.Rendering.UI.Label) {
                data.text = ((com.codebyriley.Core.Rendering.UI.Label) elem).getText();
            }
            // Add more fields for other UI types as needed
            dataList.add(data);
        }
        return dataList;
    }

    // Convert UIElementData list to UIElement list (for loading)
    public static List<com.codebyriley.Core.Rendering.UI.UIElement> dataToUIElements(
            List<UIElementData> dataList,
            com.codebyriley.Core.Rendering.UI.Text.TextRenderer textRenderer,
            com.codebyriley.Core.Rendering.UI.UIActionHandler actionHandler) {
        List<com.codebyriley.Core.Rendering.UI.UIElement> elements = new java.util.ArrayList<>();
        if (dataList == null) return elements;
        for (UIElementData data : dataList) {
            com.codebyriley.Core.Rendering.UI.UIElement elem = com.codebyriley.Core.Rendering.UI.UILayoutLoader.createElement(data, textRenderer, actionHandler);
            if (elem != null) elements.add(elem);
        }
        return elements;
    }

    public static void SaveSceneWithUI(SceneWithUI scene) {
        // Save the scene (entities + UI) to a file
        String json = gson.toJson(scene);
        String filePath = scenePath + scene.mName + ".json";
        try {
            if(json == null || json.isEmpty()) {
                Log.error("Failed to save scene: json is null or empty");
                return;
            }
            if(!Files.exists(Paths.get(scenePath))) {
                Log.info("Directory doesn't exist, creating at: " + scenePath);
                Files.createDirectories(Paths.get(scenePath));
            } else {
                Log.info("Directory already exists: " + scenePath);
            }
            Log.info("Writing to file: " + filePath);
            FileWriter fileWriter = new FileWriter(filePath);
            fileWriter.write(json);
            fileWriter.close();
        } catch (IOException e) { 
            Log.error("Failed to save scene: " + e.getMessage());
        }
    }

    public static SceneWithUI LoadSceneWithUI(String path) throws IOException {
        // Load the scene (entities + UI) from a file
        String json = new String(Files.readAllBytes(Paths.get(getAppdataPath() + "saves/" + path + ".json")));
        SceneWithUI scene = gson.fromJson(json, SceneWithUI.class);
        // Restore parent links and reload components for all entities
        if (scene != null && scene.entities != null) {
            for (EntityBase entity : scene.entities) {
                restoreParentLinks(entity, null);
                restoreComponentLinks(entity);
            }
            // Set custom parent relationships if needed
            setCustomParentRelationships(scene.entities);
        }
        return scene;
    }

    // --- Legacy methods for entity-only scenes (optional) ---
    public static void SaveScene(SceneBase scene) {
        String json = toJson(scene);
        String filePath = scenePath + scene.mName + ".json";
        try {
            if(json == null || json.isEmpty()) {
                Log.error("Failed to save scene: json is null or empty");
                return;
            }
            if(!Files.exists(Paths.get(scenePath))) {
                Log.info("Directory doesn't exist, creating at: " + scenePath);
                Files.createDirectories(Paths.get(scenePath));
            } else {
                Log.info("Directory already exists: " + scenePath);
            }
            Log.info("Writing to file: " + filePath);
            FileWriter fileWriter = new FileWriter(filePath);
            fileWriter.write(json);
            fileWriter.close();
        } catch (IOException e) { 
            Log.error("Failed to save scene: " + e.getMessage());
        }
    }

    public static <T extends SceneBase> T LoadScene(String path, Class<T> clazz) throws IOException {
        String json = new String(Files.readAllBytes(Paths.get(getAppdataPath() + "saves/" + path + ".json")));
        T scene = fromJson(json, clazz);
        if (scene != null) {
            for (EntityBase entity : scene.entities) {
                restoreParentLinks(entity, null);
                restoreComponentLinks(entity);
            }
        }
        return scene;
    }

    private static String toJson(SceneBase scene) {
        return gson.toJson(scene);
    }
    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    // Recursively restore parent links for all entities
    private static void restoreParentLinks(EntityBase entity, EntityBase parent) {
        entity.mParent = parent;
        for (EntityBase child : entity.GetChildren()) {
            restoreParentLinks(child, entity);
        }
    }

    // Set custom parent relationships based on entity names or IDs
    public static void setCustomParentRelationships(List<EntityBase> entities) {
        // Example: Set parent relationships based on entity names
        for (EntityBase entity : entities) {
            switch (entity.mName) {
                case "Weapon":
                    // Find the player entity and set it as parent
                    EntityBase player = findEntityByName(entities, "PlayerShip");
                    if (player != null) {
                        entity.mParent = player;
                        player.AddChild(entity);
                    }
                    break;
                case "Shield":
                    // Find the player entity and set it as parent
                    EntityBase playerForShield = findEntityByName(entities, "PlayerShip");
                    if (playerForShield != null) {
                        entity.mParent = playerForShield;
                        playerForShield.AddChild(entity);
                    }
                    break;
                // Add more cases as needed
            }
        }
    }
    
    // Helper method to find entity by name
    private static EntityBase findEntityByName(List<EntityBase> entities, String name) {
        for (EntityBase entity : entities) {
            if (entity.mName.equals(name)) {
                return entity;
            }
        }
        return null;
    }
    
    // Helper method to find entity by ID
    private static EntityBase findEntityById(List<EntityBase> entities, int id) {
        for (EntityBase entity : entities) {
            if (entity.mId == id) {
                return entity;
            }
        }
        return null;
    }

    // Recursively call OnDeserialize for all components
    private static void restoreComponentLinks(EntityBase entity) {
        for (var comp : entity.GetComponents()) {
            comp.OnDeserialize(gson);
        }
        for (EntityBase child : entity.GetChildren()) {
            restoreComponentLinks(child);
        }
    }
}