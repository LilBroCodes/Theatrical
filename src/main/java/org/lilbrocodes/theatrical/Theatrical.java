package org.lilbrocodes.theatrical;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.lilbrocodes.theatrical.mixin.accessor.PlayerHandHeld;
import org.lilbrocodes.theatrical.mixin.accessor.WalkSpeedHolder;

public class Theatrical implements ModInitializer {
    public static final String MOD_ID = "theatrical";

    public static final Identifier HAND_HELD_PACKET = identify("hand_held_out");
    public static final Identifier WALK_SPEED_PACKET = identify("walk_speed");

    public static final Identifier TAKE_ITEM_PACKET = identify("take_item_c2s");

    @Override
    public void onInitialize() {
        ServerPlayNetworking.registerGlobalReceiver(HAND_HELD_PACKET, (server, player, handler, buf, responseSender) -> {
            boolean held = buf.readBoolean();
            server.execute(() -> ((PlayerHandHeld) player).theatrical$setHandHeldOut(held));
        });

        ServerPlayNetworking.registerGlobalReceiver(WALK_SPEED_PACKET, (server, player, handler, buf, responseSender) -> {
            int value = buf.readInt();
            server.execute(() -> ((WalkSpeedHolder) player).theatrical$setWalkSpeed(value));
        });

        ServerPlayNetworking.registerGlobalReceiver(TAKE_ITEM_PACKET, (server, player, handler, buf, responseSender) -> {
            int targetId = buf.readInt();
            server.execute(() -> {
                Entity targetEntity = player.getWorld().getEntityById(targetId);
                if (targetEntity instanceof PlayerEntity targetPlayer) {
                    PlayerHandHeld targetHolder = (PlayerHandHeld) targetPlayer;
                    PlayerHandHeld requesterHolder = (PlayerHandHeld) player;

                    double maxDistanceSq = 3.0 * 3.0;
                    if (player.squaredDistanceTo(targetPlayer) <= maxDistanceSq
                            && targetHolder.theatrical$isHandHeldOut()
                            && player.getStackInHand(Hand.MAIN_HAND).isEmpty()
                            && !targetPlayer.getStackInHand(Hand.MAIN_HAND).isEmpty()) {
                        ItemStack item = targetPlayer.getStackInHand(Hand.MAIN_HAND);
                        player.setStackInHand(Hand.MAIN_HAND, item);
                        targetPlayer.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
                        player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS);
                    } else {
                        requesterHolder.theatrical$setHandHeldOut(true);
                    }
                } else {
                    ((PlayerHandHeld) player).theatrical$setHandHeldOut(true);
                }
            });
        });
    }

    public static Identifier identify(String name) {
        return new Identifier(MOD_ID, name);
    }
}
