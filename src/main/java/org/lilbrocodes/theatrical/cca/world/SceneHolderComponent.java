package org.lilbrocodes.theatrical.cca.world;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.lilbrocodes.theatrical.cca.TheatricalCardinalComponents;
import org.lilbrocodes.theatrical.scene.Scene;

import java.util.*;

public class SceneHolderComponent implements AutoSyncedComponent {
    public Map<String, Scene> scenes;
    public final World world;

    public SceneHolderComponent(World world) {
        this.world = world;
        scenes = new HashMap<>();
    }

    public void sync() {
        TheatricalCardinalComponents.SCENES.sync(world);
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag) {
        scenes.clear();
        NbtList sceneTag = tag.getList("scenes", NbtElement.COMPOUND_TYPE);
        sceneTag.forEach(nbtElement -> {
            if (nbtElement instanceof NbtCompound nbt) {
                String name = nbt.getString("name");
                Scene scene = Scene.readFromNbt(world, nbt.getCompound("scene"));
                scenes.put(name, scene);
            };
        });
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag) {
        NbtList sceneTag = new NbtList();
        scenes.forEach((name, scene) -> {
            NbtCompound nbt = new NbtCompound();
            nbt.putString("name", name);
            nbt.put("scene", scene.writeToNbt(new NbtCompound()));
            sceneTag.add(nbt);
        });
        tag.put("scenes", sceneTag);
    }

    public void tryStart(List<UUID> players) {
        scenes.values().forEach(scene -> {
            if (scene.actors().size() == players.size() && new HashSet<>(players).containsAll(scene.actors())) {
                scene.start();
            }
        });
        sync();
    }
}
