package org.hhoa.mc.item_information.mobdictionary.network.packet.syncdata;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.network.PacketBuffer;
import org.hhoa.mc.item_information.mobdictionary.network.Event;
import org.hhoa.mc.item_information.mobdictionary.network.EventType;

public abstract class SyncDataMessage implements Event {

    private final Collection<String> nameList;
    private final EventType requestType;

    public SyncDataMessage(Collection<String> names, EventType requestType) {
        this.nameList = names;
        this.requestType = requestType;
    }

    public static SyncDataMessage decode(PacketBuffer buf, boolean isServer) {
        int length = buf.readInt();
        Set<String> mobNames = new HashSet<>();

        for (int i = 0; i < length; i++) {
            mobNames.add(buf.readString());
        }
        EventType requestType = buf.readEnumValue(EventType.class);

        if (isServer) {
            return new ServerSyncDataMessage(mobNames, requestType);
        } else {
            return new ClientSyncDataMessage(mobNames, requestType);
        }
    }

    public static void encode(SyncDataMessage msg, PacketBuffer buf) {
        buf.writeInt(msg.nameList.size());
        for (String name : msg.nameList) {
            buf.writeString(name);
        }
        buf.writeEnumValue(msg.requestType);
    }

    public Collection<String> getNameList() {
        return this.nameList;
    }

    @Override
    public EventType getRequestType() {
        return requestType;
    }
}
