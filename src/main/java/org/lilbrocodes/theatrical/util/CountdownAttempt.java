package org.lilbrocodes.theatrical.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.lilbrocodes.theatrical.cca.TheatricalCardinalComponents;
import org.lilbrocodes.theatrical.commands.CommandUtil;
import org.lilbrocodes.theatrical.config.Configs;

import java.util.*;
import java.util.stream.Collectors;

public class CountdownAttempt {
    private static final Map<Integer, Map<Integer, Text>> STATE_TEXTS = Map.of(
            0, Map.of(
                    0, Text.translatable("theatrical.countdown.state.in_cooldown").formatted(Formatting.RED).formatted(Formatting.BOLD),
                    1, Text.translatable("theatrical.countdown.state.not_in_cooldown").formatted(Formatting.GREEN).formatted(Formatting.BOLD)
            ),
            1, Map.of(
                    0, Text.translatable("theatrical.countdown.state.vc.not_loaded").formatted(Formatting.GOLD).formatted(Formatting.BOLD),
                    1, Text.translatable("theatrical.countdown.state.vc.recording").formatted(Formatting.GREEN).formatted(Formatting.BOLD),
                    2, Text.translatable("theatrical.countdown.state.vc.started").formatted(Formatting.YELLOW).formatted(Formatting.BOLD),
                    3, Text.translatable("theatrical.countdown.state.vc.stopped").formatted(Formatting.RED).formatted(Formatting.BOLD),
                    4, Text.translatable("theatrical.countdown.state.vc.disabled").formatted(Formatting.GOLD).formatted(Formatting.BOLD)
            ),
            2, Map.of(
                    0, Text.translatable("theatrical.countdown.state.rm.not_loaded").formatted(Formatting.GOLD).formatted(Formatting.BOLD),
                    1, Text.translatable("theatrical.countdown.state.rm.recording").formatted(Formatting.GREEN).formatted(Formatting.BOLD),
                    2, Text.translatable("theatrical.countdown.state.rm.started").formatted(Formatting.YELLOW).formatted(Formatting.BOLD),
                    3, Text.translatable("theatrical.countdown.state.rm.stopped").formatted(Formatting.RED).formatted(Formatting.BOLD),
                    4, Text.translatable("theatrical.countdown.state.rm.disabled").formatted(Formatting.GOLD).formatted(Formatting.BOLD)
            )
    );

    public final UUID owner;
    public final Map<UUID, List<Integer>> players;
    public final int duration;
    public final long creationTime;

    public CountdownAttempt(UUID owner, Map<UUID, List<Integer>> players, int duration, @Nullable Long startTime) {
        this.owner = owner;
        this.players = players;
        this.duration = duration;
        if (startTime == null) creationTime = System.currentTimeMillis();
        else creationTime = startTime;
    }

    public CountdownAttempt(UUID owner, List<UUID> players, int duration, @Nullable Long startTime) {
        this.owner = owner;
        this.players = new HashMap<>();
        players.forEach(uuid -> this.players.put(uuid, new ArrayList<>()));
        this.duration = duration;
        if (startTime == null) creationTime = System.currentTimeMillis();
        else creationTime = startTime;
    }

    public void setState(UUID player, List<Integer> state) {
        if (players.containsKey(player)) players.put(player, state);
    }

    public boolean getStartable() {
        int failCount = 0;
        for (List<Integer> value : players.values()) {
            if (!isSuccess(value)) failCount++;
        }
        return failCount <= Configs.SERVER.tolerableUncheckedPlayers.get();
    }

    private boolean isSuccess(List<Integer> state) {
        if (state.isEmpty()) return false;

        int inCountdownState = state.get(0);
        int voicechatState = state.get(1);
        int replaymodState = state.get(2);

        boolean inCountdownValid = inCountdownState == 1;
        boolean replaymodValid = replaymodState != 3;
        boolean voicechatValid = voicechatState != 3;

        return inCountdownValid && replaymodValid && voicechatValid;
    }

