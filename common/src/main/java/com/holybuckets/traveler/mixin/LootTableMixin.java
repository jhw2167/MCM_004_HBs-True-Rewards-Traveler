package com.holybuckets.traveler.mixin;

import com.holybuckets.traveler.config.ModConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LootTable.class)
public class LootTableMixin {

    @Inject(
        method = "<init>",
        at = @At("TAIL")
    )
    private void onInit(
        LootContextParamSet paramSet,
        @Nullable ResourceLocation randomSequence,
        LootPool[] pools,
        LootItemFunction[] functions,
        CallbackInfo ci
    ) {
        LootTable self = (LootTable) (Object) this;

        // randomSequence doubles as the loot table ID on Fabric's merged jar
        if (randomSequence == null || !randomSequence.getPath().contains("entities")) return;
        if (pools == null || pools.length == 0) return;

        ModConfig.saveEntityLootTables(randomSequence, pools, functions);
    }
}