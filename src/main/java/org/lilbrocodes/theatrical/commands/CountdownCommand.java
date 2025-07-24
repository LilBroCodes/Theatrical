package org.lilbrocodes.theatrical.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.lilbrocodes.theatrical.cca.TheatricalCardinalComponents;
import org.lilbrocodes.theatrical.commands.argument_type.PlayerListArgumentType;
import org.lilbrocodes.theatrical.util.Misc;
import org.lilbrocodes.theatrical.util.TextUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CountdownCommand extends CommandUtil {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("countdown")
                .then(CommandManager.literal("radius")
                        .then(CommandManager.argument("radius", IntegerArgumentType.integer(0, 128))
                                .then(CommandManager.argument("duration", IntegerArgumentType.integer(1))
                                        .executes(ctx -> {
                                            ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                                            int radius = ctx.getArgument("radius", Integer.class);
                                            int duration = ctx.getArgument("duration", Integer.class);

                                            List<UUID> playerIds = player.getWorld()
                                                    .getEntitiesByClass(PlayerEntity.class,
                                                            player.getBoundingBox().expand(radius),
                                                            player1 -> true)
                                                    .stream()
                                                    .map(PlayerEntity::getUuid)
                                                    .collect(Collectors.toList());

                                            TheatricalCardinalComponents.COUNTDOWN_ATTEMPTS
                                                    .get(player.getWorld())
                                                    .create(player.getUuid(), playerIds, duration * 20);

                                            feedback(ctx, TextUtil.replaceTranslatable("command.theatrical.countdown.success", duration, playerIds.size(), Misc.getQuantifier(playerIds)));
                                            return 1;
                                        })
                                )
                        )
                ).then(CommandManager.literal("players")
                        .then(CommandManager.argument("duration", IntegerArgumentType.integer(0, 128))
                                .then(CommandManager.argument("targets", PlayerListArgumentType.noDuplicates())
                                        .executes(ctx -> {
                                            int duration = ctx.getArgument("duration", Integer.class);
                                            List<String> playerNames = ctx.getArgument("targets", List.class);

                                            ServerCommandSource source = ctx.getSource();
                                            List<UUID> uuids = new ArrayList<>();
                                            for (String name : playerNames) {
                                                ServerPlayerEntity target = source.getServer().getPlayerManager().getPlayer(name);
                                                if (target != null) {
                                                    uuids.add(target.getUuid());
                                                }
                                            }

                                            ServerPlayerEntity player = source.getPlayerOrThrow();
                                            TheatricalCardinalComponents.COUNTDOWN_ATTEMPTS
                                                    .get(player.getWorld())
                                                    .create(player.getUuid(), uuids, duration * 20);

                                            feedback(ctx, TextUtil.replaceTranslatable("command.theatrical.countdown.success", duration, uuids.size(), Misc.getQuantifier(uuids)));
                                            return 1;
                                        })
                                )
                        )
                ).then(CommandManager.literal("selector")
                        .then(CommandManager.argument("duration", IntegerArgumentType.integer(0, 128))
                                .then(CommandManager.argument("targets", EntityArgumentType.players())
                                        .executes(ctx -> {
                                            int duration = ctx.getArgument("duration", Integer.class);
                                            Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(ctx, "targets");

                                            List<UUID> uniqueUuids = players.stream()
                                                    .map(ServerPlayerEntity::getUuid)
                                                    .distinct()
                                                    .collect(Collectors.toList());

                                            ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                                            TheatricalCardinalComponents.COUNTDOWN_ATTEMPTS
                                                    .get(player.getWorld())
                                                    .create(player.getUuid(), uniqueUuids, duration * 20);

                                            feedback(ctx, TextUtil.replaceTranslatable("command.theatrical.countdown.success", duration, uniqueUuids.size(), Misc.getQuantifier(uniqueUuids)));
                                            return 1;
                                        })
                                )
                        )
                ).requires(Misc::isDirector)
        );
    }
}
