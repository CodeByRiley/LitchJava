package com.codebyriley.Core.Rendering.UI;

import com.codebyriley.Core.Scene.SceneSerialisation.UIElementData;
import com.codebyriley.Core.Rendering.UI.Text.TextRenderer;

public class UILayoutLoader {
    /**
     * Create a UIElement from UIElementData. Supports Button and Label.
     * @param data The UI element data from JSON
     * @param textRenderer The text renderer to use
     * @param actionHandler The action handler for button clicks (can be null)
     * @return The created UIElement, or null if type is unknown
     */
    public static UIElement createElement(UIElementData data, TextRenderer textRenderer, UIActionHandler actionHandler) {
        try {
            if ("Button".equals(data.type)) {
                Button button = new Button(data.x, data.y, data.width, data.height, data.label, textRenderer);
                if (data.actionType != null && !data.actionType.isEmpty()) {
                    button.setOnClick(b -> {
                        System.out.println(data.onClickMessage != null ? data.onClickMessage : "Button clicked");
                        if (actionHandler != null) {
                            actionHandler.handleAction(data.actionType, data.actionParameter != null ? data.actionParameter : "");
                        }
                    }, data.onClickMessage != null ? data.onClickMessage : "", data.actionType, data.actionParameter != null ? data.actionParameter : "");
                } else if (data.onClickMessage != null) {
                    // Legacy support for old format
                    button.setOnClick(b -> {
                        System.out.println(data.onClickMessage);
                    }, data.onClickMessage);
                }
                return button;
            } else if ("Label".equals(data.type)) {
                return new Label(data.x, data.y, data.text, textRenderer);
            }
            // Add more UI types as needed
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
} 