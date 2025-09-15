package org.lilbrocodes.theatrical.cca.entity;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import org.lilbrocodes.theatrical.cca.TheatricalCardinalComponents;
import org.lilbrocodes.theatrical.scene.Scene;

import java.util.ArrayList;
import java.util.List;

public class VisualisedScenesComponent implements AutoSyncedComponent {
    private final PlayerEntity player;
    private final List<String> visualisedScenes = new ArrayList<>();

    public VisualisedScenesComponent(PlayerEntity player) {
        this.player = player;
    }

    public void sync() {
        TheatricalCardinalComponents.VISUALISED_SCENES.sync(player);
    }

    public void visualise(String scene) {
        this.visualisedScenes.add(scene);
        sync();
    }

    public boolean isVisualising(String scene) {
        return visualisedScenes.contains(scene);
    }

    public void stopVisualisingScene(String scene) {
        visualisedScenes.remove(scene);
        sync();
    }

    public List<Scene> getScenes() {
        List<Scene> scenes = new ArrayList<>();
        TheatricalCardinalComponents.SCENES.get(player.getWorld()).scenes.forEach((k, v) -> {
            if (isVisualising(k)) scenes.add(v);
        });
        return scenes;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        visualisedScenes.clear();
        NbtList scenes = tag.getList("scenes", NbtElement.STRING_TYPE);
        scenes.forEach(t -> {
            if (t instanceof NbtString string) visualisedScenes.add(string.asString());
        });
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        NbtList scenes = new NbtList();
        visualisedScenes.forEach(scene -> {
            scenes.add(NbtString.of(scene));
        });
        tag.put("scenes", scenes);
    }
}
