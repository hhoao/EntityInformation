package org.hhoa.mc.item_information.mobdictionary.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Dispatcher
 *
 * @author xianxing
 * @since 2024/11/9
 */
public class Dispatcher {
    private final Map<EventType, List<EventHandlerWrapper>> requestHandlerMap = new HashMap<>();

    public String registerQuestHandler(List<EventType> requestTypes, EventHandler requestHandler) {
        EventHandlerWrapper eventHandlerWrapper =
                new EventHandlerWrapper(UUID.randomUUID().toString(), requestHandler);
        for (EventType requestType : requestTypes) {
            List<EventHandlerWrapper> requestHandlerList = getRequestHandlerList(requestType);
            requestHandlerList.add(eventHandlerWrapper);
        }
        return eventHandlerWrapper.getId();
    }

    public void removeEventHandler(String id) {
        Collection<List<EventHandlerWrapper>> values = requestHandlerMap.values();
        for (List<EventHandlerWrapper> value : values) {
            value.removeIf(eventHandlerWrapper -> eventHandlerWrapper.getId().equals(id));
        }
    }

    public void process(Event request) {
        EventType requestType = request.getRequestType();
        if (requestType == null) {
            throw new RuntimeException("Request must not be null");
        }
        List<EventHandlerWrapper> requestHandlers = getRequestHandlerList(requestType);
        for (EventHandler requestHandler : requestHandlers) {
            if (requestHandler != null) {
                requestHandler.handle(request);
            }
        }
    }

    public List<EventHandlerWrapper> getRequestHandlerList(EventType requestType) {
        return requestHandlerMap.computeIfAbsent(requestType, (rt) -> new ArrayList<>());
    }
}
