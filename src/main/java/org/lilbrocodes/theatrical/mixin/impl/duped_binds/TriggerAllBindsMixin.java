package org.lilbrocodes.theatrical.mixin.impl.duped_binds;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lilbrocodes.theatrical.util.DupeBinds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBinding.class)
public class TriggerAllBindsMixin {
    @Inject(method = "onKeyPressed", at = @At("TAIL"))
    private static void flowed_combat$triggerOtherBinds(InputUtil.Key key, CallbackInfo ci, @Local KeyBinding keyBinding) {
        if (DupeBinds.bindAllowed(keyBinding)) {
            for (KeyBinding keyBinding2 : MinecraftClient.getInstance().options.allKeys) {
                if (keyBinding != keyBinding2 && keyBinding.equals(keyBinding2) && DupeBinds.bindAllowed(keyBinding2)) keyBinding2.timesPressed++;
            }
        }
    }

    @Inject(method = "setKeyPressed", at = @At("TAIL"))
    private static void flowed_combat$toggleOtherBinds(InputUtil.Key key, boolean pressed, CallbackInfo ci, @Local KeyBinding keyBinding) {
        if (DupeBinds.bindAllowed(keyBinding)) {
            for (KeyBinding keyBinding2 : MinecraftClient.getInstance().options.allKeys) {
                if (keyBinding != keyBinding2 && keyBinding.equals(keyBinding2) && DupeBinds.bindAllowed(keyBinding2)) keyBinding2.setPressed(pressed);
            }
        }
    }
}
