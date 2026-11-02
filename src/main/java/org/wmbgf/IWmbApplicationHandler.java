package org.wmbgf;

import org.wmbgf.input.ClickAction;
import org.wmbgf.input.KeyButton;

/**
 * The interface for interactions between the framework and underlying application.
 */
public interface IWmbApplicationHandler {

    /**
     * Called by the framework when the application can initialize itself.
     */
    void onInit();

    /**
     * Called on every frame, might be impacted by V-sync.
     *
     * @param deltaTime The delta-time in seconds for this update.
     */
    void onUpdate(float deltaTime);

    /**
     * Called by the framework when the application should dispose and come to an end.
     */
    void onDispose();

    /**
     * Called when a keyboard event happens.
     *
     * @param key    The key.
     * @param action The action.
     */
    void onKeyEvent(KeyButton key, ClickAction action);
}
