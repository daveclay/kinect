package com.daveclay.server.presentation;

import com.daveclay.processing.gestures.GestureDataStore;
import com.daveclay.processing.gestures.RecognitionResult;
import com.daveclay.processing.kinect.bodylocator.BodyLocator;
import com.daveclay.processing.kinect.api.Stage;
import org.java_websocket.WebSocket;

public class PresentationWebSocketListener implements BodyLocator.Listener {

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
        presentationServer.sendToAll("{ type: 'userGestureRecognized', data: { name: '" + gesture.name + "', score: " + gesture.score + " }}");
    }

    @Override
    public void userDidEnteredZone(Stage.StageZone stageZone) {
        presentationServer.sendToAll("{ type: 'userDidEnterZone', data: { zone: '" + stageZone.getID() + "'}}");
    }
}
