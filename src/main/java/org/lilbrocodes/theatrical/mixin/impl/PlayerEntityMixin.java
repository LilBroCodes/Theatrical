package org.lilbrocodes.theatrical.mixin.impl;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.lilbrocodes.theatrical.cca.TheatricalCardinalComponents;
import org.lilbrocodes.theatrical.mixin.accessor.PlayerHandHeld;
import org.lilbrocodes.theatrical.mixin.accessor.WalkSpeedHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PlayerHandHeld, WalkSpeedHolder {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public boolean theatrical$isHandHeldOut() {
        return TheatricalCardinalComponents.HAND_HELD_OUT.get(this).isHeld();
    }

    @Override
    public void theatrical$setHandHeldOut(boolean handHeld) {
        TheatricalCardinalComponents.HAND_HELD_OUT.get(this).setHeld(handHeld);
    }

    @Override
    public int theatrical$getWalkSpeed() {
        return TheatricalCardinalComponents.WALK_SPEED.get(this).getValue();
    }

    @Override
    public void theatrical$setWalkSpeed(int speed) {
        TheatricalCardinalComponents.WALK_SPEED.get(this).setValue(speed);
    }

    @ModifyReturnValue(method = "getMovementSpeed", at = @At("TAIL"))
    public float theatrical$applyMovementSpeed(float original) {
        return original * (theatrical$getWalkSpeed() / 100f);
    }
}
