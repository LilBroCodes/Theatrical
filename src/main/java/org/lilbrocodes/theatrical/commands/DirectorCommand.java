package org.lilbrocodes.theatrical.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.lilbrocodes.theatrical.commands.argument_type.PlotArmorArgumentType;
import org.lilbrocodes.theatrical.mixin.accessor.DirectorDataHolder;
import org.lilbrocodes.theatrical.mixin.accessor.PlotArmorDataHolder;
import org.lilbrocodes.theatrical.util.Misc;
import org.lilbrocodes.theatrical.util.PlotArmorType;
import org.lilbrocodes.theatrical.util.TextUtil;

public class DirectorCommand extends CommandUtil {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("director")
                .then(CommandManager.literal("plot_armor")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .then(CommandManager.argument("type", PlotArmorArgumentType.plotArmor())
                                        .executes(ctx -> {
                                            Misc.checkDirectorOrThrow(ctx);
                                            ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");

                                            if (!(player instanceof PlotArmorDataHolder holder)) {
                                                    feedback(ctx, Text.translatable("command.theatrical.director.plot_armor.player_no_holder"));
                                                    return 0;
                                            }

                                            PlotArmorType type = PlotArmorArgumentType.getPlotArmorType(ctx, "type");
                                            holder.theatrical$setType(type);

                                            feedback(ctx, TextUtil.replaceTranslatable("command.theatrical.director.plot_armor.success", player.getName().getString(), type.asString()));
                                            return 1;
                                        })
                                )
                        )
                ).then(CommandManager.literal("director")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .then(CommandManager.argument("value", BoolArgumentType.bool())
                                        .executes(ctx -> {
                                            Misc.checkDirectorOrThrow(ctx);
                                            ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");

                                            if (!(player instanceof DirectorDataHolder holder)) {
                                                feedback(ctx, Text.translatable("command.theatrical.director.plot_armor.player_no_holder"));
                                                return 0;
                                            }

                                            boolean value = BoolArgumentType.getBool(ctx, "value");
                                            holder.theatrical$setDirector(value);

                                            feedback(ctx,TextUtil.replaceTranslatable( value ? "command.theatrical.director.director.turned_on" : "command.theatrical.director.director.turned_off", player.getName().getString()));
                                            return 1;
                                        })
                                )
                        )
                ).requires(Misc::isDirector)
        );
    }
}
