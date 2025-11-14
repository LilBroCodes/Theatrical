package org.lilbrocodes.theatrical.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import org.lilbrocodes.composer_reloaded.api.controls.BindManager;
import org.lilbrocodes.composer_reloaded.api.toast.ToastManager;
import org.lilbrocodes.composer_reloaded.client.duped_binds.BindTracker;
import org.lilbrocodes.theatrical.Theatrical;
import org.lilbrocodes.theatrical.cca.TheatricalCardinalComponents;
import org.lilbrocodes.theatrical.cca.world.CountdownInstanceHolderComponent;
import org.lilbrocodes.theatrical.client.render.SceneVisualizer;
import org.lilbrocodes.theatrical.client.toast.CountdownToast;
import org.lilbrocodes.theatrical.integrations.CooldownChecks;
import org.lilbrocodes.theatrical.mixin.accessor.HandHeldDataHolder;
import org.lilbrocodes.theatrical.util.CountdownInstance;
import org.lwjgl.glfw.GLFW;

import io.netty.buffer.Unpooled;

import java.util.ArrayList;
import java.util.List;

public class TheatricalClient implements ClientModInitializer {
    public static KeyBinding HAND_HOLD_BIND = new KeyBinding(
            "key.theatrical.hand_hold",
            GLFW.GLFW_KEY_G,
            "category.theatrical"
    );

    public static KeyBinding WALK_SPEED_TRIGGER = new KeyBinding(
            "key.theatrical.walk_speed_trigger",
            GLFW.GLFW_KEY_LEFT_ALT,
            "category.theatrical"
    );

    public static KeyBinding WALK_SPEED_MODIFIER = new KeyBinding(
            "key.theatrical.walk_speed_modifier",
            GLFW.GLFW_KEY_LEFT_SHIFT,
            "category.theatrical"
    );

    @Override
    public void onInitializeClient() {
        registerBind(HAND_HOLD_BIND);
        registerBind(WALK_SPEED_TRIGGER);
        registerBind(WALK_SPEED_MODIFIER);
        TheatricalClientEvents.initialize();
    }

    public static void registerBind(KeyBinding keyBinding) {
        KeyBindingHelper.registerKeyBinding(keyBinding);
        BindManager.addDuplicateAllowedKeybind(keyBinding);
    }
}
