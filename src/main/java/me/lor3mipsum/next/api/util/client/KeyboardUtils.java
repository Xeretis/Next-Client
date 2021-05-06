package me.lor3mipsum.next.api.util.client;

import org.lwjgl.glfw.GLFW;

public class KeyboardUtils {

    public enum KeyAction {
        Press,
        Repeat,
        Release;

        public static KeyAction get(int action) {
            if (action == GLFW.GLFW_PRESS) return Press;
            else if (action == GLFW.GLFW_RELEASE) return Release;
            else return Repeat;
        }
    }

}
