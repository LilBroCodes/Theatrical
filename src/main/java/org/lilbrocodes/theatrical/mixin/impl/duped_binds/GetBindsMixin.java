package org.lilbrocodes.theatrical.mixin.impl.duped_binds;

import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(value = GameOptions.class, priority = -1)
public class GetBindsMixin {
    @Shadow @Final public KeyBinding[] allKeys;

    @Inject(method = "load", at = @At("HEAD"))
    private void flowed_combat$getBaseBinds(CallbackInfo ci) {
        org.lilbrocodes.theatrical.util.DupeBinds.MC_TH_BINDS.addAll(Arrays.asList(allKeys));
    }
}
