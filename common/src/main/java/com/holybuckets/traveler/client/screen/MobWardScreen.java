package com.holybuckets.traveler.client.screen;

import com.holybuckets.traveler.Constants;
import com.holybuckets.traveler.menu.MobWardMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * Screen for Mob Ward menu
 */
public class MobWardScreen extends AbstractContainerScreen<MobWardMenu> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(
        Constants.MOD_ID, "textures/gui/container/mob_ward.png"
    ); // Using hopper texture as base (single slot container)

    public MobWardScreen(MobWardMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        // Draw background texture
        graphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);

        // Draw ward message at bottom
        Component wardMessage = this.menu.getWardMessage();
        if (wardMessage != null && !wardMessage.getString().isEmpty()) {
            int x = this.imageWidth / 2 - this.font.width(wardMessage) / 2;
            int y = 60; // Below the slot
            graphics.drawString(this.font, wardMessage, x, y, 0x404040, false);
        }
    }
}