package org.lilbrocodes.theatrical.client;

import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import org.lilbrocodes.theatrical.Theatrical;
import org.lilbrocodes.theatrical.config.TheatricalConfig;
import org.lilbrocodes.theatrical.mixin.accessor.PlayerHandHeld;
import org.lwjgl.glfw.GLFW;

import io.netty.buffer.Unpooled;

public class TheatricalClient implements ClientModInitializer {
    public static KeyBinding HAND_HOLD_BIND = new KeyBinding(
            "key.theatrical.hand_hold",
            GLFW.GLFW_KEY_G,
            "category.theatrical"
    );

    public static KeyBinding WALK_SPEED_TRIGGER = new KeyBinding(
            "key.theatrical.walk_speed_trigger",
            GLFW.GLFW_KEY_LEFT_ALT,
            "category.theatrical"
    );

    public static KeyBinding WALK_SPEED_MODIFIER = new KeyBinding(
            "key.theatrical.walk_speed_modifier",
            GLFW.GLFW_KEY_LEFT_SHIFT,
            "category.theatrical"
    );

    @Override
    public void onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(HAND_HOLD_BIND);
        KeyBindingHelper.registerKeyBinding(WALK_SPEED_TRIGGER);
        KeyBindingHelper.registerKeyBinding(WALK_SPEED_MODIFIER);
        MidnightConfig.init(Theatrical.MOD_ID, TheatricalConfig.class);

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                PlayerHandHeld holder = (PlayerHandHeld) client.player;

                if (HAND_HOLD_BIND.isPressed()) {
                    if (HAND_HOLD_BIND.wasPressed() && !holder.theatrical$isHandHeldOut()) {
                        Entity targetEntity = client.targetedEntity;
                        if (targetEntity instanceof PlayerEntity targetPlayer) {
                            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                            buf.writeInt(targetPlayer.getId());
                            ClientPlayNetworking.send(Theatrical.TAKE_ITEM_PACKET, buf);
                        } else {
                            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                            buf.writeBoolean(true);
                            ClientPlayNetworking.send(Theatrical.HAND_HELD_PACKET, buf);
                        }
                    }
                } else {
                    if (holder.theatrical$isHandHeldOut()) {
                        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                        buf.writeBoolean(false);
                        ClientPlayNetworking.send(Theatrical.HAND_HELD_PACKET, buf);
                    }
                }
            }
        });
    }
}
