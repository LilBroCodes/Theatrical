package org.lilbrocodes.theatrical.mixin.impl;

import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;
import org.lilbrocodes.theatrical.mixin.accessor.HandHeldDataHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unchecked")
@Mixin(BipedEntityModel.class)
public class BipedEntityModelMixin<T extends LivingEntity> {

    @Inject(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
    private void theatrical$rotateHeldHand(
            T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch,
            CallbackInfo ci
    ) {
        if (!(entity instanceof HandHeldDataHolder held)) return;

        if (held.theatrical$isHandHeldOut()) {
            BipedEntityModel<T> model = (BipedEntityModel<T>) (Object) this;

            Arm mainArm = entity.getMainArm();
            if (mainArm == Arm.RIGHT) {
                model.rightArm.pitch = (float)(-Math.PI / 2.0F) * 0.8F;
            } else {
                model.leftArm.pitch = (float)(-Math.PI / 2.0F) * 0.8F;
            }
        }
    }
}
