package com.github.xini1.usecase;

import java.util.Objects;
import java.util.UUID;

/**
 * @author Maxim Tereshchenko
 */
public final class ItemAddedToCart implements CartEvent {

    private final UUID userId;
    private final UUID itemId;

    public ItemAddedToCart(UUID userId, UUID itemId) {
        this.userId = userId;
        this.itemId = itemId;
    }

    @Override
    public UUID userId() {
        return userId;
    }

    public UUID itemId() {
        return itemId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, itemId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        var that = (ItemAddedToCart) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(itemId, that.itemId);
    }

    @Override
    public String toString() {
        return "ItemAddedToCart{" +
                "userId=" + userId +
                ", itemId=" + itemId +
                '}';
    }
}