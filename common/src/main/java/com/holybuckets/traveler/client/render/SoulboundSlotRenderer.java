package com.holybuckets.traveler.client.render;

import com.holybuckets.traveler.core.ManagedTraveler;
import net.blay09.mods.balm.api.event.client.screen.ContainerScreenDrawEvent;
import net.blay09.mods.balm.api.event.client.GuiDrawEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;

import java.util.Set;

/**
 * Renders visual indicators for soulbound inventory slots
 * - Hotbar: Rendered on the in-game HUD
 * - Inventory Screen: Rendered on container/inventory screens
 */
public class SoulboundSlotRenderer {

    // Visual constants
    private static final int BORDER_COLOR = 0xFF000000; // Black (ARGB format)
    private static final int BORDER_THICKNESS = 2;
    private static final int BORDER_THICKNESS_HOVER = 1;
    private static final int SLOT_SIZE = 16; // Standard Minecraft slot size

    private static final int BORDER_COLOR_OUTER = 0xFF1a1a3d; // Dark blue (outer layer)
    private static final int BORDER_COLOR_INNER = 0xFF00ffff; // Aqua/cyan (inner layer)

    // Hotbar constants
    private static final int HOTBAR_SLOTS = 9;
    private static final int HOTBAR_Y_OFFSET = 22; // Distance from bottom of screen to hotbar top

    /**
     * Renders soulbound indicators on the in-game hotbar (HUD)
     * Called from GuiDrawEvent.Post
     */
    public static void renderHotbarSoulboundIndicators(GuiDrawEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        ManagedTraveler traveler = ManagedTraveler.localTraveler;
        if (traveler == null) return;

        Set<Integer> soulboundSlots = traveler.getSoulboundSlots();
        if (soulboundSlots.isEmpty()) return;

        GuiGraphics graphics = event.getGuiGraphics();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        // Hotbar is centered at bottom of screen
        // Hotbar spans: 9 slots * 20 pixels per slot (16px slot + 4px spacing) = 182 pixels total
        int hotbarWidth = 182;
        int hotbarStartX = (screenWidth / 2) - (hotbarWidth / 2);
        int hotbarY = screenHeight - HOTBAR_Y_OFFSET;

        // Render indicator for each soulbound hotbar slot
        for (int hotbarSlot = 0; hotbarSlot < HOTBAR_SLOTS; hotbarSlot++) {
            // Hotbar slots are indices 0-8 in player inventory
            if (soulboundSlots.contains(hotbarSlot)) {
                int slotX = hotbarStartX + (hotbarSlot * 20) + 3; // 20px spacing, 3px offset for slot centering
                int slotY = hotbarY + 3; // 3px offset for slot centering

                drawSlotBorderGradient(graphics, slotX, slotY);
            }
        }
    }

    /**
     * Renders soulbound indicators on inventory/container screens
     * Called from ContainerScreenDrawEvent.Foreground
     */
    public static void renderInventorySoulboundIndicators(ContainerScreenDrawEvent.Foreground event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        ManagedTraveler traveler = ManagedTraveler.localTraveler;
        if (traveler == null) return;

        Set<Integer> soulboundSlots = traveler.getSoulboundSlots();
        if (soulboundSlots.isEmpty()) return;

        // Get screen and verify it's a container screen
        Screen screenBase = event.getScreen();
        if (!(screenBase instanceof AbstractContainerScreen)) return;

        AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) screenBase;
        GuiGraphics graphics = event.getGuiGraphics();

        // Get mouse position for hover detection
        double mouseX = mc.mouseHandler.xpos() * mc.getWindow().getGuiScaledWidth() / mc.getWindow().getScreenWidth();
        double mouseY = mc.mouseHandler.ypos() * mc.getWindow().getGuiScaledHeight() / mc.getWindow().getScreenHeight();

