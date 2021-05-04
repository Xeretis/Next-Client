package me.lor3mipsum.next.client.impl.modules.render;

import com.mojang.blaze3d.systems.RenderSystem;
import me.lor3mipsum.next.client.event.EventTarget;
import me.lor3mipsum.next.client.impl.events.EntityRenderEvent;
import me.lor3mipsum.next.client.impl.settings.BooleanSetting;
import me.lor3mipsum.next.client.impl.settings.KeybindSetting;
import me.lor3mipsum.next.client.impl.settings.ModeSetting;
import me.lor3mipsum.next.client.impl.settings.NumberSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.Module;
import me.lor3mipsum.next.client.social.SocialManager;
import me.lor3mipsum.next.client.utils.render.WorldRenderUtils;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Nametags extends Module {

    public NumberSetting size = new NumberSetting("Size", 2, 0.5, 5, 0.1);
    public BooleanSetting armor = new BooleanSetting("Armor", true);
    public BooleanSetting armorE = new BooleanSetting("ArmorEnchants", true);
    public NumberSetting itemOffset = new NumberSetting("ItemOffset", 1.5, 0.1, 3, 0.1);
    public BooleanSetting name = new BooleanSetting("Name", true);
    public BooleanSetting health = new BooleanSetting("Health", true);
    public ModeSetting hpMode = new ModeSetting("HpMode", "Bar", "Bar", "Num");
    public BooleanSetting ping = new BooleanSetting("Ping", true);
    public BooleanSetting gm = new BooleanSetting("Gamemode", false);
    public BooleanSetting distance = new BooleanSetting("Distance", false);
    public KeybindSetting keybind = new KeybindSetting(GLFW.GLFW_KEY_UNKNOWN);

    public Nametags() {
        super("Nametags", "Replaces vanilla nametags with cooler ones", Category.RENDER);
    }

    @EventTarget
    public void onLivingLabelRender(EntityRenderEvent.Single.Label event) {
        if (event.getEntity() instanceof PlayerEntity)
            event.setCancelled(true);
    }

    @EventTarget
    public void onLivingRender(EntityRenderEvent.Single.Post event) {
        List<String> lines = new ArrayList<>();
        double scale = 0;

        Entity entity = event.getEntity();
        Vec3d rPos = getRenderPos(entity);

        if (entity instanceof PlayerEntity) {
            if (entity == mc.player || entity.hasPassenger(mc.player) || mc.player.hasPassenger(entity)) {
                return;
            }

            PlayerEntity livingEntity = (PlayerEntity) entity;

            scale = Math.max(size.getNumber() * (mc.cameraEntity.distanceTo(entity) / 20), 1);

            addPlayerLines(lines, (PlayerEntity) entity);

            /* Drawing Items */
            double c = 0;
            double lscale = scale * 0.4;
            double up = ((0.3 + lines.size() * 0.25) * scale) + lscale / 2;

            if (armor.isOn()) {
                drawItem(rPos.x, rPos.y + up, rPos.z, -4 + itemOffset.getNumber(), 0, lscale, livingEntity.getEquippedStack(EquipmentSlot.MAINHAND));
                drawItem(rPos.x, rPos.y + up, rPos.z, 1 + itemOffset.getNumber(), 0, lscale, livingEntity.getEquippedStack(EquipmentSlot.OFFHAND));

                for (ItemStack i : livingEntity.getArmorItems()) {
                    drawItem(rPos.x, rPos.y + up, rPos.z, c + itemOffset.getNumber(), 0, lscale, i);
                    c--;
                }
            }
        }

        if (!lines.isEmpty()) {
            float offset = 0.25f + lines.size() * 0.25f;

            for (String s: lines) {
                WorldRenderUtils.drawText(s, rPos.x, rPos.y + (offset * scale), rPos.z, scale);

                offset -= 0.25f;
            }
        }
    }

    public void addPlayerLines(List<String> lines, PlayerEntity player) {
        addPlayerNameHealthLine(lines, player, SocialManager.isFriend(player.getName().getString()) ? Formatting.AQUA : (SocialManager.isEnemy(player.getName().getString()) ? Formatting.RED : Formatting.WHITE),
                name.isOn(),
                health.isOn(),
                ping.isOn(),
                gm.isOn(),
                distance.isOn());
    }

    private void addPlayerNameHealthLine(List<String> lines, PlayerEntity player, Formatting color, boolean addName, boolean addHealth, boolean addPing, boolean addGm, boolean addDist) {
        PlayerListEntry playerEntry = mc.player.networkHandler.getPlayerListEntry(mc.player.getGameProfile().getId());

        String pingText = addPing && playerEntry != null ? Formatting.GOLD.toString() + playerEntry.getLatency() + "ms" : "";
        String nameText = addName ? color + player.getName().getString() : "";
        String gmText = addGm && playerEntry != null ?
                Formatting.GRAY + "[" + playerEntry.getGameMode().toString().substring(0, playerEntry.getGameMode() == GameMode.SPECTATOR ? 2 : 1) + "]" : "";

        double dist = Math.round(mc.player.distanceTo(player) * 10.0) / 10.0;
        String distText = addDist ? getDistanceColor(dist) + String.valueOf(dist) + "m" : "";

        if (addName || addHealth || addPing || addGm || addDist) {
            lines.add(color + pingText + " " + nameText + " " + (addHealth ? getHealthText(player) : "") + " " + (addDist ? distText : "") + gmText.trim().replaceAll("  *", " "));
        }
    }

    private Vec3d getRenderPos(Entity e) {
        return new Vec3d(
                e.lastRenderX + (e.getX() - e.lastRenderX) * mc.getTickDelta(),
                (e.lastRenderY + (e.getY() - e.lastRenderY) * mc.getTickDelta()) + e.getHeight(),
                e.lastRenderZ + (e.getZ() - e.lastRenderZ) * mc.getTickDelta());
    }

    private String getHealthText(LivingEntity e) {
        if (hpMode.getMode() == "Bar") {
            /* Health bar */
            String health = "";
            /* - Add Green Normal Health */
            for (int i = 0; i < e.getHealth(); i++)
                health += Formatting.GREEN + "|";
            /* - Add Red Empty Health (Remove Based on absorption amount) */
            for (int i = 0; i < MathHelper.clamp(e.getAbsorptionAmount(), 0, e.getMaxHealth() - e.getHealth()); i++)
                health += Formatting.YELLOW + "|";
            /* - Add Yellow Absorption Health */
            for (int i = 0; i < e.getMaxHealth() - (e.getHealth() + e.getAbsorptionAmount()); i++)
                health += Formatting.RED + "|";

            return health;
        }

        return getHealthColor(e).toString() + (int) (e.getHealth() + e.getAbsorptionAmount());
    }

    private Formatting getHealthColor(LivingEntity entity) {
        if (entity.getHealth() + entity.getAbsorptionAmount() > entity.getMaxHealth())
            return Formatting.DARK_GREEN;
        else if (entity.getHealth() + entity.getAbsorptionAmount() >= entity.getMaxHealth() * 0.7)
            return Formatting.GREEN;
        else if (entity.getHealth() + entity.getAbsorptionAmount() >= entity.getMaxHealth() * 0.4)
            return Formatting.YELLOW;
        else if (entity.getHealth() + entity.getAbsorptionAmount() >= entity.getMaxHealth() * 0.1)
            return Formatting.RED;
        else
            return Formatting.DARK_RED;
    }

    private Formatting getDistanceColor(double dist) {
        if (dist > 50)
            return Formatting.DARK_GREEN;
        else if (dist > 20)
            return Formatting.GREEN;
        else if (dist > 10)
            return Formatting.YELLOW;
        else if (dist > 5)
            return Formatting.RED;
        else
            return Formatting.DARK_RED;
    }

    private void drawItem(double x, double y, double z, double offX, double offY, double scale, ItemStack item) {
        MatrixStack matrix = WorldRenderUtils.drawGuiItem(x, y, z, offX, offY, scale, item);

        matrix.scale(-0.05F, -0.05F, 0.05f);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(GL11.GL_ALWAYS);

        if (!item.isEmpty() && !(item.getCount() == 1)) {
            int w = mc.textRenderer.getWidth("x" + item.getCount()) / 2;
            mc.textRenderer.draw("x" + item.getCount(), 7 - w, 3, 0xffffff, true, matrix.peek().getModel(),
                    mc.getBufferBuilders().getEntityVertexConsumers(), true, 0, 0xf000f0);
        }

        matrix.scale(0.85F, 0.85F, 1F);

        int c = 0;
        if (!(item.getItem() instanceof ArmorItem) || armorE.isOn())
            for (Map.Entry<Enchantment, Integer> m : EnchantmentHelper.get(item).entrySet()) {
                String text = I18n.translate(m.getKey().getName(2).getString());

                if (text.isEmpty())
                    continue;

                String subText = text.substring(0, Math.min(text.length(), 2)) + m.getValue();

                int w1 = mc.textRenderer.getWidth(subText) / 2;
                mc.textRenderer.draw(subText, -2 - w1, c * 10 - 19,
                        m.getKey() == Enchantments.VANISHING_CURSE || m.getKey() == Enchantments.BINDING_CURSE ? Color.RED.getRGB() : Color.WHITE.getRGB(),
                        true, matrix.peek().getModel(), mc.getBufferBuilders().getEntityVertexConsumers(), true, 0, 0xf000f0);
                c--;
            }

        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        RenderSystem.disableDepthTest();

        RenderSystem.disableBlend();
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
