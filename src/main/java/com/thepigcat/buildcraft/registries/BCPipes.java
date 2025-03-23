package com.thepigcat.buildcraft.registries;

import com.thepigcat.buildcraft.BuildcraftLegacy;
import com.thepigcat.buildcraft.api.pipes.PipeHolder;
import com.thepigcat.buildcraft.util.PipeRegistrationHelper;

public final class BCPipes {
    public static final PipeRegistrationHelper HELPER = new PipeRegistrationHelper();

    public static final PipeHolder WOODEN = HELPER.register("wooden", 0.2f, true);
    public static final PipeHolder COBBLESTONE = HELPER.register("cobblestone", 0.2f, false);
    public static final PipeHolder DIAMOND = HELPER.register("diamond", 0.5f, true);
    public static final PipeHolder GOLD = HELPER.register("gold", 0.5f, false);
}
