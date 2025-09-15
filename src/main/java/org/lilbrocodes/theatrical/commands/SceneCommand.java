package org.lilbrocodes.theatrical.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.RotationArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.lilbrocodes.theatrical.cca.TheatricalCardinalComponents;
import org.lilbrocodes.theatrical.scene.Scene;
import org.lilbrocodes.theatrical.util.Misc;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SceneCommand extends CommandUtil {

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher,
                         CommandRegistryAccess registryAccess,
                         CommandManager.RegistrationEnvironment environment) {

        dispatcher.register(CommandManager.literal("scene")
                .requires(Misc::isDirector)

                .then(CommandManager.literal("create")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                                .then(CommandManager.argument("lock", BoolArgumentType.bool())
                                        .then(CommandManager.argument("loadStates", BoolArgumentType.bool())
                                                .then(CommandManager.argument("loadWorldState", BoolArgumentType.bool())
                                                        .executes(this::createScene)
                                                )
                                        )
                                )
                        )
                )

                .then(CommandManager.literal("remove")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                                .suggests(this::scenes)
                                .executes(this::removeScene)
                        )
                )

                .then(CommandManager.literal("edit")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                                .suggests(this::scenes)
                                .then(CommandManager.literal("lock")
                                        .then(CommandManager.argument("lock", BoolArgumentType.bool())
                                                .executes(this::editLock)
                                        )
                                )
                                .then(addOrMove("add"))
                                .then(addOrMove("move"))
                                .then(CommandManager.literal("remove")
                                    .then(CommandManager.argument("player", EntityArgumentType.player()).suggests(this::scenePlayers)
                                        .executes(this::remove)
                                    )
                                )
                        )
                )

                .then(CommandManager.literal("setup")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                                .suggests(this::scenes)
                                .executes(this::setupScene)
                        )
                )

                .then(CommandManager.literal("start")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                                .suggests(this::scenes)
                                .executes(this::startScene)
                        )
                )

                .then(CommandManager.literal("countdown")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                                .suggests(this::scenes)
                                .then(CommandManager.argument("duration", IntegerArgumentType.integer(1, 3600))
                                        .executes(this::startCountdown)
                                )
                        )
                )

                .then(CommandManager.literal("state")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                                .suggests(this::scenes)
                                .then(CommandManager.literal("save")
                                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                                .executes(this::saveState)
                                        )
                                )
                                .then(CommandManager.literal("load")
                                        .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                                .executes(this::loadState)
                                        )
                                )
                                .then(CommandManager.literal("world")
                                        .then(CommandManager.literal("save").executes(this::saveWorldState))
                                        .then(CommandManager.literal("load").executes(this::loadWorldState))
                                )
                        )
                )

                .then(CommandManager.literal("visuals")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                                .suggests(this::scenes)
                                .then(CommandManager.literal("on").executes(ctx -> toggleVisuals(ctx, true)))
                                .then(CommandManager.literal("off").executes(ctx -> toggleVisuals(ctx, false)))
                        )
                )

                .then(CommandManager.literal("trust")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                                .suggests(this::scenes)
                                .then(CommandManager.argument("player", EntityArgumentType.player())
                                        .executes(this::toggleTrust)
                                )
                        )
                )
        );
    }

    private int createScene(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity player = ctx.getSource().getPlayerOrThrow();
        String name = ctx.getArgument("name", String.class);
        boolean lock = ctx.getArgument("lock", Boolean.class);
        boolean loadStates = ctx.getArgument("loadStates", Boolean.class);
        boolean loadWorldState = ctx.getArgument("loadWorldState", Boolean.class);

        Scene scene = new Scene(player.getWorld(), player.getUuid(), lock, loadStates, loadWorldState);
        TheatricalCardinalComponents.SCENES.get(player.getWorld()).scenes.put(name, scene);
        TheatricalCardinalComponents.SCENES.get(player.getWorld()).sync();

        feedback(player, Text.translatable("theatrical.scene.created"));
        return 1;
    }

    private int removeScene(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity player = ctx.getSource().getPlayerOrThrow();
        String name = ctx.getArgument("name", String.class);
        Scene scene = TheatricalCardinalComponents.SCENES.get(player.getWorld()).scenes.get(name);

        if (checkScenePerms(player, scene)) return 0;

        TheatricalCardinalComponents.SCENES.get(player.getWorld()).scenes.remove(name);
        TheatricalCardinalComponents.SCENES.get(player.getWorld()).sync();

        feedback(player, Text.translatable("theatrical.scene.removed", name).formatted(Formatting.GREEN));
        return 1;
    }

    private int editLock(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity player = ctx.getSource().getPlayerOrThrow();
        String name = ctx.getArgument("name", String.class);
        boolean lock = ctx.getArgument("lock", Boolean.class);
        Scene scene = TheatricalCardinalComponents.SCENES.get(player.getWorld()).scenes.get(name);

        if (checkScenePerms(player, scene)) return 0;

        scene.setLock(lock);
        feedback(player, Text.translatable("theatrical.scene.edit.success"));
        return 1;
    }

    private CompletableFuture<Suggestions> scenes(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        PlayerEntity player = context.getSource().getPlayer();
        if (player != null) {
            TheatricalCardinalComponents.SCENES.get(player.getWorld()).scenes.keySet().forEach(builder::suggest);
        }
        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> scenePlayers(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        PlayerEntity player = context.getSource().getPlayerOrThrow();
        String name = context.getArgument("name", String.class);
        Scene scene = TheatricalCardinalComponents.SCENES.get(player.getWorld()).scenes.get(name);
        if (checkScenePerms(player, scene)) return builder.buildFuture();

        if (scene != null) {
            scene.actors().forEach(id -> {
                PlayerEntity p = player.getWorld().getPlayerByUuid(id);
                if (p != null) builder.suggest(player.getDisplayName().getString());
                else builder.suggest(id.toString());
            });
        }

        return builder.buildFuture();
    }

    private boolean checkScenePerms(PlayerEntity player, Scene scene) {
        if (scene == null) {
            feedback(player, Text.translatable("theatrical.scene.edit.not_exists").formatted(Formatting.RED));
            return true;
        }
        if (!scene.hasPermission(player.getUuid())) {
            feedback(player, Text.translatable("theatrical.scene.edit.no_permission").formatted(Formatting.RED));
            return true;
        }
        return false;
    }

    private LiteralArgumentBuilder<ServerCommandSource> addOrMove(String type) {
        return CommandManager.literal(type)
                .then(CommandManager.literal("pos")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .executes(ctx -> executeAddMove(ctx, type, true, null, null))
                        )
                )
                .then(CommandManager.literal("coords")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .then(CommandManager.argument("pos", Vec3ArgumentType.vec3())
                                        .executes(ctx -> executeAddMove(ctx, type, false, Vec3ArgumentType.getVec3(ctx, "pos"), null))
                                        .then(CommandManager.argument("rotation", RotationArgumentType.rotation())
                                                .executes(ctx -> executeAddMove(ctx, type, false, Vec3ArgumentType.getVec3(ctx, "pos"),
                                                        RotationArgumentType.getRotation(ctx, "rotation").toAbsoluteRotation(ctx.getSource())))
                                        )
                                )
                        )
                );
    }

    private int remove(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity player = ctx.getSource().getPlayerOrThrow();
        PlayerEntity target = EntityArgumentType.getPlayer(ctx, "player");
        String name = ctx.getArgument("name", String.class);
        Scene scene = TheatricalCardinalComponents.SCENES.get(player.getWorld()).scenes.get(name);

        if (checkScenePerms(player, scene)) return 0;

        scene.remove(target.getUuid());

        feedback(ctx, Text.translatable("theatrical.scene.edit.success"));
        return 1;
    }

    private int executeAddMove(CommandContext<ServerCommandSource> ctx, String type, boolean usePlayerPos, Vec3d pos, Vec2f rot) throws CommandSyntaxException {
        PlayerEntity player = ctx.getSource().getPlayerOrThrow();
        String name = ctx.getArgument("name", String.class);
        PlayerEntity target = EntityArgumentType.getPlayer(ctx, "player");
        Scene scene = TheatricalCardinalComponents.SCENES.get(player.getWorld()).scenes.get(name);

        if (checkScenePerms(player, scene)) return 0;
        if (rot == null) rot = Vec2f.ZERO;

        if (usePlayerPos) {
            pos = target.getPos();
            rot = new Vec2f(target.getYaw(), target.getPitch());
        }

        boolean success;
        if (type.equals("add")) success = scene.add(target.getUuid(), pos, rot.x, rot.y);
        else {
            scene.move(target.getUuid(), pos, rot.x, rot.y);
            success = true;
        };

        if (!success) {
            feedback(player, Text.translatable("theatrical.scene.add.already_exists"));
            return 0;
        }

        
        feedback(player, Text.translatable(type.equals("add") ?
                "theatrical.scene.add.success" :
                "theatrical.scene.move.success"));
        return 1;
    }

    private int setupScene(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity player = ctx.getSource().getPlayerOrThrow();
        String name = ctx.getArgument("name", String.class);
        Scene scene = TheatricalCardinalComponents.SCENES.get(player.getWorld()).scenes.get(name);

        if (checkScenePerms(player, scene)) return 0;

        scene.setup();
        feedback(player, Text.translatable("theatrical.scene.setup"));
        return 1;
    }

    private int startScene(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity player = ctx.getSource().getPlayerOrThrow();
        String name = ctx.getArgument("name", String.class);
        Scene scene = TheatricalCardinalComponents.SCENES.get(player.getWorld()).scenes.get(name);

        if (checkScenePerms(player, scene)) return 0;

        scene.start();
        feedback(player, Text.translatable("theatrical.scene.start"));
        return 1;
    }

    private int startCountdown(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity player = ctx.getSource().getPlayerOrThrow();
        String name = ctx.getArgument("name", String.class);
        int duration = ctx.getArgument("duration", Integer.class) * 20;
        Scene scene = TheatricalCardinalComponents.SCENES.get(player.getWorld()).scenes.get(name);

        if (checkScenePerms(player, scene)) return 0;

        List<UUID> actors = scene.actors();
        TheatricalCardinalComponents.COUNTDOWN_ATTEMPTS.get(player.getWorld()).create(player.getUuid(), actors, duration);

        feedback(ctx, Text.translatable("command.theatrical.countdown.success", duration, actors.size(), Misc.getQuantifier(actors)));
        return 1;
    }

    private int saveState(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity player = ctx.getSource().getPlayerOrThrow();
        String name = ctx.getArgument("name", String.class);
        Scene scene = TheatricalCardinalComponents.SCENES.get(player.getWorld()).scenes.get(name);

        if (checkScenePerms(player, scene)) return 0;

        PlayerEntity target = EntityArgumentType.getPlayer(ctx, "player");
        scene.saveState(target);

        feedback(player, Text.translatable("theatrical.scene.state.saved", target.getDisplayName()));
        return 1;
    }

    private int loadState(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity player = ctx.getSource().getPlayerOrThrow();
        String name = ctx.getArgument("name", String.class);
        boolean enabled = ctx.getArgument("enabled", Boolean.class);
        Scene scene = TheatricalCardinalComponents.SCENES.get(player.getWorld()).scenes.get(name);

        if (checkScenePerms(player, scene)) return 0;

        scene.setLoadStates(enabled);
        feedback(player, Text.translatable(enabled ?
                "theatrical.scene.state.load_on" :
                "theatrical.scene.state.load_off"));
        return 1;
    }

    private int loadWorldState(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity player = ctx.getSource().getPlayerOrThrow();
        String name = ctx.getArgument("name", String.class);
        Scene scene = TheatricalCardinalComponents.SCENES.get(player.getWorld()).scenes.get(name);

        if (checkScenePerms(player, scene)) return 0;

        scene.loadWorldState();
        feedback(ctx, Text.translatable("theatrical.scene.state.world.load"));

        return 1;
    }

    private int saveWorldState(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity player = ctx.getSource().getPlayerOrThrow();
        String name = ctx.getArgument("name", String.class);
        Scene scene = TheatricalCardinalComponents.SCENES.get(player.getWorld()).scenes.get(name);

        if (checkScenePerms(player, scene)) return 0;

        scene.saveWorldState();
        feedback(ctx, Text.translatable("theatrical.scene.state.world.save"));

        return 1;
    }

    private int toggleVisuals(CommandContext<ServerCommandSource> ctx, boolean state) throws CommandSyntaxException {
        PlayerEntity player = ctx.getSource().getPlayerOrThrow();
        String name = ctx.getArgument("name", String.class);

        if (state) {
            TheatricalCardinalComponents.VISUALISED_SCENES.get(player).visualise(name);
            feedback(player, Text.translatable("theatrical.scene.visuals.on", name));
        } else {
            TheatricalCardinalComponents.VISUALISED_SCENES.get(player).stopVisualisingScene(name);
            feedback(player, Text.translatable("theatrical.scene.visuals.off", name));
        }

        return 1;
    }

    private int toggleTrust(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity player = ctx.getSource().getPlayerOrThrow();
        String name = ctx.getArgument("name", String.class);
        PlayerEntity target = EntityArgumentType.getPlayer(ctx, "player");
        Scene scene = TheatricalCardinalComponents.SCENES.get(player.getWorld()).scenes.get(name);

        feedback(player, scene.toggleTrust(target) ?
                Text.translatable("theatrical.scene.trust", target.getDisplayName()) :
                Text.translatable("theatrical.scene.untrust", target.getDisplayName())
        );
        return 1;
    }
}
