package org.hhoa.mc.item_information.mobdictionary.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.stream.Stream;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.Logger;
import org.hhoa.mc.item_information.mobdictionary.data.MobDatas;
import org.hhoa.mc.item_information.utils.LoggerUtils;

public class MobDictionaryCommand {
    private static final Logger LOG = LoggerUtils.getLogger(MobDictionaryCommand.class);

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> builder =
                Commands.literal("mobdictionary")
                        .then(
                                Commands.literal("init")
                                        .executes(
                                                context -> {
                                                    try {
                                                        ServerPlayer serverPlayer =
                                                                context.getSource()
                                                                        .getPlayerOrException();
                                                        MobDatas.clearMobNameOnServer(serverPlayer);
                                                        context.getSource()
                                                                .sendSuccess(
                                                                        new TextComponent(
                                                                                "Init Dictionary successful!"),
                                                                        true);
                                                    } catch (Exception e) {
                                                        LOG.error(e);
                                                    }
                                                    return Command.SINGLE_SUCCESS;
                                                }))
                        .then(
                                Commands.literal("unlock")
                                        .then(
                                                Commands.argument("all", StringArgumentType.word())
                                                        .suggests(
                                                                (context, allSuggestBuilder) ->
                                                                        SharedSuggestionProvider
                                                                                .suggest(
                                                                                        Stream.of(
                                                                                                "all"),
                                                                                        allSuggestBuilder))
                                                        .executes(
                                                                context -> {
                                                                    try {
                                                                        MobDatas
                                                                                .unlockAllMobNameOnServer(
                                                                                        context.getSource()
                                                                                                .getPlayerOrException());
                                                                        context.getSource()
                                                                                .sendSuccess(
                                                                                        new TextComponent(
                                                                                                "Unlock all successful"),
                                                                                        true);
                                                                    } catch (Exception e) {
                                                                        LOG.error(e);
                                                                    }
                                                                    return Command.SINGLE_SUCCESS;
                                                                })));
        builder.requires(
                builder.getRequirement()
                        .and(
                                (c) -> {
                                    try {

                                        return c.getPlayerOrException().hasPermissions(2);
                                    } catch (CommandSyntaxException e) {
                                        throw new RuntimeException(e);
                                    }
                                }));

        dispatcher.register(builder);
    }
}
