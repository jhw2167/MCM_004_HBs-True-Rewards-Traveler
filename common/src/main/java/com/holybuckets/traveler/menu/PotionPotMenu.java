package com.holybuckets.traveler.menu;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;

import java.util.Random;

/**
 * Menu for Potion Pot item
 * Displays as an active brewing stand with immutable awkward potions and blaze powder
 */
public class PotionPotMenu extends AbstractContainerMenu {

    private final Container brewingContainer;
    private final ItemStack potionPotStack;
    private final int awkwardPotionCount;

    // Client-side constructor
    public PotionPotMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory, playerInventory.getSelected(), new SimpleContainer(5), new Random().nextInt(3) + 1);
    }

    // Server-side constructor
    public PotionPotMenu(int containerId, Inventory playerInventory, ItemStack potionPotStack, Container brewingContainer, int awkwardPotionCount) {
        super(ModMenus.POTION_POT_MENU.get(), containerId);

        checkContainerSize(brewingContainer, 5);

        this.brewingContainer = brewingContainer;
        this.potionPotStack = potionPotStack;
        this.awkwardPotionCount = awkwardPotionCount;

        // Add potion slots (bottom 3 slots) - indices 0, 1, 2
        this.addSlot(new LockedPotionSlot(brewingContainer, 0, 56, 51));
        this.addSlot(new LockedPotionSlot(brewingContainer, 1, 79, 58));
        this.addSlot(new LockedPotionSlot(brewingContainer, 2, 102, 51));

        // Add ingredient slot (top slot) - index 3 - OPEN for player
        this.addSlot(new Slot(brewingContainer, 3, 79, 17) {
            @Override
            public void setChanged() {
                super.setChanged();
                // Save to NBT whenever ingredient changes
                saveToNBT();
            }

            @Override
            public int getMaxStackSize() {
                return 64;
            }
        });

        // Add fuel slot (blaze powder) - index 4 - LOCKED
        this.addSlot(new LockedFuelSlot(brewingContainer, 4, 17, 17));

        // Add player inventory
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        // Load from NBT or initialize
        if (!loadFromNBT()) {
            initializeContents();
        }
    }

    /**
     * Initialize the brewing stand with awkward potions and blaze powder
     */
    private void initializeContents() {
        // Add awkward potions to slots (1-3 random)
        ItemStack awkwardPotion = new ItemStack(Items.POTION);
        PotionUtils.setPotion(awkwardPotion, Potions.AWKWARD);

        for (int i = 0; i < awkwardPotionCount; i++) {
            brewingContainer.setItem(i, awkwardPotion.copy());
        }

        // Fill empty potion slots
        for (int i = awkwardPotionCount; i < 3; i++) {
            brewingContainer.setItem(i, ItemStack.EMPTY);
        }

        // Add blaze powder to fuel slot
        brewingContainer.setItem(4, new ItemStack(Items.BLAZE_POWDER, 1));

        // Ingredient slot starts empty
        brewingContainer.setItem(3, ItemStack.EMPTY);

        // Save initial state
        saveToNBT();
    }

    /**
     * Save brewing container contents to Potion Pot's NBT
     */
    private void saveToNBT() {
        if (potionPotStack.isEmpty()) return;

        CompoundTag tag = potionPotStack.getOrCreateTag();

        // Save ingredient slot (slot 3)
        ItemStack ingredient = brewingContainer.getItem(3);
        if (!ingredient.isEmpty()) {
            CompoundTag ingredientTag = new CompoundTag();
            ingredient.save(ingredientTag);
            tag.put("Ingredient", ingredientTag);
        } else {
            tag.remove("Ingredient");
        }

        // Save awkward potion count
        tag.putInt("AwkwardPotionCount", awkwardPotionCount);
    }

    /**
     * Load brewing container contents from Potion Pot's NBT
     * @return true if loaded successfully, false if needs initialization
     */
    private boolean loadFromNBT() {
        if (potionPotStack.isEmpty() || !potionPotStack.hasTag()) {
            return false;
        }

        CompoundTag tag = potionPotStack.getTag();

        // Load awkward potions
        ItemStack awkwardPotion = new ItemStack(Items.POTION);
        PotionUtils.setPotion(awkwardPotion, Potions.AWKWARD);

        int savedCount = tag.contains("AwkwardPotionCount") ? tag.getInt("AwkwardPotionCount") : awkwardPotionCount;
        for (int i = 0; i < savedCount; i++) {
            brewingContainer.setItem(i, awkwardPotion.copy());
        }

        for (int i = savedCount; i < 3; i++) {
            brewingContainer.setItem(i, ItemStack.EMPTY);
        }

        // Load ingredient
        if (tag.contains("Ingredient")) {
            ItemStack ingredient = ItemStack.of(tag.getCompound("Ingredient"));
            brewingContainer.setItem(3, ingredient);
        } else {
            brewingContainer.setItem(3, ItemStack.EMPTY);
        }

        // Always add blaze powder
        brewingContainer.setItem(4, new ItemStack(Items.BLAZE_POWDER, 1));

        return true;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);

        // Save when menu closes
        if (!player.level().isClientSide()) {
            saveToNBT();
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            result = slotStack.copy();

            // Don't allow shift-clicking from locked slots
            if (index < 3 || index == 4) {
                return ItemStack.EMPTY;
            }

            if (index < 5) {
                // Moving from ingredient slot to player inventory
                if (!this.moveItemStackTo(slotStack, 5, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Moving from player inventory to ingredient slot
                if (!this.moveItemStackTo(slotStack, 3, 4, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        return brewingContainer.stillValid(player);
    }

    public int getAwkwardPotionCount() {
        return awkwardPotionCount;
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    // Locked slot classes remain the same
    private static class LockedPotionSlot extends Slot {
        public LockedPotionSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public boolean mayPickup(Player player) {
            return false;
        }

        @Override
        public ItemStack remove(int amount) {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean isActive() {
            return true;
        }
    }

    private static class LockedFuelSlot extends Slot {
        public LockedFuelSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public boolean mayPickup(Player player) {
            return false;
        }

        @Override
        public ItemStack remove(int amount) {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean isActive() {
            return true;
        }
    }
}