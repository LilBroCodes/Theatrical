package org.lilbrocodes.theatrical.cca.entity;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.lilbrocodes.theatrical.cca.TheatricalCardinalComponents;
import org.lilbrocodes.theatrical.util.PlotArmorType;

public class PlotArmorComponent implements AutoSyncedComponent {
    private final PlayerEntity player;
    private PlotArmorType plotArmorType = PlotArmorType.NONE;

    public PlotArmorComponent(PlayerEntity player) {
        this.player = player;
    }

    public void sync() {
        TheatricalCardinalComponents.PLOT_ARMOR_TYPE.sync(player);
    }

    public void setType(PlotArmorType plotArmorType) {
        this.plotArmorType = plotArmorType;
        sync();
    }

    public PlotArmorType getType() {
        return plotArmorType;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        String typeString = tag.getString("type");
        for (PlotArmorType type : PlotArmorType.values()) {
            if (type.asString().equals(typeString)) {
                this.plotArmorType = type;
                return;
            }
        }
        this.plotArmorType = PlotArmorType.NONE;
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putString("type", plotArmorType.asString());
    }
}
