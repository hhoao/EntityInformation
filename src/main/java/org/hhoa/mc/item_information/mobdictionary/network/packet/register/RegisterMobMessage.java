package org.hhoa.mc.item_information.mobdictionary.network.packet.register;

import net.minecraft.network.FriendlyByteBuf;

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

    public static void encode(RegisterMobMessage message, FriendlyByteBuf buffer) {
        buffer.writeUtf(message.uuidString);
        buffer.writeUtf(message.mobName);
    }

    public static RegisterMobMessage decode(FriendlyByteBuf buffer) {
        return new RegisterMobMessage(buffer.readUtf(), buffer.readUtf());
    }
}
