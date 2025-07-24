package org.lilbrocodes.theatrical.commands.argument_type;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.EnumArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import org.lilbrocodes.theatrical.util.PlotArmorType;

public class PlotArmorArgumentType extends EnumArgumentType<PlotArmorType> {
    private PlotArmorArgumentType() {
        super(PlotArmorType.CODEC, PlotArmorType::values);
    }

    public static EnumArgumentType<PlotArmorType> plotArmor() {
        return new PlotArmorArgumentType();
    }

    public static PlotArmorType getPlotArmorType(CommandContext<ServerCommandSource> ctx, String id) {
        return ctx.getArgument(id, PlotArmorType.class);
    }
}
