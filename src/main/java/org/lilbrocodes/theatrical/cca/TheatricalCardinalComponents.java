package org.lilbrocodes.theatrical.cca;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.entity.player.PlayerEntity;
import org.lilbrocodes.theatrical.Theatrical;
import org.lilbrocodes.theatrical.cca.entity.HandHeldOutComponent;
import org.lilbrocodes.theatrical.cca.entity.WalkSpeedComponent;

public class TheatricalCardinalComponents implements EntityComponentInitializer {
    public static ComponentKey<HandHeldOutComponent> HAND_HELD_OUT = ComponentRegistry.getOrCreate(Theatrical.HAND_HELD_PACKET, HandHeldOutComponent.class);
    public static ComponentKey<WalkSpeedComponent> WALK_SPEED = ComponentRegistry.getOrCreate(Theatrical.WALK_SPEED_PACKET, WalkSpeedComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.beginRegistration(PlayerEntity.class, HAND_HELD_OUT).respawnStrategy(RespawnCopyStrategy.NEVER_COPY).end(HandHeldOutComponent::new);
        registry.beginRegistration(PlayerEntity.class, WALK_SPEED).respawnStrategy(RespawnCopyStrategy.ALWAYS_COPY).end(WalkSpeedComponent::new);
    }
}
