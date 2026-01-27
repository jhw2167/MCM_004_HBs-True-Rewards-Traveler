package com.holybuckets.traveler.client.render;

import com.holybuckets.traveler.core.ManagedTraveler;
import net.blay09.mods.balm.api.event.client.screen.ContainerScreenDrawEvent;
import net.blay09.mods.balm.api.event.client.GuiDrawEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;

import java.util.Set;

/**
 * Renders visual indicators for soulbound inventory slots
 * - Hotbar: Rendered on the in-game HUD
 * - Inventory Screen: Rendered on container/inventory screens
 */
public class SoulboundSlotRenderer {

    // ===== CONFIGURABLE SETTINGS - Adjust these as needed =====

    /** Enable simple black border mode (disables pulsating effect) */
    public static boolean USE_SIMPLE_BLACK_BORDER = false;

    /** Enable pulsating effect for inventory screens */
    public static boolean ENABLE_PULSATING_INVENTORY = true;

    // Border thickness settings
    public static int BORDER_THICKNESS = 2;
    public static int BORDER_THICKNESS_HOVER = 1; // When hovering in inventory
    public static int BORDER_THICKNESS_SELECTED = 1; // When hotbar slot is selected

    // Pulsating animation settings
    /** Milliseconds for one complete pulse cycle (lower = faster pulsing) */
    public static float PULSE_SPEED = 2000.0f;

    /** Minimum brightness during pulse (0.0 = invisible, 1.0 = full brightness) */
    public static float PULSE_MIN_ALPHA = 0.4f;

    /** Maximum brightness during pulse (0.0 = invisible, 1.0 = full brightness) */
    public static float PULSE_MAX_ALPHA = 1.0f;

    // Pulsating colors - deep dark purple/black theme
    public static int PULSE_COLOR_DARK = 0xFF000000; // black
    public static int PULSE_COLOR_BRIGHT = 0xFF0a0015; // Very dark purple (almost black)

    // ===== CONSTANTS =====

    private static final int BORDER_COLOR_BLACK = 0xFF000000; // Pure black
    private static final int SLOT_SIZE = 16; // Standard Minecraft slot size
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

        // Get currently selected hotbar slot from player
        int selectedHotbarIndex = traveler.getCurrentlySelectedHotbarIndex();

