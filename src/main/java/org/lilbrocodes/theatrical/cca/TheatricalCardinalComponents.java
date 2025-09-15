package org.lilbrocodes.theatrical.cca;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import net.minecraft.entity.player.PlayerEntity;
import org.lilbrocodes.theatrical.Theatrical;
import org.lilbrocodes.theatrical.cca.entity.*;
import org.lilbrocodes.theatrical.cca.world.CountdownAttemptHolderComponent;
import org.lilbrocodes.theatrical.cca.world.CountdownInstanceHolderComponent;
import org.lilbrocodes.theatrical.cca.world.SceneHolderComponent;

public class TheatricalCardinalComponents implements EntityComponentInitializer, WorldComponentInitializer {
    public static ComponentKey<VisualisedScenesComponent> VISUALISED_SCENES = ComponentRegistry.getOrCreate(Theatrical.VISUALISED_SCENES_PACKET, VisualisedScenesComponent.class);
    public static ComponentKey<HandHeldOutComponent> HAND_HELD_OUT = ComponentRegistry.getOrCreate(Theatrical.HAND_HELD_PACKET, HandHeldOutComponent.class);
    public static ComponentKey<PlotArmorComponent> PLOT_ARMOR_TYPE = ComponentRegistry.getOrCreate(Theatrical.PLOT_ARMOR_PACKET, PlotArmorComponent.class);
    public static ComponentKey<WalkSpeedComponent> WALK_SPEED = ComponentRegistry.getOrCreate(Theatrical.WALK_SPEED_PACKET, WalkSpeedComponent.class);
    public static ComponentKey<DirectorComponent> DIRECTOR = ComponentRegistry.getOrCreate(Theatrical.DIRECTOR_PACKET, DirectorComponent.class);
    public static ComponentKey<LockedComponent> LOCKED = ComponentRegistry.getOrCreate(Theatrical.LOCKED_PACKET, LockedComponent.class);

    public static ComponentKey<CountdownInstanceHolderComponent> COUNTDOWNS = ComponentRegistry.getOrCreate(Theatrical.COUNTDOWNS, CountdownInstanceHolderComponent.class);
    public static ComponentKey<CountdownAttemptHolderComponent> COUNTDOWN_ATTEMPTS = ComponentRegistry.getOrCreate(Theatrical.COUNTDOWN_ATTEMPTS, CountdownAttemptHolderComponent.class);
    public static ComponentKey<SceneHolderComponent> SCENES = ComponentRegistry.getOrCreate(Theatrical.SCENES, SceneHolderComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.beginRegistration(PlayerEntity.class, VISUALISED_SCENES).respawnStrategy(RespawnCopyStrategy.ALWAYS_COPY).end(VisualisedScenesComponent::new);
        registry.beginRegistration(PlayerEntity.class, PLOT_ARMOR_TYPE).respawnStrategy(RespawnCopyStrategy.ALWAYS_COPY).end(PlotArmorComponent::new);
        registry.beginRegistration(PlayerEntity.class, HAND_HELD_OUT).respawnStrategy(RespawnCopyStrategy.NEVER_COPY).end(HandHeldOutComponent::new);
        registry.beginRegistration(PlayerEntity.class, WALK_SPEED).respawnStrategy(RespawnCopyStrategy.ALWAYS_COPY).end(WalkSpeedComponent::new);
        registry.beginRegistration(PlayerEntity.class, DIRECTOR).respawnStrategy(RespawnCopyStrategy.ALWAYS_COPY).end(DirectorComponent::new);
        registry.beginRegistration(PlayerEntity.class, LOCKED).respawnStrategy(RespawnCopyStrategy.NEVER_COPY).end(LockedComponent::new);
    }

    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        registry.register(COUNTDOWNS, CountdownInstanceHolderComponent::new);
        registry.register(COUNTDOWN_ATTEMPTS, CountdownAttemptHolderComponent::new);
        registry.register(SCENES, SceneHolderComponent::new);
    }
}
