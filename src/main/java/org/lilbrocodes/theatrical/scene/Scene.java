package org.lilbrocodes.theatrical.scene;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lilbrocodes.theatrical.cca.TheatricalCardinalComponents;
import org.lilbrocodes.theatrical.mixin.accessor.LockedDataHolder;

import java.util.*;

public class Scene {
    public record ActorData(Vec3d pos, float yaw, float pitch) {}
    public record ActorState(NbtCompound data) {}
    public record WorldState(long time, boolean raining, boolean thundering) {
        public NbtCompound writeNbt(NbtCompound tag) {
            tag.putLong("Time", time);
            tag.putBoolean("Raining", raining);
            tag.putBoolean("Thundering", thundering);
            return tag;
        }

        public static WorldState readNbt(NbtCompound tag) {
            return new WorldState(
                    tag.getLong("Time"),
                    tag.getBoolean("Raining"),
                    tag.getBoolean("Thundering")
            );
        }
    }

    private final UUID director;
    private final World world;
    public final Map<UUID, ActorData> actors;
    private final Map<UUID, ActorState> states = new HashMap<>();
    private WorldState worldState;
    private final List<UUID> trusted;
    private boolean lock;
    private boolean loadStates;
    private boolean loadWorldState;
    private boolean setup;

    public Scene(World world, UUID director, Map<UUID, ActorData> actors, List<UUID> trusted, WorldState state, boolean lock, boolean loadStates, boolean loadWorldState, boolean setup) {
        this.director = director;
        this.world = world;
        this.actors = actors;
        this.trusted = trusted;
        this.lock = lock;
        this.loadStates = loadStates;
        this.loadWorldState = loadWorldState;
        this.setup = setup;
    }

    public Scene(World world, UUID director, boolean lock, boolean loadStates, boolean loadWorldState) {
        this(world, director, new HashMap<>(), new ArrayList<>(), null, lock, loadStates, loadWorldState, false);
    }

    /** Adds a new actor if not already present. */
    public boolean add(UUID id, Vec3d pos, float yaw, float pitch) {
        if (actors.containsKey(id)) return false;
        actors.put(id, new ActorData(pos, yaw, pitch));
        sync();
        return true;
    }

    public boolean add(PlayerEntity player) {
        return add(player.getUuid(), player.getPos(), player.getYaw(), player.getPitch());
    }

    /** Moves (or adds if missing) an actor’s position/rotation. */
    public void move(UUID id, Vec3d pos, float yaw, float pitch) {
        actors.put(id, new ActorData(pos, yaw, pitch));
        sync();
    }

    public void remove(UUID id) {
        actors.remove(id);
        sync();
    }

    public boolean hasPermission(UUID id) {
        return director.equals(id) || trusted.contains(id);
    }

    public void setLock(boolean lock) {
        this.lock = lock;
        sync();
    }

    public void setLoadStates(boolean enabled) {
        loadStates = enabled;
        sync();
    }

    public List<UUID> actors() {
        return actors.keySet().stream().toList();
    }

    public void sync() {
        TheatricalCardinalComponents.SCENES.sync(world);
    }

    public boolean toggleTrust(PlayerEntity player) {
        UUID id = player.getUuid();
        if (trusted.contains(id)) {
            trusted.remove(id);
            sync();
            return false;
        } else {
            trusted.add(id);
            sync();
            return true;
        }
    }

    public void saveWorldState() {
        worldState = new WorldState(
                world.getTimeOfDay(),
                world.isRaining(),
                world.isThundering()
        );
        sync();
    }

    public void loadWorldState() {
        if (world instanceof ServerWorld sw && worldState != null) {
            sw.setTimeOfDay(worldState.time);
            sw.setWeather(0, 60 * 30 * 20, worldState.raining, worldState.thundering);
        }
        sync();
    }

    public void saveState(PlayerEntity player) {
        NbtCompound tag = new NbtCompound();
        player.writeNbt(tag);
        states.put(player.getUuid(), new ActorState(tag));
        sync();
    }

    public void restoreState(PlayerEntity player) {
        ActorState state = states.get(player.getUuid());
        if (state != null) {
            player.readNbt(state.data().copy());
        }
        sync();
    }

