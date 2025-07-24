package org.lilbrocodes.theatrical;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.lilbrocodes.theatrical.cca.TheatricalCardinalComponents;
import org.lilbrocodes.theatrical.commands.CountdownCommand;
import org.lilbrocodes.theatrical.commands.DirectorCommand;
import org.lilbrocodes.theatrical.commands.argument_type.PlayerListArgumentSerializer;
import org.lilbrocodes.theatrical.commands.argument_type.PlayerListArgumentType;
import org.lilbrocodes.theatrical.commands.argument_type.PlotArmorArgumentType;
import org.lilbrocodes.theatrical.config.Configs;
import org.lilbrocodes.theatrical.mixin.accessor.HandHeldDataHolder;
import org.lilbrocodes.theatrical.mixin.accessor.WalkSpeedHolder;

import java.util.ArrayList;
import java.util.List;

public class Theatrical implements ModInitializer {
    public static final String MOD_ID = "theatrical";

    public static final Identifier COUNTDOWNS = identify("countdowns");
    public static final Identifier COUNTDOWN_ATTEMPTS = identify("countdown_attempts");
    public static final Identifier PLAYER_LIST = identify("player_list");
    public static final Identifier PLOT_ARMOR_TYPE = identify("plot_armor_type");
    public static final Identifier DIRECTOR_PACKET = identify("director");
    public static final Identifier PLOT_ARMOR_PACKET = identify("plot_armor");

    /** Packet: tell server whether player is holding out their hand.
     *  <p>Payload: {@code boolean} value.
     */
    public static final Identifier HAND_HELD_PACKET = identify("hand_held_out");

    /** Packet: update walk speed.
     *  <p>Payload: {@code int} speed value.
     */
    public static final Identifier WALK_SPEED_PACKET = identify("walk_speed");

    /** Packet: client requests to take an item.
     *  <p>Payload: {@code int targetId}.
     */
    public static final Identifier TAKE_ITEM_PACKET = identify("take_item_c2s");

    /** Packet: trigger checks to see if starting a countdown is possible.
     *  <p>Payload: {@code int duration}.
     */
    public static final Identifier START_COUNTDOWN_PACKET = identify("start_countdown");

    /**
     * Packet: start a new countdown.
     */
    public static final Identifier COUNTDOWN_PING_PACKET = identify("countdown_ping");

    /** Packet: response from server about countdown request.
     *  <p>Payload: {@code int type}:
     *  <ul>
     *      <li>1 → Success</li>
     *      <li>2 → Checks failed</li>
     *      <li>3 → Player is already in cooldown</li>
     *  </ul>
     */
    public static final Identifier COUNTDOWN_RESPONSE_PACKET = identify("countdown_response");

    @Override
    public void onInitialize() {
        ArgumentTypeRegistry.registerArgumentType(PLAYER_LIST, PlayerListArgumentType.class, PlayerListArgumentSerializer.INSTANCE);
        ArgumentTypeRegistry.registerArgumentType(PLOT_ARMOR_TYPE, PlotArmorArgumentType.class, ConstantArgumentSerializer.of(PlotArmorArgumentType::plotArmor));

        CommandRegistrationCallback.EVENT.register(CountdownCommand::register);
        CommandRegistrationCallback.EVENT.register(DirectorCommand::register);

        Configs.initialize();

        ServerPlayNetworking.registerGlobalReceiver(COUNTDOWN_RESPONSE_PACKET, (server, player, handler, buf, responseSender) -> {
            List<Integer> state = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                state.add(buf.readInt());
            }
            TheatricalCardinalComponents.COUNTDOWN_ATTEMPTS.get(player.getWorld()).updatePlayerState(player.getUuid(), state);
        });

        ServerPlayNetworking.registerGlobalReceiver(HAND_HELD_PACKET, (server, player, handler, buf, responseSender) -> {
            boolean held = buf.readBoolean();
            server.execute(() -> ((HandHeldDataHolder) player).theatrical$setHandHeldOut(held));
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
                    HandHeldDataHolder targetHolder = (HandHeldDataHolder) targetPlayer;
                    HandHeldDataHolder requesterHolder = (HandHeldDataHolder) player;

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
                    ((HandHeldDataHolder) player).theatrical$setHandHeldOut(true);
                }
            });
        });
    }

    public static Identifier identify(String name) {
        return new Identifier(MOD_ID, name);
    }
}
