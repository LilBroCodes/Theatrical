package org.lilbrocodes.theatrical.cca.entity;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.lilbrocodes.theatrical.cca.TheatricalCardinalComponents;

public class WalkSpeedComponent implements AutoSyncedComponent {
    private final PlayerEntity player;
    private int value = 100;

    private void sync() {
        TheatricalCardinalComponents.WALK_SPEED.sync(player);
    }

    public void setValue(int value) {
        this.value = value;
        sync();
    }

    public int getValue() {
        return value;
    }

    public WalkSpeedComponent(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound) {
        value = nbtCompound.getInt("value");
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound) {
        nbtCompound.putInt("value", value);
    }
}
