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
        if(mc.options.hideGui) return;

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

        int selectedHotbarIndex = traveler.getCurrentlySelectedHotbarIndex();

        for (int hotbarSlot = 0; hotbarSlot < HOTBAR_SLOTS; hotbarSlot++) {
            if (soulboundSlots.contains(hotbarSlot)) {
                int slotX = hotbarStartX + (hotbarSlot * 20) + 3;
                int slotY = hotbarY + 3;

                boolean isSelected = (hotbarSlot == selectedHotbarIndex);
                int thickness = isSelected ? BORDER_THICKNESS_SELECTED : BORDER_THICKNESS;

                boolean suppressRightBorder = soulboundSlots.contains(hotbarSlot) && (hotbarSlot + 1 == selectedHotbarIndex);
                boolean suppressLeftBorder  = soulboundSlots.contains(hotbarSlot) && (hotbarSlot - 1 == selectedHotbarIndex);

                drawPulsatingBorder(graphics, slotX, slotY, thickness, suppressLeftBorder, suppressRightBorder);
            }
        }

        if (soulboundSlots.contains(40) && !player.getOffhandItem().isEmpty()) {
            int offhandX = hotbarStartX - 26;  // 29px left of hotbar start
            int offhandY = hotbarY + 3;
            drawPulsatingBorder(graphics, offhandX, offhandY, BORDER_THICKNESS, false, false);
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
            if (isPlayerInventorySlot(slot, player))
            {
                int slotIndex = getPlayerSlotIndex(slot, player);

                if (soulboundSlots.contains(slotIndex)) {
                    int thickness = BORDER_THICKNESS;

                    if (USE_SIMPLE_BLACK_BORDER) {
                        drawSimpleBorder(graphics, slot.x, slot.y, thickness, BORDER_COLOR_BLACK, false, false);
                    } else if (ENABLE_PULSATING_INVENTORY) {
                        drawPulsatingBorder(graphics, slot.x, slot.y, thickness, false, false);
                    } else {
                        drawSimpleBorder(graphics, slot.x, slot.y, thickness, PULSE_COLOR_BRIGHT, false, false);
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
    private static void drawPulsatingBorder(GuiGraphics graphics, int x, int y, int thickness,
                                            boolean suppressLeftBorder, boolean suppressRightBorder) {
        long currentTime = System.currentTimeMillis();
        float pulseProgress = (currentTime % (long)PULSE_SPEED) / PULSE_SPEED;
        float pulse = (float) Math.sin(pulseProgress * Math.PI * 2.0) * 0.5f + 0.5f;

        float alpha = PULSE_MIN_ALPHA + (pulse * (PULSE_MAX_ALPHA - PULSE_MIN_ALPHA));
        int color = interpolateColor(PULSE_COLOR_DARK, PULSE_COLOR_BRIGHT, alpha);

        drawSimpleBorder(graphics, x, y, thickness, color, suppressLeftBorder, suppressRightBorder);
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
    private static void drawSimpleBorder(GuiGraphics graphics, int x, int y, int thickness, int color,
                                         boolean suppressLeftBorder, boolean suppressRightBorder) {
        int z = 300;

        int topBottomLeft  = suppressLeftBorder  ? x : x - thickness;
        int topBottomRight = suppressRightBorder ? x + SLOT_SIZE : x + SLOT_SIZE + thickness;

        graphics.fill(topBottomLeft, y - thickness, topBottomRight, y, z, color);
        graphics.fill(topBottomLeft, y + SLOT_SIZE, topBottomRight, y + SLOT_SIZE + thickness, z, color);

        if (!suppressLeftBorder) {
            graphics.fill(x - thickness, y, x, y + SLOT_SIZE, z, color);
        }
        if (!suppressRightBorder) {
            graphics.fill(x + SLOT_SIZE, y, x + SLOT_SIZE + thickness, y + SLOT_SIZE, z, color);
        }
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


}
//END CLASS