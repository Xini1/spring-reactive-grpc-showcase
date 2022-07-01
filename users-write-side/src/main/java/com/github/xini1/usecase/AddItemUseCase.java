package com.github.xini1.usecase;

import java.util.UUID;

/**
 * @author Maxim Tereshchenko
 */
public interface AddItemUseCase {

    void addItem(UUID userId, User user, String name);
}
