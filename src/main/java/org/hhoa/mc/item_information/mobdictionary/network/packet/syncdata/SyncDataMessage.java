package org.hhoa.mc.item_information.mobdictionary.network.packet.syncdata;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.network.FriendlyByteBuf;
import org.hhoa.mc.item_information.mobdictionary.network.Event;
import org.hhoa.mc.item_information.mobdictionary.network.EventType;

public abstract class SyncDataMessage implements Event {

    private final Collection<String> nameList;
    private final EventType requestType;

    public SyncDataMessage(Collection<String> names, EventType requestType) {
        this.nameList = names;
        this.requestType = requestType;
    }

    public static SyncDataMessage decode(FriendlyByteBuf buf, boolean isServer) {
        int length = buf.readInt();
        Set<String> mobNames = new HashSet<>();

        for (int i = 0; i < length; i++) {
            mobNames.add(buf.readUtf());
        }
        EventType requestType = buf.readEnum(EventType.class);

        if (isServer) {
            return new ServerSyncDataMessage(mobNames, requestType);
        } else {
            return new ClientSyncDataMessage(mobNames, requestType);
        }
    }

    public static void encode(SyncDataMessage msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.nameList.size());
        for (String name : msg.nameList) {
            buf.writeUtf(name);
        }
        buf.writeEnum(msg.requestType);
    }

    public Collection<String> getNameList() {
        return this.nameList;
    }

    @Override
    public EventType getRequestType() {
        return requestType;
    }
}
