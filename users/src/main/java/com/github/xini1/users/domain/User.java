package com.github.xini1.users.domain;

import com.github.xini1.common.*;
import com.github.xini1.users.exception.*;
import com.github.xini1.users.port.*;
import com.github.xini1.users.usecase.*;

import java.util.*;

/**
 * @author Maxim Tereshchenko
 */
final class User {

    private final UUID id;
    private final String name;
    private final String password;
    private final UserType userType;

    private User(UUID id, String name, String password, UserType userType) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.userType = userType;
    }

    public User(UserStore.Dto dto) {
        this(dto.getId(), dto.getUsername(), dto.getPassword(), dto.getUserType());
    }

    static User create(String name, String password, UserType userType) {
        if (name.isBlank()) {
            throw new UsernameIsEmpty();
        }
        if (password.isBlank()) {
            throw new PasswordIsEmpty();
        }
        return new User(UUID.randomUUID(), name, password, userType);
    }

    UUID id() {
        return id;
    }

    String name() {
        return name;
    }

    UserStore.Dto dto() {
        return new UserStore.Dto(id, name, password, userType);
    }

    String jwt(TokenProvider tokenProvider) {
        return tokenProvider.sign(id);
    }

    DecodeJwtUseCase.Response info() {
        return new DecodeJwtUseCase.Response(id, userType);
    }
}
