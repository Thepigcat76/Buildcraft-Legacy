package com.thepigcat.buildcraft.datagen.assets;

import com.thepigcat.buildcraft.BuildcraftLegacy;
import com.thepigcat.buildcraft.registries.BCBlocks;
import com.thepigcat.buildcraft.registries.BCFluidTypes;
import com.thepigcat.buildcraft.registries.BCItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class BCEnUSLangProvider extends LanguageProvider {
    public BCEnUSLangProvider(PackOutput output) {
        super(output, BuildcraftLegacy.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        addItem(BCItems.WRENCH, "Wrench");
        addItem(BCItems.WOODEN_GEAR, "Wooden Gear");
        addItem(BCItems.STONE_GEAR, "Stone Gear");
        addItem(BCItems.IRON_GEAR, "Iron Gear");
        addItem(BCItems.GOLD_GEAR, "Gold Gear");
        addItem(BCItems.DIAMOND_GEAR, "Diamond Gear");
        addItem(BCItems.OIL_BUCKET, "Oil Bucket");

        addBlock(BCBlocks.CRATE, "Crate");
        addBlock(BCBlocks.TANK, "Tank");
        addBlock(BCBlocks.COBBLESTONE_ITEM_PIPE, "Cobblestone Item Pipe");
        addBlock(BCBlocks.WOODEN_ITEM_PIPE, "Wooden Item Pipe");
        addBlock(BCBlocks.OIL_FLUID, "Oil");

        addBlock(BCBlocks.REDSTONE_ENGINE, "Redstone Engine");
        addBlock(BCBlocks.STIRLING_ENGINE, "Stirling Engine");
        addBlock(BCBlocks.COMBUSTION_ENGINE, "Combustion Engine");

        addFluidType(BCFluidTypes.OIL_FLUID_TYPE, "Oil");

        add("itemGroup.buildcraft.bc_tab", "Buildcraft");
    }

    private void addFluidType(Supplier<FluidType> fluidTypeSupplier, String translation) {
        ResourceLocation location = NeoForgeRegistries.FLUID_TYPES.getKey(fluidTypeSupplier.get());
        String fluidTypeName = location.getPath();
        String modid = location.getNamespace();
        add("fluid_type."+modid+"."+fluidTypeName, translation);
    }
}
