package com.holybuckets.traveler.item;

import com.holybuckets.traveler.effect.ModEffects;
import net.blay09.mods.balm.api.DeferredObject;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public class BlessingTravelerPotionItem extends Item {
    
    private static final int[] LEVEL_WEIGHTS = {50, 30, 15, 5}; // Weights for levels 1, 2, 3, 4
    private static final int DURATION = 24000 / 2; // Half a Minecraft day
    
    private final Supplier<MobEffect> effect;
    private final float r, g, b;

    public BlessingTravelerPotionItem(DeferredObject<MobEffect> effect, int color, Properties properties) {
        super(properties);
        this.effect = () -> effect.get();
        this.r = ((color >> 16)  & 0xFF) / 255.0f;
        this.g = ((color >> 8)   & 0xFF) / 255.0f;
        this.b = ((color)        & 0xFF) / 255.0f;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        
        if (!level.isClientSide)
        {
            int duration = DURATION / 2; //for dev
            MobEffectInstance effectInstance = new MobEffectInstance(
                this.effect.get(),
                duration,
                0, true, false, true
            );
            
            player.addEffect(effectInstance);
            if (!player.getAbilities().instabuild) {
                itemStack.shrink(1);
            }
        }
        
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }
}
