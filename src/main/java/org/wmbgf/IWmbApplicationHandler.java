package org.wmbgf;

public interface IWmbApplicationHandler {

    void onInit();
    void onUpdate(float deltaTime);
    void onDestroy();
}
