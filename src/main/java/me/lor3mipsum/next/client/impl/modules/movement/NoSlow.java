package me.lor3mipsum.next.client.impl.modules.movement;

import com.lukflug.panelstudio.mc16.MinecraftGUI;
import me.lor3mipsum.next.client.event.EventTarget;
import me.lor3mipsum.next.client.impl.events.ClientMoveEvent;
import me.lor3mipsum.next.client.impl.events.SendPacketEvent;
import me.lor3mipsum.next.client.impl.events.TickEvent;
import me.lor3mipsum.next.client.impl.events.WorldRenderEvent;
import me.lor3mipsum.next.client.impl.settings.BooleanSetting;
import me.lor3mipsum.next.client.impl.settings.KeybindSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.Module;
import me.lor3mipsum.next.client.utils.world.WorldUtils;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemGroup;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class NoSlow extends Module {

    public BooleanSetting items = new BooleanSetting("Items", true);
    public BooleanSetting soul = new BooleanSetting("SoulSand", true);
    public BooleanSetting webs = new BooleanSetting("Webs", true);
    public BooleanSetting bushes = new BooleanSetting("Bushes", true);
    public BooleanSetting slime = new BooleanSetting("SlimeBlocks", true);
    public BooleanSetting sneak = new BooleanSetting("Sneak", true);
    public BooleanSetting inv = new BooleanSetting("Inventory", true);
    public BooleanSetting bypass = new BooleanSetting("InvNCPBypass", false);
    public KeybindSetting keybind = new KeybindSetting(GLFW.GLFW_KEY_UNKNOWN);

    public NoSlow() {
        super("NoSlow", "Removes the slowing down property of stuff", Category.MOVEMENT);
    }

    @EventTarget
    private void onClientMove(ClientMoveEvent event) {
        if (soul.isOn() && mc.world.getBlockState(mc.player.getBlockPos()).getBlock() == Blocks.SOUL_SAND) {
            mc.player.setVelocity(mc.player.getVelocity().multiply(2.5, 1, 2.5));
        }

        if (slime.isOn()
                && mc.world.getBlockState(new BlockPos(mc.player.getPos().add(0, -0.01, 0))).getBlock() == Blocks.SLIME_BLOCK && mc.player.isOnGround()) {
            double d = Math.abs(mc.player.getVelocity().y);
            if (d < 0.1D && !mc.player.bypassesSteppingEffects()) {
                double e = 1 / (0.4D + d * 0.2D);
                mc.player.setVelocity(mc.player.getVelocity().multiply(e, 1.0D, e));
            }
        }

        if (webs.isOn() && WorldUtils.doesBoxTouchBlock(mc.player.getBoundingBox(), Blocks.COBWEB)) {
            // still kinda scuffed until i get an actual mixin
            mc.player.slowMovement(mc.player.getBlockState(), new Vec3d(1.75, 1.75, 1.75));
        }

        if (bushes.isOn() && WorldUtils.doesBoxTouchBlock(mc.player.getBoundingBox(), Blocks.SWEET_BERRY_BUSH)) {
            // also scuffed
            mc.player.slowMovement(mc.player.getBlockState(), new Vec3d(1.7, 1.7, 1.7));
        }
    }

    @EventTarget
    private void onSendPacket(SendPacketEvent event) {
        if (event.getPacket() instanceof ClickSlotC2SPacket && bypass.isOn()) {
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
        }
    }

    @EventTarget
    private void onTick(TickEvent.Post event) {
        if(inv.isOn() && shouldInvMove(mc.currentScreen)) {
            for (KeyBinding k : new KeyBinding[] { mc.options.keyForward, mc.options.keyBack,
                    mc.options.keyLeft, mc.options.keyRight, mc.options.keyJump, mc.options.keySprint }) {
                k.setPressed(InputUtil.isKeyPressed(mc.getWindow().getHandle(),
                        InputUtil.fromTranslationKey(k.getBoundKeyTranslationKey()).getCode()));
            }
        }
    }

    private boolean shouldInvMove(Screen screen) {
        if (screen == null || screen instanceof MinecraftGUI) {
            return false;
        }

        return !(screen instanceof ChatScreen
                || screen instanceof BookEditScreen
                || screen instanceof SignEditScreen
                || screen instanceof JigsawBlockScreen
                || screen instanceof StructureBlockScreen
                || screen instanceof AnvilScreen
                || (screen instanceof CreativeInventoryScreen
                && ((CreativeInventoryScreen) screen).getSelectedTab() == ItemGroup.SEARCH.getIndex()));
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
