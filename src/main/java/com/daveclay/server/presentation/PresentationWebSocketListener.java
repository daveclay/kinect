package com.daveclay.server.presentation;

import com.daveclay.processing.gestures.GestureDataStore;
import com.daveclay.processing.gestures.RecognitionResult;
import com.daveclay.processing.kinect.api.User;
import com.daveclay.processing.kinect.api.stage.Stage;
import com.daveclay.processing.kinect.api.stage.StagePosition;
import com.daveclay.processing.kinect.bodylocator.BodyLocatorListener;
import org.java_websocket.WebSocket;

public class PresentationWebSocketListener implements BodyLocatorListener {

    public static final String GESTURE_RECOGNIZED_TEMPLATE = "{\"type\": \"userGestureRecognized\", \"data\": { \"user\": \"%s\", \"name\": \"%s\", \"score\": %.2f }}";
    public static final String USER_DID_MOVE_TEMPLATE = "{\"type\": \"userDidMove\", \"data\": { \"user\": \"%s\", \"fromLeft\": %.2f, \"fromBottom\": %.2f, \"fromFront\": %.2f }}";
    public static final String USER_DID_ENTER_ZONE_TEMPLATE = "{\"type\": \"userDidEnterZone\", \"data\": { \"user\": \"%s\", \"zone\": \"%s\" }}";

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
    public void gestureWasRecognized(User user, RecognitionResult gesture) {
        presentationServer.sendToAll(String.format(GESTURE_RECOGNIZED_TEMPLATE,
                user.getID(),
                gesture.name,
                gesture.score));
    }

    @Override
    public void userDidEnteredZone(User user, Stage.StageZone stageZone) {
        presentationServer.sendToAll(String.format(USER_DID_ENTER_ZONE_TEMPLATE,
                user.getID(),
                stageZone.getID()));
    }

    @Override
    public void userDidMove(User user, StagePosition position) {
        presentationServer.sendToAll(String.format(USER_DID_MOVE_TEMPLATE,
                user.getID(),
                position.getFromLeftPercent(),
                position.getFromBottomPercent(),
                position.getFromFrontPercent()));

    }
}
