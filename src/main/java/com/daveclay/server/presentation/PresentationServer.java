package com.daveclay.server.presentation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.daveclay.processing.gestures.GestureDataStore;
import com.daveclay.processing.kinect.bodylocator.BodyLocator;
import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class PresentationServer extends WebSocketServer {

    public static void register(GestureDataStore gestureDataStore, BodyLocator bodyLocator) {
        try {
            PresentationServer presentationServer = new PresentationServer(12345);
            presentationServer.start();
            PresentationWebSocketListener listener = new PresentationWebSocketListener(
                    presentationServer,
                    gestureDataStore);

            bodyLocator.setListener(listener);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }

    public static interface Delegate {
        public void messageWasReceived(WebSocket conn, String message);
        void connectionWasClosed(int code, String reason);
    }

    private Delegate delegate;

    public PresentationServer(int port) throws UnknownHostException {
        super( new InetSocketAddress( port ) );
    }

    public PresentationServer(InetSocketAddress address) {
        super(address);
    }

    public void setDelegate(Delegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public void onOpen( WebSocket conn, ClientHandshake handshake ) {
        this.sendToAll("{ \"connected\": \"" + handshake.getResourceDescriptor() + "\"}");
        System.out.println( conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!" );
        Executors.newSingleThreadScheduledExecutor().schedule(ping, 5, TimeUnit.SECONDS);
    }

    @Override
    public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
        this.delegate.connectionWasClosed(code, reason);
    }

    @Override
    public void onMessage( WebSocket conn, String message ) {
        this.delegate.messageWasReceived(conn, message);
        System.out.println( conn + ": " + message );
    }

    @Override
    public void onError( WebSocket conn, Exception ex ) {
        ex.printStackTrace();
        if( conn != null ) {
            // some errors like port binding failed may not be assignable to a specific websocket
        }
    }

    private Runnable ping = new Runnable() {
        @Override
        public void run() {
            sendToAll("{ ping: true }");
        }
    };

    /**
     * Sends <var>text</var> to all currently connected WebSocket clients.
     *
     * @param text
     *            The String to send across the network.
     * @throws InterruptedException
     *             When socket related I/O errors occur.
     */
    public void sendToAll( String text ) {
        Collection<WebSocket> con = connections();
        synchronized ( con ) {
            for( WebSocket c : con ) {
                c.send( text );
            }
        }
    }

    public static void main( String[] args ) throws InterruptedException , IOException {
        WebSocketImpl.DEBUG = true;
        int port = 8887; // 843 flash policy port
        try {
            port = Integer.parseInt( args[ 0 ] );
        } catch ( Exception ex ) {
        }
        PresentationServer server = new PresentationServer(port);
        server.start();
        System.out.println("ChatServer started on port: " + server.getPort());

        BufferedReader sysin = new BufferedReader( new InputStreamReader( System.in ) );
        while ( true ) {
            String in = sysin.readLine();
            server.sendToAll(in);
            if( in.equals( "exit" ) ) {
                server.stop();
                break;
            } else if( in.equals( "restart" ) ) {
                server.stop();
                server.start();
                break;
            }
        }
    }
}

