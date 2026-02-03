package com.holybuckets.traveler.core;

import com.holybuckets.foundation.GeneralConfig;
import com.holybuckets.foundation.structure.StructureAPI;
import com.holybuckets.traveler.LoggerProject;
import com.holybuckets.traveler.TravelerRewardsMain;
import com.holybuckets.traveler.config.ModConfig;
import com.holybuckets.traveler.enchantment.ModEnchantments;
import io.netty.util.collection.IntObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.*;

import static com.holybuckets.foundation.CommonClass.MESSAGER;
import static com.holybuckets.foundation.HBUtil.BlockUtil;

/**
 * ItemImplementation - Singleton for handling item-specific functionality
 */
public class ItemImplementation {

    public static final String CLASS_ID = "021";

    private static ItemImplementation instance;
    private GeneralConfig GENERAL_CONFIG;


    private static final UUID PURE_HEART_MODIFIER_UUID = UUID.fromString("a3d89f7e-5c8d-4f3a-9b2e-1d4c6e8f0a1b");
    private static final String PURE_HEART_MODIFIER_NAME = "Pure Heart";
    private static final double HEALTH_PER_HEART = 2.0;

    private ItemImplementation() {
        GENERAL_CONFIG = GeneralConfig.getInstance();
    }

    public static ItemImplementation getInstance() {
        if (instance == null) {
            instance = new ItemImplementation();
        }
        return instance;
    }

    //** PURE HEART TRACKING

    /**
     * Records that the player consumed a Pure Heart
     */
    void setHealth(Player player, int heartsConsumed)
    {
        AttributeInstance healthAttribute = player.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttribute == null) {
            LoggerProject.logError("020002", "Failed to retrieve MAX_HEALTH attribute for unkown reason, player: " );
            return;
        }

        // Check if player already has the modifier (for stacking multiple pure hearts)
        AttributeModifier existingModifier = healthAttribute.getModifier(PURE_HEART_MODIFIER_UUID);

        //double currentBonus = existingModifier != null ? existingModifier.getAmount() : 0.0;
        double currentBonus = 0.0;
        double newBonus = currentBonus + (heartsConsumed * HEALTH_PER_HEART);

        // Remove existing modifier if present
        if (existingModifier != null) {
            healthAttribute.removeModifier(PURE_HEART_MODIFIER_UUID);
        }

        AttributeModifier newModifier = new AttributeModifier(
            PURE_HEART_MODIFIER_UUID, PURE_HEART_MODIFIER_NAME, newBonus,
            AttributeModifier.Operation.ADDITION
        );

