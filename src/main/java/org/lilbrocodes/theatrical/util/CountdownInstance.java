package org.lilbrocodes.theatrical.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLongArray;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CountdownInstance {
    public final List<UUID> players;
    public final int duration;
    public int progress;

    public CountdownInstance(List<UUID> players, int progress, int duration) {
        this.players = players;
        this.progress = progress;
        this.duration = duration;
    }

    public CountdownInstance(List<UUID> players, int duration) {
        this(players, 0, duration);
    }

    public NbtCompound writeToNbt(NbtCompound tag) {
        NbtList playerList = new NbtList();
        for (UUID uuid : players) {
            playerList.add(new NbtLongArray(new long[]{ uuid.getMostSignificantBits(), uuid.getLeastSignificantBits() }));
        }
        tag.put("players", playerList);
        tag.putInt("progress", progress);
        tag.putInt("duration", duration);
        return tag;
    }


    public static CountdownInstance readFromNbt(NbtCompound tag) {
        List<UUID> players = new ArrayList<>();
        NbtList playerList = tag.getList("players", NbtLongArray.LONG_ARRAY_TYPE);
        for (NbtElement nbtElement : playerList) {
            NbtLongArray uuidArray = (NbtLongArray) nbtElement;
            long[] uuidLongs = uuidArray.getLongArray();
            if (uuidLongs.length == 2) {
                UUID uuid = new UUID(uuidLongs[0], uuidLongs[1]);
                players.add(uuid);
            }
        }
        int progress = tag.getInt("progress");
        int duration = tag.getInt("duration");
        return new CountdownInstance(players, progress, duration);
    }

    public boolean tick() {
        progress++;
        if (progress >= duration) {
            return true;
        }

        if (progress % 20 == 0) {
            int secondsLeft = (duration - progress) / 20;
        }
        return false;
    }
}
