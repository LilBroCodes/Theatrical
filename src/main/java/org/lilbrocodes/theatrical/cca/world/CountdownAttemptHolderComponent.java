package org.lilbrocodes.theatrical.cca.world;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.lilbrocodes.theatrical.Theatrical;
import org.lilbrocodes.theatrical.cca.TheatricalCardinalComponents;
import org.lilbrocodes.theatrical.util.CountdownAttempt;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CountdownAttemptHolderComponent implements ServerTickingComponent, AutoSyncedComponent {
    private final World world;
    private final List<CountdownAttempt> holder = new ArrayList<>();

    public CountdownAttemptHolderComponent(World world) {
        this.world = world;
    }

    private void sync() {
        TheatricalCardinalComponents.COUNTDOWN_ATTEMPTS.sync(world);
    }

    public void updatePlayerState(UUID uuid, List<Integer> state) {
        for (CountdownAttempt attempt : holder) {
            attempt.setState(uuid, state);
        }
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag) {
        NbtList countdowns = new NbtList();
        for (CountdownAttempt attempt : holder) {
            countdowns.add(attempt.writeToNbt(new NbtCompound()));
        }
        tag.put("countdowns", countdowns);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        holder.clear();
        NbtList countdownTagList = tag.getList("countdowns", NbtElement.COMPOUND_TYPE);
        for (NbtElement element : countdownTagList) {
            holder.add(CountdownAttempt.readFromNbt((NbtCompound) element));
        }
    }

    public void create(UUID owner, List<UUID> players, int duration) {
        holder.add(new CountdownAttempt(owner, players, duration, null));
        players.forEach(uuid -> ServerPlayNetworking.send((ServerPlayerEntity) world.getPlayerByUuid(uuid), Theatrical.COUNTDOWN_PING_PACKET, new PacketByteBuf(Unpooled.buffer())));
        sync();
    }

    @Override
    public void serverTick() {
        holder.removeIf(attempt -> attempt.shouldRemove(world));
        sync();
    }
}
