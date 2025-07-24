package org.lilbrocodes.theatrical.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import org.lilbrocodes.composer_reloaded.api.toast.ToastManager;
import org.lilbrocodes.theatrical.Theatrical;
import org.lilbrocodes.theatrical.cca.TheatricalCardinalComponents;
import org.lilbrocodes.theatrical.cca.world.CountdownInstanceHolderComponent;
import org.lilbrocodes.theatrical.client.toast.CountdownToast;
import org.lilbrocodes.theatrical.integrations.CooldownChecks;
import org.lilbrocodes.theatrical.mixin.accessor.HandHeldDataHolder;
import org.lilbrocodes.theatrical.util.CountdownInstance;
import org.lwjgl.glfw.GLFW;

import io.netty.buffer.Unpooled;

import java.util.ArrayList;
import java.util.List;

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

    private void countdownFeedback(List<Integer> state) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        state.forEach(buf::writeInt);
        ClientPlayNetworking.send(Theatrical.COUNTDOWN_RESPONSE_PACKET, buf);
    }

    @Override
    public void onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(HAND_HOLD_BIND);
        KeyBindingHelper.registerKeyBinding(WALK_SPEED_TRIGGER);
        KeyBindingHelper.registerKeyBinding(WALK_SPEED_MODIFIER);


        ClientPlayNetworking.registerGlobalReceiver(Theatrical.COUNTDOWN_PING_PACKET, (client, handler, buf, sender) -> {
           List<Integer> checksPassed = new ArrayList<>(List.of(1));

           if (client.world != null && client.player != null) {
               CountdownInstanceHolderComponent holder = TheatricalCardinalComponents.COUNTDOWNS.get(client.world);

               for (CountdownInstance instance : holder.getHolder()) {
                   if (instance.players.contains(client.player.getUuid())) {
                       checksPassed.set(0, 0);
                   }
               }
           }

           checksPassed.addAll(CooldownChecks.runChecks());
           countdownFeedback(checksPassed);
        });
        ClientPlayNetworking.registerGlobalReceiver(Theatrical.START_COUNTDOWN_PACKET, (client, handler, buf, sender) -> {
            CooldownChecks.fixChecks();
            ToastManager.getInstance().addToast(new CountdownToast(buf.readInt()), ToastManager.Corner.TOP_RIGHT);
        });

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                 HandHeldDataHolder holder = (HandHeldDataHolder) client.player;

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
