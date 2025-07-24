package org.lilbrocodes.theatrical.cca.entity;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.lilbrocodes.theatrical.cca.TheatricalCardinalComponents;

public class DirectorComponent implements AutoSyncedComponent {
    private final PlayerEntity player;
    private boolean isDirector = false;

    public DirectorComponent(PlayerEntity player) {
        this.player = player;
    }

    public void sync() {
        TheatricalCardinalComponents.HAND_HELD_OUT.sync(player);
    }

    public void setDirector(boolean director) {
        this.isDirector = director;
        sync();
    }

    public boolean isDirector() {
        return isDirector;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.isDirector = tag.getBoolean("director");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putBoolean("director", isDirector);
    }
}
