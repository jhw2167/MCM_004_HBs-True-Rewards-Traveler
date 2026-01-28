package com.holybuckets.traveler.client.screen;

import com.holybuckets.traveler.menu.PotionPotMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * Screen for Potion Pot menu
 * Shows a fully active brewing stand (cosmetic only)
 */
public class PotionPotScreen extends AbstractContainerScreen<PotionPotMenu> {

    private static final ResourceLocation BREWING_STAND_TEXTURE = new ResourceLocation(
        "minecraft", "textures/gui/container/brewing_stand.png"
    );

    public PotionPotScreen(PotionPotMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        // Draw background
        graphics.blit(BREWING_STAND_TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        // Draw FULL fuel indicator (blaze powder) - always full
        // Full fuel bar (no empty part shown)
        graphics.blit(BREWING_STAND_TEXTURE, x + 60, y + 44, 176, 29, 9, 18);

        // Draw FULL brewing bubbles - always active/brewing appearance
        // Full bubble column (maximum brew progress)
        graphics.blit(BREWING_STAND_TEXTURE, x + 63, y + 14, 185, 0, 12, 29);

        // Draw FULL brewing arrow - always showing full progress
        // Full arrow (completely filled)
        graphics.blit(BREWING_STAND_TEXTURE, x + 97, y + 16, 176, 0, 9, 28);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        // Draw title
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);

        // Draw "Inventory" label
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);

        // No "Locked" message - removed as requested
    }
}