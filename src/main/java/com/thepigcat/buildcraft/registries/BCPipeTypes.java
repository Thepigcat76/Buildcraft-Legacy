package com.thepigcat.buildcraft.registries;

import com.thepigcat.buildcraft.BuildcraftLegacy;
import com.thepigcat.buildcraft.api.pipes.PipeTypeHolder;
import com.thepigcat.buildcraft.content.blocks.ExtractingItemPipeBlock;
import com.thepigcat.buildcraft.content.blocks.ItemPipeBlock;
import com.thepigcat.buildcraft.content.items.blocks.ItemPipeBlockItem;
import com.thepigcat.buildcraft.util.ModelUtils;
import com.thepigcat.buildcraft.util.PipeRegistrationHelper;

public final class BCPipeTypes {
    public static final PipeRegistrationHelper HELPER = new PipeRegistrationHelper(BuildcraftLegacy.MODID);

    public static final PipeTypeHolder<ItemPipeBlock, ItemPipeBlockItem> DEFAULT = HELPER.registerPipeType("default", ItemPipeBlock::new, ItemPipeBlockItem::new,
            ModelUtils.DEFAULT_BLOCK_MODEL_DEFINITION, ModelUtils.DEFAULT_BLOCK_MODEL_FILE, ModelUtils.DEFAULT_ITEM_MODEL_FILE,
            "base", "connection");
    public static final PipeTypeHolder<ExtractingItemPipeBlock, ItemPipeBlockItem> EXTRACTING = HELPER.registerPipeType("extracting", ExtractingItemPipeBlock::new, ItemPipeBlockItem::new,
            ModelUtils.EXTRACTING_BLOCK_MODEL_DEFINITION, ModelUtils.DEFAULT_BLOCK_MODEL_FILE, ModelUtils.DEFAULT_ITEM_MODEL_FILE,
            "base", "connection", "connection_extracting");

    public static void init() {
    }
}
