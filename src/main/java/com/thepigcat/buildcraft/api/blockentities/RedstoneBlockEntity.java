package com.thepigcat.buildcraft.api.blockentities;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

// TODO: Move this to pdl
public interface RedstoneBlockEntity {
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
    }
}
