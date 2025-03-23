package de.teamlapen.vampirism.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.command.arguments.FactionArgument;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class LevelCommand extends BasicCommand {


    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext buildContext) {

        return Commands.literal("level")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_CHEAT))
                .then(Commands.argument("faction", FactionArgument.playableFactions(buildContext))
                        .executes(context -> FactionCommand.setLevel(context, FactionArgument.getPlayableFaction(context, "faction"), 1, Lists.newArrayList(context.getSource().getPlayerOrException())))
                        .then(Commands.argument("level", IntegerArgumentType.integer(0))
                                .executes(context -> FactionCommand.setLevel(context, FactionArgument.getPlayableFaction(context, "faction"), IntegerArgumentType.getInteger(context, "level"), Lists.newArrayList(context.getSource().getPlayerOrException())))
                                .then(Commands.argument("player", EntityArgument.entities())
                                        .executes(context -> FactionCommand.setLevel(context, FactionArgument.getPlayableFaction(context, "faction"), IntegerArgumentType.getInteger(context, "level"), EntityArgument.getPlayers(context, "player"))))));
    }

}
