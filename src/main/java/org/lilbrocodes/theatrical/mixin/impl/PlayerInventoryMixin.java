package org.lilbrocodes.theatrical.mixin.impl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lilbrocodes.theatrical.client.TheatricalClient;
import org.lilbrocodes.theatrical.config.Configs;
import org.lilbrocodes.theatrical.mixin.accessor.WalkSpeedHolder;
import org.lilbrocodes.theatrical.util.Misc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {
    @Inject(method = "scrollInHotbar", at = @At("HEAD"), cancellable = true)
    public void theatrical$changeWalkSpeed(double scrollAmount, CallbackInfo ci) {
        if (TheatricalClient.WALK_SPEED_TRIGGER.isPressed() && MinecraftClient.getInstance().player instanceof WalkSpeedHolder walkSpeedHolder) {
            ci.cancel();

            int i = (int) Math.signum(scrollAmount);
            int value = TheatricalClient.WALK_SPEED_MODIFIER.isPressed() ? 5 : 1;
            walkSpeedHolder.theatrical$setWalkSpeed(MathHelper.clamp(walkSpeedHolder.theatrical$getWalkSpeed() + (i * value), 0, 100));

            if (Configs.CLIENT.showWalkSpeedChangeMessage) {
                MinecraftClient.getInstance().inGameHud.setOverlayMessage(
                        Text.literal(String.format(Text.translatable("theatrical.gui.walk_speed_message").getString(), walkSpeedHolder.theatrical$getWalkSpeed()))
                                .styled(style -> style.withColor(Misc.mixSpeedColor(walkSpeedHolder.theatrical$getWalkSpeed()))),
                        false
                );

                MinecraftClient.getInstance().player.playSound(
                        SoundEvents.UI_BUTTON_CLICK.value(),
                        SoundCategory.PLAYERS,
                        0.5F,
                        walkSpeedHolder.theatrical$getWalkSpeed() / 100.0F * 2.0F
                );
            }
        }
    }
}
