package org.lilbrocodes.theatrical.mixin.impl;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.lilbrocodes.theatrical.cca.TheatricalCardinalComponents;
import org.lilbrocodes.theatrical.config.Configs;
import org.lilbrocodes.theatrical.mixin.accessor.DirectorDataHolder;
import org.lilbrocodes.theatrical.mixin.accessor.HandHeldDataHolder;
import org.lilbrocodes.theatrical.mixin.accessor.PlotArmorDataHolder;
import org.lilbrocodes.theatrical.mixin.accessor.WalkSpeedHolder;
import org.lilbrocodes.theatrical.util.PlotArmorType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements HandHeldDataHolder, WalkSpeedHolder, DirectorDataHolder, PlotArmorDataHolder {
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

    @Override
    public boolean theatrical$isDirector() {
        return TheatricalCardinalComponents.DIRECTOR.get(this).isDirector();
    }

    @Override
    public void theatrical$setDirector(boolean director) {
        TheatricalCardinalComponents.DIRECTOR.get(this).setDirector(director);
    }

    @Override
    public PlotArmorType theatrical$getType() {
        return TheatricalCardinalComponents.PLOT_ARMOR_TYPE.get(this).getType();
    }

    @Override
    public void theatrical$setType(PlotArmorType type) {
        TheatricalCardinalComponents.PLOT_ARMOR_TYPE.get(this).setType(type);
    }

    @ModifyReturnValue(method = "getMovementSpeed", at = @At("TAIL"))
    public float theatrical$applyMovementSpeed(float original) {
        return original * (theatrical$getWalkSpeed() / 100f);
    }

    @Inject(method = "applyDamage", at = @At("TAIL"))
    private void theatrical$plotArmorAdjustments(DamageSource source, float amount, CallbackInfo ci) {
        if ((Object) this instanceof PlayerEntity player) {
            PlotArmorType type = ((PlotArmorDataHolder) player).theatrical$getType();

            if (type == PlotArmorType.POSITIVE) {
                float minHealth = Configs.SERVER.plotArmor.positive_minHealth * 2.0f;
                if (player.getHealth() < minHealth) {
                    player.setHealth(minHealth);
                }

            } else if (type == PlotArmorType.NEGATIVE) {
                float multiplier = Configs.SERVER.plotArmor.negative_damageMultiplier;
                if (multiplier > 1.0f) {
                    float extra = amount * (multiplier - 1.0f);
                    player.setHealth(Math.max(player.getHealth() - extra, 0.0f));
                }
            }
        }
    }
}
