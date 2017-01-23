package com.jingtum.start;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by IntelliJ IDEA.
 *
 * @author heipacker
 * @date 17-1-17 下午8:57.
 */
public class WebsocketTest extends WebSocketClient {

    public WebsocketTest(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public WebsocketTest(URI serverURI) {
        super(serverURI);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("new connection opened");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("closed with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onMessage(String message) {
        System.out.println("received message: " + message);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("an error occurred:" + ex);
    }

    public static void main(String[] args) throws URISyntaxException {
        WebSocketClient client = new WebsocketTest(new URI("ws://localhost:8887"), new Draft_10());
        client.connect();
    }
}
