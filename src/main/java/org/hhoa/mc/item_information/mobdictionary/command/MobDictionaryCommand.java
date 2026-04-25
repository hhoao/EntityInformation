package org.hhoa.mc.item_information.mobdictionary.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.stream.Stream;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import org.apache.logging.log4j.Logger;
import org.hhoa.mc.item_information.mobdictionary.data.MobDatas;
import org.hhoa.mc.item_information.utils.LoggerUtils;

public class MobDictionaryCommand {
    private static final Logger LOG = LoggerUtils.getLogger(MobDictionaryCommand.class);

    public static void register(CommandDispatcher<CommandSource> dispatcher) {

        LiteralArgumentBuilder<CommandSource> builder =
                Commands.literal("mobdictionary")
                        .then(
                                Commands.literal("init")
                                        .executes(
                                                context -> {
                                                    try {
                                                        ServerPlayerEntity serverPlayer =
                                                                context.getSource().asPlayer();
                                                        MobDatas.clearMobNameOnServer(serverPlayer);
                                                        context.getSource()
                                                                .sendFeedback(
                                                                        new StringTextComponent(
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
                                                                        ISuggestionProvider.suggest(
                                                                                Stream.of("all"),
                                                                                allSuggestBuilder))
                                                        .executes(
                                                                context -> {
                                                                    try {
                                                                        MobDatas
                                                                                .unlockAllMobNameOnServer(
                                                                                        context.getSource()
                                                                                                .asPlayer());
                                                                        context.getSource()
                                                                                .sendFeedback(
                                                                                        new StringTextComponent(
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
                                        return c.asPlayer().hasPermissionLevel(2);
                                    } catch (CommandSyntaxException e) {
                                        throw new RuntimeException(e);
                                    }
                                }));

        dispatcher.register(builder);
    }
}
