// WeatheredBeaconScreen.java
package com.holybuckets.traveler.client.screen;

import com.google.common.collect.Lists;
import com.holybuckets.traveler.Constants;
import com.holybuckets.traveler.menu.WeatheredBeaconMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.BeaconScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ServerboundSetBeaconPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class WeatheredBeaconScreen extends AbstractContainerScreen<WeatheredBeaconMenu> {
    private static final ResourceLocation WEATHERED_BEACON_LOCATION =
        new ResourceLocation(Constants.MOD_ID, "textures/gui/container/weathered_beacon.png");
    private static final Component PRIMARY_EFFECT_LABEL =
        Component.translatable("block.minecraft.beacon.primary");

    // Weathered beacon has all effects available at level 2+, including regeneration
    private static final MobEffect[][] WEATHERED_BEACON_EFFECTS = new MobEffect[][] {
        {MobEffects.MOVEMENT_SPEED, MobEffects.DIG_SPEED}, // Tier 0
        {MobEffects.DAMAGE_RESISTANCE, MobEffects.JUMP},                             // Tier 1
        {MobEffects.DAMAGE_BOOST, MobEffects.REGENERATION}                                                    // Tier 2
    };

    private final List<WeatheredBeaconButton> beaconButtons = Lists.newArrayList();

    @Nullable
    private MobEffect primary;

    public WeatheredBeaconScreen(final WeatheredBeaconMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 230;
        this.imageHeight = 219;

        menu.addSlotListener(new ContainerListener() {
            @Override
            public void slotChanged(AbstractContainerMenu container, int slotId, ItemStack stack) {
            }

            @Override
            public void dataChanged(AbstractContainerMenu container, int dataId, int value) {
                WeatheredBeaconScreen.this.primary = menu.getPrimaryEffect();
            }
        });
    }

    private <T extends AbstractWidget & WeatheredBeaconButton> void addWeatheredBeaconButton(T button) {
        this.addRenderableWidget(button);
        this.beaconButtons.add(button);
    }

    @Override
    protected void init() {
        super.init();
        this.beaconButtons.clear();

        // Add confirm button (no cancel button for weathered beacon)
        this.addWeatheredBeaconButton(new ConfirmButton(this.leftPos + 164, this.topPos + 107));

        // Add all effect buttons - 3 tiers, all available at level 2+
        for (int tier = 0; tier <= 2; ++tier) {
            int effectCount = WEATHERED_BEACON_EFFECTS[tier].length;
            int totalWidth = effectCount * 22 + (effectCount - 1) * 2;

            for (int i = 0; i < effectCount; ++i) {
                MobEffect effect = WEATHERED_BEACON_EFFECTS[tier][i];
                EffectButton button = new EffectButton(
                    this.leftPos + 76 + i * 24 - totalWidth / 2,
                    this.topPos + 22 + tier * 25,
                    effect,
                    tier
                );
                button.active = false;
                this.addWeatheredBeaconButton(button);
            }
        }
    }

    @Override
    public void containerTick() {
        super.containerTick();
        this.updateButtons();
    }

    private void updateButtons() {
        int pyramidLevel = this.menu.getLevels();
        this.beaconButtons.forEach(button -> button.updateStatus(pyramidLevel));
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawCenteredString(this.font, PRIMARY_EFFECT_LABEL, 62, 10, 14737632);
        graphics.drawCenteredString(this.font, CommonComponents.GUI_DONE, 169, 17, 14737632);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int leftPos = (this.width - this.imageWidth) / 2;
        int topPos = (this.height - this.imageHeight) / 2;
        graphics.blit(WEATHERED_BEACON_LOCATION, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);

        // Render payment item icons
        graphics.pose().pushPose();
        graphics.pose().translate(0.0F, 0.0F, 100.0F);
        graphics.renderItem(new ItemStack(Items.NETHERITE_INGOT), leftPos + 20, topPos + 109);
        graphics.renderItem(new ItemStack(Items.EMERALD), leftPos + 41, topPos + 109);
        graphics.renderItem(new ItemStack(Items.DIAMOND), leftPos + 41 + 22, topPos + 109);
        graphics.renderItem(new ItemStack(Items.GOLD_INGOT), leftPos + 42 + 44, topPos + 109);
        graphics.renderItem(new ItemStack(Items.IRON_INGOT), leftPos + 42 + 66, topPos + 109);
        graphics.pose().popPose();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    // Interface for button status updates
    @OnlyIn(Dist.CLIENT)
    private interface WeatheredBeaconButton {
        void updateStatus(int pyramidLevel);
    }

    // Base button class
    @OnlyIn(Dist.CLIENT)
    private abstract static class WeatheredBeaconScreenButton extends AbstractButton implements WeatheredBeaconButton {
        private boolean selected;

        protected WeatheredBeaconScreenButton(int x, int y) {
            super(x, y, 22, 22, CommonComponents.EMPTY);
        }

        protected WeatheredBeaconScreenButton(int x, int y, Component component) {
            super(x, y, 22, 22, component);
        }

        @Override
        public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            int textureX = 0;

            if (!this.active) {
                textureX += this.width * 2;
            } else if (this.selected) {
                textureX += this.width * 1;
            } else if (this.isHoveredOrFocused()) {
                textureX += this.width * 3;
            }

            graphics.blit(WEATHERED_BEACON_LOCATION, this.getX(), this.getY(), textureX, 219, this.width, this.height);
            this.renderIcon(graphics);
        }

        protected abstract void renderIcon(GuiGraphics graphics);

        public boolean isSelected() {
            return this.selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        @Override
        public void updateWidgetNarration(NarrationElementOutput output) {
            this.defaultButtonNarrationText(output);
        }
    }

    // Confirm button
    @OnlyIn(Dist.CLIENT)
    private class ConfirmButton extends WeatheredBeaconScreenButton {
        public ConfirmButton(int x, int y) {
            super(x, y, CommonComponents.GUI_DONE);
        }

        @Override
        protected void renderIcon(GuiGraphics graphics) {
            graphics.blit(WEATHERED_BEACON_LOCATION, this.getX() + 2, this.getY() + 2, 90, 220, 18, 18);
        }

        @Override
        public void onPress() {
            WeatheredBeaconScreen.this.minecraft.getConnection().send(new ServerboundSetBeaconPacket(Optional.ofNullable(WeatheredBeaconScreen.this.primary), Optional.empty()));
            WeatheredBeaconScreen.this.minecraft.player.closeContainer();
        }

        @Override
        public void updateStatus(int pyramidLevel) {
            // Active only if there's a payment item and an effect is selected
            this.active = WeatheredBeaconScreen.this.menu.hasPayment() &&
                WeatheredBeaconScreen.this.primary != null;
        }
    }

    // Effect selection button
    @OnlyIn(Dist.CLIENT)
    private class EffectButton extends WeatheredBeaconScreenButton {
        private final int tier;
        private final MobEffect effect;
        private final TextureAtlasSprite sprite;

        public EffectButton(int x, int y, MobEffect effect, int tier) {
            super(x, y);
            this.tier = tier;
            this.effect = effect;
            this.sprite = Minecraft.getInstance().getMobEffectTextures().get(effect);
            this.setTooltip(Tooltip.create(this.createEffectDescription(effect), null));
        }

        protected MutableComponent createEffectDescription(MobEffect effect) {
            // Show "Effect II" at level 4, otherwise just "Effect"
            Component baseDescription = Component.translatable(effect.getDescriptionId());
            int pyramidLevel = WeatheredBeaconScreen.this.menu.getLevels();

            if (pyramidLevel >= 4) {
                return baseDescription.copy().append(" II");
            }

            return (MutableComponent) baseDescription;
        }

        @Override
        public void onPress() {
            if (!this.isSelected()) {
                WeatheredBeaconScreen.this.primary = this.effect;
                WeatheredBeaconScreen.this.updateButtons();
            }
        }

        @Override
        protected void renderIcon(GuiGraphics graphics) {
            graphics.blit(this.getX() + 2, this.getY() + 2, 0, 18, 18, this.sprite);
        }

        @Override
        public void updateStatus(int pyramidLevel) {
            // Active at level 2+, inactive at level 0-1
            this.active = pyramidLevel >= 2;
            this.setSelected(this.effect == WeatheredBeaconScreen.this.primary);

            // Update tooltip to show "II" at level 4
            this.setTooltip(Tooltip.create(this.createEffectDescription(this.effect), null));
        }

        @Override
        protected MutableComponent createNarrationMessage() {
            return this.createEffectDescription(this.effect);
        }
    }
}