package com.thepigcat.buildcraft.api.blockentities;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.entity.BlockEntity;

// TODO: Move this to pdl
public interface RedstoneBlockEntity {
    default BlockEntity self() {
        return (BlockEntity) this;
    }

    int emitRedstoneLevel();

    void setRedstoneSignalType(RedstoneSignalType redstoneSignalType);

    RedstoneSignalType getRedstoneSignalType();

    enum RedstoneSignalType implements StringRepresentable {
        IGNORED("ignored"),
        LOW_SIGNAL("low_signal"),
        HIGH_SIGNAL("high_signal");

        public static final Codec<RedstoneSignalType> CODEC = StringRepresentable.fromEnum(RedstoneSignalType::values);

        private final String name;

        RedstoneSignalType(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public boolean isActive(int signalStrength) {
            return switch (this) {
                case IGNORED -> true;
                case LOW_SIGNAL -> signalStrength > 0;
                case HIGH_SIGNAL -> signalStrength == 15;
            };
        }

    }
}
