package com.holybuckets.traveler.block;

import com.holybuckets.traveler.Constants;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.block.BalmBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModBlocks {

    public static Block templateBlock;
    public static Block weatheredBeacon;
    public static Block ironBlockFacade;
    public static Block goldBlockFacade;
    public static Block diamondBlockFacade;
    public static Block emeraldBlockFacade;

    //public static Block[] scopedSharestones = new SharestoneBlock[DyeColor.values().length];

    public static void initialize(BalmBlocks blocks) {
        //blocks.register(() -> templateBlock = new EmptyBlock(defaultProperties()), () -> itemBlock(templateBlock), id("template_block"));

        blocks.register(
            () -> weatheredBeacon = new WeatheredBeaconBlock(weatheredBeaconProperties()),
            () -> itemBlock(weatheredBeacon),
            id("weathered_beacon")
        );

        blocks.register(
            () -> ironBlockFacade = new MetalFacadeBlock(facadeProperties()),
            () -> itemBlock(ironBlockFacade),
            id("iron_block_facade")
        );

        blocks.register(
            () -> goldBlockFacade = new MetalFacadeBlock(facadeProperties()),
            () -> itemBlock(goldBlockFacade),
            id("gold_block_facade")
        );

        blocks.register(
            () -> diamondBlockFacade = new MetalFacadeBlock(facadeProperties()),
            () -> itemBlock(diamondBlockFacade),
            id("diamond_block_facade")
        );

        blocks.register(
            () -> emeraldBlockFacade = new MetalFacadeBlock(facadeProperties()),
            () -> itemBlock(emeraldBlockFacade),
            id("emerald_block_facade")
        );
    }

    private static BlockItem itemBlock(Block block) {
        return new BlockItem(block, Balm.getItems().itemProperties());
    }

    private static ResourceLocation id(String name) {
        return new ResourceLocation(Constants.MOD_ID, name);
    }

    private static BlockBehaviour.Properties defaultProperties() {
        return Balm.getBlocks().blockProperties().sound(SoundType.STONE).strength(5f, 2000f);
    }

    private static BlockBehaviour.Properties weatheredBeaconProperties() {
        return Balm.getBlocks().blockProperties()
            .sound(SoundType.METAL)
            .strength(3.0f)
            .lightLevel(state -> 15)
            .noOcclusion()
            .isRedstoneConductor((state, level, pos) -> false);
    }

    private static BlockBehaviour.Properties facadeProperties() {
        return Balm.getBlocks().blockProperties()
            .sound(SoundType.METAL)
            .strength(5.0f, 6.0f)
            .requiresCorrectToolForDrops();
    }
}