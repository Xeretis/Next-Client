package me.lor3mipsum.next.client.impl.modules.movement;

import com.lukflug.panelstudio.mc16fabric.MinecraftGUI;
import me.lor3mipsum.next.api.event.NextEvent;
import me.lor3mipsum.next.api.event.client.TickEvent;
import me.lor3mipsum.next.api.event.network.PacketSendEvent;
import me.lor3mipsum.next.api.event.player.ClientMoveEvent;
import me.lor3mipsum.next.api.util.world.WorldUtils;
import me.lor3mipsum.next.client.core.module.Category;
import me.lor3mipsum.next.client.core.module.Module;
import me.lor3mipsum.next.client.core.module.annotation.Mod;
import me.lor3mipsum.next.client.impl.settings.BooleanSetting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemGroup;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Mod(name = "NoSlow", description = "Removes slowdown in certain cases", category = Category.MOVEMENT)
public class NoSlow extends Module {

    public BooleanSetting items = new BooleanSetting("Items", true);
    public BooleanSetting soul = new BooleanSetting("SoulSand", true);
    public BooleanSetting webs = new BooleanSetting("Webs", true);
    public BooleanSetting bushes = new BooleanSetting("Bushes", true);
    public BooleanSetting slime = new BooleanSetting("SlimeBlocks", true);
    public BooleanSetting sneak = new BooleanSetting("Sneak", true);
    public BooleanSetting inv = new BooleanSetting("Inventory", true);
    public BooleanSetting bypass = new BooleanSetting("Inv NCP Bypass", false);

    @EventHandler
    private Listener<ClientMoveEvent> onClientMove = new Listener<>(event -> {
        if (soul.getValue() && mc.world.getBlockState(mc.player.getBlockPos()).getBlock() == Blocks.SOUL_SAND) {
            mc.player.setVelocity(mc.player.getVelocity().multiply(2.5, 1, 2.5));
        }

        if (slime.getValue() && mc.world.getBlockState(new BlockPos(mc.player.getPos().add(0, -0.01, 0))).getBlock() == Blocks.SLIME_BLOCK && mc.player.isOnGround()) {
            double d = Math.abs(mc.player.getVelocity().y);
            if (d < 0.1D && !mc.player.bypassesSteppingEffects()) {
                double e = 1 / (0.4D + d * 0.2D);
                mc.player.setVelocity(mc.player.getVelocity().multiply(e, 1.0D, e));
            }
        }

        if (webs.getValue() && WorldUtils.doesBoxTouchBlock(mc.player.getBoundingBox(), Blocks.COBWEB)) {
            mc.player.slowMovement(mc.player.getBlockState(), new Vec3d(1.75, 1.75, 1.75));
        }

        if (bushes.getValue() && WorldUtils.doesBoxTouchBlock(mc.player.getBoundingBox(), Blocks.SWEET_BERRY_BUSH)) {
            mc.player.slowMovement(mc.player.getBlockState(), new Vec3d(1.7, 1.7, 1.7));
        }
    });

    @EventHandler
    private Listener<PacketSendEvent> onPacketSend = new Listener<>(event -> {
        if (event.packet instanceof ClickSlotC2SPacket && bypass.getValue()) {
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
        }
    });

    @EventHandler
    private Listener<TickEvent> onTick = new Listener<>(event -> {
        if(inv.getValue() && shouldInvMove(mc.currentScreen)) {
            for (KeyBinding k : new KeyBinding[] { mc.options.keyForward, mc.options.keyBack,
                    mc.options.keyLeft, mc.options.keyRight, mc.options.keyJump, mc.options.keySprint }) {
                k.setPressed(InputUtil.isKeyPressed(mc.getWindow().getHandle(),
                        InputUtil.fromTranslationKey(k.getBoundKeyTranslationKey()).getCode()));
            }
        }
    }, event -> event.era == NextEvent.Era.POST);

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

}
