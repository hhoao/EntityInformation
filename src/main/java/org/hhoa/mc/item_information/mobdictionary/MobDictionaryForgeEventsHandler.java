package org.hhoa.mc.item_information.mobdictionary;

import com.mojang.brigadier.CommandDispatcher;
import java.util.Collections;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import org.hhoa.mc.item_information.EntityInformation;
import org.hhoa.mc.item_information.mobdictionary.capabilities.FirstLoginCapabilityProvider;
import org.hhoa.mc.item_information.mobdictionary.capabilities.IFirstLoginCapability;
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
    public static final ResourceLocation FIRST_LOGIN_CAP =
            EntityInformation.location("first_login");
    public static final ResourceLocation MOB_DATA = EntityInformation.location("mob_data");

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();
        if (player instanceof ServerPlayerEntity) {
            MobDatas.loadMobDataOnServer((ServerPlayerEntity) player);
            LazyOptional<IFirstLoginCapability> capability =
                    player.getCapability(MobDictionary.firstLoginCapability);

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
        if (event.getObject() instanceof PlayerEntity) {
            event.addCapability(FIRST_LOGIN_CAP, new FirstLoginCapabilityProvider());
            event.addCapability(MOB_DATA, new MobDataCapabilityProvider());
        }
    }

    @SubscribeEvent
    public void onServerStart(FMLServerStartingEvent event) {
        MinecraftServer server = event.getServer();
        CommandDispatcher<CommandSource> dispatcher = server.getCommandManager().getDispatcher();
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
            String descriptionId = type.getTranslationKey();
            EntityManager entityManager = MobDictionary.getEntityManager();
            if (entityManager.containsName(descriptionId)) {
                MobDatas.sendSyncDataOnClient(Collections.singleton(descriptionId), EventType.PUT);
            }
        }
    }
}
