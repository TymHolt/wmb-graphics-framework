package org.wmbgf;

public interface IApplicationHandler {

    void onInit();
    void onUpdate(float deltaTime);
    void onDestroy();
}
