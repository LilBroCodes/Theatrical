package org.lilbrocodes.theatrical.client.events;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.lilbrocodes.composer_reloaded.api.events.ScrollEvents.ScrollAction;
import org.lilbrocodes.theatrical.client.TheatricalClient;
import org.lilbrocodes.theatrical.config.Configs;
import org.lilbrocodes.theatrical.mixin.accessor.WalkSpeedHolder;
import org.lilbrocodes.theatrical.util.Misc;

public class ScrollEvent implements ScrollAction {
    @Override
    public boolean onScroll(MinecraftClient minecraftClient, @Nullable ClientWorld clientWorld, @Nullable ClientPlayerEntity clientPlayerEntity, double v) {
        if (TheatricalClient.WALK_SPEED_TRIGGER.isPressed() && MinecraftClient.getInstance().player instanceof WalkSpeedHolder walkSpeedHolder) {
            int i = (int) Math.signum(v);
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

            return true;
        }
        return false;
    }
}
