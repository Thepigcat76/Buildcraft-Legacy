package com.thepigcat.buildcraft.networking;

import com.thepigcat.buildcraft.BuildcraftLegacy;
import com.thepigcat.buildcraft.content.blockentities.ItemPipeBE;
import com.thepigcat.buildcraft.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SyncPipeMovementPayload(BlockPos pos, float movement, float lastMovement) implements CustomPacketPayload {
    public static final Type<SyncPipeMovementPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(BuildcraftLegacy.MODID, "sync_pipe_movement"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncPipeMovementPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            SyncPipeMovementPayload::pos,
            ByteBufCodecs.FLOAT,
            SyncPipeMovementPayload::movement,
            ByteBufCodecs.FLOAT,
            SyncPipeMovementPayload::lastMovement,
            SyncPipeMovementPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void sync(SyncPipeMovementPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ItemPipeBE be = BlockUtils.getBE(ItemPipeBE.class, context.player().level(), payload.pos());
            if (be != null) {
                be.lastMovement = payload.lastMovement;
                be.movement = payload.movement;
            }
        });
    }
}
