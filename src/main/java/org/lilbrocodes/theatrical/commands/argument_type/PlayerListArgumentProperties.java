package org.lilbrocodes.theatrical.commands.argument_type;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.serialize.ArgumentSerializer;

public class PlayerListArgumentProperties implements ArgumentSerializer.ArgumentTypeProperties<PlayerListArgumentType> {
    private final boolean allowDuplicates;

    public PlayerListArgumentProperties(boolean allowDuplicates) {
        this.allowDuplicates = allowDuplicates;
    }

    @Override
    public PlayerListArgumentType createType(CommandRegistryAccess registryAccess) {
        return new PlayerListArgumentType(allowDuplicates);
    }

    @Override
    public ArgumentSerializer<PlayerListArgumentType, ?> getSerializer() {
        return PlayerListArgumentSerializer.INSTANCE;
    }

    public boolean allowsDuplicates() {
        return allowDuplicates;
    }
}
