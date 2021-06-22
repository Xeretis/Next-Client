package me.lor3mipsum.next.mixin;

import com.mojang.authlib.GameProfile;
import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.event.NextEvent;
import me.lor3mipsum.next.api.event.player.ClientMoveEvent;
import me.lor3mipsum.next.api.event.player.SendMessageEvent;
import me.lor3mipsum.next.api.event.player.SendMovementPacketsEvent;
import me.lor3mipsum.next.client.impl.modules.movement.NoSlow;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ClientPlayerEntity.class, priority = 1001)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

    @Shadow public Input input;

    @Shadow @Final public ClientPlayNetworkHandler networkHandler;

    @Shadow public int ticksSinceSprintingChanged;

    @Shadow @Final protected MinecraftClient client;

    @Shadow protected int ticksLeftToDoubleTapSprint;

    @Shadow private int field_3938;

    @Shadow private float field_3922;

    @Shadow private int underwaterVisibilityTicks;

    @Shadow private boolean inSneakingPose;

    @Shadow protected abstract void startRidingJump();

    @Shadow private boolean field_3939;

    @Shadow public abstract boolean hasJumpingMount();

    @Shadow protected abstract boolean isCamera();

    @Shadow protected abstract boolean isWalking();

    @Shadow protected abstract void pushOutOfBlocks(double x, double y);

    @Shadow public abstract boolean shouldSlowDown();

    @Shadow protected abstract void updateNausea();

    @Shadow private int ticksToNextAutojump;

    @Shadow public abstract float method_3151();

    @Shadow protected void autoJump(float dx, float dz) {}

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(at = @At("HEAD"), method = "sendChatMessage", cancellable = true)
    private void onSendChatMessage(String msg, CallbackInfo info) {
        if(msg.startsWith(Main.prefix) && msg.length() > 1) {
            Main.commandManager.executeCommand(msg);
            info.cancel();
            return;
        }
    }

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    public void move(MovementType type, Vec3d movement, CallbackInfo info) {
        ClientMoveEvent event = new ClientMoveEvent(type, movement);

        Main.EVENT_BUS.post(event);

        if (event.isCancelled())
            info.cancel();
        else if (!type.equals(event.type) || !movement.equals(event.vec3d)) {
            double double_1 = this.getX();
            double double_2 = this.getZ();
            super.move(event.type, event.vec3d);
            this.autoJump((float) (this.getX() - double_1), (float) (this.getZ() - double_2));
            info.cancel();
        }
    }

    @Inject(method = "tickMovement", at = @At("HEAD"), cancellable = true)
    public void tickMovePre(CallbackInfo info) {
        ++this.ticksSinceSprintingChanged;
        if (this.ticksLeftToDoubleTapSprint > 0) {
            --this.ticksLeftToDoubleTapSprint;
        }

        this.updateNausea();
        boolean bl = this.input.jumping;
        boolean bl2 = this.input.sneaking;
        boolean bl3 = this.isWalking();
        this.inSneakingPose = !this.abilities.flying && !this.isSwimming() && this.wouldPoseNotCollide(EntityPose.CROUCHING) && (this.isSneaking() || !this.isSleeping() && !this.wouldPoseNotCollide(EntityPose.STANDING));
        this.input.tick(this.shouldSlowDown());
        this.client.getTutorialManager().onMovement(this.input);
        if (this.isUsingItem() && !this.hasVehicle() && !(Main.moduleManager.getModule(NoSlow.class).items.getValue() && Main.moduleManager.getModule(NoSlow.class).getEnabled())) {
            Input var10000 = this.input;
            var10000.movementSideways *= 0.2F;
            var10000 = this.input;
            var10000.movementForward *= 0.2F;
            this.ticksLeftToDoubleTapSprint = 0;
        }

        boolean bl4 = false;
        if (this.ticksToNextAutojump > 0) {
            --this.ticksToNextAutojump;
            bl4 = true;
            this.input.jumping = true;
        }

        if (!this.noClip) {
            this.pushOutOfBlocks(this.getX() - (double)this.getWidth() * 0.35D, this.getZ() + (double)this.getWidth() * 0.35D);
            this.pushOutOfBlocks(this.getX() - (double)this.getWidth() * 0.35D, this.getZ() - (double)this.getWidth() * 0.35D);
            this.pushOutOfBlocks(this.getX() + (double)this.getWidth() * 0.35D, this.getZ() - (double)this.getWidth() * 0.35D);
            this.pushOutOfBlocks(this.getX() + (double)this.getWidth() * 0.35D, this.getZ() + (double)this.getWidth() * 0.35D);
        }

        if (bl2) {
            this.ticksLeftToDoubleTapSprint = 0;
        }

        boolean bl5 = (float)this.getHungerManager().getFoodLevel() > 6.0F || this.abilities.allowFlying;
        if ((this.onGround || this.isSubmergedInWater()) && !bl2 && !bl3 && this.isWalking() && !this.isSprinting() && bl5 && !this.isUsingItem() && !this.hasStatusEffect(StatusEffects.BLINDNESS)) {
            if (this.ticksLeftToDoubleTapSprint <= 0 && !this.client.options.keySprint.isPressed()) {
                this.ticksLeftToDoubleTapSprint = 7;
            } else {
                this.setSprinting(true);
            }
        }

        if (!this.isSprinting() && (!this.isTouchingWater() || this.isSubmergedInWater()) && this.isWalking() && bl5 && !this.isUsingItem() && !this.hasStatusEffect(StatusEffects.BLINDNESS) && this.client.options.keySprint.isPressed()) {
            this.setSprinting(true);
        }

        boolean bl8;
        if (this.isSprinting()) {
            bl8 = !this.input.hasForwardMovement() || !bl5;
            boolean bl7 = bl8 || this.horizontalCollision || this.isTouchingWater() && !this.isSubmergedInWater();
            if (this.isSwimming()) {
                if (!this.onGround && !this.input.sneaking && bl8 || !this.isTouchingWater()) {
                    this.setSprinting(false);
                }
            } else if (bl7) {
                this.setSprinting(false);
            }
        }

        bl8 = false;
        if (this.abilities.allowFlying) {
            if (this.client.interactionManager.isFlyingLocked()) {
                if (!this.abilities.flying) {
                    this.abilities.flying = true;
                    bl8 = true;
                    this.sendAbilitiesUpdate();
                }
            } else if (!bl && this.input.jumping && !bl4) {
                if (this.abilityResyncCountdown == 0) {
                    this.abilityResyncCountdown = 7;
                } else if (!this.isSwimming()) {
                    this.abilities.flying = !this.abilities.flying;
                    bl8 = true;
                    this.sendAbilitiesUpdate();
                    this.abilityResyncCountdown = 0;
                }
            }
        }

        if (this.input.jumping && !bl8 && !bl && !this.abilities.flying && !this.hasVehicle() && !this.isClimbing()) {
            ItemStack itemStack = this.getEquippedStack(EquipmentSlot.CHEST);
            if (itemStack.getItem() == Items.ELYTRA && ElytraItem.isUsable(itemStack) && this.checkFallFlying()) {
                this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            }
        }

        this.field_3939 = this.isFallFlying();
        if (this.isTouchingWater() && this.input.sneaking && this.method_29920()) {
            this.knockDownwards();
        }

        int j;
        if (this.isSubmergedIn(FluidTags.WATER)) {
            j = this.isSpectator() ? 10 : 1;
            this.underwaterVisibilityTicks = MathHelper.clamp(this.underwaterVisibilityTicks + j, 0, 600);
        } else if (this.underwaterVisibilityTicks > 0) {
            this.isSubmergedIn(FluidTags.WATER);
            this.underwaterVisibilityTicks = MathHelper.clamp(this.underwaterVisibilityTicks - 10, 0, 600);
        }

        if (this.abilities.flying && this.isCamera()) {
            j = 0;
            if (this.input.sneaking) {
                --j;
            }

            if (this.input.jumping) {
                ++j;
            }

            if (j != 0) {
                this.setVelocity(this.getVelocity().add(0.0D, (double)((float)j * this.abilities.getFlySpeed() * 3.0F), 0.0D));
            }
        }

        if (this.hasJumpingMount()) {
            JumpingMount jumpingMount = (JumpingMount)this.getVehicle();
            if (this.field_3938 < 0) {
                ++this.field_3938;
                if (this.field_3938 == 0) {
                    this.field_3922 = 0.0F;
                }
            }

            if (bl && !this.input.jumping) {
                this.field_3938 = -10;
                jumpingMount.setJumpStrength(MathHelper.floor(this.method_3151() * 100.0F));
                this.startRidingJump();
            } else if (!bl && this.input.jumping) {
                this.field_3938 = 0;
                this.field_3922 = 0.0F;
            } else if (bl) {
                ++this.field_3938;
                if (this.field_3938 < 10) {
                    this.field_3922 = (float)this.field_3938 * 0.1F;
                } else {
                    this.field_3922 = 0.8F + 2.0F / (float)(this.field_3938 - 9) * 0.1F;
                }
            }
        } else {
            this.field_3922 = 0.0F;
        }

        super.tickMovement();
        if (this.onGround && this.abilities.flying && !this.client.interactionManager.isFlyingLocked()) {
            this.abilities.flying = false;
            this.sendAbilitiesUpdate();
        }
        info.cancel();
    }

    @Inject(method = "shouldSlowDown", at = @At("HEAD"), cancellable = true)
    private void onShouldSlowDown(CallbackInfoReturnable<Boolean> info) {
        if (Main.moduleManager.getModule(NoSlow.class).sneak.getValue() && Main.moduleManager.getModule(NoSlow.class).getEnabled()) {
            info.setReturnValue(shouldLeaveSwimmingPose());
        }
    }

    @Inject(method = "sendMovementPackets", at = @At("HEAD"))
    private void onSendMovementPacketsHead(CallbackInfo info) {
        Main.EVENT_BUS.post(new SendMovementPacketsEvent(NextEvent.Era.PRE));
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V", ordinal = 0))
    private void onTickHasVehicleBeforeSendPackets(CallbackInfo info) {
        Main.EVENT_BUS.post(new SendMovementPacketsEvent(NextEvent.Era.PRE));
    }

    @Inject(method = "sendMovementPackets", at = @At("TAIL"))
    private void onSendMovementPacketsTail(CallbackInfo info) {
        Main.EVENT_BUS.post(new SendMovementPacketsEvent(NextEvent.Era.POST));
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V", ordinal = 1, shift = At.Shift.AFTER))
    private void onTickHasVehicleAfterSendPackets(CallbackInfo info) {
        Main.EVENT_BUS.post(new SendMovementPacketsEvent(NextEvent.Era.POST));
    }
}
