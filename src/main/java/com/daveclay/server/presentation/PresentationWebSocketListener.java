package com.daveclay.server.presentation;

import com.daveclay.processing.gestures.GestureDataStore;
import com.daveclay.processing.gestures.RecognitionResult;
import com.daveclay.processing.kinect.api.Stage;
import com.daveclay.processing.kinect.bodylocator.BodyLocatorListener;
import org.java_websocket.WebSocket;
import processing.core.PVector;

public class PresentationWebSocketListener implements BodyLocatorListener {

    public static final String GESTURE_RECOGNIZED_TEMPLATE = "{\"type\": \"userGestureRecognized\", \"data\": { \"name\": \"%s\", \"score\": %.2f }}";
    public static final String USER_DID_MOVE_TEMPLATE = "{\"type\": \"userDidMove\", \"data\": { \"x\": %.2f, \"y\": %.2f, \"z\": %.2f }}";
    private final PresentationServer presentationServer;

    public PresentationWebSocketListener(final PresentationServer presentationServer,
                                         final GestureDataStore gestureDataStore) {
        this.presentationServer = presentationServer;

        presentationServer.setDelegate(new PresentationServer.Delegate() {
            @Override
            public void messageWasReceived(WebSocket conn, String message) {
                if (message.equals("getGestureData")) {
                }
            }

            @Override
            public void connectionWasClosed(int code, String reason) {
            }
        });
    }

    @Override
    public void gestureWasRecognized(RecognitionResult gesture) {
        presentationServer.sendToAll(String.format(GESTURE_RECOGNIZED_TEMPLATE, gesture.name, gesture.score));
    }

    @Override
    public void userDidEnteredZone(Stage.StageZone stageZone) {
        presentationServer.sendToAll("{\"type\": \"userDidEnterZone\", \"data\": { \"zone\": \"" + stageZone.getID() + "\" }}");
    }

    @Override
    public void userDidMove(PVector position) {
        presentationServer.sendToAll(String.format(USER_DID_MOVE_TEMPLATE, position.x, position.y, position.z));

    }
}
