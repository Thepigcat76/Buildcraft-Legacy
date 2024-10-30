package com.thepigcat.buildcraft.datagen.assets;

import com.thepigcat.buildcraft.FancyPipes;
import com.thepigcat.buildcraft.registries.FPBlocks;
import com.thepigcat.buildcraft.registries.FPFluidTypes;
import com.thepigcat.buildcraft.registries.FPItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class EnUSLangProvider extends LanguageProvider {
    public EnUSLangProvider(PackOutput output) {
        super(output, FancyPipes.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        addItem(FPItems.WRENCH, "Wrench");
        addItem(FPItems.WOODEN_GEAR, "Wooden Gear");
        addItem(FPItems.STONE_GEAR, "Stone Gear");
        addItem(FPItems.IRON_GEAR, "Iron Gear");
        addItem(FPItems.GOLD_GEAR, "Gold Gear");
        addItem(FPItems.DIAMOND_GEAR, "Diamond Gear");
        addItem(FPItems.OIL_BUCKET, "Oil Bucket");

        addBlock(FPBlocks.CRATE, "Crate");
        addBlock(FPBlocks.TANK, "Tank");
        addBlock(FPBlocks.COBBLESTONE_ITEM_PIPE, "Cobblestone Item Pipe");
        addBlock(FPBlocks.WOODEN_ITEM_PIPE, "Wooden Item Pipe");
        addBlock(FPBlocks.OIL_FLUID, "Oil");

        addFluidType(FPFluidTypes.OIL_FLUID_TYPE, "Oil");
    }

    private void addFluidType(Supplier<FluidType> fluidTypeSupplier, String translation) {
        ResourceLocation location = NeoForgeRegistries.FLUID_TYPES.getKey(fluidTypeSupplier.get());
        String fluidTypeName = location.getPath();
        String modid = location.getNamespace();
        add("fluid_type."+modid+"."+fluidTypeName, translation);
    }
}
