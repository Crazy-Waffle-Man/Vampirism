package de.teamlapen.vampirism.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.vampire.IDraculaPlayer;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.command.arguments.FactionArgument;
import de.teamlapen.vampirism.core.ModFactions;
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

public class FactionCommand extends BasicCommand {
    private static final SimpleCommandExceptionType LEVEL_UP_FAILED = new SimpleCommandExceptionType(Component.translatable("command.vampirism.base.lord.level_failed"));
    private static final SimpleCommandExceptionType LORD_FAILED = new SimpleCommandExceptionType(Component.translatable("command.vampirism.base.lord.failed"));
    private static final SimpleCommandExceptionType DRACULA_FAILED = new SimpleCommandExceptionType(Component.translatable("command.vampirism.base.dracula.failed"));

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext buildContext) {
        return Commands.literal("faction")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_CHEAT))
                .then(Commands.argument("faction", FactionArgument.playableFactions(buildContext))
                        .executes(context -> setLevel(context, FactionArgument.getPlayableFaction(context, "faction"), 1, Lists.newArrayList(context.getSource().getPlayerOrException())))
                        .then(Commands.literal("level")
                                .executes(context -> setLevel(context, FactionArgument.getPlayableFaction(context, "faction"), 1, Lists.newArrayList(context.getSource().getPlayerOrException())))
                                .then(Commands.argument("level", IntegerArgumentType.integer(0))
                                        .executes(context -> setLevel(context, FactionArgument.getPlayableFaction(context, "faction"), IntegerArgumentType.getInteger(context, "level"), Lists.newArrayList(context.getSource().getPlayerOrException())))
                                        .then(Commands.argument("players", EntityArgument.players())
                                                .executes(context -> setLevel(context, FactionArgument.getPlayableFaction(context, "faction"), IntegerArgumentType.getInteger(context, "level"), EntityArgument.getPlayers(context, "players"))))
                                ))
                        .then(Commands.literal("lord-level")
                                .executes(context -> setLordLevel(context, FactionArgument.getPlayableFaction(context, "faction"), 1, Lists.newArrayList(context.getSource().getPlayerOrException())))
                                .then(Commands.argument("level", IntegerArgumentType.integer(0))
                                        .executes(context -> setLordLevel(context, FactionArgument.getPlayableFaction(context, "faction"), IntegerArgumentType.getInteger(context, "level"), Lists.newArrayList(context.getSource().getPlayerOrException()))))
                                .then(Commands.argument("players", EntityArgument.players())
                                        .executes(context -> setLordLevel(context, FactionArgument.getPlayableFaction(context, "faction"), IntegerArgumentType.getInteger(context, "level"), EntityArgument.getPlayers(context, "players"))))
                        )
                        .then(Commands.literal("final-form")
                                .executes(context -> setDracula(context, Lists.newArrayList(context.getSource().getPlayerOrException())))
                                .then(Commands.argument("players", EntityArgument.players())
                                        .executes(context -> setDracula(context, EntityArgument.getPlayers(context, "players")))))
                )
                ;
    }

    public static int setLevel(CommandContext<CommandSourceStack> context, @NotNull Holder<IPlayableFaction<?>> faction, final int level, @NotNull Collection<ServerPlayer> players) {
        for (ServerPlayer player : players) {
            FactionPlayerHandler factionPlayerHandler = FactionPlayerHandler.get(player);
            if (level == 0 && !factionPlayerHandler.canLeaveFaction()) {
                context.getSource().sendFailure(Component.translatable("command.vampirism.base.level.cant_leave", players.size() > 1 ? player.getDisplayName() : "Player", factionPlayerHandler.getFaction().value().getName()));
            } else {
                int finalLevel = Math.min(level, faction.value().getHighestReachableLevel());
                if (factionPlayerHandler.setFactionAndLevel(faction, finalLevel)) {
                    context.getSource().sendSuccess(() -> Component.translatable("command.vampirism.base.level.successful", player.getName(), faction.value().getName(), finalLevel), true);
                } else {
                    context.getSource().sendFailure(players.size() > 1 ? Component.translatable("command.vampirism.failed_to_execute.players", player.getDisplayName()) : Component.translatable("command.vampirism.failed_to_execute"));
                }
            }
        }
        return 0;
    }

    private static int setLordLevel(CommandContext<CommandSourceStack> context, @NotNull Holder<IPlayableFaction<?>> faction, final int level, @NotNull Collection<ServerPlayer> players) throws CommandSyntaxException {
        for (ServerPlayer player : players) {
            FactionPlayerHandler handler = FactionPlayerHandler.get(player);
            int maxLevel = faction.value().getHighestReachableLevel();
            if (!(IFaction.is(handler.getFaction(), faction) && handler.getCurrentLevel() == maxLevel) && !handler.setFactionAndLevel(faction, maxLevel)) {
                throw LEVEL_UP_FAILED.create();
            }
            if (!handler.setLordLevel(Math.min(level, faction.value().getHighestLordLevel()))) {
                throw LORD_FAILED.create();
            }
            context.getSource().sendSuccess(() -> Component.translatable("command.vampirism.base.lord.successful", player.getName(), faction.value().getName(), handler.getLordLevel()), true);
        }
        return 0;
    }

    private static int setDracula(CommandContext<CommandSourceStack> context, @NotNull Collection<ServerPlayer> players) throws CommandSyntaxException {
        for (ServerPlayer player : players) {
            FactionPlayerHandler handler = FactionPlayerHandler.get(player);
            Holder<IPlayableFaction<IVampirePlayer>> faction = ModFactions.VAMPIRE;
            int maxLevel = faction.value().getHighestReachableLevel();

            if (!(IFaction.is(handler.getFaction(), faction) && handler.getCurrentLevel() == maxLevel) && !handler.setFactionAndLevel(faction, maxLevel)) {
                throw LEVEL_UP_FAILED.create();
            }

            if (handler.getLordLevel() < faction.value().getHighestLordLevel() && !handler.setLordLevel(Math.min(faction.value().getHighestLordLevel(), faction.value().getHighestLordLevel()))) {
                throw LORD_FAILED.create();
            }

            var dracula = handler.getCurrentFactionPlayer().filter(x -> x instanceof IDraculaPlayer).map(s -> (IDraculaPlayer) s).orElseThrow(DRACULA_FAILED::create);
            dracula.awardTitle();
            context.getSource().sendSuccess(() -> Component.translatable("command.vampirism.base.dracula.successful", player.getName(), faction.value().getName()), true);
        }
        return 0;
    }
}
