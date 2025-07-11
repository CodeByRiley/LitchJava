package com.codebyriley.Core.Rendering.UI;

/**
 * Interface for handling different UI actions.
 * This allows for flexible action handling without hardcoding specific callbacks.
 */
public interface UIActionHandler {
    /**
     * Handle a UI action with the given type and parameter.
     * @param actionType The type of action to perform
     * @param actionParameter The parameter for the action
     * @return true if the action was handled successfully
     */
    boolean handleAction(String actionType, String actionParameter);
} 