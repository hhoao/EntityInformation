package org.hhoa.mc.item_information.mobdictionary.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.NetworkDirection;
import org.hhoa.mc.item_information.mobdictionary.MobDictionary;
import org.hhoa.mc.item_information.mobdictionary.MobDictionaryForgeEventsHandler;
import org.hhoa.mc.item_information.mobdictionary.capabilities.MobDataCapability;
import org.hhoa.mc.item_information.mobdictionary.capabilities.MobDataCapabilityImpl;
import org.hhoa.mc.item_information.mobdictionary.network.EventType;
import org.hhoa.mc.item_information.mobdictionary.network.PacketHandler;
import org.hhoa.mc.item_information.mobdictionary.network.packet.syncdata.ClientSyncDataMessage;
import org.hhoa.mc.item_information.mobdictionary.network.packet.syncdata.ServerSyncDataMessage;
import org.hhoa.mc.item_information.mobdictionary.network.packet.syncdata.SyncDataMessage;

public final class MobDatas {
    private static final Set<String> clientMobNameSet = new HashSet<>();
    private static final Map<UUID, MobSavedData> serverMobNameListMap = new HashMap<>();

    public static boolean containsMobNameOnClient(String entityName) {
        return clientMobNameSet.contains(entityName);
    }

    public static boolean containsMobNameOnServer(String name, ServerPlayer player) {
        MobSavedData mobSavedData = getMobSavedData(player);
        return mobSavedData.containsMobName(name);
    }

    public static int getRegisteredMobCountOnClient() {
        return clientMobNameSet.size();
    }

    public static Set<String> getUnLockMobNamesOnClient() {
        return clientMobNameSet;
    }

    private static String getMobSavedDataName(UUID playerUUID) {
        return "mob_data" + playerUUID;
    }

    public static void loadMobDataOnServer(ServerPlayer player) {
        MobSavedData mobSavedData = getMobSavedData(player);
        Set<String> mobNameList = mobSavedData.getMobNameSet();

        sendSyncDataMessageOnServer(player, mobNameList, EventType.REGISTER);
    }

    public static void saveMobNameOnServer(ServerPlayer player, String mobName) throws IOException {
        MobSavedData mobSavedData = getMobSavedData(player);
        mobSavedData.addMobName(mobName);

        sendSyncDataMessageOnServer(player, Collections.singleton(mobName), EventType.PUT);
    }

    public static void saveMobNamesOnServer(ServerPlayer sender, Collection<String> mobNames) {
        MobSavedData mobSavedData = getMobSavedData(sender);
        mobSavedData.addMobNames(mobNames);

        sendSyncDataMessageOnServer(sender, mobNames, EventType.PUT);
    }

    public static void removeMobNameOnServer(ServerPlayer player, String mobName) {
        MobSavedData mobSavedData = getMobSavedData(player);
        mobSavedData.removeMobName(mobName);

        sendSyncDataMessageOnServer(player, Collections.singleton(mobName), EventType.DELETE);
    }

    public static void removeMobNameOnServer(ServerPlayer player, Collection<String> mobNames) {
        MobSavedData mobSavedData = getMobSavedData(player);
        mobSavedData.removeMobNames(mobNames);

        sendSyncDataMessageOnServer(player, mobNames, EventType.DELETE);
    }

    private static MobSavedData getMobSavedData(ServerPlayer serverPlayer) {
        UUID playerUuid = serverPlayer.getUUID();

        MobSavedData mobSavedData;
        if (serverMobNameListMap.containsKey(playerUuid)) {
            mobSavedData = serverMobNameListMap.get(playerUuid);
        } else {
            LazyOptional<MobDataCapability> capability =
                    serverPlayer.getCapability(MobDictionaryForgeEventsHandler.MOB_DATA_CAPABILITY);
            MobDataCapability mobDataCapability = capability.orElseGet(MobDataCapabilityImpl::new);
            mobSavedData = mobDataCapability.getMobSavedData();
            serverMobNameListMap.put(playerUuid, mobSavedData);
        }
        return mobSavedData;
    }

    public static void clearMobNameOnServer(ServerPlayer serverPlayer) {
        MobSavedData mobSavedData = getMobSavedData(serverPlayer);
        HashSet<String> mobNames = mobSavedData.clearMobNames();
        sendSyncDataMessageOnServer(serverPlayer, mobNames, EventType.DELETE);
    }

    public static void unlockAllMobNameOnServer(ServerPlayer serverPlayer) {
        List<String> list =
                MobDictionary.getEntityManager().getEntityTypes().stream()
                        .map(EntityType::getDescriptionId)
                        .toList();
        saveMobNamesOnServer(serverPlayer, list);
    }

    public static void sendSyncDataOnClient(Collection<String> names, EventType requestType) {
        SyncDataMessage syncDataMessage = new ClientSyncDataMessage(names, requestType);
        PacketHandler.CHANNEL.sendToServer(syncDataMessage);
    }

    public static void sendSyncDataMessageOnServer(
            ServerPlayer serverPlayer, Collection<String> names, EventType requestType) {
        SyncDataMessage syncDataMessage = new ServerSyncDataMessage(names, requestType);
        PacketHandler.CHANNEL.sendTo(
                syncDataMessage,
                serverPlayer.connection.connection,
                NetworkDirection.PLAY_TO_CLIENT);
    }

    public static ArrayList<String> removeMobNamesOnClient(Collection<String> names) {
        ArrayList<String> successMobNames = new ArrayList<>();
        for (String name : names) {
            if (clientMobNameSet.remove(name)) {
                successMobNames.add(name);
            }
        }
        return successMobNames;
    }

    public static ArrayList<String> addMobNamesOnClient(Collection<String> names) {
        ArrayList<String> addMobNames = new ArrayList<>();
        for (String name : names) {
            if (clientMobNameSet.add(name)) {
                addMobNames.add(name);
            }
        }
        return addMobNames;
    }
}
