package org.hhoa.mc.item_information.mobdictionary.network.packet.register;

import net.minecraft.network.PacketBuffer;

public class RegisterMobMessage {

    private final String uuidString;
    private final String mobName;

    public RegisterMobMessage(String stringUUID, String mobName) {
        this.uuidString = stringUUID;
        this.mobName = mobName;
    }

    public String getUUIDString() {
        return this.uuidString;
    }

    public String getMobName() {
        return this.mobName;
    }

    public static void encode(RegisterMobMessage message, PacketBuffer buffer) {
        buffer.writeString(message.uuidString);
        buffer.writeString(message.mobName);
    }

    public static RegisterMobMessage decode(PacketBuffer buffer) {
        return new RegisterMobMessage(buffer.readString(), buffer.readString());
    }
}
