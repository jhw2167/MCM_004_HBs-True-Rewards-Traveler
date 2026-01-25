package com.holybuckets.traveler.item;

import com.holybuckets.traveler.core.ManagedTraveler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;

/**
 * Soulbound Ritual Tablet - Marks the inventory slot it's used in as soulbound
 * Items in soulbound slots are kept after death
 */
public class SoulboundRitualTabletItem extends InteractiveRewardItem {

    public static final String SOULBOUND_TAG = "hbs_soulbound_slot";

    public SoulboundRitualTabletItem() {
        super("soulbound_ritual_tablet", true); // Consumes on use
    }

    @Override
    protected InteractionResult onRightClickAir(Level level, Player player, InteractionHand hand, ItemStack stack) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            return markSlotAsSoulbound(serverPlayer, hand);
        }
        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
    }

    private InteractionResult markSlotAsSoulbound(ServerPlayer player, InteractionHand hand) {
        ManagedTraveler mp = ManagedTraveler.getManagedTraveler(player);
        mp.addSoulboundSlot(player.getInventory().selected);
        return InteractionResult.CONSUME;
    }

    /**
     * Helper method to check if a slot is soulbound for a player
     */
    public static boolean isSlotSoulbound(Player player, int slotIndex) {
        ManagedTraveler mp = ManagedTraveler.getManagedTraveler(player);
        return mp.isSlotSoulbound(slotIndex);
    }
}