package com.thepigcat.buildcraft.datagen.assets;

import com.thepigcat.buildcraft.FancyPipes;
import com.thepigcat.buildcraft.api.blocks.ExtractingPipeBlock;
import com.thepigcat.buildcraft.api.blocks.PipeBlock;
import com.thepigcat.buildcraft.content.blocks.TankBlock;
import com.thepigcat.buildcraft.registries.FPBlocks;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class FPBlockStateProvider extends BlockStateProvider {
    public FPBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, FancyPipes.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        pillarBlock(FPBlocks.CRATE.get());
        tankBlock(FPBlocks.TANK.get());
        simpleBlock(FPBlocks.WOODEN_ENGINE.get(), new ModelFile.UncheckedModelFile(modLoc("block/wooden_engine")));

        for (Block block : FPBlocks.BLOCKS.getRegistry().get()) {
            if (block instanceof ExtractingPipeBlock) {
                extractingPipeBlock(block);
            } else if (block instanceof PipeBlock) {
                pipeBlock(block);
            }

        }
    }

    private void tankBlock(Block block) {
        ResourceLocation blockTexture = blockTexture(block);
        ResourceLocation topTexture = extend(blockTexture, "_top");
        ResourceLocation topJoinedTexture = extend(blockTexture, "_top_joined");
        ResourceLocation sideTexture = extend(blockTexture, "_side");
        ResourceLocation sideJoinedTexture = extend(blockTexture, "_side_joined");

        getVariantBuilder(block)
                .partialState().with(TankBlock.TOP_JOINED, true).with(TankBlock.BOTTOM_JOINED, true)
                .modelForState().modelFile(tankModel(extend(blockTexture, "_top_and_bottom_joined"), topJoinedTexture, sideJoinedTexture, topJoinedTexture)).addModel()
                .partialState().with(TankBlock.TOP_JOINED, true).with(TankBlock.BOTTOM_JOINED, false)
                .modelForState().modelFile(tankModel(extend(blockTexture, "_top_joined"), topJoinedTexture, sideTexture, topTexture)).addModel()
                .partialState().with(TankBlock.TOP_JOINED, false).with(TankBlock.BOTTOM_JOINED, true)
                .modelForState().modelFile(tankModel(extend(blockTexture, "_bottom_joined"), topTexture, sideJoinedTexture, topJoinedTexture)).addModel()
                .partialState().with(TankBlock.TOP_JOINED, false).with(TankBlock.BOTTOM_JOINED, false)
                .modelForState().modelFile(tankModel(blockTexture, topTexture, sideTexture, topTexture)).addModel();
    }

    private void pillarBlock(Block block) {
        ResourceLocation side = blockTexture(block);
        ResourceLocation top = extend(side, "_top");
        simpleBlock(
                block,
                models().cube(
                        name(block),
                        top,
                        top,
                        side,
                        side,
                        side,
                        side
                ).texture("particle", side)
        );
    }

    private void pipeBlock(Block block) {
        ResourceLocation loc = BuiltInRegistries.BLOCK.getKey(block);
        MultiPartBlockStateBuilder builder = getMultipartBuilder(block);
        pipeConnection(builder, loc, Direction.DOWN, 0, 0);
        pipeConnection(builder, loc, Direction.UP, 180, 0);
        pipeConnection(builder, loc, Direction.NORTH, 90, 180);
        pipeConnection(builder, loc, Direction.EAST, 90, 270);
        pipeConnection(builder, loc, Direction.SOUTH, 90, 0);
        pipeConnection(builder, loc, Direction.WEST, 90, 90);
        builder.part().modelFile(pipeBaseModel(loc)).addModel().end();
    }

    private void pipeConnection(MultiPartBlockStateBuilder builder, ResourceLocation loc, Direction direction, int x, int y) {
        builder.part().modelFile(pipeConnectionModel(loc)).rotationX(x).rotationY(y).addModel()
                .condition(PipeBlock.CONNECTION[direction.get3DDataValue()], PipeBlock.PipeState.CONNECTED).end();
    }

    private void extractingPipeBlock(Block block) {
        ResourceLocation loc = BuiltInRegistries.BLOCK.getKey(block);
        MultiPartBlockStateBuilder builder = getMultipartBuilder(block);
        extractingPipeConnection(builder, loc, Direction.DOWN, 0, 0);
        extractingPipeConnection(builder, loc, Direction.UP, 180, 0);
        extractingPipeConnection(builder, loc, Direction.NORTH, 90, 180);
        extractingPipeConnection(builder, loc, Direction.EAST, 90, 270);
        extractingPipeConnection(builder, loc, Direction.SOUTH, 90, 0);
        extractingPipeConnection(builder, loc, Direction.WEST, 90, 90);
        builder.part().modelFile(pipeBaseModel(loc)).addModel().end();
    }

    private void extractingPipeConnection(MultiPartBlockStateBuilder builder, ResourceLocation loc, Direction direction, int x, int y) {
        builder.part().modelFile(pipeConnectionModel(loc)).rotationX(x).rotationY(y).addModel()
                .condition(PipeBlock.CONNECTION[direction.get3DDataValue()], PipeBlock.PipeState.CONNECTED).end()
                .part().modelFile(pipeExtractingModel(loc)).rotationX(x).rotationY(y).addModel()
                .condition(PipeBlock.CONNECTION[direction.get3DDataValue()], PipeBlock.PipeState.EXTRACTING).end();
    }

    private ModelFile pipeBaseModel(ResourceLocation blockLoc) {
        return models().withExistingParent(blockLoc.getPath() + "_base", modLoc("block/pipe_base"))
                .texture("texture", ResourceLocation.fromNamespaceAndPath(blockLoc.getNamespace(), "block/" + blockLoc.getPath()));
    }

    private ModelFile tankModel(ResourceLocation baseLoc, ResourceLocation topLoc, ResourceLocation sideLoc, ResourceLocation bottomLoc) {
        return models().withExistingParent(baseLoc.getPath(), modLoc("block/tank_base"))
                .texture("top", ResourceLocation.fromNamespaceAndPath(topLoc.getNamespace(), topLoc.getPath()))
                .texture("bottom", ResourceLocation.fromNamespaceAndPath(bottomLoc.getNamespace(), bottomLoc.getPath()))
                .texture("side", ResourceLocation.fromNamespaceAndPath(sideLoc.getNamespace(), sideLoc.getPath()));
    }

    private ModelFile pipeConnectionModel(ResourceLocation blockLoc) {
        return models().withExistingParent(blockLoc.getPath() + "_connection", modLoc("block/pipe_connection"))
                .texture("texture", ResourceLocation.fromNamespaceAndPath(blockLoc.getNamespace(), "block/" + blockLoc.getPath()));
    }

    private ModelFile pipeExtractingModel(ResourceLocation blockLoc) {
        return models().withExistingParent(blockLoc.getPath() + "_connection_extracting", modLoc("block/pipe_connection"))
                .texture("texture", ResourceLocation.fromNamespaceAndPath(blockLoc.getNamespace(), "block/" + blockLoc.getPath() + "_extracting"));
    }

    private ResourceLocation key(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    private String name(Block block) {
        return key(block).getPath();
    }

    public ResourceLocation blockTexture(Block block) {
        ResourceLocation name = key(block);
        return ResourceLocation.fromNamespaceAndPath(name.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + name.getPath());
    }

    private ResourceLocation extend(ResourceLocation rl, String suffix) {
        return ResourceLocation.fromNamespaceAndPath(rl.getNamespace(), rl.getPath() + suffix);
    }

}
