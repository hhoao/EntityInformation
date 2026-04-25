package org.hhoa.mc.item_information.mobdictionary.network.packet.syncdata;

import java.util.Collection;
import org.hhoa.mc.item_information.mobdictionary.network.EventType;

public class ClientSyncDataMessage extends SyncDataMessage {
    public ClientSyncDataMessage(Collection<String> names, EventType requestType) {
        super(names, requestType);
    }
}
