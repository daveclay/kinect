package com.daveclay.server.presentation;

import com.daveclay.processing.gestures.RecognitionResult;
import com.daveclay.processing.kinect.BodyLocator;
import com.daveclay.processing.kinect.api.Stage;

public class PresentationWebSocketListener implements BodyLocator.Listener {

    private final PresentationServer presentationServer;

    public PresentationWebSocketListener(PresentationServer presentationServer) {
        this.presentationServer = presentationServer;
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
