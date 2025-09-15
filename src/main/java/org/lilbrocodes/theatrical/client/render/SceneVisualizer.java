package org.lilbrocodes.theatrical.client.render;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import org.lilbrocodes.theatrical.cca.TheatricalCardinalComponents;
import org.lilbrocodes.theatrical.scene.Scene;
import org.lilbrocodes.theatrical.scene.Scene.ActorData;
import org.lilbrocodes.theatrical.scene.Scene.ActorState;

import java.util.Collections;
import java.util.UUID;

public class SceneVisualizer implements WorldRenderEvents.AfterEntities {

    private static Entity getActorEntity(ClientWorld world, Scene scene, UUID actorId) {
        ActorData data = scene.actors.get(actorId);
        if (data == null) return null;
        ActorState state = scene.getState(actorId);
        PlayerEntity player = world.getPlayerByUuid(actorId);

        ArmorStandEntity actor = new ArmorStandEntity(world, data.pos().x, data.pos().y, data.pos().z);
        if (state != null) {
            NbtCompound playerData = state.data().copy();
            int selectedItemSlot = playerData.getInt("SelectedItemSlot");

            NbtList handItems = new NbtList();
            NbtList armorItems = new NbtList();

            for (int i = 0; i < 2; i++) handItems.add(ItemStack.EMPTY.writeNbt(new NbtCompound()));
            for (int i = 0; i < 4; i++) armorItems.add(ItemStack.EMPTY.writeNbt(new NbtCompound()));

            if (playerData.contains("Inventory", NbtElement.LIST_TYPE)) {
                NbtList invList = playerData.getList("Inventory", NbtElement.COMPOUND_TYPE);
                PlayerInventory inventory = new PlayerInventory(player);
                inventory.readNbt(invList);

                handItems.set(0, inventory.main.get(selectedItemSlot).writeNbt(new NbtCompound()));
                handItems.set(1, inventory.offHand.get(0).writeNbt(new NbtCompound()));
                for (int i = 0; i < 4; i++) armorItems.set(i, inventory.armor.get(i).writeNbt(new NbtCompound()));
            }

            NbtCompound armorStandNbt = new NbtCompound();
            armorStandNbt.put("HandItems", handItems);
            armorStandNbt.put("ArmorItems", armorItems);

            actor.readCustomDataFromNbt(armorStandNbt);
        }


        actor.setCustomNameVisible(true);
        actor.setCustomName(player != null ? player.getName() : Text.literal("Actor " + actorId.toString()));
        actor.setShowArms(true);
        actor.setGlowing(true);
        actor.setInvisible(true);
        return actor;
    }

    public static void renderScene(MinecraftClient client, Scene scene, WorldRenderContext ctx, float tickDelta) {
        EntityRenderDispatcher dispatcher = client.getEntityRenderDispatcher();
        MatrixStack matrices = ctx.matrixStack();
        ClientWorld world = client.world;

        if (world == null) return;

        for (UUID actorId : scene.actors.keySet()) {
            ActorData actor = scene.actors.get(actorId);
            if (actor == null) continue;

            Entity entity = getActorEntity(world, scene, actorId);
            if (entity == null) continue;

            matrices.push();

            matrices.translate(
                    actor.pos().x - ctx.camera().getPos().x,
                    actor.pos().y - ctx.camera().getPos().y,
                    actor.pos().z - ctx.camera().getPos().z
            );

            dispatcher.render(entity, 0.0, 0.0, 0.0, actor.yaw(), tickDelta, matrices,
                    ctx.consumers(), LightmapTextureManager.MAX_LIGHT_COORDINATE);

            matrices.pop();
        }
    }


    @Override
    public void afterEntities(WorldRenderContext ctx) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        PlayerEntity player = client.player;

        TheatricalCardinalComponents.VISUALISED_SCENES.get(player).getScenes().forEach(scene -> {
            renderScene(client, scene, ctx, ctx.tickDelta());
        });
    }
}
