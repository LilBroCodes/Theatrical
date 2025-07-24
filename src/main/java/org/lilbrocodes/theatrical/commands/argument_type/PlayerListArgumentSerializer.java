package org.lilbrocodes.theatrical.commands.argument_type;

import com.google.gson.JsonObject;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;

public class PlayerListArgumentSerializer implements ArgumentSerializer<PlayerListArgumentType, PlayerListArgumentProperties> {
    public static final PlayerListArgumentSerializer INSTANCE = new PlayerListArgumentSerializer();

    @Override
    public void writePacket(PlayerListArgumentProperties properties, PacketByteBuf buf) {
        buf.writeBoolean(properties.allowsDuplicates());
    }

    @Override
    public PlayerListArgumentProperties fromPacket(PacketByteBuf buf) {
        boolean allowDuplicates = buf.readBoolean();
        return new PlayerListArgumentProperties(allowDuplicates);
    }

    @Override
    public void writeJson(PlayerListArgumentProperties properties, JsonObject json) {
        json.addProperty("allowDuplicates", properties.allowsDuplicates());
    }

    @Override
    public PlayerListArgumentProperties getArgumentTypeProperties(PlayerListArgumentType argumentType) {
        return new PlayerListArgumentProperties(argumentType.allowsDuplicates());
    }
}