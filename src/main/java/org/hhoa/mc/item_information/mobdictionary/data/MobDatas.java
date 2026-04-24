package org.hhoa.mc.item_information.mobdictionary.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import org.hhoa.mc.item_information.mobdictionary.MobDictionary;
import org.hhoa.mc.item_information.mobdictionary.attachment.ModAttachments;
import org.hhoa.mc.item_information.mobdictionary.network.EventType;

public final class MobDatas {
    private static final Set<String> CLIENT_MOB_NAMES = new LinkedHashSet<>();

    private MobDatas() {}

    public static boolean containsMobNameOnClient(String entityName) {
        return CLIENT_MOB_NAMES.contains(entityName);
    }

    public static boolean containsMobNameOnServer(String name, ServerPlayer player) {
        return getMobSavedData(player).containsMobName(name);
    }

    public static int getRegisteredMobCountOnClient() {
        return CLIENT_MOB_NAMES.size();
    }

    public static Set<String> getUnLockMobNamesOnClient() {
        return Set.copyOf(CLIENT_MOB_NAMES);
    }

    public static Set<String> getUnlockedMobNamesOnServer(ServerPlayer player) {
        return Set.copyOf(getMobSavedData(player).getMobNameSet());
    }

    public static MobSavedData getMobSavedData(ServerPlayer player) {
        return player.getData(ModAttachments.MOB_DATA);
    }

    public static void loadMobDataOnServer(ServerPlayer player) {
        sendSyncDataMessageOnServer(player, getMobSavedData(player).getMobNameSet(), EventType.REGISTER);
    }

    public static void saveMobNameOnServer(ServerPlayer player, String mobName) throws IOException {
        MobSavedData mobSavedData = getMobSavedData(player);
        if (mobSavedData.containsMobName(mobName)) {
            return;
        }

        mobSavedData.addMobName(mobName);
        sendSyncDataMessageOnServer(player, Set.of(mobName), EventType.PUT);
    }

    public static void saveMobNamesOnServer(ServerPlayer player, Collection<String> mobNames) {
        getMobSavedData(player).addMobNames(mobNames);
        sendSyncDataMessageOnServer(player, mobNames, EventType.PUT);
    }

    public static void removeMobNameOnServer(ServerPlayer player, String mobName) {
        getMobSavedData(player).removeMobName(mobName);
        sendSyncDataMessageOnServer(player, Set.of(mobName), EventType.DELETE);
    }

    public static void removeMobNameOnServer(ServerPlayer player, Collection<String> mobNames) {
        getMobSavedData(player).removeMobNames(mobNames);
        sendSyncDataMessageOnServer(player, mobNames, EventType.DELETE);
    }

    public static void clearMobNameOnServer(ServerPlayer player) {
        var mobNames = getMobSavedData(player).clearMobNames();
        sendSyncDataMessageOnServer(player, mobNames, EventType.DELETE);
    }

    public static void unlockAllMobNameOnServer(ServerPlayer player) {
        var entityNames =
                MobDictionary.getEntityManager().getEntityTypes().stream()
                        .map(EntityType::getDescriptionId)
                        .toList();
        saveMobNamesOnServer(player, entityNames);
    }

    public static void replaceClientMobNames(Collection<String> mobNames) {
        CLIENT_MOB_NAMES.clear();
        CLIENT_MOB_NAMES.addAll(mobNames);
    }

    public static ArrayList<String> removeMobNamesOnClient(Collection<String> mobNames) {
        ArrayList<String> successMobNames = new ArrayList<>();
        for (String mobName : mobNames) {
            if (CLIENT_MOB_NAMES.remove(mobName)) {
                successMobNames.add(mobName);
            }
        }
        return successMobNames;
    }

    public static ArrayList<String> addMobNamesOnClient(Collection<String> mobNames) {
        ArrayList<String> addedMobNames = new ArrayList<>();
        for (String mobName : mobNames) {
            if (CLIENT_MOB_NAMES.add(mobName)) {
                addedMobNames.add(mobName);
            }
        }
        return addedMobNames;
    }

    public static void sendSyncDataOnClient(Collection<String> names, EventType requestType) {
        // Task 5 ports the packet layer to payloads.
    }

    public static void sendSyncDataMessageOnServer(
            ServerPlayer serverPlayer, Collection<String> names, EventType requestType) {
        // Task 5 ports the packet layer to payloads.
    }
}
