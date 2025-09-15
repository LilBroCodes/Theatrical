package org.lilbrocodes.theatrical.mixin.impl;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;
import org.lilbrocodes.theatrical.mixin.accessor.HandHeldDataHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityModel.class)
public class PlayerEntityModelMixin<T extends LivingEntity> extends BipedEntityModel<T> {
    public PlayerEntityModelMixin(ModelPart root) {
        super(root);
    }

    @Inject(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
    private void theatrical$rotateHeldHand(
            T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch,
            CallbackInfo ci
    ) {
        if (!(entity instanceof HandHeldDataHolder held)) return;

        if (held.theatrical$isHandHeldOut()) {
            Arm mainArm = entity.getMainArm();
            if (mainArm == Arm.RIGHT) {
                rightArm.pitch = (float) (-Math.PI / 2.0F) * 0.8F;
            } else {
                leftArm.pitch = (float) (-Math.PI / 2.0F) * 0.8F;
            }
        }
    }
}
