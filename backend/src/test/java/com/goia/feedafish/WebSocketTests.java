package com.goia.feedafish;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;


import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class WebSocketTests {

    @Test
    @Disabled
    public void websocketTest() {
        System.out.println("AAAAAAA");
        WebSocketClient client = new StandardWebSocketClient();

        WebSocketStompClient stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSessionHandler sessionHandler = new StompSessionHandlerAdapter()
        {
            @Override
            public void afterConnected(
                    StompSession session, StompHeaders connectedHeaders) {
                System.out.println("Connected");
                session.subscribe("/topic/greetings", this);


                session.send("/app/hello", "{\"name\": \"Test\"}");
            }
            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                System.out.println("Received : " + payload);
            }


        };

        stompClient.connect("ws://127.0.0.1:8081/gs-guide-websocket", sessionHandler);

        new Scanner(System.in).nextLine(); // Don't close immediately.

    }
}

