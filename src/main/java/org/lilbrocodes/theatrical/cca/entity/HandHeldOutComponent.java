package org.lilbrocodes.theatrical.cca.entity;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.lilbrocodes.theatrical.cca.TheatricalCardinalComponents;

public class HandHeldOutComponent implements AutoSyncedComponent {
    private final PlayerEntity player;
    private boolean held = false;

    public HandHeldOutComponent(PlayerEntity player) {
        this.player = player;
    }

    public void sync() {
        TheatricalCardinalComponents.HAND_HELD_OUT.sync(player);
    }

    public void setHeld(boolean held) {
        this.held = held;
        sync();
    }

    public boolean isHeld() {
        return held;
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound) {
        this.held = nbtCompound.getBoolean("held");
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound) {
        nbtCompound.putBoolean("held", held);
    }
}
