package com.codebyriley.Core.Scene;

import com.codebyriley.Core.Scene.Entities.Components.ComponentBase;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

/**
 * Minimal local implementation of RuntimeTypeAdapterFactory for Gson polymorphic deserialization.
 * Source: https://github.com/google/gson/blob/master/extras/src/main/java/com/google/gson/typeadapters/RuntimeTypeAdapterFactory.java
 */
public class RuntimeTypeAdapterFactory<T> implements TypeAdapterFactory {
    private final Class<?> baseType;
    private final String typeFieldName;
    private final Map<String, Class<?>> labelToSubtype = new HashMap<>();
    private final Map<Class<?>, String> subtypeToLabel = new HashMap<>();

    private RuntimeTypeAdapterFactory(Class<?> baseType, String typeFieldName) {
        this.baseType = baseType;
        this.typeFieldName = typeFieldName;
    }

    public static <T> RuntimeTypeAdapterFactory<T> of(Class<T> baseType, String typeFieldName) {
        return new RuntimeTypeAdapterFactory<>(baseType, typeFieldName);
    }

    public RuntimeTypeAdapterFactory<T> registerSubtype(Class<? extends T> type, String label) {
        labelToSubtype.put(label, type);
        subtypeToLabel.put(type, label);
        return this;
    }

    public static RuntimeTypeAdapterFactory<ComponentBase> createComponentAdapter() {
        RuntimeTypeAdapterFactory<ComponentBase> factory =
            RuntimeTypeAdapterFactory.of(ComponentBase.class, "type");
    
        // Scan the package where your components are
        Reflections reflections = new Reflections("com.codebyriley.Core.Scene.Entities.Components");
        Set<Class<? extends ComponentBase>> subTypes = reflections.getSubTypesOf(ComponentBase.class);
    
        for (Class<? extends ComponentBase> clazz : subTypes) {
            factory.registerSubtype(clazz, clazz.getSimpleName());
        }
        return factory;
    }


    @Override
    public <R> TypeAdapter<R> create(Gson gson, TypeToken<R> type) {
        if (!baseType.isAssignableFrom(type.getRawType())) {
            return null;
        }
        final Map<String, TypeAdapter<?>> labelToDelegate = new HashMap<>();
        final Map<Class<?>, TypeAdapter<?>> subtypeToDelegate = new HashMap<>();
        for (Map.Entry<String, Class<?>> entry : labelToSubtype.entrySet()) {
            TypeAdapter<?> delegate = gson.getDelegateAdapter(this, TypeToken.get(entry.getValue()));
            labelToDelegate.put(entry.getKey(), delegate);
            subtypeToDelegate.put(entry.getValue(), delegate);
        }
        return new TypeAdapter<R>() {
            @Override
            public void write(JsonWriter out, R value) throws IOException {
                Class<?> srcType = value.getClass();
                String label = subtypeToLabel.get(srcType);
                if (label == null) {
                    throw new IllegalArgumentException("Cannot serialize " + srcType.getName() + "; did you forget to register a subtype?");
                }
                TypeAdapter<R> delegate = (TypeAdapter<R>) subtypeToDelegate.get(srcType);
                
                // Write the type field first
                out.beginObject();
                out.name(typeFieldName).value(label);
                
                // Use a custom approach to avoid nesting issues
                // We'll serialize the object to a JsonElement and then write its fields
                com.google.gson.JsonElement element = delegate.toJsonTree(value);
                if (element.isJsonObject()) {
                    com.google.gson.JsonObject obj = element.getAsJsonObject();
                    for (java.util.Map.Entry<String, com.google.gson.JsonElement> entry : obj.entrySet()) {
                        String fieldName = entry.getKey();
                        if (!typeFieldName.equals(fieldName)) {
                            out.name(fieldName);
                            writeJsonElement(out, entry.getValue());
                        }
                    }
                }
                
                out.endObject();
            }
            
            private void writeJsonElement(JsonWriter out, com.google.gson.JsonElement element) throws IOException {
                if (element.isJsonPrimitive()) {
                    com.google.gson.JsonPrimitive primitive = element.getAsJsonPrimitive();
                    if (primitive.isString()) {
                        out.value(primitive.getAsString());
                    } else if (primitive.isNumber()) {
                        out.value(primitive.getAsNumber());
                    } else if (primitive.isBoolean()) {
                        out.value(primitive.getAsBoolean());
                    }
                } else if (element.isJsonNull()) {
                    out.nullValue();
                } else if (element.isJsonArray()) {
                    out.beginArray();
                    for (com.google.gson.JsonElement item : element.getAsJsonArray()) {
                        writeJsonElement(out, item);
                    }
                    out.endArray();
                } else if (element.isJsonObject()) {
                    out.beginObject();
                    for (java.util.Map.Entry<String, com.google.gson.JsonElement> entry : element.getAsJsonObject().entrySet()) {
                        out.name(entry.getKey());
                        writeJsonElement(out, entry.getValue());
                    }
                    out.endObject();
                }
            }
            @Override
            public R read(JsonReader in) throws IOException {
                in.beginObject();
                String label = null;
                
                // First pass: find the type field and collect all fields
                java.util.Map<String, com.google.gson.JsonElement> fields = new java.util.HashMap<>();
                while (in.hasNext()) {
                    String fieldName = in.nextName();
                    if (typeFieldName.equals(fieldName)) {
                        label = in.nextString();
                    } else {
                        // Read the value as a JsonElement to preserve proper JSON structure
                        fields.put(fieldName, com.google.gson.internal.Streams.parse(in));
                    }
                }
                in.endObject();
                
                if (label == null) {
                    throw new IllegalStateException("Cannot deserialize, missing type field: " + typeFieldName);
                }
                
                Class<?> subtype = labelToSubtype.get(label);
                if (subtype == null) {
                    throw new IllegalArgumentException("Cannot deserialize subtype: " + label);
                }
                
                TypeAdapter<?> delegate = labelToDelegate.get(label);
                if (delegate == null) {
                    throw new IllegalArgumentException("No delegate found for type: " + label);
                }
                
                // Create a new JsonObject with the type field first
                com.google.gson.JsonObject jsonObject = new com.google.gson.JsonObject();
                jsonObject.addProperty(typeFieldName, label);
                
                // Add all other fields
                for (java.util.Map.Entry<String, com.google.gson.JsonElement> entry : fields.entrySet()) {
                    jsonObject.add(entry.getKey(), entry.getValue());
                }
                
                // Parse the reconstructed JSON with the delegate
                return (R) delegate.fromJsonTree(jsonObject);
            }
        };
    }
} 