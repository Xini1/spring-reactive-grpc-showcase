package com.github.xini1.orders.read.usecase;

import com.github.xini1.common.*;
import com.github.xini1.orders.read.view.*;

import java.util.*;

/**
 * @author Maxim Tereshchenko
 */
public interface ViewCartUseCase {

    Cart view(UUID userId, UserType userType);
}
