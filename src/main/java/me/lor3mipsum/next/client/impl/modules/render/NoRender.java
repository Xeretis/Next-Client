package me.lor3mipsum.next.client.impl.modules.render;

import me.lor3mipsum.next.client.event.EventTarget;
import me.lor3mipsum.next.client.impl.events.ParticleEvent;
import me.lor3mipsum.next.client.impl.events.RenderOverlayEvent;
import me.lor3mipsum.next.client.impl.settings.BooleanSetting;
import me.lor3mipsum.next.client.impl.settings.KeybindSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.Module;
import net.minecraft.client.particle.CampfireSmokeParticle;
import net.minecraft.client.particle.ElderGuardianAppearanceParticle;
import net.minecraft.client.particle.ExplosionLargeParticle;
import net.minecraft.particle.ParticleTypes;
import org.lwjgl.glfw.GLFW;

public class NoRender extends Module{

    public BooleanSetting fire = new BooleanSetting("Fire", true);
    public BooleanSetting blindness = new BooleanSetting("Blindness", true);
    public BooleanSetting hurtcam = new BooleanSetting("Hurtcam", true);
    public BooleanSetting liquid = new BooleanSetting("Liquid", true);
    public BooleanSetting pumpkin = new BooleanSetting("Pumpkin", true);
    public BooleanSetting wobble = new BooleanSetting("Wobble", true);
    public BooleanSetting totem = new BooleanSetting("Totem", true);
    public BooleanSetting skylight = new BooleanSetting("Skylight", true);
    public BooleanSetting explosions = new BooleanSetting("Explosions", true);
    public BooleanSetting campfire = new BooleanSetting("Campfire", true);
    public BooleanSetting bossbar = new BooleanSetting("Bossbar", true);
    public KeybindSetting keybind = new KeybindSetting(GLFW.GLFW_KEY_UNKNOWN);

    public NoRender() {
        super("NoRender", "It makes stuff not render", Category.RENDER);
    }

    @EventTarget
    public void onRenderOverlay(RenderOverlayEvent event) {
        if (pumpkin.isOn()) {
            event.setCancelled(true);
        }
    }

    @EventTarget
    public void onParticle(ParticleEvent.Normal event) {
        if (explosions.isOn() && event.particle instanceof ExplosionLargeParticle) {
            event.setCancelled(true);

        } else if (campfire.isOn() && event.particle instanceof CampfireSmokeParticle) {
            event.setCancelled(true);
        }
    }

    @EventTarget
    public void onParticleEmitter(ParticleEvent.Emitter event) {
        if (totem.isOn() && event.effect.getType() == ParticleTypes.TOTEM_OF_UNDYING) {
            event.setCancelled(true);
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
