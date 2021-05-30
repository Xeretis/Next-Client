package me.lor3mipsum.next.client.impl.modules.movement;

import me.lor3mipsum.next.api.event.NextEvent;
import me.lor3mipsum.next.api.event.client.TickEvent;
import me.lor3mipsum.next.client.core.module.Category;
import me.lor3mipsum.next.client.core.module.Module;
import me.lor3mipsum.next.client.core.module.annotation.Mod;
import me.lor3mipsum.next.client.impl.settings.BooleanSetting;
import me.lor3mipsum.next.client.impl.settings.EnumSetting;
import me.zero.alpine.event.EventPriority;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.util.Formatting;

@Mod(name = "Sprint", description = "Makes you sprint", category = Category.MOVEMENT)
public class Sprint extends Module {

    public EnumSetting<SprintMode> mode = new EnumSetting<>("Mode", SprintMode.Normal);
    public BooleanSetting stationary = new BooleanSetting("When Stationary", false);

    public enum SprintMode {
        Normal,
        Boost
    }

    @EventHandler
    private Listener<TickEvent> onTick = new Listener<>(event -> {
        if (mc.player == null || mc.world == null)
            return;

        switch (mode.getValue()) {
            case Normal:
                if((stationary.getValue() || mc.options.keyForward.isPressed()) && !mc.options.keyBack.isPressed() && !mc.options.keyRight.isPressed() && !mc.options.keyLeft.isPressed() && !mc.player.isSneaking() && !mc.player.horizontalCollision)
                    mc.player.setSprinting(true);
                break;
            case Boost:
                if ((stationary.getValue() || mc.options.keyForward.isPressed()) || mc.options.keyBack.isPressed() || mc.options.keyRight.isPressed() || mc.options.keyLeft.isPressed() && !mc.player.isSneaking() && !mc.player.horizontalCollision)
                    mc.player.setSprinting(true);
                break;
        }

    }, EventPriority.LOW, event -> event.era == NextEvent.Era.POST);

    @Override
    public void onDisable() {
        if (mc.player != null)
            mc.player.setSprinting(false);
    }

    @Override
    public String getHudInfo() {
        return "[" + Formatting.WHITE + mode.getValue().toString() + Formatting.GRAY + "]";
    }

}