    public ActorState getState(UUID actorId) {
        return states.get(actorId);
    }

    public void setup() {
        world.getPlayers().forEach(player -> {
            ActorData data = actors.get(player.getUuid());
            if (data != null && player instanceof LockedDataHolder holder) {
                player.teleport((ServerWorld) world, data.pos.x, data.pos.y, data.pos.z,
                        EnumSet.noneOf(PositionFlag.class), data.yaw, data.pitch);
                if (loadStates) restoreState(player);
                holder.theatrical$setLocked(lock);
            }
        });
        if (loadWorldState) loadWorldState();
        setup = true;
    }

    public void start() {
        if (setup) {
            setup = false;
            world.getPlayers().forEach(player -> {
                if (actors.containsKey(player.getUuid()) && player instanceof LockedDataHolder holder) {
                    holder.theatrical$setLocked(false);
                }
            });
        }
    }

    public NbtCompound writeToNbt(NbtCompound tag) {
        tag.putUuid("director", director);
        tag.putBoolean("lock", lock);
        tag.putBoolean("setup", setup);

        NbtList actorTag = new NbtList();
        actors.forEach((id, data) -> {
            NbtCompound t = new NbtCompound();
            t.putUuid("id", id);
            t.putDouble("x", data.pos().x);
            t.putDouble("y", data.pos().y);
            t.putDouble("z", data.pos().z);
            t.putFloat("yaw", data.yaw());
            t.putFloat("pitch", data.pitch());
            actorTag.add(t);
        });
        tag.put("actors", actorTag);

        NbtList trustedTag = new NbtList();
        trusted.forEach(id -> {
            NbtCompound c = new NbtCompound();
            c.putUuid("id", id);
            trustedTag.add(c);
        });
        tag.put("trusted", trustedTag);

        NbtList stateTag = new NbtList();
        states.forEach((id, state) -> {
            NbtCompound t = new NbtCompound();
            t.putUuid("id", id);
            t.put("data", state.data());
            stateTag.add(t);
        });
        tag.put("states", stateTag);
        tag.putBoolean("loadStates", loadStates);
        tag.putBoolean("loadWorldState", loadWorldState);

        tag.put("worldState", worldState == null ? new NbtCompound() : worldState.writeNbt(new NbtCompound()));

        return tag;
    }

    public static Scene readFromNbt(World world, NbtCompound tag) {
        UUID director = tag.getUuid("director");
        boolean lock = tag.getBoolean("lock");
        boolean setup = tag.getBoolean("setup");

        NbtList actorTag = tag.getList("actors", NbtElement.COMPOUND_TYPE);
        Map<UUID, ActorData> actors = new HashMap<>();

        actorTag.forEach(t -> {
            if (t instanceof NbtCompound nbt) {
                UUID id = nbt.getUuid("id");
                Vec3d pos = new Vec3d(
                        nbt.getDouble("x"),
                        nbt.getDouble("y"),
                        nbt.getDouble("z")
                );
                float yaw = nbt.getFloat("yaw");
                float pitch = nbt.getFloat("pitch");
                actors.put(id, new ActorData(pos, yaw, pitch));
            }
        });

        NbtList trustedTag = tag.getList("trusted", NbtElement.COMPOUND_TYPE);
        List<UUID> trusted = new ArrayList<>();

        trustedTag.forEach(t -> {
            if (t instanceof NbtCompound nbt) {
                trusted.add(nbt.getUuid("id"));
            }
        });

        NbtList stateTag = tag.getList("states", NbtElement.COMPOUND_TYPE);
        Map<UUID, ActorState> states = new HashMap<>();
        stateTag.forEach(t -> {
            if (t instanceof NbtCompound nbt) {
                states.put(nbt.getUuid("id"), new ActorState(nbt.getCompound("data")));
            }
        });

        WorldState worldState = WorldState.readNbt(tag.getCompound("worldState"));

        boolean loadStates = tag.getBoolean("loadStates");
        boolean loadWorldState = tag.getBoolean("loadWorldState");

        Scene scene = new Scene(world, director, actors, trusted, worldState, lock, loadStates, loadWorldState, setup);
        scene.states.putAll(states);
        return scene;
    }
}
