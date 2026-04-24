package org.hhoa.mc.item_information.mobdictionary.attachment;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.hhoa.mc.item_information.ModInfo;
import org.hhoa.mc.item_information.mobdictionary.data.MobSavedData;

public final class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, ModInfo.ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<MobSavedData>> MOB_DATA =
            ATTACHMENTS.register(
                    "mob_data",
                    () -> AttachmentType.builder(MobSavedData::new)
                            .serialize(MobSavedData.CODEC.codec())
                            .copyOnDeath()
                            .build());

    private ModAttachments() {}

    public static void register(IEventBus modBus) {
        ATTACHMENTS.register(modBus);
    }
}
