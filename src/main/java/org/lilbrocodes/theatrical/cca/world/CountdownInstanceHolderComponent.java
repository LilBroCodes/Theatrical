package org.lilbrocodes.theatrical.cca.world;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.lilbrocodes.theatrical.Theatrical;
import org.lilbrocodes.theatrical.cca.TheatricalCardinalComponents;
import org.lilbrocodes.theatrical.util.CountdownInstance;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class CountdownInstanceHolderComponent implements ServerTickingComponent, AutoSyncedComponent {
    private final World world;
    private final List<CountdownInstance> holder = new ArrayList<>();

    public CountdownInstanceHolderComponent(World world) {
        this.world = world;
    }

    public List<CountdownInstance> getHolder() {
        return holder;
    }

    private void sync() {
        TheatricalCardinalComponents.COUNTDOWNS.sync(world);
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag) {
        NbtList countdowns = new NbtList();
        for (CountdownInstance countdown : holder) {
            countdowns.add(countdown.writeToNbt(new NbtCompound()));
        }
        tag.put("countdowns", countdowns);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        holder.clear();
        NbtList countdownTagList = tag.getList("countdowns", NbtElement.COMPOUND_TYPE);
        for (NbtElement element : countdownTagList) {
            holder.add(CountdownInstance.readFromNbt((NbtCompound) element));
        }
    }

    public void create(List<UUID> players, int duration) {
        holder.add(new CountdownInstance(players, duration));
        players.forEach(uuid -> {
            PlayerEntity player = world.getPlayerByUuid(uuid);
            if (player != null) {
                ServerPlayNetworking.send((ServerPlayerEntity) player, Theatrical.START_COUNTDOWN_PACKET, new PacketByteBuf(Unpooled.buffer().writeInt(duration)));
            }
        });
        sync();
    }

    @Override
    public void serverTick() {
        Iterator<CountdownInstance> iterator = holder.iterator();
        while (iterator.hasNext()) {
            CountdownInstance countdown = iterator.next();
            if (countdown.tick()) {
                TheatricalCardinalComponents.SCENES.get(world).tryStart(countdown.players);
                iterator.remove();
            }
        }
        sync();
    }
}
