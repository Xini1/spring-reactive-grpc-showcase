package com.github.xini1.domain;

import com.github.xini1.*;
import com.github.xini1.exception.*;
import com.github.xini1.port.*;
import com.github.xini1.usecase.*;

import java.util.*;

/**
 * @author Maxim Tereshchenko
 */
final class ItemService implements CreateItemUseCase, DeactivateItemUseCase, ActivateItemUseCase {

    private final EventStore eventStore;

    ItemService(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public UUID create(UUID userId, User user, String name) {
        if (user != User.ADMIN) {
            throw new UserIsNotAdmin();
        }
        var item = Item.create(userId, name);
        item.save(eventStore);
        return item.id();
    }

    @Override
    public void deactivate(UUID userId, User user, UUID itemId) {
        if (user != User.ADMIN) {
            throw new UserIsNotAdmin();
        }
        var item = Item.fromEvents(itemId, eventStore);
        item.deactivate(userId);
        item.save(eventStore);
    }

    @Override
    public void activate(UUID userId, User user, UUID itemId) {
        if (user != User.ADMIN) {
            throw new UserIsNotAdmin();
        }
        var item = Item.fromEvents(itemId, eventStore);
        item.activate(userId);
        item.save(eventStore);
    }
}
