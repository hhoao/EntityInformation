package org.hhoa.mc.item_information.mobdictionary;

import com.mojang.brigadier.CommandDispatcher;
import java.util.Collections;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.hhoa.mc.item_information.EntityInformation;
import org.hhoa.mc.item_information.mobdictionary.capabilities.FirstLoginCapabilityProvider;
import org.hhoa.mc.item_information.mobdictionary.capabilities.IFirstLoginCapability;
import org.hhoa.mc.item_information.mobdictionary.capabilities.MobDataCapability;
import org.hhoa.mc.item_information.mobdictionary.capabilities.MobDataCapabilityProvider;
import org.hhoa.mc.item_information.mobdictionary.command.MobDictionaryCommand;
import org.hhoa.mc.item_information.mobdictionary.data.MobDatas;
import org.hhoa.mc.item_information.mobdictionary.network.EventType;
import org.hhoa.mc.item_information.utils.PlayerUtils;

/**
 * MobDictionaryForgeEventsHandler
 *
 * @author xianxing
 * @since 2024/10/28
 */
public class MobDictionaryForgeEventsHandler {
    public static final Capability<IFirstLoginCapability> FIRST_LOGIN_CAPABILITY =
            CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<MobDataCapability> MOB_DATA_CAPABILITY =
            CapabilityManager.get(new CapabilityToken<>() {});
    public static final ResourceLocation FIRST_LOGIN_CAP =
            EntityInformation.location("first_login_capability");
    public static final ResourceLocation MOB_DATA_CAP =
            EntityInformation.location("mob_data_capability");

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getPlayer();
        if (player instanceof ServerPlayer serverPlayer) {
            MobDatas.loadMobDataOnServer(serverPlayer);
            LazyOptional<IFirstLoginCapability> capability =
                    player.getCapability(MobDictionaryForgeEventsHandler.FIRST_LOGIN_CAPABILITY);

            capability.ifPresent(
                    cap -> {
                        if (!cap.hasLoggedIn()) {
                            ItemStack welcomeItem = new ItemStack(MobDictionary.mobDictionary, 1);
                            PlayerUtils.addItemToPlayer(welcomeItem, player);

                            cap.setHasLoggedIn(true);
                        }
                    });
        }
    }

    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof ServerPlayer) {
            event.addCapability(FIRST_LOGIN_CAP, new FirstLoginCapabilityProvider());
            event.addCapability(MOB_DATA_CAP, new MobDataCapabilityProvider());
        }
    }

    @SubscribeEvent
    public void onServerStart(ServerStartingEvent event) {
        MinecraftServer server = event.getServer();
        CommandDispatcher<CommandSourceStack> dispatcher = server.getCommands().getDispatcher();
        MobDictionaryCommand.register(dispatcher);
        MobDictionary.getEntityManager().loadAllMob(event.getServer());
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        LivingEntity entityLiving = event.getEntityLiving();
        unLockMobData(entityLiving);
    }

    @SubscribeEvent
    public void onPlayerAttack(AttackEntityEvent event) {
        Entity target = event.getTarget();
        unLockMobData(target);
    }

    private static void unLockMobData(Entity entityLiving) {
        if (entityLiving != null) {
            EntityType<?> type = entityLiving.getType();
            String descriptionId = type.getDescriptionId();
            EntityManager entityManager = MobDictionary.getEntityManager();
            if (entityManager.containsName(descriptionId)) {
                MobDatas.sendSyncDataOnClient(Collections.singleton(descriptionId), EventType.PUT);
            }
        }
    }
}
