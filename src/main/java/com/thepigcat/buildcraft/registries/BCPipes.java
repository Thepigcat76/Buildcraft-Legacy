package com.thepigcat.buildcraft.registries;

import com.thepigcat.buildcraft.BuildcraftLegacy;
import com.thepigcat.buildcraft.api.pipes.PipeHolder;
import com.thepigcat.buildcraft.util.PipeRegistrationHelper;

public final class BCPipes {
    public static final PipeRegistrationHelper HELPER = new PipeRegistrationHelper(BuildcraftLegacy.MODID);

    //public static final PipeHolder WOODEN = HELPER.registerPipe("wooden", BCPipeTypes.DEFAULT, 0.2f);
    //public static final PipeHolder COBBLESTONE = HELPER.registerPipe("cobblestone", BCPipeTypes.EXTRACTING, 0.2f);
    public static final PipeHolder DIAMOND = HELPER.registerPipe("diamond", BCPipeTypes.DEFAULT, 0.5f);
    public static final PipeHolder GOLD = HELPER.registerPipe("gold", BCPipeTypes.DEFAULT, 0.5f);
}
