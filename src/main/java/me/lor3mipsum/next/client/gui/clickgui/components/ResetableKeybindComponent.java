package me.lor3mipsum.next.client.gui.clickgui.components;

import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.settings.KeybindComponent;
import com.lukflug.panelstudio.settings.KeybindSetting;
import com.lukflug.panelstudio.theme.Renderer;
import org.lwjgl.glfw.GLFW;

public class ResetableKeybindComponent extends KeybindComponent {
    public ResetableKeybindComponent(Renderer renderer, KeybindSetting keybind) {
        super(renderer, keybind);
    }

    @Override
    public void handleKey(Context context, int scancode) {
        context.setHeight(renderer.getHeight(false));

        if (hasFocus(context) && (scancode == GLFW.GLFW_KEY_DELETE || scancode == GLFW.GLFW_KEY_BACKSPACE)) {
            keybind.setKey(GLFW.GLFW_KEY_UNKNOWN);
            releaseFocus();
            return;
        }
        super.handleKey(context, scancode);
    }
}
