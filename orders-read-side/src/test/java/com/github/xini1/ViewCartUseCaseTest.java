package com.github.xini1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.xini1.domain.Module;
import com.github.xini1.event.cart.ItemAddedToCart;
import com.github.xini1.event.cart.ItemRemovedFromCart;
import com.github.xini1.event.item.ItemCreated;
import com.github.xini1.exception.UserIsNotRegular;
import com.github.xini1.view.Cart;
import org.junit.jupiter.api.Test;

import java.util.UUID;

/**
 * @author Maxim Tereshchenko
 */
final class ViewCartUseCaseTest {

    private final Module module = new Module(new InMemoryViewStore());
    private final UUID userId = UUID.fromString("00000000-000-0000-0000-000000000001");
    private final UUID itemId = UUID.fromString("00000000-000-0000-0000-000000000002");

    @Test
    void givenUserIsNotRegular_whenViewCart_thenUserIsNotRegularThrown() {
        var useCase = module.viewCartUseCase();

        assertThatThrownBy(() -> useCase.view(userId, User.ADMIN))
                .isInstanceOf(UserIsNotRegular.class);
    }

    @Test
    void givenNoItemAddedToCartEvents_whenViewCart_thenCartIsEmpty() {
        assertThat(module.viewCartUseCase().view(userId, User.REGULAR)).isEqualTo(new Cart(userId));
    }

    @Test
    void givenItemAddedToCartEvent_whenViewCart_thenCartHasThatItem() {
        module.onItemCreatedEventUseCase().onEvent(new ItemCreated(1, userId, itemId, "item"));
        module.onItemAddedToCartEventUseCase().onEvent(new ItemAddedToCart(1, userId, itemId, 1));

        assertThat(module.viewCartUseCase().view(userId, User.REGULAR))
                .isEqualTo(
                        new Cart(
                                userId,
                                new Cart.ItemInCart(itemId, "item", 1)
                        )
                );
    }

    @Test
    void givenCartHasItem_whenViewCart_thenCartHasMoreOfThatItem() {
        module.onItemCreatedEventUseCase().onEvent(new ItemCreated(1, userId, itemId, "item"));
        var itemAddedToCart = new ItemAddedToCart(1, userId, itemId, 1);
        module.onItemAddedToCartEventUseCase().onEvent(itemAddedToCart);
        module.onItemAddedToCartEventUseCase().onEvent(itemAddedToCart);

        assertThat(module.viewCartUseCase().view(userId, User.REGULAR))
                .isEqualTo(
                        new Cart(
                                userId,
                                new Cart.ItemInCart(itemId, "item", 2)
                        )
                );
    }

    @Test
    void givenItemRemovedFromCartEvent_whenViewCart_thenCartHasNotThatItem() {
        module.onItemCreatedEventUseCase().onEvent(new ItemCreated(1, userId, itemId, "item"));
        module.onItemAddedToCartEventUseCase().onEvent(new ItemAddedToCart(1, userId, itemId, 1));
        module.onItemRemovedFromCartEventUseCase().onEvent(new ItemRemovedFromCart(2, userId, itemId, 1));

        assertThat(module.viewCartUseCase().view(userId, User.REGULAR)).isEqualTo(new Cart(userId));
    }
}