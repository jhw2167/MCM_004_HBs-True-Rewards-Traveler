package com.holybuckets.traveler.menu;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.holybuckets.foundation.GeneralConfig;
import com.holybuckets.foundation.HBUtil;
import com.holybuckets.foundation.event.EventRegistrar;
import com.holybuckets.foundation.event.custom.SimpleMessageEvent;
import com.holybuckets.traveler.Constants;
import com.holybuckets.traveler.block.be.WeatheredBeaconBlockEntity;
import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.menu.BalmMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Optional;

import static net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT;

public class ModMenus {

    public static DeferredObject<MenuType<MobWardMenu>> MOB_WARD_MENU;
    public static DeferredObject<MenuType<PotionPotMenu>> POTION_POT_MENU;
    //for weatheed beacon
    public static DeferredObject<MenuType<WeatheredBeaconMenu>> WEATHERED_BEACON;


    public static void initialize(BalmMenus menu) {
        MOB_WARD_MENU = menu.registerMenu(
            new ResourceLocation(Constants.MOD_ID, "mob_ward"),
            MobWardMenu::new
        );

        POTION_POT_MENU = menu.registerMenu(
            new ResourceLocation(Constants.MOD_ID, "potion_pot"),
            PotionPotMenu::new
        );

        WEATHERED_BEACON = menu.registerMenu(
            new ResourceLocation(Constants.MOD_ID, "weathered_beacon"),
             WeatheredBeaconMenu::new
        );
    }


    public static void init(EventRegistrar reg) {
        reg.registerOnSimpleMessage(MSG_ID_SET_EFFECT, ModMenus::updateWeatheredBeaconMenu);
    }

    public static final String MSG_ID_SET_EFFECT = "weathered_beacon_set_effect";
    private static void updateWeatheredBeaconMenu(SimpleMessageEvent event) {
        JsonObject json = JsonParser.parseString(event.getContent()).getAsJsonObject();
        if(!json.has("playerId")) return;
        if(!json.has("effectId")) return;
        String playerId = json.get("playerId").getAsString();
        Player p = HBUtil.PlayerUtil.getPlayer(playerId, HBUtil.PlayerUtil.PlayerNameSpace.SERVER);
        if(p == null) return;
        if(p.containerMenu instanceof WeatheredBeaconMenu menu) {
            int effectId = json.get("effectId").getAsInt();
            Optional<MobEffect> effect = effectId == -1 ? Optional.empty() :
            Optional.of(MOB_EFFECT.byId(effectId));
            menu.updateEffects( effect, Optional.empty() );
            ((ServerPlayer) p).closeContainer();
        }
    }
}