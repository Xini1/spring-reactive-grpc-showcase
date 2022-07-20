package com.github.xini1.users.application;

import com.github.xini1.common.event.*;
import com.github.xini1.users.*;
import com.github.xini1.users.rpc.*;
import io.grpc.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.test.context.*;
import org.testcontainers.containers.*;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.*;
import org.testcontainers.utility.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

/**
 * @author Maxim Tereshchenko
 */
@SpringBootTest(classes = Main.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
final class IntegrationTest {

    @Container
    public static final MongoDBContainer MONGO_DB = new MongoDBContainer(
            DockerImageName.parse("mongo:5.0.9")
    );

    static {
        MONGO_DB.start();
    }

    private final UserServiceGrpc.UserServiceBlockingStub stub = UserServiceGrpc.newBlockingStub(
            ManagedChannelBuilder.forAddress("localhost", 8080)
                    .usePlaintext()
                    .build()
    );
    @Autowired
    private EventRepository eventRepository;
    private String userId;
    private String jwt;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.host", MONGO_DB::getHost);
        registry.add("spring.data.mongodb.port", MONGO_DB::getFirstMappedPort);
    }

    @Test
    @Order(0)
    void userCanRegister() {
        userId = stub.register(
                        RegisterRequest.newBuilder()
                                .setUsername("username")
                                .setPassword("password")
                                .setUserType("REGULAR")
                                .build()
                )
                .getId();

        assertThat(userId).isNotNull();
        assertThat(eventRepository.findAll().collectList().block()).containsExactly(expectedEventDocument());
    }

    @Test
    @Order(1)
    void userCannotRegisterIfUsernameIsTaken() {
        var registerRequest = RegisterRequest.newBuilder()
                .setUsername("username")
                .setPassword("password")
                .setUserType("REGULAR")
                .build();

        assertThatThrownBy(() -> stub.register(registerRequest))
                .isInstanceOf(StatusRuntimeException.class)
                .extracting(Status::fromThrowable)
                .isEqualTo(Status.INVALID_ARGUMENT);
    }

    @Test
    @Order(2)
    void userCanLogin() {
        jwt = stub.login(
                        LoginRequest.newBuilder()
                                .setUsername("username")
                                .setPassword("password")
                                .build()
                )
                .getJwt();

        assertThat(jwt).isNotNull();
    }

    @Test
    @Order(3)
    void userCanBeIdentifiedByJwt() {
        assertThat(
                stub.decode(
                        DecodeJwtRequest.newBuilder()
                                .setJwt(jwt)
                                .build()
                )
        )
                .isEqualTo(
                        DecodedJwtResponse.newBuilder()
                                .setUserId(userId)
                                .setUserType("REGULAR")
                                .build()
                );
    }

    private EventDocument expectedEventDocument() {
        var eventDocument = new EventDocument();
        eventDocument.setType(EventType.USER);
        eventDocument.setAggregateId(UUID.fromString(userId));
        eventDocument.setVersion(1);
        return eventDocument;
    }
}