        // Render indicator for each soulbound hotbar slot
        for (int hotbarSlot = 0; hotbarSlot < HOTBAR_SLOTS; hotbarSlot++) {
            // Hotbar slots are indices 0-8 in player inventory
            if (soulboundSlots.contains(hotbarSlot)) {
                int slotX = hotbarStartX + (hotbarSlot * 20) + 3; // 20px spacing, 3px offset for slot centering
                int slotY = hotbarY + 3; // 3px offset for slot centering

                // Reduce thickness if this slot is currently selected by mouse wheel
                boolean isSelected = (hotbarSlot == selectedHotbarIndex);
                int thickness = isSelected ? BORDER_THICKNESS_SELECTED : BORDER_THICKNESS;

                // Always use pulsating effect for hotbar (looks better in-game)
                drawPulsatingBorder(graphics, slotX, slotY, thickness);
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

                    // Determine thickness based on hover state
                    int thickness = isHovering ? BORDER_THICKNESS_HOVER : BORDER_THICKNESS;

                    // Draw border based on settings
                    if (USE_SIMPLE_BLACK_BORDER) {
                        // Simple black border
                        drawSimpleBorder(graphics, slot.x, slot.y, thickness, BORDER_COLOR_BLACK);
                    } else if (ENABLE_PULSATING_INVENTORY) {
                        // Pulsating purple border
                        drawPulsatingBorder(graphics, slot.x, slot.y, thickness);
                    } else {
                        // Static purple border (no pulsating)
                        drawSimpleBorder(graphics, slot.x, slot.y, thickness, PULSE_COLOR_BRIGHT);
                    }
                }
            }
        }
    }

    /**
     * Draws a pulsating purple/black border around a slot
     *
     * @param graphics GuiGraphics for rendering
     * @param x Slot X position (top-left corner)
     * @param y Slot Y position (top-left corner)
     * @param thickness Border thickness in pixels
     */
    private static void drawPulsatingBorder(GuiGraphics graphics, int x, int y, int thickness) {
        // Calculate pulse value (0.0 to 1.0) based on current time
        long currentTime = System.currentTimeMillis();
        float pulseProgress = (currentTime % (long)PULSE_SPEED) / PULSE_SPEED;
        float pulse = (float) Math.sin(pulseProgress * Math.PI * 2.0) * 0.5f + 0.5f; // Smooth sine wave

        // Map pulse to configured alpha range
        float alpha = PULSE_MIN_ALPHA + (pulse * (PULSE_MAX_ALPHA - PULSE_MIN_ALPHA));

        // Interpolate between dark and bright purple
        int color = interpolateColor(PULSE_COLOR_DARK, PULSE_COLOR_BRIGHT, alpha);

        // Draw the border
        drawSimpleBorder(graphics, x, y, thickness, color);
    }

    /**
     * Draws a simple single-color border around a slot
     *
     * @param graphics GuiGraphics for rendering
     * @param x Slot X position (top-left corner)
     * @param y Slot Y position (top-left corner)
     * @param thickness Border thickness in pixels
     * @param color Border color (ARGB format)
     */
    private static void drawSimpleBorder(GuiGraphics graphics, int x, int y, int thickness, int color) {
        int z = 300; // Z-level to render on top of slots but below tooltips

        // Top border
        graphics.fill(x - thickness, y - thickness, x + SLOT_SIZE + thickness, y, z, color);

        // Bottom border
        graphics.fill(x - thickness, y + SLOT_SIZE, x + SLOT_SIZE + thickness, y + SLOT_SIZE + thickness, z, color);

        // Left border
        graphics.fill(x - thickness, y, x, y + SLOT_SIZE, z, color);

        // Right border
        graphics.fill(x + SLOT_SIZE, y, x + SLOT_SIZE + thickness, y + SLOT_SIZE, z, color);
    }

    /**
     * Interpolates between two ARGB colors
     *
     * @param color1 Starting color (ARGB)
     * @param color2 Ending color (ARGB)
     * @param factor Interpolation factor (0.0 = color1, 1.0 = color2)
     * @return Interpolated color (ARGB)
     */
    private static int interpolateColor(int color1, int color2, float factor) {
        // Clamp factor to valid range
        factor = Math.max(0.0f, Math.min(1.0f, factor));

        // Extract ARGB components from both colors
        int a1 = (color1 >> 24) & 0xFF;
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int a2 = (color2 >> 24) & 0xFF;
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        // Interpolate each component
        int a = (int) (a1 + (a2 - a1) * factor);
        int r = (int) (r1 + (r2 - r1) * factor);
        int g = (int) (g1 + (g2 - g1) * factor);
        int b = (int) (b1 + (b2 - b1) * factor);

        // Combine back into ARGB format
        return (a << 24) | (r << 16) | (g << 8) | b;
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
     * @param isHovering Whether the slot is being hovered
     */
    public static void renderSoulboundIndicatorByIndex(GuiGraphics graphics, AbstractContainerScreen<?> screen, int slotIndex, boolean isHovering) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        // Find the slot with this index
        for (Slot slot : screen.getMenu().slots) {
            if (isPlayerInventorySlot(slot, player) && getPlayerSlotIndex(slot, player) == slotIndex) {
                int thickness = isHovering ? BORDER_THICKNESS_HOVER : BORDER_THICKNESS;

                if (USE_SIMPLE_BLACK_BORDER) {
                    drawSimpleBorder(graphics, slot.x, slot.y, thickness, BORDER_COLOR_BLACK);
                } else if (ENABLE_PULSATING_INVENTORY) {
                    drawPulsatingBorder(graphics, slot.x, slot.y, thickness);
                } else {
                    drawSimpleBorder(graphics, slot.x, slot.y, thickness, PULSE_COLOR_BRIGHT);
                }
                return;
            }
        }
    }

}
//END CLASS