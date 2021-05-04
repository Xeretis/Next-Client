package me.lor3mipsum.next.client.impl.modules.render;

import me.lor3mipsum.next.client.event.EventTarget;
import me.lor3mipsum.next.client.impl.events.RenderItemEvent;
import me.lor3mipsum.next.client.impl.settings.BooleanSetting;
import me.lor3mipsum.next.client.impl.settings.KeybindSetting;
import me.lor3mipsum.next.client.impl.settings.NumberSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.Module;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;

public class ItemViewModel extends Module {
    //public BooleanSetting rightHand = new BooleanSetting("RightHand", true);
    public NumberSetting rightHandScale = new NumberSetting("RScale", 1, 0.1, 3, 0.05);
    public NumberSetting rightHandX = new NumberSetting("RX", 0, -2, 2, 0.05);
    public NumberSetting rightHandY = new NumberSetting("RY", 0, -2, 2, 0.05);
    public NumberSetting rightHandZ = new NumberSetting("RZ", 0, -2, 2, 0.05);

    //public BooleanSetting leftHand = new BooleanSetting("LeftHand", true);
    public NumberSetting leftHandScale = new NumberSetting("LScale", 1, 0.1, 3, 0.05);
    public NumberSetting leftHandX = new NumberSetting("LX", 0, -2, 2, 0.05);
    public NumberSetting leftHandY = new NumberSetting("LY", 0, -2, 2, 0.05);
    public NumberSetting leftHandZ = new NumberSetting("LZ", 0, -2, 2, 0.05);
    public KeybindSetting keybind = new KeybindSetting(GLFW.GLFW_KEY_UNKNOWN);

    public ItemViewModel() {
        super("ItemViewModel", "Changes how items in your hand are rendered", Category.RENDER);
    }

    @EventTarget
    private void onRenderItem(RenderItemEvent event) {
        MatrixStack matrixStack = event.getMatrixStack();
        switch (event.getRenderTime()) {
            case PRE:
                matrixStack.push();
                switch (event.getType()) {
                    case FIRST_PERSON_RIGHT_HAND:
                        matrixStack.translate(rightHandX.getNumber(), rightHandY.getNumber(), rightHandZ.getNumber());
                        matrixStack.scale((float) rightHandScale.getNumber(), (float) rightHandScale.getNumber(), (float) rightHandScale.getNumber());
                        break;
                    case FIRST_PERSON_LEFT_HAND:
                        matrixStack.translate(leftHandX.getNumber(), leftHandY.getNumber(), leftHandZ.getNumber());
                        matrixStack.scale((float) leftHandScale.getNumber(), (float) leftHandScale.getNumber(), (float) leftHandScale.getNumber());
                        break;
                }
                break;
            case POST:
                matrixStack.pop();
                break;
        }
    }

    @Override
    public int getKeybind() {
        return keybind.getKey();
    }

    @Override
    public void setKeybind(int keybind) {
        this.keybind.setKey(keybind);
    }
}
