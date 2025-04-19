package com.thepigcat.buildcraft.networking;

import com.portingdeadmods.portingdeadlibs.utils.codec.CodecUtils;
import com.thepigcat.buildcraft.BuildcraftLegacy;
import com.thepigcat.buildcraft.api.blockentities.RedstoneBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RedstoneSignalTypeSyncPayload(BlockPos pos, RedstoneBlockEntity.RedstoneSignalType signalType) implements CustomPacketPayload {
    public static final Type<RedstoneSignalTypeSyncPayload> TYPE = new Type<>(BuildcraftLegacy.rl("redstone_signal_type_sync"));
    public static final StreamCodec<ByteBuf, RedstoneSignalTypeSyncPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            RedstoneSignalTypeSyncPayload::pos,
            CodecUtils.enumStreamCodec(RedstoneBlockEntity.RedstoneSignalType.class),
            RedstoneSignalTypeSyncPayload::signalType,
            RedstoneSignalTypeSyncPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            BlockEntity blockEntity = context.player().level().getBlockEntity(this.pos);
            if (blockEntity instanceof RedstoneBlockEntity redstoneBlockEntity) {
                redstoneBlockEntity.setRedstoneSignalType(this.signalType);
            }
        }).exceptionally(err -> {
            BuildcraftLegacy.LOGGER.error("Failed to handle RedstoneSignalTypeSync payload", err);
            return null;
        });
    }
}