    public boolean notifyOwnerIfNotAllSuccess(World world) {
        boolean allResponded = players.values().stream().noneMatch(List::isEmpty);
        if (!allResponded) return false;
        boolean allSuccess = getStartable();
        if (allSuccess) return false;

        PlayerEntity ownerPlayer = world.getPlayerByUuid(owner);
        if (!(ownerPlayer instanceof ServerPlayerEntity)) return false;

        List<Text> playerStates = players.entrySet().stream()
                .map(entry -> {
                    UUID playerUuid = entry.getKey();
                    List<Integer> state = entry.getValue();

                    if (isSuccess(state)) return Text.literal("");

                    String playerName = Optional.ofNullable(world.getPlayerByUuid(playerUuid))
                            .map(p -> p.getName().getString())
                            .orElse(playerUuid.toString());

                    MutableText text = Text.literal("• " + playerName).formatted(Formatting.GOLD);

                    for (int i = 0; i < state.size(); i++) {
                        int value = state.get(i);
                        Text stateText = STATE_TEXTS
                                .getOrDefault(i, Collections.emptyMap())
                                .getOrDefault(value, Text.translatable("theatrical.countdown.state.unknown"));

                        text.append(Text.literal("\n   ")).append(stateText);
                    }

                    return text;
                })
                .collect(Collectors.toList());

        MutableText message = Text.translatable("command.theatrical.countdown.fail2").formatted(Formatting.BOLD);

        for (Text playerText : playerStates) {
            message.append(Text.literal("\n")).append(playerText);
        }

        CommandUtil.feedback(ownerPlayer, message);
        return true;
    }

    public NbtCompound writeToNbt(NbtCompound tag) {
        NbtList playerList = new NbtList();
        for (Map.Entry<UUID, List<Integer>> entry : players.entrySet()) {
            UUID uuid = entry.getKey();
            List<Integer> stateList = entry.getValue();

            NbtCompound playerTag = new NbtCompound();
            playerTag.put("uuid", new NbtLongArray(new long[] {
                    uuid.getMostSignificantBits(),
                    uuid.getLeastSignificantBits()
            }));

            NbtList stateNbtList = new NbtList();
            for (Integer state : stateList) {
                stateNbtList.add(NbtInt.of(state));
            }
            playerTag.put("state", stateNbtList);

            playerList.add(playerTag);
        }

        tag.put("players", playerList);
        tag.putInt("duration", duration);
        tag.putUuid("owner", owner);
        tag.putLong("creationTime", creationTime);
        return tag;
    }

    public static CountdownAttempt readFromNbt(NbtCompound tag) {
        Map<UUID, List<Integer>> players = new HashMap<>();
        NbtList playerList = tag.getList("players", NbtElement.COMPOUND_TYPE);
        for (NbtElement element : playerList) {
            if (element instanceof NbtCompound playerTag) {
                long[] uuidLongs = playerTag.getLongArray("uuid");
                if (uuidLongs.length == 2) {
                    UUID uuid = new UUID(uuidLongs[0], uuidLongs[1]);

                    List<Integer> stateList = new ArrayList<>();
                    NbtList stateNbtList = playerTag.getList("state", NbtElement.INT_TYPE);
                    for (NbtElement stateElement : stateNbtList) {
                        stateList.add(((NbtInt) stateElement).intValue());
                    }

                    players.put(uuid, stateList);
                }
            }
        }

        int duration = tag.getInt("duration");
        long creationTime = tag.getLong("creationTime");
        UUID ownerUuid = tag.getUuid("owner");
        return new CountdownAttempt(ownerUuid, players, duration, creationTime);
    }

    public boolean shouldRemove(World world) {
        if (System.currentTimeMillis() - creationTime >= Configs.SERVER.countdownTimeoutMinutes.get() * 60000) {
            PlayerEntity player = world.getPlayerByUuid(owner);
            if (player != null) CommandUtil.feedback(player, Text.translatable("command.theatrical.countdown.fail1"));
            return true;
        }

        if (notifyOwnerIfNotAllSuccess(world)) {
            return true;
        }

        if (getStartable()) {
            TheatricalCardinalComponents.COUNTDOWNS.get(world).create(new ArrayList<>(players.keySet()), duration);
            return true;
        }
        return false;
    }
}