        healthAttribute.addTransientModifier(newModifier);
        player.setHealth(player.getMaxHealth());
    }

    boolean isInStructure(Player player)
    {
        StructureAPI api = TravelerRewardsMain.STRUCTURE_APIS.get(player.level());
        BlockPos structurePos = api.nearestStructures(player.blockPosition(),1).get(0).getOrigin();
        if(structurePos == null) return false;
        if(BlockUtil.inRange(player.blockPosition(), structurePos, 64))
            return true;
        return false;
    }

    public static final int ESCAPE_ROPE_MAX_Y_CAVE_ESCAPE = 16;
    boolean isInDeepCaves(Player player) {
        return (player.blockPosition().getY() < ESCAPE_ROPE_MAX_Y_CAVE_ESCAPE);
    }

    /**
     * Finds the surface directly above the player's current position
     * @return BlockPos at surface level, or null if not found
     */
    @Nullable
    private BlockPos findSurfaceAbove(Player player)
    {
        if (!(player instanceof ServerPlayer serverPlayer)) return null;

        Level level = serverPlayer.level();
        BlockPos playerPos = player.blockPosition();
        int x = playerPos.getX();
        int z = playerPos.getZ();

        // Get the top solid block at this X,Z coordinate
        // This uses Minecraft's built-in heightmap which tracks the surface
        int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
        BlockPos surfacePos = new BlockPos(x, surfaceY, z);
        if (level.getBlockState(surfacePos).isAir() &&
            level.getBlockState(surfacePos.above()).isAir()) {
            return surfacePos;
        }

        return null;
    }

    void onUseEscapeRope(Player player, BlockPos structureEntryPos)
    {
        if(isInStructure(player) && structureEntryPos != null)
        {
            player.teleportTo(
                structureEntryPos.getX() + 0.5, structureEntryPos.getY(), structureEntryPos.getZ() + 0.5
            );
        } else if( isInDeepCaves(player) ) {
            BlockPos surface = findSurfaceAbove(player);
            if(surface != null)
                player.teleportTo( surface.getX() + 0.5, surface.getY(), surface.getZ() + 0.5);
        }
    }

    /**
     * Checks all items in player inventory for Lasting enchantment
     * Tracks expiration time and removes expired items
     */
    void checkLastingEnchantments(ServerPlayer player, IntObjectMap<ItemStack> lastingItems)
    {
        Inventory inventory = player.getInventory();
        long currentTick = GENERAL_CONFIG.getTotalTickCount();

        List<Integer> slotsToRemove = new ArrayList<>();
        for (Integer i : lastingItems.keySet())
        {
            ItemStack stack = inventory.getItem(i);
            if (stack.isEmpty()) continue;
            if(!stack.isEnchanted()) continue;

            int lastingLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.LASTING.get(), stack);
            if (lastingLevel > 0)
            {
                Long expirationTick = getLastingExpiration(stack);
                if (expirationTick == null)
                {
                    expirationTick = calculateLastingExpiration(stack, currentTick);
                    setLastingExpiration(stack, expirationTick);
                    LoggerProject.logDebug("020003",
                        String.format("Player %s has new Lasting item: %s, expires at tick %d",
                            player.getName().getString(), stack.getDisplayName().getString(), expirationTick));
                }

                // Check if item has expired
                if (currentTick >= expirationTick) {
                    slotsToRemove.add(i);

                    LoggerProject.logInfo("020004",
                        String.format("Lasting item expired for player %s: %s (tick %d >= %d)",
                            player.getName().getString(), stack.getDisplayName().getString(),
                            currentTick, expirationTick));
                }
            } else {
                removeLastingExpiration(stack);
            }
        }

        // Remove expired items
        for (int slot : slotsToRemove)
        {
            ItemStack expiredStack = inventory.getItem(slot);
            removeLastingExpiration(expiredStack);
            inventory.setItem(slot, ItemStack.EMPTY);

            // Optional: Notify player
            MESSAGER.sendBottomActionHint(
                Component.translatable("enchantment.hbs_traveler_rewards.lasting.expired",
                    expiredStack.getDisplayName()).getString()
            );
        }
    }

    private long calculateLastingExpiration(ItemStack stack, long currentTick)
    {
        if (stack.hasTag() && stack.getTag().contains("LastingDuration")) {
            long customDuration = stack.getTag().getLong("LastingDuration");
            return currentTick + customDuration;
        }
        int lastingLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.LASTING.get(), stack);
        if(lastingLevel == 0) {
            stack.enchant(ModEnchantments.LASTING.get(), 1);
            lastingLevel = 1;
        }
        int duration = ModConfig.LASTING_TICKS[ Math.min(9, lastingLevel - 1) ];
        return currentTick + duration;
    }

    @Nullable
    public Long getLastingExpiration(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("LastingExpiration")) {
            return stack.getTag().getLong("LastingExpiration");
        }
        return null;
    }

    public void setLastingExpiration(ItemStack stack, long expirationTick) {
        if (!stack.hasTag()) {
            stack.setTag(new CompoundTag());
        }
        stack.getTag().putLong("LastingExpiration", expirationTick);
    }

    public void removeLastingExpiration(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("LastingExpiration")) {
            stack.getTag().remove("LastingExpiration");
        }
    }


    //** Mob Ward **//
    void wardMobs(ServerPlayer player, IntObjectMap<ItemStack> mobWards,  Set<Entity> nearbyEntities)
    {
        for (ItemStack mobWardStack : mobWards.values()) {
            if(mobWardStack.getTag().contains("filterItem")) {
                ItemStack filterItem = ItemStack.of(mobWardStack.getTag().getCompound("filterItem"));
                nearbyEntities.forEach(e -> wardEntity(e, player, filterItem));
            }
        }
    }

    private static void wardEntity(Entity entity, Player player, ItemStack filterItem) {
        if(filterItem == null || filterItem.isEmpty()) return;
        if(!(entity instanceof Mob mob)) return;
        if(ModConfig.isMobWardedByItem(mob, filterItem.getItem()))
            wardMob(mob, player);
    }

    private static void wardMob(Mob mob, Player player)
    {
        if (mob.getTarget() == player) mob.setTarget(null);
        mob.getBrain().eraseMemory(net.minecraft.world.entity.ai.memory.MemoryModuleType.ATTACK_TARGET);
        mob.getBrain().eraseMemory(net.minecraft.world.entity.ai.memory.MemoryModuleType.ANGRY_AT);
        boolean tooClose = BlockUtil.inRange(mob.blockPosition(), player.blockPosition(), 16);
        if(tooClose) addFleeGoal(mob, player);
    }

    private static void addFleeGoal(Mob mob, Player player) {
        Vec3 fleeDirection = mob.position().subtract(player.position()).normalize();
        Vec3 fleeTarget = mob.position().add(fleeDirection.scale(5.0)); // Flee 5 blocks away
        mob.getNavigation().moveTo(fleeTarget.x, fleeTarget.y, fleeTarget.z, 1.2); // 1.2 = movement speed multiplier
    }

    //** POTION POT **//

    private static List<ItemStack> brewPotionPot(ServerPlayer player, ItemStack potionPotStack)
    {
        if (!potionPotStack.hasTag()) return List.of();
        CompoundTag tag = potionPotStack.getTag();

        int awkwardPotionCount = tag.getInt("AwkwardPotionCount");
        if (awkwardPotionCount <= 0) return List.of();

        List<ItemStack> potions = new ArrayList<>();
        for(int i=0; i<awkwardPotionCount; i++) {
            ItemStack awkwardPotion = new ItemStack(Items.POTION);
            PotionUtils.setPotion(awkwardPotion, Potions.AWKWARD);
            potions.add(awkwardPotion);
        }

        if (!tag.contains("Ingredient")) return potions;

        ItemStack ingredient = ItemStack.of(tag.getCompound("Ingredient"));
        if (ingredient.isEmpty()) return potions;
        if (!PotionBrewing.hasMix(ingredient, potions.get(0))) return potions;

        List<ItemStack> brewedPotions = new ArrayList<>();
        for(ItemStack basePotion : potions) {
            brewedPotions.add( PotionBrewing.mix(ingredient, basePotion) );
        }

        return brewedPotions;
    }

    void handlePotionPotToss(ServerPlayer player, ItemStack stack)
    {
        List<ItemStack> dropItems = brewPotionPot(player, stack);
        stack.shrink(1); // Consume one Potion Pot item
        player.level().playSound(null, player.blockPosition(),
            SoundEvents.GLASS_BREAK, SoundSource.PLAYERS,
            1.0f, 1.0f
        );

        if(!dropItems.isEmpty())
        {
            Vec3 inFront = player.getEyePosition().add(player.getLookAngle().scale(3.5));
            inFront = inFront.add(0, -0.5, 0); // Drop slightly below eye level
            for(ItemStack dropStack : dropItems)
            {
                ItemEntity itemEntity = new ItemEntity( player.level(),
                    inFront.x, inFront.y, inFront.z, dropStack
                );
                player.level().addFreshEntity(itemEntity);
            }
        }
    }

    private static final UUID WARRIOR_RITUAL_MODIFIER_UUID = UUID.fromString("b4e8af8f-6d9e-5f4b-ac3f-2e5d7f9d1c2d");
    private static final String WARRIOR_RITUAL_MODIFIER_NAME = "Warrior Ritual Bonus";
    private static final float ATK_SPEED_BONUS = 1.20f;

    /**
     * Applies warrior ritual attack speed bonus to player
     */
    void applyWarriorRitualBonus(Player player, int warriorTabletsUsed)
    {
        AttributeInstance attackSpeedAttribute = player.getAttribute(Attributes.ATTACK_SPEED);
        if (attackSpeedAttribute ==  null) return;

        // Calculate base attack speed from all other modifiers
        double baseAttackSpeed = Attributes.ATTACK_SPEED.getDefaultValue();
        Collection<AttributeModifier> attackSpeedModifiers = attackSpeedAttribute.getModifiers();
        for (AttributeModifier modifier : attackSpeedModifiers) {
            if (!modifier.getName().equals(WARRIOR_RITUAL_MODIFIER_NAME) && modifier.getAmount() > 0) {
                baseAttackSpeed += modifier.getAmount();
            }
        }

        // Remove existing warrior ritual modifier if present
        AttributeModifier existingModifier = attackSpeedAttribute.getModifier(WARRIOR_RITUAL_MODIFIER_UUID);
        if (existingModifier != null) {
            attackSpeedAttribute.removeModifier(WARRIOR_RITUAL_MODIFIER_UUID);
        }

        double bonusAmount = (baseAttackSpeed * Math.pow(ATK_SPEED_BONUS, warriorTabletsUsed)) - baseAttackSpeed;
        AttributeModifier newModifier = new AttributeModifier(
            WARRIOR_RITUAL_MODIFIER_UUID, WARRIOR_RITUAL_MODIFIER_NAME, bonusAmount,
            AttributeModifier.Operation.ADDITION
        );
        attackSpeedAttribute.addTransientModifier(newModifier);
    }
}
