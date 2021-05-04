package me.lor3mipsum.next.client.impl.events;

import me.lor3mipsum.next.client.event.events.Cancellable;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class RenderItemEvent extends Cancellable {
    private MatrixStack matrixStack;
    private ItemStack itemStack;
    private ModelTransformation.Mode type;
    private RenderTime renderTime;
    private boolean leftHanded;

    public RenderItemEvent(MatrixStack matrixStack, ItemStack itemStack, ModelTransformation.Mode type, RenderTime renderTime, boolean leftHanded)
    {
        this.matrixStack = matrixStack;
        this.itemStack = itemStack;
        this.type = type;
        this.renderTime = renderTime;
        this.leftHanded = leftHanded;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public ModelTransformation.Mode getType() {
        return type;
    }

    public RenderTime getRenderTime() {
        return renderTime;
    }

    public MatrixStack getMatrixStack() {
        return matrixStack;
    }

    public boolean isLeftHanded() {
        return leftHanded;
    }

    public enum RenderTime {
        PRE, POST
    }
}
