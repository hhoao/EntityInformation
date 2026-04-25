package org.hhoa.mc.item_information.mobdictionary.network;

/**
 * MessageHandler
 *
 * @author xianxing
 * @since 2024/11/9
 */
public class EventHandlerWrapper implements EventHandler {
    private final String id;
    private final EventHandler innerEventHandler;

    public EventHandlerWrapper(String id, EventHandler innerEventHandler) {
        this.id = id;
        this.innerEventHandler = innerEventHandler;
    }

    public String getId() {
        return id;
    }

    @Override
    public void handle(Event event) {
        innerEventHandler.handle(event);
    }
}
