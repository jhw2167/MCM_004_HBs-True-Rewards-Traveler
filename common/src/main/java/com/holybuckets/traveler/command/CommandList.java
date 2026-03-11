package com.holybuckets.traveler.command;

//Project imports

import com.holybuckets.foundation.HBUtil;
import com.holybuckets.foundation.event.CommandRegistry;
import com.holybuckets.traveler.LoggerProject;
import com.holybuckets.traveler.core.ManagedTraveler;
import com.holybuckets.traveler.core.ManagedTravelerApi;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class CommandList {

    public static final String CLASS_ID = "033";
    private static final String PREFIX = "hbTraveler";

    public static void register() {
        //CommandRegistry.register(LocateClusters::noArgs);
        //CommandRegistry.register(LocateClusters::limitCount);
        //CommandRegistry.register(LocateClusters::limitCountSpecifyBlockType);

        CommandRegistry.register(ClearSoulboundSlots::register);
    }

    //1. Locate Clusters
    private static class LocateClusters
    {
        // Register the base command with no arguments
        private static LiteralArgumentBuilder<CommandSourceStack> noArgs() {
            return Commands.literal(PREFIX)
                .then(Commands.literal("locateClusters")
                    .executes(context -> execute(context.getSource(), -1, null)) // Default case (no args)
                );

        }

        // Register command with count argument
        private static LiteralArgumentBuilder<CommandSourceStack> limitCount() {
            return Commands.literal(PREFIX)
                .then(Commands.literal("locateClusters")
                    .then(Commands.argument("count", IntegerArgumentType.integer(1))
                        .executes(context -> {
                            int count = IntegerArgumentType.getInteger(context, "count");
                            return execute(context.getSource(), count, null);
                        })
                    )
            );
        }

        // Register command with both count and blockType OR just blockType
        private static LiteralArgumentBuilder<CommandSourceStack> limitCountSpecifyBlockType() {
            return Commands.literal(PREFIX)
                .then(Commands.literal("locateClusters")
                    .then(Commands.argument("count", IntegerArgumentType.integer(1))
                        .then(Commands.argument("blockType", StringArgumentType.string())
                            .executes(context -> {
                                int count = IntegerArgumentType.getInteger(context, "count");
                                String blockType = StringArgumentType.getString(context, "blockType");
                                return execute(context.getSource(), count, blockType);
                            })
                        )
                    )
                    .then(Commands.argument("blockType", StringArgumentType.string())
                        .executes(context -> {
                            String blockType = StringArgumentType.getString(context, "blockType");
                            return execute(context.getSource(), -1, blockType);
                        })
                    )
            );
        }


        private static int execute(CommandSourceStack source, int count, String blockType)
        {

            LoggerProject.logDebug("010001", "Locate Clusters Command");
            return 0;
        }


    }
    //END COMMAND

    //2. Clear Soulbound Slots
    private static class ClearSoulboundSlots
    {
        private static LiteralArgumentBuilder<CommandSourceStack> register() {
            return Commands.literal(PREFIX)
                .then(Commands.literal("clearSoulboundSlots")
                    .executes(context -> execute(context.getSource()))
                );
        }

        private static int execute(CommandSourceStack source)
        {
            if(source.getEntity() instanceof ServerPlayer player) {
                ManagedTravelerApi.clearSoulboundSlots(player);
                return 1;
            } else {
                LoggerProject.logError("033002", "clearSoulboundSlots command should only be executed by a player.");
                return 0;
            }
        }
    }

}
//END CLASS COMMANDLIST
