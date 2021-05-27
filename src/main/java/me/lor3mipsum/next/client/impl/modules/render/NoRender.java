package me.lor3mipsum.next.client.impl.modules.render;

import me.lor3mipsum.next.api.event.game.*;
import me.lor3mipsum.next.api.event.world.ParticleEmitterEvent;
import me.lor3mipsum.next.api.event.world.ParticleNormalEvent;
import me.lor3mipsum.next.client.core.module.Category;
import me.lor3mipsum.next.client.core.module.Module;
import me.lor3mipsum.next.client.core.module.annotation.Mod;
import me.lor3mipsum.next.client.impl.settings.BooleanSetting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.particle.CampfireSmokeParticle;
import net.minecraft.client.particle.ExplosionLargeParticle;
import net.minecraft.particle.ParticleTypes;

@Mod(name = "NoRender", description = "Makes stuff not render", category = Category.RENDER)
public class NoRender extends Module {

    public BooleanSetting fire = new BooleanSetting("Fire", true);
    public BooleanSetting hurtcam = new BooleanSetting("Hurtcam", true);
    public BooleanSetting pumpkin = new BooleanSetting("Pumpkin", true);
    public BooleanSetting totem = new BooleanSetting("Totem", true);
    public BooleanSetting explosions = new BooleanSetting("Explosions", true);
    public BooleanSetting campfire = new BooleanSetting("Campfire", true);
    public BooleanSetting bossbar = new BooleanSetting("Bossbar", true);

    @EventHandler
    private Listener<RenderHurtcamEvent> onHurtcam = new Listener<>(event -> {
       if (hurtcam.getValue())
           event.cancel();
    });

    @EventHandler
    private Listener<RenderOverlayEvent> onOverlay = new Listener<>(event -> {
        if (pumpkin.getValue())
            event.cancel();
    });

    @EventHandler
    private Listener<RenderBossBarEvent> onBossBar = new Listener<>(event -> {
        if (bossbar.getValue())
            event.cancel();
    });

    @EventHandler
    private Listener<ParticleEmitterEvent> onEmitter = new Listener<>(event -> {
        if (totem.getValue())
            event.cancel();
    }, event -> event.particle.getType() == ParticleTypes.TOTEM_OF_UNDYING);

    @EventHandler
    private Listener<RenderFloatingTotemEvent> onFloatingTotem = new Listener<>(event -> {
        if (totem.getValue())
            event.cancel();
    });

    @EventHandler
    private Listener<ParticleNormalEvent> onNormal = new Listener<>(event -> {
        if (explosions.getValue() && event.particle instanceof ExplosionLargeParticle) {
            event.cancel();

        } else if (campfire.getValue() && event.particle instanceof CampfireSmokeParticle)
            event.cancel();
    });

    @EventHandler
    private Listener<RenderFireEvent> onFire = new Listener<>(event -> {
        if (fire.getValue())
            event.cancel();
    });

}
