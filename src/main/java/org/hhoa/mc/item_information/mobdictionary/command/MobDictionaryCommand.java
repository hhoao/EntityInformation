package org.hhoa.mc.item_information.mobdictionary.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
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
                                Commands.argument("init", StringArgumentType.word())
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
                                                })
                                        .suggests(
                                                (context, builder1) -> {
                                                    Suggestions init =
                                                            new Suggestions(
                                                                    StringRange.at(0),
                                                                    Collections.singletonList(
                                                                            new Suggestion(
                                                                                    StringRange.at(
                                                                                            0),
                                                                                    "init")));
                                                    return CompletableFuture.completedFuture(init);
                                                }))
                        .then(
                                Commands.argument("unlock", StringArgumentType.word())
                                        .then(
                                                Commands.argument("all", StringArgumentType.word())
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

        dispatcher.register(builder);
    }
}
