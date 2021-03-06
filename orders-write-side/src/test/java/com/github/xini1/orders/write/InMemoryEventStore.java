package com.github.xini1.orders.write;

import com.github.xini1.common.event.*;
import com.github.xini1.common.event.cart.*;
import com.github.xini1.common.event.item.*;
import com.github.xini1.orders.write.port.*;

import java.util.*;
import java.util.stream.*;

/**
 * @author Maxim Tereshchenko
 */
final class InMemoryEventStore implements EventStore {

    private final List<Event> events = new ArrayList<>();

    @Override
    public void publish(Event event) {
        events.add(event);
    }

    @Override
    public List<ItemEvent> itemEvents(UUID itemId) {
        return find(EventType.itemEvents(), itemId, ItemEvent.class);
    }

    @Override
    public List<CartEvent> cartEvents(UUID userId) {
        return find(EventType.cartEvents(), userId, CartEvent.class);
    }

    List<Event> events() {
        return events;
    }

    private <T extends Event> List<T> find(Collection<EventType> eventTypes, UUID aggregateId, Class<T> resultType) {
        return events.stream()
                .filter(event -> eventTypes.contains(event.type()))
                .filter(event -> event.aggregateId().equals(aggregateId))
                .sorted(Comparator.comparingLong(Event::version))
                .map(resultType::cast)
                .collect(Collectors.toList());
    }
}
