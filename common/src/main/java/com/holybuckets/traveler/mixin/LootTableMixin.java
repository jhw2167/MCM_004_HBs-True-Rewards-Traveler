package com.holybuckets.traveler.mixin;

import com.holybuckets.traveler.config.ModConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * Mixin to capture entity loot tables for mob ward filtering
 */
@Mixin(LootTable.class)
public class LootTableMixin {

    @Shadow
    @Final
    private List<LootPool> pools;

    @Shadow
    @Final
    private LootItemFunction[] functions;

    @Inject(
        method = "setLootTableId",
        at = @At("TAIL"),
        remap = false
    )
    private void onSetLootTableId(ResourceLocation id, CallbackInfo ci)
    {
        if (pools == null || pools.isEmpty()) return;
        if (id == null || !id.getPath().contains("entities")) return;
        ModConfig.loadEntityLootTables(id, pools, functions);
    }
}