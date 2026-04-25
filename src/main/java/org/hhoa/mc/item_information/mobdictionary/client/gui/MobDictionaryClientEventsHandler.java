package org.hhoa.mc.item_information.mobdictionary.client.gui;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterPictureInPictureRenderersEvent;

public class MobDictionaryClientEventsHandler {
    @SubscribeEvent
    public void onRegisterPictureInPictureRenderers(RegisterPictureInPictureRenderersEvent event) {
        event.register(LockedEntityRenderState.class, LockedEntityRenderer::new);
    }
}
