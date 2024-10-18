package com.thepigcat.fancy_pipes.networking;

import com.thepigcat.fancy_pipes.FancyPipes;
import com.thepigcat.fancy_pipes.content.blockentities.PipeBlockEntity;
import com.thepigcat.fancy_pipes.util.BlockUtils;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record SyncPipeDirectionPayload(BlockPos pos, Optional<Direction> from, Optional<Direction> to) implements CustomPacketPayload {
    public static final Type<SyncPipeDirectionPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(FancyPipes.MODID, "sync_pipe_dir"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncPipeDirectionPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            SyncPipeDirectionPayload::pos,
            ByteBufCodecs.optional(Direction.STREAM_CODEC),
            SyncPipeDirectionPayload::from,
            ByteBufCodecs.optional(Direction.STREAM_CODEC),
            SyncPipeDirectionPayload::to,
            SyncPipeDirectionPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void sync(SyncPipeDirectionPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            PipeBlockEntity be = BlockUtils.getBe(PipeBlockEntity.class, context.player().level(), payload.pos());
            be.from = payload.from.orElse(null);
            be.to = payload.to.orElse(null);
        });
    }
}
