package org.hhoa.mc.item_information.mobdictionary;

import com.mojang.brigadier.CommandDispatcher;
import java.io.IOException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.hhoa.mc.item_information.mobdictionary.command.MobDictionaryCommand;
import org.hhoa.mc.item_information.mobdictionary.data.MobDatas;
import org.hhoa.mc.item_information.utils.PlayerUtils;

public class MobDictionaryForgeEventsHandler {
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        MobDatas.loadMobDataOnServer(serverPlayer);

        var mobSavedData = MobDatas.getMobSavedData(serverPlayer);
        if (!mobSavedData.hasLoggedIn()) {
            ItemStack welcomeItem = new ItemStack(MobDictionary.mobDictionary.get(), 1);
            PlayerUtils.addItemToPlayer(welcomeItem, player);
            mobSavedData.setHasLoggedIn(true);
        }
    }

    @SubscribeEvent
    public void onServerStart(ServerStartingEvent event) {
        MinecraftServer server = event.getServer();
        CommandDispatcher<CommandSourceStack> dispatcher = server.getCommands().getDispatcher();
        MobDictionaryCommand.register(dispatcher);
        MobDictionary.getEntityManager().loadAllMob(server);
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer
                && event.getTarget() instanceof LivingEntity livingEntity) {
            unlockMobData(serverPlayer, livingEntity);
        }
    }

    @SubscribeEvent
    public void onPlayerAttack(AttackEntityEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            unlockMobData(serverPlayer, event.getTarget());
        }
    }

    private static void unlockMobData(ServerPlayer player, Entity entity) {
        EntityType<?> type = entity.getType();
        String descriptionId = type.getDescriptionId();
        EntityManager entityManager = MobDictionary.getEntityManager();
        if (!entityManager.containsName(descriptionId)) {
            return;
        }

        try {
            MobDatas.saveMobNameOnServer(player, descriptionId);
        } catch (IOException e) {
            throw new RuntimeException("Failed to persist unlocked mob data", e);
        }
    }
}
