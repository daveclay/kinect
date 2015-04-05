package com.daveclay.server.presentation;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collection;

public class ExampleWebSockerServer extends WebSocketServer {

    public ExampleWebSockerServer(int port) throws UnknownHostException {
        super( new InetSocketAddress( port ) );
    }

    public ExampleWebSockerServer(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen( WebSocket conn, ClientHandshake handshake ) {
        this.sendToAll( "{\"type\": \"clientDidConnect\", \"message\": \"new connection: " + handshake.getResourceDescriptor() + "\"}");
        System.out.println( conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!" );
    }

    @Override
    public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
        this.sendToAll( "{\"type\": \"clientDidDisconnect\", message: \"" + conn + " has left the room!\"}" );
        System.out.println( conn + " has left the room!" );
    }

    @Override
    public void onMessage( WebSocket conn, String message ) {
        this.sendToAll( message );
        System.out.println( conn + ": " + message );
    }

    @Override
    public void onError( WebSocket conn, Exception ex ) {
        ex.printStackTrace();
        if( conn != null ) {
            // some errors like port binding failed may not be assignable to a specific websocket
        }
    }

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
        int port = 12345;
        try {
            port = Integer.parseInt( args[ 0 ] );
        } catch ( Exception ex ) {
        }
        ExampleWebSockerServer server = new ExampleWebSockerServer(port);
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

