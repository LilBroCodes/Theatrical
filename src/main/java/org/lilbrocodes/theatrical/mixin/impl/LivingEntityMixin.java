package org.lilbrocodes.theatrical.mixin.impl;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import org.lilbrocodes.theatrical.config.Configs;
import org.lilbrocodes.theatrical.mixin.accessor.PlotArmorDataHolder;
import org.lilbrocodes.theatrical.util.PlotArmorType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "heal", at = @At("HEAD"), cancellable = true)
    private void theatrical$adjustHeal(float amount, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof PlayerEntity player && !player.getWorld().isClient) {
            PlotArmorType type = ((PlotArmorDataHolder) player).theatrical$getType();

            if (type == PlotArmorType.NEGATIVE) {
                float multiplier = Configs.SERVER.plotArmor.negative_regenMultiplier;
                if (multiplier < 1.0f && multiplier > 0.0f) {
                    amount *= multiplier;
                }
            }

            if (type == PlotArmorType.POSITIVE) {
                float extra = Configs.SERVER.plotArmor.positive_regenAmount / 20f;
                amount += extra;
            }

            if (amount <= 0.0f) {
                ci.cancel();
            } else {
                self.setHealth(Math.min(self.getHealth() + amount, self.getMaxHealth()));
                ci.cancel();
            }
        }
    }
}
