package org.lilbrocodes.theatrical.cca.entity;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.lilbrocodes.theatrical.cca.TheatricalCardinalComponents;

public class LockedComponent implements AutoSyncedComponent {
    private final PlayerEntity player;
    private boolean locked = false;

    public LockedComponent(PlayerEntity player) {
        this.player = player;
    }

    public void sync() {
        TheatricalCardinalComponents.LOCKED.sync(player);
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
        sync();
    }

    public boolean isLocked() {
        return locked;
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound) {
        this.locked = nbtCompound.getBoolean("locked");
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound) {
        nbtCompound.putBoolean("locked", locked);
    }
}
