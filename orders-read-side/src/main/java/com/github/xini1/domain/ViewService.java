package com.github.xini1.domain;

import com.github.xini1.User;
import com.github.xini1.exception.UserIsNotRegular;
import com.github.xini1.port.ViewStore;
import com.github.xini1.usecase.ViewCartUseCase;
import com.github.xini1.view.Cart;

import java.util.UUID;

/**
 * @author Maxim Tereshchenko
 */
final class ViewService implements ViewCartUseCase {

    private final ViewStore viewStore;

    ViewService(ViewStore viewStore) {
        this.viewStore = viewStore;
    }

    @Override
    public Cart view(UUID userId, User user) {
        if (user != User.REGULAR) {
            throw new UserIsNotRegular();
        }
        return viewStore.findCart(userId);
    }
}