        // Iterate through all slots in the screen and check if they're soulbound
        for (Slot slot : screen.getMenu().slots) {
            // Check if this slot belongs to the player's inventory
            if (isPlayerInventorySlot(slot, player)) {
                int slotIndex = getPlayerSlotIndex(slot, player);

                if (soulboundSlots.contains(slotIndex)) {
                    // Calculate absolute slot position on screen
                    int slotScreenX = screen.width + slot.x;
                    int slotScreenY = screen.height + slot.y;

                    // Check if mouse is hovering over this slot
                    boolean isHovering = mouseX >= slotScreenX && mouseX < slotScreenX + SLOT_SIZE &&
                        mouseY >= slotScreenY && mouseY < slotScreenY + SLOT_SIZE;

                    // Draw border at slot's position (relative to container)
                    drawSlotBorder(graphics, slot.x, slot.y, isHovering);
                }
            }
        }
    }

    /**
     * Draws a dual-colored border around a slot at the given position
     * Uses ender pearl theme: dark blue outer layer, aqua inner layer
     *
     * @param graphics GuiGraphics for rendering
     * @param x Slot X position (top-left corner)
     * @param y Slot Y position (top-left corner)
     * @param isHovering Whether the mouse is hovering over this slot
     */
    private static void drawSlotBorder(GuiGraphics graphics, int x, int y, boolean isHovering) {
        int z = 300; // Z-level to render on top of slots but below tooltips
        int thickness = isHovering ? BORDER_THICKNESS_HOVER : BORDER_THICKNESS;

      //give a simple single color border
        graphics.fill(x - thickness, y - thickness, x + SLOT_SIZE + thickness, y, z, BORDER_COLOR);
        graphics.fill(x - thickness, y + SLOT_SIZE, x + SLOT_SIZE + thickness, y + SLOT_SIZE + thickness, z, BORDER_COLOR);
        graphics.fill(x - thickness, y, x, y + SLOT_SIZE, z, BORDER_COLOR);
        graphics.fill(x + SLOT_SIZE, y, x + SLOT_SIZE + thickness, y + SLOT_SIZE, z, BORDER_COLOR);
    }


    private static void drawSlotBorderGradient(GuiGraphics graphics, int x, int y)
    {
        int z = 300;
        int topColor = BORDER_COLOR_OUTER;
        int bottomColor = BORDER_COLOR_INNER;

        // Top border (solid top color)
        graphics.fill(x - BORDER_THICKNESS, y - BORDER_THICKNESS,
            x + SLOT_SIZE + BORDER_THICKNESS, y, z, topColor);

        // Bottom border (solid bottom color)
        graphics.fill(x - BORDER_THICKNESS, y + SLOT_SIZE,
            x + SLOT_SIZE + BORDER_THICKNESS,
            y + SLOT_SIZE + BORDER_THICKNESS, z, bottomColor);

        // Left/right borders could interpolate between colors
        graphics.fill(x - BORDER_THICKNESS, y, x, y + SLOT_SIZE, z, topColor);
        graphics.fill(x + SLOT_SIZE, y, x + SLOT_SIZE + BORDER_THICKNESS,
            y + SLOT_SIZE, z, bottomColor);
    }


    /**
     * Checks if a slot belongs to the player's inventory
     * (as opposed to a container like a chest)
     */
    private static boolean isPlayerInventorySlot(Slot slot, Player player) {
        return slot.container == player.getInventory();
    }

    /**
     * Converts a Slot to its index in the player's inventory
     * <p>
     * Minecraft inventory layout:
     * - Slots 0-8: Hotbar
     * - Slots 9-35: Main inventory (3 rows of 9)
     * - Slots 36-39: Armor (boots, leggings, chestplate, helmet)
     * - Slot 40: Offhand
     *
     * @param slot   The slot to convert
     * @param player The player
     * @return The inventory index (0-40), or -1 if not a player slot
     */
    private static int getPlayerSlotIndex(Slot slot, Player player) {
        if (!isPlayerInventorySlot(slot, player)) {
            return -1;
        }

        // The slot.getSlotIndex() returns the index within the container
        // For player inventory, this directly maps to inventory indices
        return slot.getContainerSlot();
    }

    /**
     * Alternative rendering method using slot index directly
     * Useful when you know the slot index but not the screen position
     *
     * @param graphics  GuiGraphics for rendering
     * @param screen    The inventory screen
     * @param slotIndex The player inventory slot index (0-40)
     */
    public static void renderSoulboundIndicatorByIndex(GuiGraphics graphics, AbstractContainerScreen<?> screen, int slotIndex, boolean isHovering) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        // Find the slot with this index
        for (Slot slot : screen.getMenu().slots) {
            if (isPlayerInventorySlot(slot, player) && getPlayerSlotIndex(slot, player) == slotIndex) {
                drawSlotBorder(graphics, slot.x, slot.y, isHovering);
                return;
            }
        }
    }

}
//END CLASS